/*
 * Copyright 2013 JBoss Inc
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

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.CompositeKnowledgeBuilder;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class RuleExtensionTest {

    @Test
    public void testRuleExtendsNonexistingRule() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Bas\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL );
        System.out.println(kbuilder.getErrors());
        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    public void testRuleExtendsBetweenDRLs() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "";

        String str2 =
                "package org.drools.test;\n" +

                        "\n" +
                        "rule \"ExtYes\" extends \"Base\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL );
        System.out.println(kbuilder.getErrors());
        assertFalse(kbuilder.hasErrors());

        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession knowledgeSession = kb.newStatefulKnowledgeSession();

        List list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();
        assertEquals(0, list.size());

        knowledgeSession.insert( "go" );
        knowledgeSession.fireAllRules();

        assertEquals( 1, list.size() );
    }


    @Test
    public void testRuleExtendsOnIncrementalKB() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "";

        String str2 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Base\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        assertFalse(kbuilder.hasErrors());

        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder( kb );
        kbuilder2.add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL );
        System.err.println(kbuilder2.getErrors());
        assertFalse(kbuilder2.hasErrors());

        StatefulKnowledgeSession knowledgeSession = kb.newStatefulKnowledgeSession();

        List list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();
        assertEquals( 0, list.size() );

        knowledgeSession.insert( "go" );
        knowledgeSession.fireAllRules();

        assertEquals( 1, list.size() );
    }

    @Test
    public void testRuleExtendsMissingOnIncrementalKB() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "";

        String str2 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Bse\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        assertFalse(kbuilder.hasErrors());

        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder( kb );
        kbuilder2.add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL );
        System.err.println( kbuilder2.getErrors() );
        assertTrue(kbuilder2.hasErrors());

    }



    @Test
    public void testRuleExtendsWithCompositeKBuilder() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "";

        String str2 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Base\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        CompositeKnowledgeBuilder ckb = kbuilder.batch();

        ckb
                .add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL )
                .build();

        System.err.println( kbuilder.getErrors() );
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );


        StatefulKnowledgeSession knowledgeSession = kb.newStatefulKnowledgeSession();

        List list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();
        assertEquals( 0, list.size() );

        knowledgeSession.insert( "go" );
        knowledgeSession.fireAllRules();

        assertEquals( 1, list.size() );

    }

    @Test
    public void testRuleExtendsNonExistingWithCompositeKBuilder() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "";

        String str2 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"ase\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        CompositeKnowledgeBuilder ckb = kbuilder.batch();

        ckb
                .add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL )
                .build();

        System.err.println( kbuilder.getErrors() );
        assertTrue( kbuilder.hasErrors() );

    }


    @Test
    public void testRuleExtendsNonExistingWithCompositeKBuilderOutOfOrder() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "";

        String str2 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"ase\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        CompositeKnowledgeBuilder ckb = kbuilder.batch();

        ckb
                .add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL )
                .build();

        System.err.println( kbuilder.getErrors() );
        assertTrue( kbuilder.hasErrors() );

    }



    @Test
    public void testRuleExtendsWithCompositeKBuilderFreeOrder() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "";

        String str2 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Base\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        CompositeKnowledgeBuilder ckb = kbuilder.batch();

        ckb
                .add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL )
                .build();

        System.err.println( kbuilder.getErrors() );
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );


        StatefulKnowledgeSession knowledgeSession = kb.newStatefulKnowledgeSession();

        List list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();
        assertEquals( 0, list.size() );

        knowledgeSession.insert( "go" );
        knowledgeSession.fireAllRules();

        assertEquals( 1, list.size() );

    }

}
