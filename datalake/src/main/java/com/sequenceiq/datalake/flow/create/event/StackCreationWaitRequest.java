package com.sequenceiq.datalake.flow.create.event;

import java.util.Optional;

import com.sequenceiq.datalake.flow.SdxContext;
import com.sequenceiq.datalake.flow.SdxEvent;

public class StackCreationWaitRequest extends SdxEvent {

    public StackCreationWaitRequest(Long sdxId, String userId, Optional<String> requestId) {
        super(sdxId, userId, requestId);
    }

    public static StackCreationWaitRequest from(SdxContext context) {
        return new StackCreationWaitRequest(context.getSdxId(), context.getUserId(), context.getRequestId());
    }

    @Override
    public String selector() {
        return "StackCreationWaitRequest";
    }
}
