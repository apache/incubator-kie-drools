package org.drools.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.drools.base.evaluators.MatchesEvaluatorsDefinition;
import org.drools.base.evaluators.SetEvaluatorsDefinition;
import org.drools.compiler.DrlExprParser;
import org.drools.lang.descr.ConstraintConnectiveDescr;
import org.junit.Before;
import org.junit.Test;

public class MVELDumperTest {

    private MVELDumper dumper;

    @Before
    public void setUp() throws Exception {
        // configure operators
        new SetEvaluatorsDefinition();
        new MatchesEvaluatorsDefinition();

        dumper = new MVELDumper();
    }

    @Test
    public void testDump() throws Exception {
        String input = "price > 10 && < 20 || == $val || == 30";
        String expected = "( price > 10 && price < 20 || price == $val || price == 30 )";

        ConstraintConnectiveDescr descr = parse( input );
        String result = dumper.dump( descr );

        assertEquals( expected,
                      result );
    }

    @Test
    public void testDumpMatches() throws Exception {
        String input = "type.toString matches \"something\\swith\\tsingle escapes\"";
        String expected = "type.toString ~= \"something\\swith\\tsingle escapes\"";

        ConstraintConnectiveDescr descr = parse( input );
        String result = dumper.dump( descr );

        assertEquals( expected,
                      result );
    }

    @Test
    public void testDumpMatches2() throws Exception {
        String input = "type.toString matches 'something\\swith\\tsingle escapes'";
        String expected = "type.toString ~= \"something\\swith\\tsingle escapes\"";

        ConstraintConnectiveDescr descr = parse( input );
        String result = dumper.dump( descr );

        System.out.println(expected);
        System.out.println(result);
        assertEquals( expected,
                      result );
    }

    @Test
    public void testDumpMatches3() throws Exception {
        String input = "this[\"content\"] matches \"hello ;=\"";
        String expected = "this[\"content\"] ~= \"hello ;=\"";

        ConstraintConnectiveDescr descr = parse( input );
        String result = dumper.dump( descr );

        assertEquals( expected,
                      result );
    }

    @Test
    public void testDumpWithDateAttr() throws Exception {
        String input = "son.birthDate == \"01-jan-2000\"";
        String expected = "son.birthDate == org.drools.util.DateUtils.parseDate( \"01-jan-2000\" )";

        ConstraintConnectiveDescr descr = parse( input );
        String result = dumper.dump( descr );

        assertEquals( expected,
                      result );
    }

    @Test
    public void testDumpComplex() throws Exception {
        String input = "a ( > 60 && < 70 ) || ( > 50 && < 55 ) && a3 == \"black\" || a == 40 && a3 == \"pink\" || a == 12 && a3 == \"yellow\" || a3 == \"blue\"";
        String expected = "( ( a > 60 && a < 70 || a > 50 && a < 55 ) && a3 == \"black\" || a == 40 && a3 == \"pink\" || a == 12 && a3 == \"yellow\" || a3 == \"blue\" )";

        ConstraintConnectiveDescr descr = parse( input );
        String result = dumper.dump( descr );

        assertEquals( expected,
                      result );
    }

    public ConstraintConnectiveDescr parse( final String constraint ) {
        DrlExprParser parser = new DrlExprParser();
        ConstraintConnectiveDescr result = parser.parse( constraint );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        return result;
    }
}
