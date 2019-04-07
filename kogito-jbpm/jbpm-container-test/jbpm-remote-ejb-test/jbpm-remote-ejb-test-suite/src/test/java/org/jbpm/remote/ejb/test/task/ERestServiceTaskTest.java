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

package org.jbpm.remote.ejb.test.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.remote.ejb.test.mock.RestService;
import org.jbpm.services.api.model.VariableDesc;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ERestServiceTaskTest extends RemoteEjbTest {

    @BeforeClass
    public static void startRestService() {
        RestService.start();
    }

    @AfterClass
    public static void stopRestService() {
        RestService.stop();
    }

    @Test
    public void testRestWorkItem() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("url", RestService.PING_URL);
        parameters.put("method", "GET");

        long pid = ejb.startProcess(ProcessDefinitions.REST_WORK_ITEM, parameters);

        List<VariableDesc> resultVarHistory = ejb.getVariableHistory(pid, "result");
        List<VariableDesc> statusVarHistory = ejb.getVariableHistory(pid, "status");
        List<VariableDesc> statusMsgVarHistory = ejb.getVariableHistory(pid, "statusMsg");

        Assertions.assertThat(resultVarHistory).isNotNull().isNotEmpty();
        Assertions.assertThat(statusVarHistory).isNotNull().isNotEmpty();
        Assertions.assertThat(statusMsgVarHistory).isNotNull().isNotEmpty();

        String result = ejb.getVariableHistory(pid, "result").get(0).getNewValue();
        Integer status = Integer.parseInt(ejb.getVariableHistory(pid, "status").get(0).getNewValue());
        String statusMsg = ejb.getVariableHistory(pid, "statusMsg").get(0).getNewValue();

        logger.info(status + " " + statusMsg);
        logger.info(result);

        Assertions.assertThat(status).isEqualTo(200);
        Assertions.assertThat(result.contains("pong")).isTrue();
    }

    @Test
    public void testRestWorkItemStatus() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("url", RestService.STATUS_URL + "/201");
        parameters.put("method", "GET");

        long pid = ejb.startProcess(ProcessDefinitions.REST_WORK_ITEM, parameters);

        List<VariableDesc> resultVarHistory = ejb.getVariableHistory(pid, "result");
        List<VariableDesc> statusVarHistory = ejb.getVariableHistory(pid, "status");
        List<VariableDesc> statusMsgVarHistory = ejb.getVariableHistory(pid, "statusMsg");

        Assertions.assertThat(resultVarHistory).isNotNull().isNotEmpty();
        Assertions.assertThat(statusVarHistory).isNotNull().isNotEmpty();
        Assertions.assertThat(statusMsgVarHistory).isNotNull().isNotEmpty();

        String result = ejb.getVariableHistory(pid, "result").get(0).getNewValue();
        Integer status = Integer.parseInt(ejb.getVariableHistory(pid, "status").get(0).getNewValue());
        String statusMsg = ejb.getVariableHistory(pid, "statusMsg").get(0).getNewValue();

        logger.info(status + " " + statusMsg);
        logger.info(result);

        Assertions.assertThat(status).isEqualTo(201);
        Assertions.assertThat(result != null ? result.trim() : result).isNullOrEmpty();
    }

    @Test
    public void testRestWorkItemContent() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("url", RestService.ECHO_URL);
        parameters.put("content", "Echo message");
        parameters.put("contentType", "text/plain");
        parameters.put("method", "POST");

        long pid = ejb.startProcess(ProcessDefinitions.REST_WORK_ITEM, parameters);

        List<VariableDesc> resultVarHistory = ejb.getVariableHistory(pid, "result");
        List<VariableDesc> statusVarHistory = ejb.getVariableHistory(pid, "status");
        List<VariableDesc> statusMsgVarHistory = ejb.getVariableHistory(pid, "statusMsg");

        Assertions.assertThat(resultVarHistory).isNotNull().isNotEmpty();
        Assertions.assertThat(statusVarHistory).isNotNull().isNotEmpty();
        Assertions.assertThat(statusMsgVarHistory).isNotNull().isNotEmpty();

        String result = ejb.getVariableHistory(pid, "result").get(0).getNewValue();
        Integer status = Integer.parseInt(ejb.getVariableHistory(pid, "status").get(0).getNewValue());
        String statusMsg = ejb.getVariableHistory(pid, "statusMsg").get(0).getNewValue();

        logger.info(status + " " + statusMsg);
        logger.info(result);

        Assertions.assertThat(status).isEqualTo(200);
        Assertions.assertThat(result.contains("Echo message")).isTrue();
    }
}
