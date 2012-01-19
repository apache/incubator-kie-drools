/*
 * Copyright 2011 JBoss Inc
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

package org.drools.lang.api;

import java.util.Collection;
import java.util.Collections;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.StockTick;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.type.FactType;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.junit.Test;

/**
 * DescrBuilderTest
 */
public class DescrBuilderTest extends CommonTestMethodBase {

    @Test
    public void testPackage() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools" )
                .attribute( "dialect" ).value( "mvel" ).end()
                .getDescr();

        assertEquals( "org.drools",
                      pkg.getName() );
        assertEquals( "mvel",
                      pkg.getAttribute( "dialect" ).getValue() );
        assertNull( pkg.getAttribute( "salience" ) );

        KnowledgePackage kpkg = compilePkgDescr( pkg );

        assertEquals( "org.drools",
                      kpkg.getName() );
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

        assertEquals( "org.drools",
                      pkg.getName() );
        assertEquals( 3,
                      pkg.getAttributes().size() );
        assertEquals( "mvel",
                      pkg.getAttribute( "dialect" ).getValue() );
        assertEquals( "10",
                      pkg.getAttribute( "salience" ).getValue() );
        assertEquals( "true",
                      pkg.getAttribute( "lock-on-active" ).getValue() );
        assertEquals( AttributeDescr.Type.BOOLEAN,
                      pkg.getAttribute( "lock-on-active" ).getType() );
        assertNull( pkg.getAttribute( "no-loop" ) );

        KnowledgePackage kpkg = compilePkgDescr( pkg );
        assertEquals( "org.drools",
                      kpkg.getName() );
    }

    @Test
    public void testPackageImports() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools" )
                .newImport().target( "java.util.List" ).end()
                .newImport().target( "org.drools.examples.*" ).end()
                .getDescr();

        assertEquals( 2,
                      pkg.getImports().size() );
        assertEquals( "java.util.List",
                      pkg.getImports().get( 0 ).getTarget() );
        assertEquals( "org.drools.examples.*",
                      pkg.getImports().get( 1 ).getTarget() );
    }

    @Test
    public void testGlobals() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools" )
                .newGlobal().type( "java.util.List" ).identifier( "list" ).end()
                .newGlobal().type( "Person" ).identifier( "bob" ).end()
                .getDescr();

        assertEquals( 2,
                      pkg.getGlobals().size() );
        assertEquals( "java.util.List",
                      pkg.getGlobals().get( 0 ).getType() );
        assertEquals( "list",
                      pkg.getGlobals().get( 0 ).getIdentifier() );
        assertEquals( "Person",
                      pkg.getGlobals().get( 1 ).getType() );
        assertEquals( "bob",
                      pkg.getGlobals().get( 1 ).getIdentifier() );

        KnowledgePackage kpkg = compilePkgDescr( pkg );
        assertEquals( "org.drools",
                      kpkg.getName() );
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

        assertEquals( 1,
                      pkg.getFunctionImports().size() );
        assertEquals( 1,
                      pkg.getFunctions().size() );
        assertEquals( 1,
                      pkg.getRules().size() );

        KnowledgePackage kpkg = compilePkgDescr( pkg );
        assertEquals( "org.drools",
                      kpkg.getName() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( Collections.singletonList( kpkg ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        int rules = ksession.fireAllRules();
        assertEquals( 1,
                      rules );
    }

    @Test
    public void testDeclare() throws InstantiationException,
                             IllegalAccessException {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.beans" )
                // declare
                .newDeclare().type().name( "StockTick" )
                    .newAnnotation( "role" ).value( "event" ).end()
                    .newAnnotation( "author" ).value( "bob" ).end()
                    .newField( "symbol" ).type( "String" ).end()
                    .newField( "price" ).type( "double" ).end()
                .end()
                .getDescr();

        assertEquals( 1,
                      pkg.getTypeDeclarations().size() );

        KnowledgePackage kpkg = compilePkgDescr( pkg );
        assertEquals( "org.beans",
                      kpkg.getName() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( Collections.singletonList( kpkg ) );

        FactType stType = kbase.getFactType( "org.beans",
                                             "StockTick" );
        assertNotNull( stType );
        Object st = stType.newInstance();
        stType.set( st,
                    "symbol",
                    "RHT" );
        stType.set( st,
                    "price",
                    10 );

        assertEquals( "RHT",
                      stType.get( st,
                                  "symbol" ) );

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

        assertEquals( 2,
                      pkg.getEntryPointDeclarations().size() );

        KnowledgePackage kpkg = compilePkgDescr( pkg );
        assertEquals( "org.drools",
                      kpkg.getName() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( Collections.singletonList( kpkg ) );

        assertEquals( 2,
                      kbase.getEntryPointIds().size() );

    }

    @Test
    public void testRule() throws InstantiationException,
                                       IllegalAccessException {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools" )
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
                .end()
                .getDescr();

        KnowledgePackage kpkg = compilePkgDescr( pkg );
        assertEquals( "org.drools",
                      kpkg.getName() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( Collections.singletonList( kpkg ) );
        
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.insert( new StockTick(1, "RHT", 80, 1 ) );
        int rules = ksession.fireAllRules();
        assertEquals( 0, rules );

        ksession = kbase.newStatefulKnowledgeSession();
        ksession.insert( new StockTick(2, "RHT", 150, 1 ) );
        rules = ksession.fireAllRules();
        assertEquals( 1, rules );
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

        KnowledgePackage kpkg = compilePkgDescr( pkg );
        assertEquals( "org.drools",
                      kpkg.getName() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( Collections.singletonList( kpkg ) );
        
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        WorkingMemoryEntryPoint ep = ksession.getWorkingMemoryEntryPoint( "EventStream" );
        ep.insert( "Hello World!" );
        int rules = ksession.fireAllRules();
        assertEquals( 1, rules );

    }
    
    private KnowledgePackage compilePkgDescr( PackageDescr pkg ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newDescrResource( pkg ),
                      ResourceType.DESCR );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );
        Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();
        assertEquals( 1,
                      kpkgs.size() );

        return kpkgs.iterator().next();

    }

}
