package com.myspace.demo;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitInstance;

import static java.util.stream.Collectors.toList;

@Path("/$endpointName$")
public class $unit$Query$name$Endpoint {

    RuleUnit<$UnitType$> ruleUnit;

    public $unit$Query$name$Endpoint() { }

    public $unit$Query$name$Endpoint(RuleUnit<$UnitType$> ruleUnit) {
        this.ruleUnit = ruleUnit;
    }

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<$ReturnType$> executeQuery($UnitTypeDTO$ unitDTO) {
        RuleUnitInstance<$UnitType$> instance = ruleUnit.createInstance(unitDTO.get());
        List<$ReturnType$> response = instance.executeQuery( "$queryName$" ).stream().map( this::toResult ).collect( toList() );
        return response;
    }

    @POST()
    @Path("/first")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public $ReturnType$ executeQueryFirst($UnitTypeDTO$ unitDTO) {
        List<$ReturnType$> results = executeQuery(unitDTO);
        $ReturnType$ response = results.isEmpty() ? null : results.get(0);
        return response;
    }

    private $ReturnType$ toResult(Map<String, Object> tuple) {
        return ($ReturnType$) tuple.get("");
    }
}
