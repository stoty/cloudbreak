package com.sequenceiq.environment.environment.experience.service;

import static com.sequenceiq.cloudbreak.util.ConditionBasedEvaluatorUtil.throwIfTrue;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sequenceiq.environment.environment.experience.resolve.Experience;

@Service
public class XService {

    private static final String ENV_VAR_PATTERN_REGEX = "\\$\\{.*}";

    private static final Logger LOGGER = LoggerFactory.getLogger(XService.class);

    private final ExperienceConnectorService experienceConnectorService;

    private final Map<String, Experience> configuredExperiences;

    private final String pathPostfix;

    public XService(XPServices experienceProvider, @Value("${xp.path.postfix}") String pathPostfix, ExperienceConnectorService experienceConnectorService) {
        this.configuredExperiences = identifyConfiguredExperiences(experienceProvider);
        this.pathPostfix = StringUtils.isEmpty(pathPostfix) ? "" : pathPostfix;
        this.experienceConnectorService = experienceConnectorService;
        LOGGER.debug("Experience checking postfix set to: {}", pathPostfix);
    }

    /**
     * Checks all the configured experiences for any existing workspace which has a connection with the given environment.
     * If so, it will return the set of the names of the given experience.
     *
     * @param environmentCrn the resource crn of the environment. It must not be null or empty.
     * @return the name of the experiences which has an active workspace for the given environment.
     * @throws IllegalArgumentException if environmentCrn is null or empty
     */
    public Set<String> environmentHasActiveExperience(@NotNull String environmentCrn) {
        throwIfTrue(StringUtils.isEmpty(environmentCrn), () -> new IllegalArgumentException("Unable to check environment - experience relation, since the " +
                "given environment crn is null or empty!"));
        Set<String> affectedExperiences;
        affectedExperiences = configuredExperiences
                .entrySet()
                .stream()
                .filter(this::isExperienceConfigured)
                .map(xp -> isExperienceActiveForEnvironment(xp.getKey(), xp.getValue(), environmentCrn))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toSet());
        return affectedExperiences;
    }

    private Optional<String> isExperienceActiveForEnvironment(String experienceName, Experience xp, String environmentCrn) {
        LOGGER.debug("Checking whether the environment (crn: {}) has an active experience (name: {}) or not.", environmentCrn, experienceName);
        if (isExperienceHasActiveWorkspaceForEnv(xp, environmentCrn)/* experience has entry for environment */) {
            LOGGER.info("The following experience has an active entry for the given environment! [experience: {}, environmentCrn: {}]", experienceName,
                    environmentCrn);
            return Optional.of(experienceName);
        }
        return Optional.empty();
    }

    private boolean isExperienceHasActiveWorkspaceForEnv(Experience xp, String envCrn) {
        String pathToExperience = "https://" + xp.getPathPrefix() + xp.getPathInfix() + pathPostfix + ":" + xp.getPort();
        return CollectionUtils.isNotEmpty(experienceConnectorService.getWorkspaceNamesConnectedToEnv(pathToExperience, envCrn));
    }

    private Map<String, Experience> identifyConfiguredExperiences(XPServices experienceProvider) {
        Map<String, Experience> configuredExperiences = experienceProvider.getExperiences()
                .entrySet()
                .stream()
                .filter(this::isExperienceConfigured)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (configuredExperiences.isEmpty()) {
            LOGGER.info("There are no - properly - configured experience endpoints in environment service! If you would like to check them, specify them" +
                    " in the application.yml!");
            return emptyMap();
        } else {
            LOGGER.info("The following experience(s) have given for environment service: {}", String.join(", ",
                    new HashSet<>(configuredExperiences.keySet())));
            return configuredExperiences;
        }
    }

    private boolean isExperienceConfigured(Map.Entry<String, Experience> xp) {
        boolean configured = isExperienceValuesAreValid(xp.getValue().getPathInfix(), xp.getValue().getPathPrefix(), xp.getValue().getPort());
        if (!configured) {
            LOGGER.debug("The following experience has not configured properly: {}", xp.getKey());
        }
        return configured;
    }

    private boolean isExperienceValuesAreValid(String... values) {
        for (String value : values) {
            boolean valid = false;
            if (StringUtils.isNotEmpty(value) && !value.matches(ENV_VAR_PATTERN_REGEX)) {
                valid = true;
            }
            if (!valid) {
                return false;
            }
        }
        return true;
    }

}
