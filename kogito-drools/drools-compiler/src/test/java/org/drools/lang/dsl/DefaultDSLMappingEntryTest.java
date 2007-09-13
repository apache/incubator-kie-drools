package org.drools.lang.dsl;

import junit.framework.TestCase;

public class DefaultDSLMappingEntryTest extends TestCase {

    private DSLMappingEntry entry;

    protected void setUp() throws Exception {
        super.setUp();
        setupEntry();
    }

    private void setupEntry() {
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

        final String expectedKeyP = "(\\W|^)The\\s+Customer\\s+name\\s+is\\s+(.*?)\\s+and\\s+surname\\s+is\\s+(.*?)\\s+and\\s+it\\s+has\\s+US\\$\\s+50,00\\s+on\\s+his\\s+(.*?)$";
        final String expectedValP = "Customer( name == \"$2\", surname == \"$3\", money > \\$money )";

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

        final String expectedKeyP = "(\\W|^)-\\s*name\\s+is\\s+(.*?)$";
        final String expectedValP = "name == \"$2\"";

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

        final String expectedKeyP = "(\\W|^)-\\s*name\\s+is\\s+(.*?)$";
        final String expectedValP = "name == \"$2\"";

        final DSLMappingEntry entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
                                                            null,
                                                            inputKey,
                                                            inputValue );

        assertEquals( inputKey,
                      entry.getMappingKey() );
        assertEquals( entry.getKeyPattern().pattern(),
                      expectedKeyP,
                      entry.getKeyPattern().pattern() );
        assertEquals( inputValue,
                      entry.getMappingValue() );
        assertEquals( expectedValP,
                      entry.getValuePattern() );
    }

    public void testExpandNoSpaces() {
        this.setupEntry();
        final String result = this.entry.getKeyPattern().matcher( "String is \"blah\"" ).replaceAll( this.entry.getValuePattern() );

        assertEquals( "SomeFact(value==\"blah\")",
                      result );
    }

    public void testExpandWithLeadingSpace() {
        this.setupEntry();
        final String result = this.entry.getKeyPattern().matcher( "String is \" blah\"" ).replaceAll( this.entry.getValuePattern() );

        assertEquals( "SomeFact(value==\" blah\")",
                      result );
    }

    public void testExpandWithMultipleLeadingSpaces() {
        this.setupEntry();
        final String result = this.entry.getKeyPattern().matcher( "String is \"   blah\"" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"   blah\")",
                      result );
    }

    public void testExpandWithTrailingSpace() {
        this.setupEntry();
        final String result = this.entry.getKeyPattern().matcher( "String is \"blah \"" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"blah \")",
                      result );
    }

    public void testExpandWithMultipleTrailingSpaces() {
        this.setupEntry();
        final String result = this.entry.getKeyPattern().matcher( "String is \"blah  \"" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"blah  \")",
                      result );
    }

    public void testExpandWithInternalSpace() {
        this.setupEntry();
        final String result = this.entry.getKeyPattern().matcher( "String is \"bl ah\"" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"bl ah\")",
                      result );
    }

    public void testExpandWithMultipleSpaces() {
        this.setupEntry();
        final String result = this.entry.getKeyPattern().matcher( "String is \"  bl  ah  \"" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"  bl  ah  \")",
                      result );
    }
    
    public void testExpandWithDots() {
        final String inputKey = "- {prop} is {val} ";
        final String inputValue = "{prop} == {val}";

        this.entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
                                            null,
                                            inputKey,
                                            inputValue );
        
        String result = this.entry.getKeyPattern().matcher( "- type is ClientServiceType.TypeGOLD" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( result,
                      "type == ClientServiceType.TypeGOLD",
                      result );
    }
    
    public void testExpandPartialWords() {
        final String inputKey = "- {prop} is {val} ";
        final String inputValue = "{prop} == {val}";

        this.entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
                                            null,
                                            inputKey,
                                            inputValue );
        // not supposed to expand
        String result = this.entry.getKeyPattern().matcher( "- type is_not ClientServiceType.TypeGOLD" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( result,
                      "- type is_not ClientServiceType.TypeGOLD",
                      result );
    }
    
    public void testExpandPartialWords2() {
        final String inputKey = "- {prop} is_not {val} ";
        final String inputValue = "{prop} != {val}";

        this.entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
                                            null,
                                            inputKey,
                                            inputValue );

        String result = this.entry.getKeyPattern().matcher( "- type is_not ClientServiceType.TypeGOLD" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( result,
                      "type != ClientServiceType.TypeGOLD",
                      result );
    }
    
    public void testExpandPartialWords3() {
        final String inputKey = "- {prop} is not {val} ";
        final String inputValue = "{prop} != {val}";

        this.entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
                                            null,
                                            inputKey,
                                            inputValue );

        String result = this.entry.getKeyPattern().matcher( "- type is not ClientServiceType.TypeGOLD" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( result,
                      "type != ClientServiceType.TypeGOLD",
                      result );
    }
    
    public void testExpandWithBrackets() {
        final String inputKey = "attr {attr_name} is in \\[ {values} \\]";
        final String inputValue = "{attr_name} in ( {values} )";

        this.entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
                                            null,
                                            inputKey,
                                            inputValue );

        String result = this.entry.getKeyPattern().matcher( "attr name is in [ 'Edson', 'Bob' ]" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( result,
                      "name in ( 'Edson', 'Bob' )",
                      result );
    }
    
    public void testExpandWithParethesis() {
        final String inputKey = "((H|h)e|(S|s)he) \\(is\\) (a|an) $xx {attribute} (man|woman)";
        final String inputValue = "Person( attribute == \"{attribute}\" )";

        this.entry = new DefaultDSLMappingEntry( DSLMappingEntry.CONDITION,
                                            null,
                                            inputKey,
                                            inputValue );

        String result = this.entry.getKeyPattern().matcher( "he (is) a $xx handsome man" ).replaceAll( this.entry.getValuePattern() );
        assertEquals( result,
                      "Person( attribute == \"handsome\" )",
                      result );
    }
    
}
