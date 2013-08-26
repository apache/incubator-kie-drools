package org.jbpm.process.workitem.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/test")
public class TestRESTResource {

    @GET
    @Produces("text/plain")
    public String get(@QueryParam("param")String param) {
        
        return "Hello from REST" + (param!=null?" " + param:""); 
    }
    
    @POST()
    @Path("/{name}")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String post(@PathParam("name") String name) {
        
        return "Created resource with name " + name;
    }
    
    @POST()
    @Path("/xml")
    @Consumes("application/xml")
    @Produces("application/xml")
    public Person postJSON(Person person) {
        person.setName("Updated " + person.getName());
        return person;
    }

}
