package com.univ.tracedin.domain.alert;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.domain.project.ProjectId;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertMethodManager alertMethodManager;

    public void appendMethod(AlertInfo alertInfo) {
        alertMethodManager.append(alertInfo);
    }

    public List<AlertMethod> readAllMethods(ProjectId projectId) {
        return alertMethodManager.readAll(projectId);
    }

    public void removeMethod(AlertMethodId alertMethodId) {
        final AlertMethod alertMethod = alertMethodManager.read(alertMethodId);
        alertMethodManager.remove(alertMethod);
    }

    public void deactivateMethod(AlertMethodId alertMethodId) {
        final AlertMethod alertMethod = alertMethodManager.read(alertMethodId);
        alertMethodManager.deactivate(alertMethod);
    }

    public void activateMethod(AlertMethodId alertMethodId) {
        final AlertMethod alertMethod = alertMethodManager.read(alertMethodId);
        alertMethodManager.activate(alertMethod);
    }
}
