/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.index.postgresql.reporting;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.postgresql.model.ProcessInstanceEntityRepository;
import org.kie.kogito.index.postgresql.storage.PostgreSqlStorageService;
import org.kie.kogito.index.test.TestUtils;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(PostgreSqlQuarkusTestResource.class)
class ProcessInstanceVariableMappingIT {

    private static final String CACHE_NAME = "processes";
    private static final Class<ProcessInstance> CACHE_TYPE = ProcessInstance.class;

    private static final String SQL = "SELECT " +
            "id, " +
            "firstName," +
            "lastName " +
            "FROM " +
            "ProcessInstanceVariableExtract";

    @Inject
    PostgreSqlStorageService storageService;

    @Inject
    ProcessInstanceEntityRepository repository;

    @BeforeEach
    @Transactional
    public void setup() {
        storageService.getCache(CACHE_NAME, CACHE_TYPE).clear();
    }

    @Test
    @Transactional
    void testProcessInstanceVariableMapping() {
        final Storage<String, ProcessInstance> cache = storageService.getCache(CACHE_NAME, CACHE_TYPE);

        //Insert 2 process instances for the same process
        final ProcessInstance pi0 = TestUtils.createProcessInstance("pi0",
                "process0",
                "rootProcessInstanceId",
                "rootProcessId",
                1,
                0,
                "Michael",
                "Anstis");
        final ProcessInstance pi1 = TestUtils.createProcessInstance("pi1",
                "process0",
                "rootProcessInstanceId",
                "rootProcessId",
                1,
                0,
                "Keith",
                "Flint");

        cache.put(pi0.getId(), pi0);
        cache.put(pi1.getId(), pi1);

        @SuppressWarnings("unchecked")
        final List<ProcessInstanceVariableExtract> results = repository
                .getEntityManager()
                .createNativeQuery(SQL, "ProcessInstanceVariableMappingMapping")
                .getResultList();

        assertThat(results).hasSize(2);
        final ProcessInstanceVariableExtract row0 = results.get(0);
        assertEquals(pi0.getId(), row0.id);
        assertEquals("Michael", row0.firstName);
        assertEquals("Anstis", row0.lastName);

        final ProcessInstanceVariableExtract row1 = results.get(1);
        assertEquals(pi1.getId(), row1.id);
        assertEquals("Keith", row1.firstName);
        assertEquals("Flint", row1.lastName);
    }

    @Test
    @Transactional
    void testProcessInstanceVariableMapping_Partitioned() {
        final Storage<String, ProcessInstance> cache = storageService.getCache(CACHE_NAME, CACHE_TYPE);

        //Insert 2 process instances, however only 1 relates process0 that has mappings defined
        final ProcessInstance pi0 = TestUtils.createProcessInstance("pi0",
                "process0",
                "rootProcessInstanceId",
                "rootProcessId",
                1,
                0,
                "Michael",
                "Anstis");
        final ProcessInstance pi1 = TestUtils.createProcessInstance("pi1",
                "process1",
                "rootProcessInstanceId",
                "rootProcessId",
                1,
                0,
                "Keith",
                "Flint");

        cache.put(pi0.getId(), pi0);
        cache.put(pi1.getId(), pi1);

        @SuppressWarnings("unchecked")
        final List<ProcessInstanceVariableExtract> results = repository
                .getEntityManager()
                .createNativeQuery(SQL, "ProcessInstanceVariableMappingMapping")
                .getResultList();

        //... Consequentially we only expect there to be one entry in the extracts table
        assertThat(results).hasSize(1);
        final ProcessInstanceVariableExtract row0 = results.get(0);
        assertEquals(pi0.getId(), row0.id);
        assertEquals("Michael", row0.firstName);
        assertEquals("Anstis", row0.lastName);
    }

    @Entity
    @SqlResultSetMapping(
            name = "ProcessInstanceVariableMappingMapping",
            entities = {
                    @EntityResult(
                            entityClass = ProcessInstanceVariableExtract.class,
                            fields = { @FieldResult(name = "id", column = "id"),
                                    @FieldResult(name = "firstName", column = "firstName"),
                                    @FieldResult(name = "lastName", column = "lastName") })
            })
    public static class ProcessInstanceVariableExtract {

        @Id
        @Column(nullable = false)
        private String id;

        @Column
        private String firstName;

        @Column
        private String lastName;
    }
}
