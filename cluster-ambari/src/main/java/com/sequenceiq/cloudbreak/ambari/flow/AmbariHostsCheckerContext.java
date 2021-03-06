package com.sequenceiq.cloudbreak.ambari.flow;

import java.util.Set;

import com.sequenceiq.ambari.client.AmbariClient;
import com.sequenceiq.cloudbreak.cluster.service.StackAware;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.domain.stack.instance.InstanceMetaData;

public class AmbariHostsCheckerContext implements StackAware {

    private final AmbariClient ambariClient;

    private final Set<InstanceMetaData> hostsInCluster;

    private final int hostCount;

    private final Stack stack;

    public AmbariHostsCheckerContext(Stack stack, AmbariClient ambariClient, Set<InstanceMetaData> hostsInCluster, int hostCount) {
        this.stack = stack;
        this.ambariClient = ambariClient;
        this.hostsInCluster = hostsInCluster;
        this.hostCount = hostCount;
    }

    public AmbariClient getAmbariClient() {
        return ambariClient;
    }

    public Set<InstanceMetaData> getHostsInCluster() {
        return hostsInCluster;
    }

    public int getHostCount() {
        return hostCount;
    }

    @Override
    public Stack getStack() {
        return stack;
    }
}
