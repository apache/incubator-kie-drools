package org.drools.compiler;

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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.Cheese;
import org.drools.DroolsTestCase;
import org.drools.FactHandle;
import org.drools.Primitives;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.StockTick;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.common.ActivationGroupNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.LogicalDependency;
import org.drools.common.ActivationNode;
import org.drools.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.commons.jci.compilers.JaninoJavaCompiler;
import org.drools.commons.jci.compilers.JavaCompiler;
import org.drools.facttemplates.Fact;
import org.drools.integrationtests.SerializationHelper;
import org.drools.io.Resource;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FactTemplateDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.FieldTemplateDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.SlidingWindowDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
import org.drools.process.core.Context;
import org.drools.process.core.Process;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.rule.Behavior;
import org.drools.rule.CompositeClassLoader;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.GroupElement;
import org.drools.rule.JavaDialectRuntimeData;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.Rule;
import org.drools.rule.SlidingTimeWindow;
import org.drools.rule.TypeDeclaration;
import org.drools.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.CompiledInvoker;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;
import org.drools.util.DroolsStreamUtils;
import org.drools.util.LinkedList;
import org.drools.workflow.core.impl.WorkflowProcessImpl;

public class PackageBuilderTest extends DroolsTestCase {

    public void testErrors() throws Exception {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "price",
                                                                     "x" );
        pattern.addConstraint( fieldBindingDescr );
        fieldBindingDescr = new FieldBindingDescr( "price",
                                                   "y" );
        pattern.addConstraint( fieldBindingDescr );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        final FieldConstraintDescr returnValue = new FieldConstraintDescr( "price" );
        returnValue.addRestriction( new ReturnValueRestrictionDescr( "==",
                                                                     "x" ) );

        pattern.addConstraint( returnValue );

        // There is no m this should produce errors.
        ruleDescr.setConsequence( "update(m);" );

        builder.addPackage( packageDescr );

        assertLength( 1,
                      builder.getErrors().getErrors() );
    }

    public void testErrorsInParser() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( this.getClass().getResourceAsStream( "bad_rule.drl" ) ) );
        assertTrue( builder.hasErrors() );
    }

    public void testReload() throws Exception {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        ruleDescr.setConsequence( "map.put(\"value\", new Integer(1) );" );

        builder.addPackage( packageDescr );

        Package pkg = builder.getPackage();
        Rule rule = pkg.getRule( "rule-1" );

        assertLength( 0,
                      builder.getErrors().getErrors() );

        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        ruleBase.getGlobals().put( "map",
                                   Map.class );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final HashMap map = new HashMap();
        workingMemory.setGlobal( "map",
                                 map );

        final Tuple tuple = new MockTuple( new HashMap() );
        final Activation activation = new MockActivation( rule,
                                                          0,
                                                          rule.getLhs(),
                                                          tuple );

        DefaultKnowledgeHelper knowledgeHelper = new org.drools.base.DefaultKnowledgeHelper( workingMemory );
        knowledgeHelper.setActivation( activation );

        rule.getConsequence().evaluate( knowledgeHelper,
                                        workingMemory );
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

        knowledgeHelper = new org.drools.base.DefaultKnowledgeHelper( workingMemory );
        knowledgeHelper.setActivation( activation );

        rule.getConsequence().evaluate( knowledgeHelper,
                                        workingMemory );
        assertEquals( new Integer( 2 ),
                      map.get( "value" ) );

    }

    public void testSerializable() throws Exception {
        final PackageBuilder builder = new PackageBuilder();

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
        final Package pkg = builder.getPackage();
        final Rule rule = pkg.getRule( "rule-1" );

        assertLength( 0,
                      builder.getErrors().getErrors() );

        final Package newPkg = SerializationHelper.serializeObject( pkg );
        final Rule newRule = newPkg.getRule( "rule-1" );

        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();

        // It's been serialised so we have to simulate the re-wiring process
        newPkg.getDialectRuntimeRegistry().onAdd( ruleBase.getRootClassLoader() );
        newPkg.getDialectRuntimeRegistry().onBeforeExecute();

        ruleBase.getGlobals().put( "map",
                                   Map.class );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final HashMap map = new HashMap();

        workingMemory.setGlobal( "map",
                                 map );

        final Tuple tuple = new MockTuple( new HashMap() );
        final Activation activation = new MockActivation( newRule,
                                                          0,
                                                          newRule.getLhs(),
                                                          tuple );

        final DefaultKnowledgeHelper knowledgeHelper = new org.drools.base.DefaultKnowledgeHelper( workingMemory );
        knowledgeHelper.setActivation( activation );

        newRule.getConsequence().evaluate( knowledgeHelper,
                                           workingMemory );
        assertEquals( new Integer( 1 ),
                      map.get( "value" ) );
    }

    public void FIXMEtestNoPackageName() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
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

    public void testErrorReset() throws Exception {
        final PackageBuilder builder = new PackageBuilder();

        builder.addPackageFromDrl( new StringReader( "package foo \n rule ORB" ) );
        assertTrue( builder.hasErrors() );

        builder.resetErrors();
        assertFalse( builder.hasErrors() );

        builder.addPackageFromDrl( new StringReader( "package foo \n rule ORB" ) );
        assertTrue( builder.hasErrors() );
    }

    public void testFactTemplate() {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final FactTemplateDescr cheese = new FactTemplateDescr( "Cheese" );
        cheese.addFieldTemplate( new FieldTemplateDescr( "name",
                                                         "String" ) );
        cheese.addFieldTemplate( new FieldTemplateDescr( "price",
                                                         "Integer" ) );

        packageDescr.addFactTemplate( cheese );

        final PatternDescr pattern = new PatternDescr( "Cheese",
                                                       "stilton" );
        lhs.addDescr( pattern );

        final FieldConstraintDescr literalDescr = new FieldConstraintDescr( "name" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "stilton" ) );

        pattern.addConstraint( literalDescr );

        ruleDescr.setConsequence( "String result = stilton.getFieldValue( \"name\" ) + \" \" + stilton.getFieldValue( \"price\" );" );

        builder.addPackage( packageDescr );

        //        assertFalse( Arrays.toString( builder.getErrors() ),
        //                     builder.hasErrors() );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final Package pkg = builder.getPackage();
        try {
            ruleBase.addPackage( pkg );
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        final Fact stilton = pkg.getFactTemplate( "Cheese" ).createFact( 1 );
        stilton.setFieldValue( "name",
                               "stilton" );
        stilton.setFieldValue( "price",
                               new Integer( 200 ) );
        workingMemory.insert( stilton );
        workingMemory.fireAllRules();

    }

    public void testLiteral() throws Exception {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        final FieldConstraintDescr literalDescr = new FieldConstraintDescr( "type" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "stilton" ) );

        pattern.addConstraint( literalDescr );

        ruleDescr.setConsequence( "update(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors().getErrors() );
    }

    public void testReturnValue() throws Exception {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "price",
                                                                     "x" );
        pattern.addConstraint( fieldBindingDescr );
        fieldBindingDescr = new FieldBindingDescr( "price",
                                                   "y" );
        pattern.addConstraint( fieldBindingDescr );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        final FieldConstraintDescr returnValue = new FieldConstraintDescr( "price" );
        returnValue.addRestriction( new ReturnValueRestrictionDescr( "==",
                                                                     "(( (Integer) map.get( new Integer( x )) ).intValue() * y)" ) );

        pattern.addConstraint( returnValue );

        ruleDescr.setConsequence( "update(stilton);" );

        builder.addPackage( packageDescr );

        assertEquals( "Should not have any errors",
                      0,
                      builder.getErrors().getErrors().length );
    }

    public void testReturnValueMethodCompare() {
        final PackageBuilder builder1 = new PackageBuilder();
        final PackageDescr packageDescr1 = new PackageDescr( "package1" );
        createReturnValueRule( packageDescr1,
                               " x + y " );
        builder1.addPackage( packageDescr1 );
        final Pattern pattern1 = (Pattern) builder1.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        final ReturnValueConstraint returnValue1 = (ReturnValueConstraint) pattern1.getConstraints().get( 2 );

        final PackageBuilder builder2 = new PackageBuilder();
        final PackageDescr packageDescr2 = new PackageDescr( "package2" );
        createReturnValueRule( packageDescr2,
                               " x + y " );
        builder2.addPackage( packageDescr2 );
        final Pattern pattern2 = (Pattern) builder2.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        final ReturnValueConstraint returnValue2 = (ReturnValueConstraint) pattern2.getConstraints().get( 2 );

        final PackageBuilder builder3 = new PackageBuilder();
        final PackageDescr packageDescr3 = new PackageDescr( "package3" );
        createReturnValueRule( packageDescr3,
                               " x - y " );
        builder3.addPackage( packageDescr3 );
        final Pattern pattern3 = (Pattern) builder3.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        final ReturnValueConstraint returnValue3 = (ReturnValueConstraint) pattern3.getConstraints().get( 2 );

        assertEquals( returnValue1,
                      returnValue2 );
        assertFalse( returnValue1.equals( returnValue3 ) );
        assertFalse( returnValue2.equals( returnValue3 ) );
    }

    public void testPredicate() throws Exception {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        final FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "price",
                                                                           "x" );
        pattern.addConstraint( fieldBindingDescr );

        final FieldBindingDescr fieldBindingDescr2 = new FieldBindingDescr( "price",
                                                                            "y" );
        pattern.addConstraint( fieldBindingDescr2 );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        final PredicateDescr predicate = new PredicateDescr( "( ( Integer )map.get( new Integer(x) ) ).intValue() == y" );
        pattern.addConstraint( predicate );

        ruleDescr.setConsequence( "update(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors().getErrors() );
    }

    public void testPredicateMethodCompare() {
        final PackageBuilder builder1 = new PackageBuilder();
        final PackageDescr packageDescr1 = new PackageDescr( "package1" );
        createPredicateRule( packageDescr1,
                             "x==y" );
        builder1.addPackage( packageDescr1 );
        final Pattern pattern1 = (Pattern) builder1.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        final PredicateConstraint predicate1 = (PredicateConstraint) pattern1.getConstraints().get( 2 );

        final PackageBuilder builder2 = new PackageBuilder();
        final PackageDescr packageDescr2 = new PackageDescr( "package2" );
        createPredicateRule( packageDescr2,
                             "x==y" );
        builder2.addPackage( packageDescr2 );
        final Pattern pattern2 = (Pattern) builder2.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        final PredicateConstraint predicate2 = (PredicateConstraint) pattern2.getConstraints().get( 2 );

        final PackageBuilder builder3 = new PackageBuilder();
        final PackageDescr packageDescr3 = new PackageDescr( "package3" );
        createPredicateRule( packageDescr3,
                             "x!=y" );
        builder3.addPackage( packageDescr3 );
        final Pattern pattern3 = (Pattern) builder3.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        final PredicateConstraint predicate3 = (PredicateConstraint) pattern3.getConstraints().get( 2 );

        assertEquals( predicate1,
                      predicate2 );
        assertFalse( predicate1.equals( predicate3 ) );
        assertFalse( predicate2.equals( predicate3 ) );
    }

    public void testEval() throws Exception {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "price",
                                                                     "x" );
        pattern.addConstraint( fieldBindingDescr );
        fieldBindingDescr = new FieldBindingDescr( "price",
                                                   "y" );
        pattern.addConstraint( fieldBindingDescr );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        final EvalDescr evalDescr = new EvalDescr( "( ( Integer )map.get( new Integer(x) ) ).intValue() == y" );
        lhs.addDescr( evalDescr );

        ruleDescr.setConsequence( "update(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors().getErrors() );

        final Package pkg = builder.getPackage();
        final Rule rule = pkg.getRule( "rule-1" );
        final EvalCondition eval = (EvalCondition) rule.getLhs().getChildren().get( 1 );
        final CompiledInvoker invoker = (CompiledInvoker) eval.getEvalExpression();
        final List list = invoker.getMethodBytecode();
    }

    public void testEvalMethodCompare() {
        final PackageBuilder builder1 = new PackageBuilder();
        final PackageDescr packageDescr1 = new PackageDescr( "package1" );
        createEvalRule( packageDescr1,
                        "1==1" );
        builder1.addPackage( packageDescr1 );
        final EvalCondition eval1 = (EvalCondition) builder1.getPackage().getRules()[0].getLhs().getChildren().get( 0 );

        final PackageBuilder builder2 = new PackageBuilder();
        final PackageDescr packageDescr2 = new PackageDescr( "package2" );
        createEvalRule( packageDescr2,
                        "1==1" );
        builder2.addPackage( packageDescr2 );
        final EvalCondition eval2 = (EvalCondition) builder2.getPackage().getRules()[0].getLhs().getChildren().get( 0 );

        final PackageBuilder builder3 = new PackageBuilder();
        final PackageDescr packageDescr3 = new PackageDescr( "package3" );
        createEvalRule( packageDescr3,
                        "1==3" );
        builder3.addPackage( packageDescr3 );
        final EvalCondition eval3 = (EvalCondition) builder3.getPackage().getRules()[0].getLhs().getChildren().get( 0 );

        assertEquals( eval1,
                      eval2 );
        assertFalse( eval1.equals( eval3 ) );
        assertFalse( eval2.equals( eval3 ) );
    }

    public void testOr() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        final Rule rule = createRule( new OrDescr(),
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

        final LiteralConstraint literalConstarint = (LiteralConstraint) pattern.getConstraints().get( 0 );
    }

    public void testAnd() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        final Rule rule = createRule( new AndDescr(),
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

        final LiteralConstraint literalConstraint = (LiteralConstraint) pattern.getConstraints().get( 0 );
    }

    public void testNot() throws Exception {
        PackageBuilder builder = new PackageBuilder();

        // Make sure we can't accessa  variable bound inside the not node
        Rule rule = createRule( new NotDescr(),
                                builder,
                                "update(stilton);" );
        assertEquals( 1,
                      builder.getErrors().getErrors().length );

        builder = new PackageBuilder();
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

        final LiteralConstraint literalConstarint = (LiteralConstraint) pattern.getConstraints().get( 0 );
    }

    public void testExists() throws Exception {
        PackageBuilder builder = new PackageBuilder();

        // Make sure we can't accessa  variable bound inside the not node
        Rule rule = createRule( new ExistsDescr(),
                                builder,
                                "update(stilton);" );
        assertEquals( 1,
                      builder.getErrors().getErrors().length );

        builder = new PackageBuilder();
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

        final LiteralConstraint literalConstarint = (LiteralConstraint) pattern.getConstraints().get( 0 );
    }

    public void testNumbers() throws Exception {
        // test boolean
        FieldConstraintDescr literalDescr = new FieldConstraintDescr( "booleanPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "true" ) );
        createLiteralRule( literalDescr );

        // test boolean
        literalDescr = new FieldConstraintDescr( "booleanPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "false" ) );
        createLiteralRule( literalDescr );

        // test char
        literalDescr = new FieldConstraintDescr( "charPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "a" ) );
        createLiteralRule( literalDescr );

        // test byte
        literalDescr = new FieldConstraintDescr( "bytePrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "1" ) );
        createLiteralRule( literalDescr );

        literalDescr = new FieldConstraintDescr( "bytePrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "0" ) );
        createLiteralRule( literalDescr );

        literalDescr = new FieldConstraintDescr( "bytePrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "-1" ) );
        createLiteralRule( literalDescr );

        // test short
        literalDescr = new FieldConstraintDescr( "shortPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "1" ) );
        createLiteralRule( literalDescr );

        literalDescr = new FieldConstraintDescr( "shortPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "0" ) );
        createLiteralRule( literalDescr );

        literalDescr = new FieldConstraintDescr( "shortPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "-1" ) );
        createLiteralRule( literalDescr );

        // test int
        literalDescr = new FieldConstraintDescr( "intPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "1" ) );
        createLiteralRule( literalDescr );

        literalDescr = new FieldConstraintDescr( "intPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "0" ) );
        createLiteralRule( literalDescr );

        literalDescr = new FieldConstraintDescr( "intPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "-1" ) );
        createLiteralRule( literalDescr );

        // test long
        literalDescr = new FieldConstraintDescr( "longPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "1" ) );
        createLiteralRule( literalDescr );

        literalDescr = new FieldConstraintDescr( "longPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "0" ) );
        createLiteralRule( literalDescr );

        literalDescr = new FieldConstraintDescr( "longPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "0" ) );
        createLiteralRule( literalDescr );

        // test float
        literalDescr = new FieldConstraintDescr( "floatPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "1.1" ) );
        createLiteralRule( literalDescr );

        literalDescr = new FieldConstraintDescr( "floatPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "0" ) );
        createLiteralRule( literalDescr );

        literalDescr = new FieldConstraintDescr( "floatPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "-1.1" ) );
        createLiteralRule( literalDescr );

        // test double
        literalDescr = new FieldConstraintDescr( "doublePrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "1.1" ) );
        createLiteralRule( literalDescr );

        literalDescr = new FieldConstraintDescr( "doublePrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "0" ) );
        createLiteralRule( literalDescr );

        literalDescr = new FieldConstraintDescr( "floatPrimitive" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "-1.1" ) );
        createLiteralRule( literalDescr );
    }

    public void testNull() {
        final PackageBuilder builder = new PackageBuilder();

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

        final Package pkg = (Package) builder.getPackage();
        final Rule rule = pkg.getRule( "rule-1" );

        assertLength( 0,
                      builder.getErrors().getErrors() );
    }

    public void testQuery() throws Exception {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final QueryDescr queryDescr = new QueryDescr( "query1" );
        queryDescr.setParameters( new String[]{"$type"} );
        queryDescr.setParameterTypes( new String[]{"String"} );

        packageDescr.addRule( queryDescr );

        final AndDescr lhs = new AndDescr();
        queryDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        final FieldConstraintDescr literalDescr = new FieldConstraintDescr( "type" );
        literalDescr.addRestriction( new VariableRestrictionDescr( "==",
                                                                   "$type" ) );

        pattern.addConstraint( literalDescr );

        queryDescr.setConsequence( "update(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors().getErrors() );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        StatefulSession session = ruleBase.newStatefulSession();

        session.insert( new Cheese( "stilton",
                                    15 ) );

        QueryResults results = session.getQueryResults( "query1",
                                                        new Object[]{"stilton"} );
        assertEquals( 1,
                      results.size() );
        Object object = results.get( 0 ).get( 0 );
        assertEquals( new Cheese( "stilton",
                                  15 ),
                      object );

        results = session.getQueryResults( "query1",
                                           new Object[]{"cheddar"} );
    }

    public void testDuplicateRuleNames() throws Exception {

        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );

        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
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

        assertLength( 1,
                      builder.getErrors().getErrors() );
        final ParserError err = (ParserError) builder.getErrors().getErrors()[0];
        assertEquals( 42,
                      err.getRow() );
        assertEquals( 43,
                      err.getCol() );

    }

    public void testDuplicateDeclaration() {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern1 = new PatternDescr( Cheese.class.getName() );
        lhs.addDescr( pattern1 );

        final FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "type",
                                                                           "$type" );

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

    public void testCompilerConfiguration() throws Exception {
        // test default is eclipse jdt core
        PackageBuilder builder = new PackageBuilder();
        PackageDescr pkgDescr = new PackageDescr( "org.test" );
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
        assertSame( EclipseJavaCompiler.class,
                    compiler.getClass() );

        // test JANINO with property settings
        PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
        JavaDialectConfiguration javaConf = (JavaDialectConfiguration) conf.getDialectConfiguration( "java" );
        javaConf.setCompiler( JavaDialectConfiguration.JANINO );
        builder = new PackageBuilder( conf );
        builder.addPackage( pkgDescr );

        dialectName = (String) dialectField.get( builder );
        reg = builder.getPackageRegistry( pkgDescr.getName() ).getDialectCompiletimeRegistry();
        dialect = reg.getDialect( dialectName );
        compiler = (JavaCompiler) compilerField.get( dialect );
        assertSame( JaninoJavaCompiler.class,
                    compiler.getClass() );

        // test eclipse jdt core with property settings and default source level
        conf = new PackageBuilderConfiguration();
        javaConf = (JavaDialectConfiguration) conf.getDialectConfiguration( "java" );
        javaConf.setCompiler( JavaDialectConfiguration.ECLIPSE );
        builder = new PackageBuilder( conf );
        builder.addPackage( pkgDescr );

        dialectName = (String) dialectField.get( builder );
        reg = builder.getPackageRegistry( pkgDescr.getName() ).getDialectCompiletimeRegistry();
        dialect = reg.getDialect( dialectName );
        compiler = (JavaCompiler) compilerField.get( dialect );
        assertSame( EclipseJavaCompiler.class,
                    compiler.getClass() );
    }

    public void testTypeDeclaration() throws Exception {
        PackageDescr pkgDescr = new PackageDescr( "org.test" );
        TypeDeclarationDescr typeDescr = new TypeDeclarationDescr( "StockTick" );
        typeDescr.addMetaAttribute( TypeDeclaration.Role.ID,
                                    "event" );
        typeDescr.addMetaAttribute( TypeDeclaration.ATTR_CLASS,
                                    "org.drools.StockTick" );
        pkgDescr.addTypeDeclaration( typeDescr );

        PackageBuilder builder = new PackageBuilder();
        builder.addPackage( pkgDescr );

        Package pkg = builder.getPackage();
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

    public void testTypeDeclarationNewBean() throws Exception {
        PackageDescr pkgDescr = new PackageDescr( "org.test" );
        TypeDeclarationDescr typeDescr = new TypeDeclarationDescr( "NewBean" );

        TypeFieldDescr f1 = new TypeFieldDescr( "name",
                                                new PatternDescr( "String" ) );
        TypeFieldDescr f2 = new TypeFieldDescr( "age",
                                                new PatternDescr( "int" ) );

        typeDescr.addField( f1 );
        typeDescr.addField( f2 );

        pkgDescr.addTypeDeclaration( typeDescr );

        PackageBuilder builder = new PackageBuilder();
        builder.addPackage( pkgDescr );

        Package pkg = builder.getPackage();
        assertEquals( 1,
                      pkg.getTypeDeclarations().size() );

        TypeDeclaration type = pkg.getTypeDeclaration( "NewBean" );
        assertEquals( "NewBean",
                      type.getTypeName() );
        assertEquals( TypeDeclaration.Role.FACT,
                      type.getRole() );
        assertEquals( "org.test.NewBean",
                      type.getTypeClass().getName() );
        assertFalse( builder.hasErrors() );

        Package bp = builder.getPackage();
        CompositeClassLoader rootClassloader = new CompositeClassLoader( Thread.currentThread().getContextClassLoader() );
        JavaDialectRuntimeData dialectData = (JavaDialectRuntimeData) bp.getDialectRuntimeRegistry().getDialectData( "java" );
        dialectData.onAdd( bp.getDialectRuntimeRegistry(),
                           rootClassloader );

        Class newBean = rootClassloader.loadClass( "org.test.NewBean" );
        assertNotNull( newBean );
    }

    public void testPackageMerge() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        try {
            builder.addPackage( new PackageDescr( "org.drools" ) );

            builder.addPackageFromDrl( new StringReader( "package org.drools\n" + "function boolean testIt() {\n" + "  return true;\n" + "}\n" ) );
        } catch ( RuntimeException e ) {
            fail( "Should not raise any exception: " + e.getMessage() );
        }
    }

    private void createReturnValueRule(final PackageDescr packageDescr,
                                       final String expression) {
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "price",
                                                                     "x" );
        pattern.addConstraint( fieldBindingDescr );
        fieldBindingDescr = new FieldBindingDescr( "price",
                                                   "y" );
        pattern.addConstraint( fieldBindingDescr );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        final FieldConstraintDescr returnValue = new FieldConstraintDescr( "price" );
        returnValue.addRestriction( new ReturnValueRestrictionDescr( "==",
                                                                     expression ) );

        pattern.addConstraint( returnValue );

        ruleDescr.setConsequence( "update(stilton);" );
    }

    private void createPredicateRule(final PackageDescr packageDescr,
                                     final String expression) {
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        final FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "price",
                                                                           "x" );
        pattern.addConstraint( fieldBindingDescr );

        final FieldBindingDescr fieldBindingDescr2 = new FieldBindingDescr( "price",
                                                                            "y" );
        pattern.addConstraint( fieldBindingDescr2 );

        packageDescr.addGlobal( new GlobalDescr( "map",
                                                 "java.util.Map" ) );

        final PredicateDescr predicate = new PredicateDescr( expression );
        pattern.addConstraint( predicate );

        ruleDescr.setConsequence( "update(stilton);" );
    }

    private void createEvalRule(final PackageDescr packageDescr,
                                final String expression) {
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

    private void createLiteralRule(final FieldConstraintDescr literalDescr) {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Primitives.class.getName() );
        lhs.addDescr( pattern );

        pattern.addConstraint( literalDescr );

        ruleDescr.setConsequence( "" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors().getErrors() );
    }

    private Rule createRule(final ConditionalElementDescr ceDescr,
                            final PackageBuilder builder,
                            final String consequence) throws Exception {
        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        lhs.addDescr( (BaseDescr) ceDescr );

        final PatternDescr patternDescr = new PatternDescr( Cheese.class.getName(),
                                                            "stilton" );

        final FieldConstraintDescr literalDescr = new FieldConstraintDescr( "type" );
        literalDescr.addRestriction( new LiteralRestrictionDescr( "==",
                                                                  "stilton" ) );

        patternDescr.addConstraint( literalDescr );

        ceDescr.addDescr( patternDescr );

        ruleDescr.setConsequence( consequence );

        builder.addPackage( packageDescr );

        final Package pkg = (Package) builder.getPackage();
        final Rule rule = pkg.getRule( "rule-1" );

        assertEquals( "rule-1",
                      rule.getName() );

        return rule;
    }

    public void testRuleFlow() throws Exception {
        PackageBuilder builder = new PackageBuilder();

        InputStream in = this.getClass().getResourceAsStream( "/org/drools/integrationtests/ruleflow.rfm" );
        assertNotNull( in );

        builder.addPackage( new PackageDescr( "com.sample" ) );

        builder.addRuleFlow( new InputStreamReader( in ) );
        Package pkg = builder.getPackage();
        assertNotNull( pkg );

        Map flows = pkg.getRuleFlows();
        assertNotNull( flows );
        assertEquals( 1,
                      flows.size() );

        assertTrue( flows.containsKey( "0" ) );

        Process p = (Process) flows.get( "0" );
        assertTrue( p instanceof WorkflowProcessImpl );

        //now serialization
        Package pkg2 = (Package) DroolsStreamUtils.streamIn( DroolsStreamUtils.streamOut( pkg ) );
        assertNotNull( pkg2 );

        flows = pkg2.getRuleFlows();
        assertNotNull( flows );
        assertEquals( 1,
                      flows.size() );
        assertTrue( flows.containsKey( "0" ) );
        p = (Process) flows.get( "0" );
        assertTrue( p instanceof WorkflowProcessImpl );
    }

    public void testRuleFlowUpgrade() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        // Set the system property so that automatic conversion can happen
        System.setProperty( "drools.ruleflow.port",
                            "true" );

        InputStream in = this.getClass().getResourceAsStream( "/org/drools/integrationtests/ruleflow40.rfm" );
        assertNotNull( in );

        builder.addPackage( new PackageDescr( "com.sample" ) );

        builder.addRuleFlow( new InputStreamReader( in ) );
        Package pkg = builder.getPackage();
        assertNotNull( pkg );

        Map flows = pkg.getRuleFlows();
        assertNotNull( flows );
        assertEquals( 1,
                      flows.size() );

        assertTrue( flows.containsKey( "0" ) );

        Process p = (Process) flows.get( "0" );
        assertTrue( p instanceof WorkflowProcessImpl );

        //now serialization
        Package pkg2 = (Package) DroolsStreamUtils.streamIn( DroolsStreamUtils.streamOut( pkg ) );
        assertNotNull( pkg2 );

        flows = pkg2.getRuleFlows();
        assertNotNull( flows );
        assertEquals( 1,
                      flows.size() );
        assertTrue( flows.containsKey( "0" ) );
        p = (Process) flows.get( "0" );
        assertTrue( p instanceof WorkflowProcessImpl );
        // Reset the system property so that automatic conversion should not happen
        System.setProperty( "drools.ruleflow.port",
                            "false" );
    }

    public void testPackageRuleFlows() throws Exception {
        Package pkg = new Package( "boo" );
        Process rf = new MockRuleFlow( "1" );
        pkg.addProcess( rf );
        assertTrue( pkg.getRuleFlows().containsKey( "1" ) );
        assertSame( rf,
                    pkg.getRuleFlows().get( "1" ) );

        Process rf2 = new MockRuleFlow( "2" );
        pkg.addProcess( rf2 );
        assertTrue( pkg.getRuleFlows().containsKey( "1" ) );
        assertSame( rf,
                    pkg.getRuleFlows().get( "1" ) );
        assertTrue( pkg.getRuleFlows().containsKey( "2" ) );
        assertSame( rf2,
                    pkg.getRuleFlows().get( "2" ) );

        pkg.removeRuleFlow( "1" );
        assertTrue( pkg.getRuleFlows().containsKey( "2" ) );
        assertSame( rf2,
                    pkg.getRuleFlows().get( "2" ) );
        assertFalse( pkg.getRuleFlows().containsKey( "1" ) );

    }

    public void testJaninoWithStaticImports() throws Exception {
        PackageBuilderConfiguration cfg = new PackageBuilderConfiguration();
        JavaDialectConfiguration javaConf = (JavaDialectConfiguration) cfg.getDialectConfiguration( "java" );
        javaConf.setCompiler( JavaDialectConfiguration.JANINO );

        PackageBuilder bldr = new PackageBuilder( cfg );
        bldr.addPackageFromDrl( new StringReader( "package testBuilderPackageConfig \n import java.util.List" ) );
        bldr.addPackageFromDrl( new StringReader( "package testBuilderPackageConfig \n function void doSomething() {\n System.err.println(List.class.toString()); }" ) );

        assertFalse( bldr.hasErrors() );
    }

    public void testSinglePackage() throws Exception {
        PackageBuilderConfiguration cfg = new PackageBuilderConfiguration();
        cfg.setAllowMultipleNamespaces( false );
        PackageBuilder bldr = new PackageBuilder( cfg );
        bldr.addPackageFromDrl( new StringReader( "package whee\n import org.drools.Cheese" ) );
        assertFalse( bldr.hasErrors() );
        bldr.addPackageFromDrl( new StringReader( "package whee\n import org.drools.Person" ) );
        assertFalse( bldr.hasErrors() );
        // following package will not be added because configuration is set for single namespace builders
        bldr.addPackageFromDrl( new StringReader( "package whee2\n import org.drools.Person" ) );
        assertFalse( bldr.hasErrors() );

        assertEquals( 1,
                      bldr.getPackages().length );

        cfg = new PackageBuilderConfiguration();
        assertEquals( true,
                      cfg.isAllowMultipleNamespaces() );
        bldr = new PackageBuilder( cfg );
        bldr.addPackageFromDrl( new StringReader( "package whee\n import org.drools.Cheese" ) );
        assertFalse( bldr.hasErrors() );
        // following import will be added to the default package name
        bldr.addPackageFromDrl( new StringReader( "import org.drools.Person" ) );
        assertFalse( bldr.hasErrors() );
        bldr.addPackageFromDrl( new StringReader( "package whee2\n import org.drools.Person" ) );
        assertFalse( bldr.hasErrors() );

        assertEquals( 3,
                      bldr.getPackages().length );
    }

    public void testTimeWindowBehavior() throws Exception {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final TypeDeclarationDescr typeDeclDescr = new TypeDeclarationDescr(StockTick.class.getName());
        typeDeclDescr.addMetaAttribute( "role", "event" );
        packageDescr.addTypeDeclaration( typeDeclDescr );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final PatternDescr patternDescr = new PatternDescr( StockTick.class.getName(),
                                                            "$tick" );
        final SlidingWindowDescr windowDescr = new SlidingWindowDescr( "time",
                                                                       "60000" );
        patternDescr.addBehavior( windowDescr );

        lhs.addDescr( patternDescr );

        ruleDescr.setConsequence( "System.out.println( $tick );" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors().getErrors() );

        final Package pkg = builder.getPackage();
        final Rule rule = pkg.getRule( "rule-1" );
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

    class MockRuleFlow
        implements
        Process {

        private String id;

        MockRuleFlow(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return null;
        }

        public String getType() {
            return null;
        }

        public String getVersion() {
            return null;
        }

        public String getPackageName() {
            return null;
        }

        public void setId(String id) {
        }

        public void setName(String name) {
        }

        public void setType(String type) {
        }

        public void setVersion(String version) {
        }

        public void setPackageName(String packageName) {
        }

        public void addContext(Context context) {
        }

        public List<Context> getContexts(String contextId) {
            return null;
        }

        public Context getDefaultContext(String contextId) {
            return null;
        }

        public void setDefaultContext(Context context) {
        }

        public Context getContext(String contextType,
                                  long id) {
            return null;
        }

        public Object getMetaData(String name) {
            return null;
        }

        public void setMetaData(String name,
                                Object value) {
        }

        public Resource getResource() {
            return null;
        }

        public void setResource(Resource resource) {
        }

		public String[] getGlobalNames() {
			return null;
		}

		public Map<String, String> getGlobals() {
			return null;
		}

		public List<String> getImports() {
			return null;
		}

		public void setGlobals(Map<String, String> globals) {
		}

		public void setImports(List<String> imports) {
		}

		public List<String> getFunctionImports() {
			// TODO Auto-generated method stub
			return null;
		}

		public void setFunctionImports(List<String> functionImports) {
			// TODO Auto-generated method stub
			
		}

    }

    class MockActivation
        implements
        Activation {
        private Rule               rule;
        private int                salience;
        private final GroupElement subrule;
        private Tuple              tuple;

        public MockActivation(final Rule rule,
                              int salience,
                              final GroupElement subrule,
                              final Tuple tuple) {
            this.rule = rule;
            this.salience = salience;
            this.tuple = tuple;
            this.subrule = subrule;
        }

        public Rule getRule() {
            return this.rule;
        }

        public int getSalience() {
            return this.salience;
        }

        public Tuple getTuple() {
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

        public void addLogicalDependency(final LogicalDependency node) {
        }

        public LinkedList getLogicalDependencies() {
            return null;
        }

        public boolean isActivated() {
            return false;
        }

        public void setActivated(final boolean activated) {
        }

        public ActivationGroupNode getActivationGroupNode() {
            // TODO Auto-generated method stub
            return null;
        }

        public void setActivationGroupNode(final ActivationGroupNode activationGroupNode) {
            // TODO Auto-generated method stub

        }

        public GroupElement getSubRule() {
            return this.subrule;
        }

        public AgendaGroup getAgendaGroup() {
            // TODO Auto-generated method stub
            return null;
        }

        public ActivationNode getActivationNode() {
            // TODO Auto-generated method stub
            return null;
        }

        public void setActivationNode(final ActivationNode ruleFlowGroupNode) {
            // TODO Auto-generated method stub

        }

        public void setLogicalDependencies(LinkedList justified) {
            // TODO Auto-generated method stub

        }

        public Collection<FactHandle> getFactHandles() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    class MockTuple
        implements
        Tuple {
        private Map declarations;

        public MockTuple(final Map declarations) {
            this.declarations = declarations;
        }

        public InternalFactHandle get(final int patern) {
            return null;
        }

        public InternalFactHandle get(final Declaration declaration) {
            return (InternalFactHandle) this.declarations.get( declaration );
        }

        public InternalFactHandle[] getFactHandles() {
            return (InternalFactHandle[]) this.declarations.values().toArray( new FactHandle[0] );
        }

        public boolean dependsOn(final FactHandle handle) {
            return false;
        }

        public void setActivation(final Activation activation) {
        }

        public long getRecency() {
            // TODO Auto-generated method stub
            return 0;
        }

        public int size() {
            // TODO Auto-generated method stub
            return 0;
        }

    }
}