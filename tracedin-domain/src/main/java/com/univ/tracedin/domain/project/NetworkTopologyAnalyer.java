package com.univ.tracedin.domain.project;

import com.univ.tracedin.domain.span.Topology;

public interface NetworkTopologyAnalyer {

    Topology analyze(Project projectKey);
}
