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
package org.drools.mvel.compiler.builder.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.DuplicateFunction;
import org.drools.compiler.compiler.DuplicateRule;
import org.drools.base.base.ClassObjectType;
import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationNode;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.common.PropagationContext;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.rule.Behavior;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.EvalCondition;
import org.drools.base.rule.GroupElement;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.base.rule.Pattern;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.base.rule.TypeDeclaration;
import org.drools.base.rule.accessor.CompiledInvoker;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.base.rule.consequence.Consequence;
import org.drools.base.rule.constraint.Constraint;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.BehaviorDescr;
import org.drools.drl.ast.descr.BindingDescr;
import org.drools.drl.ast.descr.ConditionalElementDescr;
import org.drools.drl.ast.descr.EvalDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.FieldConstraintDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.LiteralRestrictionDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.OrDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.drl.ast.descr.TypeFieldDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.parser.ParserError;
import org.drools.ecj.EclipseJavaCompiler;
import org.drools.kiesession.consequence.DefaultKnowledgeHelper;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.MockBetaNode;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Primitives;
import org.drools.mvel.compiler.StockTick;
import org.drools.mvel.integrationtests.SerializationHelper;
import org.drools.mvel.java.JavaForMvelDialectConfiguration;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.TypeSafe;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.jdknative.NativeJavaCompiler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class KnowledgeBuilderTest {
    
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

        assertThat(builder.getErrors().getErrors().length > 0).isTrue();
    }

    @Test
    public void testErrorsInParser() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        builder.addPackageFromDrl( new InputStreamReader( this.getClass().getResourceAsStream( "bad_rule.drl" ) ) );
        assertThat(builder.hasErrors()).isTrue();
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

        InternalKnowledgePackage pkg = builder.getPackage(packageDescr.getName());
        RuleImpl rule = pkg.getRule( "rule-1" );

        assertThat(builder.getErrors().getErrors()).hasSize(0);

        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addGlobal( "map", Map.class );
        final KieSession workingMemory = kBase.newKieSession();

        final HashMap map = new HashMap();
        workingMemory.setGlobal( "map",
                                 map );

        final LeftTuple tuple = new MockTuple(new HashMap() );
        tuple.setLeftTupleSink( new RuleTerminalNode(1, new MockBetaNode(), rule,rule.getLhs(), 0,new BuildContext(kBase, Collections.emptyList()) )  );
        final InternalMatch internalMatch = new MockInternalMatch(rule,
                                                                  0,
                                                                  rule.getLhs(),
                                                                  tuple );

        DefaultKnowledgeHelper knowledgeHelper = new DefaultKnowledgeHelper( ((StatefulKnowledgeSessionImpl)workingMemory) );
        knowledgeHelper.setActivation(internalMatch);

        rule.getConsequence().evaluate( knowledgeHelper,
                                        ((StatefulKnowledgeSessionImpl)workingMemory) );
        assertThat(map.get("value")).isEqualTo(Integer.valueOf( 1 ));

        ruleDescr.setConsequence( "map.put(\"value\", new Integer(2) );" );
        pkg.removeRule( rule );

        // Make sure the compiled classes are also removed
        assertThat(((JavaDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData("java")).getStore().size()).isEqualTo(0);

        builder.addPackage( packageDescr );

        pkg = builder.getPackage(packageDescr.getName());

        rule = pkg.getRule( "rule-1" );

        knowledgeHelper = new DefaultKnowledgeHelper( ((StatefulKnowledgeSessionImpl)workingMemory) );
        knowledgeHelper.setActivation(internalMatch);

        rule.getConsequence().evaluate( knowledgeHelper,
                                        ((StatefulKnowledgeSessionImpl)workingMemory) );
        assertThat(map.get("value")).isEqualTo(Integer.valueOf( 2 ));

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
        final PackageDescr back = SerializationHelper.serializeObject(packageDescr);
        assertThat(back).isNotNull();
        assertThat(back.getName()).isEqualTo("p1");

        builder.addPackage( packageDescr );
        InternalKnowledgePackage pkg = builder.getPackage(packageDescr.getName());
        final RuleImpl rule = pkg.getRule( "rule-1" );

        assertThat(builder.getErrors().getErrors()).hasSize(0);

        InternalKnowledgePackage newPkg = SerializationHelper.serializeObject( pkg );
        final RuleImpl newRule = newPkg.getRule( "rule-1" );

        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();;

        // It's been serialised so we have to simulate the re-wiring process
        newPkg.getDialectRuntimeRegistry().onAdd( kBase.getRootClassLoader() );
        newPkg.getDialectRuntimeRegistry().onBeforeExecute();

        kBase.getGlobals().put( "map", Map.class );
        final KieSession workingMemory = kBase.newKieSession();

        final HashMap map = new HashMap();

        workingMemory.setGlobal( "map",
                                 map );

        final LeftTuple tuple = new MockTuple(new HashMap() );
        tuple.setLeftTupleSink( new RuleTerminalNode(1, new MockBetaNode(), newRule,newRule.getLhs(), 0, new BuildContext(kBase, Collections.emptyList()) )  );
        final InternalMatch internalMatch = new MockInternalMatch(newRule,
                                                                  0,
                                                                  newRule.getLhs(),
                                                                  tuple );

        final DefaultKnowledgeHelper knowledgeHelper = new DefaultKnowledgeHelper( ((StatefulKnowledgeSessionImpl)workingMemory) );
        knowledgeHelper.setActivation(internalMatch);

        newRule.getConsequence().evaluate( knowledgeHelper,
                                           ((StatefulKnowledgeSessionImpl)workingMemory) );
        assertThat(map.get("value")).isEqualTo(Integer.valueOf( 1 ));
    }

    @Test
    @Ignore
    public void testNoPackageName() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        try {
            builder.addPackage( new PackageDescr( null ) );
            fail( "should have errored here." );
        } catch ( final RuntimeException e ) {
            assertThat(e.getMessage()).isNotNull();
        }
        try {
            builder.addPackage( new PackageDescr( "" ) );
            fail( "should have errored here." );
        } catch ( final RuntimeException e ) {
            assertThat(e.getMessage()).isNotNull();
        }

        builder.addPackageFromDrl( new StringReader( "package foo" ) );
        builder.addPackageFromDrl( new StringReader( "rule x then end" ) );

    }

    @Test
    public void testErrorReset() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        builder.addPackageFromDrl( new StringReader( "package foo \n rule ORB" ) );
        assertThat(builder.hasErrors()).isTrue();

        builder.resetErrors();
        assertThat(builder.hasErrors()).isFalse();

        builder.addPackageFromDrl( new StringReader( "package foo \n rule ORB" ) );
        assertThat(builder.hasErrors()).isTrue();
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

        assertThat(builder.getErrors().getErrors()).hasSize(0);
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

        assertThat(builder.getErrors().getErrors().length).as("Should not have any errors").isEqualTo(0);
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
        final Pattern pattern1 = (Pattern) ((RuleImpl)builder1.getPackage("package1").getRules().iterator().next()).getLhs().getChildren().get( 0 );
        final Constraint returnValue1 = pattern1.getConstraints().get( 0 );

        final KnowledgeBuilderImpl builder2 = new KnowledgeBuilderImpl();
        final PackageDescr packageDescr2 = new PackageDescr( "package2" );
        createReturnValueRule( packageDescr2,
                               " x + y " );
        builder2.addPackage( packageDescr2 );
        final Pattern pattern2 = (Pattern) ((RuleImpl)builder2.getPackage("package2").getRules().iterator().next()).getLhs().getChildren().get( 0 );
        final Constraint returnValue2 = pattern2.getConstraints().get( 0 );

        final KnowledgeBuilderImpl builder3 = new KnowledgeBuilderImpl();
        final PackageDescr packageDescr3 = new PackageDescr( "package3" );
        createReturnValueRule( packageDescr3,
                               " x - y " );
        builder3.addPackage( packageDescr3 );
        final Pattern pattern3 = (Pattern) ((RuleImpl)builder3.getPackage("package3").getRules().iterator().next()).getLhs().getChildren().get( 0 );
        final Constraint returnValue3 = pattern3.getConstraints().get( 0 );

        assertThat(returnValue2).isEqualTo(returnValue1);
        assertThat(returnValue1.equals(returnValue3)).isFalse();
        assertThat(returnValue2.equals(returnValue3)).isFalse();
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

        assertThat(builder.getErrors().getErrors()).hasSize(0);
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
        final Pattern pattern1 = (Pattern) ((RuleImpl)builder1.getPackage("package1").getRules().iterator().next()).getLhs().getChildren().get( 0 );
        final Constraint predicate1 = pattern1.getConstraints().get( 0 );

        final KnowledgeBuilderImpl builder2 = new KnowledgeBuilderImpl();
        final PackageDescr packageDescr2 = new PackageDescr( "package2" );
        createPredicateRule( packageDescr2,
                             "eval(x==y)" );
        builder2.addPackage( packageDescr2 );
        if ( builder2.hasErrors() ) {
            fail( builder2.getErrors().toString() );
         }
        
        final Pattern pattern2 = (Pattern) ((RuleImpl)builder2.getPackage("package2").getRules().iterator().next()).getLhs().getChildren().get( 0 );
        final Constraint predicate2 = pattern2.getConstraints().get( 0 );

        final KnowledgeBuilderImpl builder3 = new KnowledgeBuilderImpl();
        if ( builder3.hasErrors() ) {
            fail( builder3.getErrors().toString() );
         }        
        final PackageDescr packageDescr3 = new PackageDescr( "package3" );
        createPredicateRule( packageDescr3,
                             "eval(x!=y)" );
        builder3.addPackage( packageDescr3 );
        final Pattern pattern3 = (Pattern) ((RuleImpl)builder3.getPackage("package3").getRules().iterator().next()).getLhs().getChildren().get( 0 );
        final Constraint predicate3 = pattern3.getConstraints().get( 0 );

        assertThat(predicate2).isEqualTo(predicate1);
        assertThat(predicate1.equals(predicate3)).isFalse();
        assertThat(predicate2.equals(predicate3)).isFalse();
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

        assertThat(builder.getErrors().getErrors()).hasSize(0);

        InternalKnowledgePackage pkg = builder.getPackage(packageDescr.getName());
        final RuleImpl rule = pkg.getRule( "rule-1" );
        final EvalCondition eval = (EvalCondition) rule.getLhs().getChildren().get( 1 );
        final CompiledInvoker invoker = (CompiledInvoker) eval.getEvalExpression();
        String s = invoker.getMethodBytecode();
    }

    @Test
    public void testEvalMethodCompare() {
        final KnowledgeBuilderImpl builder1 = new KnowledgeBuilderImpl();
        final PackageDescr packageDescr1 = new PackageDescr( "package1" );
        createEvalRule( packageDescr1,
                        "1==1" );
        builder1.addPackage( packageDescr1 );
        final EvalCondition eval1 = (EvalCondition) ((RuleImpl)builder1.getPackage("package1").getRules().iterator().next()).getLhs().getChildren().get( 0 );

        final KnowledgeBuilderImpl builder2 = new KnowledgeBuilderImpl();
        final PackageDescr packageDescr2 = new PackageDescr( "package2" );
        createEvalRule( packageDescr2,
                        "1==1" );
        builder2.addPackage( packageDescr2 );
        final EvalCondition eval2 = (EvalCondition) ((RuleImpl)builder2.getPackage("package2").getRules().iterator().next()).getLhs().getChildren().get( 0 );

        final KnowledgeBuilderImpl builder3 = new KnowledgeBuilderImpl();
        final PackageDescr packageDescr3 = new PackageDescr( "package3" );
        createEvalRule( packageDescr3,
                        "1==3" );
        builder3.addPackage( packageDescr3 );
        final EvalCondition eval3 = (EvalCondition) ((RuleImpl)builder3.getPackage("package3").getRules().iterator().next()).getLhs().getChildren().get( 0 );

        assertThat(eval2).isEqualTo(eval1);
        assertThat(eval1.equals(eval3)).isFalse();
        assertThat(eval2.equals(eval3)).isFalse();
    }

    @Test
    public void testOr() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        final RuleImpl rule = createRule( new OrDescr(),
                                      builder,
                                      "update(stilton);" );
        assertThat(builder.getErrors().getErrors()).hasSize(0);

        final GroupElement lhs = rule.getLhs();
        assertThat(lhs.getChildren()).hasSize(1);

        final GroupElement or = (GroupElement) lhs.getChildren().get( 0 );
        assertThat(or.getChildren()).hasSize(1);
        final Pattern pattern = (Pattern) or.getChildren().get( 0 );
    }

    @Test
    public void testAnd() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        final RuleImpl rule = createRule( new AndDescr(),
                                      builder,
                                      "update(stilton);" );
        assertThat(builder.getErrors().getErrors()).hasSize(0);

        final GroupElement lhs = rule.getLhs();
        assertThat(lhs.getChildren()).hasSize(1);

        final GroupElement and = (GroupElement) lhs.getChildren().get( 0 );
        assertThat(and.getChildren()).hasSize(1);
        final Pattern pattern = (Pattern) and.getChildren().get( 0 );
    }

    @Test
    public void testNot() throws Exception {
        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        // Make sure we can't accessa  variable bound inside the not node
        createRule( new NotDescr(),
                    builder,
                    "update(stilton);" );

        assertThat(builder.hasErrors()).isTrue();

        builder = new KnowledgeBuilderImpl();
        RuleImpl rule = createRule( new NotDescr(),
                           builder,
                           "" );
        assertThat(builder.getErrors().getErrors().length).isEqualTo(0);

        final GroupElement lhs = rule.getLhs();
        assertThat(lhs.getChildren()).hasSize(1);

        final GroupElement not = (GroupElement) lhs.getChildren().get( 0 );
        assertThat(not.getChildren()).hasSize(1);
        final Pattern pattern = (Pattern) not.getChildren().get( 0 );
    }

    @Test
    public void testExists() throws Exception {
        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        // Make sure we can't accessa  variable bound inside the not node
        createRule( new ExistsDescr(),
                    builder,
                    "update(stilton);" );

        assertThat(builder.hasErrors()).isTrue();

        builder = new KnowledgeBuilderImpl();
        RuleImpl rule = createRule( new ExistsDescr(),
                           builder,
                           "" );
        assertThat(builder.getErrors().getErrors().length).isEqualTo(0);

        final GroupElement lhs = rule.getLhs();
        assertThat(lhs.getChildren()).hasSize(1);

        final GroupElement exists = (GroupElement) lhs.getChildren().get( 0 );
        assertThat(exists.getChildren()).hasSize(1);
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

        InternalKnowledgePackage pkg = builder.getPackage(packageDescr.getName());
        final RuleImpl rule = pkg.getRule( "rule-1" );

        assertThat(builder.getErrors().getErrors()).hasSize(0);
    }
    
    @Test
    public void testWarnings() {
        System.setProperty( "drools.kbuilder.severity." + DuplicateRule.KEY, "WARNING");
        
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        
        final PackageDescr packageDescr1 = createBasicPackageWithOneRule(11, 1);
        
        final PackageDescr packageDescr2 = createBasicPackageWithOneRule(22, 2);
        
        builder.addPackage(packageDescr1);
        builder.addPackage(packageDescr2);

        assertThat(builder.hasErrors()).isFalse();
        assertThat(builder.hasWarnings()).isTrue();
       
    }
    
    @Test
    public void testWarningsReportAsErrors() {
        System.setProperty( "drools.kbuilder.severity." + DuplicateRule.KEY, "ERROR");
        KnowledgeBuilderConfigurationImpl cfg = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl(cfg);
        
        final PackageDescr packageDescr1 = createBasicPackageWithOneRule(11, 1);
        
        final PackageDescr packageDescr2 = createBasicPackageWithOneRule(22, 2);
        
        builder.addPackage(packageDescr1);
        builder.addPackage(packageDescr2);

        assertThat(builder.hasErrors()).isTrue();
        assertThat(builder.hasWarnings()).isFalse();
       
    }
    
    @Test
    public void testResetWarnings() {
        
        System.setProperty( "drools.kbuilder.severity." + DuplicateRule.KEY, "WARNING");
        
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        
        final PackageDescr packageDescr1 = createBasicPackageWithOneRule(11, 1);
        
        final PackageDescr packageDescr2 = createBasicPackageWithOneRule(22, 2);
        
        builder.addPackage(packageDescr1);
        builder.addPackage(packageDescr2);

        assertThat(builder.hasWarnings()).isTrue();
        
        builder.resetWarnings();
        assertThat(builder.hasWarnings()).isFalse();
        
        builder.addPackage(packageDescr1);

        assertThat(builder.hasWarnings()).isTrue();
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
        assertThat(builder.hasWarnings()).isTrue();
        assertThat(builder.hasErrors()).isTrue();
        
        builder.resetProblems();
        assertThat(builder.hasWarnings()).isFalse();
        assertThat(builder.hasErrors()).isFalse();
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
        assertThat(builder.hasWarnings()).isTrue();
        assertThat(builder.hasErrors()).isTrue();
        
        builder.resetWarnings();
        assertThat(builder.hasWarnings()).isFalse();
        assertThat(builder.hasErrors()).isTrue();
    }
    
    @Test
    public void testWarnOnFunctionReplacement() throws DroolsParserException, IOException {
        System.setProperty( "drools.kbuilder.severity." + DuplicateFunction.KEY, "WARNING");
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        builder.addPackageFromDrl( new StringReader( "package org.drools\n" + "function boolean testIt() {\n" + "  return true;\n" + "}\n" ) );
        builder.addPackageFromDrl( new StringReader( "package org.drools\n" + "function boolean testIt() {\n" + "  return false;\n" + "}\n" ) );
        assertThat(builder.hasWarnings()).isTrue();
        
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

        assertThat(builder.getErrors().getErrors()).hasSize(2);
        final ParserError err = (ParserError) builder.getErrors().getErrors()[0];
        assertThat(err.getRow()).isEqualTo(42);
        assertThat(err.getCol()).isEqualTo(43);

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

        assertThat(builder.getErrors().getErrors()).hasSize(2);
    }

    @Test
    public void testCompilerConfiguration() throws Exception {
        // test default is eclipse jdt core
        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        PackageDescr pkgDescr = new PackageDescr( "org.drools.mvel.compiler.test" );
        builder.addPackage( pkgDescr );

        final Field dialectField = builder.getClass().getDeclaredField( "defaultDialect" );
        dialectField.setAccessible( true );
        String dialectName = (String) dialectField.get( builder );

        DialectCompiletimeRegistry reg = builder.getPackageRegistry( pkgDescr.getName() ).getDialectCompiletimeRegistry();
        Dialect dialect = reg.getDialect( dialectName );

        final Field compilerField = dialect.getClass().getDeclaredField( "compiler" );
        compilerField.setAccessible( true );
        JavaCompiler compiler = ( JavaCompiler ) compilerField.get( dialect );

        KnowledgeBuilderConfigurationImpl conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
        JavaForMvelDialectConfiguration javaConf = ( JavaForMvelDialectConfiguration ) conf.getDialectConfiguration( "java" );
        switch( javaConf.getCompiler() ) {
            case NATIVE : assertThat(compiler.getClass()).isSameAs(NativeJavaCompiler.class);
                break;
            case ECLIPSE: assertThat(compiler.getClass()).isSameAs(EclipseJavaCompiler.class);
                break;
            default:
                fail( "Unrecognized java compiler");
        }

        // test eclipse jdt core with property settings and default source level
        conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
        javaConf = ( JavaForMvelDialectConfiguration ) conf.getDialectConfiguration( "java" );
        javaConf.setCompiler( JavaForMvelDialectConfiguration.CompilerType.ECLIPSE );
        builder = new KnowledgeBuilderImpl( conf );
        builder.addPackage( pkgDescr );

        dialectName = (String) dialectField.get( builder );
        reg = builder.getPackageRegistry( pkgDescr.getName() ).getDialectCompiletimeRegistry();
        dialect = reg.getDialect( dialectName );
        compiler = (JavaCompiler) compilerField.get( dialect );
        assertThat(compiler.getClass()).isSameAs(EclipseJavaCompiler.class);
    }

    @Test
    public void testTypeDeclaration() throws Exception {
        PackageDescr pkgDescr = new PackageDescr( "org.drools.mvel.compiler" );
        TypeDeclarationDescr typeDescr = new TypeDeclarationDescr( "StockTick" );
        typeDescr.addAnnotation( Role.class.getCanonicalName(), "Event" );
        typeDescr.addAnnotation( TypeSafe.class.getCanonicalName(), "true" );
        pkgDescr.addTypeDeclaration( typeDescr );

        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        builder.addPackage( pkgDescr );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        InternalKnowledgePackage pkg = builder.getPackage(pkgDescr.getName());
        assertThat(pkg.getTypeDeclarations().size()).isEqualTo(1);

        TypeDeclaration type = pkg.getTypeDeclaration( "StockTick" );
        assertThat(type.isTypesafe()).isTrue();
        assertThat(type.getRole()).isEqualTo(Role.Type.EVENT);
        assertThat(type.getTypeClass()).isEqualTo(StockTick.class);
    }

    @Test
    public void testTypeDeclarationNewBean() throws Exception {
        PackageDescr pkgDescr = new PackageDescr( "org.drools.mvel.compiler.test" );
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

        InternalKnowledgePackage pkg = builder.getPackage(pkgDescr.getName());
        assertThat(pkg.getTypeDeclarations().size()).isEqualTo(1);

        TypeDeclaration type = pkg.getTypeDeclaration( "NewBean" );
        assertThat(type.getTypeName()).isEqualTo("NewBean");
        assertThat(type.getRole()).isEqualTo(Role.Type.FACT);
        assertThat(type.getTypeClass().getName()).isEqualTo("org.drools.mvel.compiler.test.NewBean");
        assertThat(builder.hasErrors()).isFalse();

        InternalKnowledgePackage bp = builder.getPackage(pkgDescr.getName());

        Class newBean = bp.getPackageClassLoader().loadClass( "org.drools.mvel.compiler.test.NewBean" );
        assertThat(newBean).isNotNull();
    }

    @Test
    public void testTypeDeclarationWithFieldMetadata() throws Exception {
        PackageDescr pkgDescr = new PackageDescr( "org.drools.mvel.compiler.test" );
        TypeDeclarationDescr typeDescr = new TypeDeclarationDescr( "TypeWithFieldMeta" );

        TypeFieldDescr f1 = new TypeFieldDescr( "field",
                                                new PatternDescr( "String" ) );
        f1.addAnnotation("custom", null);
        typeDescr.addField( f1 );

        pkgDescr.addTypeDeclaration( typeDescr );

        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        builder.addPackage(pkgDescr);
        assertThat(builder.hasErrors()).isFalse();

        InternalKnowledgePackage bp = builder.getPackage(pkgDescr.getName());

        final FactType factType = bp.getFactType("org.drools.mvel.compiler.test.TypeWithFieldMeta");
        assertThat(factType).isNotNull();
        final FactField field = factType.getField( "field" );
        assertThat(field).isNotNull();

        final Map<String, Object> fieldMetaData = field.getMetaData();
        assertThat(fieldMetaData).as("No field-level custom metadata got compiled").isNotNull();
        assertThat(fieldMetaData.containsKey("custom")).as("Field metadata does not include expected value").isTrue();
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

        assertThat(builder.getErrors().getErrors()).hasSize(0);
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

        InternalKnowledgePackage pkg = builder.getPackage(packageDescr.getName());
        final RuleImpl rule = pkg.getRule( "rule-1" );

        assertThat(rule.getName()).isEqualTo("rule-1");

        return rule;
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

        assertThat(builder.getErrors().getErrors()).hasSize(0);

        InternalKnowledgePackage pkg = builder.getPackageRegistry().get( "p1" ).getPackage();
        final RuleImpl rule = pkg.getRule( "rule-1" );
        assertThat(rule).isNotNull();

        final Pattern pattern = (Pattern) rule.getLhs().getChildren().get( 0 );
        assertThat(((ClassObjectType) pattern.getObjectType()).getClassType().getName()).isEqualTo(StockTick.class.getName());
        final Behavior window = pattern.getBehaviors().get(0);
        assertThat(window.getType()).isEqualTo(Behavior.BehaviorType.TIME_WINDOW);
        assertThat(((SlidingTimeWindow) window).getSize()).isEqualTo(60000);
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

        List<FactField> fieldsBean1 = builder.getPackage("foo").getFactType( "foo.Bean1" ).getFields();
        assertThat(fieldsBean1.size()).isEqualTo(2);
        assertThat(fieldsBean1.get(0).getName()).isEqualTo("age");
        assertThat(fieldsBean1.get(0).getType()).isEqualTo(int.class);
        assertThat(fieldsBean1.get(1).getName()).isEqualTo("name");
        assertThat(fieldsBean1.get(1).getType()).isEqualTo(String.class);

        List<FactField> fieldsBean2 = builder.getPackage("foo").getFactType( "foo.Bean2" ).getFields();
        assertThat(fieldsBean2.size()).isEqualTo(3);
        assertThat(fieldsBean2.get(0).getName()).isEqualTo("age");
        assertThat(fieldsBean2.get(0).getType()).isEqualTo(int.class);
        assertThat(fieldsBean2.get(1).getName()).isEqualTo("name");
        assertThat(fieldsBean2.get(1).getType()).isEqualTo(String.class);
        assertThat(fieldsBean2.get(2).getName()).isEqualTo("cheese");
        assertThat(fieldsBean2.get(2).getType()).isEqualTo(String.class);
    }

    class MockInternalMatch implements InternalMatch {

        private RuleImpl               rule;
        private int                salience;
        private final GroupElement subrule;
        private LeftTuple tuple;

        public MockInternalMatch(final RuleImpl rule,
                                 int salience,
                                 final GroupElement subrule,
                                 final LeftTuple tuple) {
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

        public LeftTuple getTuple() {
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

        public InternalFactHandle getActivationFactHandle() {
            return null;
        }

        public boolean isAdded() {
            return false;
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
        public void dequeue() {
        }

        @Override
        public int getQueueIndex() {
            return 0;
        }

        @Override
        public void setQueueIndex(int index) {

        }

        @Override
        public RuleAgendaItem getRuleAgendaItem() {
            return null;
        }


        @Override
        public void setActivationFactHandle(InternalFactHandle factHandle) {

        }

        @Override
        public TerminalNode getTerminalNode() {
            return null;
        }

        @Override
        public String toExternalForm() {
            return null;
        }

        @Override
        public Runnable getCallback() {
            return null;
        }

        @Override
        public void setCallback(Runnable callback) {

        }
    }

    class MockTuple
        extends
        LeftTuple {
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

        public void setActivation( final InternalMatch internalMatch) {
        }

        public long getRecency() {
            return 0;
        }

        public int size() {
            return 0;
        }

    }
}
