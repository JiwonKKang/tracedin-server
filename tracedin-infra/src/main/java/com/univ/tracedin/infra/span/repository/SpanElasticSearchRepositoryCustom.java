package com.univ.tracedin.infra.span.repository;

import java.util.List;
import java.util.Map;

import com.univ.tracedin.common.dto.SearchResult;
import com.univ.tracedin.domain.project.EndPointUrl;
import com.univ.tracedin.domain.project.HttpTps;
import com.univ.tracedin.domain.project.ServiceSearchCondition;
import com.univ.tracedin.domain.project.StatusCodeDistribution.StatusCodeBucket;
import com.univ.tracedin.domain.project.TraceHipMap.EndTimeBucket;
import com.univ.tracedin.domain.project.TraceSearchCondition;
import com.univ.tracedin.domain.span.SpanKind;
import com.univ.tracedin.domain.span.SpanType;
import com.univ.tracedin.domain.span.Trace;
import com.univ.tracedin.infra.span.document.SpanDocument;

public interface SpanElasticSearchRepositoryCustom {

    List<SpanDocument> search(String projectKey, SpanType spanType, SpanKind spanKind);

    List<String> findServiceNames(String projectKey);

    SearchResult<Trace> searchTracesByNode(
            TraceSearchCondition cond, int size, Map<String, Object> afterKey);

    List<SpanDocument> findByTraceId(String traceId);

    List<EndTimeBucket> getTraceHitMapByProjectKey(TraceSearchCondition cond);

    List<StatusCodeBucket> getStatusCodeDistribution(TraceSearchCondition cond);

    List<HttpTps> getHttpTps(TraceSearchCondition cond);

    List<EndPointUrl> getEndpointUrls(ServiceSearchCondition cond);
}
