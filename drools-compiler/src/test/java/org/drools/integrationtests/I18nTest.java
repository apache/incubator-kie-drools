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

package org.drools.integrationtests;

import org.drools.CommonTestMethodBase;
import org.drools.I18nPerson;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests DRL's with foreign characters.
 */
public class I18nTest extends CommonTestMethodBase {

    private static Logger logger = LoggerFactory.getLogger(I18nTest.class);

    @Test
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
        i18nPerson.setÉlève("Value 2");
        ksession.insert(i18nPerson);
        ksession.fireAllRules();

        assertTrue(list.contains("garçon"));
        assertTrue(list.contains("élève"));
        ksession.dispose();
    }

}
