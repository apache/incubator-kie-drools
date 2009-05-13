package org.drools.lang.dsl;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

public class DSLMappingEntryTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private DSLMappingEntry createEntry(final String inputKey,
                                        final String inputValue) throws IOException {
        String mapping = "[condition][]" + inputKey + "=" + inputValue;
        StringReader dsl = new StringReader( mapping );
        DSLMappingEntry entry = null;
        try {
            DSLTokenizedMappingFile parser = new DSLTokenizedMappingFile();
            if ( parser.parseAndLoad( dsl ) ) {
                entry = parser.getMapping().getEntries().get( 0 );
            } else {
                throw new RuntimeException( "Error parsing entry: " + mapping + ": " + parser.getErrors().toString() );
            }
        } finally {
            dsl.close();
        }

        return entry;
    }

    public void testPatternCalculation() throws IOException {
        final String inputKey = "The Customer name is {name} and surname is {surname} and it has US$ 50,00 on his {pocket}";
        final String inputValue = "Customer( name == \"{name}\", surname == \"{surname}\", money > $money )";

        final String expectedKeyP = "(\\W|^)The\\s+Customer\\s+name\\s+is\\s+(.*?)\\s+and\\s+surname\\s+is\\s+(.*?)\\s+and\\s+it\\s+has\\s+US\\$\\s+50,00\\s+on\\s+his\\s+(.*?)$";
        final String expectedValP = "$1Customer( name == \"$2\", surname == \"$3\", money > \\$money )";

        final DSLMappingEntry entry = createEntry( inputKey,
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

    public void testPatternCalculation2() throws IOException {
        final String inputKey = "-name is {name}";
        final String inputValue = "name == \"{name}\"";

        final String expectedKeyP = "(\\W|^)-\\s*name\\s+is\\s+(.*?)$";
        final String expectedValP = "$1name == \"$2\"";

        final DSLMappingEntry entry = createEntry( inputKey,
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

    public void testPatternCalculation3() throws IOException {
        final String inputKey = "- name is {name}";
        final String inputValue = "name == \"{name}\"";

        final String expectedKeyP = "(\\W|^)-\\s*name\\s+is\\s+(.*?)$";
        final String expectedValP = "$1name == \"$2\"";

        final DSLMappingEntry entry = createEntry( inputKey,
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

    private DSLMappingEntry setupEntry() throws IOException {
        final String inputKey = "String is \"{value}\"";
        final String inputValue = "SomeFact(value==\"{value}\")";

        return createEntry( inputKey,
                            inputValue );
    }

    public void testExpandNoSpaces() throws IOException {
        DSLMappingEntry entry = this.setupEntry();
        final String result = entry.getKeyPattern().matcher( "String is \"blah\"" ).replaceAll( entry.getValuePattern() );

        assertEquals( "SomeFact(value==\"blah\")",
                      result );
    }

    public void testExpandWithLeadingSpace() throws IOException {
        DSLMappingEntry entry = this.setupEntry();
        final String result = entry.getKeyPattern().matcher( "String is \" blah\"" ).replaceAll( entry.getValuePattern() );

        assertEquals( "SomeFact(value==\" blah\")",
                      result );
    }

    public void testExpandWithMultipleLeadingSpaces() throws IOException {
        DSLMappingEntry entry = this.setupEntry();
        final String result = entry.getKeyPattern().matcher( "String is \"   blah\"" ).replaceAll( entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"   blah\")",
                      result );
    }

    public void testExpandWithTrailingSpace() throws IOException {
        DSLMappingEntry entry = this.setupEntry();
        final String result = entry.getKeyPattern().matcher( "String is \"blah \"" ).replaceAll( entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"blah \")",
                      result );
    }

    public void testExpandWithMultipleTrailingSpaces() throws IOException {
        DSLMappingEntry entry = this.setupEntry();
        final String result = entry.getKeyPattern().matcher( "String is \"blah  \"" ).replaceAll( entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"blah  \")",
                      result );
    }

    public void testExpandWithInternalSpace() throws IOException {
        DSLMappingEntry entry = this.setupEntry();
        final String result = entry.getKeyPattern().matcher( "String is \"bl ah\"" ).replaceAll( entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"bl ah\")",
                      result );
    }

    public void testExpandWithMultipleSpaces() throws IOException {
        DSLMappingEntry entry = this.setupEntry();
        final String result = entry.getKeyPattern().matcher( "String is \"  bl  ah  \"" ).replaceAll( entry.getValuePattern() );
        assertEquals( "SomeFact(value==\"  bl  ah  \")",
                      result );
    }

    public void testExpandWithDots() throws IOException {
        final String inputKey = "- {prop} is {val} ";
        final String inputValue = "{prop} == {val}";

        DSLMappingEntry entry = createEntry( inputKey,
                                  inputValue );

        String result = entry.getKeyPattern().matcher( "- type is ClientServiceType.TypeGOLD" ).replaceAll( entry.getValuePattern() );
        assertEquals( result,
                      "type == ClientServiceType.TypeGOLD",
                      result );
    }

    public void testExpandPartialWords() throws IOException {
        final String inputKey = "- {prop} is {val} ";
        final String inputValue = "{prop} == {val}";

        DSLMappingEntry entry = createEntry( inputKey,
                                  inputValue );
        // not supposed to expand
        String result = entry.getKeyPattern().matcher( "- type is_not ClientServiceType.TypeGOLD" ).replaceAll( entry.getValuePattern() );
        assertEquals( result,
                      "- type is_not ClientServiceType.TypeGOLD",
                      result );
    }

    public void testExpandPartialWords2() throws IOException {
        final String inputKey = "- {prop} is_not {val} ";
        final String inputValue = "{prop} != {val}";

        DSLMappingEntry entry = createEntry( inputKey,
                                  inputValue );

        String result = entry.getKeyPattern().matcher( "- type is_not ClientServiceType.TypeGOLD" ).replaceAll( entry.getValuePattern() );
        assertEquals( result,
                      "type != ClientServiceType.TypeGOLD",
                      result );
    }

    public void testExpandPartialWords3() throws IOException {
        final String inputKey = "- {prop} is not {val} ";
        final String inputValue = "{prop} != {val}";

        DSLMappingEntry entry = createEntry( inputKey,
                                  inputValue );

        String result = entry.getKeyPattern().matcher( "- type is not ClientServiceType.TypeGOLD" ).replaceAll( entry.getValuePattern() );
        assertEquals( result,
                      "type != ClientServiceType.TypeGOLD",
                      result );
    }

    public void testExpandWithBrackets() throws IOException {
        final String inputKey = "attr {attr_name} is in \\[ {values} \\]";
        final String inputValue = "{attr_name} in ( {values} )";

        DSLMappingEntry entry = createEntry( inputKey,
                                  inputValue );

        String result = entry.getKeyPattern().matcher( "attr name is in [ 'Edson', 'Bob' ]" ).replaceAll( entry.getValuePattern() );
        assertEquals( result,
                      "name in ( 'Edson', 'Bob' )",
                      result );
    }

    public void testExpandWithParethesis() throws IOException {
        final String inputKey = "((H|h)e|(S|s)he) \\(is\\) (a|an) $xx {attribute} (man|woman)";
        final String inputValue = "Person( attribute == \"{attribute}\" )";

        DSLMappingEntry entry = createEntry( inputKey,
                                  inputValue );

        String result = entry.getKeyPattern().matcher( "he (is) a $xx handsome man" ).replaceAll( entry.getValuePattern() );

        assertEquals( result,
                      "Person( attribute == \"handsome\" )",
                      result );
    }

    public void testSingleCharacterBetweenVars() throws IOException {
        final String inputKey = "DSL sentence with {key1} {key2}";
        final String inputValue = "Sentence( {key1} == {key2} )";

        DSLMappingEntry entry = createEntry( inputKey,
                                  inputValue );

        String result = entry.getKeyPattern().matcher( "DSL sentence with mykey myvalue" ).replaceAll( entry.getValuePattern() );
        assertEquals( result,
                      "Sentence( mykey == myvalue )",
                      result );
    }

    public void testExpandWithQualifiedVars() throws IOException {
        final String inputKey = "When the credit rating is {rating:ENUM:Applicant.creditRating}";
        final String inputValue = "applicant:Applicant(credit=={rating})";

        DSLMappingEntry entry = createEntry( inputKey,
                                  inputValue );

        String result = entry.getKeyPattern().matcher( "When the credit rating is AA" ).replaceAll( entry.getValuePattern() );

        assertEquals( result,
                      "applicant:Applicant(credit==AA)",
                      result );
    }

    
    public void testExpandWithRegexp() throws IOException {
        final String inputKey = "When the credit rating is {rating:regexp:\\d{3}}";
        final String inputValue = "applicant:Applicant(credit=={rating})";

        DSLMappingEntry entry = createEntry( inputKey,
                                  inputValue );

        assertEquals( "(\\W|^)When\\s+the\\s+credit\\s+rating\\s+is\\s+(\\d{3})(\\W|$)",
                      entry.getKeyPattern().toString() );
        assertEquals( "$1applicant:Applicant(credit==$2)$3",
                      entry.getValuePattern());
        
        
        String result = entry.getKeyPattern().matcher( "When the credit rating is 555" ).replaceAll( entry.getValuePattern() );

        assertEquals( result,
                      "applicant:Applicant(credit==555)",
                      result );
    }

    public void testExpandWithLineBreaks() throws IOException {
        final String inputKey = "This is a sentence with line breaks";
        final String inputValue = "Cheese\\n(price == 10)";

        DSLMappingEntry entry = createEntry( inputKey,
                                  inputValue );

        assertEquals( "(\\W|^)This\\s+is\\s+a\\s+sentence\\s+with\\s+line\\s+breaks(\\W|$)",
                      entry.getKeyPattern().toString() );
        assertEquals( "$1Cheese\n(price == 10)$2",
                      entry.getValuePattern());
        
        
        String result = entry.getKeyPattern().matcher( "This is a sentence with line breaks" ).replaceAll( entry.getValuePattern() );

        assertEquals( result,
                      "Cheese\n(price == 10)",
                      result );
    }
    
    public void testExpandWithPound() throws IOException {
        final String inputKey = "Bedingung-\\#19-MKM4";
        final String inputValue = "eval ( $p.getTempVal(\"\\#UML-ATZ-1\") < $p.getZvUmlStfr() )";

        DSLMappingEntry entry = createEntry( inputKey,
                                  inputValue );

        assertEquals( "(\\W|^)Bedingung-#19-MKM4(\\W|$)",
                      entry.getKeyPattern().toString() );
        assertEquals( "$1eval ( \\$p.getTempVal(\"#UML-ATZ-1\") < \\$p.getZvUmlStfr() )$2",
                      entry.getValuePattern());
        
        
        String result = entry.getKeyPattern().matcher( "Bedingung-#19-MKM4" ).replaceAll( entry.getValuePattern() );

        assertEquals( result,
                      "eval ( $p.getTempVal(\"#UML-ATZ-1\") < $p.getZvUmlStfr() )",
                      result );
    }
    
}
