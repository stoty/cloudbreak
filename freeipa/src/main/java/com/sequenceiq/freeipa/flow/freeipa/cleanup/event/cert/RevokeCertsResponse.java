package com.sequenceiq.freeipa.flow.freeipa.cleanup.event.cert;

import java.util.Map;
import java.util.Set;

import com.sequenceiq.freeipa.flow.freeipa.cleanup.CleanupEvent;
import com.sequenceiq.freeipa.flow.freeipa.cleanup.event.AbstractCleanupEvent;

public class RevokeCertsResponse extends AbstractCleanupEvent {

    private final Set<String> certCleanupSuccess;

    private final Map<String, String> certCleanupFailed;

    public RevokeCertsResponse(CleanupEvent cleanupEvent, Set<String> certCleanupSuccess, Map<String, String> certCleanupFailed) {
        super(cleanupEvent);
        this.certCleanupSuccess = certCleanupSuccess;
        this.certCleanupFailed = certCleanupFailed;
    }

    public RevokeCertsResponse(String selector, CleanupEvent cleanupEvent, Set<String> certCleanupSuccess,
            Map<String, String> certCleanupFailed) {
        super(selector, cleanupEvent);
        this.certCleanupSuccess = certCleanupSuccess;
        this.certCleanupFailed = certCleanupFailed;
    }

    public Set<String> getCertCleanupSuccess() {
        return certCleanupSuccess;
    }

    public Map<String, String> getCertCleanupFailed() {
        return certCleanupFailed;
    }
}
