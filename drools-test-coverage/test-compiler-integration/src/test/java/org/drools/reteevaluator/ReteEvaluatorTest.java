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
package org.drools.reteevaluator;

import java.util.List;
import java.util.UUID;

import org.drools.core.common.ReteEvaluator;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.model.codegen.ExecutableModelProject;
import org.drools.mvel.compiler.Person;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ReteEvaluatorTest {

    @Test
    public void testPropertyReactivity() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  $s : String()\n" +
                        "  $p : Person(name == $s)\n" +
                        "then\n" +
                        "  modify($p) { setAge($p.getAge()+1) }\n" +
                        "end";

        ReteEvaluator reteEvaluator = new StatefulKnowledgeSessionImpl( 1L, getKBase( str ) );
        try {
            Person me = new Person( "Mario", 40 );
            reteEvaluator.insert( "Mario" );
            reteEvaluator.insert( me );
            assertThat(reteEvaluator.fireAllRules()).isEqualTo(1);

            assertThat(me.getAge()).isEqualTo(41);
        } finally {
            reteEvaluator.dispose();
        }
    }

    private InternalKnowledgeBase getKBase(String... stringRules) {
        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "kjar-test-" + UUID.randomUUID(), "1.0" );

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writePomXML( getPom( releaseId ) );
        for (int i = 0; i < stringRules.length; i++) {
            kfs.write( String.format("src/main/resources/r%d.drl", i), stringRules[i] );
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);

        List<Message> messages = kieBuilder.getResults().getMessages();
        if ( !messages.isEmpty() ) {
            fail( messages.toString() );
        }

        return (InternalKnowledgeBase) ks.newKieContainer(releaseId).getKieBase();
    }

    private static String getPom(ReleaseId releaseId) {
        String pom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                        "  <modelVersion>4.0.0</modelVersion>\n" +
                        "\n" +
                        "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
                        "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
                        "  <version>" + releaseId.getVersion() + "</version>\n" +
                        "</project>";
        return pom;
    }
}
