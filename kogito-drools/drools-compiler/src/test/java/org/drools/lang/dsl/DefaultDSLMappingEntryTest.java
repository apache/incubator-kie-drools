package org.drools.lang.dsl;

import junit.framework.TestCase;

public class DefaultDSLMappingEntryTest extends TestCase {

    private DSLMappingEntry entry;

    protected void setUp() throws Exception {
        super.setUp();
        String inputKey = "String is \"{value}\"";
        String inputValue = "SomeFact(value==\"{value}\")";

        entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
                                                                           null,
                                                                           inputKey,
                                                                           inputValue );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPatternCalculation() {
        String inputKey = "The Customer name is {name} and surname is {surname} and it has US$ 50,00 on his {pocket}";
        String inputValue = "Customer( name == \"{name}\", surname == \"{surname}\", money > $money )";

        String expectedKeyP = "The\\s*Customer\\s*name\\s*is\\s*(.*?)\\s*and\\s*surname\\s*is\\s*(.*?)\\s*and\\s*it\\s*has\\s*US\\$\\s*50,00\\s*on\\s*his\\s*(.*?)$";
        String expectedValP = "Customer( name == \"$1\", surname == \"$2\", money > \\$money )";

        DSLMappingEntry entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
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
        String inputKey = "-name is {name}";
        String inputValue = "name == \"{name}\"";

        String expectedKeyP = "-\\s*name\\s*is\\s*(.*?)$";
        String expectedValP = "name == \"$1\"";

        DSLMappingEntry entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
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
        String inputKey = "- name is {name}";
        String inputValue = "name == \"{name}\"";

        String expectedKeyP = "-\\s*name\\s*is\\s*(.*?)$";
        String expectedValP = "name == \"$1\"";

        DSLMappingEntry entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
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
        String result = entry.getKeyPattern().matcher( "String is \"blah\"" ).replaceAll( entry.getValuePattern() ); 

        assertEquals( "SomeFact(value==\"blah\")",
                      result );
    }

    public void testExpandWithLeadingSpace() {
        String result = entry.getKeyPattern().matcher( "String is \" blah\"" ).replaceAll( entry.getValuePattern() ); 

        assertEquals( "SomeFact(value==\" blah\")",
                      result );
    }

    public void testExpandWithMultipleLeadingSpaces() {
        String result = entry.getKeyPattern().matcher( "String is \"   blah\"" ).replaceAll( entry.getValuePattern() ); 
        assertEquals( "SomeFact(value==\"   blah\")",
                      result );
    }

    public void testExpandWithTrailingSpace() {
        String result = entry.getKeyPattern().matcher( "String is \"blah \"" ).replaceAll( entry.getValuePattern() ); 
        assertEquals( "SomeFact(value==\"blah \")",
                      result );
    }

    public void testExpandWithMultipleTrailingSpaces() {
        String result = entry.getKeyPattern().matcher( "String is \"blah  \"" ).replaceAll( entry.getValuePattern() ); 
        assertEquals( "SomeFact(value==\"blah  \")",
                      result );
    }

    public void testExpandWithInternalSpace() {
        String result = entry.getKeyPattern().matcher( "String is \"bl ah\"" ).replaceAll( entry.getValuePattern() ); 
        assertEquals( "SomeFact(value==\"bl ah\")",
                      result );
    }

    public void testExpandWithMultipleSpaces() {
        String result = entry.getKeyPattern().matcher( "String is \"  bl  ah  \"" ).replaceAll( entry.getValuePattern() ); 
        assertEquals( "SomeFact(value==\"  bl  ah  \")",
                      result );
    }
}
