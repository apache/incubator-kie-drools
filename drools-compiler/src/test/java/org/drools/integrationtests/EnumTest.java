/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.integrationtests;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.util.codec.Base64;
import org.junit.Ignore;
import org.junit.Test;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.compiler.CompiledAccExpression;

import java.io.*;


/**
 * Test for declared Enums
 */
public class EnumTest extends CommonTestMethodBase {



    public StatefulKnowledgeSession genSession(String source) {
        return genSession(new String[] {source},0);
    }

    public StatefulKnowledgeSession genSession(String source, int numerrors)  {

        return genSession(new String[] {source},numerrors);
    }


    public StatefulKnowledgeSession genSession(String[] sources, int numerrors)  {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (String source : sources)
            kbuilder.add( ResourceFactory.newClassPathResource(source, getClass()), ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( kbuilder.getErrors().size() > 0 ) {
            for ( KnowledgeBuilderError error : kbuilder.getErrors() ) {
                System.err.println( error );
            }
        }
        assertEquals(numerrors, errors.size() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        return createKnowledgeSession(kbase);

    }

    @Test @Ignore
    public void testEnums() throws Exception {

        StatefulKnowledgeSession ksession = genSession("test_Enums.drl");
        java.util.List list = new java.util.ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        System.out.println(list);
        assertTrue( list.contains( 4 ) );
        assertTrue( list.contains( 5.976e+24 ) );
        assertTrue( list.contains( "Mercury" ) );

        ksession.dispose();


    }


    public void x()   {
        ParserConfiguration pc = null;
        MVEL.executeExpression( MVEL.compileExpression( "xx", new ParserContext( pc ) ) );
    }


}

