package org.drools.lang.dsl;

import junit.framework.TestCase;

public class DefaultDSLMappingEntryTest extends TestCase {

    private DSLMappingEntry entry;

    protected void setUp() throws Exception {
        super.setUp();
        final String inputKey = "String is \"{value}\"";
        final String inputValue = "SomeFact(value==\"{value}\")";

        this.entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
                                            null,
                                            inputKey,
                                            inputValue );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPatternCalculation() {
        final String inputKey = "The Customer name is {name} and surname is {surname} and it has US$ 50,00 on his {pocket}";
        final String inputValue = "Customer( name == \"{name}\", surname == \"{surname}\", money > $money )";

        final String expectedKeyP = "The\\s*Customer\\s*name\\s*is\\s*(.*?)\\s*and\\s*surname\\s*is\\s*(.*?)\\s*and\\s*it\\s*has\\s*US\\$\\s*50,00\\s*on\\s*his\\s*(.*?)$";
        final String expectedValP = "Customer( name == \"$1\", surname == \"$2\", money > \\$money )";

        final DSLMappingEntry entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
                                                            null,
                                                            inputKey,
                                                            inputValue );

        assertEquals( inputKey,
                      entry.getMappingKey() );
        assertEquals( expectedKeyP,
                      entry.getKeyPattern().pattern() );
        assertEquals( inputValue,
                      entry.getMappingValue() );
        assertEquals( expectedValP,
                      entry.getValuePattern() );

    }

    public void testPatternCalculation2() {
        final String inputKey = "-name is {name}";
        final String inputValue = "name == \"{name}\"";

        final String expectedKeyP = "-\\s*name\\s*is\\s*(.*?)$";
        final String expectedValP = "name == \"$1\"";

        final DSLMappingEntry entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
                                                            null,
                                                            inputKey,
                                                            inputValue );

        assertEquals( inputKey,
                      entry.getMappingKey() );
        assertEquals( expectedKeyP,
                      entry.getKeyPattern().pattern() );
        assertEquals( inputValue,
                      entry.getMappingValue() );
        assertEquals( expectedValP,
                      entry.getValuePattern() );

    }

    public void testPatternCalculation3() {
        final String inputKey = "- name is {name}";
        final String inputValue = "name == \"{name}\"";

        final String expectedKeyP = "-\\s*name\\s*is\\s*(.*?)$";
        final String expectedValP = "name == \"$1\"";

        final DSLMappingEntry entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
                                                            null,
                                                            inputKey,
                                                            inputValue );

        assertEquals( inputKey,
                      entry.getMappingKey() );
        assertEquals( expectedKeyP,
                      entry.getKeyPattern().pattern() );
        assertEquals( inputValue,
                      entry.getMappingValue() );
        assertEquals( expectedValP,
                      entry.getValuePattern() );
    }

    public void testExpandNoSpaces() {
        final String result = this.entry.getKeyPattern().matcher( "String is \"blah\"" ).replaceAll( this.entry.getValuePattern() );

        assertEquals( "SomeFact(value==\"blah\")",
                      result );
    }

    public void testExpandWithLeadingSpace() {
        final String result = this.entry.getKeyPattern().matcher( "String is \" blah\"" ).replaceAll( this.entry.getValuePattern() );

        assertEquals( "SomeFact(value==\" blah\")",
                      result );
    }

    public void testExpandWithMultipleLeadingSpaces() {
        final String result = this.entry.getKeyPattern().matcher( "String is \"   blah\"" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"   blah\")",
                      result );
    }

    public void testExpandWithTrailingSpace() {
        final String result = this.entry.getKeyPattern().matcher( "String is \"blah \"" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"blah \")",
                      result );
    }

    public void testExpandWithMultipleTrailingSpaces() {
        final String result = this.entry.getKeyPattern().matcher( "String is \"blah  \"" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"blah  \")",
                      result );
    }

    public void testExpandWithInternalSpace() {
        final String result = this.entry.getKeyPattern().matcher( "String is \"bl ah\"" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"bl ah\")",
                      result );
    }

    public void testExpandWithMultipleSpaces() {
        final String result = this.entry.getKeyPattern().matcher( "String is \"  bl  ah  \"" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"  bl  ah  \")",
                      result );
    }
}
