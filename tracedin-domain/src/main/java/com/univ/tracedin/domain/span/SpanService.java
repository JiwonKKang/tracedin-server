package com.univ.tracedin.domain.span;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.common.dto.SearchCursor;
import com.univ.tracedin.common.dto.SearchResult;
import com.univ.tracedin.domain.project.TraceSearchCondition;

@Service
@RequiredArgsConstructor
public class SpanService {

    private final SpanReader spanReader;
    private final SpanMessagePublisher spanMessagePublisher;

    public void appendSpans(List<Span> spans) {
        spanMessagePublisher.publish(SpanCollectedEvent.from(spans));
    }

    public SearchResult<Trace> getTraces(TraceSearchCondition cond, SearchCursor cursor) {
        return spanReader.read(cond, cursor);
    }

    public SpanTree getSpanTree(TraceId traceId) {
        final List<Span> spans = spanReader.read(traceId);
        return SpanTreeBuilder.build(spans);
    }
}
