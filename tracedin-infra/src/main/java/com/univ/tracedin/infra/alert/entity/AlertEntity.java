package com.univ.tracedin.infra.alert.entity;

import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.univ.tracedin.domain.alert.Alert;
import com.univ.tracedin.domain.alert.AlertId;
import com.univ.tracedin.domain.alert.Receiver;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "alerts")
public class AlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;

    private String title;

    private boolean isRead;

    @ElementCollection
    @CollectionTable(name = "alert_details", joinColumns = @JoinColumn(name = "alert_id"))
    @MapKeyColumn(name = "detail_key")
    @Column(name = "detail_value", length = 1000)
    private Map<String, String> details;

    public static AlertEntity from(Alert alert) {
        final Long alertId = (alert.getId() == null) ? null : alert.getId().getValue();
        return builder()
                .id(alertId)
                .projectId(alert.getReceiver().projectId().getValue())
                .title(alert.getTitle())
                .isRead(alert.isRead())
                .details(alert.getDetails())
                .build();
    }

    public Alert toDomain() {
        return Alert.builder()
                .id(AlertId.from(id))
                .receiver(Receiver.from(projectId))
                .title(title)
                .isRead(isRead)
                .details(details)
                .build();
    }
}
