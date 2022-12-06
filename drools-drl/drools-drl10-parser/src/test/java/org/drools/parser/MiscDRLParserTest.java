package org.drools.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.FunctionImportDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.MVELExprDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.OrDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This test class is being ported from org.drools.mvel.compiler.lang.RuleParserTest
 */
class MiscDRLParserTest {

    private DRLParserWrapper parser;

    @BeforeEach
    public void setUp() {
        parser = new DRLParserWrapper();
    }

    @AfterEach
    public void tearDown() {
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

    private RuleDescr parseAndGetFirstRule(String drl) {
        PackageDescr pkg = parser.parse(drl);
        assertThat(parser.hasErrors()).as(parser.getErrorMessages().toString()).isFalse();
        assertThat(pkg.getRules()).isNotEmpty();
        return pkg.getRules().get(0);
    }

    private RuleDescr parseAndGetFirstRuleFromFile(String filename) throws Exception {
        return parseAndGetFirstRule(readResource(filename));
    }

    @Test
    void parse_validPackage() {
        final String source = "package foo.bar.baz";
        final PackageDescr pkg = parser.parse(source);
        assertThat(pkg.getName()).isEqualTo("foo.bar.baz");
    }

    @Test
    void parse_packageWithErrorNode() {
        final String source = "package 12 foo.bar.baz";
        final PackageDescr pkg = parser.parse(source);
        assertThat(parser.hasErrors()).isTrue();
        assertThat(pkg.getName()).isEqualTo("foo.bar.baz");
    }

    @Test
    void parse_packageWithAllErrorNode() {
        final String source = "package 12 12312 231";
        final PackageDescr pkg = parser.parse(source);
        assertThat(parser.hasErrors()).isTrue();
        assertThat(pkg.getName()).isEmpty();
    }

    @Test
    void parse_import() {
        final String source = "package foo; import com.foo.Bar; import com.foo.Baz;";
        PackageDescr pkg = parser.parse(source);
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        assertThat(pkg.getName()).isEqualTo("foo");
        assertThat(pkg.getImports()).hasSize(2);
        ImportDescr impdescr = pkg.getImports().get(0);
        assertThat(impdescr.getTarget()).isEqualTo("com.foo.Bar");
        assertThat(impdescr.getStartCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()));
        assertThat(impdescr.getEndCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length());

        impdescr = pkg.getImports().get(1);
        assertThat(impdescr.getTarget()).isEqualTo("com.foo.Baz");
        assertThat(impdescr.getStartCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()));
        assertThat(impdescr.getEndCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length());
    }

    @Test
    void parse_functionImport() {
        final String source = "package foo\n" +
                "import function java.lang.Math.max\n" +
                "import function java.lang.Math.min;\n" +
                "import foo.bar.*\n" +
                "import baz.Baz";
        PackageDescr pkg = parser.parse(source);
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        assertThat(pkg.getName()).isEqualTo("foo");
        assertThat(pkg.getImports()).hasSize(2);
        ImportDescr impdescr = pkg.getImports().get(0);
        assertThat(impdescr.getTarget()).isEqualTo("foo.bar.*");
        assertThat(impdescr.getStartCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()));

        assertThat(impdescr.getEndCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length() - 1);

        impdescr = pkg.getImports().get(1);
        assertThat(impdescr.getTarget()).isEqualTo("baz.Baz");
        assertThat(impdescr.getStartCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()));
        assertThat(impdescr.getEndCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length() - 1);

        assertThat(pkg.getFunctionImports()).hasSize(2);
        impdescr = pkg.getFunctionImports().get(0);
        assertThat(impdescr.getTarget()).isEqualTo("java.lang.Math.max");
        assertThat(impdescr.getStartCharacter()).isEqualTo(source.indexOf("import function " + impdescr.getTarget()));
        assertThat(impdescr.getEndCharacter()).isEqualTo(source.indexOf("import function " + impdescr.getTarget()) + ("import function " + impdescr.getTarget()).length() - 1);

        impdescr = pkg.getFunctionImports().get(1);
        assertThat(impdescr.getTarget()).isEqualTo("java.lang.Math.min");
        assertThat(impdescr.getStartCharacter()).isEqualTo(source.indexOf("import function " + impdescr.getTarget()));
        assertThat(impdescr.getEndCharacter()).isEqualTo(source.indexOf("import function " + impdescr.getTarget()) + ("import function " + impdescr.getTarget()).length());
    }

    @Test
    void parse_globalWithComplexType() {
        final String source = "package foo.bar.baz\n" +
                "import com.foo.Bar\n" +
                "global java.util.List<java.util.Map<String,Integer>> aList;\n" +
                "global Integer aNumber";
        PackageDescr pkg = parser.parse(source);
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        assertThat(pkg.getName()).isEqualTo("foo.bar.baz");
        assertThat(pkg.getImports()).hasSize(1);

        ImportDescr impdescr = pkg.getImports().get(0);
        assertThat(impdescr.getTarget()).isEqualTo("com.foo.Bar");
        assertThat(impdescr.getStartCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()));
        assertThat(impdescr.getEndCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length() - 1);

        assertThat(pkg.getGlobals()).hasSize(2);

        GlobalDescr global = pkg.getGlobals().get(0);
        assertThat(global.getType()).isEqualTo("java.util.List<java.util.Map<String,Integer>>");
        assertThat(global.getIdentifier()).isEqualTo("aList");
        assertThat(global.getStartCharacter()).isEqualTo(source.indexOf("global " + global.getType()));
        assertThat(global.getEndCharacter()).isEqualTo(source.indexOf("global " + global.getType() + " " + global.getIdentifier()) +
                                                               ("global " + global.getType() + " " + global.getIdentifier()).length());

        global = pkg.getGlobals().get(1);
        assertThat(global.getType()).isEqualTo("Integer");
        assertThat(global.getIdentifier()).isEqualTo("aNumber");
        assertThat(global.getStartCharacter()).isEqualTo(source.indexOf("global " + global.getType()));
        assertThat(global.getEndCharacter()).isEqualTo(source.indexOf("global " + global.getType() + " " + global.getIdentifier()) +
                                                               ("global " + global.getType() + " " + global.getIdentifier()).length() - 1);
    }

    @Test
    void parse_globalWithOrWithoutSemi() throws Exception {
        String source = readResource("globals.drl");
        PackageDescr pkg = parser.parse(source);

        assertThat(pkg.getRules()).hasSize(1);

        final RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs()).hasSize(1);

        assertThat(pkg.getImports()).hasSize(1);
        assertThat(pkg.getGlobals()).hasSize(2);

        final GlobalDescr foo = (GlobalDescr) pkg.getGlobals().get(0);
        assertThat(foo.getType()).isEqualTo("java.lang.String");
        assertThat(foo.getIdentifier()).isEqualTo("foo");
        final GlobalDescr bar = (GlobalDescr) pkg.getGlobals().get(1);
        assertThat(bar.getType()).isEqualTo("java.lang.Integer");
        assertThat(bar.getIdentifier()).isEqualTo("bar");
    }

    @Test
    void parse_functionImportWithNotExist() throws Exception {
        String source = readResource("test_FunctionImport.drl");
        PackageDescr pkg = parser.parse(source);

        assertThat(pkg.getFunctionImports()).hasSize(2);

        assertThat(((FunctionImportDescr) pkg.getFunctionImports().get(0)).getTarget()).isEqualTo("abd.def.x");
        assertThat(((FunctionImportDescr) pkg.getFunctionImports().get(0)).getStartCharacter()).isNotSameAs(-1);
        assertThat(((FunctionImportDescr) pkg.getFunctionImports().get(0)).getEndCharacter()).isNotSameAs(-1);
        assertThat(((FunctionImportDescr) pkg.getFunctionImports().get(1)).getTarget()).isEqualTo("qed.wah.*");
        assertThat(((FunctionImportDescr) pkg.getFunctionImports().get(1)).getStartCharacter()).isNotSameAs(-1);
        assertThat(((FunctionImportDescr) pkg.getFunctionImports().get(1)).getEndCharacter()).isNotSameAs(-1);
    }

    @Test
    void parse_fromComplexAccessor() {
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

        assertThat(parser.hasErrors()).as(parser.getErrorMessages().toString()).isFalse();

        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("Invalid customer id");

        assertThat(rule.getLhs().getDescrs()).hasSize(2);

        NotDescr not = (NotDescr) rule.getLhs().getDescrs().get(1);
        PatternDescr customer = (PatternDescr) not.getDescrs().get(0);

        assertThat(customer.getObjectType()).isEqualTo("Customer");
        assertThat(((FromDescr) customer.getSource()).getDataSource().getText()).isEqualTo("customerService.getCustomer(o.getCustomerId())");
    }

    @Test
    void parse_fromWithInlineList() {
        String source = "rule XYZ \n" +
                " when \n" +
                " o: Order( ) \n" +
                " not( Number( ) from [1, 2, 3] ) \n" +
                " then \n" +
                " System.err.println(\"Invalid customer id found!\"); \n" +
                " o.addError(\"Invalid customer id\"); \n" +
                "end \n";
        PackageDescr pkg = parser.parse(source);
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("XYZ");

        PatternDescr number = (PatternDescr) ((NotDescr) rule.getLhs().getDescrs().get(1)).getDescrs().get(0);
        assertThat(((FromDescr) number.getSource()).getDataSource().toString()).isEqualToIgnoringWhitespace("[1, 2, 3]");
    }

    @Test
    void parse_fromWithInlineListMethod() {
        String source = "rule XYZ \n" +
                " when \n" +
                " o: Order( ) \n" +
                " Number( ) from [1, 2, 3].sublist(1, 2) \n" +
                " then \n" +
                " System.err.println(\"Invalid customer id found!\"); \n" +
                " o.addError(\"Invalid customer id\"); \n" +
                "end \n";
        PackageDescr pkg = parser.parse(source);
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("XYZ");

        assertThat(parser.hasErrors()).isFalse();
        PatternDescr number = (PatternDescr) rule.getLhs().getDescrs().get(1);

        assertThat(((FromDescr) number.getSource()).getDataSource().toString()).isEqualToIgnoringWhitespace("[1, 2, 3].sublist(1, 2)");
    }

    @Test
    void parse_fromWithInlineListIndex() {
        String source = "rule XYZ \n" +
                " when \n" +
                " o: Order( ) \n" +
                " Number( ) from [1, 2, 3][1] \n" +
                " then \n" +
                " System.err.println(\"Invalid customer id found!\"); \n" +
                " o.addError(\"Invalid customer id\"); \n" +
                "end \n";
        PackageDescr pkg = parser.parse(source);

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("XYZ");

        assertThat(parser.hasErrors()).isFalse();
        PatternDescr number = (PatternDescr) rule.getLhs().getDescrs().get(1);
        assertThat(((FromDescr) number.getSource()).getDataSource().toString()).isEqualToIgnoringWhitespace("[1, 2, 3][1]");
    }

    @Test
    void parse_ruleWithoutEnd() {
        String source = "rule \"Invalid customer id\" \n" +
                " when \n" +
                " o: Order( ) \n" +
                " then \n" +
                " System.err.println(\"Invalid customer id found!\"); \n";
        parser.parse(source);
        assertThat(parser.hasErrors()).isTrue();
    }

    @Test
    void parse_orWithSpecialBind() {
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
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get(0);
        AndDescr lhs = rule.getLhs();
        assertThat(lhs.getDescrs()).hasSize(2);

        PatternDescr pdo1 = (PatternDescr) lhs.getDescrs().get(0);
        assertThat(pdo1.getIdentifier()).isEqualTo("pdo1");

        OrDescr or = (OrDescr) rule.getLhs().getDescrs().get(1);
        assertThat(or.getDescrs()).hasSize(3);
        for (BaseDescr pdo2 : or.getDescrs()) {
            assertThat(((PatternDescr) pdo2).getIdentifier()).isEqualTo("pdo2");
        }
    }

    @Test
    void parse_compatibleRestriction() {
        String source = "package com.sample  rule test  when  Test( ( text == null || text2 matches \"\" ) )  then  end";
        PackageDescr pkg = parser.parse(source);

        assertThat(pkg.getName()).isEqualTo("com.sample");
        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("test");
        ExprConstraintDescr expr = (ExprConstraintDescr) ((PatternDescr) rule.getLhs().getDescrs().get(0)).getDescrs().get(0);
        assertThat(expr.getText()).isEqualTo("( text == null || text2 matches \"\" )");
    }

    @Test
    void parse_simpleConstraint() {
        String source = "package com.sample  rule test  when  Cheese( type == 'stilton', price > 10 )  then  end";
        PackageDescr pkg = parser.parse(source);

        assertThat(pkg.getName()).isEqualTo("com.sample");
        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("test");

        assertThat(rule.getLhs().getDescrs()).hasSize(1);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);

        AndDescr constraint = (AndDescr) pattern.getConstraint();
        assertThat(constraint.getDescrs()).hasSize(2);
        assertThat(constraint.getDescrs().get(0)).hasToString("type == \"stilton\"");
        assertThat(constraint.getDescrs().get(1)).hasToString("price > 10");
    }

    @Test
    void parse_stringEscapes() {
        String source = "package com.sample  rule test  when  Cheese( type matches \"\\..*\\\\.\" )  then  end";
        PackageDescr pkg = parser.parse(source);
        assertThat(pkg.getName()).isEqualTo("com.sample");
        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("test");

        assertThat(rule.getLhs().getDescrs()).hasSize(1);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);

        AndDescr constraint = (AndDescr) pattern.getConstraint();
        assertThat(constraint.getDescrs()).hasSize(1);
        assertThat(constraint.getDescrs().get(0)).hasToString("type matches \"\\..*\\\\.\"");
    }

    @Test
    void parse_dialectWithSingleQuotation() {
        final String source = "dialect 'mvel'";
        PackageDescr pkg = parser.parse(source);
        AttributeDescr attr = (AttributeDescr) pkg.getAttributes().get(0);
        assertThat(attr.getName()).isEqualTo("dialect");
        assertThat(attr.getValue()).isEqualTo("mvel");
    }

    @Test
    void parse_dialectWithDoubleQuotation() {
        final String source = "dialect \"mvel\"";
        PackageDescr pkg = parser.parse(source);
        AttributeDescr attr = pkg.getAttributes().get(0);
        assertThat(attr.getName()).isEqualTo("dialect");
        assertThat(attr.getValue()).isEqualTo("mvel");
    }

    @Test
    void parse_emptyRuleWithoutWhen() throws Exception {
        String source = readResource("empty_rule.drl"); // without WHEN
        PackageDescr pkg = parser.parse(source);

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isTrue();

        // Note : RuleParserTest.testEmptyRule allows this DRL, but I think is doesn't make sense to pass this DRL
    }

    @Test
    void parse_keywordCollisions() throws Exception {
        String source = readResource("eol_funny_business.drl"); // keywords everywhere

        // Note: eol_funny_business.drl is modified from the one under drools-test-coverage to be more realistic.
        // e.g. "package" is not allowed in a package value in Java, so it doesn't make sense to test. (Right to raise a parser error)

        PackageDescr pkg = parser.parse(source);

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(pkg.getRules()).hasSize(1);
    }

    @Test
    void parse_ternaryExpression() throws Exception {
        String source = readResource("ternary_expression.drl");
        PackageDescr pkg = parser.parse(source);

        final RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertThat(pkg.getRules()).hasSize(1);

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace("if (speed > speedLimit ? true : false;) pullEmOver();");
    }

    @Test
    void parse_functionWithArrays() throws Exception {
        String source = readResource("function_arrays.drl");

        // Note: function_arrays.drl is modified from the one under drools-test-coverage to be more realistic.
        // new String[3] {"a","b","c"} is invalid in Java (Cannot define dimension expressions when an array initializer is provided)
        // , so it doesn't make sense to test. (Right to raise a parser error)

        PackageDescr pkg = parser.parse(source);

        assertThat(pkg.getName()).isEqualTo("foo");
        assertThat(pkg.getRules()).hasSize(1);

        final RuleDescr rule = (RuleDescr) pkg.getRules().get(0);

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace("yourFunction(new String[] {\"a\",\"b\",\"c\"});");

        final FunctionDescr func = (FunctionDescr) pkg.getFunctions().get(0);

        assertThat(func.getReturnType()).isEqualTo("String[]");
        assertThat(func.getParameterNames().get(0)).isEqualTo("args[]");
        assertThat(func.getParameterTypes().get(0)).isEqualTo("String");
    }

    @Test
    void parse_almostEmptyRule() throws Exception {
        String source = readResource("almost_empty_rule.drl");
        PackageDescr pkg = parser.parse(source);

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        assertThat(pkg).isNotNull();

        RuleDescr rule = pkg.getRules().get(0);

        assertThat(rule.getName()).isEqualTo("almost_empty");
        assertThat(rule.getLhs()).isNotNull();
        assertThat(((String) rule.getConsequence()).trim()).isEmpty();
    }

    @Test
    void parse_quotedStringNameRule() throws Exception {
        String source = readResource("quoted_string_name_rule.drl");
        PackageDescr pkg = parser.parse(source);

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("quoted string name");
        assertThat(rule.getLhs()).isNotNull();
        assertThat(((String) rule.getConsequence()).trim()).isEmpty();
    }

    @Test
    void parse_noLoop() throws Exception {
        String source = readResource("no-loop.drl");
        PackageDescr pkg = parser.parse(source);

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("rule1");
        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get("no-loop");
        assertThat(att.getValue()).isEqualTo("false");
        assertThat(att.getName()).isEqualTo("no-loop");
    }

    @Test
    void parse_autofocus() throws Exception {
        String source = readResource("autofocus.drl");
        PackageDescr pkg = parser.parse(source);

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("rule1");
        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get("auto-focus");
        assertThat(att.getValue()).isEqualTo("true");
        assertThat(att.getName()).isEqualTo("auto-focus");
    }

    @Test
    void parse_ruleFlowGroup() throws Exception {
        String source = readResource("ruleflowgroup.drl");
        PackageDescr pkg = parser.parse(source);

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("rule1");
        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get("ruleflow-group");
        assertThat(att.getValue()).isEqualTo("a group");
        assertThat(att.getName()).isEqualTo("ruleflow-group");
    }

    @Test
    void parse_consequenceWithDeclaration() throws Exception {
        String source = readResource("declaration-in-consequence.drl");
        PackageDescr pkg = parser.parse(source);

        // Note : Removed "i\i;" from the original declaration-in-consequence.drl under drools-test-coverage
        // because it's not a valid java expression and doesn't make sense to test. (Right to raise a parser error)

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("myrule");

        final String expected = "int i = 0; i = 1; i / 1; i == 1; i(i); i = 'i'; i.i.i; i<i; i>i; i=\"i\";  ++i;" +
                "i++; --i; i--; i += i; i -= i; i *= i; i /= i;" +
                "int i = 5;" + "for(int j; j<i; ++j) {" +
                "System.out.println(j);}" +
                "Object o = new String(\"Hello\");" +
                "String s = (String) o;";

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace(expected);
        assertThat(((String) rule.getConsequence()).indexOf("++") > 0).isTrue();
        assertThat(((String) rule.getConsequence()).indexOf("--") > 0).isTrue();
        assertThat(((String) rule.getConsequence()).indexOf("+=") > 0).isTrue();
        assertThat(((String) rule.getConsequence()).indexOf("==") > 0).isTrue();
        assertThat(((String) rule.getConsequence()).indexOf("i++") > 0).isTrue();
        // note, need to assert that "i++" is preserved as is, no extra spaces.
    }

    @Test
    void parse_or() {
        final String text = "rule X when Person(age < 42, location==\"atlanta\") \nor\nPerson(name==\"bob\") then end";
        PackageDescr pkg = parser.parse(text);
        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        AndDescr lhs = rule.getLhs();
        assertThat(lhs.getDescrs()).hasSize(1);
        assertThat(((OrDescr) lhs.getDescrs().get(0)).getDescrs()).hasSize(2);
    }

    @Test
    void parse_lhsWithStringQuotes() {
        final String text = "rule X when Person( location==\"atlanta\\\"\") then end\n";
        PackageDescr pkg = parser.parse(text);
        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        AndDescr lhs = rule.getLhs();
        ExprConstraintDescr constr = (ExprConstraintDescr) ((PatternDescr) lhs.getDescrs().get(0)).getDescrs().get(0);

        assertThat(constr.getText()).isEqualToIgnoringWhitespace("location==\"atlanta\\\"\"");
    }

    @Test
    void parse_lhsWithStringQuotesEscapeChars() {
        final String text = "rule X when Cheese( $x: type, type == \"s\\tti\\\"lto\\nn\" ) then end\n";
        PackageDescr pkg = parser.parse(text);
        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        AndDescr lhs = rule.getLhs();
        ExprConstraintDescr constr = (ExprConstraintDescr) ((PatternDescr) lhs.getDescrs().get(0)).getDescrs().get(1);

        assertThat(constr.getText()).isEqualToIgnoringWhitespace("type == \"s\\tti\\\"lto\\nn\"");
    }

    @Test
    void parse_literalBoolAndNegativeNumbersRule() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleFromFile("literal_bool_and_negative.drl");

        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat(rule.getLhs()).isNotNull();

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace("cons();");

        final AndDescr lhs = rule.getLhs();
        assertThat(lhs.getDescrs()).hasSize(3);

        PatternDescr pattern = (PatternDescr) lhs.getDescrs().get(0);
        assertThat(pattern.getConstraint().getDescrs()).hasSize(1);
        AndDescr fieldAnd = (AndDescr) pattern.getConstraint();
        ExprConstraintDescr fld = (ExprConstraintDescr) fieldAnd.getDescrs().get(0);
        assertThat(fld.getExpression()).isEqualToIgnoringWhitespace("bar == false");

        pattern = (PatternDescr) lhs.getDescrs().get(1);
        assertThat(pattern.getConstraint().getDescrs()).hasSize(1);

        fieldAnd = (AndDescr) pattern.getConstraint();
        fld = (ExprConstraintDescr) fieldAnd.getDescrs().get(0);

        assertThat(fld.getText()).isEqualToIgnoringWhitespace("boo > -42");

        pattern = (PatternDescr) lhs.getDescrs().get(2);
        assertThat(pattern.getConstraint().getDescrs()).hasSize(1);

        fieldAnd = (AndDescr) pattern.getConstraint();
        fld = (ExprConstraintDescr) fieldAnd.getDescrs().get(0);

        assertThat(fld.getText()).isEqualToIgnoringWhitespace("boo > -42.42");
    }

    @Test
    void parse_emptyPattern() throws Exception {
        String source = readResource("test_EmptyPattern.drl");
        PackageDescr pkg = parser.parse(source);

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(pkg.getRules()).hasSize(1);
        final RuleDescr ruleDescr = (RuleDescr) pkg.getRules().get(0);
        assertThat(ruleDescr.getName()).isEqualTo("simple rule");
        assertThat(ruleDescr.getLhs()).isNotNull();
        assertThat(ruleDescr.getLhs().getDescrs()).hasSize(1);
        final PatternDescr patternDescr = (PatternDescr) ruleDescr.getLhs().getDescrs().get(0);
        assertThat(patternDescr.getConstraint().getDescrs()).isEmpty(); // this
        assertThat(patternDescr.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    void parse_simpleMethodCallWithFrom() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleFromFile("test_SimpleMethodCallWithFrom.drl");
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr method = (MVELExprDescr) from.getDataSource();

        assertThat(method.getExpression()).isEqualToIgnoringWhitespace("something.doIt( foo,bar,42,\"hello\",[ a : \"b\", \"something\" : 42, \"a\" : foo, x : [x:y]],\"end\", [a, \"b\", 42] )");
    }

    @Test
    void parse_simpleFunctionCallWithFrom() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleFromFile("test_SimpleFunctionCallWithFrom.drl");
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr func = (MVELExprDescr) from.getDataSource();

        assertThat(func.getExpression()).isEqualToIgnoringWhitespace("doIt( foo,bar,42,\"hello\",[ a : \"b\", \"something\" : 42, \"a\" : foo, x : [x:y]],\"end\", [a, \"b\", 42] )");
    }

    @Test
    void parse_simpleAccessorWithFrom() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleFromFile("test_SimpleAccessorWithFrom.drl");
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr accessor = (MVELExprDescr) from.getDataSource();

        assertThat(accessor.getExpression()).isEqualTo("something.doIt");
    }

    @Test
    void parse_simpleAccessorAndArgWithFrom() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleFromFile("test_SimpleAccessorArgWithFrom.drl");
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr accessor = (MVELExprDescr) from.getDataSource();

        assertThat(accessor.getExpression()).isEqualTo("something.doIt[\"key\"]");
    }

    @Test
    void parse_complexChainedAccessor() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleFromFile("test_ComplexChainedCallWithFrom.drl");

        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr accessor = (MVELExprDescr) from.getDataSource();

        assertThat(accessor.getExpression()).isEqualToIgnoringWhitespace("doIt1( foo,bar,42,\"hello\",[ a : \"b\"], [a, \"b\", 42] ).doIt2(bar, [a, \"b\", 42]).field[\"key\"]");
    }

    @Test
    void parse_from() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleFromFile("from.drl");
        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("using_from");

        assertThat(rule.getLhs().getDescrs()).hasSize(9);
    }

    @Test
    void parse_simpleRuleWithBindings() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleFromFile("simple_rule.drl");
        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("simple_rule");

        assertThat(rule.getConsequenceLine()).isEqualTo(22);
        assertThat(rule.getConsequencePattern()).isEqualTo(2);

        final AndDescr lhs = rule.getLhs();

        assertThat(lhs).isNotNull();

        assertThat(lhs.getDescrs()).hasSize(3);

        // Check first pattern
        final PatternDescr first = (PatternDescr) lhs.getDescrs().get(0);
        assertThat(first.getIdentifier()).isEqualTo("foo3");
        assertThat(first.getObjectType()).isEqualTo("Bar");

        assertThat(first.getConstraint().getDescrs()).hasSize(1);

        AndDescr fieldAnd = (AndDescr) first.getConstraint();
        ExprConstraintDescr constraint = (ExprConstraintDescr) fieldAnd.getDescrs().get(0);
        assertThat(constraint).isNotNull();

        assertThat(constraint.getExpression()).isEqualToIgnoringWhitespace("a==3");

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get(1);
        assertThat(second.getIdentifier()).isEqualTo("foo4");
        assertThat(second.getObjectType()).isEqualTo("Bar");

        // no constraints, only a binding
        fieldAnd = (AndDescr) second.getConstraint();
        assertThat(fieldAnd.getDescrs()).hasSize(1);

        final ExprConstraintDescr binding = (ExprConstraintDescr) second.getConstraint().getDescrs().get(0);
        assertThat(binding.getExpression()).isEqualToIgnoringWhitespace("a4:a==4");

        // Check third pattern
        final PatternDescr third = (PatternDescr) lhs.getDescrs().get(2);
        assertThat(third.getIdentifier()).isNull();
        assertThat(third.getObjectType()).isEqualTo("Baz");

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace("if ( a == b ) { " + "  assert( foo3 );" + "} else {" + "  retract( foo4 );" + "}" + "  System.out.println( a4 );");
    }
}
