package com.sequenceiq.datalake.flow.stop.event;

import java.util.Optional;

import com.sequenceiq.datalake.flow.SdxEvent;

public class SdxStopSuccessEvent extends SdxEvent {

    public SdxStopSuccessEvent(Long sdxId, String userId, Optional<String> requestId) {
        super(sdxId, userId, requestId);
    }

    @Override
    public String selector() {
        return "SdxStopSuccessEvent";
    }
}
