package com.sequenceiq.cloudbreak.cloud.aws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AmazonEC2Exception;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.GetConsoleOutputRequest;
import com.amazonaws.services.ec2.model.GetConsoleOutputResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.google.common.collect.Sets;
import com.sequenceiq.cloudbreak.cloud.InstanceConnector;
import com.sequenceiq.cloudbreak.cloud.aws.poller.PollerUtil;
import com.sequenceiq.cloudbreak.cloud.aws.view.AwsCredentialView;
import com.sequenceiq.cloudbreak.cloud.context.AuthenticatedContext;
import com.sequenceiq.cloudbreak.cloud.exception.CloudOperationNotSupportedException;
import com.sequenceiq.cloudbreak.cloud.model.CloudInstance;
import com.sequenceiq.cloudbreak.cloud.model.CloudResource;
import com.sequenceiq.cloudbreak.cloud.model.CloudVmInstanceStatus;
import com.sequenceiq.cloudbreak.cloud.model.InstanceStatus;

@Service
public class AwsInstanceConnector implements InstanceConnector {

    public static final String INSTANCE_NOT_FOUND_ERROR_CODE = "InvalidInstanceID.NotFound";

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsInstanceConnector.class);

    @Inject
    private AwsClient awsClient;

    @Inject
    private PollerUtil pollerUtil;

    @Value("${cb.aws.hostkey.verify:}")
    private boolean verifyHostKey;

    @Override
    public String getConsoleOutput(AuthenticatedContext authenticatedContext, CloudInstance vm) {
        if (!verifyHostKey) {
            throw new CloudOperationNotSupportedException("Host key verification is disabled on AWS");
        }
        AmazonEC2Client amazonEC2Client = awsClient.createAccess(new AwsCredentialView(authenticatedContext.getCloudCredential()),
                authenticatedContext.getCloudContext().getLocation().getRegion().value());
        GetConsoleOutputRequest getConsoleOutputRequest = new GetConsoleOutputRequest().withInstanceId(vm.getInstanceId());
        GetConsoleOutputResult getConsoleOutputResult = amazonEC2Client.getConsoleOutput(getConsoleOutputRequest);
        try {
            return getConsoleOutputResult.getOutput() == null ? "" : getConsoleOutputResult.getDecodedOutput();
        } catch (Exception ex) {
            LOGGER.warn(ex.getMessage(), ex);
            return "";
        }
    }

    @Retryable(
            value = SdkClientException.class,
            maxAttempts = 15,
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 10000)
    )
    @Override
    public List<CloudVmInstanceStatus> start(AuthenticatedContext ac, List<CloudResource> resources, List<CloudInstance> vms) {
        AmazonEC2Client amazonEC2Client = awsClient.createAccess(new AwsCredentialView(ac.getCloudCredential()),
                ac.getCloudContext().getLocation().getRegion().value());
        try {
            Collection<String> instances = instanceIdsWhichAreNotInCorrectState(vms, amazonEC2Client, "Running");
            if (!instances.isEmpty()) {
                amazonEC2Client.startInstances(new StartInstancesRequest().withInstanceIds(instances));
            }
        } catch (AmazonEC2Exception e) {
            handleEC2Exception(vms, e);
        } catch (SdkClientException e) {
            LOGGER.warn("Failed to send start request to AWS: ", e);
            throw e;
        }
        return pollerUtil.waitFor(ac, vms, Sets.newHashSet(InstanceStatus.STARTED, InstanceStatus.FAILED));
    }

    @Retryable(
            value = SdkClientException.class,
            maxAttempts = 15,
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 10000)
    )
    @Override
    public List<CloudVmInstanceStatus> stop(AuthenticatedContext ac, List<CloudResource> resources, List<CloudInstance> vms) {
        AmazonEC2Client amazonEC2Client = awsClient.createAccess(new AwsCredentialView(ac.getCloudCredential()),
                ac.getCloudContext().getLocation().getRegion().value());
        try {
            Collection<String> instances = instanceIdsWhichAreNotInCorrectState(vms, amazonEC2Client, "Stopped");
            if (!instances.isEmpty()) {
                amazonEC2Client.stopInstances(new StopInstancesRequest().withInstanceIds(instances));
            }
        } catch (AmazonEC2Exception e) {
            handleEC2Exception(vms, e);
        } catch (SdkClientException e) {
            LOGGER.warn("Failed to send stop request to AWS: ", e);
            throw e;
        }
        return pollerUtil.waitFor(ac, vms, Sets.newHashSet(InstanceStatus.STOPPED, InstanceStatus.FAILED));
    }

    private void handleEC2Exception(List<CloudInstance> vms, AmazonEC2Exception e) throws AmazonEC2Exception {
        LOGGER.debug("Exception received from AWS: ", e);
        if (e.getErrorCode().equalsIgnoreCase(INSTANCE_NOT_FOUND_ERROR_CODE)) {
            Pattern pattern = Pattern.compile("i-[a-z0-9]*");
            Matcher matcher = pattern.matcher(e.getErrorMessage());
            if (matcher.find()) {
                String doesNotExistInstanceId = matcher.group();
                LOGGER.debug("Remove instance from vms: {}", doesNotExistInstanceId);
                vms.removeIf(vm -> doesNotExistInstanceId.equals(vm.getInstanceId()));
            }
        }
        throw e;
    }

    @Retryable(
            value = SdkClientException.class,
            maxAttempts = 15,
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 10000)
    )
    @Override
    public List<CloudVmInstanceStatus> check(AuthenticatedContext ac, List<CloudInstance> vms) {
        LOGGER.debug("Check instances on aws side: {}", vms);
        List<CloudInstance> cloudIntancesWithInstanceId = vms.stream()
                .filter(cloudInstance -> cloudInstance.getInstanceId() != null)
                .collect(Collectors.toList());
        LOGGER.debug("Instances with intanceId: {}", cloudIntancesWithInstanceId);

        List<String> instanceIds = cloudIntancesWithInstanceId.stream().map(CloudInstance::getInstanceId)
                .collect(Collectors.toList());

        String region = ac.getCloudContext().getLocation().getRegion().value();
        try {
            DescribeInstancesResult result = awsClient.createAccess(new AwsCredentialView(ac.getCloudCredential()),
                    ac.getCloudContext().getLocation().getRegion().value())
                    .describeInstances(new DescribeInstancesRequest().withInstanceIds(instanceIds));
            LOGGER.debug("Result from AWS: {}", result);
            return fillCloudVmInstanceStatuses(ac, cloudIntancesWithInstanceId, region, result);
        } catch (AmazonEC2Exception e) {
            handleEC2Exception(vms, e);
        } catch (SdkClientException e) {
            LOGGER.warn("Failed to send request to AWS: ", e);
            throw e;
        }
        return Collections.emptyList();
    }

    private List<CloudVmInstanceStatus> fillCloudVmInstanceStatuses(AuthenticatedContext ac, List<CloudInstance> cloudIntancesWithInstanceId, String region,
            DescribeInstancesResult result) {
        List<CloudVmInstanceStatus> cloudVmInstanceStatuses = new ArrayList<>();
        for (Reservation reservation : result.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                Optional<CloudInstance> cloudInstanceForInstanceId = cloudIntancesWithInstanceId.stream()
                        .filter(cloudInstance -> cloudInstance.getInstanceId().equals(instance.getInstanceId()))
                        .findFirst();
                if (cloudInstanceForInstanceId.isPresent()) {
                    CloudInstance cloudInstance = cloudInstanceForInstanceId.get();
                    if ("Stopped".equalsIgnoreCase(instance.getState().getName())) {
                        LOGGER.debug("AWS instance [{}] is in {} state, region: {}, stack: {}",
                                instance.getInstanceId(), instance.getState().getName(), region, ac.getCloudContext().getId());
                        cloudVmInstanceStatuses.add(new CloudVmInstanceStatus(cloudInstance, InstanceStatus.STOPPED));
                    } else if ("Running".equalsIgnoreCase(instance.getState().getName())) {
                        LOGGER.debug("AWS instance [{}] is in {} state, region: {}, stack: {}",
                                instance.getInstanceId(), instance.getState().getName(), region, ac.getCloudContext().getId());
                        cloudVmInstanceStatuses.add(new CloudVmInstanceStatus(cloudInstance, InstanceStatus.STARTED));
                    } else if ("Terminated".equalsIgnoreCase(instance.getState().getName())) {
                        LOGGER.debug("AWS instance [{}] is in {} state, region: {}, stack: {}",
                                instance.getInstanceId(), instance.getState().getName(), region, ac.getCloudContext().getId());
                        cloudVmInstanceStatuses.add(new CloudVmInstanceStatus(cloudInstance, InstanceStatus.TERMINATED));
                    } else {
                        LOGGER.debug("AWS instance [{}] is in {} state, region: {}, stack: {}",
                                instance.getInstanceId(), instance.getState().getName(), region, ac.getCloudContext().getId());
                        cloudVmInstanceStatuses.add(new CloudVmInstanceStatus(cloudInstance, InstanceStatus.IN_PROGRESS));
                    }
                }
            }
        }
        return cloudVmInstanceStatuses;
    }

    private Collection<String> instanceIdsWhichAreNotInCorrectState(List<CloudInstance> vms, AmazonEC2 amazonEC2Client, String state) {
        Set<String> instances = vms.stream().map(CloudInstance::getInstanceId).collect(Collectors.toCollection(HashSet::new));
        DescribeInstancesResult describeInstances = amazonEC2Client.describeInstances(
                new DescribeInstancesRequest().withInstanceIds(instances));
        for (Reservation reservation : describeInstances.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                if (state.equalsIgnoreCase(instance.getState().getName())) {
                    instances.remove(instance.getInstanceId());
                }
            }
        }
        return instances;
    }
}
