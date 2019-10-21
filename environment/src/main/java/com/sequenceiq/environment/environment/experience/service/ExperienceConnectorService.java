package com.sequenceiq.environment.environment.experience.service;

import static com.sequenceiq.cloudbreak.util.ConditionBasedEvaluatorUtil.throwIfTrue;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.client.RestClientUtil;
import com.sequenceiq.environment.environment.experience.response.ExperienceCallResponse;

@Service
public class ExperienceConnectorService {

    private final String componentToReplaceInPath;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperienceConnectorService.class);

    public ExperienceConnectorService(@Value("${xp.path.componentToReplace}") String componentToReplaceInPath) {
        throwIfTrue(isEmpty(componentToReplaceInPath),
                () -> new IllegalArgumentException("Component what should be replaced in experience path should not be empty or null."));
        this.componentToReplaceInPath = componentToReplaceInPath;
    }

    @NotNull Set<String> getWorkspaceNamesConnectedToEnv(String experienceBasePath, String environmentCrn) {
        String pathToExperience = experienceBasePath.replace(componentToReplaceInPath, environmentCrn);
        LOGGER.debug("About to connect to experience on path: {}", pathToExperience);
        Client client = RestClientUtil.get();
        WebTarget webTarget = client.target(experienceBasePath);
        // TODO: 2019. 10. 28. should add actor crn as a header/cookie from some context
        Response result = webTarget.request().get();
        Optional<ExperienceCallResponse> response = readResponse(webTarget, result);
        return response.map(experienceCallResponse -> Set.of(experienceCallResponse.getName())).orElseGet(Set::of);
    }

    private Optional<ExperienceCallResponse> readResponse(WebTarget target, Response response)  {
        ExperienceCallResponse experienceCallResponse = null;
        if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            try {
                experienceCallResponse = response.readEntity(ExperienceCallResponse.class);
            } catch (IllegalStateException | ProcessingException e) {
                String msg = "Failed to resolve response from experience on path: " + target.getUri().toString();
                LOGGER.warn(msg, e);
            }
        }
        return Optional.ofNullable(experienceCallResponse);
    }

}
