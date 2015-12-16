/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;


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

    @Test
    public void testEnums() throws Exception {

        StatefulKnowledgeSession ksession = genSession( "test_Enums.drl" );
        java.util.List list = new java.util.ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertTrue( list.contains( 4 ) );
        assertTrue( list.contains( 5.976e+24 ) );
        assertTrue( list.contains( "Mercury" ) );

        ksession.dispose();


    }

    @Test
    public void testEnumsWithCompositeBuildingProcess() throws Exception {
        String drl = "package org.test; " +
                     "" +
                     "declare enum DaysOfWeek " +
                     "    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;\n" +
                     "end\n" +

                     "declare Test " +
                     "  field: DaysOfWeek " +
                     "end";

        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent( drl, ResourceType.DRL );
        Results res = kieHelper.verify();
        assertEquals( 0, res.getMessages().size() );
    }


    @Test
    public void testQueryEnum() {
        String str = "package org.kie.test;\n" +
                "\n" +
                "declare enum Ennumm\n" +
                "  ONE, TWO;\n" +
                "end\n" +
                "\n" +
                "declare Bean\n" +
                "  fld : Ennumm\n" +
                "end\n" +
                "\n" +
                "query seeWhat( Ennumm $e, Bean $b )\n" +
                "  $b := Bean( $e == Ennumm.ONE )\n" +
                "end\n" +
                "\n" +
                "rule rool\n" +
                "when\n" +
                "then\n" +
                "  insert( new Bean( Ennumm.ONE ) );\n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule rool2\n" +
                "when\n" +
                "  seeWhat( $ex, $bx ; )\n" +
                "then\n" +
                "  System.out.println( $bx );\n" +
                "end";

        String str2 = "package org.drools.compiler.test2; \n" +
                        "" +
                        "declare Naeb \n" +
                        "   fld : String \n" +
                        "end \n";


        KnowledgeBuilder kbuilder =  KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );



        KnowledgeBuilder kbuilder2 =  KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder2.add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL );

        if ( kbuilder2.hasErrors() ) {
            fail( kbuilder2.getErrors().toString() );
        }
        kbase.addKnowledgePackages( kbuilder2.getKnowledgePackages() );


        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        kbuilder.add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL );


        ksession.fireAllRules();

        ksession.dispose();
    }




}

