package com.demo.controllers;

import com.demo.commands.CreateCompanyCommand;
import com.demo.dto.commands.CompanyCreateDTO;
import com.demo.engine.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CompanyCommandController {

    private final Producer producer;
    private final KafkaTemplate<Object, Object> template;

    CompanyCommandController(Producer producer, KafkaTemplate<Object, Object> template) {
        this.producer = producer;
        this.template = template;
    }

    @PostMapping(value = "/publish")
    public void sendMessageToKafkaTopic(@RequestParam("message") String message) {
        this.producer.sendMessage(message);
    }

    @PostMapping(path = "companies")
    public void createCompany(@RequestBody @Validated CompanyCreateDTO company) {
        CreateCompanyCommand createCompanyCommand = convert(company);
        this.template.executeInTransaction(kafkaTemplate -> {
            kafkaTemplate.send(new ProducerRecord<>("command_company", createCompanyCommand.id, createCompanyCommand));
            return null;
        });
    }

    private CreateCompanyCommand convert(CompanyCreateDTO createDTO) {
        return new CreateCompanyCommand(UUID.randomUUID().toString(), createDTO.getName());
    }
}
