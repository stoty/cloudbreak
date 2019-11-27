package com.sequenceiq.datalake.flow.stop.event;

import java.util.Optional;

import com.sequenceiq.datalake.flow.SdxEvent;

public class SdxStartStopEvent extends SdxEvent {

    public SdxStartStopEvent(String selector, Long sdxId, String userId, Optional<String> requestId) {
        super(selector, sdxId, userId, requestId);
    }

    @Override
    public String selector() {
        return "SdxStartStopEvent";
    }
}
