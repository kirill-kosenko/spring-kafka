package com.demo.events;

public class CompanyCreatedEvent extends BaseEvent<String> {
    public String name;
    public String address;

    public CompanyCreatedEvent() {
    }

    public CompanyCreatedEvent(String id, String name, String address) {
        super(id);
        this.name = name;
        this.address = address;
    }
}
