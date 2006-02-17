package org.drools.semantics.java;

import org.drools.CheckedDroolsException;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ReturnValueDescr;
import org.drools.rule.Rule;
import org.drools.rule.RuleSet;

import junit.framework.TestCase;

public class JavaCompilerTest extends TestCase {

    public void test1() throws CheckedDroolsException {
        AndDescr lhs = new AndDescr();
        
        ColumnDescr column = new ColumnDescr(0, Cheese.class.getName());
        lhs.addDescr( column );
        
        ReturnValueDescr returnValue = new ReturnValueDescr("age", "==", "x * 2");
        returnValue.setDeclarations( new String[] { "x" } );
        column.addDescr( returnValue );
        
        RuleSet ruleSet = new RuleSet("ruleset-1");
        RuleSetBundle bundle = new RuleSetBundle(ruleSet);
        
        JavaRuleCompiler compiler = new JavaRuleCompiler(bundle);
        Rule rule = new Rule("rule-1");
        
        compiler.configure( rule, lhs, null );
    }
    
    class Cheese {
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
