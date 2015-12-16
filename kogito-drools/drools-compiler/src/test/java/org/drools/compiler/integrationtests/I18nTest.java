/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.I18nPerson;
import org.drools.compiler.Person;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests DRL's with foreign characters.
 */
public class I18nTest extends CommonTestMethodBase {

    private static Logger logger = LoggerFactory.getLogger(I18nTest.class);

    @Test @Ignore("Fails because of JBRULES-3435. But the JBRULES-2853 part works fine. Support for i18n properties must be fixed in mvel")
    public void readDrlInEncodingUtf8() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_I18nPerson_utf8.drl", "UTF-8", getClass() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

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

        assertTrue(list.contains("garçon"));
        assertTrue(list.contains("élève"));
        assertTrue(list.contains("имя"));
        assertTrue(list.contains("名称"));
        ksession.dispose();
    }

    @Test
    public void readDrlInEncodingLatin1() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_I18nPerson_latin1.drl.latin1", "ISO-8859-1", getClass() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list",
                list );

        I18nPerson i18nPerson = new I18nPerson();
        i18nPerson.setGarçon("Value 1");
//        i18nPerson.setÉlève("Value 2");
        ksession.insert(i18nPerson);
        ksession.fireAllRules();

        assertTrue(list.contains("garçon"));
//        assertTrue(list.contains("élève"));
        ksession.dispose();
    }

    @Test
    public void testIdeographicSpaceInDSL() throws Exception {
        // JBRULES-3723
        String dsl =
                "// Testing 'IDEOGRAPHIC SPACE' (U+3000)\n" +
                "[when]名前が {firstName}=Person(name==\"山本　{firstName}\")\n" +
                "[then]メッセージ {message}=messages.add(\"メッセージ　\" + {message});";

        String dslr =
                "package test\n" +
                "\n" +
                "import org.drools.compiler.Person\n" +
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        // Here I should explicitly set "UTF-8" because String.getBytes() depends on platform encoding and is not dealt by Drools side.
        kbuilder.add( ResourceFactory.newByteArrayResource(dsl.getBytes("UTF-8")), ResourceType.DSL );
        kbuilder.add( ResourceFactory.newByteArrayResource( dslr.getBytes("UTF-8") ), ResourceType.DSLR );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List messages = new ArrayList();
        ksession.setGlobal( "messages", messages );

        Person person = new Person();
        person.setName("山本　太郎");
        ksession.insert(person);
        ksession.fireAllRules();

        assertTrue(messages.contains("メッセージ　ルールにヒットしました"));

        ksession.dispose();
    }

    @Test
    public void testNewClassPathResource() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        // newClassPathResource without specifying encoding
        kbuilder.add( ResourceFactory.newClassPathResource( "test_I18nPerson_utf8_forTestNewClassPathResource.drl", getClass() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        I18nPerson i18nPerson = new I18nPerson();
        i18nPerson.set名称("山田花子");
        ksession.insert(i18nPerson);
        ksession.fireAllRules();

        assertTrue(list.contains("名称は山田花子です"));

        ksession.dispose();
    }

    @Test
    public void testKieFileSystem() {
        String str = "package org.drools.compiler.i18ntest;\n" +
                "import org.drools.compiler.I18nPerson;\n" +
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
        ks.newKieBuilder( kfs ).buildAll();
        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        I18nPerson i18nPerson = new I18nPerson();
        i18nPerson.set名称("山田花子");
        ksession.insert(i18nPerson);
        ksession.fireAllRules();

        assertTrue(list.contains("名称は山田花子です"));

        ksession.dispose();
    }

    @Test
    public void testKieModuleJar() {
        String str = "package org.drools.compiler.i18ntest;\n" +
                "import org.drools.compiler.I18nPerson;\n" +
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
        byte[] jar = createKJar(ks, releaseId, null, str);
        KieModule km = deployJar(ks, jar);
        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieSession ksession = kc.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        I18nPerson i18nPerson = new I18nPerson();
        i18nPerson.set名称("山田花子");
        ksession.insert(i18nPerson);
        ksession.fireAllRules();

        assertTrue(list.contains("名称は山田花子です"));

        ksession.dispose();
    }
}
