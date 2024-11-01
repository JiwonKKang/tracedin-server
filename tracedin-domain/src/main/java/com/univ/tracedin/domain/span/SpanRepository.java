package com.univ.tracedin.domain.span;

import java.util.List;

import com.univ.tracedin.common.dto.SearchCursor;
import com.univ.tracedin.common.dto.SearchResult;
import com.univ.tracedin.domain.project.EndPointUrl;
import com.univ.tracedin.domain.project.HttpTps;
import com.univ.tracedin.domain.project.ProjectKey;
import com.univ.tracedin.domain.project.ServiceSearchCondition;
import com.univ.tracedin.domain.project.StatusCodeDistribution;
import com.univ.tracedin.domain.project.TraceHipMap;
import com.univ.tracedin.domain.project.TraceSearchCondition;

public interface SpanRepository {

    void saveAll(List<Span> spans);

    void save(Span span);

    Span findById(SpanId id);

    List<Span> findByProjectKeyAndSpanKind(
            ProjectKey projectKey, SpanType spanType, SpanKind spanKind);

    SearchResult<Trace> findTracesByNode(TraceSearchCondition cond, SearchCursor cursor);

    List<Span> findByTraceId(TraceId traceId);

    TraceHipMap getTraceHitMap(TraceSearchCondition cond);

    StatusCodeDistribution getStatusCodeDistribution(TraceSearchCondition cond);

    List<HttpTps> getHttpTps(TraceSearchCondition cond);

    List<EndPointUrl> getEndpoints(ServiceSearchCondition cond);
}
