/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.workitem.rest;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/test")
public class TestRESTResource {

    @GET
    @Produces("text/plain")
    public String get(@QueryParam("param") String param) {

        return "Hello from REST" + (param != null ? " " + param : "");
    }

    @GET
    @Path("/charset")
    @Produces("text/plain")
    public Response getPlainText(@HeaderParam("Accept-Charset") String acceptCharset) {
        String entity = "Š"; // utf string
        Charset charset = Charset.forName(acceptCharset);

        Response myResponse = Response.status(Status.OK)
                .entity(entity.getBytes(charset))
                .build();
        return myResponse;
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

    @PUT
    @Path("/xml-charset")
    @Consumes("application/xml")
    @Produces("application/xml; charset=UTF-8")
    public Person putXmlWithCharset(Person person) {
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

    @GET
    @Path("/xml")
    @Produces("application/xml")
    public Person getXml() {
        Person person = new Person();
        person.setName("Person Xml");
        return person;
    }

    @GET
    @Path("/json")
    @Produces("application/json")
    public String getJson() {

        return "{\"name\":\"Person Json\"}";
    }

    @GET
    @Path("/xml-charset")
    @Produces("application/xml; charset=utf-8")
    public Person getXmlWithCharset() {
        Person person = new Person();
        person.setName("Person Xml");
        return person;
    }

    @GET
    @Path("/json-charset")
    @Produces("application/json; charset=utf-8")
    public String getJsonWithCharset() {

        return "{\"name\":\"Person Json\"}";
    }
    
    @GET
    @Path("/header/{headerKey}")
    @Produces("text/plain")
    public String getHeaderValue(@PathParam("headerKey") String headerKey, 
    		@Context HttpHeaders headers) {
    	return headers.getRequestHeaders().getOrDefault(headerKey, Collections.emptyList())
    			.stream().collect(Collectors.joining(","));
    }
}