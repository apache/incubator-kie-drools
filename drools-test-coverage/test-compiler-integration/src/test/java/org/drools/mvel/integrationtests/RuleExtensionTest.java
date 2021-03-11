/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvel.integrationtests;

import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        assertTrue( kbuilder.hasErrors() );
        kbuilder.getErrors().iterator().next().toString().contains("Base");
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
        assertFalse(kbuilder.hasErrors());

        InternalKnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addPackages( kbuilder.getKnowledgePackages() );

        KieSession knowledgeSession = kb.newKieSession();

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

        InternalKnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addPackages( kbuilder.getKnowledgePackages() );

        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder( kb );
        kbuilder2.add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL );
        assertFalse(kbuilder2.hasErrors());

        KieSession knowledgeSession = kb.newKieSession();

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

        InternalKnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addPackages( kbuilder.getKnowledgePackages() );

        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder( kb );
        kbuilder2.add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL );
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

        ckb.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL )
                .build();

        assertFalse( kbuilder.hasErrors() );

        InternalKnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addPackages( kbuilder.getKnowledgePackages() );

        KieSession knowledgeSession = kb.newKieSession();

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

        ckb.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL )
                .build();

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

        ckb.add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL )
                .build();

        assertTrue( kbuilder.hasErrors() );
        System.out.println( kbuilder.getErrors() );
        assertFalse( kbuilder.getErrors().toString().contains( "Circular" ) );
        assertTrue( kbuilder.getErrors().toString().contains( "Base" ) );
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

        ckb.add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL )
                .build();

        assertFalse( kbuilder.hasErrors() );

        InternalKnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addPackages( kbuilder.getKnowledgePackages() );

        KieSession knowledgeSession = kb.newKieSession();

        List list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();
        assertEquals( 0, list.size() );

        knowledgeSession.insert( "go" );
        knowledgeSession.fireAllRules();

        assertEquals( 1, list.size() );
    }

    @Test
    public void testRuleExtendsExtendsWithCompositeKBuilderFreeOrder() {
        // DROOLS-100
        String str1 =
                "package org.drools.test;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  $i : Integer( this < 5 )\n" +
                        "then\n" +
                        "end\n";

        String str2 =
                "package org.drools.test;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Base\"\n" +
                        "when\n" +
                        "  $j : Integer( this > 5 )\n" +
                        "then\n" +
                        "end\n";

        String str3 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"FinalRule\" extends \"ExtYes\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( $i + $j );\n" +
                        "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        CompositeKnowledgeBuilder ckb = kbuilder.batch();

        ckb.add( ResourceFactory.newByteArrayResource( str3.getBytes() ), ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( str1.getBytes() ), ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL )
                .build();

        assertFalse( kbuilder.hasErrors() );

        InternalKnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addPackages( kbuilder.getKnowledgePackages() );

        KieSession knowledgeSession = kb.newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();
        assertEquals( 0, list.size() );

        knowledgeSession.insert( 4 );
        knowledgeSession.insert( 6 );
        knowledgeSession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 10, (int)list.get(0) );
    }

    @Test
    public void testRuleCircularExtension() {
        // DROOLS-100
        String str1 =
                "package org.drools.test;\n" +
                        "\n" +
                        "rule \"Base\" extends \"FinalRule\"\n" +
                        "when\n" +
                        "  $i : Integer( this < 5 )\n" +
                        "then\n" +
                        "end\n";

        String str2 =
                "package org.drools.test;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Base\"\n" +
                        "when\n" +
                        "  $j : Integer( this > 5 )\n" +
                        "then\n" +
                        "end\n";

        String str3 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Dummy\"\n" +
                        "when\n" +
                        "then\n" +
                        "end\n" +
                        "rule \"FinalRule\" extends \"ExtYes\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( $i + $j );\n" +
                        "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        CompositeKnowledgeBuilder ckb = kbuilder.batch();

        ckb.add( ResourceFactory.newByteArrayResource( str3.getBytes() ), ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( str1.getBytes() ), ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL )
                .build();

        assertTrue( kbuilder.hasErrors() );
        assertEquals( 1, kbuilder.getErrors().size() );
        assertTrue( kbuilder.getErrors().iterator().next().toString().contains("Circular") );
    }
}
