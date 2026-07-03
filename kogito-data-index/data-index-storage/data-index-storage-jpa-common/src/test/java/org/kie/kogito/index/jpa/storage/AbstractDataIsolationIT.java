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
package org.kie.kogito.index.jpa.storage;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.index.jpa.model.*;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Abstract integration test for data isolation filtering.
 *
 * Concrete implementations must handle database cleanup to ensure test isolation:
 * - Quarkus: Use @TestTransaction at class level + @Transactional @BeforeEach cleanup
 * - Spring Boot: Use @Transactional + @DirtiesContext at class level
 *
 * The cleanup must run in a separate transaction before each test to remove data
 * from other test classes that may have run in the same test suite.
 */
public abstract class AbstractDataIsolationIT {

    EntityManager entityManager;

    JPADataIndexStorageService storageService;

    public AbstractDataIsolationIT(EntityManager entityManager, JPADataIndexStorageService storageService) {
        this.entityManager = entityManager;
        this.storageService = storageService;
    }

    /**
     * Hook for subclasses to wrap operations in transactions.
     * Quarkus: No-op (uses @TestTransaction)
     * Spring Boot: Wraps in TransactionTemplate
     */
    protected void executeInTransaction(Runnable operation) {
        operation.run();
    }

    /**
     * Test that ProcessInstanceEntity filtering works with direct processId field
     */
    @Test
    @Transactional
    public void testProcessInstanceDataIsolation() {
        // Insert process instances directly into database
        ProcessInstanceEntity travel = createProcessInstance("travel", "1.0", UUID.randomUUID().toString());
        ProcessInstanceEntity orders = createProcessInstance("orders", "2.0", UUID.randomUUID().toString());
        ProcessInstanceEntity orders2 = createProcessInstance("orders", "2.1", UUID.randomUUID().toString());
        ProcessInstanceEntity hiring = createProcessInstance("hiring", "3.0", UUID.randomUUID().toString());

        executeInTransaction(() -> {
            entityManager.persist(travel);
            entityManager.persist(orders);
            entityManager.persist(orders2);
            entityManager.persist(hiring);
            entityManager.flush();
        });

        // Query with data isolation for travel and orders only
        Processes processes = createProcesses(Set.of(
                DataIsolationKeyDescriptor.builder().processId("travel").processVersion("1.0").build(),
                DataIsolationKeyDescriptor.builder().processId("orders").processVersion("2.0").build()));
        JPAQuery<ProcessInstanceEntity, ProcessInstanceEntity> query = new JPAQuery<>(
                entityManager,
                Function.identity(),
                ProcessInstanceEntity.class,
                Optional.empty(),
                Optional.of(processes));

        List<ProcessInstanceEntity> results = query.execute();

        // Should only return travel and orders, not hiring
        assertThat(results).hasSize(2);
        assertThat(results).extracting("processId")
                .containsExactlyInAnyOrder("travel", "orders");
        assertThat(results).filteredOn(entity -> entity.getProcessId().equals("orders") && entity.getVersion().equals("2.1")).isEmpty();
        assertThat(results).filteredOn(entity -> entity.getProcessId().equals("orders") && entity.getVersion().equals("2.0")).hasSize(1);
    }

    /**
     * Test that querying without Processes returns all entities (no filtering)
     */
    @Test
    @Transactional
    public void testNoDataIsolationWhenProcessesNotProvided() {
        // Insert process instances directly into database
        ProcessInstanceEntity travel = createProcessInstance("travel", "1.0", UUID.randomUUID().toString());
        ProcessInstanceEntity orders = createProcessInstance("orders", "2.0", UUID.randomUUID().toString());
        ProcessInstanceEntity orders2 = createProcessInstance("orders", "2.1", UUID.randomUUID().toString());
        ProcessInstanceEntity hiring = createProcessInstance("hiring", "3.0", UUID.randomUUID().toString());

        executeInTransaction(() -> {
            entityManager.persist(travel);
            entityManager.persist(orders);
            entityManager.persist(orders2);
            entityManager.persist(hiring);
            entityManager.flush();
        });

        // Query without Processes (no data isolation)
        JPAQuery<ProcessInstanceEntity, ProcessInstanceEntity> query = new JPAQuery<>(
                entityManager,
                Function.identity(),
                ProcessInstanceEntity.class,
                Optional.empty(),
                Optional.empty());

        List<ProcessInstanceEntity> results = query.execute();

        // Should return all three test instances
        assertThat(results).hasSize(4);
        assertThat(results).extracting("processId")
                .containsExactlyInAnyOrder("travel", "orders", "orders", "hiring");

        assertThat(results).filteredOn(entity -> entity.getProcessId().equals("orders") && entity.getVersion().equals("2.1")).hasSize(1);
        assertThat(results).filteredOn(entity -> entity.getProcessId().equals("orders") && entity.getVersion().equals("2.0")).hasSize(1);
    }

    /**
     * Test that empty processIds set returns no results
     */
    @Test
    @Transactional
    public void testEmptyProcessIdsReturnsNothing() {
        // Query with empty process IDs - should return nothing regardless of existing data
        Processes processes = createProcesses(Set.of());
        JPAQuery<ProcessInstanceEntity, ProcessInstanceEntity> query = new JPAQuery<>(
                entityManager,
                Function.identity(),
                ProcessInstanceEntity.class,
                Optional.empty(),
                Optional.of(processes));

        List<ProcessInstanceEntity> results = query.execute();

        // Should return nothing
        assertThat(results).isEmpty();
    }

    /**
     * Test UserTaskInstanceEntity filtering (direct processId field)
     */
    @Test
    @Transactional
    public void testUserTaskInstanceDataIsolation() {
        // Insert user tasks directly into database
        UserTaskInstanceEntity travelTask = createUserTask("travel", "1.0", UUID.randomUUID().toString());
        UserTaskInstanceEntity orderTask = createUserTask("orders", "2.0", UUID.randomUUID().toString());
        UserTaskInstanceEntity order2Task = createUserTask("orders", "2.1", UUID.randomUUID().toString());
        UserTaskInstanceEntity orderNullTask = createUserTask("orders", null, UUID.randomUUID().toString());
        UserTaskInstanceEntity hiringTask = createUserTask("hiring", "3.0", UUID.randomUUID().toString());

        executeInTransaction(() -> {
            entityManager.persist(travelTask);
            entityManager.persist(orderTask);
            entityManager.persist(order2Task);
            entityManager.persist(orderNullTask);
            entityManager.persist(hiringTask);
            entityManager.flush();
        });

        // Query with data isolation
        Processes processes = createProcesses(Set.of(
                DataIsolationKeyDescriptor.builder().processId("travel").processVersion("1.0").build(),
                DataIsolationKeyDescriptor.builder().processId("orders").processVersion("2.0").build()));
        JPAQuery<UserTaskInstanceEntity, UserTaskInstanceEntity> query = new JPAQuery<>(
                entityManager,
                Function.identity(),
                UserTaskInstanceEntity.class,
                Optional.empty(),
                Optional.of(processes));

        List<UserTaskInstanceEntity> results = query.execute();

        assertThat(results).hasSize(3);
        assertThat(results).extracting("processId")
                .containsExactlyInAnyOrder("travel", "orders", "orders");
        assertThat(results).filteredOn(entity -> entity.getProcessVersion() != null).filteredOn(entity -> entity.getProcessId().equals("orders") && entity.getProcessVersion().equals("2.1")).isEmpty();
        assertThat(results).filteredOn(entity -> entity.getProcessVersion() != null).filteredOn(entity -> entity.getProcessId().equals("orders") && entity.getProcessVersion().equals("2.0"))
                .hasSize(1);
        assertThat(results).filteredOn(entity -> entity.getProcessId().equals("orders") && entity.getProcessVersion() == null).hasSize(1);
    }

    /**
     * Test JobEntity filtering (direct processId field)
     */
    @Test
    @Transactional
    public void testJobDataIsolation() {
        // Insert jobs directly into database
        JobEntity travelJob = createJob("travel", "1.0", UUID.randomUUID().toString());
        JobEntity orderJob = createJob("orders", "2.0", UUID.randomUUID().toString());
        JobEntity order2Job = createJob("orders", "2.1", UUID.randomUUID().toString());
        JobEntity orderNullJob = createJob("orders", null, UUID.randomUUID().toString());
        JobEntity hiringJob = createJob("hiring", "3.0", UUID.randomUUID().toString());

        executeInTransaction(() -> {
            entityManager.persist(travelJob);
            entityManager.persist(orderJob);
            entityManager.persist(order2Job);
            entityManager.persist(orderNullJob);
            entityManager.persist(hiringJob);
            entityManager.flush();
        });

        // Query with data isolation
        Processes processes = createProcesses(Set.of(
                DataIsolationKeyDescriptor.builder().processId("travel").processVersion("1.0").build(),
                DataIsolationKeyDescriptor.builder().processId("orders").processVersion("2.0").build()));
        JPAQuery<JobEntity, JobEntity> query = new JPAQuery<>(
                entityManager,
                Function.identity(),
                JobEntity.class,
                Optional.empty(),
                Optional.of(processes));

        List<JobEntity> results = query.execute();

        assertThat(results).hasSize(3);
        assertThat(results).extracting("processId")
                .containsExactlyInAnyOrder("travel", "orders", "orders");
        assertThat(results).filteredOn(entity -> entity.getProcessVersion() != null).filteredOn(entity -> entity.getProcessId().equals("orders") && entity.getProcessVersion().equals("2.1")).isEmpty();
        assertThat(results).filteredOn(entity -> entity.getProcessVersion() != null).filteredOn(entity -> entity.getProcessId().equals("orders") && entity.getProcessVersion().equals("2.0"))
                .hasSize(1);
        assertThat(results).filteredOn(entity -> entity.getProcessId().equals("orders") && entity.getProcessVersion() == null).hasSize(1);
    }

    /**
     * Test NodeInstanceEntity filtering (relationship-based: processInstance.processId)
     */
    @Test
    @Transactional
    public void testNodeInstanceDataIsolation() {
        // Insert process instances first
        ProcessInstanceEntity travelPI = createProcessInstance("travel", "1.0", UUID.randomUUID().toString());
        ProcessInstanceEntity orderPI = createProcessInstance("orders", "2.0", UUID.randomUUID().toString());
        ProcessInstanceEntity order2PI = createProcessInstance("orders", "2.1", UUID.randomUUID().toString());
        ProcessInstanceEntity hiringPI = createProcessInstance("hiring", "3.0", UUID.randomUUID().toString());

        executeInTransaction(() -> {
            entityManager.persist(travelPI);
            entityManager.persist(orderPI);
            entityManager.persist(order2PI);
            entityManager.persist(hiringPI);
            entityManager.flush();

            // Insert node instances with relationships to process instances
            NodeInstanceEntity travelNode = createNodeInstance(travelPI);
            NodeInstanceEntity orderNode = createNodeInstance(orderPI);
            NodeInstanceEntity order2Node = createNodeInstance(order2PI);
            NodeInstanceEntity hiringNode = createNodeInstance(hiringPI);

            entityManager.persist(travelNode);
            entityManager.persist(orderNode);
            entityManager.persist(order2Node);
            entityManager.persist(hiringNode);
            entityManager.flush();
        });

        // Query with data isolation - NodeInstance navigates via processInstance.processId
        Processes processes = createProcesses(Set.of(
                DataIsolationKeyDescriptor.builder().processId("travel").processVersion("1.0").build(),
                DataIsolationKeyDescriptor.builder().processId("orders").processVersion("2.0").build()));
        JPAQuery<NodeInstanceEntity, NodeInstanceEntity> query = new JPAQuery<>(
                entityManager,
                Function.identity(),
                NodeInstanceEntity.class,
                Optional.empty(),
                Optional.of(processes));

        List<NodeInstanceEntity> results = query.execute();
        assertThat(results).hasSize(2);
        // NodeInstance doesn't have direct processId, verify via processInstance relationship
        assertThat(results).allSatisfy(node -> assertThat(node.getProcessInstance().getProcessId()).isIn("travel", "orders"));
    }

    // Helper methods to create test entities

    private ProcessInstanceEntity createProcessInstance(String processId, String version, String id) {
        ProcessInstanceEntity entity = new ProcessInstanceEntity();
        entity.setId(id);
        entity.setProcessId(processId);
        entity.setVersion(version);
        entity.setState(ProcessInstanceState.ACTIVE.ordinal());
        entity.setStart(ZonedDateTime.now());
        return entity;
    }

    private UserTaskInstanceEntity createUserTask(String processId, String version, String processInstanceId) {
        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setProcessId(processId);
        entity.setProcessVersion(version);
        entity.setProcessInstanceId(processInstanceId);
        entity.setState("Ready");
        entity.setStarted(ZonedDateTime.now());
        return entity;
    }

    private JobEntity createJob(String processId, String version, String processInstanceId) {
        JobEntity entity = new JobEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setProcessId(processId);
        entity.setProcessVersion(version);
        entity.setProcessInstanceId(processInstanceId);
        entity.setStatus("SCHEDULED");
        entity.setExpirationTime(ZonedDateTime.now().plusHours(1));
        return entity;
    }

    private NodeInstanceEntity createNodeInstance(ProcessInstanceEntity processInstance) {
        NodeInstanceEntity entity = new NodeInstanceEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setProcessInstance(processInstance);
        entity.setNodeId(UUID.randomUUID().toString());
        entity.setDefinitionId(UUID.randomUUID().toString());
        entity.setName("TestNode");
        entity.setType("StartNode");
        entity.setEnter(ZonedDateTime.now());
        return entity;
    }

    private Processes createProcesses(Set<DataIsolationKeyDescriptor> descriptors) {
        return new Processes() {

            @Override
            public Collection<String> processIds() {
                return descriptors.stream().map(DataIsolationKeyDescriptor::processId).collect(Collectors.toSet());
            }

            @Override
            public Process<? extends Model> processById(String processId) {
                Process<? extends Model> process = mock(Process.class);
                when(process.id()).thenReturn(processId);
                when(process.version()).thenReturn("1.0");
                return process;
            }

            @Override
            public Collection<Process<? extends Model>> processes() {
                return descriptors.stream().map(it -> {
                    Process<? extends Model> p = mock(Process.class);
                    when(p.id()).thenReturn(it.processId());
                    when(p.version()).thenReturn(it.processVersion());
                    return (Process<? extends Model>) p;
                }).collect(Collectors.toSet());
            }
        };
    }
}
