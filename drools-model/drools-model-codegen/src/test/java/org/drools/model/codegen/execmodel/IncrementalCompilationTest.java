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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drools.model.codegen.execmodel.domain.Address;
import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.AlphaNetworkCompilerOption;

import static org.assertj.core.api.Assertions.assertThat;

public class IncrementalCompilationTest extends BaseModelTest {

    public IncrementalCompilationTest( RUN_TYPE testRunType ) {
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
    public void testKJarUpgradeSameAndDifferentSessions() throws Exception {
        String drl1 = "package org.drools.incremental\n" +
                "import " + Message.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "   $m : Message( value.startsWith(\"H\") )\n" +
                "then\n" +
                "   System.out.println($m.getValue());" +
                "end\n";

        String drl2_1 = "package org.drools.incremental\n" +
                "import " + Message.class.getCanonicalName() + ";\n" +
                "rule R2 when\n" +
                "   $m : Message( value == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl2_2 = "package org.drools.incremental\n" +
                "import " + Message.class.getCanonicalName() + ";\n" +
                "rule R2 when\n" +
                "   $m : Message( value == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        createAndDeployJar( ks, releaseId1, drl1, drl2_1 );

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, releaseId2, drl1, drl2_2 );

        // try to update the container to version 1.1.0
        kc.updateToVersion( releaseId2 );

        // continue working with the session
        ksession.insert( new Message( "Hello World" ) );
        assertThat(ksession.fireAllRules()).isEqualTo(3);

        // try with a new session
        KieSession ksession2 = kc.newKieSession();
        ksession2.insert( new Message( "Hello World" ) );
        assertThat(ksession2.fireAllRules()).isEqualTo(2);
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
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, kieModuleModel, releaseId2, drl1, drl2_2 );

        // try to update the container to version 1.1.0
        kc.updateToVersion( releaseId2 );

        // try with a new session
        KieSession ksession2 = kc.newKieSession();
        assertThat(ksession2.fireAllRules()).isEqualTo(3);

        // Create a new jar for version 1.2.0
        ReleaseId releaseId3 = ks.newReleaseId( "org.kie", "test-upgrade", "1.2.0" );
        createAndDeployJar( ks, kieModuleModel, releaseId3, drl1, drl2_3 );

        // try to update the container to version 1.2.0
        kc.updateToVersion( releaseId3 );
        KieSession kieSession3 = kc.newKieSession();

        List<String> list = new ArrayList<>();
        ksession2.setGlobal( "list", list );
        ksession2.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Hello World");
    }

    @Test
    public void testKJarUpgradeWithChangedDeclaredType() throws Exception {
        String drl1a = "package org.drools.incremental\n" +
                "declare Message value : String end\n" +
                "rule Init when then insert(new Message( \"Hello World\" )); end\n" +
                "rule R1 when\n" +
                "   $m : Message( value.startsWith(\"H\") )\n" +
                "then\n" +
                "   System.out.println($m.getValue());" +
                "end\n";

        String drl1b = "package org.drools.incremental\n" +
                "declare Message msg : String end\n" +
                "rule Init when then insert(new Message( \"Hello World\" )); end\n" +
                "rule R1 when\n" +
                "   $m : Message( msg.startsWith(\"H\") )\n" +
                "then\n" +
                "   System.out.println($m.getMsg());" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        createAndDeployJar( ks, releaseId1, drl1a );

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession ksession = kc.newKieSession();
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, releaseId2, drl1b );

        // try to update the container to version 1.1.0
        kc.updateToVersion( releaseId2 );

        // continue working with the session
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        // try with a new session
        KieSession ksession2 = kc.newKieSession();
        assertThat(ksession2.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testIdenticalConsequenceButImportChange() {
        final String drl1 = "package org.drools.test\n" +
                    "import " + Person.class.getCanonicalName() + "\n" +
                    "rule R\n" +
                    "    when\n" +
                    "       $p: Person ()\n" +
                    "    then\n" +
                    "        System.out.println(\"$p.getAge() = \" + $p.getAge());\n" +
                    "end";

        final String drl2 = "package org.drools.test\n" +
                    "import " + Person.class.getCanonicalName() + "\n" +
                    "import " + Address.class.getCanonicalName() + "\n" +
                    "rule R\n" +
                    "    when\n" +
                    "       $p: Person (name == \"Paul\")\n" +
                    "       $a: Address ()\n" +
                    "    then\n" +
                    "        System.out.println(\"$p.getAge() = \" + $p.getAge());\n" +
                    "end\n";

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        createAndDeployJar(ks, releaseId1, drl1);

        final KieContainer kc = ks.newKieContainer(releaseId1);
        final KieSession kieSession = kc.newKieSession();

        Person john = new Person("John", 40);
        kieSession.insert(john);
        assertThat(kieSession.fireAllRules()).isEqualTo(1);

        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "2.0.0");
        createAndDeployJar(ks, releaseId2, drl2);

        kc.updateToVersion(releaseId2);

        Person paul = new Person("Paul", 35);
        kieSession.insert(paul);
        final Address address = new Address();
        kieSession.insert(address);
        assertThat(kieSession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testIdenticalPredicateButImportChange() {
        // This test also verifies LambdaExtractor
        final String drl1 = "package org.drools.test\n" +
                    "import " + Person.class.getCanonicalName() + "\n" +
                    "rule R\n" +
                    "    when\n" +
                    "       $p: Person (name == \"Paul\")\n" +
                    "    then\n" +
                    "        System.out.println(\"Hello\");\n" +
                    "end";

        final String drl2 = "package org.drools.test\n" +
                    "import " + Person.class.getCanonicalName() + "\n" +
                    "import " + Address.class.getCanonicalName() + "\n" +
                    "rule R\n" +
                    "    when\n" +
                    "       $p: Person (name == \"Paul\")\n" +
                    "       $a: Address ()\n" +
                    "    then\n" +
                    "        System.out.println(\"$p.getAge() = \" + $p.getAge());\n" +
                    "end\n";

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        createAndDeployJar(ks, releaseId1, drl1);

        final KieContainer kc = ks.newKieContainer(releaseId1);
        final KieSession kieSession = kc.newKieSession();

        Person john = new Person("John", 40);
        kieSession.insert(john);
        assertThat(kieSession.fireAllRules()).isEqualTo(0);

        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "2.0.0");
        createAndDeployJar(ks, releaseId2, drl2);

        kc.updateToVersion(releaseId2);

        Person paul = new Person("Paul", 35);
        kieSession.insert(paul);
        final Address address = new Address();
        kieSession.insert(address);
        assertThat(kieSession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testKJarUpgradeWithChangedFunctionForRHSWithoutExternalizeLambda() throws Exception {

        String drl1a = "package org.drools.incremental\n" +
                       "import " + Person.class.getCanonicalName() + "\n" +
                       "global java.util.List list;\n" +
                       "function String hello(String name) { return \"Hello \" + name; }\n" +
                       "rule R1 when\n" +
                       "   $p : Person()\n" +
                       "then\n" +
                       "   list.add(hello($p.getName()));" +
                       "end\n";

        String drl1b = "package org.drools.incremental\n" +
                       "import " + Person.class.getCanonicalName() + "\n" +
                       "global java.util.List list;\n" +
                       "function String hello(String name) { return \"Good bye \" + name; }\n" +
                       "rule R1 when\n" +
                       "   $p : Person()\n" +
                       "then\n" +
                       "   list.add(hello($p.getName()));" +
                       "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieModuleModel model1 = ks.newKieModuleModel();
        model1.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.FALSE.toString());
        createAndDeployJar(ks, model1, releaseId1, drl1a);

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer(releaseId1);
        KieSession ksession = kc.newKieSession();
        ArrayList<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.insert(new Person("John"));

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(list).containsExactlyInAnyOrder("Hello John");

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.0");
        KieModuleModel model2 = ks.newKieModuleModel();
        model2.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.FALSE.toString());
        createAndDeployJar(ks, model2, releaseId2, drl1b);

        // try to update the container to version 1.1.0
        kc.updateToVersion(releaseId2);

        ksession.insert(new Person("Paul"));

        assertThat(ksession.fireAllRules()).isEqualTo(2);
        assertThat(list).containsExactlyInAnyOrder("Hello John", "Good bye Paul", "Good bye John");

        // try with a new session
        KieSession ksession2 = kc.newKieSession();
        ArrayList<String> list2 = new ArrayList<>();
        ksession2.setGlobal("list", list2);
        ksession2.insert(new Person("George"));

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        assertThat(list2).containsExactlyInAnyOrder("Good bye George");
    }

    @Test
    public void testKJarUpgradeWithChangedFunctionForRHSWithExternalizeLambda() throws Exception {

        String drl1a = "package org.drools.incremental\n" +
                       "import " + Person.class.getCanonicalName() + "\n" +
                       "global java.util.List list;\n" +
                       "function String hello(String name) { return \"Hello \" + name; }\n" +
                       "rule R1 when\n" +
                       "   $p : Person()\n" +
                       "then\n" +
                       "   list.add(hello($p.getName()));" +
                       "end\n";

        String drl1b = "package org.drools.incremental\n" +
                       "import " + Person.class.getCanonicalName() + "\n" +
                       "global java.util.List list;\n" +
                       "function String hello(String name) { return \"Good bye \" + name; }\n" +
                       "rule R1 when\n" +
                       "   $p : Person()\n" +
                       "then\n" +
                       "   list.add(hello($p.getName()));" +
                       "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieModuleModel model1 = ks.newKieModuleModel();
        model1.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.TRUE.toString());
        createAndDeployJar(ks, model1, releaseId1, drl1a);

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer(releaseId1);
        KieSession ksession = kc.newKieSession();
        ArrayList<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.insert(new Person("John"));

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(list).containsExactlyInAnyOrder("Hello John");

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.0");
        KieModuleModel model2 = ks.newKieModuleModel();
        model2.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.TRUE.toString());
        createAndDeployJar(ks, model2, releaseId2, drl1b);

        // try to update the container to version 1.1.0
        kc.updateToVersion(releaseId2);

        ksession.insert(new Person("Paul"));

        assertThat(ksession.fireAllRules()).isEqualTo(2);
        assertThat(list).containsExactlyInAnyOrder("Hello John", "Good bye Paul", "Good bye John");

        // try with a new session
        KieSession ksession2 = kc.newKieSession();
        ArrayList<String> list2 = new ArrayList<>();
        ksession2.setGlobal("list", list2);
        ksession2.insert(new Person("George"));

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        assertThat(list2).containsExactlyInAnyOrder("Good bye George");
    }

    @Test
    public void testKJarUpgradeWithChangedJavaFunctionForRHSWithoutExternalizeLambda() throws Exception {

        String java1 = "package org.drools.test;\n" +
                       "public class MyFunction {\n" +
                       "    public static String hello(String name) {\n" +
                       "        return \"Hello \" + name;\n" +
                       "    }\n" +
                       "}";

        String java2 = "package org.drools.test;\n" +
                       "public class MyFunction {\n" +
                       "    public static String hello(String name) {\n" +
                       "        return \"Good bye \" + name;\n" +
                       "    }\n" +
                       "}";

        String drl = "package org.drools.incremental\n" +
                     "import " + Person.class.getCanonicalName() + "\n" +
                     "import static org.drools.test.MyFunction.hello\n" +
                     "global java.util.List list;\n" +
                     "rule R1 when\n" +
                     "   $p : Person()\n" +
                     "then\n" +
                     "   list.add(hello($p.getName()));" +
                     "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieModuleModel model1 = ks.newKieModuleModel();
        model1.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.FALSE.toString());
        createAndDeployJar(ks, model1, releaseId1,
                           new KieFile("src/main/java/org/drools/test/MyFunction.java", java1),
                           new KieFile("src/main/resources/org/drools/incremental/r1.drl", drl));

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer(releaseId1);
        KieSession ksession = kc.newKieSession();
        ArrayList<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.insert(new Person("John"));

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(list).containsExactlyInAnyOrder("Hello John");

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.0");
        KieModuleModel model2 = ks.newKieModuleModel();
        model2.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.FALSE.toString());
        createAndDeployJar(ks, model2, releaseId2,
                           new KieFile("src/main/java/org/drools/test/MyFunction.java", java2),
                           new KieFile("src/main/resources/org/drools/incremental/r1.drl", drl));
        // try to update the container to version 1.1.0
        kc.updateToVersion(releaseId2);

        ksession.insert(new Person("Paul"));

        assertThat(ksession.fireAllRules()).isEqualTo(2);
        assertThat(list).containsExactlyInAnyOrder("Hello John", "Good bye Paul", "Good bye John");

        // try with a new session
        KieSession ksession2 = kc.newKieSession();
        ArrayList<String> list2 = new ArrayList<>();
        ksession2.setGlobal("list", list2);
        ksession2.insert(new Person("George"));

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        assertThat(list2).containsExactlyInAnyOrder("Good bye George");
    }

    @Test
    public void testKJarUpgradeWithChangedJavaFunctionForRHSWithExternalizeLambda() throws Exception {

        String java1 = "package org.drools.test;\n" +
                       "public class MyFunction {\n" +
                       "    public static String hello(String name) {\n" +
                       "        return \"Hello \" + name;\n" +
                       "    }\n" +
                       "}";

        String java2 = "package org.drools.test;\n" +
                       "public class MyFunction {\n" +
                       "    public static String hello(String name) {\n" +
                       "        return \"Good bye \" + name;\n" +
                       "    }\n" +
                       "}";

        String drl = "package org.drools.incremental\n" +
                     "import " + Person.class.getCanonicalName() + "\n" +
                     "import static org.drools.test.MyFunction.hello\n" +
                     "global java.util.List list;\n" +
                     "rule R1 when\n" +
                     "   $p : Person()\n" +
                     "then\n" +
                     "   list.add(hello($p.getName()));" +
                     "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieModuleModel model1 = ks.newKieModuleModel();
        model1.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.TRUE.toString());
        createAndDeployJar(ks, model1, releaseId1,
                           new KieFile("src/main/java/org/drools/test/MyFunction.java", java1),
                           new KieFile("src/main/resources/org/drools/incremental/r1.drl", drl));

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer(releaseId1);
        KieSession ksession = kc.newKieSession();
        ArrayList<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.insert(new Person("John"));

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(list).containsExactlyInAnyOrder("Hello John");

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.0");
        KieModuleModel model2 = ks.newKieModuleModel();
        model2.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.TRUE.toString());
        createAndDeployJar(ks, model2, releaseId2,
                           new KieFile("src/main/java/org/drools/test/MyFunction.java", java2),
                           new KieFile("src/main/resources/org/drools/incremental/r1.drl", drl));

        // try to update the container to version 1.1.0
        kc.updateToVersion(releaseId2);

        ksession.insert(new Person("Paul"));

        assertThat(ksession.fireAllRules()).isEqualTo(2);
        assertThat(list).containsExactlyInAnyOrder("Hello John", "Good bye Paul", "Good bye John");

        // try with a new session
        KieSession ksession2 = kc.newKieSession();
        ArrayList<String> list2 = new ArrayList<>();
        ksession2.setGlobal("list", list2);
        ksession2.insert(new Person("George"));

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        assertThat(list2).containsExactlyInAnyOrder("Good bye George");
    }

    @Test
    public void testKJarUpgradeWithChangedFunctionForLHSWithoutExternalizeLambda() throws Exception {

        String drl1a = "package org.drools.incremental\n" +
                       "import " + Person.class.getCanonicalName() + "\n" +
                       "global java.util.List list;\n" +
                       "function String hello(String name) { System.out.println(\"function 1.0.0 is called\"); return \"XXX\" + name; }\n" +
                       "rule R1 when\n" +
                       "   $p : Person(hello(name) == \"XXXJohn\")\n" +
                       "then\n" +
                       "   list.add($p.getName());" +
                       "end\n";

        String drl1b = "package org.drools.incremental\n" +
                       "import " + Person.class.getCanonicalName() + "\n" +
                       "global java.util.List list;\n" +
                       "function String hello(String name) { System.out.println(\"function 1.1.0 is called\"); return \"XXX\" + name; }\n" +
                       "rule R1 when\n" +
                       "   $p : Person(hello(name) == \"XXXJohn\")\n" +
                       "then\n" +
                       "   list.add($p.getName());" +
                       "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieModuleModel model1 = ks.newKieModuleModel();
        model1.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.FALSE.toString());
        createAndDeployJar(ks, model1, releaseId1, drl1a);

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer(releaseId1);
        KieSession ksession = kc.newKieSession();
        ArrayList<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.insert(new Person("John"));

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(list).containsExactlyInAnyOrder("John");

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.0");
        KieModuleModel model2 = ks.newKieModuleModel();
        model2.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.FALSE.toString());
        createAndDeployJar(ks, model2, releaseId2, drl1b);

        // try to update the container to version 1.1.0
        System.out.println("=== updateToVersion ===");
        kc.updateToVersion(releaseId2);

        ksession.insert(new Person("Paul"));

        // re-fire
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(list).containsExactlyInAnyOrder("John", "John");

        // try with a new session
        KieSession ksession2 = kc.newKieSession();
        ArrayList<String> list2 = new ArrayList<>();
        ksession2.setGlobal("list", list2);
        ksession2.insert(new Person("John"));

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        assertThat(list2).containsExactlyInAnyOrder("John");
    }

    @Test
    public void testKJarUpgradeWithChangedFunctionForLHSWithExternalizeLambda() throws Exception {

        String drl1a = "package org.drools.incremental\n" +
                       "import " + Person.class.getCanonicalName() + "\n" +
                       "global java.util.List list;\n" +
                       "function String hello(String name) { System.out.println(\"function 1.0.0 is called\"); return \"XXX\" + name; }\n" +
                       "rule R1 when\n" +
                       "   $p : Person(hello(name) == \"XXXJohn\")\n" +
                       "then\n" +
                       "   list.add($p.getName());" +
                       "end\n";

        String drl1b = "package org.drools.incremental\n" +
                       "import " + Person.class.getCanonicalName() + "\n" +
                       "global java.util.List list;\n" +
                       "function String hello(String name) { System.out.println(\"function 1.1.0 is called\"); return \"XXX\" + name; }\n" +
                       "rule R1 when\n" +
                       "   $p : Person(hello(name) == \"XXXJohn\")\n" +
                       "then\n" +
                       "   list.add($p.getName());" +
                       "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieModuleModel model1 = ks.newKieModuleModel();
        model1.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.TRUE.toString());
        createAndDeployJar(ks, model1, releaseId1, drl1a);

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer(releaseId1);
        KieSession ksession = kc.newKieSession();
        ArrayList<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.insert(new Person("John"));

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(list).containsExactlyInAnyOrder("John");

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.0");
        KieModuleModel model2 = ks.newKieModuleModel();
        model2.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.TRUE.toString());
        createAndDeployJar(ks, model2, releaseId2, drl1b);

        // try to update the container to version 1.1.0
        System.out.println("=== updateToVersion ===");
        kc.updateToVersion(releaseId2);

        ksession.insert(new Person("Paul"));

        // re-fire
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(list).containsExactlyInAnyOrder("John", "John");

        // try with a new session
        KieSession ksession2 = kc.newKieSession();
        ArrayList<String> list2 = new ArrayList<>();
        ksession2.setGlobal("list", list2);
        ksession2.insert(new Person("John"));

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        assertThat(list2).containsExactlyInAnyOrder("John");
    }

    @Test
    public void testKJarUpgradeWithChangedJavaFunctionForLHSWithoutExternalizeLambda() throws Exception {

        String java1 = "package org.drools.test;\n" +
                       "public class MyFunction {\n" +
                       "    public static String hello(String name) {\n" +
                       "        System.out.println(\"function 1.0.0 is called\");" +
                       "        return \"XXX\" + name;\n" +
                       "    }\n" +
                       "}";

        String java2 = "package org.drools.test;\n" +
                       "public class MyFunction {\n" +
                       "    public static String hello(String name) {\n" +
                       "        System.out.println(\"function 1.1.0 is called\");" +
                       "        return \"XXX\" + name;\n" +
                       "    }\n" +
                       "}";

        String drl = "package org.drools.incremental\n" +
                       "import " + Person.class.getCanonicalName() + "\n" +
                       "import static org.drools.test.MyFunction.hello\n" +
                       "global java.util.List list;\n" +
                       "rule R1 when\n" +
                       "   $p : Person(hello(name) == \"XXXJohn\")\n" +
                       "then\n" +
                       "   list.add($p.getName());" +
                       "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieModuleModel model1 = ks.newKieModuleModel();
        model1.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.FALSE.toString());
        createAndDeployJar(ks, model1, releaseId1,
                           new KieFile("src/main/java/org/drools/test/MyFunction.java", java1),
                           new KieFile("src/main/resources/org/drools/incremental/r1.drl", drl));

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer(releaseId1);
        KieSession ksession = kc.newKieSession();
        ArrayList<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.insert(new Person("John"));

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(list).containsExactlyInAnyOrder("John");

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.0");
        KieModuleModel model2 = ks.newKieModuleModel();
        model2.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.FALSE.toString());
        createAndDeployJar(ks, model2, releaseId2,
                           new KieFile("src/main/java/org/drools/test/MyFunction.java", java2),
                           new KieFile("src/main/resources/org/drools/incremental/r1.drl", drl));

        // try to update the container to version 1.1.0
        System.out.println("=== updateToVersion ===");
        kc.updateToVersion(releaseId2);

        ksession.insert(new Person("Paul"));

        // re-fire
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(list).containsExactlyInAnyOrder("John", "John");

        // try with a new session
        KieSession ksession2 = kc.newKieSession();
        ArrayList<String> list2 = new ArrayList<>();
        ksession2.setGlobal("list", list2);
        ksession2.insert(new Person("John"));

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        assertThat(list2).containsExactlyInAnyOrder("John");
    }

    @Test
    public void testKJarUpgradeWithChangedJavaFunctionForLHSWithExternalizeLambda() throws Exception {

        String java1 = "package org.drools.test;\n" +
                       "public class MyFunction {\n" +
                       "    public static String hello(String name) {\n" +
                       "        System.out.println(\"function 1.0.0 is called\");" +
                       "        return \"XXX\" + name;\n" +
                       "    }\n" +
                       "}";

        String java2 = "package org.drools.test;\n" +
                       "public class MyFunction {\n" +
                       "    public static String hello(String name) {\n" +
                       "        System.out.println(\"function 1.1.0 is called\");" +
                       "        return \"XXX\" + name;\n" +
                       "    }\n" +
                       "}";

        String drl = "package org.drools.incremental\n" +
                       "import " + Person.class.getCanonicalName() + "\n" +
                       "import static org.drools.test.MyFunction.hello\n" +
                       "global java.util.List list;\n" +
                       "rule R1 when\n" +
                       "   $p : Person(hello(name) == \"XXXJohn\")\n" +
                       "then\n" +
                       "   list.add($p.getName());" +
                       "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieModuleModel model1 = ks.newKieModuleModel();
        model1.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.TRUE.toString());
        createAndDeployJar(ks, model1, releaseId1,
                           new KieFile("src/main/java/org/drools/test/MyFunction.java", java1),
                           new KieFile("src/main/resources/org/drools/incremental/r1.drl", drl));

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer(releaseId1);
        KieSession ksession = kc.newKieSession();
        ArrayList<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.insert(new Person("John"));

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(list).containsExactlyInAnyOrder("John");

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.0");
        KieModuleModel model2 = ks.newKieModuleModel();
        model2.setConfigurationProperty("drools.externaliseCanonicalModelLambda", Boolean.TRUE.toString());
        createAndDeployJar(ks, model2, releaseId2,
                           new KieFile("src/main/java/org/drools/test/MyFunction.java", java2),
                           new KieFile("src/main/resources/org/drools/incremental/r1.drl", drl));

        // try to update the container to version 1.1.0
        System.out.println("=== updateToVersion ===");
        kc.updateToVersion(releaseId2);

        ksession.insert(new Person("Paul"));

        // re-fire
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(list).containsExactlyInAnyOrder("John", "John");

        // try with a new session
        KieSession ksession2 = kc.newKieSession();
        ArrayList<String> list2 = new ArrayList<>();
        ksession2.setGlobal("list", list2);
        ksession2.insert(new Person("John"));

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        assertThat(list2).containsExactlyInAnyOrder("John");
    }
}
