package org.drools.verifier.components;

import org.drools.verifier.VerifierComponentMockFactory;

import junit.framework.TestCase;

public class LiteralRestrictionTest extends TestCase {

    public void testSetValue() {

        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction literalBooleanRestriction = LiteralRestriction.createRestriction( pattern1,
                                                                                             "true" );

        assertTrue( literalBooleanRestriction instanceof BooleanRestriction );

        BooleanRestriction booleanRestriction = (BooleanRestriction) literalBooleanRestriction;

        assertEquals( Field.BOOLEAN,
                      booleanRestriction.getValueType() );
        assertEquals( true,
                      booleanRestriction.getValue() );

        LiteralRestriction intLiteralRestriction = LiteralRestriction.createRestriction( pattern1,
                                                                                         "1" );
        assertTrue( intLiteralRestriction instanceof NumberRestriction );
        NumberRestriction intRestriction = (NumberRestriction) intLiteralRestriction;

        assertTrue( intRestriction.isInt() );
        assertEquals( Field.INT,
                      intRestriction.getValueType() );
        assertEquals( 1,
                      intRestriction.getValue() );

        LiteralRestriction doubleLiteralRestriction = LiteralRestriction.createRestriction( pattern1,
                                                                                            "1.0" );
        assertTrue( doubleLiteralRestriction instanceof NumberRestriction );

        NumberRestriction doubleRestriction = (NumberRestriction) doubleLiteralRestriction;

        assertEquals( Field.DOUBLE,
                      doubleRestriction.getValueType() );
        assertEquals( 1.0,
                      doubleRestriction.getValue() );

        LiteralRestriction dateLiteralRestriction = LiteralRestriction.createRestriction( pattern1,
                                                                                          "11-jan-2008" );

        assertTrue( dateLiteralRestriction instanceof DateRestriction );

        DateRestriction dateRestriction = (DateRestriction) dateLiteralRestriction;

        assertEquals( Field.DATE,
                      dateRestriction.getValueType() );

        LiteralRestriction stringRestriction = LiteralRestriction.createRestriction( pattern1,
                                                                                     "test test" );

        assertEquals( Field.STRING,
                      stringRestriction.getValueType() );
        assertEquals( "test test",
                      stringRestriction.getValueAsString() );

        LiteralRestriction nullRestriction = LiteralRestriction.createRestriction( pattern1,
                                                                                   null );

        assertTrue( nullRestriction instanceof StringRestriction );

        assertEquals( Field.UNKNOWN,
                      nullRestriction.getValueType() );
        assertEquals( "",
                      nullRestriction.getValueAsString() );
    }
}
