package com.univ.tracedin.infra.span.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.common.dto.SearchCursor;
import com.univ.tracedin.common.dto.SearchResult;
import com.univ.tracedin.domain.project.EndPointUrl;
import com.univ.tracedin.domain.project.HttpTps;
import com.univ.tracedin.domain.project.ProjectKey;
import com.univ.tracedin.domain.project.ServiceSearchCondition;
import com.univ.tracedin.domain.project.StatusCodeDistribution;
import com.univ.tracedin.domain.project.StatusCodeDistribution.StatusCodeBucket;
import com.univ.tracedin.domain.project.TraceHipMap;
import com.univ.tracedin.domain.project.TraceHipMap.EndTimeBucket;
import com.univ.tracedin.domain.project.TraceSearchCondition;
import com.univ.tracedin.domain.span.Span;
import com.univ.tracedin.domain.span.SpanId;
import com.univ.tracedin.domain.span.SpanKind;
import com.univ.tracedin.domain.span.SpanRepository;
import com.univ.tracedin.domain.span.SpanType;
import com.univ.tracedin.domain.span.Trace;
import com.univ.tracedin.domain.span.TraceId;
import com.univ.tracedin.infra.span.document.SpanDocument;

@Transactional
@Repository
@RequiredArgsConstructor
public class SpanCoreRepository implements SpanRepository {

    private final SpanElasticSearchRepository spanElasticSearchRepository;

    @Override
    public void saveAll(List<Span> spans) {
        spanElasticSearchRepository.saveAll(spans.stream().map(SpanDocument::from).toList());
    }

    @Override
    public void save(Span span) {
        spanElasticSearchRepository.save(SpanDocument.from(span));
    }

    @Override
    public Span findById(SpanId id) {
        return spanElasticSearchRepository
                .findById(id.getValue())
                .map(SpanDocument::toSpan)
                .orElseThrow(() -> new RuntimeException("Span not found"));
    }

    @Override
    public List<Span> findByProjectKeyAndSpanKind(
            ProjectKey projectKey, SpanType spanType, SpanKind spanKind) {
        return spanElasticSearchRepository.search(projectKey.value(), spanType, spanKind).stream()
                .map(SpanDocument::toSpan)
                .toList();
    }

    @Override
    public SearchResult<Trace> findTracesByNode(TraceSearchCondition cond, SearchCursor cursor) {
        return spanElasticSearchRepository.searchTracesByNode(
                cond, cursor.size(), cursor.afterKey());
    }

    @Override
    public List<Span> findByTraceId(TraceId traceId) {
        return spanElasticSearchRepository.findByTraceId(traceId.getValue()).stream()
                .map(SpanDocument::toSpan)
                .toList();
    }

    @Override
    public TraceHipMap getTraceHitMap(TraceSearchCondition cond) {
        final List<EndTimeBucket> traceHitMapByProjectKey =
                spanElasticSearchRepository.getTraceHitMapByProjectKey(cond);
        return TraceHipMap.from(traceHitMapByProjectKey);
    }

    @Override
    public StatusCodeDistribution getStatusCodeDistribution(TraceSearchCondition cond) {
        final List<StatusCodeBucket> statusCodeDistribution =
                spanElasticSearchRepository.getStatusCodeDistribution(cond);

        return StatusCodeDistribution.from(statusCodeDistribution);
    }

    @Override
    public List<HttpTps> getHttpTps(TraceSearchCondition cond) {
        return spanElasticSearchRepository.getHttpTps(cond);
    }

    @Override
    public List<EndPointUrl> getEndpoints(ServiceSearchCondition cond) {
        return spanElasticSearchRepository.getEndpointUrls(cond);
    }
}
