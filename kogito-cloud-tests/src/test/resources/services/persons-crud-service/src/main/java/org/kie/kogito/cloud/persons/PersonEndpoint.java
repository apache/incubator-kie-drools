/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.cloud.persons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/persons")
public class PersonEndpoint {

    private List<Person> persons = new ArrayList<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("")
    public Map<String, List<Person>> getAll() {
        Map<String, List<Person>> map = new HashMap<>();
        map.put("persons", persons);
        return map;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("")
    public Person createPerson(Person person) {
        person.setId(UUID.randomUUID().toString());
        persons.add(person);
        return person;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("")
    public Person updatePerson(Person person) {
        findPerson(person.getId()).ifPresent(persons::remove);
        persons.add(person);
        return person;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("")
    public Person deletePerson(Person person) {
        findPerson(person.getId()).ifPresent(persons::remove);

        // Return person with no id, meaning it is unregistered  
        person.setId(null);
        return person;
    }

    private Optional<Person> findPerson(String id) {
        return persons.stream().filter(p -> p.getId().equals(id)).findFirst();
    }
}
