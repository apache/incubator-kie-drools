package org.drools.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.drools.base.evaluators.MatchesEvaluatorsDefinition;
import org.drools.base.evaluators.SetEvaluatorsDefinition;
import org.drools.compiler.DrlExprParser;
import org.drools.lang.MVELDumper.MVELDumperContext;
import org.drools.lang.descr.BindingDescr;
import org.drools.lang.descr.ConstraintConnectiveDescr;
import org.junit.Before;
import org.junit.Ignore;
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
    public void testDumpContains() throws Exception {
        String input = "list contains \"b\"";
        String expected = "list contains \"b\"";

        ConstraintConnectiveDescr descr = parse( input );
        String result = dumper.dump( descr );

        assertEquals( expected,
                      result );
    }

    @Test
    public void testDumpContains2() throws Exception {
        String input = "list not contains \"b\"";
        String expected = "!( list contains \"b\" )";

        ConstraintConnectiveDescr descr = parse( input );
        String result = dumper.dump( descr );

        assertEquals( expected,
                      result );
    }

    @Test
    public void testDumpExcludes() throws Exception {
        String input = "list excludes \"b\"";
        String expected = "!( list contains \"b\" )";

        ConstraintConnectiveDescr descr = parse( input );
        String result = dumper.dump( descr );

        assertEquals( expected,
                      result );
    }

    @Test
    public void testDumpExcludes2() throws Exception {
        String input = "list not excludes \"b\"";
        String expected = "list contains \"b\"";

        ConstraintConnectiveDescr descr = parse( input );
        String result = dumper.dump( descr );

        assertEquals( expected,
                      result );
    }

    @Test
    @Ignore
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

    @Test
    public void testDumpBindings() throws Exception {
        String input = "$x : property > value";
        String expected = "property > value";

        ConstraintConnectiveDescr descr = parse( input );
        MVELDumperContext ctx = new MVELDumperContext();
        String result = dumper.dump( descr,
                                     ctx );

        assertEquals( expected,
                      result );
        assertEquals( 1,
                      ctx.getBindings().size() );
        BindingDescr bind = ctx.getBindings().get( 0 );
        assertEquals( "$x",
                      bind.getVariable() );
        assertEquals( "property",
                      bind.getExpression() );
    }

    @Test
    public void testDumpBindings2() throws Exception {
        String input = "( $a : a > $b : b[10].prop || 10 != 20 ) && $x : someMethod(10) == 20";
        String expected = "( a > b[10].prop || 10 != 20 ) && someMethod(10) == 20";

        ConstraintConnectiveDescr descr = parse( input );
        MVELDumperContext ctx = new MVELDumperContext();
        String result = dumper.dump( descr, 
                                     ctx );

        assertEquals( expected,
                      result );
        assertEquals( 3,
                      ctx.getBindings().size() );
        BindingDescr bind = ctx.getBindings().get( 0 );
        assertEquals( "$a",
                      bind.getVariable() );
        assertEquals( "a",
                      bind.getExpression() );
        bind = ctx.getBindings().get( 1 );
        assertEquals( "$b",
                      bind.getVariable() );
        assertEquals( "b[10].prop",
                      bind.getExpression() );
        bind = ctx.getBindings().get( 2 );
        assertEquals( "$x",
                      bind.getVariable() );
        assertEquals( "someMethod(10)",
                      bind.getExpression() );
    }

    @Test
    public void testDumpBindings3() throws Exception {
        String input = "( $a : a > $b : b[10].prop || 10 != 20 ) && $x : someMethod(10)";
        String expected = "( a > b[10].prop || 10 != 20 ) && true";

        ConstraintConnectiveDescr descr = parse( input );
        String result = dumper.dump( descr );

        assertEquals( expected,
                      result );
    }

    @Test
    public void testDumpBindings4() throws Exception {
        String input = "( $a : a > $b : b[10].prop || $x : someMethod(10) ) && 10 != 20";
        String expected = "( a > b[10].prop || true ) && 10 != 20";

        ConstraintConnectiveDescr descr = parse( input );
        String result = dumper.dump( descr );

        assertEquals( expected,
                      result );
    }

    @Test
    public void testDumpBindingsWithRestriction() throws Exception {
        String input = "$x : age > 10 && < 20 || > 30";
        String expected = "( age > 10 && age < 20 || age > 30 )";

        ConstraintConnectiveDescr descr = parse( input );
        MVELDumperContext ctx = new MVELDumperContext();
        String result = dumper.dump( descr,
                                     ctx );

        assertEquals( expected,
                      result );
        assertEquals( 1,
                      ctx.getBindings().size() );
        BindingDescr bind = ctx.getBindings().get( 0 );
        assertEquals( "$x",
                      bind.getVariable() );
        assertEquals( "age",
                      bind.getExpression() );
    }

    public ConstraintConnectiveDescr parse( final String constraint ) {
        DrlExprParser parser = new DrlExprParser();
        ConstraintConnectiveDescr result = parser.parse( constraint );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        return result;
    }
}
