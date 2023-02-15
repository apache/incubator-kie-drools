/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.bpmn2;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.drools.core.impl.EnvironmentFactory;
import org.drools.kiesession.audit.LogEvent;
import org.drools.kiesession.audit.RuleFlowLogEvent;
import org.drools.kiesession.audit.RuleFlowNodeLogEvent;
import org.drools.mvel.MVELSafeHelper;
import org.jbpm.bpmn2.audit.KogitoWorkingMemoryInMemoryLogger;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.jbpm.process.instance.impl.actions.SignalProcessInstanceAction;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.Timeout;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.definition.process.Node;
import org.kie.api.io.Resource;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstanceContainer;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Base test case for the jbpm-bpmn2 module.
 */
@Timeout(value = 3000, unit = TimeUnit.SECONDS)
public abstract class JbpmBpmn2TestCase {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Used by many subclasses. Instead of each test duplicating the cleanup code, we extract it here in
     * the superclass.
     */
    protected KogitoProcessRuntime kruntime;

    protected KogitoWorkingMemoryInMemoryLogger workingMemoryLogger;

    @AfterEach
    public void disposeKogitoProcessRuntime() {
        if (kruntime != null && kruntime.getKieSession() != null) {
            kruntime.getKieSession().dispose();
            kruntime = null;
        }
    }

    @BeforeEach
    protected void logTestStartAndSetup(TestInfo testInfo) {
        logger.info(" >>> {} <<<", testInfo.getDisplayName());
        // this is to preserve the same behavior when executing over ksession
        System.setProperty("org.jbpm.signals.defaultscope", SignalProcessInstanceAction.DEFAULT_SCOPE);
    }

    @AfterEach
    protected void logTestEndAndSetup(TestInfo testInfo) {
        logger.info("Finished {}", testInfo.getDisplayName());
        System.clearProperty("org.jbpm.signals.defaultscope");
    }

    @AfterEach
    public void clear() {
        clearHistory();
    }

    protected KogitoProcessRuntime createKogitoProcessRuntime(String... process) throws Exception {
        return InternalProcessRuntime.asKogitoProcessRuntime(createKnowledgeSession(process));
    }

    protected KogitoProcessRuntime createKogitoProcessRuntime(Resource... process) throws Exception {
        return InternalProcessRuntime.asKogitoProcessRuntime(createKnowledgeSession(createKnowledgeBaseFromResources(process)));
    }

    private KieBase createKnowledgeBaseWithoutDumper(String... process) throws Exception {
        Resource[] resources = new Resource[process.length];
        for (int i = 0; i < process.length; ++i) {
            String p = process[i];
            resources[i] = (ResourceFactory.newClassPathResource(p));
        }
        return createKnowledgeBaseFromResources(resources);
    }

    private KieBase createKnowledgeBaseFromResources(Resource... process)
            throws Exception {

        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        if (process.length > 0) {
            KieFileSystem kfs = ks.newKieFileSystem();

            for (Resource p : process) {
                kfs.write(p);
            }

            KieBuilder kb = ks.newKieBuilder(kfs);

            kb.buildAll(); // kieModule is automatically deployed to KieRepository
                           // if successfully built.

            if (kb.getResults().hasMessages(Level.ERROR)) {
                throw new RuntimeException("Build Errors:\n"
                        + kb.getResults().toString());
            }
        }

        return ks.newKieContainer(kr.getDefaultReleaseId()).getKieBase();
    }

    private StatefulKnowledgeSession createKnowledgeSession(KieBase kbase)
            throws Exception {

        StatefulKnowledgeSession result;
        Environment env = EnvironmentFactory.newEnvironment();

        Properties defaultProps = new Properties();
        defaultProps.setProperty("drools.processSignalManagerFactory",
                DefaultSignalManagerFactory.class.getName());
        defaultProps.setProperty("drools.processInstanceManagerFactory",
                DefaultProcessInstanceManagerFactory.class.getName());
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration(defaultProps);
        conf.setOption(ForceEagerActivationOption.YES);
        result = (StatefulKnowledgeSession) kbase.newKieSession(conf, env);
        workingMemoryLogger = new KogitoWorkingMemoryInMemoryLogger(result);

        return result;
    }

    private StatefulKnowledgeSession createKnowledgeSession(String... process)
            throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper(process);
        return createKnowledgeSession(kbase);
    }

    public void assertProcessInstanceCompleted(KogitoProcessInstance processInstance) {
        assertThat(assertProcessInstanceState(KogitoProcessInstance.STATE_COMPLETED, processInstance)).as("Process instance has not been completed.").isTrue();
    }

    public void assertProcessInstanceAborted(KogitoProcessInstance processInstance) {
        assertThat(assertProcessInstanceState(KogitoProcessInstance.STATE_ABORTED, processInstance)).as("Process instance has not been aborted.").isTrue();
    }

    public void assertProcessInstanceActive(KogitoProcessInstance processInstance) {
        assertThat(assertProcessInstanceState(KogitoProcessInstance.STATE_ACTIVE, processInstance)
                || assertProcessInstanceState(KogitoProcessInstance.STATE_PENDING, processInstance)).as("Process instance is not active.").isTrue();
    }

    public void assertProcessInstanceFinished(KogitoProcessInstance processInstance,
            KogitoProcessRuntime kruntime) {
        assertThat(kruntime.getProcessInstance(processInstance.getStringId())).as("Process instance has not been finished.").isNull();
    }

    public void assertNodeActive(String processInstanceId, KogitoProcessRuntime kruntime,
            String... name) {
        List<String> names = new ArrayList<>();
        for (String n : name) {
            names.add(n);
        }
        KogitoProcessInstance processInstance = kruntime
                .getProcessInstance(processInstanceId);
        if (processInstance instanceof KogitoWorkflowProcessInstance) {
            assertNodeActive((KogitoWorkflowProcessInstance) processInstance, names);
        }
        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Node(s) not active: " + s);
        }
    }

    private void assertNodeActive(KogitoNodeInstanceContainer container,
            List<String> names) {
        for (KogitoNodeInstance nodeInstance : container.getKogitoNodeInstances()) {
            String nodeName = nodeInstance.getNodeName();
            if (names.contains(nodeName)) {
                names.remove(nodeName);
            }
            if (nodeInstance instanceof KogitoNodeInstanceContainer) {
                assertNodeActive((KogitoNodeInstanceContainer) nodeInstance, names);
            }
        }
    }

    public void assertNodeTriggered(String processInstanceId, String... nodeNames) {
        List<String> names = getNotTriggeredNodes(nodeNames);
        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Node(s) not executed: " + s);
        }
    }

    public void assertNotNodeTriggered(String processInstanceId, String... nodeNames) {
        List<String> names = getNotTriggeredNodes(nodeNames);
        assertThat(names).containsExactly(nodeNames);
    }

    public int getNumberOfProcessInstances(String processId) {
        int counter = 0;
        LogEvent[] events = workingMemoryLogger.getLogEvents().toArray(new LogEvent[0]);
        for (LogEvent event : events) {
            if (event.getType() == LogEvent.BEFORE_RULEFLOW_CREATED) {
                if (((RuleFlowLogEvent) event).getProcessId().equals(processId)) {
                    counter++;
                }
            }
        }

        return counter;
    }

    protected boolean assertProcessInstanceState(int state, KogitoProcessInstance processInstance) {

        return processInstance.getState() == state;
    }

    private List<String> getNotTriggeredNodes(String... nodeNames) {
        Set<String> triggeredNodes = workingMemoryLogger.getLogEvents().stream()
                .filter(e -> e instanceof RuleFlowNodeLogEvent)
                .map(e -> ((RuleFlowNodeLogEvent) e).getNodeName())
                .collect(toSet());

        return Arrays.stream(nodeNames).filter(n -> !triggeredNodes.contains(n)).collect(toList());
    }

    protected void clearHistory() {
        if (workingMemoryLogger != null) {
            workingMemoryLogger.clear();
        }
    }

    public void assertProcessVarExists(KogitoProcessInstance process,
            String... processVarNames) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        List<String> names = new ArrayList<>();
        for (String nodeName : processVarNames) {
            names.add(nodeName);
        }

        for (String pvar : instance.getVariables().keySet()) {
            if (names.contains(pvar)) {
                names.remove(pvar);
            }
        }

        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Process Variable(s) do not exist: " + s);
        }

    }

    public String getProcessVarValue(KogitoProcessInstance processInstance, String varName) {
        String actualValue = null;
        Object value = ((WorkflowProcessInstanceImpl) processInstance).getVariable(varName);
        if (value != null) {
            actualValue = value.toString();
        }

        return actualValue;
    }

    public void assertProcessVarValue(KogitoProcessInstance processInstance, String varName, Object varValue) {
        String actualValue = getProcessVarValue(processInstance, varName);
        assertThat(actualValue).as("Variable " + varName + " value misatch!").isEqualTo(varValue);
    }

    public void assertNodeExists(KogitoProcessInstance process, String... nodeNames) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        List<String> names = new ArrayList<>();
        for (String nodeName : nodeNames) {
            names.add(nodeName);
        }

        for (Node node : instance.getNodeContainer().getNodes()) {
            if (names.contains(node.getName())) {
                names.remove(node.getName());
            }
        }

        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Node(s) do not exist: " + s);
        }
    }

    public void assertNumOfIncommingConnections(KogitoProcessInstance process,
            String nodeName, int num) {
        assertNodeExists(process, nodeName);
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        for (Node node : instance.getNodeContainer().getNodes()) {
            if (node.getName().equals(nodeName)) {
                if (node.getIncomingConnections().size() != num) {
                    fail("Expected incomming connections: " + num + " - found "
                            + node.getIncomingConnections().size());
                } else {
                    break;
                }
            }
        }
    }

    public void assertNumOfOutgoingConnections(KogitoProcessInstance process,
            String nodeName, int num) {
        assertNodeExists(process, nodeName);
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        for (Node node : instance.getNodeContainer().getNodes()) {
            if (node.getName().equals(nodeName)) {
                if (node.getOutgoingConnections().size() != num) {
                    fail("Expected outgoing connections: " + num + " - found "
                            + node.getOutgoingConnections().size());
                } else {
                    break;
                }
            }
        }
    }

    public void assertVersionEquals(KogitoProcessInstance process, String version) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if (!instance.getWorkflowProcess().getVersion().equals(version)) {
            fail("Expected version: " + version + " - found "
                    + instance.getWorkflowProcess().getVersion());
        }
    }

    public void assertProcessNameEquals(KogitoProcessInstance process, String name) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if (!instance.getWorkflowProcess().getName().equals(name)) {
            fail("Expected name: " + name + " - found "
                    + instance.getWorkflowProcess().getName());
        }
    }

    public void assertPackageNameEquals(KogitoProcessInstance process,
            String packageName) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if (!instance.getWorkflowProcess().getPackageName().equals(packageName)) {
            fail("Expected package name: " + packageName + " - found "
                    + instance.getWorkflowProcess().getPackageName());
        }
    }

    public Object eval(Reader reader, Map vars) {
        try {
            return eval(toString(reader), vars);
        } catch (IOException e) {
            throw new RuntimeException("Exception Thrown", e);
        }
    }

    private String toString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder(1024);
        int charValue;

        while ((charValue = reader.read()) != -1) {
            sb.append((char) charValue);
        }
        return sb.toString();
    }

    public Object eval(String str, Map vars) {

        ParserContext context = new ParserContext();
        context.addPackageImport("org.jbpm.task");
        context.addPackageImport("org.jbpm.task.service");
        context.addPackageImport("org.jbpm.task.query");
        context.addPackageImport("java.util");

        vars.put("now", new Date());
        return MVELSafeHelper.getEvaluator().executeExpression(MVEL.compileExpression(str, context),
                vars);
    }

    protected void assertProcessInstanceCompleted(String processInstanceId, KogitoProcessRuntime kruntime) {
        KogitoProcessInstance processInstance = kruntime.getProcessInstance(processInstanceId);
        assertThat(processInstance).as("Process instance has not completed.").isNull();
    }

    protected void assertProcessInstanceAborted(String processInstanceId, KogitoProcessRuntime kruntime) {
        assertThat(kruntime.getProcessInstance(processInstanceId)).isNull();
    }

    protected void assertProcessInstanceActive(String processInstanceId, KogitoProcessRuntime kruntime) {
        assertThat(kruntime.getProcessInstance(processInstanceId)).isNotNull();
    }

}
