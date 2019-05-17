package com.demo.commands;

public class CreateCompanyCommand extends BaseCommand<String> {

    public String name;
    public String address;

    public CreateCompanyCommand() {
    }

    public CreateCompanyCommand(String id, String name, String address) {
        super(id);
        this.name = name;
        this.address = address;
    }
}
