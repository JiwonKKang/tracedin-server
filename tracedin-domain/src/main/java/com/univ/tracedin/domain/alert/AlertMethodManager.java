package com.univ.tracedin.domain.alert;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.domain.project.ProjectId;

@Component
@RequiredArgsConstructor
public class AlertMethodManager {

    private final AlertMethodRepository alertMethodRepository;

    public void append(AlertInfo alertInfo) {
        final AlertMethod alertMethod = AlertMethod.create(alertInfo);
        alertMethodRepository.saveMethod(alertMethod);
    }

    public AlertMethod read(AlertMethodId alertMethodId) {
        return alertMethodRepository.findMethodById(alertMethodId);
    }

    public List<AlertMethod> readAll(ProjectId projectId) {
        return alertMethodRepository.findAllMethodByProjectId(projectId);
    }

    public void remove(AlertMethod alertMethod) {
        alertMethodRepository.deleteMethod(alertMethod);
    }

    public void deactivate(AlertMethod alertMethod) {
        alertMethod.deactivate();
        alertMethodRepository.saveMethod(alertMethod);
    }

    public void activate(AlertMethod alertMethod) {
        alertMethod.activate();
        alertMethodRepository.saveMethod(alertMethod);
    }
}
