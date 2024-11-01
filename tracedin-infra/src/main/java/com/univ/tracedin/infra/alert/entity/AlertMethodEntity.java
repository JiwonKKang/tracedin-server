package com.univ.tracedin.infra.alert.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.univ.tracedin.domain.alert.AlertMethod;
import com.univ.tracedin.domain.alert.AlertMethodId;
import com.univ.tracedin.domain.alert.AlertType;
import com.univ.tracedin.domain.project.ProjectId;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "alert_method")
public class AlertMethodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;

    @Enumerated(EnumType.STRING)
    private AlertType alertType;

    private String contact;

    private boolean isActivated;

    public static AlertMethodEntity from(AlertMethod alertMethod) {
        final Long alertMethodId =
                (alertMethod.getId() == null) ? null : alertMethod.getId().getValue();
        return builder()
                .id(alertMethodId)
                .projectId(alertMethod.getProjectId().getValue())
                .alertType(alertMethod.getAlertType())
                .contact(alertMethod.getContact())
                .isActivated(alertMethod.isActivated())
                .build();
    }

    public AlertMethod toDomain() {
        return AlertMethod.builder()
                .id(AlertMethodId.from(id))
                .projectId(ProjectId.from(projectId))
                .alertType(alertType)
                .contact(contact)
                .isActivated(isActivated)
                .build();
    }
}
