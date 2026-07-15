package com.payments.processor

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker

@TestConfiguration
class TestKafkaConfiguration {
    @Bean
    fun kafkaTemplate(embeddedKafkaBroker: EmbeddedKafkaBroker): KafkaTemplate<String, String> {
        val producerFactory =
            DefaultKafkaProducerFactory<String, String>(
                mapOf(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to embeddedKafkaBroker.brokersAsString,
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ),
            )

        return KafkaTemplate(producerFactory)
    }
}
