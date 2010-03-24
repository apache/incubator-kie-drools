package org.drools.verifier.components;

import org.drools.verifier.VerifierComponentMockFactory;

import junit.framework.TestCase;

public class LiteralRestrictionTest extends TestCase {

    public void testSetValue() {

        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction booleanRestriction = new LiteralRestriction( pattern1 );
        booleanRestriction.setValue( "true" );

        assertEquals( Field.BOOLEAN,
                      booleanRestriction.getValueType() );
        assertEquals( true,
                      booleanRestriction.getBooleanValue() );

        LiteralRestriction intRestriction = new LiteralRestriction( pattern1 );
        intRestriction.setValue( "1" );

        assertEquals( Field.INT,
                      intRestriction.getValueType() );
        assertEquals( 1,
                      intRestriction.getIntValue() );

        LiteralRestriction doubleRestriction = new LiteralRestriction( pattern1 );
        doubleRestriction.setValue( "1.0" );

        assertEquals( Field.DOUBLE,
                      doubleRestriction.getValueType() );
        assertEquals( 1.0,
                      doubleRestriction.getDoubleValue() );

        LiteralRestriction dateRestriction = new LiteralRestriction( pattern1 );
        dateRestriction.setValue( "11-jan-2008" );

        assertEquals( Field.DATE,
                      dateRestriction.getValueType() );

        LiteralRestriction stringRestriction = new LiteralRestriction( pattern1 );
        stringRestriction.setValue( "test test" );

        assertEquals( Field.STRING,
                      stringRestriction.getValueType() );
        assertEquals( "test test",
                      stringRestriction.getValueAsString() );

        LiteralRestriction nullRestriction = new LiteralRestriction( pattern1 );
        nullRestriction.setValue( null );

        assertEquals( Field.UNKNOWN,
                      nullRestriction.getValueType() );
        assertEquals( null,
                      nullRestriction.getValueAsString() );
        assertEquals( null,
                      nullRestriction.getValueAsObject() );
    }
}
