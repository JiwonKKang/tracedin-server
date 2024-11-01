package com.univ.tracedin.api.alert;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.api.alert.dto.AppendAlertMethodRequest;
import com.univ.tracedin.api.global.dto.Response;
import com.univ.tracedin.domain.alert.AlertMethod;
import com.univ.tracedin.domain.alert.AlertMethodId;
import com.univ.tracedin.domain.alert.AlertService;
import com.univ.tracedin.domain.project.ProjectId;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertApi implements AlertApiDocs {

    private final AlertService alertService;

    @Override
    @PostMapping("/methods")
    public Response<Void> appendAlertMethod(AppendAlertMethodRequest request) {
        alertService.appendMethod(request.toAlertInfo());
        return Response.success();
    }

    @Override
    @GetMapping("/methods")
    public Response<List<AlertMethod>> getAllAlertMethods(Long projectId) {
        return Response.success(alertService.readAllMethods(ProjectId.from(projectId)));
    }

    @Override
    @PatchMapping("/methods/{alertMethodId}/deactivate")
    public Response<Void> deactivateAlertMethod(@PathVariable Long alertMethodId) {
        alertService.deactivateMethod(AlertMethodId.from(alertMethodId));
        return Response.success();
    }

    @Override
    @PatchMapping("/methods/{alertMethodId}/activate")
    public Response<Void> activateAlertMethod(@PathVariable Long alertMethodId) {
        alertService.activateMethod(AlertMethodId.from(alertMethodId));
        return Response.success();
    }
}
