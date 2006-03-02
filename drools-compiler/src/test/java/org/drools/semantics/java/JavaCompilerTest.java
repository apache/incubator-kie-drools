package org.drools.semantics.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.drools.CheckedDroolsException;
import org.drools.DroolsTestCase;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
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
import org.drools.reteoo.RuleBaseImpl;
import org.drools.rule.And;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.Exists;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Or;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;
import org.drools.util.LinkedList;

public class JavaCompilerTest extends DroolsTestCase {

    public void testErrors() throws Exception {
        RuleBaseManager manager = new RuleBaseManager();

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

        manager.addPackage( packageDescr );

        assertLength( 2,
                      manager.getCompilationResults().getErrors() );
    }

    public void testReload() throws Exception {
        RuleBaseManager manager = new RuleBaseManager();

        PackageDescr packageDescr = new PackageDescr( "p1" );
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        ruleDescr.setConsequence( "map.put(\"value\", new Integer(1) );" );

        manager.addPackage( packageDescr );

        assertLength( 0,
                      manager.getCompilationResults().getErrors() );

        Package pkg = (Package) manager.getPackages().get( "p1" );
        Rule rule = pkg.getRule( "rule-1" );

        RuleBaseImpl ruleBase = new RuleBaseImpl();
        ruleBase.getGlobalDeclarations().put( "map",
                                              Map.class );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        HashMap map = new HashMap();

        workingMemory.setGlobal( "map",
                                 map );

        Tuple tuple = new MockTuple( new HashMap() );
        Activation activation = new MockActivation( rule,
                                                    tuple );

        rule.getConsequence().evaluate( activation,
                                      workingMemory );
        assertEquals( new Integer( 1 ),
                      map.get( "value" ) );

        ruleDescr.setConsequence( "map.put(\"value\", new Integer(2) );" );
        pkg.removeRule( rule );
        manager.addPackage( packageDescr );

        rule.getConsequence().evaluate( activation,
                                      workingMemory );
        assertEquals( new Integer( 2 ),
                      map.get( "value" ) );

    }

    public void testSerializable() throws Exception {
        RuleBaseManager manager = new RuleBaseManager();

        PackageDescr packageDescr = new PackageDescr( "p1" );
        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );

        AndDescr lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        packageDescr.addGlobal( "map",
                                "java.util.Map" );

        ruleDescr.setConsequence( "map.put(\"value\", new Integer(1) );" );

        manager.addPackage( packageDescr );

        assertLength( 0,
                      manager.getCompilationResults().getErrors() );

        Package pkg = (Package) manager.getPackages().get( "p1" );

        // Serialize to a byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream( bos );
        out.writeObject( pkg );
        out.close();

        // Get the bytes of the serialized object
        byte[] bytes = bos.toByteArray();

        // Deserialize from a byte array

        ObjectInput in = new ObjectInputStream( new ByteArrayInputStream( bytes ) );
        Package newPkg = (Package) in.readObject();
        in.close();

        Rule newRule = newPkg.getRule( "rule-1" );

        RuleBaseImpl ruleBase = new RuleBaseImpl();
        ruleBase.getGlobalDeclarations().put( "map",
                                              Map.class );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        HashMap map = new HashMap();

        workingMemory.setGlobal( "map",
                                 map );

        Tuple tuple = new MockTuple( new HashMap() );
        Activation activation = new MockActivation( newRule,
                                                    tuple );

        newRule.getConsequence().evaluate( activation,
                                         workingMemory );
        assertEquals( new Integer( 1 ),
                      map.get( "value" ) );
    }

    public void testLiteral() throws Exception {
        RuleBaseManager manager = new RuleBaseManager();

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

        manager.addPackage( packageDescr );

        assertLength( 0,
                      manager.getCompilationResults().getErrors() );
    }

    public void testReturnValue() throws Exception {
        RuleBaseManager manager = new RuleBaseManager();

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

        manager.addPackage( packageDescr );

        assertLength( 0,
                      manager.getCompilationResults().getErrors() );
    }

    public void testPredicate() throws Exception {
        RuleBaseManager manager = new RuleBaseManager();

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

        manager.addPackage( packageDescr );

        assertLength( 0,
                      manager.getCompilationResults().getErrors() );
    }

    public void testEval() throws Exception {
        RuleBaseManager manager = new RuleBaseManager();

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

        manager.addPackage( packageDescr );

        assertLength( 0,
                      manager.getCompilationResults().getErrors() );
    }

    public void testOr() throws Exception {
        Rule rule = t( new OrDescr() );

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
        Rule rule = t( new AndDescr() );

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
        Rule rule = t( new NotDescr() );

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
        Rule rule = t( new ExistsDescr() );

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
        RuleBaseManager manager = new RuleBaseManager();

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

        ruleDescr.setConsequence( "modify(stilton);" );

        manager.addPackage( packageDescr );

        assertLength( 0,
                      manager.getCompilationResults().getErrors() );

        Package pkg = (Package) manager.getPackages().get( "p1" );
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
    }

    class MockTuple
        implements
        Tuple {
        private Map declarations;

        public MockTuple(Map declarations) {
            this.declarations = declarations;
        }

        public FactHandle get(int column) {
            return null;
        }

        public FactHandle get(Declaration declaration) {
            return (FactHandle) this.declarations.get( declaration );
        }

        public FactHandle[] getFactHandles() {
            return (FactHandle[]) this.declarations.values().toArray( new FactHandle[0] );
        }

        public boolean dependsOn(FactHandle handle) {
            return false;
        }

        public void setActivation(Activation activation) {
        }

    }
}
