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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.drools.mvel.compiler.I18nPerson;
import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests DRL's with foreign characters.
 */
public class I18nTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    @Disabled("Fails because of JBRULES-3435. But the JBRULES-2853 part works fine. Support for i18n properties must be fixed in mvel")
    public void readDrlInEncodingUtf8(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        final Resource drlResource = ResourceFactory.newClassPathResource( "test_I18nPerson_utf8.drl", "UTF-8", getClass());
        drlResource.setResourceType(ResourceType.DRL);
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromResources("i18n-test", kieBaseTestConfiguration, drlResource);
        final KieSession ksession = kbase.newKieSession();


        List list = new ArrayList();
        ksession.setGlobal( "list",
                list );

        I18nPerson i18nPerson = new I18nPerson();
        i18nPerson.setGarçon("Value 1");
        i18nPerson.setÉlève("Value 2");
        i18nPerson.setИмя("Value 3");
        i18nPerson.set名称("Value 4");
        ksession.insert(i18nPerson);
        ksession.fireAllRules();

        assertThat(list.contains("garçon")).isTrue();
        assertThat(list.contains("élève")).isTrue();
        assertThat(list.contains("имя")).isTrue();
        assertThat(list.contains("名称")).isTrue();
        ksession.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void readDrlInEncodingLatin1(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        final Resource drlResource = ResourceFactory.newClassPathResource( "test_I18nPerson_latin1.drl.latin1", "ISO-8859-1", getClass());
        drlResource.setResourceType(ResourceType.DRL);
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromResources("i18n-test", kieBaseTestConfiguration, drlResource);
        final KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list",
                list );

        I18nPerson i18nPerson = new I18nPerson();
        i18nPerson.setGarçon("Value 1");
//        i18nPerson.setÉlève("Value 2");
        ksession.insert(i18nPerson);
        ksession.fireAllRules();

        assertThat(list.contains("garçon")).isTrue();
//        assertTrue(list.contains("élève"));
        ksession.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testIdeographicSpaceInDSL(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        // JBRULES-3723
        String dsl =
                "// Testing 'IDEOGRAPHIC SPACE' (U+3000)\n" +
                "[when]名前が {firstName}=Person(name==\"山本　{firstName}\")\n" +
                "[then]メッセージ {message}=messages.add(\"メッセージ　\" + {message});";

        String dslr =
                "package test\n" +
                "\n" +
                "import org.drools.mvel.compiler.Person\n" +
                "\n" +
                "expander test_I18n.dsl\n" +
                "\n" +
                "global java.util.List messages;\n" +
                "\n" +
                "rule \"IDEOGRAPHIC SPACE test\"\n" +
                "    when\n" +
                "        // Person(name==\"山本　太郎\")\n" +
                "        名前が 太郎\n" +
                "    then\n" +
                "        // messages.add(\"メッセージ　ルールにヒットしました\");\n" +
                "         メッセージ \"ルールにヒットしました\"\n" +
                "end";

        final Resource dslResource = ResourceFactory.newByteArrayResource(dsl.getBytes("UTF-8"));
        dslResource.setResourceType(ResourceType.DSL);
        dslResource.setTargetPath("src/main/resources/test.dsl");
        final Resource dslrResource = ResourceFactory.newByteArrayResource(dslr.getBytes("UTF-8"));
        dslrResource.setResourceType(ResourceType.DSLR);
        dslrResource.setTargetPath("src/main/resources/test-rule.dslr");

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromResources("i18n-test", kieBaseTestConfiguration, dslResource, dslrResource);
        final KieSession ksession = kbase.newKieSession();

        List<String> messages = new ArrayList<>();
        ksession.setGlobal( "messages", messages );

        Person person = new Person();
        person.setName("山本　太郎");
        ksession.insert(person);
        ksession.fireAllRules();

        assertThat(messages.contains("メッセージ　ルールにヒットしました")).isTrue();

        ksession.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNewClassPathResource(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final Resource drl = ResourceFactory.newClassPathResource( "test_I18nPerson_utf8_forTestNewClassPathResource.drl", getClass());
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromResources("i18n-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        I18nPerson i18nPerson = new I18nPerson();
        i18nPerson.set名称("山田花子");
        ksession.insert(i18nPerson);
        ksession.fireAllRules();

        assertThat(list.contains("名称は山田花子です")).isTrue();

        ksession.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testKieFileSystem(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "package org.drools.mvel.compiler.i18ntest;\n" +
                "import org.drools.mvel.compiler.I18nPerson;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "rule \"名称 is 山田花子\"\n" +
                "    when\n" +
                "        p : I18nPerson( 名称 == \"山田花子\" )\n" +
                "    then\n" +
                "        list.add( \"名称は山田花子です\" );\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, true);
        assertThat(kieBuilder.buildAll().getResults().getMessages().isEmpty()).isTrue();

        ReleaseId releaseId = kieBuilder.getKieModule().getReleaseId();
        final KieContainer kieContainer = ks.newKieContainer(releaseId);
        final KieSession ksession = kieContainer.newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        I18nPerson i18nPerson = new I18nPerson();
        i18nPerson.set名称("山田花子");
        ksession.insert(i18nPerson);
        ksession.fireAllRules();

        assertThat(list.contains("名称は山田花子です")).isTrue();

        ksession.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testKieModuleJar(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "package org.drools.mvel.compiler.i18ntest;\n" +
                "import org.drools.mvel.compiler.I18nPerson;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "rule \"名称 is 山田花子\"\n" +
                "    when\n" +
                "        p : I18nPerson( 名称 == \"山田花子\" )\n" +
                "    then\n" +
                "        list.add( \"名称は山田花子です\" );\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "118ntest", "1.0.0");
        KieModule kmodule = KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, str);
        KieContainer kc = ks.newKieContainer(kmodule.getReleaseId());
        KieSession ksession = kc.newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        I18nPerson i18nPerson = new I18nPerson();
        i18nPerson.set名称("山田花子");
        ksession.insert(i18nPerson);
        ksession.fireAllRules();

        assertThat(list.contains("名称は山田花子です")).isTrue();

        ksession.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testMultibytePositonalQueryParam(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-1619
        String drl = "package org.drools.mvel.compiler.i18ntest;\n" +
                "import org.drools.mvel.compiler.Person;\n" +
                "\n" +
                "query testquery(int $a, Person $t)\n" +
                "    $t := Person(age > $a)\n" +
                "end\n" +
                "\n" +
                "rule \"hoge\"\n" +
                "    when\n" +
                "        testquery(30, $あああ;)\n" +
                "    then\n" +
                "        System.out.println($あああ.getName());\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("i18n-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();

        Person p1 = new Person("John", 25);
        Person p2 = new Person("Paul", 35);
        ksession.insert(p1);
        ksession.insert(p2);
        int fired = ksession.fireAllRules();

        assertThat(fired).isEqualTo(1);

        ksession.dispose();
    }
}
