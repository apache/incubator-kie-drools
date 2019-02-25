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

package org.jbpm.xes;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.apache.commons.dbcp2.BasicDataSource;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.jbpm.xes.dataset.DataSetService;
import org.jbpm.xes.dataset.DataSetServiceImpl;
import org.jbpm.xes.model.LogType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class XESProcessExportTest extends JbpmJUnitBaseTestCase {

    private BasicDataSource xesDataSource;

    public XESProcessExportTest() {
        super(true, true);
    }

    public static BasicDataSource setupDataSource(String connectURI) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl(connectURI);
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    @Before
    public void setup() {
        xesDataSource = setupDataSource("jdbc:h2:mem:jbpm-db;MVCC=true");
    }

    @After
    public void cleanup() throws Exception {
        if (xesDataSource != null) {
            xesDataSource.close();
        }
    }

    @Test
    public void testHelloProcess() throws Exception {
        // create runtime manager with single process - hello.bpmn
        createRuntimeManager("hello.bpmn");

        // take RuntimeManager to work with process engine
        RuntimeEngine runtimeEngine = getRuntimeEngine();

        // get access to KieSession instance
        KieSession ksession = runtimeEngine.getKieSession();

        int instances = 5;
        IntStream.range(0, instances).forEach(i -> {
            // start process
            ProcessInstance processInstance = ksession.startProcess("hello");

            // check whether the process instance has completed successfully
            assertProcessInstanceCompleted(processInstance.getId(), ksession);

            // check what nodes have been triggered
            assertNodeTriggered(processInstance.getId(), "Start", "Hello", "End");
        });

        DataSetService dataSetService = new DataSetServiceImpl(() -> xesDataSource);
        XESExportServiceImpl service = new XESExportServiceImpl();
        service.setDataSetService(dataSetService);
        final String xml = service.export(XESProcessFilter.builder().withProcessId("hello").withAllNodeTypes().build());
        final XESLogMarshaller marshaller = new XESLogMarshaller();

        LogType log = marshaller.unmarshall(xml);
        assertNotNull(log);
        assertEquals(4, log.getExtension().size());
        assertEquals(3, log.getStringOrDateOrInt().size());
        assertEquals(2, log.getGlobal().size());
        assertEquals(4, log.getClassifier().size());
        assertEquals(instances, log.getTrace().size());
        IntStream.range(0, instances).forEach(i -> assertEquals(6, log.getTrace().get(i).getEvent().size()));
    }

    @Test
    public void testHelloProcessWithFilters() throws Exception {
        // create runtime manager with single process - hello.bpmn
        createRuntimeManager("hello.bpmn");

        // take RuntimeManager to work with process engine
        RuntimeEngine runtimeEngine = getRuntimeEngine();

        // get access to KieSession instance
        KieSession ksession = runtimeEngine.getKieSession();

        int instances = 5;
        IntStream.range(0, instances).forEach(i -> {
            // start process
            ProcessInstance processInstance = ksession.startProcess("hello");

            // check whether the process instance has completed successfully
            assertProcessInstanceCompleted(processInstance.getId(), ksession);

            // check what nodes have been triggered
            assertNodeTriggered(processInstance.getId(), "Start", "Hello", "End");
        });

        DataSetService dataSetService = new DataSetServiceImpl(() -> xesDataSource);
        XESExportServiceImpl service = new XESExportServiceImpl();
        service.setDataSetService(dataSetService);
        final XESProcessFilter filter = XESProcessFilter.builder().withProcessId("hello").withProcessVersion("1.0").withStatus(Arrays.asList(ProcessInstance.STATE_COMPLETED)).withNodeInstanceLogType(NodeInstanceLog.TYPE_EXIT).build();
        final String xml = service.export(filter);
        final XESLogMarshaller marshaller = new XESLogMarshaller();

        LogType log = marshaller.unmarshall(xml);
        assertNotNull(log);
        assertEquals(4, log.getExtension().size());
        assertEquals(3, log.getStringOrDateOrInt().size());
        assertEquals(2, log.getGlobal().size());
        assertEquals(4, log.getClassifier().size());
        assertEquals(instances, log.getTrace().size());
        IntStream.range(0, instances).forEach(i -> assertEquals(1, log.getTrace().get(i).getEvent().size()));
    }
}
