package com.sequenceiq.freeipa.kerberosmgmt.v1;

import java.util.Optional;
import java.util.Set;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.sequenceiq.freeipa.client.FreeIpaClientException;

public class KerberosMgmtUtil {

    public static final int NOT_MAPPED_ERROR_CODE = 4000;

    public static final int NOT_FOUND_ERROR_CODE = 4001;

    public static final int DUPLICATE_ENTRY_ERROR_CODE = 4002;

    private KerberosMgmtUtil() {
    }

    public static boolean isNotFoundException(FreeIpaClientException e) {
        return isExceptionWithErrorCode(e, Set.of(NOT_FOUND_ERROR_CODE));
    }

    public static boolean isDuplicateEntryException(FreeIpaClientException e) {
        return isExceptionWithErrorCode(e, Set.of(DUPLICATE_ENTRY_ERROR_CODE));
    }

    public static boolean isExceptionWithErrorCode(FreeIpaClientException e, Set<Integer> errorCodes) {
        return Optional.ofNullable(e.getCause())
                .filter(JsonRpcClientException.class::isInstance)
                .map(JsonRpcClientException.class::cast)
                .map(JsonRpcClientException::getCode)
                .filter(c -> errorCodes.contains(c))
                .isPresent();
    }
}
