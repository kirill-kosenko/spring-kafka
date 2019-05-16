package com.demo.events;

public class BaseEvent<T> {

    public T id;

    public BaseEvent() {}

    public BaseEvent(T id) {
        this.id = id;
    }
}
