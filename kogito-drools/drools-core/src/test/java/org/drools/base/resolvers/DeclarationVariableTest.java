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
import org.drools.spi.Tuple;

public class DeclarationVariableTest extends TestCase {
    public void testDeclaration() throws Exception {
        final RuleBase rb = RuleBaseFactory.newRuleBase();
        final Package pkg = new Package( "org.test" );
        rb.addPackage( pkg );
        final WorkingMemory wm = rb.newWorkingMemory();

        final Column column = new Column( 0,
                                    new ClassObjectType( Cheese.class ),
                                    "stilton" );
        final DeclarationVariable declaration = new DeclarationVariable( column.getDeclaration() );

        final Cheese stilton = new Cheese( "stilton",
                                     20 );
        final FactHandle stiltonHandle = wm.assertObject( stilton );

        final Tuple tuple = new ReteTuple( (DefaultFactHandle) stiltonHandle );
        assertEquals( stilton,
                      declaration.getValue( tuple,
                                            wm ) );
    }
}
