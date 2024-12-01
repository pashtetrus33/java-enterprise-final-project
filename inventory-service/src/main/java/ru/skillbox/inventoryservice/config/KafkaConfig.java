
package ru.skillbox.inventoryservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;
import ru.skillbox.inventoryservice.dto.ErrorKafkaDto;
import ru.skillbox.inventoryservice.dto.InventoryKafkaDto;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "inventory-service-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        return props;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        BackOff fixedBackOff = new FixedBackOff(1L, 0L);
        return new DefaultErrorHandler(fixedBackOff);
    }

    @Bean
    public ConsumerFactory<Long, ErrorKafkaDto> consumerErrorFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new LongDeserializer(),
                new JsonDeserializer<>(ErrorKafkaDto.class, false)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, ErrorKafkaDto> kafkaListenerContainerErrorFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, ErrorKafkaDto> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerErrorFactory());
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    @Bean
    public ConsumerFactory<Long, InventoryKafkaDto> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new LongDeserializer(),
                new JsonDeserializer<>(InventoryKafkaDto.class, false)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, InventoryKafkaDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, InventoryKafkaDto> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "inventory-service");
        return props;
    }

    @Bean
    public ProducerFactory<Long, Object> producerKafkaFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<Long, Object> kafkaTemplate() {
        KafkaTemplate<Long, Object> template = new KafkaTemplate<>(producerKafkaFactory());
        template.setMessageConverter(new StringJsonMessageConverter());
        return template;
    }
}
