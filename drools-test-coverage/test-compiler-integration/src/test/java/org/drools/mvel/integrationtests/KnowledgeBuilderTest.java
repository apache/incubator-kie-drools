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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.base.definitions.impl.KnowledgePackageImpl;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.TypeDeclaration;
import org.drools.core.util.FileManager;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class KnowledgeBuilderTest {

    private FileManager fileManager;

    @Before
    public void setUp() throws Exception {
        this.fileManager = new FileManager().setUp();
    }

    @After
    public void tearDown() throws Exception {
        this.fileManager.tearDown();
    }

    @Test
    public void testCompositeKnowledgeBuilder() throws Exception {
        String rule = "package org.drools.mvel.compiler.test\n" +
                      "rule R1 when\n" +
                      "   $fieldA : FactA( $fieldB : fieldB )\n" +
                      "   FactB( this == $fieldB, fieldA == $fieldA )\n" +
                      "then\n" +
                      "end";

        String declarationA = "package org.drools.mvel.compiler.test\n" +
                              "declare FactA\n" +
                              "    fieldB: FactB\n" +
                              "end\n";

        String declarationB = "package org.drools.mvel.compiler.test\n" +
                              "declare FactB\n" +
                              "    fieldA: FactA\n" +
                              "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( rule.getBytes() ), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( declarationA.getBytes() ), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( declarationB.getBytes() ), ResourceType.DRL );
        assertThat(kbuilder.hasErrors()).isTrue();

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( declarationA.getBytes() ), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( declarationB.getBytes() ), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( rule.getBytes() ), ResourceType.DRL );
        assertThat(kbuilder.hasErrors()).isTrue();

        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder2.batch()
                .add( ResourceFactory.newByteArrayResource( rule.getBytes() ), ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( declarationA.getBytes() ), ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource(declarationB.getBytes()), ResourceType.DRL )
                .build();

        if ( kbuilder2.hasErrors() ) {
            fail( kbuilder2.getErrors().toString() );
        }

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder2.getKnowledgePackages() );
        KieSession ksession = kbase.newKieSession();

        FactType aType = kbase.getFactType( "org.drools.mvel.compiler.test", "FactA" );
        Object a = aType.newInstance();
        FactType bType = kbase.getFactType( "org.drools.mvel.compiler.test", "FactB" );
        Object b = bType.newInstance();
        aType.set( a, "fieldB", b );
        bType.set( b, "fieldA", a );

        // JBRULES-3683 - check that the recurisive type declaration doesn't cause a StackOverflowError
        a.toString();
        b.toString();

        ksession.insert( a );
        ksession.insert( b );

        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }

    @Test
    public void testDifferentPackages() throws Exception {
        String rule = "package org.drools.mvel.compiler.test.rule\n" +
                      "import org.drools.mvel.compiler.testA.FactA\n" +
                      "import org.drools.mvel.compiler.testB.FactB\n" +
                      "rule R1 when\n" +
                      "   $fieldA : FactA( $fieldB : fieldB, bigint == 1 )\n" +
                      "   FactB( this == $fieldB, fieldA == $fieldA )\n" +
                      "then\n" +
                      "   list.add(\"OK\");" +
                      "end";

        String declarationA = "package org.drools.mvel.compiler.testA\n" +
                              "import org.drools.mvel.compiler.testB.FactB\n" +
                              "import java.math.BigInteger\n" +
                              "declare FactA\n" +
                              "    fieldB: FactB\n" +
                              "    bigint: BigInteger\n" +
                              "end\n";

        String declarationB = "package org.drools.mvel.compiler.testB\n" +
                              "import org.drools.mvel.compiler.testA.FactA\n" +
                              "global java.util.List list\n" +
                              "declare FactB\n" +
                              "    fieldA: FactA\n" +
                              "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.batch()
                .type( ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( rule.getBytes() ) )
                .add( ResourceFactory.newByteArrayResource( declarationA.getBytes() ) )
                .add( ResourceFactory.newByteArrayResource( declarationB.getBytes() ) )
                .build();


        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        String declarationC = "package org.drools.mvel.compiler.testA\n" +
                              "declare FactC\n" +
                              "    field : UnknownClass\n" +
                              "end\n";

        kbuilder.add( ResourceFactory.newByteArrayResource( declarationC.getBytes() ), ResourceType.DRL );

        assertThat(kbuilder.hasErrors()).isTrue();
        kbuilder.undo();
        assertThat(kbuilder.hasErrors()).isFalse();

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactType aType = kbase.getFactType( "org.drools.mvel.compiler.testA", "FactA" );
        Object a = aType.newInstance();
        FactType bType = kbase.getFactType( "org.drools.mvel.compiler.testB", "FactB" );
        Object b = bType.newInstance();
        aType.set( a, "fieldB", b );
        aType.set( a, "bigint", new BigInteger( "1" ) );
        bType.set( b, "fieldA", a );
        ksession.insert( a );
        ksession.insert( b );

        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("OK");
    }

    @Test @Ignore("All the classes generated by type declarations are now defined in the ProjectClassLoader")
    public void testUndoTypeDeclaration() throws Exception {
        String rule = "package org.drools.mvel.compiler.test\n" +
                      "import org.drools.compiler.test.FactA\n" +
                      "import org.drools.compiler.test.FactB\n" +
                      "rule R1 when\n" +
                      "   FactA( i == 1 )\n" +
                      "   FactB( i == 1 )\n" +
                      "then\n" +
                      "   list.add(\"OK\");" +
                      "end\n";

        String declarationA = "package org.drools.mvel.compiler.test\n" +
                              "global java.util.List list\n" +
                              "declare FactA\n" +
                              "    j : int\n" +
                              "end\n";

        String declarationB = "package org.drools.mvel.compiler.test\n" +
                              "declare FactB\n" +
                              "    i : int\n" +
                              "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( declarationB.getBytes() ), ResourceType.DRL );
        assertThat(kbuilder.hasErrors()).isFalse();

        kbuilder.batch()
                .type( ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( rule.getBytes() ) )
                .add( ResourceFactory.newByteArrayResource( declarationA.getBytes() ) )
                .build();

        assertThat(kbuilder.hasErrors()).isTrue();
        kbuilder.undo();
        assertThat(kbuilder.hasErrors()).isFalse();

        declarationA = "package org.drools.mvel.compiler.test\n" +
                       "global java.util.List list\n" +
                       "declare FactA\n" +
                       "    i : int\n" +
                       "end\n";

        kbuilder.batch()
                .type( ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( rule.getBytes() ) )
                .add( ResourceFactory.newByteArrayResource( declarationA.getBytes() ) )
                .build();

        assertThat(kbuilder.hasErrors()).isFalse();

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactType aType = kbase.getFactType( "org.drools.mvel.compiler.test", "FactA" );
        Object a = aType.newInstance();
        aType.set( a, "i", 1 );

        FactType bType = kbase.getFactType( "org.drools.mvel.compiler.test", "FactB" );
        Object b = bType.newInstance();
        bType.set( b, "i", 1 );

        ksession.insert( a );
        ksession.insert( b );

        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("OK");
    }

    @Test
    public void testUndoRule() throws Exception {
        String rule = "package org.drools.mvel.compiler.test\n" +
                      "global java.util.List list\n" +
                      "import org.drools.mvel.compiler.test.FactA\n" +
                      "import org.drools.mvel.compiler.test.FactB\n" +
                      "rule R1 when\n" +
                      "   FactA( j == 1 )\n" +
                      "   FactB( i == 1 )\n" +
                      "then\n" +
                      "   list.add(\"OK\");" +
                      "end\n";

        String declarationA = "package org.drools.mvel.compiler.test\n" +
                              "declare FactA\n" +
                              "    i : int\n" +
                              "end\n";

        String declarationB = "package org.drools.mvel.compiler.test\n" +
                              "declare FactB\n" +
                              "    i : int\n" +
                              "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( declarationB.getBytes() ), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( declarationA.getBytes() ), ResourceType.DRL );
        assertThat(kbuilder.hasErrors()).isFalse();

        kbuilder.batch()
                .type( ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( rule.getBytes() ) )
                .build();

        assertThat(kbuilder.hasErrors()).isTrue();
        kbuilder.undo();
        assertThat(kbuilder.hasErrors()).isFalse();

        rule = "package org.drools.mvel.compiler.test\n" +
               "global java.util.List list\n" +
               "import org.drools.mvel.compiler.test.FactA\n" +
               "import org.drools.mvel.compiler.test.FactB\n" +
               "rule R1 when\n" +
               "   FactA( i == 1 )\n" +
               "   FactB( i == 1 )\n" +
               "then\n" +
               "   list.add(\"OK\");" +
               "end\n";

        kbuilder.batch()
                .type( ResourceType.DRL )
                .add( ResourceFactory.newByteArrayResource( rule.getBytes() ) )
                .add( ResourceFactory.newByteArrayResource( declarationA.getBytes() ) )
                .build();

        assertThat(kbuilder.hasErrors()).isFalse();

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactType aType = kbase.getFactType( "org.drools.mvel.compiler.test", "FactA" );
        Object a = aType.newInstance();
        aType.set( a, "i", 1 );

        FactType bType = kbase.getFactType( "org.drools.mvel.compiler.test", "FactB" );
        Object b = bType.newInstance();
        bType.set( b, "i", 1 );

        ksession.insert( a );
        ksession.insert( b );

        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("OK");
    }

    @Test
    public void testAddPackageSingle() throws Exception {
        String rule = "package org.drools.mvel.compiler.test\n" +
                      "import org.drools.mvel.compiler.StockTick\n" +
                      "rule R1 when\n" +
                      "   StockTick()\n" +
                      "then\n" +
                      "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL);

        assertThat(kbuilder.hasErrors()).as(kbuilder.getErrors().toString()).isFalse();

        Collection<KiePackage> kpkgs = kbuilder.getKnowledgePackages();
        assertThat(kpkgs.size()).isEqualTo(2);
        KiePackage kpkg = kpkgs.iterator().next();
        assertThat(kpkg.getRules().size()).isEqualTo(1);
    }

    @Test
    public void testAddPackageArray() throws Exception {
        String rule = "package org.drools.mvel.compiler.test\n" +
                      "import org.drools.mvel.compiler.StockTick\n" +
                      "declare StockTick @role(event) end\n" +
                      "rule R1 when\n" +
                      "   StockTick()\n" +
                      "then\n" +
                      "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL);

        assertThat(kbuilder.hasErrors()).as(kbuilder.getErrors().toString()).isFalse();

        Collection<KiePackage> kpkgs = kbuilder.getKnowledgePackages();
        assertThat(kpkgs.size()).isEqualTo(2);
    }


    @Ignore
    @Test
    public void testResourceMapping() throws Exception {
        String rule = "package org.drools.mvel.compiler.test\n" +
                "rule R1 when\n" +
                " \n" +
                "then\n" +
                "end\n";

        Resource res1 = ResourceFactory.newByteArrayResource( rule.getBytes() );
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( res1, ResourceType.DRL );
        assertThat(kbuilder.hasErrors()).as(kbuilder.getErrors().toString()).isFalse();

        KiePackage kp1 = kbuilder.getKnowledgePackages().iterator().next();
        assertThat(kp1.getRules().size()).isEqualTo(1);
        Rule r = kp1.getRules().iterator().next();
        assertThat(((RuleImpl) r).getResource()).isEqualTo(res1);

        String pmml = "<PMML version=\"4.0\"><Header/></PMML>";

        Resource res2 = ResourceFactory.newByteArrayResource( pmml.getBytes() );
        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        kbuilder2.add( res2, ResourceType.PMML );
        assertThat(kbuilder2.hasErrors()).as(kbuilder2.getErrors().toString()).isFalse();

        KiePackage kp2 = kbuilder2.getKnowledgePackages().iterator().next();
        assertThat(kp2.getRules().size()).isEqualTo(1);
        Rule r2 = kp2.getRules().iterator().next();
        assertThat(((RuleImpl) r2).getResource()).isEqualTo(res2);

    }

    @Test
    public void testRepeatedDeclarationInMultiplePackages() {
        String str =
                "package org.drools.test1;\n" +
                "import org.drools.mvel.compiler.Cheese;\n" +
                "" +
                "rule R\n" +
                "when Cheese() then end \n" +
                "";
        String str2 =
                "package org.drools.test2;\n" +
                "import org.drools.mvel.compiler.Cheese;\n" +
                "" +
                "rule S\n" +
                "when Cheese() then end \n" +
                "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL );

        assertThat(kbuilder.getKnowledgePackages().size()).isEqualTo(3);
        for ( KiePackage kp : kbuilder.getKnowledgePackages() ) {
            KnowledgePackageImpl kpi = (KnowledgePackageImpl) kp;
            TypeDeclaration cheez = kpi.getTypeDeclaration( "Cheese" );
            if ( "org.drools.mvel.compiler".equals( kpi.getName() ) ) {
                assertThat(cheez).isNotNull();
            } else {
                assertThat(cheez).isNull();
            }

        }
    }
}
