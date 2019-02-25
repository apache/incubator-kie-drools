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

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.jbpm.xes.dataset.DataSetService;
import org.jbpm.xes.dataset.DataSetServiceImpl;
import org.junit.*;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

public class EvaluationExport extends JbpmJUnitBaseTestCase {

    private BasicDataSource xesDataSource;

    public EvaluationExport() {
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
    @Ignore
    public void exportProcess() throws Exception {
        //users
        final String administrator = "Administrator";

        // create runtime manager with single process - hello.bpmn
        createRuntimeManager("evaluation.bpmn");

        // take RuntimeManager to work with process engine
        RuntimeEngine runtimeEngine = getRuntimeEngine();

        // get access to KieSession instance
        KieSession ksession = runtimeEngine.getKieSession();

        List<Long> pIds = new ArrayList<>();

        int instances = 100;

        IntStream.range(0, instances).forEach(i -> {
            Map<String, Object> vars = new HashMap<>();
            vars.put("employee", administrator);
            vars.put("reason", "test instance " + i);
            vars.put("performance", RandomUtils.nextInt(0, 11));

            // start process
            ProcessInstance processInstance = ksession.startProcess("evaluation", vars);

            // check whether the process instance has completed successfully
            assertProcessInstanceActive(processInstance.getId(), ksession);

            pIds.add(processInstance.getId());
        });

        final TaskService taskService = getRuntimeEngine().getTaskService();
        final List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministrator(administrator, null);
        final ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch count = new CountDownLatch(instances * 3);
        tasks.forEach(t -> {
            executorService.submit(() -> {
                taskService.start(t.getId(), administrator);
                try {
                    Thread.sleep(2 * 1000);
                } catch (Exception ex) {
                }
                taskService.complete(t.getId(), administrator, null);
                count.countDown();
                taskService.getTasksByProcessInstanceId(t.getProcessInstanceId()).stream().filter(newTaskId -> newTaskId.equals(t.getId()) == false).forEach(taskId -> {
                    executorService.submit(() -> {
                        final Task task = taskService.getTaskById(taskId);
                        final String userId = "HR Evaluation".equals(task.getName()) ? "mary" : "john";
                        taskService.claim(taskId, userId);
                        taskService.start(taskId, userId);
                        if ("HR Evaluation".equals(task.getName())) {
                            try {
                                Thread.sleep(4 * 1000);
                            } catch (Exception ex) {
                            }
                        } else {
                            try {
                                Thread.sleep(2 * 1000);
                            } catch (Exception ex) {
                            }
                        }
                        taskService.complete(taskId, userId, null);

                        count.countDown();
                    });
                });
            });
        });

        count.await();
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        pIds.forEach(id -> assertProcessInstanceCompleted(id));

        DataSetService dataSetService = new DataSetServiceImpl(() -> xesDataSource);
        XESExportServiceImpl service = new XESExportServiceImpl();
        service.setDataSetService(dataSetService);
        final String xml = service.export(XESProcessFilter.builder().withProcessId("evaluation").build());

        FileUtils.write(new File("evaluation.xes"), xml);
    }
}
