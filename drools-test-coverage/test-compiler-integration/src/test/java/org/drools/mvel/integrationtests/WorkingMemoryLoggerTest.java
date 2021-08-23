/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.mvel.integrationtests;

import java.util.Collection;
import java.util.List;

import org.drools.core.WorkingMemory;
import org.drools.core.audit.WorkingMemoryFileLogger;
import org.drools.core.audit.WorkingMemoryInMemoryLogger;
import org.drools.core.audit.event.ActivationLogEvent;
import org.drools.core.audit.event.LogEvent;
import org.drools.core.event.ProcessNodeLeftEventImpl;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Message;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class WorkingMemoryLoggerTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public WorkingMemoryLoggerTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testOutOfMemory() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "empty.drl");

        for (int i = 0; i < 10000; i++) {
            KieSession session = kbase.newKieSession();
            final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger((WorkingMemory) session);
            session.fireAllRules();
            session.dispose();
        }
    }

    @Test
    public void testLogAllBoundVariables() throws Exception {
        // BZ-1271909
        final String drl =
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule \"Hello World\" no-loop\n" +
                "    when\n" +
                "        $messageInstance : Message( $myMessage : message )\n" +
                "    then\n" +
                "        update($messageInstance);\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final WorkingMemoryInMemoryLogger logger = new WorkingMemoryInMemoryLogger((WorkingMemory) ksession);

        final Message message = new Message();
        message.setMessage("Hello World");
        ksession.insert(message);
        ksession.fireAllRules();

        for (final LogEvent logEvent : logger.getLogEvents()) {
            if (logEvent instanceof ActivationLogEvent) {
                assertTrue( ((ActivationLogEvent) logEvent ).getDeclarations().contains( "$messageInstance" ));
                assertTrue( ((ActivationLogEvent) logEvent ).getDeclarations().contains( "$myMessage" ));
            }
        }
    }

    public static class AnyType {
        private Integer typeId = 1;
        private String typeName = "test";

        public String getTypeName() {
            return typeName;
        }

        public Integer getTypeId() {
            return typeId.intValue();
        }

        public void setTypeId(final Integer id) {
            typeId = id;
        }

        public AnyType() {
            typeId = 1;
            typeName = "test";
        }

        public AnyType(final Integer id, final String type) {
            typeId = id;
            typeName = type;
        }
    }

    @Test
    public void testRetraction() throws Exception {
        // RHBRMS-2641
        final String drl =
                 "import " + AnyType.class.getCanonicalName() + ";\n" +
                 "rule \"retract\" when\n" +
                 "    $any : AnyType( $typeId :typeId, typeName in (\"Standard\", \"Extended\") )\n" +
                 "    $any_c1 : AnyType( typeId == $typeId, typeName not in (\"Standard\", \"Extended\") ) \r\n" +
                 "then\n" +
                 "    delete($any);\n" +
                 "    $any.setTypeId(null);\n" +
                 "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final WorkingMemoryInMemoryLogger logger = new WorkingMemoryInMemoryLogger( (WorkingMemory) ksession );

        ksession.insert(new AnyType(1, "Standard"));
        ksession.insert(new AnyType(1, "Extended"));
        ksession.insert(new AnyType(1, "test"));

        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testWorkingMemoryLoggerWithUnbalancedBranches() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_Logger.drl");
        KieSession wm = kbase.newKieSession();

        try {
            wm.fireAllRules();

            wm.insert(new Cheese("a", 10));
            wm.insert(new Cheese("b", 11));
            wm.fireAllRules();

        } catch (final Exception e) {
            e.printStackTrace();
            fail("No exception should be raised ");
        }
    }

    @Test
    public void testLogEvents() throws Exception {

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration);
        KieSession ksession = kbase.newKieSession();


        final WorkingMemoryInMemoryLogger logger = new WorkingMemoryInMemoryLogger((WorkingMemory) ksession);

        logger.afterNodeLeft(new ProcessNodeLeftEventImpl(new EmtpyNodeInstance(), ksession));
        List<LogEvent> logEvents = logger.getLogEvents();
        assertEquals(logEvents.size(), 1);
        assertTrue(logEvents.get(0).toString().startsWith("AFTER PROCESS NODE EXITED"));

    }

    static public class EmtpyNodeInstance implements NodeInstance {

        @Override
        public long getId() {
            return 0;
        }

        @Override
        public long getNodeId() {
            return 0;
        }

        @Override
        public Node getNode() {
            return null;
        }

        @Override
        public String getNodeName() {
            return "empty.node";
        }

        @Override
        public WorkflowProcessInstance getProcessInstance() {
            return new EmtpyWorkflowProcessInstance();
        }

        @Override
        public NodeInstanceContainer getNodeInstanceContainer() {
            return null;
        }

        @Override
        public Object getVariable(String variableName) {
            return null;
        }

        @Override
        public void setVariable(String variableName, Object value) {
        }

    }

    static class EmtpyWorkflowProcessInstance implements WorkflowProcessInstance {

        @Override
        public String getProcessId() {
            return "emtpy.process";
        }

        @Override
        public Process getProcess() {
            return null;
        }

        @Override
        public long getId() {
            return 1;
        }


        @Override
        public String getProcessName() {
            return null;
        }


        @Override
        public int getState() {
            return ProcessInstance.STATE_ACTIVE;
        }

        @Override
        public long getParentProcessInstanceId() {
            return -1;
        }


        @Override
        public void signalEvent(String type, Object event) {
        }

        @Override
        public String[] getEventTypes() {
            return null;
        }

        @Override
        public Collection<NodeInstance> getNodeInstances() {
            return null;
        }

        @Override
        public NodeInstance getNodeInstance(long nodeInstanceId) {
            return null;
        }

        @Override
        public Object getVariable(String name) {
            return null;
        }

        @Override
        public void setVariable(String name, Object value) {
        }

    }
}
