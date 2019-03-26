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

package org.jbpm.remote.ejb.test.mock;

import java.util.Arrays;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.jboss.qa.bpms.remote.ejb.domain.Person;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;

public class RestService {

    private static final String URL = "http://localhost";
    private static final int PORT = 5667;

    public static final String ECHO_URL = URL + ":" + PORT + "/echo";
    public static final String PING_URL = URL + ":" + PORT + "/ping";
    public static final String STATUS_URL = URL + ":" + PORT + "/status";
    public static final String PERSON_URL = URL + ":" + PORT + "/person";

    public static final Person PERSON = new Person("John Doe", 42);

    @Provider
    @Path("/")
    public static class Resource {

        @GET
        @Path("/ping")
        @Produces({"text/plain"})
        public String ping() {
            return "pong";
        }

        @POST
        @Path("/echo")
        @Consumes({"text/plain", "application/xml", "application/json"})
        @Produces({"text/plain", "application/xml", "application/json"})
        public String echo(String message) {
            return message;
        }

        @GET
        @Path("/status/{code}")
        public Response getStatus(@PathParam("code") int code) {
            if (code < 100 || code > 599) {
                code = 400;
            }
            return Response.status(code).build();
        }

        @GET
        @Path("/person")
        @Produces({"text/plain"})
        public Person person() {
            return PERSON;
        }

    }

    private static TJWSEmbeddedJaxrsServer server;

    private RestService() {
    }

    public static void start() {
        server = new TJWSEmbeddedJaxrsServer();
        server.setPort(PORT);
        server.getDeployment().setResources(Arrays.asList(new Resource()));
        server.start();
    }

    public static void stop() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

}
