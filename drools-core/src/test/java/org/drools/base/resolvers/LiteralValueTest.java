package org.drools.base.resolvers;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.common.DefaultFactHandle;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Package;

public class LiteralValueTest extends TestCase {
    public void testLiteral() throws Exception {
        RuleBase rb = RuleBaseFactory.newRuleBase();
        Package pkg = new Package( "org.test" );
        rb.addPackage( pkg );
        WorkingMemory wm = rb.newWorkingMemory();

        LiteralValue literal = new LiteralValue( "literal", String.class );

        Cheese stilton = new Cheese( "stilton",
                                     20 );
        FactHandle stiltonHandle = wm.assertObject( stilton );

        ReteTuple tuple = new ReteTuple( (DefaultFactHandle) stiltonHandle );
        assertEquals( "literal",
                      literal.getValue( tuple,
                                        wm ) );
    }
}
