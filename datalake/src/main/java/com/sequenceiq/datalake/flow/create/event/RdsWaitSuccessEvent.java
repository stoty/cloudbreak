package com.sequenceiq.datalake.flow.create.event;

import java.util.Optional;

import com.sequenceiq.datalake.flow.SdxEvent;
import com.sequenceiq.environment.api.v1.environment.model.response.DetailedEnvironmentResponse;
import com.sequenceiq.redbeams.api.endpoint.v4.databaseserver.responses.DatabaseServerStatusV4Response;

public class RdsWaitSuccessEvent extends SdxEvent {

    private DetailedEnvironmentResponse detailedEnvironmentResponse;

    private DatabaseServerStatusV4Response databaseServerResponse;

    public RdsWaitSuccessEvent(Long sdxId, String userId, Optional<String> requestId, DetailedEnvironmentResponse detailedEnvironmentResponse,
            DatabaseServerStatusV4Response databaseServerResponse) {
        super(sdxId, userId, requestId);
        this.detailedEnvironmentResponse = detailedEnvironmentResponse;
        this.databaseServerResponse = databaseServerResponse;
    }

    @Override
    public String selector() {
        return "RdsWaitSuccessEvent";
    }

    public DetailedEnvironmentResponse getDetailedEnvironmentResponse() {
        return detailedEnvironmentResponse;
    }

    public DatabaseServerStatusV4Response getDatabaseServerResponse() {
        return databaseServerResponse;
    }
}
