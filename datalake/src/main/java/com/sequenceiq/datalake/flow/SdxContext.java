package com.sequenceiq.datalake.flow;

import java.util.Optional;

import com.sequenceiq.flow.core.CommonContext;
import com.sequenceiq.flow.core.FlowParameters;

public class SdxContext extends CommonContext {

    private Long sdxId;

    private String userId;

    private Optional<String> requestId;

    public SdxContext(FlowParameters flowParameters, Long sdxId, String userId, Optional<String> requestId) {
        super(flowParameters);
        this.sdxId = sdxId;
        this.userId = userId;
        this.requestId = requestId;
    }

    public SdxContext(FlowParameters flowParameters, SdxEvent event) {
        super(flowParameters);
        sdxId = event.getResourceId();
        userId = event.getUserId();
        requestId = event.getRequestId();
    }

    public static SdxContext from(FlowParameters flowParameters, SdxEvent event) {
        return new SdxContext(flowParameters, event);
    }

    public Long getSdxId() {
        return sdxId;
    }

    public void setSdxId(Long sdxId) {
        this.sdxId = sdxId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Optional<String> getRequestId() {
        return requestId;
    }

    public void setRequestId(Optional<String> requestId) {
        this.requestId = requestId;
    }

}
