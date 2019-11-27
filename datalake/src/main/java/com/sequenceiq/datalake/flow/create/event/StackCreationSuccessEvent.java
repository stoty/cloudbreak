package com.sequenceiq.datalake.flow.create.event;

import java.util.Optional;

import com.sequenceiq.datalake.flow.SdxEvent;

public class StackCreationSuccessEvent extends SdxEvent {

    public StackCreationSuccessEvent(Long sdxId, String userId, Optional<String> requestId) {
        super(sdxId, userId, requestId);
    }

    @Override
    public String selector() {
        return "StackCreationSuccessEvent";
    }

}
