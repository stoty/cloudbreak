package com.sequenceiq.datalake.flow.start.event;

import java.util.Optional;

import com.sequenceiq.datalake.flow.SdxEvent;

public class SdxSyncSuccessEvent extends SdxEvent {

    public SdxSyncSuccessEvent(Long sdxId, String userId, Optional<String> requestId) {
        super(sdxId, userId, requestId);
    }

    @Override
    public String selector() {
        return "SdxSyncSuccessEvent";
    }
}
