package com.sequenceiq.datalake.flow.upgrade.event;

import java.util.Optional;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.UpgradeOptionV4Response;
import com.sequenceiq.datalake.flow.SdxEvent;

public class SdxUpgradeStartEvent extends SdxEvent {

    private UpgradeOptionV4Response upgradeOption;

    public SdxUpgradeStartEvent(String selector, Long sdxId, String userId, Optional<String> requestId, UpgradeOptionV4Response upgradeOption) {
        super(selector, sdxId, userId, requestId);
        this.upgradeOption = upgradeOption;
    }

    public UpgradeOptionV4Response getUpgradeOption() {
        return upgradeOption;
    }

    @Override
    public String selector() {
        return "SdxUpgradeStartEvent";
    }
}
