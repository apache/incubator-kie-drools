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
package org.drools.mvel.compiler.lang.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.io.ByteArrayResource;
import org.drools.base.rule.GroupElement;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.dsl.DescrFactory;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.mvel.DrlDumper;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.StockTick;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * DescrBuilderTest
 */
public class DescrBuilderTest {

    @Test
    public void testPackage() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools" )
                .attribute( "dialect" ).value( "mvel" ).end()
                .getDescr();

        assertThat(pkg.getName()).isEqualTo("org.drools");
        assertThat(pkg.getAttribute("dialect").getValue()).isEqualTo("mvel");
        assertThat(pkg.getAttribute("salience")).isNull();

        KiePackage kpkg = compilePkgDescr( pkg );

        assertThat(kpkg.getName()).isEqualTo("org.drools");
    }

    @Test
    public void testPackageAttributes() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools" )
                // first syntax
                .attribute( "dialect" ).value( "mvel" ).end()
                // second syntax
                .attribute( "salience",
                            "10" )
                // third syntax
                .attribute( "lock-on-active",
                            "true",
                            AttributeDescr.Type.BOOLEAN )
                .getDescr();

        assertThat(pkg.getName()).isEqualTo("org.drools");
        assertThat(pkg.getAttributes().size()).isEqualTo(3);
        assertThat(pkg.getAttribute("dialect").getValue()).isEqualTo("mvel");
        assertThat(pkg.getAttribute("salience").getValue()).isEqualTo("10");
        assertThat(pkg.getAttribute("lock-on-active").getValue()).isEqualTo("true");
        assertThat(pkg.getAttribute("lock-on-active").getType()).isEqualTo(AttributeDescr.Type.BOOLEAN);
        assertThat(pkg.getAttribute("no-loop")).isNull();

        KiePackage kpkg = compilePkgDescr( pkg );
        assertThat(kpkg.getName()).isEqualTo("org.drools");
    }

    @Test
    public void testPackageImports() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools" )
                .newImport().target( "java.util.List" ).end()
                .newImport().target( "org.drools.examples.*" ).end()
                .getDescr();

        assertThat(pkg.getImports().size()).isEqualTo(2);
        assertThat(pkg.getImports().get(0).getTarget()).isEqualTo("java.util.List");
        assertThat(pkg.getImports().get(1).getTarget()).isEqualTo("org.drools.examples.*");
    }

    @Test
    public void testGlobals() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools.mvel.compiler" )
                .newGlobal().type( "java.util.List" ).identifier( "list" ).end()
                .newGlobal().type( "Person" ).identifier( "bob" ).end()
                .getDescr();

        assertThat(pkg.getGlobals().size()).isEqualTo(2);
        assertThat(pkg.getGlobals().get(0).getType()).isEqualTo("java.util.List");
        assertThat(pkg.getGlobals().get(0).getIdentifier()).isEqualTo("list");
        assertThat(pkg.getGlobals().get(1).getType()).isEqualTo("Person");
        assertThat(pkg.getGlobals().get(1).getIdentifier()).isEqualTo("bob");

        KiePackage kpkg = compilePkgDescr( pkg );
        assertThat(kpkg.getName()).isEqualTo("org.drools.mvel.compiler");
    }

    @Test
    public void testFunctions() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools" )
                // functions
                .newFunctionImport().target( "java.lang.Math.max" ).end()
                .newFunction().returnType( "long" ).name( "myMax" )
                    .parameter( "long",
                                "v1" ).parameter( "long",
                                                  "v2" )
                    .body( "return max(v1, v2);" )
                .end()
                // rule
                .newRule().name( "test" )
                    .lhs()
                        .eval().constraint( "myMax(5, 10) == 10" ).end()
                    .end()
                    .rhs( "// do something" )
                .end()
                .getDescr();

        assertThat(pkg.getFunctionImports().size()).isEqualTo(1);
        assertThat(pkg.getFunctions().size()).isEqualTo(1);
        assertThat(pkg.getRules().size()).isEqualTo(1);

        KiePackage kpkg = compilePkgDescr( pkg );
        assertThat(kpkg.getName()).isEqualTo("org.drools");

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( Collections.singletonList( kpkg ) );
        KieSession ksession = kbase.newKieSession();
        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }

    @Test
    public void testNamedConsequence() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools.mvel.compiler" )
                .newRule().name( "test" )
                    .lhs()
                        .pattern("Cheese").constraint( "type == \"stilton\"" ).end()
                        .namedConsequence().name("c1").end()
                        .pattern("Cheese").constraint( "type == \"cheddar\"" ).end()
                    .end()
                    .rhs( "// do something" )
                    .namedRhs( "c1", "// do something else" )
                .end()
                .getDescr();

        assertThat(pkg.getRules().size()).isEqualTo(1);

        KiePackage kpkg = compilePkgDescr( pkg );
        assertThat(kpkg.getName()).isEqualTo("org.drools.mvel.compiler");

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( Collections.singletonList( kpkg ) );
        KieSession ksession = kbase.newKieSession();

        Cheese stilton = new Cheese( "stilton", 5 );
        Cheese cheddar = new Cheese( "cheddar", 7 );
        Cheese brie = new Cheese( "brie", 5 );

        ksession.insert( stilton );
        ksession.insert( cheddar );
        ksession.insert( brie );

        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(2);
    }

    @Test
    public void testConditionalBranch() {
        String expected = "packageorg.drools.mvel.compiler\n" +
                          "rule \"test\"\n" +
                          "when\n" +
                          "    Cheese( type == \"stilton\" )  \n" +
                          "    if ( price < 10 ) do[c1] \n" +
                          "    Cheese( type == \"cheddar\" )  \n" +
                          "then\n" +
                          "// do something\n" +
                          "then[c1]\n" +
                          "// do something else\n" +
                          "end";

        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools.mvel.compiler" )
                .newRule().name( "test" )
                    .lhs()
                        .pattern("Cheese").constraint( "type == \"stilton\"" ).end()
                        .conditionalBranch()
                            .condition().constraint("price < 10").end()
                            .consequence().name("c1").end()
                        .end()
                        .pattern("Cheese").constraint( "type == \"cheddar\"" ).end()
                    .end()
                    .rhs( "// do something" )
                    .namedRhs( "c1", "// do something else" )
                .end()
                .getDescr();

        assertThat(pkg.getRules().size()).isEqualTo(1);

        String drl = new DrlDumper().dump( pkg );
        assertThat(expected).isEqualToIgnoringWhitespace(drl);

        KiePackage kpkg = compilePkgDescr( pkg );
        assertThat(kpkg.getName()).isEqualTo("org.drools.mvel.compiler");

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( Collections.singletonList( kpkg ) );
        KieSession ksession = kbase.newKieSession();

        Cheese stilton = new Cheese( "stilton", 5 );
        Cheese cheddar = new Cheese( "cheddar", 7 );
        Cheese brie = new Cheese( "brie", 5 );

        ksession.insert( stilton );
        ksession.insert( cheddar );
        ksession.insert( brie );

        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(2);
    }

    @Test
    public void testDeclare() throws InstantiationException,
                             IllegalAccessException {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.beans" )
                // declare
                .newDeclare()
                    .type().name( "StockTick" )
                    .newAnnotation( "role" ).value( "event" ).end()
                    .newAnnotation( "author" ).value( "bob" ).end()
                    .newField( "symbol" ).type( "String" ).end()
                    .newField( "price" ).type( "double" ).end()
                .end()
                .newDeclare()
                    .enumerative().name( "Planets" )
                    .newAnnotation( "kind" ).value( "enum" ).end()
                    .newEnumLiteral( "earth" ).constructorArg( "6.0" ).constructorArg( "10.0" ).end()
                    .newEnumLiteral( "jupiter" ).constructorArg( "44.0" ).constructorArg( "50.0" ).end()
                    .newField( "mass" ).type( "double" ).end()
                    .newField( "radius" ).type( "double" ).end()
                .end()
                .getDescr();

        assertThat(pkg.getTypeDeclarations().size()).isEqualTo(1);

        assertThat(pkg.getEnumDeclarations().size()).isEqualTo(1);

        KiePackage kpkg = compilePkgDescr( pkg );
        assertThat(kpkg.getName()).isEqualTo("org.beans");

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( Collections.singletonList( kpkg ) );

        FactType stType = kbase.getFactType( "org.beans",
                                             "StockTick" );
        assertThat(stType).isNotNull();
        Object st = stType.newInstance();
        stType.set( st,
                    "symbol",
                    "RHT" );
        stType.set( st,
                    "price",
                    10 );

        assertThat(stType.get(st,
                "symbol")).isEqualTo("RHT");

        //stType.getAnnotation("author"); TODO: implement support for this

    }

    @Test
    public void testDeclareEntryPoint() throws InstantiationException,
                                       IllegalAccessException {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools" )
                // declare
                .newDeclare().entryPoint()
                    .entryPointId( "ep1" )
                .end()
                .newDeclare().entryPoint()
                    .entryPointId( "ep3" )
                .end()
                .getDescr();

        assertThat(pkg.getEntryPointDeclarations().size()).isEqualTo(2);

        KiePackage kpkg = compilePkgDescr( pkg );
        assertThat(kpkg.getName()).isEqualTo("org.drools");
        
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( Collections.singletonList( kpkg ) );

        assertThat(kbase.getEntryPointIds().size()).isEqualTo(2);

    }

    @Test
    public void testRuleRHSOptional() throws InstantiationException,
                                       IllegalAccessException {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools.mvel.compiler" )
                .newRule().name( "r1" )
                    .lhs()
                        .pattern("StockTick").constraint( "company == \"RHT\"" ).end()
                    .end()
                .end()
                .getDescr();

        KiePackage kpkg = compilePkgDescr( pkg );
        assertThat(kpkg.getName()).isEqualTo("org.drools.mvel.compiler");
        
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( Collections.singletonList( kpkg ) );
        
        KieSession ksession = kbase.newKieSession();
        ksession.insert( new StockTick(1, "RHT", 80, 1 ) );
        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }
    
    @Test
    public void testRuleRHSComment() throws InstantiationException,
                                       IllegalAccessException {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools.mvel.compiler" )
                .newRule().name( "r1" )
                    .lhs()
                        .pattern("StockTick").constraint( "company == \"RHT\"" ).end()
                    .end()
                    .rhs( "// some comment" )
                .end()
                .getDescr();

        KiePackage kpkg = compilePkgDescr( pkg );
        assertThat(kpkg.getName()).isEqualTo("org.drools.mvel.compiler");
        
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( Collections.singletonList( kpkg ) );
        
        KieSession ksession = kbase.newKieSession();
        ksession.insert( new StockTick(1, "RHT", 80, 1 ) );
        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }
    
    @Test
    public void testTopLevelAccumulate() throws InstantiationException,
                                       IllegalAccessException {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools.mvel.compiler" )
                .newRule().name( "r1" )
                    .lhs()
                        .accumulate()
                            .source()
                                .pattern("StockTick").constraint( "company == \"RHT\"" ).bind( "$p", "price", false ).end()
                            .end()
                            .function( "sum", "$sum", false, "$p" )
                            .function( "count", "$cnt", false, "$p" )
                        .end()
                    .end()
                    .rhs( "// some comment" )
                .end()
                .getDescr();

        KiePackage kpkg = compilePkgDescr( pkg, "org.drools.mvel.compiler" );
        assertThat(kpkg.getName()).isEqualTo("org.drools.mvel.compiler");
        
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( Collections.singletonList( kpkg ) );
        
        KieSession ksession = kbase.newKieSession();
        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );
        
        ksession.insert( new StockTick(1, "RHT", 80, 1 ) );
        ksession.insert( new StockTick(2, "RHT", 100, 10 ) );
        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
        
        ArgumentCaptor<AfterMatchFiredEvent> cap = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );
        verify( ael ).afterMatchFired(cap.capture());
        
        assertThat(((Number) cap.getValue().getMatch().getDeclarationValue("$sum")).intValue()).isEqualTo(180);
        assertThat(((Number) cap.getValue().getMatch().getDeclarationValue("$cnt")).intValue()).isEqualTo(2);
    }
    
    @Test
    public void testRule() throws InstantiationException,
                                       IllegalAccessException {

        PackageDescrBuilder packBuilder =
                DescrFactory.newPackage()
                .name( "org.drools.mvel.compiler" )
                .newRule().name( "r1" )
                    .lhs()
                        .and()
                            .or()
                                .pattern( "StockTick" ).constraint( "price > 100" ).end()
                                .pattern( "StockTick" ).constraint( "price < 10" ).end()
                            .end()
                            .pattern("StockTick").constraint( "company == \"RHT\"" ).end()
                        .end()
                    .end()
                    .rhs( "    System.out.println(\"foo\");\n" )
                .end();

        PackageDescr pkg = packBuilder.getDescr();

                String drl = new DrlDumper().dump( packBuilder.getDescr() );
        System.out.println(drl);

        KiePackage kpkg = compilePkgDescr( pkg );
        assertThat(kpkg.getName()).isEqualTo("org.drools.mvel.compiler");
        
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( Collections.singletonList( kpkg ) );
        
        KieSession ksession = kbase.newKieSession();
        ksession.insert( new StockTick(1, "RHT", 80, 1 ) );
        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(0);

        ksession = kbase.newKieSession();
        ksession.insert( new StockTick(2, "RHT", 150, 1 ) );
        rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }
    
    @Test
    public void testFromEntryPoint() throws InstantiationException,
                                            IllegalAccessException {
        PackageDescr pkg = DescrFactory
                .newPackage().name("org.drools")
                .newRule().name("from rule")
                    .lhs()
                        .pattern("String").id("s", false).from().entryPoint("EventStream").end()
                    .end()
                .rhs("//System.out.println(s);")
                .end().getDescr();

        KiePackage kpkg = compilePkgDescr( pkg, "org.drools" );
        assertThat(kpkg.getName()).isEqualTo("org.drools");
        
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( Collections.singletonList( kpkg ) );
        
        KieSession ksession = kbase.newKieSession();
        EntryPoint ep = ksession.getEntryPoint( "EventStream" );
        ep.insert( "Hello World!" );
        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);

    }

    @Test
    public void testDumperFromPkg() {
        //DROOLS-109
        PackageDescr pkg = DescrFactory.newPackage().name( "org.test" )
                                       .newRule().name( "org.test" )
                                       .lhs().and()
                                       .or()
                                       .pattern().id( "$x", false ).type( "Integer" ).constraint( "this > 10" ).end()
                                       .pattern().id( "$x", false ).type( "Integer" ).constraint( "this < 20" ).end()
                                       .end()
                                       .pattern().type( "Integer" ).constraint( "this == $x" ).constraint( "this == 42" ).end()
                                       .end().end()
                                       .rhs( "" )
                                       .end()
                                       .end().getDescr();

        String drl = new DrlDumper().dump( pkg );
        System.out.println( drl );
        KnowledgeBuilderImpl knowledgeBuilder = (KnowledgeBuilderImpl)KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        System.err.println( knowledgeBuilder.getErrors() );
        assertThat(knowledgeBuilder.hasErrors()).as(knowledgeBuilder.getErrors().toString()).isFalse();


        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( knowledgeBuilder.getKnowledgePackages() );
        KieSession knowledgeSession = kbase.newKieSession();

        KiePackage rebuiltPkg = knowledgeBuilder.getPackage( "org.test" );
        org.kie.api.definition.rule.Rule rule = rebuiltPkg.getRules().iterator().next();
        RuleImpl r = ((RuleImpl) rule);

        assertThat(r.getLhs().getChildren().size()).isEqualTo(2);
        Iterator<RuleConditionElement> iter = r.getLhs().getChildren().iterator();

        RuleConditionElement arg1 = iter.next();
        assertThat(arg1 instanceof GroupElement && ((GroupElement) arg1).getType() == GroupElement.Type.OR).isTrue();
        assertThat(((GroupElement) arg1).getChildren().size()).isEqualTo(2);

        RuleConditionElement arg2 = iter.next();
        assertThat(arg2 instanceof Pattern).isTrue();

    }

    @Test
    public void testAccumulate() throws InstantiationException,
            IllegalAccessException {

        PackageDescrBuilder packBuilder =
                DescrFactory.newPackage()
                        .newGlobal().identifier( "list" ).type( List.class.getName() ).end()
                        .name( "org.drools.mvel.compiler" )
                            .newRule().name( "r1" )
                                .lhs()
                                    .pattern().id( "$tot", true ).type( Double.class.getName() ).end()
                                    .accumulate().source().pattern().id( "$i", false ).type( Integer.class.getName() ).end().end()
                                        .function( "sum", "$tot", true, "$i" )
                                        .constraint( "$tot > 15" )
                                .end()
                            .end()
                            .rhs( "list.add( $tot );" )
                            .end()
                            .newRule().name( "r2" )
                                .attribute( "dialect", "mvel" )
                                .lhs()
                                    .pattern().id( "$tot", true ).type( Double.class.getName() ).end()
                                    .accumulate().source().pattern().id( "$i", false ).type( Integer.class.getName() ).end().end()
                                        .function( "sum", "$tot", true, "$i" )
                                        .constraint( "$tot > 15" )
                                .end()
                            .end()
                            .rhs( "list.add( $tot * 2 );" )
                        .end();

        String drl = new DrlDumper().dump( packBuilder.getDescr() );
        System.out.println(drl);

        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        System.err.println( knowledgeBuilder.getErrors() );
        assertThat(knowledgeBuilder.hasErrors()).as(knowledgeBuilder.getErrors().toString()).isFalse();


        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( knowledgeBuilder.getKnowledgePackages() );
        KieSession knowledgeSession = kbase.newKieSession();
        List list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.insert( 3 );
        knowledgeSession.insert( 39 );
        knowledgeSession.insert( 24.0 );
        knowledgeSession.insert( 42.0 );

        knowledgeSession.fireAllRules();
        assertThat(list).isEqualTo(Arrays.asList(42.0, 84.0));
    }

    @Test
    public void testDumperPositional() {
        PackageDescr pkg = DescrFactory.newPackage().name( "org.test" )
                .newRule().name( "org.test" )
                .lhs()
                .pattern().type( "Integer" ).constraint( "this > 10", true ).constraint( "this > 11", true ).constraint( "this > 12", false).constraint( "this > 13", false).end()
                .end()
                .rhs( "" )
                .end()
                .end().getDescr();

        String drl = new DrlDumper().dump( pkg );
        assertThat(drl.contains("Integer( this > 10, this > 11; this > 12, this > 13 )")).isTrue();
    }

    @Test
    public void testDumperDuration() {
        PackageDescr pkg = DescrFactory.newPackage().name("org.test")
                .newRule().name("org.test").attribute("duration").value("int: 0 3600000; repeat-limit = 6").end()
                .lhs()
                .end()
                .rhs( "" )
                .end()
                .end().getDescr();

        String drl = new DrlDumper().dump( pkg );
        assertThat(drl.contains("duration (int: 0 3600000; repeat-limit = 6)")).isTrue();
    }

    @Test
    public void testDumperTimer() {
        PackageDescr pkg = DescrFactory.newPackage().name("org.test")
                .newRule().name("org.test").attribute("timer").value("cron:0/5 * * * * ?").end()
                .lhs()
                .end()
                .rhs( "" )
                .end()
                .end().getDescr();

        String drl = new DrlDumper().dump( pkg );
        assertThat(drl.contains("timer (cron:0/5 * * * * ?)")).isTrue();
    }

    private KiePackage compilePkgDescr( PackageDescr pkg ) {
        return compilePkgDescr( pkg, null );
    }

    private KiePackage compilePkgDescr( PackageDescr pkg, String pkgName ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newDescrResource( pkg ),
                      ResourceType.DESCR );

        assertThat(kbuilder.hasErrors()).as(kbuilder.getErrors().toString()).isFalse();

        if (pkgName == null) {
            Collection<KiePackage> kpkgs = kbuilder.getKnowledgePackages();
            assertThat(kpkgs.size()).isEqualTo(1);
            return kpkgs.iterator().next();
        }

        return (( KnowledgeBuilderImpl ) kbuilder).getPackage( pkgName );
    }

    @Test
    public void testBehaviorForSlidingWindow() throws InstantiationException, IllegalAccessException {
        // DROOLS-852
        List<String> myParams = new LinkedList<String>();
        myParams.add("5s");

        PackageDescr pkg = DescrFactory
                .newPackage().name( "org.drools" )
                .newRule().name( "from rule" )
                .lhs()
                .not().pattern().type( "StockTick" ).constraint( "price > 10" ).behavior().type( "window", "time" ).parameters( myParams ).end().end().end()
                .end()
                .rhs("//System.out.println(s);")
                .end().getDescr();

        String drl = new DrlDumper().dump( pkg );
        assertThat(drl.contains("window:time(5s)")).isTrue();
    }

    @Test
    public void testQueryParameters() {
        // DROOLS-4604
        PackageDescrBuilder packBuilder =
                DescrFactory.newPackage()
                        .name("org.test.rules")
                        .newImport()
                        .target("org.test.event.TemporalEvent")
                        .end()
                        .newQuery()
                        .name("getTemporalEventById")
                        .parameter("String", "eventId")
                        .lhs()
                        .pattern( "TemporalEvent")
                        .constraint("id == eventId")
                        .from()
                        .entryPoint("EventStream")
                        .end()
                        .end()
                        .end()
                        .end();

        String drl = new DrlDumper().dump(packBuilder.getDescr());
        assertThat(drl.contains("query \"getTemporalEventById\" ( String eventId ) ")).isTrue();
    }
}
