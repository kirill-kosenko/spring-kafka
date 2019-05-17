package com.demo.events;

public class CompanyUpdatedEvent extends BaseEvent<String> {
    public String name;
    public String address;

    public CompanyUpdatedEvent() {}

    public CompanyUpdatedEvent(String id, String name, String address) {
        super(id);
        this.name = name;
        this.address = address;
    }
}
