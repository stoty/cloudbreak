package com.sequenceiq.datalake.flow.stop.event;

import java.util.Optional;

import com.sequenceiq.datalake.flow.SdxContext;
import com.sequenceiq.datalake.flow.SdxEvent;

public class SdxStopWaitRequest extends SdxEvent {

    public SdxStopWaitRequest(Long sdxId, String userId, Optional<String> requestId) {
        super(sdxId, userId, requestId);
    }

    public static SdxStopWaitRequest from(SdxContext context) {
        return new SdxStopWaitRequest(context.getSdxId(), context.getUserId(), context.getRequestId());
    }

    @Override
    public String selector() {
        return "SdxStopWaitRequest";
    }

}

