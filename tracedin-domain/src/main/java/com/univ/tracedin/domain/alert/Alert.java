package com.univ.tracedin.domain.alert;

import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import com.univ.tracedin.domain.project.ProjectId;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Alert {

    private AlertId id;

    private Receiver receiver;

    private String title;

    private Map<String, String> details;

    private boolean isRead;

    public static Alert create(String title, ProjectId projectId, Map<String, String> details) {
        return builder()
                .receiver(Receiver.from(projectId))
                .title(title)
                .details(details)
                .isRead(false)
                .build();
    }
}
