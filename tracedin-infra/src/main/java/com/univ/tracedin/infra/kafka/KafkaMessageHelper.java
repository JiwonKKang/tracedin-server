package com.univ.tracedin.infra.kafka;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class KafkaMessageHelper {

    public static <K, V> CompletableFuture<SendResult<K, V>> getKafkaCallback(V payload) {
        return new CompletableFuture<>() {
            @Override
            public boolean complete(SendResult<K, V> value) {
                final RecordMetadata metadata = value.getRecordMetadata();
                log.info(
                        "Kafka message sent successfully: Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp());
                return super.complete(value);
            }

            @Override
            public boolean completeExceptionally(Throwable ex) {
                log.error("Error while sending Kafka message: {}", payload.toString(), ex);
                return super.completeExceptionally(ex);
            }
        };
    }

    private KafkaMessageHelper() {}
}
