/*
 * Copyright 2012 JBoss Inc
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
import org.kie.io.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests DRL's with foreign characters.
 */
public class I18nTest extends CommonTestMethodBase {

    private static Logger logger = LoggerFactory.getLogger(I18nTest.class);

    @Test @Ignore("Fails because of JBRULES-3435. But the JBRULES-2853 part works fine")
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
        kbuilder.add( ResourceFactory.newByteArrayResource(dsl.getBytes()), ResourceType.DSL );
        kbuilder.add( ResourceFactory.newByteArrayResource( dslr.getBytes() ), ResourceType.DSLR );
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
}
