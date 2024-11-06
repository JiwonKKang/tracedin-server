package com.univ.tracedin.infra.span.repository;

import static com.univ.tracedin.infra.elasticsearch.ESUtils.executeESQuery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.json.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.univ.tracedin.common.dto.SearchResult;
import com.univ.tracedin.domain.project.EndPointUrl;
import com.univ.tracedin.domain.project.HttpTps;
import com.univ.tracedin.domain.project.ServiceSearchCondition;
import com.univ.tracedin.domain.project.StatusCodeDistribution.StatusCodeBucket;
import com.univ.tracedin.domain.project.TraceHipMap.EndTimeBucket;
import com.univ.tracedin.domain.project.TraceHipMap.ResponseTimeBucket;
import com.univ.tracedin.domain.project.TraceSearchCondition;
import com.univ.tracedin.domain.span.SpanKind;
import com.univ.tracedin.domain.span.SpanType;
import com.univ.tracedin.domain.span.Trace;
import com.univ.tracedin.domain.span.TraceId;
import com.univ.tracedin.infra.span.document.SpanDocument;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.CompositeAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.CompositeAggregationSource;
import co.elastic.clients.elasticsearch._types.aggregations.DateHistogramAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.DateHistogramBucket;
import co.elastic.clients.elasticsearch._types.aggregations.HistogramAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.HistogramBucket;
import co.elastic.clients.elasticsearch._types.aggregations.NestedAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.TopHitsAggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.Builder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;

@Slf4j
@RequiredArgsConstructor
public class SpanElasticSearchRepositoryCustomImpl implements SpanElasticSearchRepositoryCustom {

    private static final String INDEX_NAME = "span";
    private static final String SERVICE_AGGREGATION_KEY = "service_nodes";

    private final ElasticsearchClient client;

    @Override
    public List<SpanDocument> search(String projectKey, SpanType spanType, SpanKind spanKind) {
        final Query spanQuery = createSpanQuery(projectKey, spanType, spanKind);

        return executeESQuery(
                () ->
                        client
                                .search(
                                        s -> s.index(INDEX_NAME).size(10000).query(spanQuery),
                                        SpanDocument.class)
                                .hits()
                                .hits()
                                .stream()
                                .map(Hit::source)
                                .toList());
    }

    @Override
    public List<String> findServiceNames(String projectKey) {
        return executeESQuery(
                () -> {
                    final SearchResponse<Void> response =
                            client.search(
                                    s ->
                                            s.index(INDEX_NAME)
                                                    .size(0)
                                                    .query(
                                                            QueryBuilders.term(
                                                                    t ->
                                                                            t.field("projectKey")
                                                                                    .value(
                                                                                            projectKey)))
                                                    .aggregations(
                                                            "service_nodes",
                                                            createServiceNameAggregation()),
                                    Void.class);

                    return response
                            .aggregations()
                            .get(SERVICE_AGGREGATION_KEY)
                            .sterms()
                            .buckets()
                            .array()
                            .stream()
                            .map(StringTermsBucket::key)
                            .map(FieldValue::stringValue)
                            .toList();
                });
    }

    @Override
    public SearchResult<Trace> searchTracesByNode(
            TraceSearchCondition cond, int size, Map<String, Object> afterKey) {
        return executeESQuery(
                () -> {
                    final SearchResponse<Void> response =
                            client.search(
                                    s ->
                                            s.index(INDEX_NAME)
                                                    .size(0)
                                                    .query(createSearchQuery(cond))
                                                    .aggregations(
                                                            "by_trace",
                                                            createTraceAggregation(size, afterKey))
                                                    .aggregations(
                                                            "trace_count",
                                                            Aggregation.of(
                                                                    a ->
                                                                            a.cardinality(
                                                                                    c ->
                                                                                            c.field(
                                                                                                    "traceId")))),
                                    Void.class);

                    final CompositeAggregate spansByTrace =
                            response.aggregations().get("by_trace").composite();
                    final List<Trace> traces = extractTraces(spansByTrace);
                    final long traceCount =
                            response.aggregations().get("trace_count").cardinality().value();
                    final Map<String, Object> nextAfterKey = toObjectMap(spansByTrace.afterKey());
                    return SearchResult.success(traces, nextAfterKey, traceCount);
                });
    }

    @Override
    public List<SpanDocument> findByTraceId(String traceId) {
        final Query query = QueryBuilders.term(t -> t.field("traceId").value(traceId));

        return executeESQuery(
                () -> {
                    final SearchResponse<SpanDocument> response =
                            client.search(
                                    s -> s.index(INDEX_NAME).size(100).query(query),
                                    SpanDocument.class);

                    return response.hits().hits().stream().map(Hit::source).toList();
                });
    }

    @Override
    public List<EndTimeBucket> getTraceHitMapByProjectKey(TraceSearchCondition cond) {
        return executeESQuery(
                () -> {
                    final SearchResponse<Void> response =
                            client.search(
                                    s ->
                                            s.index(INDEX_NAME)
                                                    .size(0)
                                                    .query(createSearchQuery(cond))
                                                    .aggregations(
                                                            "by_end_time", histogramAggregation()),
                                    Void.class);

                    return parseHistogramResponse(response);
                });
    }

    @Override
    public List<StatusCodeBucket> getStatusCodeDistribution(TraceSearchCondition cond) {
        return executeESQuery(
                () -> {
                    final SearchResponse<Void> response =
                            client.search(
                                    s ->
                                            s.index(INDEX_NAME)
                                                    .size(0)
                                                    .query(createSearchQuery(cond))
                                                    .aggregations(
                                                            "nested_attributes",
                                                            statusCodeDistributionAggregation()),
                                    Void.class);
                    return parseStatusCodeDistributionResponse(response);
                });
    }

    @Override
    public List<HttpTps> getHttpTps(TraceSearchCondition cond) {
        return executeESQuery(
                () -> {
                    final SearchResponse<Void> response =
                            client.search(
                                    s ->
                                            s.index(INDEX_NAME)
                                                    .size(0)
                                                    .query(createSearchQuery(cond))
                                                    .aggregations("http_tps", httpTpsHistogram()),
                                    Void.class);

                    return parseHttpTpsResponse(response);
                });
    }

    @Override
    public List<EndPointUrl> getEndpointUrls(ServiceSearchCondition cond) {
        return executeESQuery(
                () -> {
                    final SearchResponse<Void> response =
                            client.search(
                                    s ->
                                            s.index(INDEX_NAME)
                                                    .size(0)
                                                    .query(createSearchQuery(cond))
                                                    .aggregations(
                                                            "nested_attributes",
                                                            endpointsAggregation()),
                                    Void.class);

                    return parseEndpointUrls(response);
                });
    }

    private static List<EndPointUrl> parseEndpointUrls(SearchResponse<Void> response) {
        return response
                .aggregations()
                .get("nested_attributes")
                .nested()
                .aggregations()
                .get("http_url_agg")
                .sterms()
                .buckets()
                .array()
                .stream()
                .map(bucket -> EndPointUrl.from(bucket.key().stringValue()))
                .toList();
    }

    private static Aggregation endpointsAggregation() {
        return Aggregation.of(
                a ->
                        a.nested(n -> n.path("attributes"))
                                .aggregations(
                                        Map.of(
                                                "http_url_agg",
                                                Aggregation.of(
                                                        agg ->
                                                                agg.terms(
                                                                        t ->
                                                                                t.field(
                                                                                                "attributes.data.http.url.keyword")
                                                                                        .size(
                                                                                                100))))));
    }

    private static Aggregation httpTpsHistogram() {
        return Aggregation.of(
                agg ->
                        agg.dateHistogram(
                                        dh ->
                                                dh.field("startEpochMillis")
                                                        .fixedInterval(Time.of(t -> t.time("10m")))
                                                        .minDocCount(1))
                                .aggregations(
                                        Map.of(
                                                "tps",
                                                Aggregation.of(
                                                        a ->
                                                                a.bucketScript(
                                                                        bs ->
                                                                                bs.bucketsPath(
                                                                                                bp ->
                                                                                                        bp
                                                                                                                .dict(
                                                                                                                        Map
                                                                                                                                .of(
                                                                                                                                        "count",
                                                                                                                                        "_count")))
                                                                                        .script(
                                                                                                s ->
                                                                                                        s
                                                                                                                .inline(
                                                                                                                        i ->
                                                                                                                                i
                                                                                                                                        .source(
                                                                                                                                                "params.count / 600"))))))));
    }

    private static List<HttpTps> parseHttpTpsResponse(SearchResponse<Void> response) {
        final List<HttpTps> httpTpsList = new ArrayList<>();
        response.aggregations()
                .get("http_tps")
                .dateHistogram()
                .buckets()
                .array()
                .forEach(
                        bucket -> {
                            final long startEpochMillis = bucket.key();
                            final double tps =
                                    bucket.aggregations().get("tps").simpleValue().value();
                            httpTpsList.add(HttpTps.of(startEpochMillis, tps));
                        });
        httpTpsList.sort(Comparator.comparing(HttpTps::timestamp).reversed());
        return httpTpsList;
    }

    private static Aggregation statusCodeDistributionAggregation() {
        return Aggregation.of(
                a ->
                        a.nested(n -> n.path("attributes"))
                                .aggregations(
                                        Map.of(
                                                "status_code_distribution",
                                                Aggregation.of(
                                                        agg ->
                                                                agg.terms(
                                                                        st ->
                                                                                st.script(
                                                                                                s ->
                                                                                                        s
                                                                                                                .inline(
                                                                                                                        i ->
                                                                                                                                i.source(
                                                                                                                                                "int statusCodeGroup = (int)(doc['attributes.data.http.status_code'].value / 100); return statusCodeGroup + 'xx';")
                                                                                                                                        .lang(
                                                                                                                                                "painless")))
                                                                                        .size(
                                                                                                10))))));
    }

    private static List<StatusCodeBucket> parseStatusCodeDistributionResponse(
            SearchResponse<Void> response) {
        final List<StatusCodeBucket> statusCodeBuckets = new ArrayList<>();

        // 어그리게이션 이름을 통해 결과 추출
        final NestedAggregate nestedAttributes =
                response.aggregations().get("nested_attributes").nested();

        nestedAttributes
                .aggregations()
                .get("status_code_distribution")
                .sterms()
                .buckets()
                .array()
                .forEach(
                        bucket -> {
                            final String statusCode = bucket.key().stringValue();
                            final long count = bucket.docCount();
                            final StatusCodeBucket statusCodeBucket =
                                    StatusCodeBucket.of(statusCode, count);
                            statusCodeBuckets.add(statusCodeBucket);
                        });
        return statusCodeBuckets;
    }

    private static List<EndTimeBucket> parseHistogramResponse(SearchResponse<Void> response) {
        final List<EndTimeBucket> endTimeBuckets = new ArrayList<>();

        final Aggregate byEndTimeAgg = response.aggregations().get("by_end_time");
        final DateHistogramAggregate dateHistogram = byEndTimeAgg.dateHistogram();

        for (DateHistogramBucket dateBucket : dateHistogram.buckets().array()) {
            final long endEpochMillis = dateBucket.key();

            final Map<String, Aggregate> attributesNested =
                    dateBucket.aggregations().get("attributes_nested").nested().aggregations();

            final HistogramAggregate histogram =
                    attributesNested.get("by_response_time").histogram();

            final List<ResponseTimeBucket> responseTimeBuckets = new ArrayList<>();

            for (HistogramBucket histogramBucket : histogram.buckets().array()) {
                final double responseTime = histogramBucket.key();
                final long count = histogramBucket.docCount();
                final ResponseTimeBucket responseTimeBucket =
                        ResponseTimeBucket.from(responseTime, count);
                responseTimeBuckets.add(responseTimeBucket);
            }
            final long errorCount = attributesNested.get("error_status_count").filter().docCount();
            final EndTimeBucket endTimeBucket =
                    EndTimeBucket.from(endEpochMillis, errorCount, responseTimeBuckets);
            endTimeBuckets.add(endTimeBucket);
        }

        return endTimeBuckets;
    }

    private static Aggregation histogramAggregation() {
        final Aggregation byResponseTimeAgg =
                Aggregation.of(
                        a ->
                                a.histogram(
                                        h ->
                                                h.field("attributes.data.http.response_time_ms")
                                                        .interval(200.0)
                                                        .minDocCount(1)));

        final Aggregation errorStatusCountAgg =
                Aggregation.of(
                        a ->
                                a.filter(
                                        f ->
                                                f.range(
                                                        r ->
                                                                r.field(
                                                                                "attributes.data.http.status_code")
                                                                        .gte(JsonData.of(400)))));

        final Aggregation attributesNestedAgg =
                Aggregation.of(
                        a ->
                                a.nested(n -> n.path("attributes"))
                                        .aggregations(
                                                Map.of(
                                                        "by_response_time",
                                                        byResponseTimeAgg,
                                                        "error_status_count",
                                                        errorStatusCountAgg)));

        return Aggregation.of(
                a ->
                        a.dateHistogram(
                                        dh ->
                                                dh.field("endEpochMillis")
                                                        .fixedInterval(Time.of(t -> t.time("5m")))
                                                        .minDocCount(1))
                                .aggregations(Map.of("attributes_nested", attributesNestedAgg)));
    }

    private static Query nestedHttpUrlTermQuery(String endPointUrl) {
        return QueryBuilders.nested(
                n ->
                        n.path("attributes")
                                .query(
                                        QueryBuilders.term(
                                                t ->
                                                        t.field("attributes.data.http.url.keyword")
                                                                .value(endPointUrl))));
    }

    private static List<Trace> extractTraces(CompositeAggregate spansByTrace) {
        final List<Trace> traces = new ArrayList<>();
        spansByTrace
                .buckets()
                .array()
                .forEach(
                        bucket -> {
                            final TopHitsAggregate spanDetails =
                                    bucket.aggregations().get("span_details").topHits();
                            spanDetails
                                    .hits()
                                    .hits()
                                    .forEach(
                                            hit -> {
                                                if (hit.source() == null) {
                                                    return;
                                                }
                                                final Trace trace =
                                                        mapToTrace(
                                                                hit.source()
                                                                        .toJson()
                                                                        .asJsonObject());

                                                traces.add(trace);
                                            });
                        });
        return traces;
    }

    private static Aggregation createTraceAggregation(int size, Map<String, Object> afterKey) {
        return Aggregation.of(
                a ->
                        a.composite(
                                        c -> {
                                            c.size(size)
                                                    .sources(
                                                            List.of(
                                                                    Map.of(
                                                                            "startEpochMillis",
                                                                            CompositeAggregationSource
                                                                                    .of(
                                                                                            ca ->
                                                                                                    ca
                                                                                                            .terms(
                                                                                                                    t ->
                                                                                                                            t.field(
                                                                                                                                            "startEpochMillis")
                                                                                                                                    .order(
                                                                                                                                            SortOrder
                                                                                                                                                    .Desc)))),
                                                                    Map.of(
                                                                            "traceId",
                                                                            CompositeAggregationSource
                                                                                    .of(
                                                                                            ca ->
                                                                                                    ca
                                                                                                            .terms(
                                                                                                                    t ->
                                                                                                                            t
                                                                                                                                    .field(
                                                                                                                                            "traceId"))))));
                                            if (afterKey != null && !afterKey.isEmpty()) {
                                                c.after(toFieldValueMap(afterKey));
                                            }
                                            return c;
                                        })
                                .aggregations(
                                        "span_details",
                                        Aggregation.of(
                                                agg ->
                                                        agg.topHits(
                                                                th ->
                                                                        th.size(1)
                                                                                .source(
                                                                                        src ->
                                                                                                src
                                                                                                        .filter(
                                                                                                                f ->
                                                                                                                        f
                                                                                                                                .includes(
                                                                                                                                        "traceId",
                                                                                                                                        "serviceName",
                                                                                                                                        "startEpochMillis",
                                                                                                                                        "endEpochMillis",
                                                                                                                                        "attributes.data.http.url",
                                                                                                                                        "attributes.data.http.status_code",
                                                                                                                                        "attributes.data.anomaly")))))));
    }

    private static Query createSearchQuery(ServiceSearchCondition cond) {
        final Builder bool = QueryBuilders.bool();

        if (cond.hasServiceName()) {
            bool.must(QueryBuilders.term(t -> t.field("serviceName").value(cond.getServiceName())));

            if (cond instanceof TraceSearchCondition traceCond && traceCond.hasEndPointUrl()) {
                bool.must(nestedHttpUrlTermQuery(traceCond.getEndPointUrl().value()));
            }
        }

        if (cond instanceof TraceSearchCondition traceCond && traceCond.hasTimeRange()) {
            bool.must(
                    QueryBuilders.range(
                            r ->
                                    r.field("startEpochMillis")
                                            .gte(JsonData.of(traceCond.getEpochMillisStartTime()))),
                    QueryBuilders.range(
                            r ->
                                    r.field("endEpochMillis")
                                            .lte(JsonData.of(traceCond.getEpochMillisEndTime()))));
        }

        bool.must(
                QueryBuilders.term(t -> t.field("projectKey").value(cond.getProjectKey().value())),
                QueryBuilders.term(t -> t.field("spanType").value("HTTP")),
                QueryBuilders.term(t -> t.field("kind").value("SERVER")));

        return bool.build()._toQuery();
    }

    private static Aggregation createServiceNameAggregation() {
        return Aggregation.of(a -> a.terms(t -> t.field("serviceName").size(100)));
    }

    private static Query createSpanQuery(String projectKey, SpanType spanType, SpanKind spanKind) {
        return BoolQuery.of(
                        b ->
                                b.must(
                                        List.of(
                                                QueryBuilders.term(
                                                        t ->
                                                                t.field("projectKey")
                                                                        .value(projectKey)),
                                                QueryBuilders.term(
                                                        t ->
                                                                t.field("spanType")
                                                                        .value(spanType.name())),
                                                QueryBuilders.term(
                                                        t ->
                                                                t.field("kind")
                                                                        .value(spanKind.name())),
                                                QueryBuilders.range(
                                                        r ->
                                                                r.field("startEpochMillis")
                                                                        .gte(
                                                                                JsonData.of(
                                                                                        "now-5m"))))))
                ._toQuery();
    }

    private static Map<String, FieldValue> toFieldValueMap(Map<String, Object> afterKey) {
        return afterKey.entrySet().stream()
                .map(
                        entry -> {
                            if (entry.getValue() instanceof Long) {
                                return Map.entry(
                                        entry.getKey(), FieldValue.of((Long) entry.getValue()));
                            } else if (entry.getValue() instanceof String) {
                                return Map.entry(
                                        entry.getKey(), FieldValue.of((String) entry.getValue()));
                            } else if (entry.getValue() instanceof Integer) {
                                return Map.entry(
                                        entry.getKey(), FieldValue.of((Integer) entry.getValue()));
                            } else if (entry.getValue() instanceof Boolean) {
                                return Map.entry(
                                        entry.getKey(), FieldValue.of((Boolean) entry.getValue()));
                            } else {
                                throw new IllegalArgumentException(
                                        "Unsupported type: " + entry.getValue().getClass());
                            }
                        })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map<String, Object> toObjectMap(Map<String, FieldValue> afterKey) {
        return afterKey.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()._get()));
    }

    private static Trace mapToTrace(JsonObject source) {
        final JsonObject data = source.getJsonObject("attributes").getJsonObject("data");

        return Trace.builder()
                .id(TraceId.from(source.getString("traceId")))
                .isAnomaly(data.containsKey("anomaly"))
                .endPoint(data.getString("http.url"))
                .serviceName(source.getString("serviceName"))
                .statusCode(data.getInt("http.status_code"))
                .endEpochMillis(source.getJsonNumber("endEpochMillis").longValue())
                .startEpochMillis(source.getJsonNumber("startEpochMillis").longValue())
                .build();
    }
}
