package com.sequenceiq.datalake.flow.repair.event;

import java.util.Optional;

import com.sequenceiq.datalake.flow.SdxContext;
import com.sequenceiq.datalake.flow.SdxEvent;

public class SdxRepairWaitRequest extends SdxEvent {

    public SdxRepairWaitRequest(Long sdxId, String userId, Optional<String> requestId) {
        super(sdxId, userId, requestId);
    }

    public static SdxRepairWaitRequest from(SdxContext context) {
        return new SdxRepairWaitRequest(context.getSdxId(), context.getUserId(), context.getRequestId());
    }

    @Override
    public String selector() {
        return "SdxRepairWaitRequest";
    }

}

