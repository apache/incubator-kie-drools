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

public class GlobalVariableTest extends TestCase {
    public void testGlobal() throws Exception {
        RuleBase rb = RuleBaseFactory.newRuleBase();
        Package pkg = new Package( "org.test" );
        pkg.addGlobal( "list",
                       List.class );
        rb.addPackage( pkg );
        WorkingMemory wm = rb.newWorkingMemory();

        GlobalVariable global = new GlobalVariable( "list", List.class );

        Cheese stilton = new Cheese( "stilton",
                                     20 );
        FactHandle stiltonHandle = wm.assertObject( stilton );

        ReteTuple tuple = new ReteTuple( (DefaultFactHandle) stiltonHandle );

        List list = new ArrayList();
        wm.setGlobal( "list",
                      list );

        assertEquals( list,
                      global.getValue( tuple,
                                       wm ) );
    }
}
