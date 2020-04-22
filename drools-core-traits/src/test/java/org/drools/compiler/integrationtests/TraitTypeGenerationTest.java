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

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.io.impl.ByteArrayResource;
import org.junit.Test;
import org.kie.api.builder.Message;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TraitTypeGenerationTest extends CommonTestMethodBase {

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

