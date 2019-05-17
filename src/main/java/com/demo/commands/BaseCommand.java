package com.demo.commands;

import com.demo.serde.JSONSerdeCompatible;

public class BaseCommand<T> implements JSONSerdeCompatible {

    public T id;
    public String _requestId;

    public BaseCommand() {}

    public BaseCommand(T id) {
        this.id = id;
    }

    public BaseCommand(T id, String _requestId) {
        this.id = id;
        this._requestId = _requestId;
    }
}
