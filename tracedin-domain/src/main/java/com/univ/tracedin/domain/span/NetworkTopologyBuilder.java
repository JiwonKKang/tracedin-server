package com.univ.tracedin.domain.span;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.univ.tracedin.domain.project.NetworkTopology.NodeType;
import com.univ.tracedin.domain.project.ProjectKey;

public final class NetworkTopologyBuilder extends AbstractTopologyBuilder {

    private List<Span> clientSpans;
    private List<Span> serverSpans;
    private List<Span> dbSpans;
    private List<Span> producerSpans;
    private List<Span> consumerSpans;

    @Override
    public NetworkTopologyBuilder projectKey(ProjectKey projectKey) {
        return (NetworkTopologyBuilder) super.projectKey(projectKey);
    }

    public NetworkTopologyBuilder client(List<Span> clientSpans) {
        this.clientSpans = clientSpans;
        return this;
    }

    public NetworkTopologyBuilder server(List<Span> serverSpans) {
        this.serverSpans = serverSpans;
        return this;
    }

    public NetworkTopologyBuilder db(List<Span> dbSpans) {
        this.dbSpans = dbSpans;
        return this;
    }

    public NetworkTopologyBuilder producer(List<Span> producerSpans) {
        this.producerSpans = producerSpans;
        return this;
    }

    public NetworkTopologyBuilder consumer(List<Span> consumerSpans) {
        this.consumerSpans = consumerSpans;
        return this;
    }

    public Topology build() {
        if (clientSpans != null && serverSpans != null) {
            buildServiceTopology();
        }

        if (hasKafka()) {
            buildKafkaTopology();
        }

        if (hasDb()) {
            buildDatabaseTopology();
        }

        return Topology.of(nodes, edges);
    }

    private void buildServiceTopology() {
        final Map<TraceId, Map<SpanId, Span>> clientSpanMap =
                clientSpans.stream()
                        .collect(
                                Collectors.groupingBy(
                                        Span::getTraceId,
                                        Collectors.toMap(Span::getId, Function.identity())));

        for (Span serverSpan : serverSpans) {
            final Map<SpanId, Span> clientSpansInTrace = clientSpanMap.get(serverSpan.getTraceId());

            if (clientSpansInTrace == null) {
                continue;
            }

            final Span clientSpan = clientSpansInTrace.get(serverSpan.getParentId());

            if (clientSpan != null) {
                final String sourceService = clientSpan.getServiceName();
                final String targetService = serverSpan.getServiceName();

                addNode(sourceService, NodeType.SERVICE);
                addNode(targetService, NodeType.SERVICE);
                addEdge(sourceService, targetService);
            }
        }
    }

    private void buildKafkaTopology() {
        addNode(KAFKA_NODE_NAME, NodeType.KAFKA);
        if (producerSpans != null && !producerSpans.isEmpty()) {
            processKafkaSpans(producerSpans, true);
        }
        if (consumerSpans != null && !consumerSpans.isEmpty()) {
            processKafkaSpans(consumerSpans, false);
        }
    }

    private void processKafkaSpans(List<Span> spans, boolean isProducer) {
        for (Span span : spans) {
            final String serviceName = span.getServiceName();
            addNode(serviceName, NodeType.SERVICE);

            if (isProducer) {
                addEdge(serviceName, KAFKA_NODE_NAME);
            } else {
                addEdge(KAFKA_NODE_NAME, serviceName);
            }
        }
    }

    private void buildDatabaseTopology() {
        for (Span dbSpan : dbSpans) {
            final String dbSystem = (String) dbSpan.getAttributes().data().get("db.system");
            final String serviceName = dbSpan.getServiceName();

            addNode(serviceName, NodeType.SERVICE);
            addNode(dbSystem, NodeType.DATABASE);
            addEdge(serviceName, dbSystem);
        }
    }

    private boolean hasKafka() {
        return (producerSpans != null && !producerSpans.isEmpty())
                || (consumerSpans != null && !consumerSpans.isEmpty());
    }

    private boolean hasDb() {
        return dbSpans != null && !dbSpans.isEmpty();
    }
}
