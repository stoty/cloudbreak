package com.sequenceiq.it.cloudbreak.action.v4.environment;

import com.sequenceiq.it.cloudbreak.EnvironmentClient;
import com.sequenceiq.it.cloudbreak.action.Action;
import com.sequenceiq.it.cloudbreak.context.TestContext;
import com.sequenceiq.it.cloudbreak.dto.environment.EnvironmentTestDto;
import com.sequenceiq.it.cloudbreak.log.Log;

public class EnvironmentCreateAction implements Action<EnvironmentTestDto, EnvironmentClient> {

    @Override
    public EnvironmentTestDto action(TestContext testContext, EnvironmentTestDto testDto, EnvironmentClient environmentClient) throws Exception {
        testDto.setResponse(
                environmentClient.getEnvironmentClient()
                        .environmentV1Endpoint()
                        .post(testDto.getRequest()));

        Log.logJSON("Environment post request: ", testDto.getRequest());
        return testDto;
    }
}