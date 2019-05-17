package com.demo.events;

import com.demo.serde.JSONSerdeCompatible;

public class BaseEvent<T> implements JSONSerdeCompatible {

    public T id;

    public BaseEvent() {}

    public BaseEvent(T id) {
        this.id = id;
    }
}
