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

package org.drools.compiler.kie.builder;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.ListenerModel;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.generatePomXml;

public class WireListenerTest {

    private static final List<ObjectInsertedEvent> insertEvents = new ArrayList<ObjectInsertedEvent>();
    private static final List<ObjectUpdatedEvent> updateEvents = new ArrayList<ObjectUpdatedEvent>();
    private static final List<ObjectDeletedEvent> retractEvents = new ArrayList<ObjectDeletedEvent>();

    @Test
    public void testWireListener() throws Exception {
        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "listener-test", "1.0-SNAPSHOT");
        build(ks, releaseId);
        KieContainer kieContainer = ks.newKieContainer(releaseId);

        KieSession ksession = kieContainer.newKieSession();
        ksession.fireAllRules();

        assertEquals(1, insertEvents.size());
        assertEquals(1, updateEvents.size());
        assertEquals(1, retractEvents.size());
    }

    private void build(KieServices ks, ReleaseId releaseId) throws IOException {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieSessionModel ksession1 = kproj.newKieBaseModel("KBase1").newKieSessionModel("KSession1").setDefault(true);

        ksession1.newListenerModel(RecordingWorkingMemoryEventListener.class.getName(), ListenerModel.Kind.RULE_RUNTIME_EVENT_LISTENER);

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML())
           .writePomXML( generatePomXml(releaseId) )
           .write("src/main/resources/KBase1/rules.drl", createDRL());

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
    }

    private String createDRL() {
        return "package org.kie.test\n" +
                "declare Account\n" +
                "    balance : int\n" +
                "end\n" +
                "rule OpenAccount when\n" +
                "then\n" +
                "    insert( new Account(100) );\n" +
                "end\n" +
                "rule PayTaxes when\n" +
                "    $account : Account( $balance : balance > 0 ) \n" +
                "then\n" +
                "    modify( $account ) { setBalance( $balance - 200 ) }\n" +
                "end\n" +
                "rule CloseAccountWithNegeativeBalance when\n" +
                "    $account : Account( balance < 0 ) \n" +
                "then\n" +
                "    retract( $account );\n" +
                "end\n";
    }

    public static class RecordingWorkingMemoryEventListener implements RuleRuntimeEventListener {

        @Override
        public void objectInserted(ObjectInsertedEvent event) {
            insertEvents.add(event);
        }

        @Override
        public void objectUpdated(ObjectUpdatedEvent event) {
            updateEvents.add(event);
        }

        @Override
        public void objectDeleted(ObjectDeletedEvent event) {
            retractEvents.add(event);
        }
    }
}
