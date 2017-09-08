/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.kie.builder;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.generatePomXml;

public class WireChannelTest {

    private static final List<Object> channelMessages = new ArrayList<Object>();
    
    @Test
    public void testWireChannel() throws Exception {
        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "listener-test", "1.0-SNAPSHOT");
        build(ks, releaseId);
        KieContainer kieContainer = ks.newKieContainer(releaseId);

        KieSession ksession = kieContainer.newKieSession();
        ksession.fireAllRules();
        
        assertEquals(1, channelMessages.size());
        assertEquals("Test Message", channelMessages.get(0));
    }

    private void build(KieServices ks, ReleaseId releaseId) throws IOException {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieSessionModel ksession1 = kproj.newKieBaseModel("KBase1").newKieSessionModel("KSession1").setDefault(true);
        
        ksession1.newChannelModel("testChannel", RecordingChannel.class.getName());

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML())
           .writePomXML( generatePomXml(releaseId) )
           .write("src/main/resources/KBase1/rules.drl", createDRL());

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
    }

    private String createDRL() {
        return "package org.kie.test\n" +
                "rule SendMessage when\n" +
                "then\n" +
                "    channels[\"testChannel\"].send(\"Test Message\");\n" +
                "end\n";
    }

    public static class RecordingChannel implements Channel {

        @Override
		public void send(Object object) {
			// TODO Auto-generated method stub
			channelMessages.add(object);
		}
    }
}
