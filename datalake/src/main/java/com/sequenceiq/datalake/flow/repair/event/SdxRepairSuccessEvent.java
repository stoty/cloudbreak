package com.sequenceiq.datalake.flow.repair.event;

import java.util.Optional;

import com.sequenceiq.datalake.flow.SdxEvent;

public class SdxRepairSuccessEvent extends SdxEvent {

    public SdxRepairSuccessEvent(Long sdxId, String userId, Optional<String> requestId) {
        super(sdxId, userId, requestId);
    }

    @Override
    public String selector() {
        return "SdxRepairSuccessEvent";
    }
}
