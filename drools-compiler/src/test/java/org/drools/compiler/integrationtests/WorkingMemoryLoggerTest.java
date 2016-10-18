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

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Message;
import org.drools.core.WorkingMemory;
import org.drools.core.audit.WorkingMemoryFileLogger;
import org.drools.core.audit.WorkingMemoryInMemoryLogger;
import org.drools.core.audit.event.ActivationLogEvent;
import org.drools.core.audit.event.LogEvent;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;

public class WorkingMemoryLoggerTest extends CommonTestMethodBase {
    private static final String LOG = "session";

    @Test
    public void testOutOfMemory() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "empty.drl");

        for (int i = 0; i < 10000; i++) {
            StatefulKnowledgeSession session = createKnowledgeSession(kbase);
            WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger(session);
            session.fireAllRules();
            session.dispose();
        }
    }

    @Test
    public void testLogAllBoundVariables() throws Exception {
        // BZ-1271909
        String drl =
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule \"Hello World\" no-loop\n" +
                "    when\n" +
                "        $messageInstance : Message( $myMessage : message )\n" +
                "    then\n" +
                "        update($messageInstance);\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        WorkingMemoryInMemoryLogger logger = new WorkingMemoryInMemoryLogger((WorkingMemory) ksession);

        Message message = new Message();
        message.setMessage("Hello World");
        ksession.insert(message);
        ksession.fireAllRules();

        for (LogEvent logEvent : logger.getLogEvents()) {
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

        public void setTypeId(Integer id) {
            typeId = id;
        }

        public AnyType() {
            typeId = 1;
            typeName = "test";
        }

        public AnyType(Integer id, String type) {
            typeId = id;
            typeName = type;
        }
    }

    @Test
    public void testRetraction() throws Exception {
        // RHBRMS-2641
        String drl =
                 "import " + AnyType.class.getCanonicalName() + ";\n" +
                 "rule \"retract\" when\n" +
                 "		$any : AnyType( $typeId :typeId, typeName in (\"Standard\", \"Extended\") )\n" +
                 "		$any_c1 : AnyType( typeId == $typeId, typeName not in (\"Standard\", \"Extended\") ) \r\n" +
                 "	then\n" +
                 "		delete($any);\n" +
                 "		$any.setTypeId(null);\n" +
                 "end";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        WorkingMemoryInMemoryLogger logger = new WorkingMemoryInMemoryLogger( (WorkingMemory) ksession );

        ksession.insert(new AnyType(1, "Standard"));
        ksession.insert(new AnyType(1, "Extended"));
        ksession.insert(new AnyType(1, "test"));

        assertEquals( 2, ksession.fireAllRules() );
    }
}
