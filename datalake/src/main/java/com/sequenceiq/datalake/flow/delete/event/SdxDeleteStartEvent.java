package com.sequenceiq.datalake.flow.delete.event;

import java.util.Optional;

import com.sequenceiq.datalake.flow.SdxEvent;

public class SdxDeleteStartEvent extends SdxEvent {

    private final boolean forced;

    public SdxDeleteStartEvent(String selector, Long sdxId, String userId, Optional<String> requestId, boolean forced) {
        super(selector, sdxId, userId, requestId);
        this.forced = forced;
    }

    public boolean isForced() {
        return forced;
    }
}
