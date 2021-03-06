package com.sequenceiq.cloudbreak.cm;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.endpoint.v4.ExposedService;
import com.sequenceiq.cloudbreak.domain.Blueprint;

@Component
public class ClouderaManagerBlueprintPortConfigCollector {

    public Map<String, Integer> getServicePorts(Blueprint blueprint, boolean tls) {
        return ExposedService.getAllServicePorts(tls);
    }
}
