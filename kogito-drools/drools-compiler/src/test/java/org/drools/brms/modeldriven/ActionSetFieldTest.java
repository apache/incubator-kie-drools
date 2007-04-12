package org.drools.brms.modeldriven;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.brxml.ActionFieldValue;
import org.drools.brms.client.modeldriven.brxml.ActionSetField;

public class ActionSetFieldTest extends TestCase {

    public void testRemove() {
        final ActionSetField set = new ActionSetField();
        set.fieldValues = new ActionFieldValue[2];
        final ActionFieldValue v0 = new ActionFieldValue( "x",
                                                    "42" );
        final ActionFieldValue v1 = new ActionFieldValue( "y",
                                                    "43" );
        set.fieldValues[0] = v0;
        set.fieldValues[1] = v1;

        set.removeField( 1 );

        assertEquals( 1,
                      set.fieldValues.length );
        assertEquals( v0,
                      set.fieldValues[0] );

    }

    public void testAdd() {
        final ActionSetField set = new ActionSetField();
        set.fieldValues = new ActionFieldValue[2];
        final ActionFieldValue v0 = new ActionFieldValue( "x",
                                                    "42" );
        final ActionFieldValue v1 = new ActionFieldValue( "y",
                                                    "43" );
        set.fieldValues[0] = v0;
        set.fieldValues[1] = v1;

        final ActionFieldValue q = new ActionFieldValue( "q",
                                                   "q" );
        set.addFieldValue( q );

        assertEquals( 3,
                      set.fieldValues.length );
        assertEquals( q,
                      set.fieldValues[2] );
        assertEquals( v0,
                      set.fieldValues[0] );

    }

}
