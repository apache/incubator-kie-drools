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

public class JavaCompilerTest extends TestCase {

    public void testReturnValue() throws Exception {
        DroolsCompiler compiler = new DroolsCompiler();
        
        PackageDescr packageDescr = new PackageDescr("p1");        
        RuleDescr ruleDescr = new RuleDescr("rule-1");
        packageDescr.addRule( ruleDescr );
        
        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );
        
        ColumnDescr column = new ColumnDescr(Cheese.class.getName(), "stilton");
        lhs.addDescr( column );
        
        FieldBindingDescr fieldBindingDescr = new FieldBindingDescr("price", "x");
        column.addDescr( fieldBindingDescr );
        fieldBindingDescr = new FieldBindingDescr("price", "y");
        column.addDescr( fieldBindingDescr );
        
        packageDescr.addGlobal( "p", "java.util.Map" );                  

        ReturnValueDescr returnValue = new ReturnValueDescr("price", "==", "new  Integer(( x.intValue() * y.intValue()))");
        column.addDescr( returnValue );              
        
        ruleDescr.setConsequence( "drools.modifyObject(stilton);" );
        
        compiler.addPackage( packageDescr );
        
        ////////////////////////////////////////////////
        
//        MemoryResourceReader src = new MemoryResourceReader();
//        MemoryResourceStore  dst = new MemoryResourceStore();
//        JavaCompiler compiler = JavaCompilerFactory.getInstance().createCompiler( JavaCompilerFactory.ECLIPSE );
//        
//        String className = this.pkg.getName() + "." + ucFirst( this.ruleDescr.getClassName() );
//        String fileName = this.pkg.getName() + "/" + ucFirst( this.ruleDescr.getClassName() ) + ".java";        
//        
//        src.addFile( fileName, string.toString().toCharArray() );                
//        CompilationResult result = compiler.compile( new String[] { className },src, dst );
//        
//        System.out.println( "errors: " + result.getErrors().length );
//        
//        if ( result.getErrors().length > 0 ) {
//            for (int i = 0; i < result.getErrors().length; i++) {
//                System.out.println( result.getErrors()[i].getMessage() );
//            }
//        }   
    }
    
    public void testPredicate() throws CheckedDroolsException, ClassNotFoundException {
        DroolsCompiler compiler = new DroolsCompiler();
        
        PackageDescr packageDescr = new PackageDescr("p1");        
        RuleDescr ruleDescr = new RuleDescr("rule-1");
        packageDescr.addRule( ruleDescr );
        
        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );
        
        ColumnDescr column = new ColumnDescr(Cheese.class.getName());
        lhs.addDescr( column );
        
        FieldBindingDescr fieldBindingDescr = new FieldBindingDescr("price", "x");
        column.addDescr( fieldBindingDescr );
        fieldBindingDescr = new FieldBindingDescr("type", "y");
        column.addDescr( fieldBindingDescr );

        fieldBindingDescr = new FieldBindingDescr("price", "z");
        column.addDescr( fieldBindingDescr );        
        
        packageDescr.addGlobal( "p", "java.util.Map" );                  
        
        PredicateDescr predicate = new PredicateDescr("price", "z", "y == p.get(x) * z");
        column.addDescr( predicate );            
        
        ruleDescr.setConsequence( "hello" );
        
        compiler.addPackage( packageDescr );
    }
    
    public void testEval() throws Exception {
        DroolsCompiler compiler = new DroolsCompiler();
        
        PackageDescr packageDescr = new PackageDescr("p1");        
        RuleDescr ruleDescr = new RuleDescr("rule-1");
        packageDescr.addRule( ruleDescr );
        
        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );
        
        ColumnDescr column = new ColumnDescr(Cheese.class.getName());
        lhs.addDescr( column );
        
        FieldBindingDescr fieldBindingDescr = new FieldBindingDescr("price", "x");
        column.addDescr( fieldBindingDescr );
        fieldBindingDescr = new FieldBindingDescr("type", "y");
        column.addDescr( fieldBindingDescr );
        
        packageDescr.addGlobal( "p", "java.util.Map" );                  
        
        EvalDescr eval = new EvalDescr("y == p.get(x) * z");
        column.addDescr( eval );                         
        
        ruleDescr.setConsequence( "hello" );
        
        compiler.addPackage( packageDescr );
    }    

    public void testConsequence() throws Exception {
        DroolsCompiler compiler = new DroolsCompiler();
        
        PackageDescr packageDescr = new PackageDescr("p1");        
        RuleDescr ruleDescr = new RuleDescr("rule-1");
        packageDescr.addRule( ruleDescr );
        
        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );
        
        ColumnDescr column = new ColumnDescr(Cheese.class.getName());
        lhs.addDescr( column );
        
        FieldBindingDescr fieldBindingDescr = new FieldBindingDescr("price", "x");
        column.addDescr( fieldBindingDescr );
        fieldBindingDescr = new FieldBindingDescr("type", "y");
        column.addDescr( fieldBindingDescr );
        
        packageDescr.addGlobal( "p", "java.util.Map" );                  
        
        ruleDescr.setConsequence( "hello" );
        
        compiler.addPackage( packageDescr );
    }       
    
    public class Cheese {
        private String type;
        private int price;
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
