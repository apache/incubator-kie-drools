package org.drools.quarkus.test.hotreload;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;

@Path("/find-adult")
public class FindAdultEndpoint {

    @Inject
    KieRuntimeBuilder kieRuntimeBuilder;

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<String> executeQuery(List<Person> persons) {
        KieSession session = kieRuntimeBuilder.newKieSession();

        List<String> adultNames = new ArrayList<>();
        session.setGlobal("results", adultNames);

        persons.forEach(session::insert);
        session.fireAllRules();

        return adultNames;
    }
}