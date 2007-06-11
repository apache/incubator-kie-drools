package org.drools.reteoo;

import org.drools.Otherwise;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.base.ShadowProxyFactory;
import org.drools.base.TestBean;
import org.drools.rule.Pattern;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;

import junit.framework.TestCase;

/**
 * This tests the "otherwise" feature.
 * @author Michael Neale
 */
public class OtherwiseTest extends TestCase {

    public void testOneRuleFiringNoOtherwise() throws Exception {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.RETEOO );

        final Package pkg = new Package( "Miss Manners" );
        final Rule rule1 = getRule( "rule1" );
        pkg.addRule( rule1 );

        final Rule ruleOtherwise = getOtherwise( "rule2" );
        pkg.addRule( ruleOtherwise );

        ruleBase.addPackage( pkg );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        workingMemory.assertObject( new TestBean() );
        workingMemory.fireAllRules();

        assertTrue( ((MockConsequence) rule1.getConsequence()).fired );
        assertFalse( ((MockConsequence) ruleOtherwise.getConsequence()).fired );

    }

    public void testTwoRulesFiringNoOtherwise() throws Exception {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.RETEOO );

        final Package pkg = new Package( "Miss Manners" );
        final Rule rule1 = getRule( "rule1" );
        pkg.addRule( rule1 );
        final Rule rule2 = getRule( "rule2" );
        pkg.addRule( rule2 );

        final Rule ruleOtherwise = getOtherwise( "ruleOtherwise" );
        pkg.addRule( ruleOtherwise );

        ruleBase.addPackage( pkg );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        workingMemory.assertObject( new TestBean() );
        workingMemory.fireAllRules();

        assertFalse( ((MockConsequence) ruleOtherwise.getConsequence()).fired );
        assertTrue( ((MockConsequence) rule1.getConsequence()).fired );
        assertTrue( ((MockConsequence) rule2.getConsequence()).fired );

    }

    public void testOtherwiseFiringWithOneRule() throws Exception {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.RETEOO );

        final Package pkg = new Package( "Miss Manners" );
        final Rule rule1 = getRule( "rule1" );
        pkg.addRule( rule1 );

        final Rule ruleOtherwise = getOtherwise( "rule2" );
        pkg.addRule( ruleOtherwise );

        ruleBase.addPackage( pkg );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        workingMemory.fireAllRules();

        assertFalse( ((MockConsequence) rule1.getConsequence()).fired );
        assertTrue( ((MockConsequence) ruleOtherwise.getConsequence()).fired );

    }

    public void testOtherwiseFiringMultipleRules() throws Exception {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.RETEOO );

        final Package pkg = new Package( "Miss Manners" );
        final Rule rule1 = getRule( "rule1" );
        pkg.addRule( rule1 );
        final Rule rule2 = getRule( "rule2" );
        pkg.addRule( rule2 );

        final Rule ruleOtherwise1 = getOtherwise( "other1" );
        pkg.addRule( ruleOtherwise1 );
        final Rule ruleOtherwise2 = getOtherwise( "other2" );
        pkg.addRule( ruleOtherwise2 );

        ruleBase.addPackage( pkg );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        workingMemory.fireAllRules();

        assertFalse( ((MockConsequence) rule1.getConsequence()).fired );
        assertFalse( ((MockConsequence) rule2.getConsequence()).fired );
        assertTrue( ((MockConsequence) ruleOtherwise1.getConsequence()).fired );
        assertTrue( ((MockConsequence) ruleOtherwise2.getConsequence()).fired );

    }

    private Rule getOtherwise(final String name) {
        final Rule rule = new Rule( name );
        final Pattern pat = new Pattern( 0,
                                       new ClassObjectType( Otherwise.class) );
        rule.addPattern( pat );
        rule.setConsequence( new MockConsequence() );
        return rule;
    }

    private Rule getRule(final String name) {
        final Rule rule = new Rule( name );

        final Pattern pat = new Pattern( 0,
                                       new ClassObjectType( TestBean.class ) );

        rule.addPattern( pat );
        rule.setConsequence( new MockConsequence() );

        return rule;
    }

    static class MockConsequence
        implements
        Consequence {

        public boolean fired = false;

        public void evaluate(final KnowledgeHelper knowledgeHelper,
                             final WorkingMemory workingMemory) throws Exception {
            this.fired = true;
        }

    }

}
