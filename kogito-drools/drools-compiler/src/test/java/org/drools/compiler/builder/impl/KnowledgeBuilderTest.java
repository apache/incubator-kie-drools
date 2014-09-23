/*
 * Copyright 2005 JBoss Inc
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

package org.drools.compiler.builder.impl;

import org.drools.compiler.Cheese;
import org.drools.compiler.Primitives;
import org.drools.compiler.StockTick;
import org.drools.compiler.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.compiler.commons.jci.compilers.JaninoJavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.NativeJavaCompiler;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.DuplicateFunction;
import org.drools.compiler.compiler.DuplicateRule;
import org.drools.compiler.compiler.ParserError;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.BindingDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.FieldConstraintDescr;
import org.drools.compiler.lang.descr.GlobalDescr;
import org.drools.compiler.lang.descr.LiteralRestrictionDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DefaultKnowledgeHelper;
import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationNode;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.common.LogicalDependency;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.CompositeObjectSinkAdapterTest;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Behavior;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EvalCondition;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.PredicateConstraint;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.Activation;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.PropagationContext;
import org.drools.core.test.model.DroolsTestCase;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListNode;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.type.FactField;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.runtime.beliefs.Mode;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class KnowledgeBuilderTest extends DroolsTestCase {
    
    @After
    public void tearDown() {
        System.getProperties().remove( "drools.warning.filters" );
        System.getProperties().remove( "drools.kbuilder.severity." + DuplicateFunction.KEY);
        System.getProperties().remove( "drools.kbuilder.severity." + DuplicateRule.KEY);
    }

    @Test
    public void testErrors() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        BindingDescr fieldBindingDescr = new BindingDescr( "x",
                                                           "price" );
        pattern.addConstraint( fieldBindingDescr );
        fieldBindingDescr = new BindingDescr( "y",
                                              "price" );
        pattern.addConstraint( fieldBindingDescr );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        pattern.addConstraint( new ExprConstraintDescr("price == x") );

        // There is no m this should produce errors.
        ruleDescr.setConsequence( "update(m);" );

        builder.addPackage( packageDescr );

        assertTrue( builder.getErrors().getErrors().length > 0  );
    }

    @Test
    public void testErrorsInParser() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        builder.addPackageFromDrl( new InputStreamReader( this.getClass().getResourceAsStream( "bad_rule.drl" ) ) );
        assertTrue( builder.hasErrors() );
    }

    @Test
    public void testReload() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        ruleDescr.setConsequence( "map.put(\"value\", new Integer(1) );" );

        builder.addPackage( packageDescr );

        InternalKnowledgePackage pkg = builder.getPackage();
        RuleImpl rule = pkg.getRule( "rule-1" );

        assertLength( 0,
                      builder.getErrors().getErrors() );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase)KnowledgeBaseFactory.newKnowledgeBase();
        kBase.getGlobals().put( "map", Map.class );
        final KieSession workingMemory = kBase.newStatefulKnowledgeSession();

        final HashMap map = new HashMap();
        workingMemory.setGlobal( "map",
                                 map );

        final LeftTupleImpl tuple = new MockTuple( new HashMap() );
        tuple.setLeftTupleSink( new RuleTerminalNode(1, new CompositeObjectSinkAdapterTest.MockBetaNode(), rule,rule.getLhs(), 0,new BuildContext(kBase, null) )  );
        final Activation activation = new MockActivation( rule,
                                                          0,
                                                          rule.getLhs(),
                                                          tuple );

        DefaultKnowledgeHelper knowledgeHelper = new org.drools.core.base.DefaultKnowledgeHelper( ((StatefulKnowledgeSessionImpl)workingMemory) );
        knowledgeHelper.setActivation( activation );

        rule.getConsequence().evaluate( knowledgeHelper,
                                        ((StatefulKnowledgeSessionImpl)workingMemory) );
        assertEquals( new Integer( 1 ),
                      map.get( "value" ) );

        ruleDescr.setConsequence( "map.put(\"value\", new Integer(2) );" );
        pkg.removeRule( rule );

        // Make sure the compiled classes are also removed
        assertEquals( 0,
                      ((JavaDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( "java" )).list().length );

        builder.addPackage( packageDescr );

        pkg = builder.getPackage();

        rule = pkg.getRule( "rule-1" );

        knowledgeHelper = new org.drools.core.base.DefaultKnowledgeHelper( ((StatefulKnowledgeSessionImpl)workingMemory) );
        knowledgeHelper.setActivation( activation );

        rule.getConsequence().evaluate( knowledgeHelper,
                                        ((StatefulKnowledgeSessionImpl)workingMemory) );
        assertEquals( new Integer( 2 ),
                      map.get( "value" ) );

    }

    @Test
    public void testSerializable() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        ruleDescr.setConsequence( "map.put(\"value\", new Integer(1) );" );
        //check that packageDescr is serializable
        final PackageDescr back = (PackageDescr) SerializationHelper.serializeObject( packageDescr );
        assertNotNull( back );
        assertEquals( "p1",
                      back.getName() );

        builder.addPackage( packageDescr );
        InternalKnowledgePackage pkg = builder.getPackage();
        final RuleImpl rule = pkg.getRule( "rule-1" );

        assertLength( 0,
                      builder.getErrors().getErrors() );

        InternalKnowledgePackage newPkg = SerializationHelper.serializeObject( pkg );
        final RuleImpl newRule = newPkg.getRule( "rule-1" );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase)KnowledgeBaseFactory.newKnowledgeBase();

        // It's been serialised so we have to simulate the re-wiring process
        newPkg.getDialectRuntimeRegistry().onAdd( kBase.getRootClassLoader() );
        newPkg.getDialectRuntimeRegistry().onBeforeExecute();

        kBase.getGlobals().put( "map", Map.class );
        final KieSession workingMemory = kBase.newStatefulKnowledgeSession();

        final HashMap map = new HashMap();

        workingMemory.setGlobal( "map",
                                 map );

        final LeftTupleImpl tuple = new MockTuple( new HashMap() );
        tuple.setLeftTupleSink( new RuleTerminalNode(1, new CompositeObjectSinkAdapterTest.MockBetaNode(), newRule,newRule.getLhs(), 0, new BuildContext(kBase, null) )  );
        final Activation activation = new MockActivation( newRule,
                                                          0,
                                                          newRule.getLhs(),
                                                          tuple );

        final DefaultKnowledgeHelper knowledgeHelper = new org.drools.core.base.DefaultKnowledgeHelper( ((StatefulKnowledgeSessionImpl)workingMemory) );
        knowledgeHelper.setActivation( activation );

        newRule.getConsequence().evaluate( knowledgeHelper,
                                           ((StatefulKnowledgeSessionImpl)workingMemory) );
        assertEquals( new Integer( 1 ),
                      map.get( "value" ) );
    }

    @Test
    @Ignore
    public void testNoPackageName() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        try {
            builder.addPackage( new PackageDescr( null ) );
            fail( "should have errored here." );
        } catch ( final RuntimeException e ) {
            assertNotNull( e.getMessage() );
        }
        try {
            builder.addPackage( new PackageDescr( "" ) );
            fail( "should have errored here." );
        } catch ( final RuntimeException e ) {
            assertNotNull( e.getMessage() );
        }

        builder.addPackageFromDrl( new StringReader( "package foo" ) );
        builder.addPackageFromDrl( new StringReader( "rule x then end" ) );

    }

    @Test
    public void testErrorReset() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        builder.addPackageFromDrl( new StringReader( "package foo \n rule ORB" ) );
        assertTrue( builder.hasErrors() );

        builder.resetErrors();
        assertFalse( builder.hasErrors() );

        builder.addPackageFromDrl( new StringReader( "package foo \n rule ORB" ) );
        assertTrue( builder.hasErrors() );
    }

    @Test
    public void testLiteral() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        pattern.addConstraint( new ExprConstraintDescr( "type == 'stilton'" ) );

        ruleDescr.setConsequence( "update(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors().getErrors() );
    }

    @Test
    public void testReturnValue() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        BindingDescr fieldBindingDescr = new BindingDescr( "x",
                                                           "price" );
        pattern.addConstraint( fieldBindingDescr );
        fieldBindingDescr = new BindingDescr( "y",
                                              "price" );
        pattern.addConstraint( fieldBindingDescr );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        pattern.addConstraint( new ExprConstraintDescr("price == (( (Integer) map.get( new Integer( x )) ).intValue() * y)") );

        ruleDescr.setConsequence( "update(stilton);" );

        builder.addPackage( packageDescr );

        assertEquals( "Should not have any errors",
                      0,
                      builder.getErrors().getErrors().length );
    }

    @Test
    public void testReturnValueMethodCompare() {
        final KnowledgeBuilderImpl builder1 = new KnowledgeBuilderImpl();
        final PackageDescr packageDescr1 = new PackageDescr( "package1" );
        createReturnValueRule( packageDescr1,
                               " x + y " );
        builder1.addPackage( packageDescr1 );
        if ( builder1.hasErrors() ) {
            fail( builder1.getErrors().toString() );
        }
        final Pattern pattern1 = (Pattern) ((RuleImpl)builder1.getPackage().getRules().iterator().next()).getLhs().getChildren().get( 0 );
        final Constraint returnValue1 = pattern1.getConstraints().get( 0 );

        final KnowledgeBuilderImpl builder2 = new KnowledgeBuilderImpl();
        final PackageDescr packageDescr2 = new PackageDescr( "package2" );
        createReturnValueRule( packageDescr2,
                               " x + y " );
        builder2.addPackage( packageDescr2 );
        final Pattern pattern2 = (Pattern) ((RuleImpl)builder2.getPackage().getRules().iterator().next()).getLhs().getChildren().get( 0 );
        final Constraint returnValue2 = pattern2.getConstraints().get( 0 );

        final KnowledgeBuilderImpl builder3 = new KnowledgeBuilderImpl();
        final PackageDescr packageDescr3 = new PackageDescr( "package3" );
        createReturnValueRule( packageDescr3,
                               " x - y " );
        builder3.addPackage( packageDescr3 );
        final Pattern pattern3 = (Pattern) ((RuleImpl)builder3.getPackage().getRules().iterator().next()).getLhs().getChildren().get( 0 );
        final Constraint returnValue3 = pattern3.getConstraints().get( 0 );

        assertEquals( returnValue1,
                      returnValue2 );
        assertFalse( returnValue1.equals( returnValue3 ) );
        assertFalse( returnValue2.equals( returnValue3 ) );
    }

    @Test
    public void testPredicate() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        final BindingDescr fieldBindingDescr = new BindingDescr( "x",
                                                                 "price" );
        pattern.addConstraint( fieldBindingDescr );

        final BindingDescr fieldBindingDescr2 = new BindingDescr( "y",
                                                                  "price" );
        pattern.addConstraint( fieldBindingDescr2 );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        final ExprConstraintDescr predicate = new ExprConstraintDescr( "eval(( ( Integer )map.get( new Integer(x) )).intValue() == y)" );
        pattern.addConstraint( predicate );

        ruleDescr.setConsequence( "update(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors().getErrors() );
    }

    @Test
    public void testPredicateMethodCompare() {
        final KnowledgeBuilderImpl builder1 = new KnowledgeBuilderImpl();
        final PackageDescr packageDescr1 = new PackageDescr( "package1" );
        createPredicateRule( packageDescr1,
                             "eval(x==y)" );
        builder1.addPackage( packageDescr1 );
        if ( builder1.hasErrors() ) {
           fail( builder1.getErrors().toString() );
        }
        final Pattern pattern1 = (Pattern) ((RuleImpl)builder1.getPackage().getRules().iterator().next()).getLhs().getChildren().get( 0 );
        final PredicateConstraint predicate1 = (PredicateConstraint) pattern1.getConstraints().get( 0 );

        final KnowledgeBuilderImpl builder2 = new KnowledgeBuilderImpl();
        final PackageDescr packageDescr2 = new PackageDescr( "package2" );
        createPredicateRule( packageDescr2,
                             "eval(x==y)" );
        builder2.addPackage( packageDescr2 );
        if ( builder2.hasErrors() ) {
            fail( builder2.getErrors().toString() );
         }
        
        final Pattern pattern2 = (Pattern) ((RuleImpl)builder2.getPackage().getRules().iterator().next()).getLhs().getChildren().get( 0 );
        final PredicateConstraint predicate2 = (PredicateConstraint) pattern2.getConstraints().get( 0 );

        final KnowledgeBuilderImpl builder3 = new KnowledgeBuilderImpl();
        if ( builder3.hasErrors() ) {
            fail( builder3.getErrors().toString() );
         }        
        final PackageDescr packageDescr3 = new PackageDescr( "package3" );
        createPredicateRule( packageDescr3,
                             "eval(x!=y)" );
        builder3.addPackage( packageDescr3 );
        final Pattern pattern3 = (Pattern) ((RuleImpl)builder3.getPackage().getRules().iterator().next()).getLhs().getChildren().get( 0 );
        final PredicateConstraint predicate3 = (PredicateConstraint) pattern3.getConstraints().get( 0 );

        assertEquals( predicate1,
                      predicate2 );
        assertFalse( predicate1.equals( predicate3 ) );
        assertFalse( predicate2.equals( predicate3 ) );
    }

    @Test
    public void testEval() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        BindingDescr fieldBindingDescr = new BindingDescr( "x",
                                                           "price" );
        pattern.addConstraint( fieldBindingDescr );
        fieldBindingDescr = new BindingDescr( "y",
                                              "price" );
        pattern.addConstraint( fieldBindingDescr );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        final EvalDescr evalDescr = new EvalDescr( "( ( Integer )map.get( new Integer(x) ) ).intValue() == y" );
        lhs.addDescr( evalDescr );

        ruleDescr.setConsequence( "update(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors().getErrors() );

        InternalKnowledgePackage pkg = builder.getPackage();
        final RuleImpl rule = pkg.getRule( "rule-1" );
        final EvalCondition eval = (EvalCondition) rule.getLhs().getChildren().get( 1 );
        final CompiledInvoker invoker = (CompiledInvoker) eval.getEvalExpression();
        final List list = invoker.getMethodBytecode();
    }

    @Test
    public void testEvalMethodCompare() {
        final KnowledgeBuilderImpl builder1 = new KnowledgeBuilderImpl();
        final PackageDescr packageDescr1 = new PackageDescr( "package1" );
        createEvalRule( packageDescr1,
                        "1==1" );
        builder1.addPackage( packageDescr1 );
        final EvalCondition eval1 = (EvalCondition) ((RuleImpl)builder1.getPackage().getRules().iterator().next()).getLhs().getChildren().get( 0 );

        final KnowledgeBuilderImpl builder2 = new KnowledgeBuilderImpl();
        final PackageDescr packageDescr2 = new PackageDescr( "package2" );
        createEvalRule( packageDescr2,
                        "1==1" );
        builder2.addPackage( packageDescr2 );
        final EvalCondition eval2 = (EvalCondition) ((RuleImpl)builder2.getPackage().getRules().iterator().next()).getLhs().getChildren().get( 0 );

        final KnowledgeBuilderImpl builder3 = new KnowledgeBuilderImpl();
        final PackageDescr packageDescr3 = new PackageDescr( "package3" );
        createEvalRule( packageDescr3,
                        "1==3" );
        builder3.addPackage( packageDescr3 );
        final EvalCondition eval3 = (EvalCondition) ((RuleImpl)builder3.getPackage().getRules().iterator().next()).getLhs().getChildren().get( 0 );

        assertEquals( eval1,
                      eval2 );
        assertFalse( eval1.equals( eval3 ) );
        assertFalse( eval2.equals( eval3 ) );
    }

    @Test
    public void testOr() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        final RuleImpl rule = createRule( new OrDescr(),
                                      builder,
                                      "update(stilton);" );
        assertLength( 0,
                      builder.getErrors().getErrors() );

        final GroupElement lhs = rule.getLhs();
        assertLength( 1,
                      lhs.getChildren() );

        final GroupElement or = (GroupElement) lhs.getChildren().get( 0 );
        assertLength( 1,
                      or.getChildren() );
        final Pattern pattern = (Pattern) or.getChildren().get( 0 );
    }

    @Test
    public void testAnd() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        final RuleImpl rule = createRule( new AndDescr(),
                                      builder,
                                      "update(stilton);" );
        assertLength( 0,
                      builder.getErrors().getErrors() );

        final GroupElement lhs = rule.getLhs();
        assertLength( 1,
                      lhs.getChildren() );

        final GroupElement and = (GroupElement) lhs.getChildren().get( 0 );
        assertLength( 1,
                      and.getChildren() );
        final Pattern pattern = (Pattern) and.getChildren().get( 0 );
    }

    @Test
    public void testNot() throws Exception {
        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        // Make sure we can't accessa  variable bound inside the not node
        RuleImpl rule = createRule( new NotDescr(),
                                builder,
                                "update(stilton);" );

        assertTrue( builder.hasErrors() );

        builder = new KnowledgeBuilderImpl();
        rule = createRule( new NotDescr(),
                           builder,
                           "" );
        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        final GroupElement lhs = rule.getLhs();
        assertLength( 1,
                      lhs.getChildren() );

        final GroupElement not = (GroupElement) lhs.getChildren().get( 0 );
        assertLength( 1,
                      not.getChildren() );
        final Pattern pattern = (Pattern) not.getChildren().get( 0 );
    }

    @Test
    public void testExists() throws Exception {
        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        // Make sure we can't accessa  variable bound inside the not node
        RuleImpl rule = createRule( new ExistsDescr(),
                                builder,
                                "update(stilton);" );
        
        assertTrue( builder.hasErrors() );

        builder = new KnowledgeBuilderImpl();
        rule = createRule( new ExistsDescr(),
                           builder,
                           "" );
        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        final GroupElement lhs = rule.getLhs();
        assertLength( 1,
                      lhs.getChildren() );

        final GroupElement exists = (GroupElement) lhs.getChildren().get( 0 );
        assertLength( 1,
                      exists.getChildren() );
        final Pattern pattern = (Pattern) exists.getChildren().get( 0 );
    }

    @Test
    public void testNumbers() throws Exception {
        // test boolean
        createLiteralRule( new ExprConstraintDescr( "booleanPrimitive == true ") );

        // test boolean
        createLiteralRule( new ExprConstraintDescr( "booleanPrimitive == false ") );

        // test char
        createLiteralRule( new ExprConstraintDescr( "charPrimitive == 'a' ") );
        createLiteralRule( new ExprConstraintDescr( "charPrimitive == \"a\" ") );

        // test byte
        createLiteralRule( new ExprConstraintDescr( "bytePrimitive == 1 ")  );
        createLiteralRule( new ExprConstraintDescr( "bytePrimitive == 0 ")  );
        createLiteralRule(  new ExprConstraintDescr( "bytePrimitive == -1 ") );

        // test short
        createLiteralRule( new ExprConstraintDescr( "shortPrimitive == 1 ")  );               
        createLiteralRule(  new ExprConstraintDescr( "shortPrimitive == 0 ")  );        
        createLiteralRule( new ExprConstraintDescr( "shortPrimitive == -1 ")  );

        // test int
        createLiteralRule( new ExprConstraintDescr( "intPrimitive == 1") );
        createLiteralRule(  new ExprConstraintDescr( "intPrimitive == 0") );
        createLiteralRule(  new ExprConstraintDescr( "intPrimitive == -1")  );

        // test long
        createLiteralRule( new ExprConstraintDescr( "longPrimitive == 1") );
        createLiteralRule( new ExprConstraintDescr( "longPrimitive == 0") );
        createLiteralRule( new ExprConstraintDescr( "longPrimitive == -1") );

        // test float
        createLiteralRule( new ExprConstraintDescr( "floatPrimitive == 1.1") );
        createLiteralRule( new ExprConstraintDescr( "floatPrimitive == 0") );
        createLiteralRule( new ExprConstraintDescr( "floatPrimitive == -1.1") );

        // test double
        createLiteralRule(  new ExprConstraintDescr( "doublePrimitive == 1.1") );
        createLiteralRule(  new ExprConstraintDescr( "doublePrimitive == 0") );
        createLiteralRule(  new ExprConstraintDescr( "doublePrimitive == -1.1") );
    }

    @Test
    public void testNull() {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr patternDescr = new PatternDescr( Cheese.class.getName(),
                                                            "stilton" );

        final FieldConstraintDescr literalDescr = new FieldConstraintDescr( "type" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  null ) );

        patternDescr.addConstraint( literalDescr );

        ruleDescr.setConsequence( "" );

        builder.addPackage( packageDescr );

        InternalKnowledgePackage pkg = builder.getPackage();
        final RuleImpl rule = pkg.getRule( "rule-1" );

        assertLength( 0,
                      builder.getErrors().getErrors() );
    }
    
    @Test
    public void testWarnings() {
        System.setProperty( "drools.kbuilder.severity." + DuplicateRule.KEY, "WARNING");
        
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        
        final PackageDescr packageDescr1 = createBasicPackageWithOneRule(11, 1);
        
        final PackageDescr packageDescr2 = createBasicPackageWithOneRule(22, 2);
        
        builder.addPackage(packageDescr1);
        builder.addPackage(packageDescr2);
        
        assertFalse(builder.hasErrors());
        assertTrue(builder.hasWarnings());
       
    }
    
    @Test
    public void testWarningsReportAsErrors() {
        System.setProperty( "drools.kbuilder.severity." + DuplicateRule.KEY, "ERROR");
        KnowledgeBuilderConfigurationImpl cfg = new KnowledgeBuilderConfigurationImpl();
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl(cfg);
        
        final PackageDescr packageDescr1 = createBasicPackageWithOneRule(11, 1);
        
        final PackageDescr packageDescr2 = createBasicPackageWithOneRule(22, 2);
        
        builder.addPackage(packageDescr1);
        builder.addPackage(packageDescr2);
        
        assertTrue(builder.hasErrors());
        assertFalse(builder.hasWarnings());
       
    }
    
    @Test
    public void testResetWarnings() {
        
        System.setProperty( "drools.kbuilder.severity." + DuplicateRule.KEY, "WARNING");
        
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        
        final PackageDescr packageDescr1 = createBasicPackageWithOneRule(11, 1);
        
        final PackageDescr packageDescr2 = createBasicPackageWithOneRule(22, 2);
        
        builder.addPackage(packageDescr1);
        builder.addPackage(packageDescr2);
        
        assertTrue(builder.hasWarnings());
        
        builder.resetWarnings();
        assertFalse(builder.hasWarnings());
        
        builder.addPackage(packageDescr1);
        
        assertTrue(builder.hasWarnings());
    }
    
    @Test
    public void testResetProblems() throws DroolsParserException, IOException {
        System.setProperty( "drools.kbuilder.severity." + DuplicateRule.KEY, "WARNING");
        System.setProperty( "drools.kbuilder.severity." + DuplicateFunction.KEY, "ERROR");
        
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        
        final PackageDescr packageDescr1 = createBasicPackageWithOneRule(11, 1);
        
        final PackageDescr packageDescr2 = createBasicPackageWithOneRule(22, 2);
        
        builder.addPackage(packageDescr1);
        builder.addPackage(packageDescr2);
        builder.addPackageFromDrl( new StringReader( "package foo \n rule ORB" ) );
        
        builder.addPackageFromDrl( new StringReader( "package org.drools\n" + "function boolean testIt() {\n" + "  return true;\n" + "}\n" ) );
        builder.addPackageFromDrl( new StringReader( "package org.drools\n" + "function boolean testIt() {\n" + "  return false;\n" + "}\n" ) );
        assertTrue(builder.hasWarnings());
        assertTrue(builder.hasErrors());
        
        builder.resetProblems();
        assertFalse(builder.hasWarnings());
        assertFalse(builder.hasErrors());
    }
    
    @Test
    public void testResetWarningsButNotErrors() throws DroolsParserException, IOException {
        System.setProperty( "drools.kbuilder.severity." + DuplicateRule.KEY, "WARNING");
        System.setProperty( "drools.kbuilder.severity." + DuplicateFunction.KEY, "ERROR");
        
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        
        final PackageDescr packageDescr1 = createBasicPackageWithOneRule(11, 1);
        
        final PackageDescr packageDescr2 = createBasicPackageWithOneRule(22, 2);
        
        builder.addPackage(packageDescr1);
        builder.addPackage(packageDescr2);
        builder.addPackageFromDrl( new StringReader( "package foo \n rule ORB" ) );
        
        builder.addPackageFromDrl( new StringReader( "package org.drools\n" + "function boolean testIt() {\n" + "  return true;\n" + "}\n" ) );
        builder.addPackageFromDrl( new StringReader( "package org.drools\n" + "function boolean testIt() {\n" + "  return false;\n" + "}\n" ) );
        assertTrue(builder.hasWarnings());
        assertTrue(builder.hasErrors());
        
        builder.resetWarnings();
        assertFalse(builder.hasWarnings());
        assertTrue(builder.hasErrors());
    }
    
    @Test
    public void testWarnOnFunctionReplacement() throws DroolsParserException, IOException {
        System.setProperty( "drools.kbuilder.severity." + DuplicateFunction.KEY, "WARNING");
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        builder.addPackageFromDrl( new StringReader( "package org.drools\n" + "function boolean testIt() {\n" + "  return true;\n" + "}\n" ) );
        builder.addPackageFromDrl( new StringReader( "package org.drools\n" + "function boolean testIt() {\n" + "  return false;\n" + "}\n" ) );
        assertTrue(builder.hasWarnings());
        
    }

    @Test
    public void testDuplicateRuleNames() throws Exception {

        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        RuleDescr ruleDescr;
        AndDescr lhs;
        PatternDescr patternDescr;
        FieldConstraintDescr literalDescr;
        final PackageDescr packageDescr = createBasicPackageWithOneRule(1,1);
        ruleDescr = new RuleDescr( "rule-1" );
        ruleDescr.setLocation( 42,
                               43 );
        packageDescr.addRule( ruleDescr );
        lhs = new AndDescr();
        ruleDescr.setLhs( lhs );
        patternDescr = new PatternDescr( Cheese.class.getName(),
                                         "stilton" );
        literalDescr = new FieldConstraintDescr( "type" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "!=",
                                                                  null ) );
        patternDescr.addConstraint( literalDescr );
        ruleDescr.setConsequence( "" );

        ruleDescr = new RuleDescr( "rule-2" );
        ruleDescr.setLocation( 42,
                               43 );
        packageDescr.addRule( ruleDescr );
        lhs = new AndDescr();
        ruleDescr.setLhs( lhs );
        patternDescr = new PatternDescr( Cheese.class.getName(),
                                         "stilton" );

        literalDescr = new FieldConstraintDescr( "type" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "!=",
                                                                  null ) );

        patternDescr.addConstraint( literalDescr );
        ruleDescr.setConsequence( "" );

        builder.addPackage( packageDescr );

        assertLength( 2,
                      builder.getErrors().getErrors() );
        final ParserError err = (ParserError) builder.getErrors().getErrors()[0];
        assertEquals( 42,
                      err.getRow() );
        assertEquals( 43,
                      err.getCol() );

    }

    private PackageDescr createBasicPackageWithOneRule(int line, int col) {
        PackageDescr packageDescr = new PackageDescr( "p1" );

        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        ruleDescr.setLocation( line,
                col );
        packageDescr.addRule( ruleDescr );
        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );
        PatternDescr patternDescr = new PatternDescr( Cheese.class.getName(),
                                                      "stilton" );
        FieldConstraintDescr literalDescr = new FieldConstraintDescr( "type" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  null ) );
        patternDescr.addConstraint( literalDescr );
        ruleDescr.setConsequence( "" );

        return packageDescr;
    }

    @Test @Ignore // TODO we now allow bindings on declarations, so update the test for this
    public void testDuplicateDeclaration() {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern1 = new PatternDescr( Cheese.class.getName() );
        lhs.addDescr( pattern1 );

        final BindingDescr fieldBindingDescr = new BindingDescr( "$type",
                                                                 "type" );

        final FieldConstraintDescr literalDescr = new FieldConstraintDescr( "type" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "stilton" ) );

        pattern1.addConstraint( fieldBindingDescr );
        pattern1.addConstraint( literalDescr );

        final PatternDescr pattern2 = new PatternDescr( Cheese.class.getName() );
        lhs.addDescr( pattern2 );
        pattern2.addConstraint( fieldBindingDescr );

        ruleDescr.setConsequence( "update(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 2,
                      builder.getErrors().getErrors() );
    }

    @Test
    public void testCompilerConfiguration() throws Exception {
        // test default is eclipse jdt core
        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        PackageDescr pkgDescr = new PackageDescr( "org.drools.compiler.test" );
        builder.addPackage( pkgDescr );
        DialectCompiletimeRegistry reg = builder.getPackageRegistry( pkgDescr.getName() ).getDialectCompiletimeRegistry();

        final Field dialectField = builder.getClass().getDeclaredField( "defaultDialect" );
        dialectField.setAccessible( true );
        String dialectName = (String) dialectField.get( builder );

        reg = builder.getPackageRegistry( pkgDescr.getName() ).getDialectCompiletimeRegistry();
        Dialect dialect = reg.getDialect( dialectName );

        final Field compilerField = dialect.getClass().getDeclaredField( "compiler" );
        compilerField.setAccessible( true );
        JavaCompiler compiler = (JavaCompiler) compilerField.get( dialect );

        KnowledgeBuilderConfigurationImpl conf = new KnowledgeBuilderConfigurationImpl();
        JavaDialectConfiguration javaConf = (JavaDialectConfiguration) conf.getDialectConfiguration( "java" );
        switch( javaConf.getCompiler() ) {
            case JavaDialectConfiguration.NATIVE : assertSame( NativeJavaCompiler.class, compiler.getClass() );
                break;
            case JavaDialectConfiguration.ECLIPSE: assertSame( EclipseJavaCompiler.class, compiler.getClass() );
                break;
            case JavaDialectConfiguration.JANINO: assertSame( JaninoJavaCompiler.class, compiler.getClass() );
                break;
            default:
                fail( "Unrecognized java compiler");
        }

        // test JANINO with property settings
        conf = new KnowledgeBuilderConfigurationImpl();
        javaConf = (JavaDialectConfiguration) conf.getDialectConfiguration( "java" );
        javaConf.setCompiler( JavaDialectConfiguration.JANINO );
        builder = new KnowledgeBuilderImpl( conf );
        builder.addPackage( pkgDescr );

        dialectName = (String) dialectField.get( builder );
        reg = builder.getPackageRegistry( pkgDescr.getName() ).getDialectCompiletimeRegistry();
        dialect = reg.getDialect( dialectName );
        compiler = (JavaCompiler) compilerField.get( dialect );
        assertSame( JaninoJavaCompiler.class,
                    compiler.getClass() );

        // test eclipse jdt core with property settings and default source level
        conf = new KnowledgeBuilderConfigurationImpl();
        javaConf = (JavaDialectConfiguration) conf.getDialectConfiguration( "java" );
        javaConf.setCompiler( JavaDialectConfiguration.ECLIPSE );
        builder = new KnowledgeBuilderImpl( conf );
        builder.addPackage( pkgDescr );

        dialectName = (String) dialectField.get( builder );
        reg = builder.getPackageRegistry( pkgDescr.getName() ).getDialectCompiletimeRegistry();
        dialect = reg.getDialect( dialectName );
        compiler = (JavaCompiler) compilerField.get( dialect );
        assertSame( EclipseJavaCompiler.class,
                    compiler.getClass() );
    }

    @Test
    public void testTypeDeclaration() throws Exception {
        PackageDescr pkgDescr = new PackageDescr( "org.drools.compiler" );
        TypeDeclarationDescr typeDescr = new TypeDeclarationDescr( "StockTick" );
        typeDescr.addAnnotation( TypeDeclaration.Role.ID,
                                    "event" );
        typeDescr.addAnnotation( TypeDeclaration.ATTR_CLASS,
                                    "org.drools.compiler.StockTick" );
        pkgDescr.addTypeDeclaration( typeDescr );

        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        builder.addPackage( pkgDescr );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        InternalKnowledgePackage pkg = builder.getPackage();
        assertEquals( 1,
                      pkg.getTypeDeclarations().size() );

        TypeDeclaration type = pkg.getTypeDeclaration( "StockTick" );
        assertEquals( "StockTick",
                      type.getTypeName() );
        assertEquals( TypeDeclaration.Role.EVENT,
                      type.getRole() );
        assertEquals( StockTick.class,
                      type.getTypeClass() );
    }

    @Test
    public void testTypeDeclarationNewBean() throws Exception {
        PackageDescr pkgDescr = new PackageDescr( "org.drools.compiler.test" );
        TypeDeclarationDescr typeDescr = new TypeDeclarationDescr( "NewBean" );

        TypeFieldDescr f1 = new TypeFieldDescr( "name",
                                                new PatternDescr( "String" ) );
        TypeFieldDescr f2 = new TypeFieldDescr( "age",
                                                new PatternDescr( "int" ) );

        typeDescr.addField( f1 );
        typeDescr.addField( f2 );

        pkgDescr.addTypeDeclaration( typeDescr );

        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        builder.addPackage( pkgDescr );

        InternalKnowledgePackage pkg = builder.getPackage();
        assertEquals( 1,
                      pkg.getTypeDeclarations().size() );

        TypeDeclaration type = pkg.getTypeDeclaration( "NewBean" );
        assertEquals( "NewBean",
                      type.getTypeName() );
        assertEquals( TypeDeclaration.Role.FACT,
                      type.getRole() );
        assertEquals( "org.drools.compiler.test.NewBean",
                      type.getTypeClass().getName() );
        assertFalse( builder.hasErrors() );

        InternalKnowledgePackage bp = builder.getPackage();

        Class newBean = bp.getPackageClassLoader().loadClass( "org.drools.compiler.test.NewBean" );
        assertNotNull( newBean );
    }

    @Test
    public void testPackageMerge() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        try {
            builder.addPackage( new PackageDescr( "org.drools" ) );

            builder.addPackageFromDrl( new StringReader( "package org.drools\n" + "function boolean testIt() {\n" + "  return true;\n" + "}\n" ) );
        } catch ( RuntimeException e ) {
            fail( "Should not raise any exception: " + e.getMessage() );
        }
    }

    private void createReturnValueRule( final PackageDescr packageDescr,
                                        final String expression ) {
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        BindingDescr fieldBindingDescr = new BindingDescr( "x",
                                                           "price" );
        pattern.addConstraint( fieldBindingDescr );
        fieldBindingDescr = new BindingDescr( "y",
                                              "price" );
        pattern.addConstraint( fieldBindingDescr );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        pattern.addConstraint( new ExprConstraintDescr("price == (" + expression + ")") );

        ruleDescr.setConsequence( "update(stilton);" );
    }

    private void createPredicateRule( final PackageDescr packageDescr,
                                      final String expression ) {
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        final BindingDescr fieldBindingDescr = new BindingDescr( "x",
                                                                 "price" );
        pattern.addConstraint( fieldBindingDescr );

        final BindingDescr fieldBindingDescr2 = new BindingDescr( "y",
                                                                  "price" );
        pattern.addConstraint( fieldBindingDescr2 );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        pattern.addConstraint( new ExprConstraintDescr( expression ) );

        ruleDescr.setConsequence( "update(stilton);" );
    }

    private void createEvalRule( final PackageDescr packageDescr,
                                 final String expression ) {
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        final EvalDescr evalDescr = new EvalDescr( expression );
        lhs.addDescr( evalDescr );

        ruleDescr.setConsequence( "" );
    }

    private void createLiteralRule( final BaseDescr descr ) {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Primitives.class.getName() );
        lhs.addDescr( pattern );

        pattern.addConstraint( descr );

        ruleDescr.setConsequence( "" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors().getErrors() );
    }

    private RuleImpl createRule( final ConditionalElementDescr ceDescr,
                             final KnowledgeBuilderImpl builder,
                             final String consequence ) throws Exception {
        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        lhs.addDescr( (BaseDescr) ceDescr );

        final PatternDescr patternDescr = new PatternDescr( Cheese.class.getName(),
                                                            "stilton" );

        patternDescr.addConstraint( new ExprConstraintDescr( "type == \"stilton\" ")  );

        ceDescr.addDescr( patternDescr );

        ruleDescr.setConsequence( consequence );

        builder.addPackage( packageDescr );

        InternalKnowledgePackage pkg = builder.getPackage();
        final RuleImpl rule = pkg.getRule( "rule-1" );

        assertEquals( "rule-1",
                      rule.getName() );

        return rule;
    }

    @Test
    public void testJaninoWithStaticImports() throws Exception {
        KnowledgeBuilderConfigurationImpl cfg = new KnowledgeBuilderConfigurationImpl();
        JavaDialectConfiguration javaConf = (JavaDialectConfiguration) cfg.getDialectConfiguration( "java" );
        javaConf.setCompiler( JavaDialectConfiguration.JANINO );

        KnowledgeBuilderImpl bldr = new KnowledgeBuilderImpl( cfg );
        bldr.addPackageFromDrl( new StringReader( "package testBuilderPackageConfig \n import java.util.List" ) );
        bldr.addPackageFromDrl( new StringReader( "package testBuilderPackageConfig \n function void doSomething() {\n System.err.println(List.class.toString()); }" ) );

        assertFalse( bldr.hasErrors() );
    }

    @Test
    public void testSinglePackage() throws Exception {
        KnowledgeBuilderConfigurationImpl cfg = new KnowledgeBuilderConfigurationImpl();
        cfg.setAllowMultipleNamespaces( false );
        KnowledgeBuilderImpl bldr = new KnowledgeBuilderImpl( cfg );
        bldr.addPackageFromDrl( new StringReader( "package whee\n import org.drools.compiler.Cheese" ) );
        assertFalse( bldr.hasErrors() );
        bldr.addPackageFromDrl( new StringReader( "package whee\n import org.drools.compiler.Person" ) );
        assertFalse( bldr.hasErrors() );
        // following package will not be added because configuration is set for single namespace builders
        bldr.addPackageFromDrl( new StringReader( "package whee2\n import org.drools.compiler.Person" ) );
        assertFalse( bldr.hasErrors() );

        assertEquals( 1,
                      bldr.getPackages().length );

        cfg = new KnowledgeBuilderConfigurationImpl();
        assertEquals( true,
                      cfg.isAllowMultipleNamespaces() );
        bldr = new KnowledgeBuilderImpl( cfg );
        bldr.addPackageFromDrl( new StringReader( "package whee\n import org.drools.compiler.Cheese" ) );
        assertFalse( bldr.hasErrors() );
        // following import will be added to the default package name
        bldr.addPackageFromDrl( new StringReader( "import org.drools.compiler.Person" ) );
        assertFalse( bldr.hasErrors() );
        bldr.addPackageFromDrl( new StringReader( "package whee2\n import org.drools.compiler.Person" ) );
        assertFalse( bldr.hasErrors() );

        assertEquals( 3,
                      bldr.getPackages().length );
    }

    @Test
    public void testTimeWindowBehavior() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final TypeDeclarationDescr typeDeclDescr = new TypeDeclarationDescr( StockTick.class.getName() );
        typeDeclDescr.addAnnotation( "role",
                                     "event" );
        packageDescr.addTypeDeclaration( typeDeclDescr );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr patternDescr = new PatternDescr( StockTick.class.getName(),
                                                            "$tick" );
        final BehaviorDescr windowDescr = new BehaviorDescr( "window" );
        windowDescr.setSubType( "time" );
        windowDescr.setParameters( Collections.singletonList( "60000" ) );
        patternDescr.addBehavior( windowDescr );

        lhs.addDescr( patternDescr );

        ruleDescr.setConsequence( "System.out.println( $tick );" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors().getErrors() );

        InternalKnowledgePackage pkg = builder.getPackageRegistry().get( "p1" ).getPackage();
        final RuleImpl rule = pkg.getRule( "rule-1" );
        assertNotNull( rule );

        final Pattern pattern = (Pattern) rule.getLhs().getChildren().get( 0 );
        assertEquals( StockTick.class.getName(),
                      ((ClassObjectType) pattern.getObjectType()).getClassType().getName() );
        final Behavior window = pattern.getBehaviors().get( 0 );
        assertEquals( Behavior.BehaviorType.TIME_WINDOW,
                      window.getType() );
        assertEquals( 60000,
                      ((SlidingTimeWindow) window).getSize() );
    }
    
    @Test
    public void testDeclaredSuperTypeFields() throws Exception {
        String drl = "package foo \n"
                     + "declare Bean1 \n"
                     + "age: int \n"
                     + "name : String \n"
                     + "end \n"
                     + "declare Bean2 extends Bean1\n"
                     + "cheese : String \n"
                     + "end";

        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        builder.addPackageFromDrl( new StringReader( drl ) );

        List<FactField> fieldsBean1 = builder.getPackage().getFactType( "foo.Bean1" ).getFields();
        assertEquals( 2,
                      fieldsBean1.size() );
        assertEquals( "age",
                      fieldsBean1.get( 0 ).getName() );
        assertEquals( int.class,
                      fieldsBean1.get( 0 ).getType() );
        assertEquals( "name",
                      fieldsBean1.get( 1 ).getName() );
        assertEquals( String.class,
                      fieldsBean1.get( 1 ).getType() );

        List<FactField> fieldsBean2 = builder.getPackage().getFactType( "foo.Bean2" ).getFields();
        assertEquals( 3,
                      fieldsBean2.size() );
        assertEquals( "age",
                      fieldsBean2.get( 0 ).getName() );
        assertEquals( int.class,
                      fieldsBean2.get( 0 ).getType() );
        assertEquals( "name",
                      fieldsBean2.get( 1 ).getName() );
        assertEquals( String.class,
                      fieldsBean2.get( 1 ).getType() );
        assertEquals( "cheese",
                      fieldsBean2.get( 2 ).getName() );
        assertEquals( String.class,
                      fieldsBean2.get( 2 ).getType() );
    }

    class MockActivation<T extends ModedAssertion<T>>
        implements
        Activation<T> {
        private RuleImpl               rule;
        private int                salience;
        private final GroupElement subrule;
        private LeftTupleImpl          tuple;

        public MockActivation(final RuleImpl rule,
                              int salience,
                              final GroupElement subrule,
                              final LeftTupleImpl tuple) {
            this.rule = rule;
            this.salience = salience;
            this.tuple = tuple;
            this.subrule = subrule;
        }

        public RuleImpl getRule() {
            return this.rule;
        }

        public Consequence getConsequence() {
            return getRule().getConsequence();
        }

        public int getSalience() {
            return this.salience;
        }

        public LeftTupleImpl getTuple() {
            return this.tuple;
        }

        public PropagationContext getPropagationContext() {
            return null;
        }

        public long getActivationNumber() {
            return 0;
        }

        public void remove() {
        }

        public void addLogicalDependency( final LogicalDependency<T> node ) {
        }

        public LinkedList<LogicalDependency<T>> getLogicalDependencies() {
            return null;
        }

        public boolean isQueued() {
            return false;
        }

        public void setQueued(final boolean activated) {
        }

        public ActivationGroupNode getActivationGroupNode() {
            return null;
        }

        public void setActivationGroupNode( final ActivationGroupNode activationGroupNode ) {
        }

        public GroupElement getSubRule() {
            return this.subrule;
        }

        public InternalAgendaGroup getAgendaGroup() {
            return null;
        }

        public InternalRuleFlowGroup getRuleFlowGroup() {
            return null;
        }

        public ActivationNode getActivationNode() {
            return null;
        }

        public void setActivationNode( final ActivationNode ruleFlowGroupNode ) {
        }
        public List<FactHandle> getFactHandles() {
            return null;
        }

        public List<Object> getObjects() {
            return null;
        }

        public Object getDeclarationValue( String variableName ) {
            return null;
        }

        public List<String> getDeclarationIds() {
            return null;
        }

        public InternalFactHandle getFactHandle() {
            return null;
        }

        public boolean isAdded() {
            return false;
        }
        
        public void addBlocked(LogicalDependency node) {
        }

        public LinkedList getBlocked() {
            return null;
        }

        public void addBlocked(LinkedListNode node) {
        }

        public LinkedList getBlockers() {
            return null;
        }

        public boolean isMatched() {
            return false;
        }

        public void setMatched(boolean matched) { }

        public boolean isActive() {
            return false;
        }

        public void setActive(boolean active) { }

        public boolean isRuleAgendaItem() {
            return false;
        }

        @Override
        public void setBlocked(LinkedList<LogicalDependency<SimpleMode>> justified) {

        }

        @Override
        public void setLogicalDependencies(LinkedList<LogicalDependency<T>> justified) {

        }

        @Override
        public void setQueueIndex(int index) {
        }

        @Override
        public int getQueueIndex() {
            return 0;
        }

        @Override
        public void dequeue() {
        }
    }

    class MockTuple
        extends
        LeftTupleImpl {
        private Map declarations;

        public MockTuple(final Map declarations) {
            this.declarations = declarations;
        }

        public InternalFactHandle get( final int patern ) {
            return null;
        }

        public InternalFactHandle get( final Declaration declaration ) {
            return (InternalFactHandle) this.declarations.get( declaration );
        }

        public InternalFactHandle[] toFactHandles() {
            return (InternalFactHandle[]) this.declarations.values().toArray( new FactHandle[0] );
        }

        public boolean dependsOn( final FactHandle handle ) {
            return false;
        }

        public void setActivation( final Activation activation ) {
        }

        public long getRecency() {
            return 0;
        }

        public int size() {
            return 0;
        }

    }
}
