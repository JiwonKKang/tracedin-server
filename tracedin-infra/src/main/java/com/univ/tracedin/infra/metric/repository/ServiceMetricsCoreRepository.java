package com.univ.tracedin.infra.metric.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.domain.metric.ServiceMetrics;
import com.univ.tracedin.domain.metric.ServiceMetricsRepository;
import com.univ.tracedin.infra.metric.document.ServiceMetricsDocument;

@Repository
@Transactional
@RequiredArgsConstructor
public class ServiceMetricsCoreRepository implements ServiceMetricsRepository {

    private final ServiceMetricsElasticSearchRepository serviceMetricsElasticSearchRepository;

    @Override
    public void saveAll(List<ServiceMetrics> metrics) {
        final List<ServiceMetricsDocument> documents =
                metrics.stream().map(ServiceMetricsDocument::from).toList();
        serviceMetricsElasticSearchRepository.saveAll(documents);
    }
}
