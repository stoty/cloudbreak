package com.sequenceiq.datalake.flow.repair;

import com.sequenceiq.flow.core.FlowEvent;

public enum SdxRepairEvent implements FlowEvent {

    SDX_REPAIR_EVENT("SDX_REPAIR_EVENT"),
    SDX_REPAIR_IN_PROGRESS_EVENT("SDX_REPAIR_IN_PROGRESS_EVENT"),
    SDX_REPAIR_SUCCESS_EVENT("SdxRepairSuccessEvent"),
    SDX_REPAIR_FAILED_EVENT("SdxRepairFailedEvent"),
    SDX_REPAIR_FAILED_HANDLED_EVENT("SDX_REPAIR_FAILED_HANDLED_EVENT"),
    SDX_REPAIR_FINALIZED_EVENT("SDX_REPAIR_FINALIZED_EVENT");

    private final String event;

    SdxRepairEvent(String event) {
        this.event = event;
    }

    @Override
    public String event() {
        return event;
    }

}
