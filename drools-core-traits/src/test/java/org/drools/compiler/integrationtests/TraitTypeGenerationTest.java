/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.beliefsystem.abductive.Abducible;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.io.impl.ByteArrayResource;
import org.junit.Test;
import org.kie.api.builder.Message;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TraitTypeGenerationTest extends CommonTestMethodBase {


    @Test
    public void testNeeds() {
        // revisiting OPSJ's version of a fragment of the famous monkey&bananas AI problem
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                        "" +
                        "import " + Thing.class.getPackage().getName() + ".*;" +
                        "import " + Abducible.class.getName() + "; \n" +
                        "global java.util.List list; \n" +

                        "declare Goal \n" +
                        "   entity : Thing \n" +
                        "   property : String \n" +
                        "   value : Object \n" +
                        "end \n" +

                        "query check( Thing $thing, String $prop, Object $val ) " +
                        "   Thing( this == $thing, fields[ $prop ] == $val ) " +
                        "   or" +
                        "   ( " +
                        "     need( $thing, $prop, $val ; ) " +
                        "     and" +
                        "     Thing( this == $thing, fields[ $prop ] == $val ) " +
                        "   ) " +
                        "end \n " +

                        "query need( Thing $thing, String $prop, Object $val ) " +
                        "   @Abductive( target=Goal.class ) \n" +
                        "   Thing( this == $thing, fields[ $prop ] != $val ) " +
                        "end \n "+

                        "rule HandleGoal " +
                        "when " +
                        "   $g : Goal( $m : entity, $prop : property, $val : value ) " +
                        "then " +
                        "   System.out.println( 'Satisfy ' + $g ); \n" +
                        "   modify ( $m ) { getFields().put( $prop, $val ); } \n" +
                        "end " +

                        "declare trait Monkey\n" +
                        "   position : Integer = 1 \n " +
                        "end \n" +

                        "rule Main\n" +
                        "when \n" +
                        "then \n" +
                        "   System.out.println( 'Don MONKEY ' ); " +
                        "   Entity e = new Entity(); \n" +
                        "   Monkey monkey = don( e, Monkey.class );" +
                        "end \n" +

                        "rule MoveAround " +
                        "when " +
                        "   $m : Monkey( $pos : position ) " +
                        "   ?check( $m, \"position\", 4 ; ) " +
                        "then " +
                        "   System.out.println( 'Monkey madness' + $m ); " +
                        "   list.add( $m.getPosition() ); " +
                        "end " +

                        "";

        /////////////////////////////////////

        KieSession session = loadKnowledgeBaseFromString(droolsSource ).newKieSession();
        List list = new ArrayList();
        session.setGlobal( "list", list );

        session.fireAllRules();

        for ( Object o : session.getObjects() ) {
            System.out.println( ">>> " + o );
        }

        assertEquals(Arrays.asList(4 ), list );
    }



    @Test
    public void testRedeclareClassAsTrait() {
        final String s1 = "package test; " +
                "global java.util.List list; " +

                "declare trait " + SomeClass.class.getCanonicalName() + " end ";

        KieHelper kh = new KieHelper();
        kh.addContent( s1, ResourceType.DRL );

        assertEquals( 1, kh.verify().getMessages(Message.Level.ERROR ).size() );
    }

    public static class SomeClass {}

    @Test
    public void testMultipleInheritanceWithPosition1() throws Exception {
        // DROOLS-249
        String drl =
                "package org.drools.test\n" +
                        "declare trait PosTrait\n" +
                        "@propertyReactive\n" +
                        " field0 : int = 100 //@position(0)\n" +
                        " field1 : int = 101 //@position(1)\n" +
                        " field2 : int = 102 //@position(0)\n" +
                        "end\n" +
                        "\n" +
                        "declare trait MultiInhPosTrait extends PosTrait\n" +
                        "@propertyReactive\n" +
                        " mfield0 : int = 200 //@position(0)\n" +
                        " mfield1 : int = 201 @position(2)\n" +
                        "end";

        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        assertFalse(kBuilder.hasErrors());

        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());

        FactType sw = knowledgeBase.getFactType("org.drools.test", "MultiInhPosTrait");
        assertEquals(5, sw.getFields().size());
        assertEquals("field0", sw.getFields().get(0).getName());
        assertEquals("field1", sw.getFields().get(1).getName());
        assertEquals("mfield1", sw.getFields().get(2).getName());
        assertEquals("field2", sw.getFields().get(3).getName());
        assertEquals("mfield0", sw.getFields().get(4).getName());
    }

    @Test
    public void testMultipleInheritanceWithPosition2() throws Exception {
        // DROOLS-249
        String drl =
                "package org.drools.test\n" +
                        "declare trait PosTrait\n" +
                        "@propertyReactive\n" +
                        " field0 : int = 100 //@position(0)\n" +
                        " field1 : int = 101 //@position(1)\n" +
                        " field2 : int = 102 //@position(0)\n" +
                        "end\n" +
                        "\n" +
                        "declare trait MultiInhPosTrait extends PosTrait\n" +
                        "@propertyReactive\n" +
                        " mfield0 : int = 200 @position(0)\n" +
                        " mfield1 : int = 201 @position(2)\n" +
                        "end";
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        assertFalse(kBuilder.hasErrors());

        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());

        FactType sw = knowledgeBase.getFactType("org.drools.test", "MultiInhPosTrait");
        assertEquals(5, sw.getFields().size());
        assertEquals("mfield0", sw.getFields().get(0).getName());
        assertEquals("field0", sw.getFields().get(1).getName());
        assertEquals("mfield1", sw.getFields().get(2).getName());
        assertEquals("field1", sw.getFields().get(3).getName());
        assertEquals("field2", sw.getFields().get(4).getName());
    }

    @Test
    public void testMultipleInheritanceWithPosition3() throws Exception {
        // DROOLS-249
        String drl =
                "package org.drools.test\n" +
                        "declare trait PosTrait\n" +
                        "@propertyReactive\n" +
                        " field0 : int = 100 @position(0)\n" +
                        " field1 : int = 101 @position(1)\n" +
                        " field2 : int = 102 @position(0)\n" +
                        "end\n" +
                        "\n" +
                        "declare trait MultiInhPosTrait extends PosTrait\n" +
                        "@propertyReactive\n" +
                        " mfield0 : int = 200 //@position(0)\n" +
                        " mfield1 : int = 201 @position(2)\n" +
                        "end";
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        assertFalse(kBuilder.hasErrors());

        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());

        FactType sw = knowledgeBase.getFactType("org.drools.test", "MultiInhPosTrait");
        assertEquals(5, sw.getFields().size());
        assertEquals("field0", sw.getFields().get(0).getName());
        assertEquals("field2", sw.getFields().get(1).getName());
        assertEquals("field1", sw.getFields().get(2).getName());
        assertEquals("mfield1", sw.getFields().get(3).getName());
        assertEquals("mfield0", sw.getFields().get(4).getName());
    }

    @Test
    public void testMultipleInheritanceWithPosition4() throws Exception {
        // DROOLS-249
        String drl =
                "package org.drools.test\n" +
                        "declare trait PosTrait\n" +
                        "@propertyReactive\n" +
                        " field0 : int = 100 @position(1)\n" +
                        " field1 : int = 101 @position(0)\n" +
                        " field2 : int = 102 @position(0)\n" +
                        "end\n" +
                        "\n" +
                        "declare trait MultiInhPosTrait extends PosTrait\n" +
                        "@propertyReactive\n" +
                        " mfield0 : int = 200 @position(0)\n" +
                        " mfield1 : int = 201 @position(2)\n" +
                        "end";
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        assertFalse(kBuilder.hasErrors());

        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());

        FactType sw = knowledgeBase.getFactType("org.drools.test", "MultiInhPosTrait");
        assertEquals(5, sw.getFields().size());
        assertEquals("field1", sw.getFields().get(0).getName());
        assertEquals("field2", sw.getFields().get(1).getName());
        assertEquals("mfield0", sw.getFields().get(2).getName());
        assertEquals("field0", sw.getFields().get(3).getName());
        assertEquals("mfield1", sw.getFields().get(4).getName());
    }

    @Test
    public void testMultipleInheritanceWithPosition5() throws Exception {
        // DROOLS-249
        String drl =
                "package org.drools.test\n" +
                        "declare trait PosTrait\n" +
                        "@propertyReactive\n" +
                        " field0 : int = 100 @position(2)\n" +
                        " field1 : int = 101 @position(1)\n" +
                        " field2 : int = 102 @position(8)\n" +
                        "end\n" +
                        "\n" +
                        "declare trait MultiInhPosTrait extends PosTrait\n" +
                        "@propertyReactive\n" +
                        " mfield0 : int = 200 @position(7)\n" +
                        " mfield1 : int = 201 @position(2)\n" +
                        "end";
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        assertFalse(kBuilder.hasErrors());

        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());

        FactType sw = knowledgeBase.getFactType("org.drools.test", "MultiInhPosTrait");
        assertEquals(5, sw.getFields().size());
        assertEquals("field1", sw.getFields().get(0).getName());
        assertEquals("field0", sw.getFields().get(1).getName());
        assertEquals("mfield1", sw.getFields().get(2).getName());
        assertEquals("mfield0", sw.getFields().get(3).getName());
        assertEquals("field2", sw.getFields().get(4).getName());
    }

    @Test
    public void testMultipleInheritanceWithPosition6() throws Exception {
        // DROOLS-249
        String drl =
                "package org.drools.test\n" +
                        "declare trait PosTrait\n" +
                        "@propertyReactive\n" +
                        " field0 : int = 100 //@position(0)\n" +
                        " field1 : int = 101 //@position(1)\n" +
                        " field2 : int = 102 //@position(0)\n" +
                        "end\n" +
                        "\n" +
                        "declare trait SecPosTrait\n" +
                        "@propertyReactive\n" +
                        " field3 : int = 100 //@position(0)\n" +
                        " field1 : int = 101 //@position(1)\n" +
                        "end\n" +
                        "\n" +
                        "declare trait MultiInhPosTrait extends PosTrait, SecPosTrait\n" +
                        "@propertyReactive\n" +
                        " mfield0 : int = 200 //@position(0)\n" +
                        " mfield1 : int = 201 //@position(2)\n" +
                        "end";
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        assertFalse(kBuilder.hasErrors());

        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());

        FactType sw = knowledgeBase.getFactType("org.drools.test", "MultiInhPosTrait");
        assertEquals(6, sw.getFields().size());
        assertEquals("field0", sw.getFields().get(0).getName());
        assertEquals("field1", sw.getFields().get(1).getName());
        assertEquals("field2", sw.getFields().get(2).getName());
        assertEquals("field3", sw.getFields().get(3).getName());
        assertEquals("mfield0", sw.getFields().get(4).getName());
        assertEquals("mfield1", sw.getFields().get(5).getName());
    }

    @Test
    public void testMultipleInheritanceWithPosition7() throws Exception {
        // DROOLS-249
        String drl =
                "package org.drools.test\n" +
                        "declare trait PosTrait\n" +
                        "@propertyReactive\n" +
                        " field0 : int = 100 @position(0)\n" +
                        " field1 : int = 101 @position(1)\n" +
                        " field2 : int = 102 @position(0)\n" +
                        "end\n" +
                        "\n" +
                        "declare trait SecPosTrait\n" +
                        "@propertyReactive\n" +
                        " field3 : int = 100 @position(2)\n" +
                        " field1 : int = 101 //@position(1)\n" +
                        "end\n" +
                        "\n" +
                        "declare trait MultiInhPosTrait extends PosTrait, SecPosTrait\n" +
                        "@propertyReactive\n" +
                        " mfield0 : int = 200 @position(0)\n" +
                        " mfield1 : int = 201 @position(2)\n" +
                        "end";
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        assertFalse(kBuilder.hasErrors());

        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());

        FactType sw = knowledgeBase.getFactType("org.drools.test", "MultiInhPosTrait");
        assertEquals(6, sw.getFields().size());
        assertEquals("field0", sw.getFields().get(0).getName());
        assertEquals("field2", sw.getFields().get(1).getName());
        assertEquals("mfield0", sw.getFields().get(2).getName());
        assertEquals("field1", sw.getFields().get(3).getName());
        assertEquals("field3", sw.getFields().get(4).getName());
        assertEquals("mfield1", sw.getFields().get(5).getName());
    }
}

