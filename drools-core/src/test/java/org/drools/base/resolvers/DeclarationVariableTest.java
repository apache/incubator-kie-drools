package org.drools.base.resolvers;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.common.DefaultFactHandle;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Column;
import org.drools.rule.Package;

public class DeclarationVariableTest extends TestCase {
    public void testDeclaration() throws Exception {
        RuleBase rb = RuleBaseFactory.newRuleBase();
        Package pkg = new Package( "org.test" );
        rb.addPackage( pkg );
        WorkingMemory wm = rb.newWorkingMemory();

        Column column = new Column( 0,
                                    new ClassObjectType( Cheese.class ),
                                    "stilton" );
        DeclarationVariable declaration = new DeclarationVariable( column.getDeclaration() );

        Cheese stilton = new Cheese( "stilton",
                                     20 );
        FactHandle stiltonHandle = wm.assertObject( stilton );

        ReteTuple tuple = new ReteTuple( (DefaultFactHandle) stiltonHandle );
        assertEquals( stilton,
                      declaration.getValue( tuple,
                                            wm ) );
    }
}
