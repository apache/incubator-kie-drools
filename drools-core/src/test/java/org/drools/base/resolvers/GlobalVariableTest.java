package org.drools.base.resolvers;

import java.util.ArrayList;
import java.util.List;

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

public class GlobalVariableTest extends TestCase {
    public void testGlobal() throws Exception {
        final RuleBase rb = RuleBaseFactory.newRuleBase();
        final Package pkg = new Package( "org.test" );
        pkg.addGlobal( "list",
                       List.class );
        rb.addPackage( pkg );
        final WorkingMemory wm = rb.newWorkingMemory();

        final GlobalVariable global = new GlobalVariable( "list",
                                                    List.class );

        final Cheese stilton = new Cheese( "stilton",
                                     20 );
        final FactHandle stiltonHandle = wm.assertObject( stilton );

        final Tuple tuple = new ReteTuple( (DefaultFactHandle) stiltonHandle );

        final List list = new ArrayList();
        wm.setGlobal( "list",
                      list );

        assertEquals( list,
                      global.getValue( tuple,
                                       wm ) );
    }
}
