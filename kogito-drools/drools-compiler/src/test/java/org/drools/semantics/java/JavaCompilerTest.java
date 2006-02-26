package org.drools.semantics.java;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.readers.MemoryResourceReader;
import org.apache.commons.jci.stores.MemoryResourceStore;
import org.drools.CheckedDroolsException;
import org.drools.DroolsTestCase;
import org.drools.base.ClassFieldExtractor;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ConsequenceDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.ReturnValueDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Rule;
import org.drools.rule.Package;
import org.drools.util.asm.FieldAccessor;
import org.drools.util.asm.FieldAccessorGenerator;
import org.drools.util.asm.FieldAccessorMap;

import junit.framework.TestCase;

public class JavaCompilerTest extends DroolsTestCase {

    public void testErrors() throws Exception {
        DroolsCompiler compiler = new DroolsCompiler();

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

        ruleDescr.setConsequence( "drools.modifyObject(m);" );

        compiler.addPackage( packageDescr );
        
        assertLength( 3, compiler.getErrors().values() );
    }
    
    public void testLiteral() throws Exception {
        DroolsCompiler compiler = new DroolsCompiler();

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

        ruleDescr.setConsequence( "drools.modifyObject(stilton);" );

        compiler.addPackage( packageDescr );
        
        assertLength( 0, compiler.getErrors().values() );
    }    
    
    public void testReturnValue() throws Exception {
        DroolsCompiler compiler = new DroolsCompiler();

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

        ruleDescr.setConsequence( "drools.modifyObject(stilton);" );

        compiler.addPackage( packageDescr );
        
        assertLength( 0, compiler.getErrors().values() );
    }

    public void testPredicate() throws CheckedDroolsException,
                               ClassNotFoundException {
        DroolsCompiler compiler = new DroolsCompiler();

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

        ruleDescr.setConsequence( "drools.modifyObject(stilton);" );

        compiler.addPackage( packageDescr );
        
        assertLength( 0, compiler.getErrors().values() );
    }

    public void testEval() throws Exception {
        DroolsCompiler compiler = new DroolsCompiler();

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

        EvalDescr eval = new EvalDescr( "( ( Integer )map.get(x) ).intValue() == y.intValue()" );
        column.addDescr( eval );

        ruleDescr.setConsequence( "drools.modifyObject(stilton);" );

        compiler.addPackage( packageDescr );
        
        assertLength( 0, compiler.getErrors().values() );
    } 
    
    public void testConditionalElements() throws Exception {
        DroolsCompiler compiler = new DroolsCompiler();

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

        EvalDescr eval = new EvalDescr( "( ( Integer )map.get(x) ).intValue() == y.intValue()" );
        column.addDescr( eval );

        ruleDescr.setConsequence( "drools.modifyObject(stilton);" );

        compiler.addPackage( packageDescr );
        
        assertLength( 0, compiler.getErrors().values() );        
    }

    public class Cheese {
        private String type;
        private int    price;

        public Cheese(String type,
                      int price) {
            super();
            this.type = type;
            this.price = price;
        }

        public int getPrice() {
            return price;
        }

        public String getType() {
            return type;
        }
    }        
}
