package com.sequenceiq.environment.credential.v1.converter;

import org.springframework.stereotype.Component;

import com.sequenceiq.common.api.type.Tunnel;

@Component
public class TunnelConverter {

    public Tunnel convert(Tunnel tunnelRequest) {
        if (tunnelRequest == null) {
            tunnelRequest = Tunnel.DIRECT;
        }
        return tunnelRequest;
    }
}
