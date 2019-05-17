package com.demo.controllers;

import com.demo.commands.CreateCompanyCommand;
import com.demo.commands.UpdateCompanyCommand;
import com.demo.dto.commands.CompanyCreateDTO;
import com.demo.dto.commands.CompanyUpdateDTO;
import com.demo.dto.commands.ValidationResult;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@RestController
public class CompanyCommandController {

    private final KafkaTemplate<Object, Object> template;
    private volatile Map<String, CountDownLatch> requestsLatches = new ConcurrentHashMap<>();
    private volatile Map<String, ValidationResult> validationResults = new HashMap<>();

    CompanyCommandController(KafkaTemplate<Object, Object> template) {
        this.template = template;
    }

    @PostMapping(path = "companies")
    public ResponseEntity<String> createCompany(@RequestBody @Validated CompanyCreateDTO company) throws InterruptedException {
        String requestId = UUID.randomUUID().toString();

        CreateCompanyCommand createCompanyCommand = convert(company);
        createCompanyCommand._requestId = requestId;
        this.template.executeInTransaction(kafkaTemplate -> {
            kafkaTemplate.send(new ProducerRecord<>("command_company", createCompanyCommand.id, createCompanyCommand));
            return null;
        });

        CountDownLatch latch = new CountDownLatch(1);
        this.requestsLatches.put(requestId, latch);
        latch.await();

        ValidationResult result = validationResults.get(requestId);

        return result.isValid
                ? ResponseEntity.ok(result.id)
                : ResponseEntity.unprocessableEntity().body(result.message);
    }

    @PatchMapping(value = "companies/{id}")
    public ResponseEntity<String> updateCompany(@RequestBody @Validated CompanyUpdateDTO company, @PathVariable("id") String id) throws InterruptedException {
        String requestId = UUID.randomUUID().toString();

        UpdateCompanyCommand updateCompantyCommand = convert(company);
        updateCompantyCommand.id = id;
        updateCompantyCommand._requestId = requestId;
        this.template.executeInTransaction(kafkaTemplate -> {
            kafkaTemplate.send(new ProducerRecord<>("command_company", updateCompantyCommand.id, updateCompantyCommand));
            return null;
        });

        CountDownLatch latch = new CountDownLatch(1);
        this.requestsLatches.put(requestId, latch);
        latch.await();

        ValidationResult result = validationResults.get(requestId);

        return result.isValid
                ? ResponseEntity.ok(result.id)
                : ResponseEntity.unprocessableEntity().body(result.message);
    }

    @KafkaListener(topics = "command_company_validation_result")
    public void validationResults(ConsumerRecord<String, ValidationResult> result, Acknowledgment ack) {
        CountDownLatch latch = this.requestsLatches.get(result.value()._requestId);
        if (null != latch) {
            this.validationResults.put(result.value()._requestId, result.value());
            latch.countDown();
        }
        ack.acknowledge();
    }

    private CreateCompanyCommand convert(CompanyCreateDTO createDTO) {
        return new CreateCompanyCommand(UUID.randomUUID().toString(), createDTO.getName(), createDTO.getAddress());
    }

    private UpdateCompanyCommand convert(CompanyUpdateDTO updateDTO) {
        return new UpdateCompanyCommand(UUID.randomUUID().toString(), updateDTO.getName(), updateDTO.getAddress());
    }
}
