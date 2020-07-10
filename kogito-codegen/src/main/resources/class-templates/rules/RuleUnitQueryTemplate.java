package com.myspace.demo;

import java.util.List;
import java.util.Map;

import org.kie.kogito.rules.RuleUnitInstance;

import static java.util.stream.Collectors.toList;

public class $unit$Query$name$ implements org.kie.kogito.rules.RuleUnitQuery<List<$ReturnType$>> {

    private final RuleUnitInstance<$UnitType$> instance;

    public $unit$Query$name$Endpoint(RuleUnitInstance<$UnitType$> instance) {
        this.instance = instance;
    }

    @Override
    public List<$ReturnType$> execute() {
        return instance.executeQuery( "$queryName$" ).stream().map(this::toResult).collect(toList());
    }

    private $ReturnType$ toResult(Map<String, Object> tuple) {
        return ($ReturnType$) tuple.get("");
    }
}
