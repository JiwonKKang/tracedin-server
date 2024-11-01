package com.univ.tracedin.domain.span;

import java.util.List;
import java.util.Map;

import com.univ.tracedin.domain.project.NetworkTopology;
import com.univ.tracedin.domain.project.NetworkTopology.Edge;
import com.univ.tracedin.domain.project.NetworkTopology.Node;

public interface Topology {

    static Topology of(Map<String, Node> nodes, Map<String, Edge> edges) {
        return of(List.copyOf(nodes.values()), List.copyOf(edges.values()));
    }

    static Topology of(List<Node> nodes, List<Edge> edges) {
        return new NetworkTopology(nodes, edges);
    }

    static NetworkTopologyBuilder builder() {
        return new NetworkTopologyBuilder();
    }
}
