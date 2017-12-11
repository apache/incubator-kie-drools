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
        String drl1 = "package org.pkg1\n" +
                "rule R11 when\n" +
                "   $m : String( this == \"Hello World\" )\n" +
                "then\n" +
                "end\n" +
                "rule R12 when\n" +
                "   $m : String( this == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.pkg2\n" +
                "rule R21 when\n" +
                "   $m : String( this == \"Hello World\" )\n" +
                "then\n" +
                "end\n" +
                "rule R22 when\n" +
                "   $m : String( this == \"Aloha Earth\" )\n" +
                "then\n" +
                "end\n";

        KieContainer kieContainer = getKieContainer( createKieProjectWithPackagesAnd2KieBases(),
                                                     new KieFile( "src/main/resources/org/pkg1/r1.drl", drl1 ),
                                                     new KieFile( "src/main/resources/org/pkg2/r2.drl", drl2 ) );

        KieSession ksession = kieContainer.newKieSession("KSession1");
        ksession.insert("Hello World");
        assertEquals( 1, ksession.fireAllRules() );

        ksession = kieContainer.newKieSession("KSession1");
        ksession.insert("Hi Universe");
        assertEquals( 1, ksession.fireAllRules() );

        ksession = kieContainer.newKieSession("KSession1");
        ksession.insert("Aloha Earth");
        assertEquals( 0, ksession.fireAllRules() );

        ksession = kieContainer.newKieSession("KSession2");
        ksession.insert("Hello World");
        assertEquals( 1, ksession.fireAllRules() );

        ksession = kieContainer.newKieSession("KSession2");
        ksession.insert("Hi Universe");
        assertEquals( 0, ksession.fireAllRules() );

        ksession = kieContainer.newKieSession("KSession2");
        ksession.insert("Aloha Earth");
        assertEquals(1, ksession.fireAllRules());
    }

    private KieModuleModel createKieProjectWithPackagesAnd2KieBases() {
        KieModuleModel kproj = KieServices.get().newKieModuleModel();

        kproj.newKieBaseModel()
                .addPackage("org.pkg1")
                .newKieSessionModel("KSession1");

        kproj.newKieBaseModel()
                .addPackage("org.pkg2")
                .newKieSessionModel("KSession2");

        return kproj;
    }
}
