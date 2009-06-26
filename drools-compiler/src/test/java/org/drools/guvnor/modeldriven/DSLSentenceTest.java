package org.drools.guvnor.modeldriven;

import junit.framework.TestCase;

import org.drools.guvnor.client.modeldriven.brl.DSLSentence;

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

    public void testEnumSentence(){
        final DSLSentence sen = new DSLSentence();
        sen.sentence = "this is {variable:ENUM:Value.test} here and {here}";
        assertEquals( "this is variable here and here",sen.toString() );
    }

    public void testLogColonSentence(){
        final DSLSentence sen = new DSLSentence();
        sen.sentence = "Log : \"{message}\"";
        assertEquals( "Log : \"message\"",sen.toString() );
    }

    public void testWithNewLines() {
    	final DSLSentence sen = new DSLSentence();
        sen.sentence = "this is {variable}\\n here and {here}";
        assertEquals( "this is variable\n here and here",sen.toString() );

    }
}