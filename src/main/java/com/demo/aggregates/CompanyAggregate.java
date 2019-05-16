package com.demo.aggregates;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CompanyAggregate{
    public String id;
    public String name;

    public void apply(CompanyAggregate aggregate) {
        if (null != aggregate.name) {
            this.name = aggregate.name;
        }
    }
}
