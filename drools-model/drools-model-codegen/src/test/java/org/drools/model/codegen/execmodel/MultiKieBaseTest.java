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
package org.drools.model.codegen.execmodel;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(ksession1.fireAllRules()).isEqualTo(1);

        ksession1.insert("Hi Universe");
        assertThat(ksession1.fireAllRules()).isEqualTo(1);

        ksession1.insert("Aloha Earth");
        assertThat(ksession1.fireAllRules()).isEqualTo(0);

        KieSession ksession2 = kieContainer.newKieSession("KSession2");
        ksession2.insert("Hello World");
        assertThat(ksession2.fireAllRules()).isEqualTo(1);

        ksession2.insert("Hi Universe");
        assertThat(ksession2.fireAllRules()).isEqualTo(0);

        ksession2.insert("Aloha Earth");
        assertThat(ksession2.fireAllRules()).isEqualTo(1);

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, createKieProjectWithPackagesAnd2KieBases(), releaseId2,
                new KieFile( "src/main/resources/org/pkg1/r1.drl", drl1a ),
                new KieFile( "src/main/resources/org/pkg2/r2.drl", drl2b ) );

        // try to update the container to version 1.1.0
        kieContainer.updateToVersion( releaseId2 );

        assertThat(ksession1.fireAllRules()).isEqualTo(0);
        assertThat(ksession2.fireAllRules()).isEqualTo(1);
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

    @Test
    public void testFoldersVsPackages() throws Exception {
        String drl1 =
                "//package org.commented1\n" +
                "package org.pkg1\n" +
                "rule R1 when\n" +
                "   $m : String()\n" +
                "then\n" +
                "end\n";

        String drl2 =
                "/*\n" +
                "package org.commented2\n" +
                "*/\n" +
                "package org.pkg2\n" +
                "rule R1 when\n" +
                "   $m : String()\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "   $m : String()\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.get();

        KieModuleModel kproj = ks.newKieModuleModel();

        kproj.newKieBaseModel("KBase1")
                .newKieSessionModel("KSession1");

        kproj.newKieBaseModel("KBase2")
                .addPackage("org.pkg1")
                .newKieSessionModel("KSession2");

        kproj.newKieBaseModel("KBase3")
                .addPackage("org.pkg2")
                .newKieSessionModel("KSession3");

        kproj.newKieBaseModel("KBase4")
                .addPackage("rules")
                .newKieSessionModel("KSession4");

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-pkgs", "1.0.0" );
        createAndDeployJar( ks, kproj, releaseId1,
                new KieFile( "src/main/resources/org/pkg1/r1.drl", drl1 ),
                new KieFile( "src/main/resources/rules/r2.drl", drl2 ) );

        // Create a session and fire rules
        KieContainer kieContainer = ks.newKieContainer( releaseId1 );

        KieSession ks1 = kieContainer.newKieSession("KSession1");
        ks1.insert( "test" );
        assertThat(ks1.fireAllRules()).isEqualTo(3); // no packages -> both drl are included

        KieSession ks2 = kieContainer.newKieSession("KSession2");
        ks2.insert( "test" );
        assertThat(ks2.fireAllRules()).isEqualTo(1); // only rule in org.pkg1 should fire

        KieSession ks3 = kieContainer.newKieSession("KSession3");
        ks3.insert( "test" );
        assertThat(ks3.fireAllRules()).isEqualTo(2); // only rules in org.pkg2 should fire

        KieSession ks4 = kieContainer.newKieSession("KSession4");
        ks4.insert( "test" );
        assertThat(ks4.fireAllRules()).isEqualTo(0); // there is no "rules" package and folder is not relevant
    }

    @Test
    public void testDotInKieBaseName() throws Exception {
        // DROOLS-5845
        String drl1 =
                "package org.pkg1\n" +
                "rule R1 when\n" +
                "   $m : String()\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.get();

        KieModuleModel kproj = ks.newKieModuleModel();

        kproj.newKieBaseModel("Kie.Base")
                .addPackage("org.pkg1")
                .newKieSessionModel("Kie.Session");

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-dor", "1.0.0" );
        createAndDeployJar( ks, kproj, releaseId1,
                new KieFile( "src/main/resources/org/pkg1/r1.drl", drl1 ) );

        // Create a session and fire rules
        KieContainer kieContainer = ks.newKieContainer( releaseId1 );

        KieSession ks2 = kieContainer.newKieSession("Kie.Session");
        ks2.insert( "test" );
        assertThat(ks2.fireAllRules()).isEqualTo(1); // only rule in org.pkg1 should fire
    }

    @Test
    public void testHelloMultiKieBasesWithSharedDeclaredType() throws Exception {
        // DROOLS-6331
        String drlType =
                "package org.pkg.type\n" +
                "declare MyType value : String end\n";

        String drl1 =
                "package org.pkg1\n" +
                "import org.pkg.type.MyType\n" +
                "rule R1 when\n" +
                "   $s : String()\n" +
                "then\n" +
                "   insert(new MyType($s));\n" +
                "end\n";

        String drl2 =
                "package org.pkg2\n" +
                "import org.pkg.type.MyType\n" +
                "rule R1 when\n" +
                "   MyType( value.startsWith(\"Hello\") )\n" +
                "then\n" +
                "end\n";

        String drl3 =
                "package org.pkg3\n" +
                "import org.pkg.type.MyType\n" +
                "rule R1 when\n" +
                "   MyType( value.startsWith(\"Hi\") )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();

        kproj.newKieBaseModel("KBaseA")
                .addPackage("org.pkg.type")
                .addPackage("org.pkg1")
                .addPackage("org.pkg2")
                .newKieSessionModel("KSessionA");

        kproj.newKieBaseModel("KBaseB")
                .addPackage("org.pkg.type")
                .addPackage("org.pkg1")
                .addPackage("org.pkg3")
                .newKieSessionModel("KSessionB");

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-types", "1.0.0" );
        createAndDeployJar( ks, kproj, releaseId1,
                new KieFile( "src/main/resources/org/pkg/type/r0.drl", drlType ),
                new KieFile( "src/main/resources/org/pkg1/r1.drl", drl1 ),
                new KieFile( "src/main/resources/org/pkg2/r2.drl", drl2 ),
                new KieFile( "src/main/resources/org/pkg3/r3.drl", drl3 ) );

        // Create a session and fire rules
        KieContainer kieContainer = ks.newKieContainer( releaseId1 );

        KieSession ksessionA = kieContainer.newKieSession("KSessionA");
        ksessionA.insert("Hello World");
        assertThat(ksessionA.fireAllRules()).isEqualTo(2);
        ksessionA.insert("Hi Universe");
        assertThat(ksessionA.fireAllRules()).isEqualTo(1);

        KieSession ksessionB = kieContainer.newKieSession("KSessionB");
        ksessionB.insert("Hello World");
        assertThat(ksessionB.fireAllRules()).isEqualTo(1);
        ksessionB.insert("Hi Universe");
        assertThat(ksessionB.fireAllRules()).isEqualTo(2);
    }
}
