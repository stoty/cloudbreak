package com.sequenceiq.datalake.flow.delete.event;

import java.util.Optional;

import com.sequenceiq.datalake.flow.SdxEvent;

public class RdsDeletionSuccessEvent extends SdxEvent {
    public RdsDeletionSuccessEvent(Long sdxId, String userId, Optional<String> requestId) {
        super(sdxId, userId, requestId);
    }

    @Override
    public String selector() {
        return "RdsDeletionSuccessEvent";
    }
}