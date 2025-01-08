/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.compiler.kie.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.generatePomXml;

public class WireChannelTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    private static final List<Object> channelMessages = new ArrayList<Object>();
    
    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testWireChannel(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        channelMessages.clear();

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "listener-test", "1.0");
        build(kieBaseTestConfiguration, ks, releaseId);
        KieContainer kieContainer = ks.newKieContainer(releaseId);

        KieSession ksession = kieContainer.newKieSession();
        ksession.fireAllRules();

        assertThat(channelMessages.size()).isEqualTo(1);
        assertThat(channelMessages.get(0)).isEqualTo("Test Message");
    }

    private void build(KieBaseTestConfiguration kieBaseTestConfiguration, KieServices ks, ReleaseId releaseId) throws IOException {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieSessionModel ksession1 = kproj.newKieBaseModel("KBase1").newKieSessionModel("KSession1").setDefault(true);
        
        ksession1.newChannelModel("testChannel", RecordingChannel.class.getName());

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML())
           .writePomXML( generatePomXml(releaseId) )
           .write("src/main/resources/KBase1/rules.drl", createDRL());

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        assertThat(kieBuilder.getResults().getMessages().isEmpty()).isTrue();
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
			channelMessages.add(object);
		}
    }
}
