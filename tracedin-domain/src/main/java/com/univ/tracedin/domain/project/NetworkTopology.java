package com.univ.tracedin.domain.project;

import java.util.List;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import com.univ.tracedin.domain.span.Topology;

@Getter
@AllArgsConstructor
public final class NetworkTopology implements Topology {

    private final List<Node> nodes;
    private final List<Edge> edges;

    @Getter
    @AllArgsConstructor
    public static final class Node {

        private ProjectKey projectKey;
        private String name;
        private NodeType nodeType;

        public static Node of(ProjectKey projectKey, String serviceName, NodeType nodeType) {
            return new Node(projectKey, serviceName, nodeType);
        }

        public static Node createService(ProjectKey projectKey, String serviceName) {
            return new Node(projectKey, serviceName, NodeType.SERVICE);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Node node)) {
                return false;
            }

            return Objects.equals(getProjectKey(), node.getProjectKey())
                    && Objects.equals(getName(), node.getName())
                    && getNodeType() == node.getNodeType();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getProjectKey().value(), getName(), getNodeType());
        }
    }

    public enum NodeType {
        SERVICE,
        KAFKA,
        DATABASE
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Edge {

        private String source;
        private String target;
        private int requestCount;

        public static Edge init(String source, String target) {
            return new Edge(source, target, 0);
        }

        public void incrementRequestCount() {
            requestCount++;
        }
    }
}
