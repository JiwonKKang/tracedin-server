package com.univ.tracedin.domain.alert;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlertSender {

    private final AlertManager alertManager;
    private final AlertMethodManager alertMethodManager;
    private final AlertClientFactory alertClientFactory;

    public void send(Alert alert) {
        alertManager.append(alert);
        alertMethodManager
                .readAll(alert.getReceiver().projectId())
                .forEach(
                        alertMethod -> {
                            if (alertMethod.isActivated()) {
                                final AlertClient client =
                                        alertClientFactory.getClient(alertMethod.getAlertType());
                                client.sendAlert(alertMethod.getContact(), alert);
                            }
                        });
    }
}
