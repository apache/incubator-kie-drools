package org.drools.semantics.java;

import org.drools.CheckedDroolsException;
import org.drools.DroolsTestCase;
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
import org.drools.rule.And;
import org.drools.rule.Column;
import org.drools.rule.Exists;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Or;
import org.drools.rule.Package;
import org.drools.rule.Rule;

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

        assertLength( 3,
                      compiler.getErrors().values() );
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

        LiteralDescr listeralDescr = new LiteralDescr( "type",
                                                       "==",
                                                       "stilton" );

        column.addDescr( listeralDescr );

        ruleDescr.setConsequence( "drools.modifyObject(stilton);" );

        compiler.addPackage( packageDescr );

        assertLength( 0,
                      compiler.getErrors().values() );
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

        assertLength( 0,
                      compiler.getErrors().values() );
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

        assertLength( 0,
                      compiler.getErrors().values() );
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

        assertLength( 0,
                      compiler.getErrors().values() );
    }

    public void testOr() throws Exception {        
        Rule rule  = t(new OrDescr());
        
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
        Rule rule  = t(new AndDescr());
        
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
        Rule rule  = t(new NotDescr());
        
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
        Rule rule = t( new ExistsDescr());
        
        And lhs = rule.getLhs();
        assertLength( 1,
                      lhs.getChildren() );
        
        Exists exists = (Exists) lhs.getChildren().get( 0 );
        assertLength( 1,
                      exists.getChildren() );
        Column column = (Column) exists.getChildren().get( 0 );

        LiteralConstraint literalConstarint = (LiteralConstraint) column.getConstraints().get( 0 );        
    }      
    
    

    private Rule t(ConditionalElementDescr ceDescr) throws Exception {
        DroolsCompiler compiler = new DroolsCompiler();
        
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

        ruleDescr.setConsequence( "drools.modifyObject(stilton);" );

        compiler.addPackage( packageDescr );

        assertLength( 0,
                      compiler.getErrors().values() );

        Package pkg = (Package) compiler.getPackages().get( "p1" );
        Rule rule = pkg.getRule( "rule-1" );
        
        return rule;
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
