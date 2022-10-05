package org.drools.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import junit.framework.TestCase;
import org.assertj.core.api.Assertions;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.FunctionImportDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.OrDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This test class is being ported from org.drools.mvel.compiler.lang.RuleParserTest
 */
public class MiscDRLParserTest extends TestCase {

    private DRLParserWrapper parser;

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        parser = new DRLParserWrapper();
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private String readResource(final String filename) throws Exception {
        Path path = Paths.get(getClass().getResource(filename).toURI());
        final StringBuilder sb = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            for (String line; (line = br.readLine()) != null; ) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Test
    public void testPackage() throws Exception {
        final String source = "package foo.bar.baz";
        final PackageDescr pkg = parser.parse(source);
        assertEquals("foo.bar.baz", pkg.getName());
    }

    @Test
    public void testPackageWithErrorNode() throws Exception {
        final String source = "package 12 foo.bar.baz";
        final PackageDescr pkg = parser.parse(source);
        assertTrue(parser.hasErrors());
        assertEquals("foo.bar.baz", pkg.getName());
    }

    @Test
    public void testPackageWithAllErrorNode() throws Exception {
        final String source = "package 12 12312 231";
        final PackageDescr pkg = parser.parse(source);
        assertTrue(parser.hasErrors());
        assertEquals("", pkg.getName());
    }

    @Test
    public void testCompilationUnit() throws Exception {
        final String source = "package foo; import com.foo.Bar; import com.foo.Baz;";
        PackageDescr pkg = parser.parse(source);
        assertFalse(parser.getErrors().toString(),
                    parser.hasErrors());
        assertEquals("foo",
                     pkg.getName());
        assertEquals(2,
                     pkg.getImports().size());
        ImportDescr impdescr = pkg.getImports().get(0);
        assertEquals("com.foo.Bar",
                     impdescr.getTarget());
        assertEquals(source.indexOf("import " + impdescr.getTarget()),
                     impdescr.getStartCharacter());
        assertEquals(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length(),
                     impdescr.getEndCharacter());

        impdescr = pkg.getImports().get(1);
        assertEquals("com.foo.Baz",
                     impdescr.getTarget());
        assertEquals(source.indexOf("import " + impdescr.getTarget()),
                     impdescr.getStartCharacter());
        assertEquals(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length(),
                     impdescr.getEndCharacter());
    }

    @Test
    public void testFunctionImport() throws Exception {
        final String source = "package foo\n" +
                "import function java.lang.Math.max\n" +
                "import function java.lang.Math.min;\n" +
                "import foo.bar.*\n" +
                "import baz.Baz";
        PackageDescr pkg = parser.parse(source);
        assertFalse(parser.getErrors().toString(),
                    parser.hasErrors());
        assertEquals("foo",
                     pkg.getName());
        assertEquals(2,
                     pkg.getImports().size());
        ImportDescr impdescr = pkg.getImports().get(0);
        assertEquals("foo.bar.*",
                     impdescr.getTarget());
        assertEquals(source.indexOf("import " + impdescr.getTarget()),
                     impdescr.getStartCharacter());

        assertEquals(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length() - 1,
                     impdescr.getEndCharacter());

        impdescr = pkg.getImports().get(1);
        assertEquals("baz.Baz",
                     impdescr.getTarget());
        assertEquals(source.indexOf("import " + impdescr.getTarget()),
                     impdescr.getStartCharacter());
        assertEquals(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length() - 1,
                     impdescr.getEndCharacter());

        assertEquals(2,
                     pkg.getFunctionImports().size());
        impdescr = pkg.getFunctionImports().get(0);
        assertEquals("java.lang.Math.max",
                     impdescr.getTarget());
        assertEquals(source.indexOf("import function " + impdescr.getTarget()),
                     impdescr.getStartCharacter());
        assertEquals(source.indexOf("import function " + impdescr.getTarget()) + ("import function " + impdescr.getTarget()).length() - 1,
                     impdescr.getEndCharacter());

        impdescr = pkg.getFunctionImports().get(1);
        assertEquals("java.lang.Math.min",
                     impdescr.getTarget());
        assertEquals(source.indexOf("import function " + impdescr.getTarget()),
                     impdescr.getStartCharacter());
        assertEquals(source.indexOf("import function " + impdescr.getTarget()) + ("import function " + impdescr.getTarget()).length(),
                     impdescr.getEndCharacter());
    }

    @Test
    public void testGlobalWithComplexType() throws Exception {
        final String source = "package foo.bar.baz\n" +
                "import com.foo.Bar\n" +
                "global java.util.List<java.util.Map<String,Integer>> aList;\n" +
                "global Integer aNumber";
        PackageDescr pkg = parser.parse(source);
        assertFalse(parser.getErrors().toString(),
                    parser.hasErrors());
        assertEquals("foo.bar.baz",
                     pkg.getName());
        assertEquals(1,
                     pkg.getImports().size());

        ImportDescr impdescr = pkg.getImports().get(0);
        assertEquals("com.foo.Bar",
                     impdescr.getTarget());
        assertEquals(source.indexOf("import " + impdescr.getTarget()),
                     impdescr.getStartCharacter());
        assertEquals(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length() - 1,
                     impdescr.getEndCharacter());

        assertEquals(2,
                     pkg.getGlobals().size());

        GlobalDescr global = pkg.getGlobals().get(0);
        assertEquals("java.util.List<java.util.Map<String,Integer>>",
                     global.getType());
        assertEquals("aList",
                     global.getIdentifier());
        assertEquals(source.indexOf("global " + global.getType()),
                     global.getStartCharacter());
        assertEquals(source.indexOf("global " + global.getType() + " " + global.getIdentifier()) +
                             ("global " + global.getType() + " " + global.getIdentifier()).length(),
                     global.getEndCharacter());

        global = pkg.getGlobals().get(1);
        assertEquals("Integer",
                     global.getType());
        assertEquals("aNumber",
                     global.getIdentifier());
        assertEquals(source.indexOf("global " + global.getType()),
                     global.getStartCharacter());
        assertEquals(source.indexOf("global " + global.getType() + " " + global.getIdentifier()) +
                             ("global " + global.getType() + " " + global.getIdentifier()).length() - 1,
                     global.getEndCharacter());
    }

    @Test
    public void testGlobalWithOrWithoutSemi() throws Exception {
        String source = readResource("globals.drl");
        PackageDescr pkg = parser.parse(source);

        assertEquals(1,
                     pkg.getRules().size());

        final RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertEquals(1,
                     rule.getLhs().getDescrs().size());

        assertEquals(1,
                     pkg.getImports().size());
        assertEquals(2,
                     pkg.getGlobals().size());

        final GlobalDescr foo = (GlobalDescr) pkg.getGlobals().get(0);
        assertEquals("java.lang.String",
                     foo.getType());
        assertEquals("foo",
                     foo.getIdentifier());
        final GlobalDescr bar = (GlobalDescr) pkg.getGlobals().get(1);
        assertEquals("java.lang.Integer",
                     bar.getType());
        assertEquals("bar",
                     bar.getIdentifier());
    }

    @Test
    public void testFunctionImportWithNotExist() throws Exception {
        String source = readResource("test_FunctionImport.drl");
        PackageDescr pkg = parser.parse(source);

        assertEquals(2,
                     pkg.getFunctionImports().size());

        assertEquals("abd.def.x",
                     ((FunctionImportDescr) pkg.getFunctionImports().get(0)).getTarget());
        assertFalse(((FunctionImportDescr) pkg.getFunctionImports().get(0)).getStartCharacter() == -1);
        assertFalse(((FunctionImportDescr) pkg.getFunctionImports().get(0)).getEndCharacter() == -1);
        assertEquals("qed.wah.*",
                     ((FunctionImportDescr) pkg.getFunctionImports().get(1)).getTarget());
        assertFalse(((FunctionImportDescr) pkg.getFunctionImports().get(1)).getStartCharacter() == -1);
        assertFalse(((FunctionImportDescr) pkg.getFunctionImports().get(1)).getEndCharacter() == -1);
    }

    @Test
    public void testFromComplexAcessor() throws Exception {
        String source = "rule \"Invalid customer id\" ruleflow-group \"validate\" lock-on-active true \n" +
                " when \n" +
                "     o: Order( ) \n" +
                "     not( Customer( ) from customerService.getCustomer(o.getCustomerId()) ) \n" +
                " then \n" +
                "     System.err.println(\"Invalid customer id found!\"); " +
                "\n" +
                "     o.addError(\"Invalid customer id\"); \n" +
                "end \n";
        PackageDescr pkg = parser.parse(source);

        assertFalse(parser.getErrorMessages().toString(),
                    parser.hasErrors());

        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertEquals("Invalid customer id",
                     rule.getName());

        assertEquals(2,
                     rule.getLhs().getDescrs().size());

        NotDescr not = (NotDescr) rule.getLhs().getDescrs().get(1);
        PatternDescr customer = (PatternDescr) not.getDescrs().get(0);

        assertEquals("Customer",
                     customer.getObjectType());
        assertEquals("customerService.getCustomer(o.getCustomerId())",
                     ((FromDescr) customer.getSource()).getDataSource().getText());
    }

    @Test
    public void testFromWithInlineList() throws Exception {
        String source = "rule XYZ \n" +
                " when \n" +
                " o: Order( ) \n" +
                " not( Number( ) from [1, 2, 3] ) \n" +
                " then \n" +
                " System.err.println(\"Invalid customer id found!\"); \n" +
                " o.addError(\"Invalid customer id\"); \n" +
                "end \n";
        PackageDescr pkg = parser.parse(source);
        assertFalse(parser.getErrors().toString(),
                    parser.hasErrors());

        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertEquals("XYZ",
                     rule.getName());

        PatternDescr number = (PatternDescr) ((NotDescr) rule.getLhs().getDescrs().get(1)).getDescrs().get(0);
        assertThat(((FromDescr) number.getSource()).getDataSource().toString()).isEqualToIgnoringWhitespace("[1, 2, 3]");
    }

    @Test
    public void testFromWithInlineListMethod() throws Exception {
        String source = "rule XYZ \n" +
                " when \n" +
                " o: Order( ) \n" +
                " Number( ) from [1, 2, 3].sublist(1, 2) \n" +
                " then \n" +
                " System.err.println(\"Invalid customer id found!\"); \n" +
                " o.addError(\"Invalid customer id\"); \n" +
                "end \n";
        PackageDescr pkg = parser.parse(source);
        assertFalse(parser.getErrors().toString(),
                    parser.hasErrors());

        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertEquals("XYZ",
                     rule.getName());

        assertFalse(parser.hasErrors());
        PatternDescr number = (PatternDescr) rule.getLhs().getDescrs().get(1);

        assertThat(((FromDescr) number.getSource()).getDataSource().toString()).isEqualToIgnoringWhitespace("[1, 2, 3].sublist(1, 2)");
    }

    @Test
    public void testFromWithInlineListIndex() throws Exception {
        String source = "rule XYZ \n" +
                " when \n" +
                " o: Order( ) \n" +
                " Number( ) from [1, 2, 3][1] \n" +
                " then \n" +
                " System.err.println(\"Invalid customer id found!\"); \n" +
                " o.addError(\"Invalid customer id\"); \n" +
                "end \n";
        PackageDescr pkg = parser.parse(source);

        assertFalse(parser.getErrors().toString(),
                    parser.hasErrors());

        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertEquals("XYZ",
                     rule.getName());

        assertFalse(parser.hasErrors());
        PatternDescr number = (PatternDescr) rule.getLhs().getDescrs().get(1);
        assertThat(((FromDescr) number.getSource()).getDataSource().toString()).isEqualToIgnoringWhitespace("[1, 2, 3][1]");
    }

    @Test
    public void testRuleWithoutEnd() throws Exception {
        String source = "rule \"Invalid customer id\" \n" +
                " when \n" +
                " o: Order( ) \n" +
                " then \n" +
                " System.err.println(\"Invalid customer id found!\"); \n";
        parser.parse(source);
        assertTrue(parser.hasErrors());
    }

    @Test
    public void testOrWithSpecialBind() throws Exception {
        String source = "rule \"A and (B or C or D)\" \n" +
                "    when \n" +
                "        pdo1 : ParametricDataObject( paramID == 101, stringValue == \"1000\" ) and \n" +
                "        pdo2 :(ParametricDataObject( paramID == 101, stringValue == \"1001\" ) or \n" +
                "               ParametricDataObject( paramID == 101, stringValue == \"1002\" ) or \n" +
                "               ParametricDataObject( paramID == 101, stringValue == \"1003\" )) \n" +
                "    then \n" +
                "        System.out.println( \"Rule: A and (B or C or D) Fired. pdo1: \" + pdo1 +  \" pdo2: \"+ pdo2); \n" +
                "end\n";
        PackageDescr pkg = parser.parse(source);
        assertFalse(parser.getErrors().toString(),
                    parser.hasErrors());

        RuleDescr rule = pkg.getRules().get(0);
        AndDescr lhs = rule.getLhs();
        assertEquals(2,
                     lhs.getDescrs().size());

        PatternDescr pdo1 = (PatternDescr) lhs.getDescrs().get(0);
        assertEquals("pdo1",
                     pdo1.getIdentifier());

        OrDescr or = (OrDescr) rule.getLhs().getDescrs().get(1);
        assertEquals(3,
                     or.getDescrs().size());
        for (BaseDescr pdo2 : or.getDescrs()) {
            assertEquals("pdo2",
                         ((PatternDescr) pdo2).getIdentifier());
        }
    }

    @Test
    public void testCompatibleRestriction() throws Exception {
        String source = "package com.sample  rule test  when  Test( ( text == null || text2 matches \"\" ) )  then  end";
        PackageDescr pkg = parser.parse(source);

        assertEquals( "com.sample",
                      pkg.getName() );
        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( "test",
                      rule.getName() );
        ExprConstraintDescr expr = (ExprConstraintDescr) ((PatternDescr) rule.getLhs().getDescrs().get(0 )).getDescrs().get(0 );
        assertEquals( "( text == null || text2 matches \"\" )",
                      expr.getText() );
    }

    @Test
    public void testSimpleConstraint() throws Exception {
        String source = "package com.sample  rule test  when  Cheese( type == 'stilton', price > 10 )  then  end";
        PackageDescr pkg = parser.parse(source);

        assertEquals( "com.sample",
                      pkg.getName() );
        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( "test",
                      rule.getName() );

        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        AndDescr constraint = (AndDescr) pattern.getConstraint();
        assertEquals( 2,
                      constraint.getDescrs().size() );
        assertEquals( "type == \"stilton\"",
                      constraint.getDescrs().get( 0 ).toString() );
        assertEquals( "price > 10",
                      constraint.getDescrs().get( 1 ).toString() );
    }

    @Test
    public void testStringEscapes() throws Exception {
        String source = "package com.sample  rule test  when  Cheese( type matches \"\\..*\\\\.\" )  then  end";
        PackageDescr pkg = parser.parse(source);
        assertEquals( "com.sample",
                      pkg.getName() );
        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( "test",
                      rule.getName() );

        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        AndDescr constraint = (AndDescr) pattern.getConstraint();
        assertEquals( 1,
                      constraint.getDescrs().size() );
        assertEquals( "type matches \"\\..*\\\\.\"",
                      constraint.getDescrs().get( 0 ).toString() );
    }

    @Test
    public void testDialect() throws Exception {
        final String source = "dialect 'mvel'";
        PackageDescr pkg = parser.parse(source);
        AttributeDescr attr = (AttributeDescr) pkg.getAttributes().get(0);
        assertEquals("dialect",
                     attr.getName());
        assertEquals("mvel",
                     attr.getValue());
    }

    @Test
    public void testDialect2() throws Exception {
        final String source = "dialect \"mvel\"";
        PackageDescr pkg = parser.parse(source);
        AttributeDescr attr = pkg.getAttributes().get(0);
        assertEquals("dialect",
                     attr.getName());
        assertEquals("mvel",
                     attr.getValue());
    }

    @Test
    public void testEmptyRuleWithoutWhen() throws Exception {
        String source = readResource("empty_rule.drl"); // without WHEN
        PackageDescr pkg = parser.parse(source);

        assertTrue(parser.getErrors().toString(),
                    parser.hasErrors());

        // Note : RuleParserTest.testEmptyRule allows this DRL, but I think is doesn't make sense to pass this DRL
    }

    @Test
    public void testKeywordCollisions() throws Exception {
        String source = readResource("eol_funny_business.drl"); // keywords everywhere

        // Note: eol_funny_business.drl is modified from the one under drools-test-coverage to be more realistic.
        // e.g. "package" is not allowed in a package value in Java, so it doesn't make sense to test. (Right to raise a parser error)

        PackageDescr pkg = parser.parse(source);

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        assertEquals( 1,
                      pkg.getRules().size() );
    }

    @Test
    public void testTernaryExpression() throws Exception {
        String source = readResource("ternary_expression.drl");
        PackageDescr pkg = parser.parse(source);

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      pkg.getRules().size() );

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace("if (speed > speedLimit ? true : false;) pullEmOver();");
    }

    @Test
    public void testFunctionWithArrays() throws Exception {
        String source = readResource("function_arrays.drl");

        // Note: function_arrays.drl is modified from the one under drools-test-coverage to be more realistic.
        // new String[3] {"a","b","c"} is invalid in Java (Cannot define dimension expressions when an array initializer is provided)
        // , so it doesn't make sense to test. (Right to raise a parser error)

        PackageDescr pkg = parser.parse(source);

        assertEquals( "foo",
                      pkg.getName() );
        assertEquals( 1,
                      pkg.getRules().size() );

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace("yourFunction(new String[] {\"a\",\"b\",\"c\"});");

        final FunctionDescr func = (FunctionDescr) pkg.getFunctions().get(0 );

        assertEquals( "String[]",
                      func.getReturnType() );
        assertEquals( "args[]",
                      func.getParameterNames().get( 0 ) );
        assertEquals( "String",
                      func.getParameterTypes().get( 0 ) );
    }

    @Test
    public void testAlmostEmptyRule() throws Exception {
        String source = readResource("almost_empty_rule.drl");
        PackageDescr pkg = parser.parse(source);

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );
        assertNotNull( pkg );

        RuleDescr rule = pkg.getRules().get( 0 );

        assertEquals( "almost_empty",
                      rule.getName() );
        assertNotNull( rule.getLhs() );
        assertEquals( "",
                      ((String) rule.getConsequence()).trim() );
    }

    @Test
    public void testQuotedStringNameRule() throws Exception {
        String source = readResource("quoted_string_name_rule.drl");
        PackageDescr pkg = parser.parse(source);

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        RuleDescr rule = pkg.getRules().get(0);
        assertNotNull( rule );

        assertEquals( "quoted string name",
                      rule.getName() );
        assertNotNull( rule.getLhs() );
        assertEquals( "",
                      ((String) rule.getConsequence()).trim() );
    }

    @Test
    public void testNoLoop() throws Exception {
        String source = readResource("no-loop.drl");
        PackageDescr pkg = parser.parse(source);

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        RuleDescr rule = pkg.getRules().get(0);
        assertNotNull( rule );

        assertEquals( "rule1",
                      rule.getName() );
        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get( "no-loop" );
        assertEquals( "false",
                      att.getValue() );
        assertEquals( "no-loop",
                      att.getName() );
    }

    @Test
    public void testAutofocus() throws Exception {
        String source = readResource("autofocus.drl");
        PackageDescr pkg = parser.parse(source);

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        RuleDescr rule = pkg.getRules().get(0);
        assertNotNull( rule );

        assertEquals( "rule1",
                      rule.getName() );
        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get( "auto-focus" );
        assertEquals( "true",
                      att.getValue() );
        assertEquals( "auto-focus",
                      att.getName() );
    }

    @Test
    public void testRuleFlowGroup() throws Exception {
        String source = readResource("ruleflowgroup.drl");
        PackageDescr pkg = parser.parse(source);

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        RuleDescr rule = pkg.getRules().get(0);
        assertNotNull( rule );

        assertEquals( "rule1",
                      rule.getName() );
        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get( "ruleflow-group" );
        assertEquals( "a group",
                      att.getValue() );
        assertEquals( "ruleflow-group",
                      att.getName() );
    }

    @Test
    public void testConsequenceWithDeclaration() throws Exception {
        String source = readResource("declaration-in-consequence.drl");
        PackageDescr pkg = parser.parse(source);

        // Note : Removed "i\i;" from the original declaration-in-consequence.drl under drools-test-coverage
        // because it's not a valid java expression and doesn't make sense to test. (Right to raise a parser error)

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        RuleDescr rule = pkg.getRules().get(0);
        assertNotNull( rule );

        assertEquals( "myrule",
                      rule.getName() );

        final String expected = "int i = 0; i = 1; i / 1; i == 1; i(i); i = 'i'; i.i.i; i<i; i>i; i=\"i\";  ++i;" +
                "i++; --i; i--; i += i; i -= i; i *= i; i /= i;" +
                "int i = 5;" + "for(int j; j<i; ++j) {" +
                "System.out.println(j);}" +
                "Object o = new String(\"Hello\");" +
                "String s = (String) o;";

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace(expected);
        assertTrue( ((String) rule.getConsequence()).indexOf( "++" ) > 0 );
        assertTrue( ((String) rule.getConsequence()).indexOf( "--" ) > 0 );
        assertTrue( ((String) rule.getConsequence()).indexOf( "+=" ) > 0 );
        assertTrue( ((String) rule.getConsequence()).indexOf( "==" ) > 0 );
        assertTrue( ((String) rule.getConsequence()).indexOf( "i++" ) > 0 );
        // note, need to assert that "i++" is preserved as is, no extra spaces.
    }
}
