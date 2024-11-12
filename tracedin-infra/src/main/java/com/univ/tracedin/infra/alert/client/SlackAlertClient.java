package com.univ.tracedin.infra.alert.client;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import com.univ.tracedin.domain.alert.Alert;
import com.univ.tracedin.domain.alert.AlertClient;
import com.univ.tracedin.domain.alert.AlertType;
import com.univ.tracedin.infra.alert.exception.AlertSendException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlackAlertClient implements AlertClient {

    private final Slack slackClient = Slack.getInstance();

    @Value("${server.url}")
    private String clientUrl;

    @Override
    public void sendAlert(String contact, Alert alert) {

        try {
            final WebhookResponse response = slackClient.send(contact, buildPayload(alert));

            if (response.getCode() != 200) {
                log.error("Failed to send alert to slack. Response: {}", response);
                throw AlertSendException.EXCEPTION;
            }

        } catch (IOException e) {
            log.error("Failed to send alert to slack", e);
            throw AlertSendException.EXCEPTION;
        }
    }

    @Override
    public AlertType getAlertType() {
        return AlertType.SLACK;
    }

    private Payload buildPayload(Alert alert) {
        final String link = generateLink(alert);
        final String messageText = alert.getTitle() + "\n자세한 내용은 " + link + "를 클릭하세요.";

        return Payload.builder()
                .text(messageText)
                .attachments(buildAttachments(alert.getDetails()))
                .build();
    }

    private String generateLink(Alert alert) {
        final String traceId = alert.getDetails().get("traceId");
        final String encodedTraceId = URLEncoder.encode(traceId, StandardCharsets.UTF_8);
        final String url = clientUrl + "/transactions/" + encodedTraceId;
        final String linkText = "여기";

        return '<' + url + '|' + linkText + '>';
    }

    private static List<Attachment> buildAttachments(Map<String, String> details) {
        return details.entrySet().stream()
                .map(
                        entry ->
                                Attachment.builder()
                                        .title(entry.getKey())
                                        .text(entry.getValue())
                                        .build())
                .toList();
    }
}
