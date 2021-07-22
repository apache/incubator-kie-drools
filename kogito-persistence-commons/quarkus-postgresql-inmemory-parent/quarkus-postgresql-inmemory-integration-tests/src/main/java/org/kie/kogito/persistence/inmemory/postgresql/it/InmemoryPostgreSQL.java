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

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

public class InmemoryPostgreSQL {

    public Long id;

    public String name;

    public InmemoryPostgreSQL() {
        // default constructor.
    }

    public InmemoryPostgreSQL(String name) {
        this.name = name;
    }

    public InmemoryPostgreSQL(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Multi<InmemoryPostgreSQL> findAll(PgPool client) {
        return client.query("SELECT id, name FROM inmemory_postgresql ORDER BY name ASC").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(InmemoryPostgreSQL::from);
    }

    public static Uni<InmemoryPostgreSQL> findById(PgPool client, Long id) {
        return client.preparedQuery("SELECT id, name FROM inmemory_postgresql WHERE id = $1").execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    public Uni<Long> save(PgPool client) {
        return client.preparedQuery("INSERT INTO inmemory_postgresql (name) VALUES ($1) RETURNING id").execute(Tuple.of(name))
                .onItem().transform(pgRowSet -> pgRowSet.iterator().next().getLong("id"));
    }

    public Uni<Boolean> update(PgPool client) {
        return client.preparedQuery("UPDATE inmemory_postgresql SET name = $1 WHERE id = $2").execute(Tuple.of(name, id))
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> delete(PgPool client, Long id) {
        return client.preparedQuery("DELETE FROM inmemory_postgresql WHERE id = $1").execute(Tuple.of(id))
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    private static InmemoryPostgreSQL from(Row row) {
        return new InmemoryPostgreSQL(row.getLong("id"), row.getString("name"));
    }
}
