package com.myspace.demo;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitInstance;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/$endpointName$")
public class $unit$Query$name$Endpoint {

    @Autowired
    RuleUnit<$UnitType$> ruleUnit;

    public $unit$Query$name$Endpoint() { }

    public $unit$Query$name$Endpoint(RuleUnit<$UnitType$> ruleUnit) {
        this.ruleUnit = ruleUnit;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<$ReturnType$> executeQuery(@RequestBody(required = true) $UnitTypeDTO$ unitDTO) {
        RuleUnitInstance<$UnitType$> instance = ruleUnit.createInstance();
        return instance.executeQuery($unit$Query$name$.class);
    }

    @PostMapping(value = "/first", produces = MediaType.APPLICATION_JSON_VALUE, consumes =
            MediaType.APPLICATION_JSON_VALUE)
    public $ReturnType$ executeQueryFirst(@RequestBody(required = true) $UnitTypeDTO$ unitDTO) {
        List<$ReturnType$> results = executeQuery(unitDTO);
        $ReturnType$ response = results.isEmpty() ? null : results.get(0);
        return response;
    }
}