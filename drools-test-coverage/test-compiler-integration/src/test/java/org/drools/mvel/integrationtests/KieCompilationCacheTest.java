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
package org.drools.mvel.integrationtests;

import java.util.Collection;

import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.mvel.compiler.Message;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is a sample class to launch a rule.
 */
@RunWith(Parameterized.class)
public class KieCompilationCacheTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public KieCompilationCacheTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testCompilationCache() throws Exception {
        String drl = "package org.drools.compiler\n" +
                "declare type X\n" +
                "    foo : String\n" +
                "end\n"+
                "rule R1 when\n" +
                "   $m : X( foo == \"Hello World\" )\n" +
                "then\n" +
                "end\n";
        
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        
        ReleaseId releaseId = ks.getRepository().getDefaultReleaseId();
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository().getKieModule( releaseId );
        byte[] jar = kieModule.getBytes();
        
        MemoryFileSystem mfs = MemoryFileSystem.readFromJar( jar );
        File file = mfs.getFile( KieBuilderImpl.getCompilationCachePath( releaseId, "KBase1") );
        assertThat(file).isNotNull();

        Resource jarRes = ks.getResources().newByteArrayResource( jar );
        KieModule km = ks.getRepository().addKieModule( jarRes );
        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        
        KieBase kbase = kc.getKieBase();
        FactType type = kbase.getFactType( "org.drools.compiler", "X" );
        FactField foo = type.getField( "foo" );
        Object x = type.newInstance();
        foo.set( x, "Hello World" );
        
        KieSession ksession = kc.newKieSession();
        ksession.insert(x);

        int count = ksession.fireAllRules();
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testHelloWorldWithPackagesAnd2KieBases() throws Exception {
        String drl1 = "package org.pkg1\n" +
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule R11 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n" +
                "rule R12 when\n" +
                "   $m : Message( message == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.pkg2\n" +
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule R21 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n" +
                "rule R22 when\n" +
                "   $m : Message( message == \"Aloha Earth\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "hello-world", "1.0");

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML(releaseId)
                .write("src/main/resources/KBase1/org/pkg1/r1.drl", drl1)
                .write("src/main/resources/KBase1/org/pkg2/r2.drl", drl2)
                .writeKModuleXML(createKieProjectWithPackagesAnd2KieBases(ks).toXML());
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository().getKieModule( releaseId );
        byte[] jar = kieModule.getBytes();
        
        MemoryFileSystem mfs = MemoryFileSystem.readFromJar( jar );
        File file = mfs.getFile( KieBuilderImpl.getCompilationCachePath( releaseId, "KBase1") );
        assertThat(file).isNotNull();
        file = mfs.getFile( KieBuilderImpl.getCompilationCachePath( releaseId, "KBase2") );
        assertThat(file).isNotNull();

        Resource jarRes = ks.getResources().newByteArrayResource( jar );
        KieModule km = ks.getRepository().addKieModule( jarRes );
        
        KieSession ksession = ks.newKieContainer( km.getReleaseId() ).newKieSession("KSession1");
        ksession.insert(new Message("Hello World"));
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        ksession = ks.newKieContainer(km.getReleaseId()).newKieSession("KSession1");
        ksession.insert(new Message("Hi Universe"));
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        ksession = ks.newKieContainer(km.getReleaseId()).newKieSession("KSession1");
        ksession.insert(new Message("Aloha Earth"));
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        ksession = ks.newKieContainer(km.getReleaseId()).newKieSession("KSession2");
        ksession.insert(new Message("Hello World"));
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        ksession = ks.newKieContainer(km.getReleaseId()).newKieSession("KSession2");
        ksession.insert(new Message("Hi Universe"));
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        ksession = ks.newKieContainer(km.getReleaseId()).newKieSession("KSession2");
        ksession.insert(new Message("Aloha Earth"));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testCacheWigAccumulate() throws Exception {
        String drl1 = "package org.pkg1\n" +
                "rule R11 when\n" +
                "   Number() from accumulate(String(), \n" +
                "              init(int x = 0;)," +
                "              action(x++;)," +
                "              reverse(x--;)," +
                "              result(x))\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "cache-accumulate", "1.0");

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML(releaseId)
                .write("src/main/resources/KBase1/org/pkg1/r1.drl", drl1)
                .writeKModuleXML(createKieProjectWithPackagesAnd2KieBases(ks).toXML());
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository().getKieModule( releaseId );
        byte[] jar = kieModule.getBytes();
        
        MemoryFileSystem mfs = MemoryFileSystem.readFromJar( jar );
        File file = mfs.getFile( KieBuilderImpl.getCompilationCachePath( releaseId, "KBase1") );
        assertThat(file).isNotNull();

        Resource jarRes = ks.getResources().newByteArrayResource( jar );
        KieModule km = ks.getRepository().addKieModule( jarRes );
        
        KieSession ksession = ks.newKieContainer( km.getReleaseId() ).newKieSession("KSession1");
        ksession.insert(new String("Hello World"));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    private KieModuleModel createKieProjectWithPackagesAnd2KieBases(KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();

        kproj.newKieBaseModel("KBase2")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .addPackage("org.pkg1")
                .newKieSessionModel("KSession1");

        kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .addPackage("org.pkg2")
                .newKieSessionModel("KSession2");

        return kproj;
    }
}
