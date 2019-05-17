package com.demo.commands;

public class UpdateCompanyCommand extends BaseCommand<String> {
    public String name;
    public String address;

    public UpdateCompanyCommand() {
    }

    public UpdateCompanyCommand(String id, String name, String address) {
        super(id);
        this.name = name;
        this.address = address;
    }
}
