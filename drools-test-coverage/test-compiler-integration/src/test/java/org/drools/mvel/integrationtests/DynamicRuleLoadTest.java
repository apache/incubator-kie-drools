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

import java.lang.reflect.Field;
import java.util.Collection;

import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.mvel.compiler.Message;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class DynamicRuleLoadTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DynamicRuleLoadTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private final String drl1 =
            "package org.drools.mvel.compiler\n" +
            "rule R1 when\n" +
            "   Message( $m : message )\n" +
            "then\n" +
            "    System.out.println($m);\n" +
            "end\n";

    private final String drl2_1 =
            "package org.drools.mvel.compiler\n" +
            "global " + DynamicRuleLoadTest.class.getCanonicalName() + " test;\n" +
            "rule R2_1 when\n" +
            "   $m : Message( message == \"Hi Universe\" )\n" +
            "then\n" +
            "    test.updateToVersion();" +
            "end\n";

    private final String drl2_2 =
            "package org.drools.mvel.compiler\n" +
            "global " + DynamicRuleLoadTest.class.getCanonicalName() + " test;\n" +
            "rule R2_2 when\n" +
            "   $m : Message( message == \"Hello World\" )\n" +
            "then\n" +
            "    test.done();" +
            "end\n";

    private final String javaSrc =
                    "package org.drools.mvel.compiler.test;\n" +
                    "public class PersonObject {\n" +
                    "    private String id;\n" +
                    "    public String getId() {\n" +
                    "        return id;\n" +
                    "    }\n" +
                    "    public void setId(String id) {\n" +
                    "        this.id = id;\n" +
                    "    }\n" +
                    "    public void updateId() {\n" +
                    "        this.id = \"Person from version 1\";\n" +
                    "    }\n" +
                    "}";

    private final String javaSrc_2 =
                    "package org.drools.mvel.compiler.test;\n" +
                    "public class PersonObject {\n" +
                    "    private String id;\n" +
                    "    public String getId() {\n" +
                    "        return id;\n" +
                    "    }\n" +
                    "    public void setId(String id) {\n" +
                    "        this.id = id;\n" +
                    "    }\n" +
                    "    public void updateId() {\n" +
                    "        this.id = \"Person from version 2\";\n" +
                    "    }\n" +
                    "}";

    private final String person_drl =
            "package org.drools.mvel.compiler.test\n" +
                    "import org.drools.mvel.compiler.test.PersonObject;\n" +
                    "\n" +
                    "rule \"Update person's id\"\n" +
                    "when\n" +
                    "    $person : PersonObject()\n" +
                    "then\n" +
                    "    $person.updateId();\n" +
                    "    delete($person);\n" +
                    "end";

    private KieContainer kieContainer;
    private KieSession ksession;

    private boolean done = false;

    @Test
    public void testKJarUpgrade() throws Exception {
        // DROOLS-919
        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, drl1, drl2_1);

        // Create a session and fire rules
        kieContainer = ks.newKieContainer( km.getReleaseId() );
        ksession = kieContainer.newKieSession();

        ksession.setGlobal( "test", this );
        ksession.insert( new Message( "Hi Universe" ) );
        ksession.fireAllRules();

        assertThat(done).isTrue();
    }

    @Test
    public void testKJarUpgradeWithJavaClass() throws Exception {

        KieServices ks = KieServices.Factory.get();

        String kmodule = "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                "         xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                "  <kbase name=\"kbase1\">\n" +
                "    <ksession name=\"ksession1\" default=\"true\"/>\n" +
                "  </kbase>\n" +
                "</kmodule>";

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade-java", "1.0.0" );
        Resource javaResource = ResourceFactory.newByteArrayResource(javaSrc.getBytes()).setResourceType( ResourceType.JAVA )
                .setSourcePath( "org/drools/mvel/compiler/test/PersonObject.java" );
        Resource drlResource = ResourceFactory.newByteArrayResource( person_drl.getBytes() ).setResourceType( ResourceType.DRL )
                .setSourcePath( "kbase1/person.drl" );
        KieFileSystem kfs = KieUtil.getKieFileSystemWithKieModule(KieModuleModelImpl.fromXML(kmodule), releaseId1, javaResource, drlResource);
        KieModule km = KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, kfs);

        // Create a session and fire rules
        kieContainer = ks.newKieContainer( km.getReleaseId() );
        ksession = kieContainer.newKieSession();

        Class<?> clazz = kieContainer.getClassLoader().loadClass("org.drools.mvel.compiler.test.PersonObject");
        Object person = clazz.newInstance();

        ksession.insert( person );
        ksession.fireAllRules();

        assertThat(person).isNotNull();
        Object personId = valueOf(person, "id");
        assertThat(personId).isNotNull();
        assertThat(personId).isEqualTo("Person from version 1");

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade-java", "1.1.0" );
        Resource javaResource2 = ResourceFactory.newByteArrayResource(javaSrc_2.getBytes()).setResourceType( ResourceType.JAVA )
                .setSourcePath( "org/drools/mvel/compiler/test/PersonObject.java" );
        Resource drlResource2 = ResourceFactory.newByteArrayResource( person_drl.getBytes() ).setResourceType( ResourceType.DRL )
                .setSourcePath( "kbase1/person.drl" );
        KieFileSystem kfs2 = KieUtil.getKieFileSystemWithKieModule(KieModuleModelImpl.fromXML(kmodule), releaseId2, javaResource2, drlResource2);
        KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, kfs2);

        // update container
        kieContainer.updateToVersion(releaseId2);
        assertThat(kieContainer.getReleaseId()).isEqualTo(releaseId2);
        // now let's run the rules
        ksession = kieContainer.newKieSession();

        person = kieContainer.getClassLoader().loadClass("org.drools.mvel.compiler.test.PersonObject").newInstance();

        ksession.insert( person );
        ksession.fireAllRules();

        assertThat(person).isNotNull();
        personId = valueOf(person, "id");
        assertThat(personId).isNotNull();
        assertThat(personId).isEqualTo("Person from version 2");
    }

    public void updateToVersion() {
        KieServices ks = KieServices.Factory.get();

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        KieModule km = KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, drl1, drl2_2);

        // try to update the container to version 1.1.0
        kieContainer.updateToVersion( releaseId2 );

        // create and use a new session
        ksession.insert( new Message( "Hello World" ) );
    }

    public void done() {
        done = true;
    }

    protected Object valueOf(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            return null;
        }
    }
}
