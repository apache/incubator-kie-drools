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

import org.drools.CheckedDroolsException;
import org.drools.Cheese;
import org.drools.DroolsTestCase;
import org.drools.FactHandle;
import org.drools.Primitives;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.common.LogicalDependency;
import org.drools.common.XorGroupNode;
import org.drools.compiler.PackageBuilder;
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
import org.drools.reteoo.RuleBaseImpl;
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
import org.drools.util.asm.MethodComparator;

public class PackageBuilderTest extends DroolsTestCase {

    public void testErrors() throws Exception {
        PackageBuilder builder = new PackageBuilder();

        PackageDescr packageDescr = new PackageDescr( "p1" );
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        ColumnDescr column = new ColumnDescr( Cheese.class.getName(),
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

        ReturnValueDescr returnValue = new ReturnValueDescr( "price",
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
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader(this.getClass().getResourceAsStream( "bad_rule.drl" )) );
        assertTrue(builder.hasErrors());
    }

    public void testReload() throws Exception {
        PackageBuilder builder = new PackageBuilder();

        PackageDescr packageDescr = new PackageDescr( "p1" );
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        ruleDescr.setConsequence( "map.put(\"value\", new Integer(1) );" );

        builder.addPackage( packageDescr );

        Package pkg = builder.getPackage();
        Rule rule = pkg.getRule( "rule-1" );

        assertLength( 0,
                      builder.getErrors() );

        RuleBaseImpl ruleBase = new RuleBaseImpl();
        ruleBase.getGlobals().put( "map",
                                   Map.class );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        HashMap map = new HashMap();
        workingMemory.setGlobal( "map",
                                 map );

        Tuple tuple = new MockTuple( new HashMap() );
        Activation activation = new MockActivation( rule,
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
        PackageBuilder builder = new PackageBuilder();

        PackageDescr packageDescr = new PackageDescr( "p1" );
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        ruleDescr.setConsequence( "map.put(\"value\", new Integer(1) );" );
        //check that packageDescr is serializable
        byte[] ast = serializeOut( packageDescr );
        PackageDescr back = (PackageDescr) serializeIn( ast );
        assertNotNull( back );
        assertEquals( "p1",
                      back.getName() );

        builder.addPackage( packageDescr );
        Package pkg = builder.getPackage();
        Rule rule = pkg.getRule( "rule-1" );

        assertLength( 0,
                      builder.getErrors() );

        byte[] bytes = serializeOut( pkg );

        // Deserialize from a byte array

        Package newPkg = (Package) serializeIn( bytes );

        Rule newRule = newPkg.getRule( "rule-1" );

        RuleBaseImpl ruleBase = new RuleBaseImpl();
        ruleBase.getGlobals().put( "map",
                                   Map.class );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        HashMap map = new HashMap();

        workingMemory.setGlobal( "map",
                                 map );

        Tuple tuple = new MockTuple( new HashMap() );
        Activation activation = new MockActivation( newRule,
                                                    tuple );

        KnowledgeHelper knowledgeHelper = new org.drools.base.DefaultKnowledgeHelper( activation,
                                                                                      workingMemory );
        newRule.getConsequence().evaluate( knowledgeHelper,
                                           workingMemory );
        assertEquals( new Integer( 1 ),
                      map.get( "value" ) );
    }

    private Object serializeIn(byte[] bytes) throws IOException,
                                            ClassNotFoundException {
        ObjectInput in = new ObjectInputStream( new ByteArrayInputStream( bytes ) );
        Object obj = in.readObject();
        in.close();
        return obj;
    }
    
    

    private byte[] serializeOut(Object obj) throws IOException {
        // Serialize to a byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream( bos );
        out.writeObject( obj );
        out.close();

        // Get the bytes of the serialized object
        byte[] bytes = bos.toByteArray();
        return bytes;
    }
    
    public void testNoPackageName() {
        PackageBuilder builder = new PackageBuilder();
        try {
            builder.addPackage( new PackageDescr(null) );
            fail("should have errored here.");
        } catch (RuntimeException e) {
            assertNotNull(e.getMessage());
        }
        try {
            builder.addPackage( new PackageDescr("") );
            fail("should have errored here.");
        } catch (RuntimeException e) {
            assertNotNull(e.getMessage());
        }
        
        
        
    }

    public void testLiteral() throws Exception {
        PackageBuilder builder = new PackageBuilder();

        PackageDescr packageDescr = new PackageDescr( "p1" );
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        ColumnDescr column = new ColumnDescr( Cheese.class.getName(),
                                              "stilton" );
        lhs.addDescr( column );

        LiteralDescr listeralDescr = new LiteralDescr( "type",
                                                       "==",
                                                       "stilton" );

        column.addDescr( listeralDescr );

        ruleDescr.setConsequence( "modify(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors() );
    }

    public void testReturnValue() throws Exception {
        PackageBuilder builder = new PackageBuilder();

        PackageDescr packageDescr = new PackageDescr( "p1" );
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        ColumnDescr column = new ColumnDescr( Cheese.class.getName(),
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

        ReturnValueDescr returnValue = new ReturnValueDescr( "price",
                                                             "==",
                                                             "new  Integer(( ( ( Integer )map.get(x) ).intValue() * y.intValue()))" );
        column.addDescr( returnValue );

        ruleDescr.setConsequence( "modify(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors() );
    }
    
    public void testReturnValueMethodCompare() {
        PackageBuilder builder1 = new  PackageBuilder();
        PackageDescr packageDescr1 = new PackageDescr( "package1" );
        createReturnValueRule( packageDescr1, "new Integer(x.intValue() + y.intValue() )" );
        builder1.addPackage( packageDescr1 );
        Column column1 = (Column) builder1.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        ReturnValueConstraint returnValue1 = (ReturnValueConstraint) column1.getConstraints().get( 2 );        
        
        PackageBuilder builder2 = new  PackageBuilder();
        PackageDescr packageDescr2 = new PackageDescr( "package2" );
        createReturnValueRule( packageDescr2, "new Integer(x.intValue() + y.intValue() )" );        
        builder2.addPackage( packageDescr2 );   
        Column column2 = (Column) builder2.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        ReturnValueConstraint returnValue2 = (ReturnValueConstraint) column2.getConstraints().get( 2 );

        PackageBuilder builder3 = new  PackageBuilder();
        PackageDescr packageDescr3 = new PackageDescr( "package3" );
        createReturnValueRule( packageDescr3, "new Integer(x.intValue() - y.intValue() )" );
        builder3.addPackage( packageDescr3 );
        Column column3 = (Column) builder3.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        ReturnValueConstraint returnValue3 = (ReturnValueConstraint) column3.getConstraints().get( 2 );
        
        assertEquals( returnValue1, returnValue2);
        assertFalse( returnValue1.equals( returnValue3 ) );
        assertFalse( returnValue2.equals( returnValue3 ) );       
    }      

    public void testPredicate() throws Exception {
        PackageBuilder builder = new PackageBuilder();

        PackageDescr packageDescr = new PackageDescr( "p1" );
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        ColumnDescr column = new ColumnDescr( Cheese.class.getName(),
                                              "stilton" );
        lhs.addDescr( column );

        FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "price",
                                                                     "x" );
        column.addDescr( fieldBindingDescr );

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        PredicateDescr predicate = new PredicateDescr( "price",
                                                       "y",
                                                       "( ( Integer )map.get(x) ).intValue() == y.intValue()" );
        column.addDescr( predicate );

        ruleDescr.setConsequence( "modify(stilton);" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors() );
    }
    
    public void testPredicateMethodCompare() {
        PackageBuilder builder1 = new  PackageBuilder();
        PackageDescr packageDescr1 = new PackageDescr( "package1" );
        createPredicateRule( packageDescr1, "x==y" );
        builder1.addPackage( packageDescr1 );
        Column column1 = (Column) builder1.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        PredicateConstraint predicate1 = (PredicateConstraint) column1.getConstraints().get( 2 );        
        
        PackageBuilder builder2 = new  PackageBuilder();
        PackageDescr packageDescr2 = new PackageDescr( "package2" );
        createPredicateRule( packageDescr2, "x==y" );        
        builder2.addPackage( packageDescr2 );   
        Column column2 = (Column) builder2.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        PredicateConstraint predicate2 = (PredicateConstraint) column2.getConstraints().get( 2 );

        PackageBuilder builder3 = new  PackageBuilder();
        PackageDescr packageDescr3 = new PackageDescr( "package3" );
        createPredicateRule( packageDescr3, "x!=y" );
        builder3.addPackage( packageDescr3 );
        Column column3 = (Column) builder3.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        PredicateConstraint predicate3 = (PredicateConstraint) column3.getConstraints().get( 2 );
        
        assertEquals( predicate1, predicate2);
        assertFalse( predicate1.equals( predicate3 ) );
        assertFalse( predicate2.equals( predicate3 ) );       
    }    

    public void testEval() throws Exception {
        PackageBuilder builder = new PackageBuilder();

        PackageDescr packageDescr = new PackageDescr( "p1" );
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        ColumnDescr column = new ColumnDescr( Cheese.class.getName(),
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

        EvalDescr evalDescr = new EvalDescr( "( ( Integer )map.get(x) ).intValue() == y.intValue()" );
        lhs.addDescr( evalDescr );

        ruleDescr.setConsequence( "modify(stilton);" );

        builder.addPackage( packageDescr );                        

        assertLength( 0,
                      builder.getErrors() );
        
        Package pkg = builder.getPackage();
        Rule rule = pkg.getRule( "rule-1" );
        EvalCondition eval = (EvalCondition) rule.getLhs().getChildren().get( 1 );
        CompiledInvoker invoker = (CompiledInvoker ) eval.getEvalExpression();
        List list =  invoker.getMethodBytecode();
    }
    
    public void testEvalMethodCompare() {
        PackageBuilder builder1 = new  PackageBuilder();
        PackageDescr packageDescr1 = new PackageDescr( "package1" );
        createEvalRule( packageDescr1, "1==1" );
        builder1.addPackage( packageDescr1 );
        EvalCondition eval1 = (EvalCondition) builder1.getPackage().getRules()[0].getLhs().getChildren().get( 0 );        
        
        PackageBuilder builder2 = new  PackageBuilder();
        PackageDescr packageDescr2 = new PackageDescr( "package2" );
        createEvalRule( packageDescr2, "1==1" );        
        builder2.addPackage( packageDescr2 );   
        EvalCondition eval2 = (EvalCondition) builder2.getPackage().getRules()[0].getLhs().getChildren().get( 0 );

        PackageBuilder builder3 = new  PackageBuilder();
        PackageDescr packageDescr3 = new PackageDescr( "package3" );
        createEvalRule( packageDescr3, "1==3" );
        builder3.addPackage( packageDescr3 );
        EvalCondition eval3 = (EvalCondition) builder3.getPackage().getRules()[0].getLhs().getChildren().get( 0 );
        
        assertEquals( eval1, eval2);
        assertFalse( eval1.equals( eval3 ) );
        assertFalse( eval2.equals( eval3 ) );       
    }

    public void testOr() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        Rule rule = createRule( new OrDescr(),
                                builder,
                                "modify(stilton);" );
        assertLength( 0,
                      builder.getErrors() );

        And lhs = rule.getLhs();
        assertLength( 1,
                      lhs.getChildren() );

        Or or = (Or) lhs.getChildren().get( 0 );
        assertLength( 1,
                      or.getChildren() );
        Column column = (Column) or.getChildren().get( 0 );

        LiteralConstraint literalConstarint = (LiteralConstraint) column.getConstraints().get( 0 );
    }

    public void testAnd() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        Rule rule = createRule( new AndDescr(),
                                builder,
                                "modify(stilton);" );
        assertLength( 0,
                      builder.getErrors() );

        And lhs = rule.getLhs();
        assertLength( 1,
                      lhs.getChildren() );

        And and = (And) lhs.getChildren().get( 0 );
        assertLength( 1,
                      and.getChildren() );
        Column column = (Column) and.getChildren().get( 0 );

        LiteralConstraint literalConstarint = (LiteralConstraint) column.getConstraints().get( 0 );
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

        And lhs = rule.getLhs();
        assertLength( 1,
                      lhs.getChildren() );

        Not not = (Not) lhs.getChildren().get( 0 );
        assertLength( 1,
                      not.getChildren() );
        Column column = (Column) not.getChildren().get( 0 );

        LiteralConstraint literalConstarint = (LiteralConstraint) column.getConstraints().get( 0 );
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

        And lhs = rule.getLhs();
        assertLength( 1,
                      lhs.getChildren() );

        Exists exists = (Exists) lhs.getChildren().get( 0 );
        assertLength( 1,
                      exists.getChildren() );
        Column column = (Column) exists.getChildren().get( 0 );

        LiteralConstraint literalConstarint = (LiteralConstraint) column.getConstraints().get( 0 );
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
        PackageBuilder builder = new PackageBuilder();
        
        PackageDescr packageDescr = new PackageDescr( "p1" );
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        ColumnDescr columnDescr = new ColumnDescr( Cheese.class.getName(),
                                                   "stilton" );

        LiteralDescr literalDescr = new LiteralDescr( "type",
                                                      "==",
                                                      null );
        columnDescr.addDescr( literalDescr );        

        ruleDescr.setConsequence( "" );

        builder.addPackage( packageDescr );

        Package pkg = (Package) builder.getPackage();
        Rule rule = pkg.getRule( "rule-1" ); 
        
        assertLength( 0,
                      builder.getErrors() );        
    }   
    
    private void createReturnValueRule(PackageDescr packageDescr, String expression) {
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        ColumnDescr column = new ColumnDescr( Cheese.class.getName(),
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

        ReturnValueDescr returnValue = new ReturnValueDescr( "price",
                                                             "==",
                                                             expression );
        column.addDescr( returnValue );

        ruleDescr.setConsequence( "modify(stilton);" );
    }      
    
    private void createPredicateRule(PackageDescr packageDescr, String expression) {
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        ColumnDescr column = new ColumnDescr( Cheese.class.getName(),
                                              "stilton" );
        lhs.addDescr( column );

        FieldBindingDescr fieldBindingDescr = new FieldBindingDescr( "price",
                                                                     "x" );
        column.addDescr( fieldBindingDescr );

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        PredicateDescr predicate = new PredicateDescr( "price",
                                                       "y",
                                                       expression );
        column.addDescr( predicate );

        ruleDescr.setConsequence( "modify(stilton);" );
    }    
    
    private void createEvalRule(PackageDescr packageDescr, String expression) {
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        EvalDescr evalDescr = new EvalDescr( expression );
        lhs.addDescr( evalDescr );

        ruleDescr.setConsequence( "" );
    }

    private void createLiteralRule(LiteralDescr literalDescr) {
        PackageBuilder builder = new PackageBuilder();

        PackageDescr packageDescr = new PackageDescr( "p1" );
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        ColumnDescr column = new ColumnDescr( Primitives.class.getName() );
        lhs.addDescr( column );

        column.addDescr( literalDescr );

        ruleDescr.setConsequence( "" );

        builder.addPackage( packageDescr );

        assertLength( 0,
                      builder.getErrors() );
    }   

    private Rule createRule(ConditionalElementDescr ceDescr,
                            PackageBuilder builder,
                            String consequence) throws Exception {
        PackageDescr packageDescr = new PackageDescr( "p1" );
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        lhs.addDescr( (PatternDescr) ceDescr );

        ColumnDescr columnDescr = new ColumnDescr( Cheese.class.getName(),
                                                   "stilton" );

        LiteralDescr literalDescr = new LiteralDescr( "type",
                                                      "==",
                                                      "stilton" );
        columnDescr.addDescr( literalDescr );

        ceDescr.addDescr( columnDescr );

        ruleDescr.setConsequence( consequence );

        builder.addPackage( packageDescr );

        Package pkg = (Package) builder.getPackage();
        Rule rule = pkg.getRule( "rule-1" );
        
        assertEquals( "rule-1", 
                       rule.getName() );

        return rule;
    }

    class MockActivation
        implements
        Activation {
        private Rule  rule;
        private Tuple tuple;

        public MockActivation(Rule rule,
                              Tuple tuple) {
            this.rule = rule;
            this.tuple = tuple;
        }

        public Rule getRule() {
            return rule;
        }

        public Tuple getTuple() {
            return tuple;
        }

        public PropagationContext getPropagationContext() {
            return null;
        }

        public long getActivationNumber() {
            return 0;
        }

        public void remove() {
        }

        public void addLogicalDependency(LogicalDependency node) {
        }

        public LinkedList getLogicalDependencies() {
            return null;
        }

        public boolean isActivated() {
            return false;
        }

        public void setActivated(boolean activated) {
        }

        public XorGroupNode getXorGroupNode() {
            // TODO Auto-generated method stub
            return null;
        }

        public void setXorGroupNode(XorGroupNode xorGroupNode) {
            // TODO Auto-generated method stub
            
        }
    }

    class MockTuple
        implements
        Tuple {
        private Map declarations;

        public MockTuple(Map declarations) {
            this.declarations = declarations;
        }

        public InternalFactHandle get(int column) {
            return null;
        }

        public InternalFactHandle get(Declaration declaration) {
            return (InternalFactHandle) this.declarations.get( declaration );
        }

        public InternalFactHandle[] getFactHandles() {
            return (InternalFactHandle[]) this.declarations.values().toArray( new FactHandle[0] );
        }

        public boolean dependsOn(FactHandle handle) {
            return false;
        }

        public void setActivation(Activation activation) {
        }

        public long getRecency() {
            // TODO Auto-generated method stub
            return 0;
        }

    }
}