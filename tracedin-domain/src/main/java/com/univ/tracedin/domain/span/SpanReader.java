package com.univ.tracedin.domain.span;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.common.dto.SearchCursor;
import com.univ.tracedin.common.dto.SearchResult;
import com.univ.tracedin.domain.project.ProjectKey;
import com.univ.tracedin.domain.project.TraceSearchCondition;

@Component
@RequiredArgsConstructor
public class SpanReader {

    private final SpanRepository spanRepository;

    public List<Span> read(ProjectKey projectKey, SpanType spanType, SpanKind spanKind) {
        return spanRepository.findByProjectKeyAndSpanKind(projectKey, spanType, spanKind);
    }

    public CompletableFuture<List<Span>> readAsync(
            ProjectKey projectKey, SpanType spanType, SpanKind spanKind) {
        return CompletableFuture.completedFuture(read(projectKey, spanType, spanKind));
    }

    public CompletableFuture<List<Span>> readClientSpans(ProjectKey projectKey) {
        return readAsync(projectKey, SpanType.HTTP, SpanKind.CLIENT);
    }

    public CompletableFuture<List<Span>> readServerSpans(ProjectKey projectKey) {
        return readAsync(projectKey, SpanType.HTTP, SpanKind.SERVER);
    }

    public CompletableFuture<List<Span>> readProducerSpans(ProjectKey projectKey) {
        return readAsync(projectKey, SpanType.UNKNOWN, SpanKind.PRODUCER);
    }

    public CompletableFuture<List<Span>> readConsumerSpans(ProjectKey projectKey) {
        return readAsync(projectKey, SpanType.UNKNOWN, SpanKind.CONSUMER);
    }

    public CompletableFuture<List<Span>> readDbSpans(ProjectKey projectKey) {
        return readAsync(projectKey, SpanType.QUERY, SpanKind.CLIENT);
    }

    public SearchResult<Trace> read(TraceSearchCondition cond, SearchCursor cursor) {
        return spanRepository.findTracesByNode(cond, cursor);
    }

    public List<Span> read(TraceId traceId) {
        return spanRepository.findByTraceId(traceId);
    }

    public Span read(SpanId spanId) {
        return spanRepository.findById(spanId);
    }
}
