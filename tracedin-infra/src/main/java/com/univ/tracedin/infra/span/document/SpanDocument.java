package com.univ.tracedin.infra.span.document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.univ.tracedin.domain.span.Span;
import com.univ.tracedin.domain.span.SpanAttributes;
import com.univ.tracedin.domain.span.SpanEvent;
import com.univ.tracedin.domain.span.SpanId;
import com.univ.tracedin.domain.span.SpanKind;
import com.univ.tracedin.domain.span.SpanStatus;
import com.univ.tracedin.domain.span.SpanTiming;
import com.univ.tracedin.domain.span.SpanType;
import com.univ.tracedin.domain.span.TraceId;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "span")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Mapping(mappingPath = "elastic/span-mappings.json")
@Setting(settingPath = "elastic/span-settings.json")
public class SpanDocument implements Serializable {

    @Id private String spanId;

    private String serviceName;

    private String projectKey;

    private String traceId;

    private String parentSpanId;

    private SpanType spanType;

    private String name;

    private SpanKind kind;

    private Long startEpochMillis;

    private Long endEpochMillis;

    private SpanStatus status;

    private Attributes attributes;

    private List<Event> events;

    public static SpanDocument from(Span span) {
        return SpanDocument.builder()
                .spanId(span.getId().getValue())
                .serviceName(span.getServiceName())
                .projectKey(span.getProjectKey())
                .traceId(span.getTraceId().getValue())
                .parentSpanId(span.getParentId().getValue())
                .spanType(span.getSpanType())
                .name(span.getName())
                .kind(span.getKind())
                .startEpochMillis(span.getTiming().startEpochMillis())
                .endEpochMillis(span.getTiming().endEpochMillis())
                .status(span.getStatus())
                .attributes(Attributes.from(span.getAttributes()))
                .events(span.getEvents().stream().map(Event::from).toList())
                .build();
    }

    public Span toSpan() {
        return Span.builder()
                .id(SpanId.from(spanId))
                .traceId(TraceId.from(traceId))
                .parentId(SpanId.from(parentSpanId))
                .serviceName(serviceName)
                .projectKey(projectKey)
                .spanType(spanType)
                .name(name)
                .kind(kind)
                .timing(
                        SpanTiming.builder()
                                .startEpochMillis(startEpochMillis)
                                .endEpochMillis(endEpochMillis)
                                .build())
                .status(status)
                .attributes(attributes.toSpanAttributes())
                .events(events.stream().map(Event::toSpanEvent).toList())
                .build();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Attributes {

        @Field(type = FieldType.Object)
        private Map<String, Object> data;

        @Field(type = FieldType.Integer)
        private Integer capacity;

        @Field(type = FieldType.Integer)
        private Integer totalAddedValues;

        public static Attributes from(SpanAttributes spanAttributes) {
            return builder()
                    .data(spanAttributes.data())
                    .capacity(spanAttributes.capacity())
                    .totalAddedValues(spanAttributes.totalAddedValues())
                    .build();
        }

        public SpanAttributes toSpanAttributes() {
            return SpanAttributes.builder()
                    .data(data)
                    .capacity(capacity)
                    .totalAddedValues(totalAddedValues)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Event {

        @Field(type = FieldType.Keyword)
        private String name;

        @Field(type = FieldType.Object)
        private Map<String, Object> attributes;

        @Field(type = FieldType.Long)
        private Long epochNanos;

        public static Event from(SpanEvent spanEvent) {
            return Event.builder()
                    .name(spanEvent.name())
                    .attributes(spanEvent.attributes())
                    .epochNanos(spanEvent.epochNanos())
                    .build();
        }

        public SpanEvent toSpanEvent() {
            return SpanEvent.builder()
                    .name(name)
                    .attributes(attributes)
                    .epochNanos(epochNanos)
                    .build();
        }
    }
}
