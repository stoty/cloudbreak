package com.sequenceiq.datalake.flow.delete.event;

import java.util.Optional;

import com.sequenceiq.datalake.flow.SdxContext;
import com.sequenceiq.datalake.flow.SdxEvent;

public class StackDeletionWaitRequest extends SdxEvent {

    private final boolean forced;

    public StackDeletionWaitRequest(Long sdxId, String userId, Optional<String> requestId, boolean forced) {
        super(sdxId, userId, requestId);
        this.forced = forced;
    }

    public static StackDeletionWaitRequest from(SdxContext context, SdxDeleteStartEvent payload) {
        return new StackDeletionWaitRequest(context.getSdxId(), context.getUserId(), context.getRequestId(), payload.isForced());
    }

    @Override
    public String selector() {
        return "StackDeletionWaitRequest";
    }

    public boolean isForced() {
        return forced;
    }
}
