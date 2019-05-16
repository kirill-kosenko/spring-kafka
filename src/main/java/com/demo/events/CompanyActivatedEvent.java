package com.demo.events;

import com.demo.aggregates.Status;

public class CompanyActivatedEvent {
    public final Status status;

    public CompanyActivatedEvent(Status status) {
        this.status = status;
    }
}
