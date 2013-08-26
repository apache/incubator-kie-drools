package org.jbpm.process.workitem.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
    
    @POST
    @Path("/{name}")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String post(@PathParam("name") String name) {
        
        return "Created resource with name " + name;
    }
    
    @POST
    @Path("/xml")
    @Consumes("application/xml")
    @Produces("application/xml")
    public Person postXml(Person person) {
        person.setName("Post " + person.getName());
        return person;
    }
    
    @PUT
    @Path("/xml")
    @Consumes("application/xml")
    @Produces("application/xml")
    public Person putXml(Person person) {
        person.setName("Put " + person.getName());
        return person;
    }
    
    @DELETE
    @Path("/xml/{name}")    
    @Produces("application/xml")
    public Person delete(@PathParam("name") String name) {
        Person person = new Person();
        person.setAge(-1);
        person.setName("deleted " + name);
        return person;
    }

}
