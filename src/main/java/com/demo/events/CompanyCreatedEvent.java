package com.demo.events;

public class CompanyCreatedEvent extends BaseEvent<String> {
    public String name;

    public CompanyCreatedEvent() {
    }

    public CompanyCreatedEvent(String id, String name) {
        super(id);
        this.name = name;
    }
}
