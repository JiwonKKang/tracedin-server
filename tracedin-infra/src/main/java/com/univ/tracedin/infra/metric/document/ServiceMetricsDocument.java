package com.univ.tracedin.infra.metric.document;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.univ.tracedin.domain.metric.Metric;
import com.univ.tracedin.domain.metric.ServiceMetrics;
import com.univ.tracedin.domain.project.ProjectKey;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "service-metrics")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Mapping(mappingPath = "elastic/metric-mappings.json")
@Setting(settingPath = "elastic/metric-settings.json")
public class ServiceMetricsDocument {

    @Id private String id;

    private String projectKey;

    private String serviceName;

    List<Metric> metrics;

    public static ServiceMetricsDocument from(ServiceMetrics serviceMetrics) {
        return builder()
                .projectKey(serviceMetrics.getProjectKey().value())
                .serviceName(serviceMetrics.getServiceName())
                .metrics(serviceMetrics.getMetrics())
                .build();
    }

    public ServiceMetrics toServiceMetrics() {
        return ServiceMetrics.builder()
                .projectKey(ProjectKey.from(projectKey))
                .serviceName(serviceName)
                .metrics(metrics)
                .build();
    }
}
