/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ancompiler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drools.core.common.NamedEntryPoint;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.ObjectSinkPropagator;
import org.drools.core.reteoo.ObjectTypeNode;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.AlphaNetworkCompilerOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AlphaNetworkCompilerTest extends BaseModelTest {

    public AlphaNetworkCompilerTest(RUN_TYPE testRunType ) {
        super( testRunType );
    }

    public class Message implements Serializable {
        private final String value;

        public Message( String value ) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Test
    public void testKJarUpgradeWithDeclaredType() throws Exception {
        String drl1 = "package org.drools.incremental\n" +
                "declare Message value : String end\n" +
                "rule Init when then insert(new Message( \"Hello World\" )); end\n" +
                "rule R1 when\n" +
                "   $m : Message( value.startsWith(\"H\") )\n" +
                "then\n" +
                "   System.out.println($m.getValue());" +
                "end\n";

        String drl2_1 = "package org.drools.incremental\n" +
                "rule R2 when\n" +
                "   $m : Message( value == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl2_2 = "package org.drools.incremental\n" +
                "rule R2 when\n" +
                "   $m : Message( value == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        String drl2_3 = "package org.drools.incremental\n" +
                "global java.util.List list;\n" +
                "rule R2 when\n" +
                "   $m : Message( value == \"Hello World\" )\n" +
                "then\n" +
                "   list.add($m.getValue());\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );

        KieModuleModel kieModuleModel = ks.newKieModuleModel();
        if(this.testRunType.isAlphaNetworkCompiler()) {
            kieModuleModel.setConfigurationProperty("drools.alphaNetworkCompiler", AlphaNetworkCompilerOption.INMEMORY.toString());
        }
        createAndDeployJar( ks, kieModuleModel, releaseId1, drl1, drl2_1 );

        KieContainer kc = ks.newKieContainer( releaseId1 );

        // Create a session and fire rules
        KieSession ksession = kc.newKieSession();
        if(this.testRunType.isAlphaNetworkCompiler()) {
            this.assertReteIsAlphaNetworkCompiled(ksession);
        }
        assertEquals( 2, ksession.fireAllRules() );

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, kieModuleModel, releaseId2, drl1, drl2_2 );

        // try to update the container to version 1.1.0
        kc.updateToVersion( releaseId2 );

        // try with a new session
        KieSession ksession2 = kc.newKieSession();
        if(this.testRunType.isAlphaNetworkCompiler()) {
            this.assertReteIsAlphaNetworkCompiled(ksession2);
        }

        assertEquals( 3, ksession2.fireAllRules() );

        // Create a new jar for version 1.2.0
        ReleaseId releaseId3 = ks.newReleaseId( "org.kie", "test-upgrade", "1.2.0" );
        createAndDeployJar( ks, kieModuleModel, releaseId3, drl1, drl2_3 );

        // try to update the container to version 1.2.0
        kc.updateToVersion( releaseId3 );
        KieSession kieSession3 = kc.newKieSession();
        if(this.testRunType.isAlphaNetworkCompiler()) {
            this.assertReteIsAlphaNetworkCompiled(kieSession3);
        }

        List<String> list = new ArrayList<>();
        ksession2.setGlobal( "list", list );
        ksession2.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( "Hello World", list.get(0) );
    }

    @Test
    public void testNormalizationForAlphaIndexing() {
        final String str =
                "package org.drools.test;\n" +
                        "import " + Person.class.getCanonicalName() + ";\n" +
                        "rule R1 when \n" +
                        " $p : Person(\"Toshiya\" == name)\n" +
                        "then\n" +
                        "end\n" +
                        "rule R2 when \n" +
                        " $p : Person(\"Mario\" == name)\n" +
                        "then\n" +
                        "end\n" +
                        "rule R3 when \n" +
                        " $p : Person(\"Luca\" == name)\n" +
                        "then\n" +
                        "end\n";

        final KieSession ksession = getKieSession(str);

        ObjectTypeNode otn = ((NamedEntryPoint) ksession.getEntryPoint("DEFAULT")).getEntryPointNode().getObjectTypeNodes().entrySet()
                .stream()
                .filter(e -> e.getKey().getClassName().equals(Person.class.getCanonicalName()))
                .map(e -> e.getValue())
                .findFirst()
                .get();
        ObjectSinkPropagator objectSinkPropagator = otn.getObjectSinkPropagator();
        if(this.testRunType.isAlphaNetworkCompiler()) {
            objectSinkPropagator = ((CompiledNetwork)objectSinkPropagator).getOriginalSinkPropagator();
        }
        CompositeObjectSinkAdapter sinkAdaptor = (CompositeObjectSinkAdapter) objectSinkPropagator;

        assertNotNull(sinkAdaptor.getHashedSinkMap());
        assertEquals(3, sinkAdaptor.getHashedSinkMap().size());

        final Person p = new Person("Toshiya", 45);
        ksession.insert(p);
        assertEquals(1, ksession.fireAllRules());
    }
}
