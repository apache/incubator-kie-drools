/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.persistence.inmemory.postgresql.it;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;

@Path("inmemory-postgresql")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InmemoryPostgreSQLResource {

    private final PgPool client;

    public InmemoryPostgreSQLResource(PgPool client) {
        this.client = client;
    }

    @GET
    public Multi<InmemoryPostgreSQL> get() {
        return InmemoryPostgreSQL.findAll(client);
    }

    @GET
    @Path("{id}")
    public Uni<Response> getSingle(Long id) {
        return InmemoryPostgreSQL.findById(client, id)
                .onItem().transform(inmemoryPostgreSQL -> inmemoryPostgreSQL != null ? Response.ok(inmemoryPostgreSQL) : Response.status(Status.NOT_FOUND))
                .onItem().transform(ResponseBuilder::build);
    }

    @POST
    public Uni<Response> create(InmemoryPostgreSQL inmemoryPostgreSQL) {
        return inmemoryPostgreSQL.save(client)
                .onItem().transform(id -> URI.create("/inmemory_postgresql/" + id))
                .onItem().transform(uri -> Response.created(uri).build());
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Long id, InmemoryPostgreSQL inmemoryPostgreSQL) {
        return inmemoryPostgreSQL.update(client)
                .onItem().transform(updated -> updated ? Status.OK : Status.NOT_FOUND)
                .onItem().transform(status -> Response.status(status).build());
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Long id) {
        return InmemoryPostgreSQL.delete(client, id)
                .onItem().transform(deleted -> deleted ? Status.NO_CONTENT : Status.NOT_FOUND)
                .onItem().transform(status -> Response.status(status).build());
    }
}
