package com.univ.tracedin.domain.project.exception;

import com.univ.tracedin.common.exception.DomainException;

public final class NetworkTopologyBuildException extends DomainException {

    public static final NetworkTopologyBuildException EXCEPTION =
            new NetworkTopologyBuildException();

    public NetworkTopologyBuildException() {
        super(ProjectErrorCode.NETWORK_TOPOLOGY_BUILD_FAILED);
    }
}
