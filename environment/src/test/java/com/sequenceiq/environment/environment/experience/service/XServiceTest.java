package com.sequenceiq.environment.environment.experience.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sequenceiq.environment.environment.experience.resolve.Experience;

class XServiceTest {
    
    private static final String PATH_POSTFIX = "/environment/{crn}";

    private static final String ENVIRONMENT_CRN = "somecrn";
    
    @Mock
    private ExperienceConnectorService experienceConnectorService;
    
    private XService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testWhenEnvironmentCrnIsNullThenIllegalArgumentExceptionComes() {
        underTest = new XService(new XPServices(), PATH_POSTFIX, experienceConnectorService);

        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.environmentHasActiveExperience(null));
    }

    @Test
    void testWhenEnvironmentCrnIsEmptyThenIllegalArgumentExceptionComes() {
        underTest = new XService(new XPServices(), PATH_POSTFIX, experienceConnectorService);

        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.environmentHasActiveExperience(""));
    }

    @Test
    void testIfExperiencesNotSpecifiedThenEmptySetShouldReturnWithoutRemoteCallAttempt() {
        underTest = new XService(new XPServices(), PATH_POSTFIX, experienceConnectorService);

        Set<String> result = underTest.environmentHasActiveExperience(ENVIRONMENT_CRN);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        verify(experienceConnectorService, times(0)).getWorkspaceNamesConnectedToEnv(anyString(), anyString());
    }

    @Test
    void testIfExperienceIsGivenAndItHasNoAttachedWorkspaceToTheGivenEnvironmentThenEmptySetShouldReturn() {
        String experienceName = "SomeGreatExperience";
        XPServices xp = createXPServices(experienceName);
        underTest = new XService(xp, PATH_POSTFIX, experienceConnectorService);
        when(experienceConnectorService.getWorkspaceNamesConnectedToEnv("https://" + xp.getExperiences().get(experienceName).getPathPrefix() +
                xp.getExperiences().get(experienceName).getPathInfix() + PATH_POSTFIX, ENVIRONMENT_CRN)).thenReturn(Collections.emptySet());

        Set<String> result = underTest.environmentHasActiveExperience(ENVIRONMENT_CRN);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0L, result.size());

        verify(experienceConnectorService, times(1)).getWorkspaceNamesConnectedToEnv(anyString(), anyString());
    }

    @Test
    void testIfExperienceIsGivenAndItHasAttachedWorkspaceToTheGivenEnvironmentThenSetShouldReturnWithTheNameOfTheGivenExperience() {
        String experienceNameConnectedToEnv = "SomeGreatExperience";
        String experienceNameWithoutEnv = "SomeGreatExperience";
        XPServices xp = createXPServices(experienceNameConnectedToEnv, experienceNameWithoutEnv);
        underTest = new XService(xp, PATH_POSTFIX, experienceConnectorService);
        String pathToResponse = "https://" + xp.getExperiences().get(experienceNameConnectedToEnv).getPathPrefix() + xp.getExperiences()
                .get(experienceNameConnectedToEnv).getPathInfix() + PATH_POSTFIX + ":" + xp.getExperiences().get(experienceNameConnectedToEnv).getPort();
        when(experienceConnectorService.getWorkspaceNamesConnectedToEnv(pathToResponse, ENVIRONMENT_CRN)).thenReturn(Set.of(experienceNameConnectedToEnv));

        Set<String> result = underTest.environmentHasActiveExperience(ENVIRONMENT_CRN);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.size());
        Assertions.assertTrue(new ArrayList<>(result).get(0).equalsIgnoreCase(experienceNameConnectedToEnv));

        verify(experienceConnectorService, times(1)).getWorkspaceNamesConnectedToEnv(anyString(), anyString());
    }

    @Test
    void testIfOneOfTheExperiencesHasJustUnfilledEnvironmentVariableNamesInThenItShouldNotBeProcessed() {
        XPServices xpServices = new XPServices();
        Map<String, Experience> experiences = new LinkedHashMap<>();
        experiences.put("someXpName", new Experience("${SOME_VAL1}", "${SOME_VAL2}", "${SOME_VAL3}"));
        xpServices.setExperiences(experiences);
        underTest = new XService(xpServices, PATH_POSTFIX, experienceConnectorService);

        when(experienceConnectorService.getWorkspaceNamesConnectedToEnv(anyString(), eq(ENVIRONMENT_CRN))).thenReturn(Set.of("something"));

        Set<String> result = underTest.environmentHasActiveExperience(ENVIRONMENT_CRN);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0L, result.size());

        verify(experienceConnectorService, times(0)).getWorkspaceNamesConnectedToEnv(anyString(), anyString());
    }

    private XPServices createXPServices(String... names) {
        XPServices xpServices = new XPServices();
        xpServices.setExperiences(createExperienceWithName(names));
        return xpServices;
    }

    private Map<String, Experience> createExperienceWithName(String... names) {
        Map<String, Experience> experiences = new LinkedHashMap<>(names.length);
        for (String name : names) {
            Experience xp = new Experience();
            xp.setPathInfix("/ml-" + name);
            xp.setPathPrefix("/" + name + "prefix");
            xp.setPort("1234");
            experiences.put(name, xp);
        }
        return experiences;
    }

}