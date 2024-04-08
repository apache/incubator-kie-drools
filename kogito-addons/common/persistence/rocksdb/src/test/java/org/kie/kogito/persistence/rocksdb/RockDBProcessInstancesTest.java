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
package org.kie.kogito.persistence.rocksdb;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.drools.io.ClassPathResource;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RockDBProcessInstancesTest {

    private static Options options;

    private final static Logger logger = LoggerFactory.getLogger(RockDBProcessInstancesTest.class);

    @TempDir
    Path tempDir;
    private RocksDBProcessInstancesFactory factory;
    private BpmnProcess process;
    private MutableProcessInstances pi;

    @BeforeAll
    static void init() {
        options = new Options().setCreateIfMissing(true);
    }

    @BeforeEach
    void setup() throws RocksDBException {
        factory = new RocksDBProcessInstancesFactory(options, tempDir.toString());
        process = createProcess("BPMN2-UserTask.bpmn2");
        pi = factory.createProcessInstances(process);
    }

    @AfterEach
    void close() {
        factory.close();
    }

    @AfterAll
    static void cleanUp() {
        options.close();
    }

    private BpmnProcess createProcess(String fileName) {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource(fileName)).get(0);
        process.setProcessInstancesFactory(factory);
        process.configure();
        return process;
    }

    @Test
    void testBasic() {
        assertThat(pi).isNotNull();
        WorkflowProcessInstance createPi = createProcessInstance();
        try (Stream<ProcessInstance<?>> stream = pi.stream()) {
            assertThat(stream.count()).isOne();
        }
        removeProcessInstance(createPi);
        try (Stream<ProcessInstance<?>> stream = pi.stream()) {
            assertThat(stream.count()).isZero();
        }
    }

    @Test
    void testMultiThread() throws InterruptedException, ExecutionException {
        int numConcurrent = 10;
        ExecutorService service = Executors.newFixedThreadPool(numConcurrent);
        Collection<Future<WorkflowProcessInstance>> futures = new ArrayList<>();
        while (--numConcurrent > 0) {
            futures.add(service.submit(() -> createProcessInstance()));
        }
        Collection<WorkflowProcessInstance> instances = new ArrayList<>();
        for (Future<WorkflowProcessInstance> future : futures) {
            instances.add(future.get());
        }
        instances.forEach(instance -> service.submit(() -> removeProcessInstance(instance)));
        service.shutdown();
        assertThat(service.awaitTermination(2, TimeUnit.SECONDS)).isTrue();
        try (Stream<ProcessInstance<?>> stream = pi.stream()) {
            assertThat(stream.count()).isZero();
        }
    }

    WorkflowProcessInstance createProcessInstance() {
        WorkflowProcessInstance instance = ((AbstractProcessInstance<?>) process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")))).internalGetProcessInstance();
        logger.debug("Created instance {}", instance.getId());
        instance.setStartDate(new Date());
        pi.create(instance.getId(), mockProcessInstance(instance, process));
        assertThat(pi.exists(instance.getId())).isTrue();
        assertThat(pi.findById(instance.getId())).isNotEmpty();
        assertThat(pi.findById("non_existant")).isEmpty();
        return instance;

    }

    void removeProcessInstance(WorkflowProcessInstance instance) {
        pi.remove(instance.getId());
        logger.debug("Removed instance {}", instance.getId());
        assertThat(pi.exists(instance.getId())).isFalse();
        logger.debug("About to check instance {}", instance.getId());
        assertThat(pi.findById(instance.getId())).isNotPresent();
        logger.debug("Checked removed instance {}", instance.getId());
    }

    private static AbstractProcessInstance<?> mockProcessInstance(WorkflowProcessInstance pi, Process process) {
        AbstractProcessInstance<?> mockPi = mock(AbstractProcessInstance.class);
        mockPi.setVersion(1L);
        when(mockPi.status()).thenReturn(ProcessInstance.STATE_ACTIVE);
        when(mockPi.internalGetProcessInstance()).thenReturn(pi);
        when(mockPi.id()).thenReturn(pi.getId());
        when(mockPi.process()).thenReturn(process);
        return mockPi;
    }
}
