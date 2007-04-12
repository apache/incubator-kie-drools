package org.drools.brms.modeldriven;

import org.drools.brms.client.modeldriven.brxml.DSLSentence;

import junit.framework.TestCase;

public class DSLSentenceTest extends TestCase {

    public void testSentence() {

        final DSLSentence sen = new DSLSentence();
        sen.sentence = "this is {something} here and {here}";
        assertEquals( "this is something here and here",
                      sen.toString() );

        sen.sentence = "foo bar";
        assertEquals( "foo bar",
                      sen.toString() );

        final DSLSentence newOne = sen.copy();
        assertFalse( newOne == sen );
        assertEquals( newOne.sentence,
                      sen.sentence );
    }

}
