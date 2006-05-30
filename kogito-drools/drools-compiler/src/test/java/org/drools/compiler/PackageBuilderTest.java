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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.Cheese;
import org.drools.DroolsTestCase;
import org.drools.FactHandle;
import org.drools.Primitives;
import org.drools.WorkingMemory;
import org.drools.common.ActivationGroupNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.LogicalDependency;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.LiteralDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.ReturnValueDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.rule.And;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Exists;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Or;
import org.drools.rule.Package;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.Rule;
import org.drools.semantics.java.CompiledInvoker;
import org.drools.spi.Activation;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;
import org.drools.util.LinkedList;

public class PackageBuilderTest extends DroolsTestCase {

    public void testErrors() throws Exception {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final ColumnDescr column = new ColumnDescr( Cheese.class.getName(),
                                              "stilton" );
        lhs.addDescr( column );

        FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "price",
                                                                     "x" );
        column.addDescr( fieldBindingDescr );
        fieldBindingDescr = new FieldBindingDescr( "price",
                                                   "y" );
        column.addDescr( fieldBindingDescr );

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        final ReturnValueDescr returnValue = new ReturnValueDescr( "price",
                                                             "==",
                                                             "x" );
        column.addDescr( returnValue );

        // There is no m this should produce errors.
        ruleDescr.setConsequence( "modify(m);" );

        builder.addPackage( packageDescr );

        assertLength( 1,
                      builder.getErrors() );
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

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        ruleDescr.setConsequence( "map.put(\"value\", new Integer(1) );" );

        builder.addPackage( packageDescr );

        final Package pkg = builder.getPackage();
        Rule rule = pkg.getRule( "rule-1" );

        assertLength( 0,
                      builder.getErrors() );

        final ReteooRuleBase ruleBase = new ReteooRuleBase();
        ruleBase.getGlobals().put( "map",
                                   Map.class );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final HashMap map = new HashMap();
        workingMemory.setGlobal( "map",
                                 map );

        final Tuple tuple = new MockTuple( new HashMap() );
        final Activation activation = new MockActivation( rule,
                                                    tuple );

        KnowledgeHelper knowledgeHelper = new org.drools.base.DefaultKnowledgeHelper( activation,
                                                                                      workingMemory );
        rule.getConsequence().evaluate( knowledgeHelper,
                                        workingMemory );
        assertEquals( new Integer( 1 ),
                      map.get( "value" ) );

        ruleDescr.setConsequence( "map.put(\"value\", new Integer(2) );" );
        pkg.removeRule( rule );

        // Make sure the compiled classes are also removed
        assertEquals( 0,
                      pkg.getPackageCompilationData().list().length );

        builder.addPackage( packageDescr );

        rule = pkg.getRule( "rule-1" );

        knowledgeHelper = new org.drools.base.DefaultKnowledgeHelper( activation,
                                                                      workingMemory );
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

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        ruleDescr.setConsequence( "map.put(\"value\", new Integer(1) );" );
        //check that packageDescr is serializable
        final byte[] ast = serializeOut( packageDescr );
        final PackageDescr back = (PackageDescr) serializeIn( ast );
        assertNotNull( back );
        assertEquals( "p1",
                      back.getName() );

        builder.addPackage( packageDescr );
        final Package pkg = builder.getPackage();
        final Rule rule = pkg.getRule( "rule-1" );

        assertLength( 0,
                      builder.getErrors() );

        final byte[] bytes = serializeOut( pkg );

        // Deserialize from a byte array

        final Package newPkg = (Package) serializeIn( bytes );

        final Rule newRule = newPkg.getRule( "rule-1" );

        final ReteooRuleBase ruleBase = new ReteooRuleBase();
        ruleBase.getGlobals().put( "map",
                                   Map.class );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final HashMap map = new HashMap();

        workingMemory.setGlobal( "map",
                                 map );

        final Tuple tuple = new MockTuple( new HashMap() );
        final Activation activation = new MockActivation( newRule,
                                                    tuple );

        final KnowledgeHelper knowledgeHelper = new org.drools.base.DefaultKnowledgeHelper( activation,
                                                                                      workingMemory );
        newRule.getConsequence().evaluate( knowledgeHelper,
                                           workingMemory );
        assertEquals( new Integer( 1 ),
                      map.get( "value" ) );
    }

    private Object serializeIn(final byte[] bytes) throws IOException,
                                            ClassNotFoundException {
        final ObjectInput in = new ObjectInputStream( new ByteArrayInputStream( bytes ) );
        final Object obj = in.readObject();
        in.close();
        return obj;
    }

    private byte[] serializeOut(final Object obj) throws IOException {
        // Serialize to a byte array
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutput out = new ObjectOutputStream( bos );
        out.writeObject( obj );
        out.close();

        // Get the bytes of the serialized object
        final byte[] bytes = bos.toByteArray();
        return bytes;
    }

    public void testNoPackageName() {
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

    }

    public void testLiteral() throws Exception {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final ColumnDescr column = new ColumnDescr( Cheese.class.getName(),
                                              "stilton" );
        lhs.addDescr( column );

        final LiteralDescr listeralDescr = new LiteralDescr( "type",
                                                       "==",
                                                       "stilton" );

        column.addDescr( listeralDescr );

        ruleDescr.setConsequence( "modify(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors() );
    }

    public void testReturnValue() throws Exception {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final ColumnDescr column = new ColumnDescr( Cheese.class.getName(),
                                              "stilton" );
        lhs.addDescr( column );

        FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "price",
                                                                     "x" );
        column.addDescr( fieldBindingDescr );
        fieldBindingDescr = new FieldBindingDescr( "price",
                                                   "y" );
        column.addDescr( fieldBindingDescr );

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        final ReturnValueDescr returnValue = new ReturnValueDescr( "price",
                                                             "==",
                                                             "new  Integer(( ( ( Integer )map.get(x) ).intValue() * y.intValue()))" );
        column.addDescr( returnValue );

        ruleDescr.setConsequence( "modify(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors() );
    }

    public void testReturnValueMethodCompare() {
        final PackageBuilder builder1 = new PackageBuilder();
        final PackageDescr packageDescr1 = new PackageDescr( "package1" );
        createReturnValueRule( packageDescr1,
                               "new Integer(x.intValue() + y.intValue() )" );
        builder1.addPackage( packageDescr1 );
        final Column column1 = (Column) builder1.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        final ReturnValueConstraint returnValue1 = (ReturnValueConstraint) column1.getConstraints().get( 2 );

        final PackageBuilder builder2 = new PackageBuilder();
        final PackageDescr packageDescr2 = new PackageDescr( "package2" );
        createReturnValueRule( packageDescr2,
                               "new Integer(x.intValue() + y.intValue() )" );
        builder2.addPackage( packageDescr2 );
        final Column column2 = (Column) builder2.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        final ReturnValueConstraint returnValue2 = (ReturnValueConstraint) column2.getConstraints().get( 2 );

        final PackageBuilder builder3 = new PackageBuilder();
        final PackageDescr packageDescr3 = new PackageDescr( "package3" );
        createReturnValueRule( packageDescr3,
                               "new Integer(x.intValue() - y.intValue() )" );
        builder3.addPackage( packageDescr3 );
        final Column column3 = (Column) builder3.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        final ReturnValueConstraint returnValue3 = (ReturnValueConstraint) column3.getConstraints().get( 2 );

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

        final ColumnDescr column = new ColumnDescr( Cheese.class.getName(),
                                              "stilton" );
        lhs.addDescr( column );

        final FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "price",
                                                                     "x" );
        column.addDescr( fieldBindingDescr );

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        final PredicateDescr predicate = new PredicateDescr( "price",
                                                       "y",
                                                       "( ( Integer )map.get(x) ).intValue() == y.intValue()" );
        column.addDescr( predicate );

        ruleDescr.setConsequence( "modify(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors() );
    }

    public void testPredicateMethodCompare() {
        final PackageBuilder builder1 = new PackageBuilder();
        final PackageDescr packageDescr1 = new PackageDescr( "package1" );
        createPredicateRule( packageDescr1,
                             "x==y" );
        builder1.addPackage( packageDescr1 );
        final Column column1 = (Column) builder1.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        final PredicateConstraint predicate1 = (PredicateConstraint) column1.getConstraints().get( 2 );

        final PackageBuilder builder2 = new PackageBuilder();
        final PackageDescr packageDescr2 = new PackageDescr( "package2" );
        createPredicateRule( packageDescr2,
                             "x==y" );
        builder2.addPackage( packageDescr2 );
        final Column column2 = (Column) builder2.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        final PredicateConstraint predicate2 = (PredicateConstraint) column2.getConstraints().get( 2 );

        final PackageBuilder builder3 = new PackageBuilder();
        final PackageDescr packageDescr3 = new PackageDescr( "package3" );
        createPredicateRule( packageDescr3,
                             "x!=y" );
        builder3.addPackage( packageDescr3 );
        final Column column3 = (Column) builder3.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        final PredicateConstraint predicate3 = (PredicateConstraint) column3.getConstraints().get( 2 );

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

        final ColumnDescr column = new ColumnDescr( Cheese.class.getName(),
                                              "stilton" );
        lhs.addDescr( column );

        FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "price",
                                                                     "x" );
        column.addDescr( fieldBindingDescr );
        fieldBindingDescr = new FieldBindingDescr( "price",
                                                   "y" );
        column.addDescr( fieldBindingDescr );

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        final EvalDescr evalDescr = new EvalDescr( "( ( Integer )map.get(x) ).intValue() == y.intValue()" );
        lhs.addDescr( evalDescr );

        ruleDescr.setConsequence( "modify(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors() );

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
                                "modify(stilton);" );
        assertLength( 0,
                      builder.getErrors() );

        final And lhs = rule.getLhs();
        assertLength( 1,
                      lhs.getChildren() );

        final Or or = (Or) lhs.getChildren().get( 0 );
        assertLength( 1,
                      or.getChildren() );
        final Column column = (Column) or.getChildren().get( 0 );

        final LiteralConstraint literalConstarint = (LiteralConstraint) column.getConstraints().get( 0 );
    }

    public void testAnd() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        final Rule rule = createRule( new AndDescr(),
                                builder,
                                "modify(stilton);" );
        assertLength( 0,
                      builder.getErrors() );

        final And lhs = rule.getLhs();
        assertLength( 1,
                      lhs.getChildren() );

        final And and = (And) lhs.getChildren().get( 0 );
        assertLength( 1,
                      and.getChildren() );
        final Column column = (Column) and.getChildren().get( 0 );

        final LiteralConstraint literalConstarint = (LiteralConstraint) column.getConstraints().get( 0 );
    }

    public void testNot() throws Exception {
        PackageBuilder builder = new PackageBuilder();

        // Make sure we can't accessa  variable bound inside the not node
        Rule rule = createRule( new NotDescr(),
                                builder,
                                "modify(stilton);" );
        assertEquals( 1,
                      builder.getErrors().length );

        builder = new PackageBuilder();
        rule = createRule( new NotDescr(),
                           builder,
                           "" );
        assertEquals( 0,
                      builder.getErrors().length );

        final And lhs = rule.getLhs();
        assertLength( 1,
                      lhs.getChildren() );

        final Not not = (Not) lhs.getChildren().get( 0 );
        assertLength( 1,
                      not.getChildren() );
        final Column column = (Column) not.getChildren().get( 0 );

        final LiteralConstraint literalConstarint = (LiteralConstraint) column.getConstraints().get( 0 );
    }

    public void testExists() throws Exception {
        PackageBuilder builder = new PackageBuilder();

        // Make sure we can't accessa  variable bound inside the not node
        Rule rule = createRule( new ExistsDescr(),
                                builder,
                                "modify(stilton);" );
        assertEquals( 1,
                      builder.getErrors().length );

        builder = new PackageBuilder();
        rule = createRule( new ExistsDescr(),
                           builder,
                           "" );
        assertEquals( 0,
                      builder.getErrors().length );

        final And lhs = rule.getLhs();
        assertLength( 1,
                      lhs.getChildren() );

        final Exists exists = (Exists) lhs.getChildren().get( 0 );
        assertLength( 1,
                      exists.getChildren() );
        final Column column = (Column) exists.getChildren().get( 0 );

        final LiteralConstraint literalConstarint = (LiteralConstraint) column.getConstraints().get( 0 );
    }

    public void testNumbers() throws Exception {
        // test boolean
        createLiteralRule( new LiteralDescr( "booleanPrimitive",
                                             "==",
                                             "true" ) );

        // test boolean
        createLiteralRule( new LiteralDescr( "booleanPrimitive",
                                             "==",
                                             "false" ) );

        // test char
        createLiteralRule( new LiteralDescr( "charPrimitive",
                                             "==",
                                             "a" ) );

        // test byte
        createLiteralRule( new LiteralDescr( "bytePrimitive",
                                             "==",
                                             "1" ) );

        createLiteralRule( new LiteralDescr( "bytePrimitive",
                                             "==",
                                             "0" ) );

        createLiteralRule( new LiteralDescr( "bytePrimitive",
                                             "==",
                                             "-1" ) );

        // test short
        createLiteralRule( new LiteralDescr( "shortPrimitive",
                                             "==",
                                             "1" ) );

        createLiteralRule( new LiteralDescr( "shortPrimitive",
                                             "==",
                                             "0" ) );

        createLiteralRule( new LiteralDescr( "shortPrimitive",
                                             "==",
                                             "-1" ) );

        // test int
        createLiteralRule( new LiteralDescr( "intPrimitive",
                                             "==",
                                             "1" ) );

        createLiteralRule( new LiteralDescr( "intPrimitive",
                                             "==",
                                             "0" ) );

        createLiteralRule( new LiteralDescr( "intPrimitive",
                                             "==",
                                             "-1" ) );

        //        // test long
        createLiteralRule( new LiteralDescr( "longPrimitive",
                                             "==",
                                             "1" ) );

        createLiteralRule( new LiteralDescr( "longPrimitive",
                                             "==",
                                             "0" ) );

        createLiteralRule( new LiteralDescr( "longPrimitive",
                                             "==",
                                             "-1" ) );

        // test float
        createLiteralRule( new LiteralDescr( "floatPrimitive",
                                             "==",
                                             "1.1" ) );

        createLiteralRule( new LiteralDescr( "floatPrimitive",
                                             "==",
                                             "0" ) );

        createLiteralRule( new LiteralDescr( "floatPrimitive",
                                             "==",
                                             "-1.1" ) );

        // test double
        createLiteralRule( new LiteralDescr( "doublePrimitive",
                                             "==",
                                             "1.1" ) );

        createLiteralRule( new LiteralDescr( "doublePrimitive",
                                             "==",
                                             "0" ) );

        createLiteralRule( new LiteralDescr( "doublePrimitive",
                                             "==",
                                             "-1.1" ) );
    }

    public void testNull() {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final ColumnDescr columnDescr = new ColumnDescr( Cheese.class.getName(),
                                                   "stilton" );

        final LiteralDescr literalDescr = new LiteralDescr( "type",
                                                      "==",
                                                      null );
        columnDescr.addDescr( literalDescr );

        ruleDescr.setConsequence( "" );

        builder.addPackage( packageDescr );

        final Package pkg = (Package) builder.getPackage();
        final Rule rule = pkg.getRule( "rule-1" );

        assertLength( 0,
                      builder.getErrors() );
    }

    private void createReturnValueRule(final PackageDescr packageDescr,
                                       final String expression) {
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final ColumnDescr column = new ColumnDescr( Cheese.class.getName(),
                                              "stilton" );
        lhs.addDescr( column );

        FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "price",
                                                                     "x" );
        column.addDescr( fieldBindingDescr );
        fieldBindingDescr = new FieldBindingDescr( "price",
                                                   "y" );
        column.addDescr( fieldBindingDescr );

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        final ReturnValueDescr returnValue = new ReturnValueDescr( "price",
                                                             "==",
                                                             expression );
        column.addDescr( returnValue );

        ruleDescr.setConsequence( "modify(stilton);" );
    }

    private void createPredicateRule(final PackageDescr packageDescr,
                                     final String expression) {
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final ColumnDescr column = new ColumnDescr( Cheese.class.getName(),
                                              "stilton" );
        lhs.addDescr( column );

        final FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "price",
                                                                     "x" );
        column.addDescr( fieldBindingDescr );

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        final PredicateDescr predicate = new PredicateDescr( "price",
                                                       "y",
                                                       expression );
        column.addDescr( predicate );

        ruleDescr.setConsequence( "modify(stilton);" );
    }

    private void createEvalRule(final PackageDescr packageDescr,
                                final String expression) {
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        final EvalDescr evalDescr = new EvalDescr( expression );
        lhs.addDescr( evalDescr );

        ruleDescr.setConsequence( "" );
    }

    private void createLiteralRule(final LiteralDescr literalDescr) {
        final PackageBuilder builder = new PackageBuilder();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        final ColumnDescr column = new ColumnDescr( Primitives.class.getName() );
        lhs.addDescr( column );

        column.addDescr( literalDescr );

        ruleDescr.setConsequence( "" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors() );
    }

    private Rule createRule(final ConditionalElementDescr ceDescr,
                            final PackageBuilder builder,
                            final String consequence) throws Exception {
        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        final AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        lhs.addDescr( (PatternDescr) ceDescr );

        final ColumnDescr columnDescr = new ColumnDescr( Cheese.class.getName(),
                                                   "stilton" );

        final LiteralDescr literalDescr = new LiteralDescr( "type",
                                                      "==",
                                                      "stilton" );
        columnDescr.addDescr( literalDescr );

        ceDescr.addDescr( columnDescr );

        ruleDescr.setConsequence( consequence );

        builder.addPackage( packageDescr );

        final Package pkg = (Package) builder.getPackage();
        final Rule rule = pkg.getRule( "rule-1" );

        assertEquals( "rule-1",
                      rule.getName() );

        return rule;
    }

    class MockActivation
        implements
        Activation {
        private Rule  rule;
        private Tuple tuple;

        public MockActivation(final Rule rule,
                              final Tuple tuple) {
            this.rule = rule;
            this.tuple = tuple;
        }

        public Rule getRule() {
            return this.rule;
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
    }

    class MockTuple
        implements
        Tuple {
        private Map declarations;

        public MockTuple(final Map declarations) {
            this.declarations = declarations;
        }

        public InternalFactHandle get(final int column) {
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

    }
}