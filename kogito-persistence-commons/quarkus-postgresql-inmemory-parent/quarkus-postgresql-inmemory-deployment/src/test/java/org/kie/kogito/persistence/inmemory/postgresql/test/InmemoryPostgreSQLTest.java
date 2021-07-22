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

package org.kie.kogito.persistence.inmemory.postgresql.test;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;

import static java.util.stream.Collectors.toList;

public class InmemoryPostgreSQLTest {

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("db/migration/V1.0.0__Inmemory_PostgreSQL.sql"))
            .withConfigurationResource("application.properties");

    @Inject
    PgPool client;

    @Test
    public void testFlyway() {
        List<String> results = select(1);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("test1", results.get(0));
    }

    @Test
    public void testCRUD() {
        List<String> results;

        // Insert
        results = select(2);
        Assertions.assertEquals(0, results.size());
        insert(2, "test2");

        // Select
        results = select(2);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("test2", results.get(0));

        // Update
        update(2, "test2_update");
        results = select(2);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("test2_update", results.get(0));

        // Delete
        delete(2);
        results = select(2);
        Assertions.assertEquals(0, results.size());
    }

    private List<String> select(int id) {
        return client.preparedQuery("SELECT name FROM inmemory_postgresql WHERE id = $1").execute(Tuple.of(id))
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(row -> row.getString("name"))
                .collect().asList().await().atMost(Duration.ofSeconds(10));
    }

    private void insert(int id, String name) {
        client.preparedQuery("INSERT INTO inmemory_postgresql(id, name) VALUES ($1, $2)")
                .execute(Tuple.tuple(Stream.of(id, name).collect(toList()))).await().atMost(Duration.ofSeconds(10));
    }

    private void update(int id, String name) {
        client.preparedQuery("UPDATE inmemory_postgresql SET name = $2 WHERE id = $1")
                .execute(Tuple.tuple(Stream.of(id, name).collect(toList()))).await().atMost(Duration.ofSeconds(10));
    }

    private void delete(int id) {
        client.preparedQuery("DELETE FROM inmemory_postgresql WHERE id = $1")
                .execute(Tuple.tuple(Stream.of(id).collect(toList()))).await().atMost(Duration.ofSeconds(10));
    }
}
