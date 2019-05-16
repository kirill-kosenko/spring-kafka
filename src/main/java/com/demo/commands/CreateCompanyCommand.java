package com.demo.commands;

public class CreateCompanyCommand extends BaseCommand<String> {

    public String name;

    public CreateCompanyCommand() {
    }

    public CreateCompanyCommand(String id, String name) {
        super(id);
        this.name = name;
    }
}
