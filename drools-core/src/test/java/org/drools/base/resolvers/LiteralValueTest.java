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
import org.drools.spi.Tuple;

public class LiteralValueTest extends TestCase {
    public void testLiteral() throws Exception {
        final RuleBase rb = RuleBaseFactory.newRuleBase();
        final Package pkg = new Package( "org.test" );
        rb.addPackage( pkg );
        final WorkingMemory wm = rb.newWorkingMemory();

        final LiteralValue literal = new LiteralValue( "literal" );

        final Cheese stilton = new Cheese( "stilton",
                                     20 );
        final FactHandle stiltonHandle = wm.assertObject( stilton );

        final Tuple tuple = new ReteTuple( (DefaultFactHandle) stiltonHandle );
        assertEquals( "literal",
                      literal.getValue( tuple,
                                        wm ) );
    }
}
