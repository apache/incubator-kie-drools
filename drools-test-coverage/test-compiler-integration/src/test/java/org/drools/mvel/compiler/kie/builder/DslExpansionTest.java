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
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.generatePomXml;

/**
 * Test for DSL expansion with KieBuilder
 */
public class DslExpansionTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testDSLExpansion_MessageImplNPE(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId = ks.newReleaseId( "org.kie", "dsl-test", "1.0" );
        final KieModuleModel kproj = ks.newKieModuleModel();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML( kproj.toXML() )
                .writePomXML( generatePomXml( releaseId ) )
                .write( "src/main/resources/KBase1/test-dsl.dsl", createDSL() )
                .write( "src/main/resources/KBase1/test-rule.dslr", createDRL() );

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        final List<Message> messages = kieBuilder.getResults().getMessages();
        if ( !messages.isEmpty() ) {
            for ( final Message m : messages ) {
                System.out.println( m.getText() );
            }
        }
        assertThat(messages.isEmpty()).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testDSLExpansion_NoExpansion(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId = ks.newReleaseId( "org.kie", "dsl-test", "1.0" );
        final KieModuleModel kproj = ks.newKieModuleModel();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML( kproj.toXML() )
                .writePomXML( generatePomXml( releaseId ) )
                .write( "src/main/resources/KBase1/test-dsl.dsl", createDSL() )
                .write( "src/main/resources/KBase1/test-rule.drl", createDRL() );

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        final List<Message> messages = kieBuilder.getResults().getMessages();
        if ( !messages.isEmpty() ) {
            for ( final Message m : messages ) {
                System.out.println( m.getText() );
            }
        }
        assertThat(messages.isEmpty()).isFalse();
    }

    private String createDSL() {
        return "[when]There is a smurf=Smurf()\n";
    }

    private String createDRL() {
        return "package org.kie.test\n" +
                "declare Smurf\n" +
                "    name : String\n" +
                "end\n" +
                "rule Smurfs\n" +
                "when\n" +
                "    There is a smurf\n" +
                "then\n" +
                "    >System.out.println(\"Smurfs rock!\");\n" +
                "end\n";
    }

}
