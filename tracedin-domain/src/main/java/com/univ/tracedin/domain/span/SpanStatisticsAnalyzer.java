package com.univ.tracedin.domain.span;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.domain.project.EndPointUrl;
import com.univ.tracedin.domain.project.NetworkTopologyAnalyer;
import com.univ.tracedin.domain.project.Project;
import com.univ.tracedin.domain.project.ProjectAnalyzer;
import com.univ.tracedin.domain.project.ProjectStatistic;
import com.univ.tracedin.domain.project.ProjectStatistic.StatisticsType;
import com.univ.tracedin.domain.project.ServiceSearchCondition;
import com.univ.tracedin.domain.project.TraceSearchCondition;
import com.univ.tracedin.domain.project.exception.NetworkTopologyBuildException;

@Component
@RequiredArgsConstructor
public class SpanStatisticsAnalyzer implements ProjectAnalyzer, NetworkTopologyAnalyer {

    private final SpanReader spanReader;
    private final SpanRepository spanRepository;

    @Override
    public ProjectStatistic<?> analyze(TraceSearchCondition cond, StatisticsType statisticsType) {
        return switch (statisticsType) {
            case HTTP_TPS ->
                    ProjectStatistic.of(spanRepository.getHttpTps(cond), StatisticsType.HTTP_TPS);
            case TRACE_HIT_MAP ->
                    ProjectStatistic.of(
                            spanRepository.getTraceHitMap(cond), StatisticsType.TRACE_HIT_MAP);
            case STATUS_CODE ->
                    ProjectStatistic.of(
                            spanRepository.getStatusCodeDistribution(cond),
                            StatisticsType.STATUS_CODE);
        };
    }

    @Override
    public List<EndPointUrl> getEndpoints(ServiceSearchCondition cond) {
        return spanRepository.getEndpoints(cond);
    }

    @Override
    public Topology analyze(Project project) {
        final var clientSpansFuture = spanReader.readClientSpans(project.getProjectKey());
        final var serverSpansFuture = spanReader.readServerSpans(project.getProjectKey());
        final var producerSpansFuture = spanReader.readProducerSpans(project.getProjectKey());
        final var consumerSpansFuture = spanReader.readConsumerSpans(project.getProjectKey());
        final var dbSpansFuture = spanReader.readDbSpans(project.getProjectKey());

        return CompletableFuture.allOf(
                        clientSpansFuture,
                        serverSpansFuture,
                        producerSpansFuture,
                        consumerSpansFuture,
                        dbSpansFuture)
                .thenApply(
                        v -> {
                            try {
                                return Topology.builder()
                                        .projectKey(project.getProjectKey())
                                        .client(clientSpansFuture.join())
                                        .server(serverSpansFuture.join())
                                        .producer(producerSpansFuture.join())
                                        .consumer(consumerSpansFuture.join())
                                        .db(dbSpansFuture.join())
                                        .build();
                            } catch (Exception e) {
                                throw NetworkTopologyBuildException.EXCEPTION;
                            }
                        })
                .join();
    }
}
