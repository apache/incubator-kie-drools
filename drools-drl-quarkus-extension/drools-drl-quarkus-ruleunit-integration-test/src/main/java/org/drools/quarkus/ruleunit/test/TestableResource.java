package org.drools.quarkus.ruleunit.test;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;

@Path("/test")
public class TestableResource {
    
    @Inject
    RuleUnit<HelloWorldUnit> ruleUnit;
    
    @GET
    @Path("testRuleUnit")
    public Response testRuleUnit() {
        HelloWorldUnit unit = new HelloWorldUnit();
        unit.getStrings().add("Mario");

        try ( RuleUnitInstance<HelloWorldUnit> instance = ruleUnit.createInstance(unit)  ) {
            instance.fire();
        }

        assertThat(unit.getResults()).hasSize(1)
            .containsExactly("Hello Mario");
        
        return Response.ok().build();
    }
}
