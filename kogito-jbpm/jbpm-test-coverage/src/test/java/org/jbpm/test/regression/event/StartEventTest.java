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

package org.jbpm.test.regression.event;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.iodata.SignalObjectReport;
import org.junit.Test;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import qa.tools.ikeeper.annotation.BZ;

public class StartEventTest extends JbpmTestCase {

    private static final String ERROR_EXCEPTION_HANDLER =
            "org/jbpm/test/regression/event/StartEvent-errorExceptionHandler.bpmn2";
    private static final String ERROR_EXCEPTION_HANDLER_ID =
            "org.jbpm.test.regression.event.StartEvent-errorExceptionHandler";

    private static final String ERROR_EXCEPTION_MAPPING =
            "org/jbpm/test/regression/event/StartEvent-errorExceptionMapping.bpmn2";
    private static final String ERROR_EXCEPTION_MAPPING_ID =
            "org.jbpm.test.regression.event.StartEvent-errorExceptionMapping";

    private static final String SIGNAL_DATA_MAPPING =
            "org/jbpm/test/regression/event/StartEvent-signalDataMapping.bpmn2";

    private static final String SIGNAL_OUTPUT_TYPE =
            "org/jbpm/test/regression/event/StartEvent-signalOutputType.bpmn2";

    @Test
    @BZ("1186015")
    public void testErrorStartEventDefaultExceptionHandler() {
        KieSession ksession = createKSession(ERROR_EXCEPTION_HANDLER);
        ProcessInstance pi = ksession.startProcess(ERROR_EXCEPTION_HANDLER_ID);

        List<? extends VariableInstanceLog> variables = getLogService().findVariableInstances(pi.getId(),
                "capturedException");
        // TODO: size should probably be just 1 (2 records caused by JBPM-4669 fix)
        Assertions.assertThat(variables).hasSize(2);
        Assertions.assertThat(variables.get(0).getValue()).isEqualTo("java.lang.RuntimeException: XXX");
    }

    @Test
    @BZ("1186016")
    public void testErrorStartEventDataOutputMapping() {
        KieSession ksession = createKSession(ERROR_EXCEPTION_MAPPING);
        ProcessInstance pi = ksession.startProcess(ERROR_EXCEPTION_MAPPING_ID);

        List<? extends VariableInstanceLog> variables = getLogService().findVariableInstances(pi.getId(),
                "capturedException");
        // TODO: size should probably be just 1 (2 records caused by JBPM-4669 fix)
        Assertions.assertThat(variables).hasSize(2);
        Assertions.assertThat(variables.get(0).getValue()).isEqualTo("java.lang.RuntimeException: XXX");
    }

    @Test
    @BZ("1154557")
    public void testSignalStartEventDataMapping() throws Exception {
        KieSession ksession = createKSession(SIGNAL_DATA_MAPPING);
        final List<Long> list = new ArrayList<Long>();
        final List<String> variableList = new ArrayList<String>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
                variableList.add((String) ((WorkflowProcessInstance) event.getProcessInstance()).getVariable("x"));
            }
        });
        ksession.signalEvent("MyStartSignal", "NewValue");
        Assertions.assertThat(list).hasSize(1);
        Assertions.assertThat(variableList.get(0)).isEqualTo("NewValue");
    }

    @Test
    @BZ("1090375")
    public void testSignalOutputType() throws Exception {
        KieSession ksession = createKSession(SIGNAL_OUTPUT_TYPE);
        SignalObjectReport report = new SignalObjectReport("Type of signal object report");
        ksession.signalEvent("SignalObjectReport", report);
        List<? extends VariableInstanceLog> vars = getLogService().findVariableInstancesByName("report", false);
        VariableInstanceLog lastvar = vars.get(vars.size() - 1);
        Assertions.assertThat(lastvar.getValue()).isEqualTo(report.toString());
    }

}
