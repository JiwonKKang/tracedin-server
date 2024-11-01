package com.univ.tracedin.domain.project;

import java.util.List;

public interface ProjectAnalyzer {

    ProjectStatistic<?> analyze(
            TraceSearchCondition cond, ProjectStatistic.StatisticsType statisticsType);

    List<EndPointUrl> getEndpoints(ServiceSearchCondition cond);
}
