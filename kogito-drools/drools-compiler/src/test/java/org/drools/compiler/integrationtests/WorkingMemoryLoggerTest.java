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

package org.drools.compiler.integrationtests;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Message;
import org.drools.core.WorkingMemory;
import org.drools.core.audit.WorkingMemoryInMemoryLogger;
import org.drools.core.audit.event.ActivationLogEvent;
import org.drools.core.audit.event.LogEvent;
import org.drools.core.event.ProcessNodeLeftEventImpl;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.utils.KieHelper;

public class WorkingMemoryLoggerTest extends CommonTestMethodBase {

    @Test
    public void testOutOfMemory() throws Exception {
        final KieBase kbase = loadKnowledgeBase( "empty.drl");

        for (int i = 0; i < 10000; i++) {
            final KieSession session = createKnowledgeSession(kbase);
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

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

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

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        ksession.insert(new AnyType(1, "Standard"));
        ksession.insert(new AnyType(1, "Extended"));
        ksession.insert(new AnyType(1, "test"));

        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testWorkingMemoryLoggerWithUnbalancedBranches() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_Logger.drl"));
        final KieSession wm = createKnowledgeSession(kbase);

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
    public void testLogEvents() {

        final KieSession ksession = new KieHelper().build()
                                                   .newKieSession();

        final WorkingMemoryInMemoryLogger logger = new WorkingMemoryInMemoryLogger((WorkingMemory) ksession);

        NodeInstance ni = mock(NodeInstance.class);
        when(ni.getProcessInstance()).thenReturn(mock(WorkflowProcessInstance.class));
        logger.afterNodeLeft(new ProcessNodeLeftEventImpl(ni, ksession));
        List<LogEvent> logEvents = logger.getLogEvents();
        assertEquals(logEvents.size(), 1);
        assertTrue(logEvents.get(0).toString().startsWith("AFTER PROCESS NODE EXITED"));
    }

}
