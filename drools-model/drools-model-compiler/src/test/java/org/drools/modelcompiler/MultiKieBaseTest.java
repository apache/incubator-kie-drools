/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class MultiKieBaseTest extends BaseModelTest {

    public MultiKieBaseTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testHelloWorldWithPackagesAnd2KieBases() throws Exception {
        String drl1a = "package org.pkg1\n" +
                "rule R1 when\n" +
                "   $m : String( this == \"Hello World\" )\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "   $m : String( this == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl2a = "package org.pkg2\n" +
                "rule R1 when\n" +
                "   $m : String( this == \"Hello World\" )\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "   $m : String( this == \"Aloha Earth\" )\n" +
                "then\n" +
                "end\n";

        String drl2b = "package org.pkg2\n" +
                "rule R1 when\n" +
                "   $m : String( this.startsWith(\"Hello\") )\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "   $m : String( this == \"Aloha Earth\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        createAndDeployJar( ks, createKieProjectWithPackagesAnd2KieBases(), releaseId1,
                new KieFile( "src/main/resources/org/pkg1/r1.drl", drl1a ),
                new KieFile( "src/main/resources/org/pkg2/r2.drl", drl2a ) );

        // Create a session and fire rules
        KieContainer kieContainer = ks.newKieContainer( releaseId1 );

        KieSession ksession1 = kieContainer.newKieSession("KSession1");
        ksession1.insert("Hello World");
        assertEquals( 1, ksession1.fireAllRules() );

        ksession1.insert("Hi Universe");
        assertEquals( 1, ksession1.fireAllRules() );

        ksession1.insert("Aloha Earth");
        assertEquals( 0, ksession1.fireAllRules() );

        KieSession ksession2 = kieContainer.newKieSession("KSession2");
        ksession2.insert("Hello World");
        assertEquals( 1, ksession2.fireAllRules() );

        ksession2.insert("Hi Universe");
        assertEquals( 0, ksession2.fireAllRules() );

        ksession2.insert("Aloha Earth");
        assertEquals( 1, ksession2.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, createKieProjectWithPackagesAnd2KieBases(), releaseId2,
                new KieFile( "src/main/resources/org/pkg1/r1.drl", drl1a ),
                new KieFile( "src/main/resources/org/pkg2/r2.drl", drl2b ) );

        // try to update the container to version 1.1.0
        kieContainer.updateToVersion( releaseId2 );

        assertEquals( 0, ksession1.fireAllRules() );
        assertEquals( 1, ksession2.fireAllRules() );
    }

    private KieModuleModel createKieProjectWithPackagesAnd2KieBases() {
        KieModuleModel kproj = KieServices.get().newKieModuleModel();

        kproj.newKieBaseModel("KBase1")
                .addPackage("org.pkg1")
                .newKieSessionModel("KSession1");

        kproj.newKieBaseModel("KBase2")
                .addPackage("org.pkg2")
                .newKieSessionModel("KSession2");

        return kproj;
    }
}
