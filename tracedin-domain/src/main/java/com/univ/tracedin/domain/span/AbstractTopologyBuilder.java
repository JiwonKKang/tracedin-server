package com.univ.tracedin.domain.span;

import java.util.HashMap;
import java.util.Map;

import com.univ.tracedin.domain.project.NetworkTopology.Edge;
import com.univ.tracedin.domain.project.NetworkTopology.Node;
import com.univ.tracedin.domain.project.NetworkTopology.NodeType;
import com.univ.tracedin.domain.project.ProjectKey;

public abstract class AbstractTopologyBuilder {

    protected static final String KAFKA_NODE_NAME = "Kafka";

    public ProjectKey projectKey;
    public Map<String, Node> nodes = new HashMap<>();
    public Map<String, Edge> edges = new HashMap<>();

    public AbstractTopologyBuilder projectKey(ProjectKey projectKey) {
        this.projectKey = projectKey;
        return this;
    }

    protected void addNode(String nodeName, NodeType nodeType) {
        nodes.computeIfAbsent(nodeName, name -> Node.of(projectKey, name, nodeType));
    }

    protected void addEdge(String source, String target) {
        final String edgeKey = source + "->" + target;
        edges.computeIfAbsent(edgeKey, k -> Edge.init(source, target)).incrementRequestCount();
    }
}
