package com.demo.commands;

public class BaseCommand<T> {

    public T id;

    public BaseCommand() {}

    public BaseCommand(T id) {
        this.id = id;
    }
}
