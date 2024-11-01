package com.univ.tracedin.domain.alert;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import com.univ.tracedin.domain.project.ProjectId;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AlertMethod {

    private AlertMethodId id;
    private ProjectId projectId;
    private AlertType alertType;
    private String contact;
    private boolean isActivated;

    public void deactivate() {
        this.isActivated = false;
    }

    public static AlertMethod create(AlertInfo alertInfo) {
        return AlertMethod.builder()
                .projectId(alertInfo.projectId())
                .alertType(alertInfo.alertType())
                .contact(alertInfo.contact())
                .isActivated(true)
                .build();
    }

    public void activate() {
        this.isActivated = true;
    }

    // TODO : 각 타입별 contact 유효성 검증

}
