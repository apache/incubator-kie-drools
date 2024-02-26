package org.drools.drl.parser.antlr4;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.AccumulateImportDescr;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.BehaviorDescr;
import org.drools.drl.ast.descr.CollectDescr;
import org.drools.drl.ast.descr.EntryPointDeclarationDescr;
import org.drools.drl.ast.descr.EntryPointDescr;
import org.drools.drl.ast.descr.EvalDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.ForallDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.MVELExprDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.OrDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.QueryDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.drl.ast.descr.TypeFieldDescr;
import org.drools.drl.ast.descr.WindowDeclarationDescr;
import org.drools.drl.parser.antlr4.DRLParserWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This test class is ported from org.drools.mvel.compiler.lang.RuleParserTest
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
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private RuleDescr parseAndGetFirstRuleDescr(String drl) {
        PackageDescr pkg = parser.parse(drl);
        assertThat(parser.hasErrors()).as(parser.getErrorMessages().toString()).isFalse();
        assertThat(pkg.getRules()).isNotEmpty();
        return pkg.getRules().get(0);
    }

    private RuleDescr parseAndGetFirstRuleDescrFromFile(String filename) throws Exception {
        return parseAndGetFirstRuleDescr(readResource(filename));
    }

    private PackageDescr parseAndGetPackageDescrFromFile(String filename) throws Exception {
        return parser.parse(readResource(filename));
    }

    private QueryDescr parseAndGetFirstQueryDescr(String drl) {
        PackageDescr pkg = parser.parse(drl);
        assertThat(parser.hasErrors()).as(parser.getErrorMessages().toString()).isFalse();
        assertThat(pkg.getRules()).isNotEmpty();
        Optional<QueryDescr> optQuery = pkg.getRules().stream().filter(QueryDescr.class::isInstance).map(QueryDescr.class::cast).findFirst();
        assertThat(optQuery).isPresent();
        return optQuery.get();
    }

    private QueryDescr parseAndGetFirstQueryDescrFromFile(String filename) throws Exception {
        return parseAndGetFirstQueryDescr(readResource(filename));
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

        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs()).hasSize(1);

        assertThat(pkg.getImports()).hasSize(1);
        assertThat(pkg.getGlobals()).hasSize(2);

        final GlobalDescr foo = pkg.getGlobals().get(0);
        assertThat(foo.getType()).isEqualTo("java.lang.String");
        assertThat(foo.getIdentifier()).isEqualTo("foo");
        final GlobalDescr bar = pkg.getGlobals().get(1);
        assertThat(bar.getType()).isEqualTo("java.lang.Integer");
        assertThat(bar.getIdentifier()).isEqualTo("bar");
    }

    @Test
    void parse_functionImportWithNotExist() throws Exception {
        String source = readResource("test_FunctionImport.drl");
        PackageDescr pkg = parser.parse(source);

        assertThat(pkg.getFunctionImports()).hasSize(2);

        assertThat(pkg.getFunctionImports().get(0).getTarget()).isEqualTo("abd.def.x");
        assertThat(pkg.getFunctionImports().get(0).getStartCharacter()).isNotSameAs(-1);
        assertThat(pkg.getFunctionImports().get(0).getEndCharacter()).isNotSameAs(-1);
        assertThat(pkg.getFunctionImports().get(1).getTarget()).isEqualTo("qed.wah.*");
        assertThat(pkg.getFunctionImports().get(1).getStartCharacter()).isNotSameAs(-1);
        assertThat(pkg.getFunctionImports().get(1).getEndCharacter()).isNotSameAs(-1);
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

        RuleDescr rule = pkg.getRules().get(0);
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

        RuleDescr rule = pkg.getRules().get(0);
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

        RuleDescr rule = pkg.getRules().get(0);
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

        RuleDescr rule = pkg.getRules().get(0);
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
        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("test");
        ExprConstraintDescr expr = (ExprConstraintDescr) ((PatternDescr) rule.getLhs().getDescrs().get(0)).getDescrs().get(0);
        assertThat(expr.getText()).isEqualTo("( text == null || text2 matches \"\" )");
    }

    @Test
    void parse_simpleConstraint() {
        String source = "package com.sample  rule test  when  Cheese( type == 'stilton', price > 10 )  then  end";
        PackageDescr pkg = parser.parse(source);

        assertThat(pkg.getName()).isEqualTo("com.sample");
        RuleDescr rule = pkg.getRules().get(0);
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
        RuleDescr rule = pkg.getRules().get(0);
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
        AttributeDescr attr = pkg.getAttributes().get(0);
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
        parser.parse(source);

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

        final RuleDescr rule = pkg.getRules().get(0);
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

        final RuleDescr rule = pkg.getRules().get(0);

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace("yourFunction(new String[] {\"a\",\"b\",\"c\"});");

        final FunctionDescr func = pkg.getFunctions().get(0);

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
        final AttributeDescr att = rule.getAttributes().get("no-loop");
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
        final AttributeDescr att = rule.getAttributes().get("auto-focus");
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
        final AttributeDescr att = rule.getAttributes().get("ruleflow-group");
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
        RuleDescr rule = pkg.getRules().get(0);

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
        RuleDescr rule = pkg.getRules().get(0);

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
        RuleDescr rule = pkg.getRules().get(0);
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        AndDescr lhs = rule.getLhs();
        ExprConstraintDescr constr = (ExprConstraintDescr) ((PatternDescr) lhs.getDescrs().get(0)).getDescrs().get(1);

        assertThat(constr.getText()).isEqualToIgnoringWhitespace("type == \"s\\tti\\\"lto\\nn\"");
    }

    @Test
    void parse_literalBoolAndNegativeNumbersRule() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleDescrFromFile("literal_bool_and_negative.drl");

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
        final RuleDescr ruleDescr = pkg.getRules().get(0);
        assertThat(ruleDescr.getName()).isEqualTo("simple rule");
        assertThat(ruleDescr.getLhs()).isNotNull();
        assertThat(ruleDescr.getLhs().getDescrs()).hasSize(1);
        final PatternDescr patternDescr = (PatternDescr) ruleDescr.getLhs().getDescrs().get(0);
        assertThat(patternDescr.getConstraint().getDescrs()).isEmpty(); // this
        assertThat(patternDescr.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    void parse_simpleMethodCallWithFrom() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleDescrFromFile("test_SimpleMethodCallWithFrom.drl");
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr method = (MVELExprDescr) from.getDataSource();

        assertThat(method.getExpression()).isEqualToIgnoringWhitespace("something.doIt( foo,bar,42,\"hello\",[ a : \"b\", \"something\" : 42, \"a\" : foo, x : [x:y]],\"end\", [a, \"b\", 42] )");
    }

    @Test
    void parse_simpleFunctionCallWithFrom() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleDescrFromFile("test_SimpleFunctionCallWithFrom.drl");
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr func = (MVELExprDescr) from.getDataSource();

        assertThat(func.getExpression()).isEqualToIgnoringWhitespace("doIt( foo,bar,42,\"hello\",[ a : \"b\", \"something\" : 42, \"a\" : foo, x : [x:y]],\"end\", [a, \"b\", 42] )");
    }

    @Test
    void parse_simpleAccessorWithFrom() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleDescrFromFile("test_SimpleAccessorWithFrom.drl");
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr accessor = (MVELExprDescr) from.getDataSource();

        assertThat(accessor.getExpression()).isEqualTo("something.doIt");
    }

    @Test
    void parse_simpleAccessorAndArgWithFrom() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleDescrFromFile("test_SimpleAccessorArgWithFrom.drl");
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr accessor = (MVELExprDescr) from.getDataSource();

        assertThat(accessor.getExpression()).isEqualTo("something.doIt[\"key\"]");
    }

    @Test
    void parse_complexChainedAccessor() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleDescrFromFile("test_ComplexChainedCallWithFrom.drl");

        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr accessor = (MVELExprDescr) from.getDataSource();

        assertThat(accessor.getExpression()).isEqualToIgnoringWhitespace("doIt1( foo,bar,42,\"hello\",[ a : \"b\"], [a, \"b\", 42] ).doIt2(bar, [a, \"b\", 42]).field[\"key\"]");
    }

    @Test
    void parse_from() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleDescrFromFile("from.drl");
        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("using_from");

        assertThat(rule.getLhs().getDescrs()).hasSize(9);
    }

    @Test
    void parse_simpleRuleWithBindings() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleDescrFromFile("simple_rule.drl");
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

    @Test
    void parse_multipleRestrictionsConstraint() throws Exception {
        RuleDescr rule = parseAndGetFirstRuleDescrFromFile("restrictions_test.drl");
        assertThat(rule).isNotNull();

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace("consequence();");
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat(rule.getLhs().getDescrs()).hasSize(2);

        // The first pattern, with 2 restrictions on a single field (plus a
        // connective)
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("Person");
        assertThat( pattern.getConstraint().getDescrs()).hasSize(1);

        AndDescr and = (AndDescr) pattern.getConstraint();
        ExprConstraintDescr fld = (ExprConstraintDescr) and.getDescrs().get( 0 );
        assertThat(fld.getExpression()).isEqualTo("age > 30 && < 40");

        // the second col, with 2 fields, the first with 2 restrictions, the
        // second field with one
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("Vehicle");
        assertThat(pattern.getConstraint().getDescrs()).hasSize(2);

        and = (AndDescr) pattern.getConstraint();
        fld = (ExprConstraintDescr) and.getDescrs().get( 0 );
        assertThat(fld.getExpression()).isEqualToIgnoringWhitespace( "type == \"sedan\" || == \"wagon\"");

        // now the second field
        fld = (ExprConstraintDescr) and.getDescrs().get( 1 );
        assertThat(fld.getExpression()).isEqualToIgnoringWhitespace( "age < 3");
    }

//-------------------------------------------------------------------------
// DROOLS-7271 : ported from RuleParserTest
// Failing tests are annotated with @Disabled. We can fix issues one by one
//-------------------------------------------------------------------------

    @Disabled("Priority : Mid | implement Descr lineNumber")
    @Test
    public void parse_LineNumberInAST() throws Exception {
        // also see testSimpleExpander to see how this works with an expander
        // (should be the same).

        final RuleDescr rule = parseAndGetFirstRuleDescrFromFile(
                                                          "simple_rule.drl" );

        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("simple_rule");

        assertThat(rule.getConsequenceLine()).isEqualTo(22);
        assertThat(rule.getConsequencePattern()).isEqualTo(2);

        final AndDescr lhs = rule.getLhs();

        assertThat(lhs).isNotNull();

        assertThat(lhs.getDescrs().size()).isEqualTo(3);

        // Check first pattern
        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(first.getIdentifier()).isEqualTo("foo3");
        assertThat(first.getObjectType()).isEqualTo("Bar");
        assertThat(first.getConstraint().getDescrs().size()).isEqualTo(1);

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
        assertThat(second.getIdentifier()).isEqualTo("foo4");
        assertThat(second.getObjectType()).isEqualTo("Bar");

        final PatternDescr third = (PatternDescr) lhs.getDescrs().get( 2 );
        assertThat(third.getObjectType()).isEqualTo("Baz");

        assertThat(first.getLine()).isEqualTo(19);
        assertThat(second.getLine()).isEqualTo(20);
        assertThat(third.getLine()).isEqualTo(21);
    }

    @Test
    public void parse_LineNumberIncludingCommentsInRHS() throws Exception {
        PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                         "test_CommentLineNumbersInConsequence.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        final String rhs = (String) ((RuleDescr) pkg.getRules().get( 0 )).getConsequence();
        String expected = "\\s*//woot$\\s*first;$\\s*$\\s*//$\\s*$\\s*/\\* lala$\\s*$\\s*\\*/$\\s*second;$\\s*";
        assertThat(Pattern.compile(expected,
                                   Pattern.DOTALL | Pattern.MULTILINE).matcher(rhs).matches()).isTrue();
    }

    @Test
    public void parse_LhsSemicolonDelim() throws Exception {
        final RuleDescr rule = parseAndGetFirstRuleDescrFromFile(
                                                          "lhs_semicolon_delim.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("simple_rule");

        final AndDescr lhs = rule.getLhs();

        assertThat(lhs).isNotNull();

        assertThat(lhs.getDescrs().size()).isEqualTo(3);

        // System.err.println( lhs.getDescrs() );

        // Check first pattern
        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(first.getIdentifier()).isEqualTo("foo3");
        assertThat(first.getObjectType()).isEqualTo("Bar");

        assertThat(first.getConstraint().getDescrs().size()).isEqualTo(1);

        // LiteralDescr constraint = (LiteralDescr) first.getDescrs().get( 0 );
        AndDescr and = (AndDescr) first.getConstraint();
        ExprConstraintDescr fld = (ExprConstraintDescr) and.getDescrs().get( 0 );
        assertThat(fld).isNotNull();

        assertThat(fld.getExpression()).isEqualTo("a==3");

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
        assertThat(second.getIdentifier()).isEqualTo("foo4");
        assertThat(second.getObjectType()).isEqualTo("Bar");

        assertThat(second.getDescrs().size()).isEqualTo(1);

        final ExprConstraintDescr fieldBindingDescr = (ExprConstraintDescr) second.getDescrs().get( 0 );
        assertThat(fieldBindingDescr.getExpression()).isEqualTo("a4:a==4");

        // Check third pattern
        final PatternDescr third = (PatternDescr) lhs.getDescrs().get( 2 );
        assertThat(third.getIdentifier()).isNull();
        assertThat(third.getObjectType()).isEqualTo("Baz");

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace("if ( a == b ) { " + "  assert( foo3 );" + "} else {" + "  retract( foo4 );" + "}" + "  System.out.println( a4 );");
    }

    @Test
    public void parse_NotNode() throws Exception {
        final RuleDescr rule = parseAndGetFirstRuleDescrFromFile(
                                                          "rule_not.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();
        assertThat(rule.getName()).isEqualTo("simple_rule");

        final AndDescr lhs = rule.getLhs();
        assertThat(lhs.getDescrs().size()).isEqualTo(1);
        final NotDescr not = (NotDescr) lhs.getDescrs().get( 0 );
        assertThat(not.getDescrs().size()).isEqualTo(1);
        final PatternDescr pattern = (PatternDescr) not.getDescrs().get( 0 );

        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(1);

        final AndDescr and = (AndDescr) pattern.getConstraint();
        final ExprConstraintDescr fld = (ExprConstraintDescr) and.getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualTo("type == \"stilton\"");
    }

    @Test
    public void parse_NotExistWithBrackets() throws Exception {

        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "not_exist_with_brackets.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        assertThat(rule).isNotNull();
        assertThat(rule.getName()).isEqualTo("simple_rule");

        final AndDescr lhs = rule.getLhs();
        assertThat(lhs.getDescrs().size()).isEqualTo(2);
        final NotDescr not = (NotDescr) lhs.getDescrs().get( 0 );
        assertThat(not.getDescrs().size()).isEqualTo(1);
        final PatternDescr pattern = (PatternDescr) not.getDescrs().get( 0 );

        assertThat(pattern.getObjectType()).isEqualTo("Cheese");

        final ExistsDescr ex = (ExistsDescr) lhs.getDescrs().get(1 );
        assertThat(ex.getDescrs().size()).isEqualTo(1);
        final PatternDescr exPattern = (PatternDescr) ex.getDescrs().get( 0 );
        assertThat(exPattern.getObjectType()).isEqualTo("Foo");
    }

    @Test
    public void parse_SimpleQuery() throws Exception {
        final QueryDescr query = parseAndGetFirstQueryDescrFromFile(
                                                            "simple_query.drl" );

        assertThat(query).isNotNull();

        assertThat(query.getName()).isEqualTo("simple_query");

        final AndDescr lhs = query.getLhs();

        assertThat(lhs).isNotNull();

        assertThat(lhs.getDescrs().size()).isEqualTo(3);

        // Check first pattern
        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(first.getIdentifier()).isEqualTo("foo3");
        assertThat(first.getObjectType()).isEqualTo("Bar");

        assertThat(first.getConstraint().getDescrs().size()).isEqualTo(1);

        AndDescr and = (AndDescr) first.getConstraint();
        ExprConstraintDescr fld = (ExprConstraintDescr) and.getDescrs().get( 0 );
        assertThat(fld).isNotNull();

        assertThat(fld.getExpression()).isEqualTo("a==3");

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
        assertThat(second.getIdentifier()).isEqualTo("foo4");
        assertThat(second.getObjectType()).isEqualTo("Bar");

        assertThat(second.getDescrs().size()).isEqualTo(1);
        // check it has field bindings.
        final ExprConstraintDescr bindingDescr = (ExprConstraintDescr) second.getDescrs().get( 0 );
        assertThat(bindingDescr.getExpression()).isEqualTo("a4:a==4");
    }

    @Test
    public void parse_QueryRuleMixed() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "query_and_rule.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(4); // as queries are rules
        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getName()).isEqualTo("bar");

        QueryDescr query = (QueryDescr) pkg.getRules().get( 1 );
        assertThat(query.getName()).isEqualTo("simple_query");

        rule = (RuleDescr) pkg.getRules().get( 2 );
        assertThat(rule.getName()).isEqualTo("bar2");

        query = (QueryDescr) pkg.getRules().get( 3 );
        assertThat(query.getName()).isEqualTo("simple_query2");
    }

    @Test
    public void parse_MultipleRules() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "multiple_rules.drl" );

        final List<RuleDescr> rules = pkg.getRules();

        assertThat(rules.size()).isEqualTo(2);

        final RuleDescr rule0 = rules.get( 0 );
        assertThat(rule0.getName()).isEqualTo("Like Stilton");

        final RuleDescr rule1 = rules.get( 1 );
        assertThat(rule1.getName()).isEqualTo("Like Cheddar");

        // checkout the first rule
        AndDescr lhs = rule1.getLhs();
        assertThat(lhs).isNotNull();
        assertThat(lhs.getDescrs().size()).isEqualTo(1);
        assertThat((String) rule0.getConsequence()).isEqualToIgnoringWhitespace( "System.out.println(\"I like \" + t);");

        // Check first pattern
        PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(first.getObjectType()).isEqualTo("Cheese");

        // checkout the second rule
        lhs = rule1.getLhs();
        assertThat(lhs).isNotNull();
        assertThat(lhs.getDescrs().size()).isEqualTo(1);
        assertThat((String) rule1.getConsequence()).isEqualToIgnoringWhitespace( "System.out.println(\"I like \" + t);");

        // Check first pattern
        first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(first.getObjectType()).isEqualTo("Cheese");
    }

    @Disabled("Priority : low | Not yet support DSL")
    @Test
    public void parse_ExpanderLineSpread() throws Exception {
//        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
//        final PackageDescr pkg = parser.parse( this.getReader( "expander_spread_lines.dslr" ),
//                                               this.getReader( "complex.dsl" ) );
//
//        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
//
//        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
//        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);
//
//        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
//        assertThat(or.getDescrs().size()).isEqualTo(2);
//        assertThat( (String) rule.getConsequence() ).isNotNull();

    }

    @Disabled("Priority : low | Not yet support DSL")
    @Test
    public void parse_ExpanderMultipleConstraints() throws Exception {
//        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
//        final PackageDescr pkg = parser.parse( this.getReader( "expander_multiple_constraints.dslr" ),
//                                               this.getReader( "multiple_constraints.dsl" ) );
//
//        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
//
//        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
//        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(2);
//
//        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
//        assertThat(pattern.getObjectType()).isEqualTo("Person");
//
//        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(2);
//        assertThat(((ExprConstraintDescr) pattern.getConstraint().getDescrs().get(0)).getExpression()).isEqualTo("age < 42");
//        assertThat(((ExprConstraintDescr) pattern.getConstraint().getDescrs().get(1)).getExpression()).isEqualTo("location==atlanta");
//
//        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
//        assertThat(pattern.getObjectType()).isEqualTo("Bar");
//
//        assertThat( (String) rule.getConsequence() ).isNotNull();

    }

    @Disabled("Priority : low | Not yet support DSL")
    @Test
    public void parse_ExpanderMultipleConstraintsFlush() throws Exception {
//        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
//        // this is similar to the other test, but it requires a flush to add the
//        // constraints
//        final PackageDescr pkg = parser.parse( this.getReader( "expander_multiple_constraints_flush.dslr" ),
//                                               this.getReader( "multiple_constraints.dsl" ) );
//
//        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
//
//        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
//        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);
//
//        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
//        assertThat(pattern.getObjectType()).isEqualTo("Person");
//
//        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(2);
//        assertThat(((ExprConstraintDescr) pattern.getConstraint().getDescrs().get(0)).getExpression()).isEqualTo("age < 42");
//        assertThat(((ExprConstraintDescr) pattern.getConstraint().getDescrs().get(1)).getExpression()).isEqualTo("location==atlanta");
//
//        assertThat( (String) rule.getConsequence() ).isNotNull();

    }

    @Test
    public void parse_BasicBinding() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "basic_binding.drl" );

        final RuleDescr ruleDescr = (RuleDescr) pkg.getRules().get( 0 );

        final AndDescr lhs = ruleDescr.getLhs();
        assertThat(lhs.getDescrs().size()).isEqualTo(1);
        final PatternDescr cheese = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(cheese.getObjectType()).isEqualTo("Cheese");
        assertThat(cheese.getConstraint().getDescrs().size()).isEqualTo(1);
        final ExprConstraintDescr fieldBinding = (ExprConstraintDescr) cheese.getDescrs().get( 0 );
        assertThat(fieldBinding.getExpression()).isEqualToIgnoringWhitespace("$type:type");
    }

    @Test
    public void parse_BoundVariables() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "bindings.drl" );

        final RuleDescr ruleDescr = (RuleDescr) pkg.getRules().get( 0 );

        final AndDescr lhs = ruleDescr.getLhs();
        assertThat(lhs.getDescrs().size()).isEqualTo(2);
        final PatternDescr cheese = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(cheese.getObjectType()).isEqualTo("Cheese");
        assertThat(cheese.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fieldBinding = (ExprConstraintDescr) cheese.getDescrs().get( 0 );
        assertThat(fieldBinding.getExpression()).isEqualTo("$type : type == \"stilton\"");

        final PatternDescr person = (PatternDescr) lhs.getDescrs().get( 1 );
        assertThat(person.getDescrs().size()).isEqualTo(2);
        fieldBinding = (ExprConstraintDescr) person.getDescrs().get( 0 );
        assertThat(fieldBinding.getExpression()).isEqualTo("$name : name == \"bob\"");

        ExprConstraintDescr fld = (ExprConstraintDescr) person.getDescrs().get( 1 );
        assertThat(fld.getExpression()).isEqualTo("likes == $type");
    }

    @Test
    public void parse_OrNesting() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "or_nesting.drl" );

        assertThat(pkg).isNotNull();
        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getName()).isEqualTo("simple_rule");

        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(or.getDescrs().size()).isEqualTo(2);

        final PatternDescr first = (PatternDescr) or.getDescrs().get( 0 );
        assertThat(first.getObjectType()).isEqualTo("Person");

        final AndDescr and = (AndDescr) or.getDescrs().get( 1 );
        assertThat(and.getDescrs().size()).isEqualTo(2);

        final PatternDescr left = (PatternDescr) and.getDescrs().get( 0 );
        assertThat(left.getObjectType()).isEqualTo("Person");

        final PatternDescr right = (PatternDescr) and.getDescrs().get( 1 );
        assertThat(right.getObjectType()).isEqualTo("Cheese");
    }

    /** Test that explicit "&&", "||" works as expected */
    @Test
    public void parse_AndOrRules() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "and_or_rule.drl" );

        assertThat(pkg).isNotNull();
        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getName()).isEqualTo("simple_rule");

        // we will have 3 children under the main And node
        final AndDescr and = rule.getLhs();
        assertThat(and.getDescrs().size()).isEqualTo(3);

        PatternDescr left = (PatternDescr) and.getDescrs().get( 0 );
        PatternDescr right = (PatternDescr) and.getDescrs().get( 1 );
        assertThat(left.getObjectType()).isEqualTo("Person");
        assertThat(right.getObjectType()).isEqualTo("Cheese");

        assertThat(left.getConstraint().getDescrs().size()).isEqualTo(1);

        ExprConstraintDescr fld = (ExprConstraintDescr) left.getConstraint().getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualTo("name == \"mark\"");

        assertThat(right.getConstraint().getDescrs().size()).isEqualTo(1);

        fld = (ExprConstraintDescr) right.getConstraint().getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualTo("type == \"stilton\"");

        // now the "||" part
        final OrDescr or = (OrDescr) and.getDescrs().get( 2 );
        assertThat(or.getDescrs().size()).isEqualTo(2);
        left = (PatternDescr) or.getDescrs().get( 0 );
        right = (PatternDescr) or.getDescrs().get( 1 );
        assertThat(left.getObjectType()).isEqualTo("Person");
        assertThat(right.getObjectType()).isEqualTo("Cheese");
        assertThat(left.getConstraint().getDescrs().size()).isEqualTo(1);

        fld = (ExprConstraintDescr) left.getConstraint().getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualTo("name == \"mark\"");

        assertThat(right.getConstraint().getDescrs().size()).isEqualTo(1);

        fld = (ExprConstraintDescr) right.getConstraint().getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualTo("type == \"stilton\"");

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( "System.out.println( \"Mark and Michael\" );");
    }

    /** test basic foo : Fact() || Fact() stuff */
    @Test
    public void parse_OrWithBinding() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "or_binding.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(2);

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(or.getDescrs().size()).isEqualTo(2);

        final PatternDescr leftPattern = (PatternDescr) or.getDescrs().get( 0 );
        assertThat(leftPattern.getObjectType()).isEqualTo("Person");
        assertThat(leftPattern.getIdentifier()).isEqualTo("foo");

        final PatternDescr rightPattern = (PatternDescr) or.getDescrs().get( 1 );
        assertThat(rightPattern.getObjectType()).isEqualTo("Person");
        assertThat(rightPattern.getIdentifier()).isEqualTo("foo");

        final PatternDescr cheeseDescr = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(cheeseDescr.getObjectType()).isEqualTo("Cheese");
        assertThat(cheeseDescr.getIdentifier()).isEqualTo(null);

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( "System.out.println( \"Mark and Michael\" + bar );");
    }

    /** test basic foo : Fact() || Fact() stuff binding to an "or" */
    @Test
    public void parse_OrBindingComplex() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "or_binding_complex.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(or.getDescrs().size()).isEqualTo(2);

        // first fact
        final PatternDescr firstFact = (PatternDescr) or.getDescrs().get( 0 );
        assertThat(firstFact.getObjectType()).isEqualTo("Person");
        assertThat(firstFact.getIdentifier()).isEqualTo("foo");

        // second "option"
        final PatternDescr secondFact = (PatternDescr) or.getDescrs().get( 1 );
        assertThat(secondFact.getObjectType()).isEqualTo("Person");
        assertThat(secondFact.getConstraint().getDescrs().size()).isEqualTo(1);
        assertThat(secondFact.getIdentifier()).isEqualTo("foo");

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( "System.out.println( \"Mark and Michael\" + bar );");
    }

    @Test
    public void parse_OrBindingWithBrackets() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "or_binding_with_brackets.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(or.getDescrs().size()).isEqualTo(2);

        // first fact
        final PatternDescr firstFact = (PatternDescr) or.getDescrs().get( 0 );
        assertThat(firstFact.getObjectType()).isEqualTo("Person");
        assertThat(firstFact.getIdentifier()).isEqualTo("foo");

        // second "option"
        final PatternDescr secondFact = (PatternDescr) or.getDescrs().get( 0 );
        assertThat(secondFact.getObjectType()).isEqualTo("Person");
        assertThat(secondFact.getIdentifier()).isEqualTo("foo");

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( "System.out.println( \"Mark and Michael\" + bar );");
    }

    @Test
    void parenthesesOrAndOr() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "brackets_precedence.drl" );

        assertThat(pkg.getRules()).hasSize(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        final AndDescr rootAnd = (AndDescr) rule.getLhs();

        assertThat(rootAnd.getDescrs()).hasSize(2);

        final OrDescr leftOr = (OrDescr) rootAnd.getDescrs().get( 0 );

        assertThat(leftOr.getDescrs()).hasSize(2);
        final NotDescr not = (NotDescr) leftOr.getDescrs().get( 0 );
        final PatternDescr foo1 = (PatternDescr) not.getDescrs().get( 0 );
        assertThat(foo1.getObjectType()).isEqualTo("Foo");
        final PatternDescr foo2 = (PatternDescr) leftOr.getDescrs().get( 1 );
        assertThat(foo2.getObjectType()).isEqualTo("Foo");

        final OrDescr rightOr = (OrDescr) rootAnd.getDescrs().get( 1 );

        assertThat(rightOr.getDescrs()).hasSize(2);
        final PatternDescr shoes = (PatternDescr) rightOr.getDescrs().get( 0 );
        assertThat(shoes.getObjectType()).isEqualTo("Shoes");
        final PatternDescr butt = (PatternDescr) rightOr.getDescrs().get( 1 );
        assertThat(butt.getObjectType()).isEqualTo("Butt");
    }

    @Test
    void parenthesesAndOrOr() {
        final String drl = "rule and_or_or\n" +
                "  when\n" +
                "     (Foo(x == 1) and Bar(x == 2)) or (Foo(x == 3) or Bar(x == 4))\n" +
                "  then\n" +
                "end";
        PackageDescr pkg = parser.parse(drl);

        assertThat(pkg.getRules()).hasSize(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        final AndDescr rootAnd = (AndDescr) rule.getLhs();
        assertThat(rootAnd.getDescrs()).hasSize(1);

        final OrDescr topOr = (OrDescr) rootAnd.getDescrs().get(0);
        assertThat(topOr.getDescrs()).hasSize(2);

        final AndDescr leftAnd = (AndDescr) topOr.getDescrs().get(0);
        assertThat(leftAnd.getDescrs()).hasSize(2);
        final PatternDescr foo1 = (PatternDescr) leftAnd.getDescrs().get(0);
        assertThat(foo1.getObjectType()).isEqualTo("Foo");
        final PatternDescr bar1 = (PatternDescr) leftAnd.getDescrs().get(1);
        assertThat(bar1.getObjectType()).isEqualTo("Bar");

        final OrDescr rightOr = (OrDescr) topOr.getDescrs().get(1);
        assertThat(rightOr.getDescrs()).hasSize(2);
        final PatternDescr foo2 = (PatternDescr) rightOr.getDescrs().get(0);
        assertThat(foo2.getObjectType()).isEqualTo("Foo");
        final PatternDescr bar2 = (PatternDescr) rightOr.getDescrs().get(1);
        assertThat(bar2.getObjectType()).isEqualTo("Bar");
    }

    @Test
    void parenthesesOrAndAnd() {
        final String drl = "rule or_and_and\n" +
                "  when\n" +
                "     (Foo(x == 1) or Bar(x == 2)) and (Foo(x == 3) and Bar(x == 4))\n" +
                "  then\n" +
                "end";
        PackageDescr pkg = parser.parse(drl);

        assertThat(pkg.getRules()).hasSize(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        final AndDescr rootAnd = (AndDescr) rule.getLhs();
        assertThat(rootAnd.getDescrs()).hasSize(2);

        final OrDescr leftOr = (OrDescr) rootAnd.getDescrs().get(0);
        assertThat(leftOr.getDescrs()).hasSize(2);
        final PatternDescr foo1 = (PatternDescr) leftOr.getDescrs().get(0);
        assertThat(foo1.getObjectType()).isEqualTo("Foo");
        final PatternDescr bar1 = (PatternDescr) leftOr.getDescrs().get(1);
        assertThat(bar1.getObjectType()).isEqualTo("Bar");

        final AndDescr rightAnd = (AndDescr) rootAnd.getDescrs().get(1);
        assertThat(rightAnd.getDescrs()).hasSize(2);
        final PatternDescr foo2 = (PatternDescr) rightAnd.getDescrs().get(0);
        assertThat(foo2.getObjectType()).isEqualTo("Foo");
        final PatternDescr bar2 = (PatternDescr) rightAnd.getDescrs().get(1);
        assertThat(bar2.getObjectType()).isEqualTo("Bar");
    }

    @Test
    void multipleLevelNestAndOrOrOrAnd() throws Exception {
        final String drl = "rule and_or_or_or_and\n" +
                "  when\n" +
                "     (Foo(x == 1) and (Bar(x == 2) or Foo(x == 3))) or (Bar(x == 4) or (Foo(x == 5) and Bar(x == 6)))\n" +
                "  then\n" +
                "end";
        PackageDescr pkg = parser.parse(drl);

        assertThat(pkg.getRules()).hasSize(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        final AndDescr rootAnd = (AndDescr) rule.getLhs();
        assertThat(rootAnd.getDescrs()).hasSize(1);

        final OrDescr topOr = (OrDescr) rootAnd.getDescrs().get(0);
        assertThat(topOr.getDescrs()).hasSize(2);

        final AndDescr leftAnd = (AndDescr) topOr.getDescrs().get(0);
        assertThat(leftAnd.getDescrs()).hasSize(2);
        final PatternDescr foo1 = (PatternDescr) leftAnd.getDescrs().get(0);
        assertThat(foo1.getObjectType()).isEqualTo("Foo");
        final OrDescr leftOr = (OrDescr) leftAnd.getDescrs().get(1);
        assertThat(leftOr.getDescrs()).hasSize(2);
        final PatternDescr bar1 = (PatternDescr) leftOr.getDescrs().get(0);
        assertThat(bar1.getObjectType()).isEqualTo("Bar");
        final PatternDescr foo2 = (PatternDescr) leftOr.getDescrs().get(1);
        assertThat(foo2.getObjectType()).isEqualTo("Foo");

        final OrDescr rightOr = (OrDescr) topOr.getDescrs().get(1);
        assertThat(rightOr.getDescrs()).hasSize(2);
        final PatternDescr bar2 = (PatternDescr) rightOr.getDescrs().get(0);
        assertThat(bar2.getObjectType()).isEqualTo("Bar");
        final AndDescr rightAnd = (AndDescr) rightOr.getDescrs().get(1);
        assertThat(rightAnd.getDescrs()).hasSize(2);
        final PatternDescr foo3 = (PatternDescr) rightAnd.getDescrs().get(0);
        assertThat(foo3.getObjectType()).isEqualTo("Foo");
        final PatternDescr bar3 = (PatternDescr) rightAnd.getDescrs().get(1);
        assertThat(bar3.getObjectType()).isEqualTo("Bar");
    }

    @Test
    void multipleLevelNestWithThreeOrSiblings() throws Exception {
        final String drl = "rule nest_or_siblings\n" +
                "  when\n" +
                "     (A() or (B() or C() or (D() and E())))\n" +
                "  then\n" +
                "end";
        PackageDescr pkg = parser.parse(drl);

        assertThat(pkg.getRules()).hasSize(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        final AndDescr rootAnd = (AndDescr) rule.getLhs();
        assertThat(rootAnd.getDescrs()).hasSize(1);

        final OrDescr topOr = (OrDescr) rootAnd.getDescrs().get(0);
        assertThat(topOr.getDescrs()).hasSize(2);

        final PatternDescr leftPattern = (PatternDescr) topOr.getDescrs().get(0);
        assertThat(leftPattern.getObjectType()).isEqualTo("A");

        final OrDescr rightOr = (OrDescr) topOr.getDescrs().get(1);
        assertThat(rightOr.getDescrs()).as("top level Or has 3 sibling children").hasSize(3);
        final PatternDescr bPattern = (PatternDescr) rightOr.getDescrs().get(0);
        assertThat(bPattern.getObjectType()).isEqualTo("B");
        final PatternDescr cPattern = (PatternDescr) rightOr.getDescrs().get(1);
        assertThat(cPattern.getObjectType()).isEqualTo("C");
        final AndDescr deAnd = (AndDescr) rightOr.getDescrs().get(2);
        assertThat(deAnd.getDescrs()).hasSize(2);

        final PatternDescr dPattern = (PatternDescr) deAnd.getDescrs().get(0);
        assertThat(dPattern.getObjectType()).isEqualTo("D");
        final PatternDescr ePattern = (PatternDescr) deAnd.getDescrs().get(1);
        assertThat(ePattern.getObjectType()).isEqualTo("E");
    }

    @Test
    public void existsMultipleLevelNestWithThreeOrSiblings() throws Exception {
        final String drl = "rule nest_or_siblings\n" +
                "  when\n" +
                "     exists(A() or (B() or C() or (D() and E())))\n" +
                "  then\n" +
                "end";
        PackageDescr pkg = parser.parse(drl);

        assertThat(pkg.getRules()).hasSize(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        final AndDescr rootAnd = (AndDescr) rule.getLhs();
        assertThat(rootAnd.getDescrs()).hasSize(1);

        final ExistsDescr topExists = (ExistsDescr) rootAnd.getDescrs().get(0);
        assertThat(topExists.getDescrs()).hasSize(1);

        final OrDescr topOr = (OrDescr) topExists.getDescrs().get(0);
        assertThat(topOr.getDescrs()).hasSize(2);

        final PatternDescr leftPattern = (PatternDescr) topOr.getDescrs().get(0);
        assertThat(leftPattern.getObjectType()).isEqualTo("A");

        final OrDescr rightOr = (OrDescr) topOr.getDescrs().get(1);
        assertThat(rightOr.getDescrs()).hasSize(3);
        final PatternDescr bPattern = (PatternDescr) rightOr.getDescrs().get(0);
        assertThat(bPattern.getObjectType()).isEqualTo("B");
        final PatternDescr cPattern = (PatternDescr) rightOr.getDescrs().get(1);
        assertThat(cPattern.getObjectType()).isEqualTo("C");
        final AndDescr deAnd = (AndDescr) rightOr.getDescrs().get(2);
        assertThat(deAnd.getDescrs()).hasSize(2);

        final PatternDescr dPattern = (PatternDescr) deAnd.getDescrs().get(0);
        assertThat(dPattern.getObjectType()).isEqualTo("D");
        final PatternDescr ePattern = (PatternDescr) deAnd.getDescrs().get(1);
        assertThat(ePattern.getObjectType()).isEqualTo("E");
    }

    @Test
    public void parse_EvalMultiple() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "eval_multiple.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(4);

        final EvalDescr eval = (EvalDescr) rule.getLhs().getDescrs().get(0 );
        assertThat((String) eval.getContent()).isEqualToIgnoringWhitespace( "abc(\"foo\") + 5");

        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("Foo");

    }

    @Test
    public void parse_WithEval() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "with_eval.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(3);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("Foo");
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("Bar");

        final EvalDescr eval = (EvalDescr) rule.getLhs().getDescrs().get( 2 );
        assertThat((String) eval.getContent()).isEqualToIgnoringWhitespace( "abc(\"foo\")");
        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( "Kapow");
    }

    @Test
    public void parse_WithRetval() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "with_retval.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(col.getConstraint().getDescrs().size()).isEqualTo(1);
        assertThat(col.getObjectType()).isEqualTo("Foo");
        final ExprConstraintDescr fld = (ExprConstraintDescr) col.getConstraint().getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualToIgnoringWhitespace("name== (a + b)");
    }

    @Test
    public void parse_WithPredicate() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "with_predicate.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        AndDescr and = (AndDescr) col.getConstraint();
        assertThat(and.getDescrs().size()).isEqualTo(2);

        final ExprConstraintDescr field = (ExprConstraintDescr) col.getDescrs().get( 0 );
        final ExprConstraintDescr pred = (ExprConstraintDescr) and.getDescrs().get( 1 );
        assertThat(field.getExpression()).isEqualToIgnoringWhitespace("$age2:age");
        assertThat(pred.getExpression()).isEqualToIgnoringWhitespace( "$age2 == $age1+2");
    }

    @Test
    public void parse_NotWithConstraint() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "not_with_constraint.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(2);

        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final ExprConstraintDescr fieldBinding = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fieldBinding.getExpression()).isEqualToIgnoringWhitespace("$likes:like");

        final NotDescr not = (NotDescr) rule.getLhs().getDescrs().get( 1 );
        pattern = (PatternDescr) not.getDescrs().get( 0 );

        final ExprConstraintDescr fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualToIgnoringWhitespace("type == $likes");
    }

    @Disabled("Priority : Mid | Implement Descr lineNumber")
    @Test
    public void parse_Functions() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "functions.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(2);

        final List<FunctionDescr> functions = pkg.getFunctions();
        assertThat(functions.size()).isEqualTo(2);

        FunctionDescr func = functions.get( 0 );
        assertThat(func.getName()).isEqualTo("functionA");
        assertThat(func.getReturnType()).isEqualTo("String");
        assertThat(func.getParameterNames().size()).isEqualTo(2);
        assertThat(func.getParameterTypes().size()).isEqualTo(2);
        assertThat(func.getLine()).isEqualTo(19);
        assertThat(func.getColumn()).isEqualTo(0);

        assertThat(func.getParameterTypes().get(0)).isEqualTo("String");
        assertThat(func.getParameterNames().get(0)).isEqualTo("s");

        assertThat(func.getParameterTypes().get(1)).isEqualTo("Integer");
        assertThat(func.getParameterNames().get(1)).isEqualTo("i");

        assertThat(func.getBody()).isEqualToIgnoringWhitespace( "foo();");

        func = functions.get( 1 );
        assertThat(func.getName()).isEqualTo("functionB");
        assertThat(func.getText()).isEqualToIgnoringWhitespace( "bar();");
    }

    @Test
    public void parse_Comment() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "comment.drl" );

        assertThat(pkg).isNotNull();

        assertThat(pkg.getName()).isEqualTo("foo.bar");
    }

    @Test
    public void parse_Attributes() throws Exception {
        final RuleDescr rule = parseAndGetFirstRuleDescrFromFile(
                                                          "rule_attributes.drl" );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( "bar();");

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(6);

        AttributeDescr at = (AttributeDescr) attrs.get( "salience" );
        assertThat(at.getName()).isEqualTo("salience");
        assertThat(at.getValue()).isEqualTo("42");

        at = (AttributeDescr) attrs.get( "agenda-group" );
        assertThat(at.getName()).isEqualTo("agenda-group");
        assertThat(at.getValue()).isEqualTo("my_group");

        at = (AttributeDescr) attrs.get( "no-loop" );
        assertThat(at.getName()).isEqualTo("no-loop");
        assertThat(at.getValue()).isEqualTo("true");

        at = (AttributeDescr) attrs.get( "duration" );
        assertThat(at.getName()).isEqualTo("duration");
        assertThat(at.getValue()).isEqualTo("42");

        at = (AttributeDescr) attrs.get( "activation-group" );
        assertThat(at.getName()).isEqualTo("activation-group");
        assertThat(at.getValue()).isEqualTo("my_activation_group");

        at = (AttributeDescr) attrs.get( "lock-on-active" );
        assertThat(at.getName()).isEqualTo("lock-on-active");
        assertThat(at.getValue()).isEqualTo("true");
    }

    @Test
    public void parse_Attributes2() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "rule_attributes2.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        List<RuleDescr> rules = pkg.getRules();
        assertThat(rules.size()).isEqualTo(3);

        RuleDescr rule = rules.get( 0 );
        assertThat(rule.getName()).isEqualTo("rule1");
        Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(2);
        AttributeDescr at = (AttributeDescr) attrs.get( "salience" );
        assertThat(at.getName()).isEqualTo("salience");
        assertThat(at.getValue()).isEqualTo("(42)");
        at = (AttributeDescr) attrs.get( "agenda-group" );
        assertThat(at.getName()).isEqualTo("agenda-group");
        assertThat(at.getValue()).isEqualTo("my_group");

        rule = rules.get( 1 );
        assertThat(rule.getName()).isEqualTo("rule2");
        attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(2);
        at = (AttributeDescr) attrs.get( "salience" );
        assertThat(at.getName()).isEqualTo("salience");
        assertThat(at.getValue()).isEqualTo("(Integer.MIN_VALUE)");
        at = (AttributeDescr) attrs.get( "no-loop" );
        assertThat(at.getName()).isEqualTo("no-loop");

        rule = rules.get( 2 );
        assertThat(rule.getName()).isEqualTo("rule3");
        attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(2);
        at = (AttributeDescr) attrs.get( "enabled" );
        assertThat(at.getName()).isEqualTo("enabled");
        assertThat(at.getValue()).isEqualTo("(Boolean.TRUE)");
        at = (AttributeDescr) attrs.get( "activation-group" );
        assertThat(at.getName()).isEqualTo("activation-group");
        assertThat(at.getValue()).isEqualTo("my_activation_group");

    }

    @Test
    public void parse_AttributeRefract() throws Exception {
        final String source = "rule Test refract when Person() then end";

        PackageDescr pkg = parser.parse(
                                                 source );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        assertThat(rule.getName()).isEqualTo("Test");
        Map<String, AttributeDescr> attributes = rule.getAttributes();
        assertThat(attributes.size()).isEqualTo(1);
        AttributeDescr refract = attributes.get( "refract" );
        assertThat(refract).isNotNull();
        assertThat(refract.getValue()).isEqualTo("true");

    }

    @Test
    public void parse_EnabledExpression() throws Exception {
        final RuleDescr rule = parseAndGetFirstRuleDescrFromFile(
                                                          "rule_enabled_expression.drl" );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( "bar();");

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(3);

        AttributeDescr at = (AttributeDescr) attrs.get( "enabled" );
        assertThat(at.getName()).isEqualTo("enabled");
        assertThat(at.getValue()).isEqualTo("( 1 + 1 == 2 )");

        at = (AttributeDescr) attrs.get( "salience" );
        assertThat(at.getName()).isEqualTo("salience");
        assertThat(at.getValue()).isEqualTo("( 1+2 )");

        at = (AttributeDescr) attrs.get( "lock-on-active" );
        assertThat(at.getName()).isEqualTo("lock-on-active");
        assertThat(at.getValue()).isEqualTo("true");
    }

    @Test
    public void parse_DurationExpression() throws Exception {
        final RuleDescr rule = parseAndGetFirstRuleDescrFromFile(
                                                          "rule_duration_expression.drl" );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( "bar();");

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(2);

        AttributeDescr at = (AttributeDescr) attrs.get( "duration" );
        assertThat(at.getName()).isEqualTo("duration");
        assertThat(at.getValue()).isEqualTo("1h30m");

        at = (AttributeDescr) attrs.get( "lock-on-active" );
        assertThat(at.getName()).isEqualTo("lock-on-active");
        assertThat(at.getValue()).isEqualTo("true");
    }

    @Test
    public void parse_Calendars() throws Exception {
        final RuleDescr rule = parseAndGetFirstRuleDescrFromFile(
                                                          "rule_calendars_attribute.drl" );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( "bar();");

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(2);

        AttributeDescr at = (AttributeDescr) attrs.get( "calendars" );
        assertThat(at.getName()).isEqualTo("calendars");
        assertThat(at.getValue()).isEqualTo("[ \"cal1\" ]");

        at = (AttributeDescr) attrs.get( "lock-on-active" );
        assertThat(at.getName()).isEqualTo("lock-on-active");
        assertThat(at.getValue()).isEqualTo("true");
    }

    @Test
    public void parse_Calendars2() throws Exception {
        final RuleDescr rule = parseAndGetFirstRuleDescrFromFile(
                                                          "rule_calendars_attribute2.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( "bar();");

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(2);

        AttributeDescr at = (AttributeDescr) attrs.get( "calendars" );
        assertThat(at.getName()).isEqualTo("calendars");
        assertThat(at.getValue()).isEqualTo("[ \"cal 1\", \"cal 2\", \"cal 3\" ]");

        at = (AttributeDescr) attrs.get( "lock-on-active" );
        assertThat(at.getName()).isEqualTo("lock-on-active");
        assertThat(at.getValue()).isEqualTo("true");
    }

    @Disabled("Priority : Low | Not written in docs nor other unit tests. Drop the support?")
    @Test
    public void parse_Attributes_alternateSyntax() throws Exception {
        final RuleDescr rule = parseAndGetFirstRuleDescrFromFile(
                                                          "rule_attributes_alt.drl" );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( "bar();");

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(6);

        AttributeDescr at = (AttributeDescr) attrs.get( "salience" );
        assertThat(at.getName()).isEqualTo("salience");
        assertThat(at.getValue()).isEqualTo("42");

        at = (AttributeDescr) attrs.get( "agenda-group" );
        assertThat(at.getName()).isEqualTo("agenda-group");
        assertThat(at.getValue()).isEqualTo("my_group");

        at = (AttributeDescr) attrs.get( "no-loop" );
        assertThat(at.getName()).isEqualTo("no-loop");
        assertThat(at.getValue()).isEqualTo("true");

        at = (AttributeDescr) attrs.get( "lock-on-active" );
        assertThat(at.getName()).isEqualTo("lock-on-active");
        assertThat(at.getValue()).isEqualTo("true");

        at = (AttributeDescr) attrs.get( "duration" );
        assertThat(at.getName()).isEqualTo("duration");
        assertThat(at.getValue()).isEqualTo("42");

        at = (AttributeDescr) attrs.get( "activation-group" );
        assertThat(at.getName()).isEqualTo("activation-group");
        assertThat(at.getValue()).isEqualTo("my_activation_group");
    }

    @Test
    public void parse_Enumeration() throws Exception {
        final RuleDescr rule = parseAndGetFirstRuleDescrFromFile(
                                                           "enumeration.drl" );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(col.getObjectType()).isEqualTo("Foo");
        assertThat(col.getConstraint().getDescrs().size()).isEqualTo(1);
        final ExprConstraintDescr fld = (ExprConstraintDescr) col.getConstraint().getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualToIgnoringWhitespace("bar == Foo.BAR");
    }

    @Test
    public void parse_ExtraLhsNewline() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                       "extra_lhs_newline.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
    }

    @Disabled("Priority : Low | Implement soundslike")
    @Test
    public void parse_SoundsLike() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "soundslike_operator.drl" );

        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        PatternDescr pat = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        pat.getConstraint();
    }

    @Test
    public void parse_PackageAttributes() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "package_attributes.drl" );

        AttributeDescr at = (AttributeDescr) pkg.getAttributes().get( 0 );
        assertThat(at.getName()).isEqualTo("agenda-group");
        assertThat(at.getValue()).isEqualTo("x");
        at = (AttributeDescr) pkg.getAttributes().get( 1 );
        assertThat(at.getName()).isEqualTo("dialect");
        assertThat(at.getValue()).isEqualTo("java");

        assertThat(pkg.getRules().size()).isEqualTo(2);

        assertThat(pkg.getImports().size()).isEqualTo(2);

        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getName()).isEqualTo("bar");
        at = (AttributeDescr) rule.getAttributes().get( "agenda-group" );
        assertThat(at.getName()).isEqualTo("agenda-group");
        assertThat(at.getValue()).isEqualTo("x");
        at = (AttributeDescr) rule.getAttributes().get( "dialect" );
        assertThat(at.getName()).isEqualTo("dialect");
        assertThat(at.getValue()).isEqualTo("java");

        rule = (RuleDescr) pkg.getRules().get( 1 );
        assertThat(rule.getName()).isEqualTo("baz");
        at = (AttributeDescr) rule.getAttributes().get( "dialect" );
        assertThat(at.getName()).isEqualTo("dialect");
        assertThat(at.getValue()).isEqualTo("mvel");
        at = (AttributeDescr) rule.getAttributes().get( "agenda-group" );
        assertThat(at.getName()).isEqualTo("agenda-group");
        assertThat(at.getValue()).isEqualTo("x");

    }

    @Test
    public void parse_StatementOrdering1() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "statement_ordering_1.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(2);

        assertThat(((RuleDescr) pkg.getRules().get(0)).getName()).isEqualTo("foo");
        assertThat(((RuleDescr) pkg.getRules().get(1)).getName()).isEqualTo("bar");

        assertThat(pkg.getFunctions().size()).isEqualTo(2);

        assertThat(((FunctionDescr) pkg.getFunctions().get(0)).getName()).isEqualTo("cheeseIt");
        assertThat(((FunctionDescr) pkg.getFunctions().get(1)).getName()).isEqualTo("uncheeseIt");

        assertThat(pkg.getImports().size()).isEqualTo(4);
        assertThat(((ImportDescr) pkg.getImports().get(0)).getTarget()).isEqualTo("im.one");
        assertThat(((ImportDescr) pkg.getImports().get(1)).getTarget()).isEqualTo("im.two");
        assertThat(((ImportDescr) pkg.getImports().get(2)).getTarget()).isEqualTo("im.three");
        assertThat(((ImportDescr) pkg.getImports().get(3)).getTarget()).isEqualTo("im.four");
    }

    @Test
    public void parse_RuleNamesStartingWithNumbers() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "rule_names_number_prefix.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(2);

        assertThat(((RuleDescr) pkg.getRules().get(0)).getName()).isEqualTo("1. Do Stuff!");
        assertThat(((RuleDescr) pkg.getRules().get(1)).getName()).isEqualTo("2. Do More Stuff!");
    }

    @Test
    public void parse_EvalWithNewline() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                                 "eval_with_newline.drl");
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
    }

    @Disabled("Priority : Mid | implement Descr lineNumber")
    @Test
    public void parse_EndPosition() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "test_EndPosition.drl" );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(col.getLine()).isEqualTo(21);
        assertThat(col.getEndLine()).isEqualTo(23);
    }

    @Test
    public void parse_QualifiedClassname() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "qualified_classname.drl" );

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        final PatternDescr p = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        assertThat(p.getObjectType()).isEqualTo("com.cheeseco.Cheese");
    }

    @Test
    public void parse_Accumulate() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "accumulate.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accum = (AccumulateDescr) outPattern.getSource();
        assertThat(accum.getInitCode()).isEqualToIgnoringWhitespace( "int x = 0 ;");
        assertThat(accum.getActionCode()).isEqualToIgnoringWhitespace( "x++;");
        assertThat(accum.getReverseCode()).isNull();
        assertThat(accum.getResultCode()).isEqualToIgnoringWhitespace( "new Integer(x)");

        assertThat(accum.isExternalFunction()).isFalse();

        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
    }

    @Test
    public void parse_AccumulateWithBindings() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "accumulate_with_bindings.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accum = (AccumulateDescr) outPattern.getSource();
        assertThat(outPattern.getIdentifier()).isEqualToIgnoringWhitespace( "$counter");
        assertThat(accum.getInitCode()).isEqualToIgnoringWhitespace( "int x = 0 ;");
        assertThat(accum.getActionCode()).isEqualToIgnoringWhitespace( "x++;");
        assertThat(accum.getResultCode()).isEqualToIgnoringWhitespace( "new Integer(x)");

        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
    }

    @Test
    public void parse_Collect() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "collect.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final CollectDescr collect = (CollectDescr) outPattern.getSource();

        final PatternDescr pattern = (PatternDescr) collect.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
    }

    @Test
    public void parse_Predicate2() throws Exception {
        // predicates are also prefixed by the eval keyword
        final RuleDescr rule = parseAndGetFirstRuleDescr(
                                                  "rule X when Foo(eval( $var.equals(\"xyz\") )) then end" );

        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final List< ? > constraints = pattern.getConstraint().getDescrs();
        assertThat(constraints.size()).isEqualTo(1);

        final ExprConstraintDescr predicate = (ExprConstraintDescr) constraints.get( 0 );
        assertThat(predicate.getExpression()).isEqualToIgnoringWhitespace("eval( $var.equals(\"xyz\") )");
    }

    @Test
    public void parse_EscapedStrings() throws Exception {
        final RuleDescr rule = parseAndGetFirstRuleDescrFromFile(
                                                          "escaped-string.drl" );

        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("test_Quotes");

        final String expected = "String s = \"\\\"\\n\\t\\\\\";";

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( expected);
    }

    @Test
    public void parse_NestedCEs() throws Exception {
        final RuleDescr rule = parseAndGetFirstRuleDescrFromFile(
                                                          "nested_conditional_elements.drl" );

        assertThat(rule).isNotNull();

        final AndDescr root = rule.getLhs();
        final NotDescr not1 = (NotDescr) root.getDescrs().get( 0 );
        final AndDescr and1 = (AndDescr) not1.getDescrs().get( 0 );

        final PatternDescr state = (PatternDescr) and1.getDescrs().get( 0 );
        final NotDescr not2 = (NotDescr) and1.getDescrs().get( 1 );
        final AndDescr and2 = (AndDescr) not2.getDescrs().get( 0 );
        final PatternDescr person = (PatternDescr) and2.getDescrs().get( 0 );
        final PatternDescr cheese = (PatternDescr) and2.getDescrs().get( 1 );

        final PatternDescr person2 = (PatternDescr) root.getDescrs().get( 1 );
        final OrDescr or = (OrDescr) root.getDescrs().get( 2 );
        final PatternDescr cheese2 = (PatternDescr) or.getDescrs().get( 0 );
        final PatternDescr cheese3 = (PatternDescr) or.getDescrs().get( 1 );

        assertThat("State").isEqualTo(state.getObjectType());
        assertThat("Person").isEqualTo(person.getObjectType());
        assertThat("Cheese").isEqualTo(cheese.getObjectType());
        assertThat("Person").isEqualTo(person2.getObjectType());
        assertThat("Cheese").isEqualTo(cheese2.getObjectType());
        assertThat("Cheese").isEqualTo(cheese3.getObjectType());
    }

    @Test
    public void parse_Forall() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "forall.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final ForallDescr forall = (ForallDescr) rule.getLhs().getDescrs().get(0 );

        assertThat(forall.getDescrs().size()).isEqualTo(2);
        final PatternDescr pattern = forall.getBasePattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
        final List<BaseDescr> remaining = forall.getRemainingPatterns();
        assertThat(remaining.size()).isEqualTo(1);
        final PatternDescr cheese = (PatternDescr) remaining.get( 0 );
        assertThat(cheese.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    public void parse_ForallWithFrom() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "forallwithfrom.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final ForallDescr forall = (ForallDescr) rule.getLhs().getDescrs().get( 0 );

        assertThat(forall.getDescrs().size()).isEqualTo(2);
        final PatternDescr pattern = forall.getBasePattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
        assertThat(((FromDescr) pattern.getSource()).getDataSource().toString()).isEqualTo("$village");
        final List<BaseDescr> remaining = forall.getRemainingPatterns();
        assertThat(remaining.size()).isEqualTo(1);
        final PatternDescr cheese = (PatternDescr) remaining.get( 0 );
        assertThat(cheese.getObjectType()).isEqualTo("Cheese");
        assertThat(((FromDescr) cheese.getSource()).getDataSource().toString()).isEqualTo("$cheesery");
    }

    @Test
    public void parse_Memberof() throws Exception {
        final String text = "rule X when Country( $cities : city )\nPerson( city memberOf $cities )\n then end";
        AndDescr descrs = parseAndGetFirstRuleDescr(
                                              text).getLhs();

        assertThat(descrs.getDescrs().size()).isEqualTo(2);
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 1 );
        ExprConstraintDescr fieldConstr = (ExprConstraintDescr) pat.getConstraint().getDescrs().get( 0 );

        assertThat(fieldConstr.getExpression()).isEqualTo("city memberOf $cities");
    }

    @Test
    public void parse_NotMemberof() throws Exception {
        final String text = "rule X when Country( $cities : city )\nPerson( city not memberOf $cities ) then end\n";
        AndDescr descrs = parseAndGetFirstRuleDescr(
                                              text).getLhs();

        assertThat(descrs.getDescrs().size()).isEqualTo(2);
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 1 );
        ExprConstraintDescr fieldConstr = (ExprConstraintDescr) pat.getConstraint().getDescrs().get( 0 );

        assertThat(fieldConstr.getExpression()).isEqualTo("city not memberOf $cities");
    }

    @Test
    public void parse_InOperator() throws Exception {
        final RuleDescr rule = parseAndGetFirstRuleDescrFromFile(
                                                          "in_operator_test.drl" );

        assertThat(rule).isNotNull();

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( "consequence();");
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(2);

        // The first pattern, with 2 restrictions on a single field (plus a
        // connective)
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("Person");
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(1);

        ExprConstraintDescr fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertThat(fld.getExpression()).isEqualTo("age > 30 && < 40");

        // the second col, with 2 fields, the first with 2 restrictions, the
        // second field with one
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("Vehicle");
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(2);

        fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertThat(fld.getExpression()).isEqualToIgnoringWhitespace("type in ( \"sedan\", \"wagon\" )");

        // now the second field
        fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 1 );
        assertThat(fld.getExpression()).isEqualTo("age < 3");

    }

    @Test
    public void parse_NotInOperator() throws Exception {
        final RuleDescr rule = parseAndGetFirstRuleDescrFromFile(
                                                          "notin_operator_test.drl" );

        assertThat(rule).isNotNull();

        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( "consequence();");
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(2);

        // The first pattern, with 2 restrictions on a single field (plus a
        // connective)
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("Person");
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(1);

        ExprConstraintDescr fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertThat(fld.getExpression()).isEqualTo("age > 30 && < 40");

        // the second col, with 2 fields, the first with 2 restrictions, the
        // second field with one
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("Vehicle");
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(2);

        fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertThat(fld.getExpression()).isEqualToIgnoringWhitespace("type not in ( \"sedan\", \"wagon\" )");

        // now the second field
        fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 1 );
        assertThat(fld.getExpression()).isEqualTo("age < 3");

    }

    @Test
    public void parse_CheckOrDescr() throws Exception {
        final String text = "rule X when Person( eval( age == 25 ) || ( eval( name.equals( \"bob\" ) ) && eval( age == 30 ) ) ) then end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(
                                                                  text ).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        assertThat(AndDescr.class).isEqualTo(pattern.getConstraint().getClass());

        assertThat(pattern.getConstraint().getDescrs().get(0).getClass()).isEqualTo(ExprConstraintDescr.class);

    }

    @Test
    public void parse_ConstraintAndConnective() throws Exception {
        final String text = "rule X when Person( age < 42 && location==\"atlanta\") then end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(
                                                                  text ).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualToIgnoringWhitespace("age < 42 && location==\"atlanta\"");
    }

    @Test
    public void parse_ConstraintOrConnective() throws Exception {
        final String text = "rule X when Person( age < 42 || location==\"atlanta\") then end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(
                                                                  text ).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualToIgnoringWhitespace("age < 42 || location==\"atlanta\"");
    }

    @Test
    public void parse_Restrictions() throws Exception {
        final String text = "rule X when Foo( bar > 1 || == 1 ) then end\n";

        AndDescr descrs = (AndDescr) parseAndGetFirstRuleDescr(
                                                         text ).getLhs();

        assertThat(descrs.getDescrs().size()).isEqualTo(1);
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 0 );
        ExprConstraintDescr fieldConstr = (ExprConstraintDescr) pat.getConstraint().getDescrs().get( 0 );

        assertThat(fieldConstr.getExpression()).isEqualTo("bar > 1 || == 1");
    }

    @Test
    public void parse_Semicolon() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "semicolon.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(pkg.getName()).isEqualTo("org.drools.mvel.compiler");
        assertThat(pkg.getGlobals().size()).isEqualTo(1);
        assertThat(pkg.getRules().size()).isEqualTo(3);

        final RuleDescr rule1 = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule1.getLhs().getDescrs().size()).isEqualTo(2);

        final RuleDescr query1 = (RuleDescr) pkg.getRules().get( 1 );
        assertThat(query1.getLhs().getDescrs().size()).isEqualTo(3);

        final RuleDescr rule2 = (RuleDescr) pkg.getRules().get( 2 );
        assertThat(rule2.getLhs().getDescrs().size()).isEqualTo(2);
    }

    @Test
    public void parse_Eval() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "eval_parsing.drl" );

        assertThat(pkg.getName()).isEqualTo("org.drools.mvel.compiler");
        assertThat(pkg.getRules().size()).isEqualTo(1);

        final RuleDescr rule1 = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule1.getLhs().getDescrs().size()).isEqualTo(1);
    }

    @Test
    public void parse_AccumulateReverse() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "accumulateReverse.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertThat(accum.getInitCode()).isEqualToIgnoringWhitespace( "int x = 0 ;"
                                       );
        assertThat(accum.getActionCode()).isEqualToIgnoringWhitespace( "x++;"
                                       );
        assertThat(accum.getReverseCode()).isEqualToIgnoringWhitespace( "x--;"
                                       );
        assertThat(accum.getResultCode()).isEqualToIgnoringWhitespace( "new Integer(x)"
                                       );
        assertThat(accum.isExternalFunction()).isFalse();

        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
    }

    @Test
    public void parse_AccumulateExternalFunction() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "accumulateExternalFunction.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertThat(accum.getFunctions().get( 0 ).getParams()[0]).isEqualToIgnoringWhitespace( "$age"
                                       );
        assertThat(accum.getFunctions().get( 0 ).getFunction()).isEqualToIgnoringWhitespace( "average"
                                       );
        assertThat(accum.isExternalFunction()).isTrue();

        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
    }

    @Test
    public void parse_CollectWithNestedFrom() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "collect_with_nested_from.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final CollectDescr collect = (CollectDescr) out.getSource();

        PatternDescr person = (PatternDescr) collect.getInputPattern();
        assertThat(person.getObjectType()).isEqualTo("Person");

        final CollectDescr collect2 = (CollectDescr) person.getSource();

        final PatternDescr people = collect2.getInputPattern();
        assertThat(people.getObjectType()).isEqualTo("People");
    }

    @Test
    public void parse_AccumulateWithNestedFrom() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "accumulate_with_nested_from.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accumulate = (AccumulateDescr) out.getSource();

        PatternDescr person = (PatternDescr) accumulate.getInputPattern();
        assertThat(person.getObjectType()).isEqualTo("Person");

        final CollectDescr collect2 = (CollectDescr) person.getSource();

        final PatternDescr people = collect2.getInputPattern();
        assertThat(people.getObjectType()).isEqualTo("People");
    }

    @Test
    public void parse_AccumulateMultipleFunctions() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "accumulateMultipleFunctions.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);

        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(out.getObjectType()).isEqualTo("Object");
        AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertThat(accum.isExternalFunction()).isTrue();

        List<AccumulateDescr.AccumulateFunctionCallDescr> functions = accum.getFunctions();
        assertThat(functions.size()).isEqualTo(3);
        assertThat(functions.get(0).getFunction()).isEqualTo("average");
        assertThat(functions.get(0).getBind()).isEqualTo("$a1");
        assertThat(functions.get(0).getParams()[0]).isEqualTo("$price");

        assertThat(functions.get(1).getFunction()).isEqualTo("min");
        assertThat(functions.get(1).getBind()).isEqualTo("$m1");
        assertThat(functions.get(1).getParams()[0]).isEqualTo("$price");

        assertThat(functions.get(2).getFunction()).isEqualTo("max");
        assertThat(functions.get(2).getBind()).isEqualTo("$M1");
        assertThat(functions.get(2).getParams()[0]).isEqualTo("$price");

        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    public void parse_AccumulateMnemonic() throws Exception {
        String drl = "package org.drools.mvel.compiler\n" +
                "rule \"Accumulate 1\"\n" +
                "when\n" +
                "     acc( Cheese( $price : price ),\n" +
                "          $a1 : average( $price ) )\n" +
                "then\n" +
                "end\n";
        PackageDescr pkg = parser.parse(
                                                       drl );

        assertThat(pkg.getRules().size()).isEqualTo(1);

        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(out.getObjectType()).isEqualTo("Object");
        AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertThat(accum.isExternalFunction()).isTrue();

        List<AccumulateDescr.AccumulateFunctionCallDescr> functions = accum.getFunctions();
        assertThat(functions.size()).isEqualTo(1);
        assertThat(functions.get(0).getFunction()).isEqualTo("average");
        assertThat(functions.get(0).getBind()).isEqualTo("$a1");
        assertThat(functions.get(0).getParams()[0]).isEqualTo("$price");

        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    public void parse_AccumulateMnemonic2() throws Exception {
        String drl = "package org.drools.mvel.compiler\n" +
                "rule \"Accumulate 1\"\n" +
                "when\n" +
                "     Number() from acc( Cheese( $price : price ),\n" +
                "                        average( $price ) )\n" +
                "then\n" +
                "end\n";
        PackageDescr pkg = parser.parse(
                                                       drl );

        assertThat(pkg.getRules().size()).isEqualTo(1);

        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(out.getObjectType()).isEqualTo("Number");
        AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertThat(accum.isExternalFunction()).isTrue();

        List<AccumulateDescr.AccumulateFunctionCallDescr> functions = accum.getFunctions();
        assertThat(functions.size()).isEqualTo(1);
        assertThat(functions.get(0).getFunction()).isEqualTo("average");
        assertThat(functions.get(0).getParams()[0]).isEqualTo("$price");

        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    public void parse_ImportAccumulate() throws Exception {
        String drl = "package org.drools.mvel.compiler\n" +
                "import acc foo.Bar baz\n" +
                "import accumulate foo.Bar2 baz2\n" +
                "rule \"Accumulate 1\"\n" +
                "when\n" +
                "     acc( Cheese( $price : price ),\n" +
                "          $v1 : baz( $price ), \n" +
                "          $v2 : baz2( $price ) )\n" +
                "then\n" +
                "end\n";
        PackageDescr pkg = parser.parse(
                                                       drl );

        assertThat(pkg.getAccumulateImports().size()).isEqualTo(2);
        AccumulateImportDescr imp = (AccumulateImportDescr) pkg.getAccumulateImports().get(0);
        assertThat(imp.getTarget()).isEqualTo("foo.Bar");
        assertThat(imp.getFunctionName()).isEqualTo("baz");

        imp = (AccumulateImportDescr) pkg.getAccumulateImports().get(1);
        assertThat(imp.getTarget()).isEqualTo("foo.Bar2");
        assertThat(imp.getFunctionName()).isEqualTo("baz2");

        assertThat(pkg.getRules().size()).isEqualTo(1);

        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(out.getObjectType()).isEqualTo("Object");
        AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertThat(accum.isExternalFunction()).isTrue();

        List<AccumulateDescr.AccumulateFunctionCallDescr> functions = accum.getFunctions();
        assertThat(functions.size()).isEqualTo(2);
        assertThat(functions.get(0).getFunction()).isEqualTo("baz");
        assertThat(functions.get(0).getBind()).isEqualTo("$v1");
        assertThat(functions.get(0).getParams()[0]).isEqualTo("$price");

        assertThat(functions.get(1).getFunction()).isEqualTo("baz2");
        assertThat(functions.get(1).getBind()).isEqualTo("$v2");
        assertThat(functions.get(1).getParams()[0]).isEqualTo("$price");

        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    public void parse_AccumulateMultipleFunctionsConstraint() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "accumulateMultipleFunctionsConstraint.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);

        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(out.getObjectType()).isEqualTo("Object");
        assertThat(out.getConstraint().getDescrs().size()).isEqualTo(2);
        assertThat(out.getConstraint().getDescrs().get(0).toString()).isEqualTo("$a1 > 10 && $M1 <= 100");
        assertThat(out.getConstraint().getDescrs().get(1).toString()).isEqualTo("$m1 == 5");
        AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertThat(accum.isExternalFunction()).isTrue();

        List<AccumulateDescr.AccumulateFunctionCallDescr> functions = accum.getFunctions();
        assertThat(functions.size()).isEqualTo(3);
        assertThat(functions.get(0).getFunction()).isEqualTo("average");
        assertThat(functions.get(0).getBind()).isEqualTo("$a1");
        assertThat(functions.get(0).getParams()[0]).isEqualTo("$price");

        assertThat(functions.get(1).getFunction()).isEqualTo("min");
        assertThat(functions.get(1).getBind()).isEqualTo("$m1");
        assertThat(functions.get(1).getParams()[0]).isEqualTo("$price");

        assertThat(functions.get(2).getFunction()).isEqualTo("max");
        assertThat(functions.get(2).getBind()).isEqualTo("$M1");
        assertThat(functions.get(2).getParams()[0]).isEqualTo("$price");

        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    public void parse_OrCE() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "or_ce.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(2);

        final PatternDescr person = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(person.getObjectType()).isEqualTo("Person");
        assertThat(person.getIdentifier()).isEqualTo("$p");

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(or.getDescrs().size()).isEqualTo(2);

        final PatternDescr cheese1 = (PatternDescr) or.getDescrs().get( 0 );
        assertThat(cheese1.getObjectType()).isEqualTo("Cheese");
        assertThat(cheese1.getIdentifier()).isEqualTo("$c");
        final PatternDescr cheese2 = (PatternDescr) or.getDescrs().get( 1 );
        assertThat(cheese2.getObjectType()).isEqualTo("Cheese");
        assertThat(cheese2.getIdentifier()).isNull();
    }

    @Test
    public void parse_RuleSingleLine() throws Exception {
        final String text = "rule \"another test\" salience 10 when eval( true ) then System.out.println(1); end";
        RuleDescr rule = parseAndGetFirstRuleDescr(
                                            text );

        assertThat(rule.getName()).isEqualTo("another test");
        assertThat((String)rule.getConsequence()).isEqualToIgnoringWhitespace("System.out.println(1); ");
    }

    @Test
    public void parse_RuleTwoLines() throws Exception {
        final String text = "rule \"another test\" salience 10 when eval( true ) then System.out.println(1);\n end";
        RuleDescr rule = parseAndGetFirstRuleDescr(
                                            text );

        assertThat(rule.getName()).isEqualTo("another test");
        assertThat((String)rule.getConsequence()).isEqualToIgnoringWhitespace("System.out.println(1);\n ");
    }

    @Test
    public void parse_RuleParseLhs3() throws Exception {
        final String text = "rule X when (or\nnot Person()\n(and Cheese()\nMeat()\nWine())) then end";
        AndDescr pattern = parseAndGetFirstRuleDescr(
                                               text ).getLhs();

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        OrDescr or = (OrDescr) pattern.getDescrs().get( 0 );
        assertThat(or.getDescrs().size()).isEqualTo(2);
        NotDescr not = (NotDescr) or.getDescrs().get( 0 );
        AndDescr and = (AndDescr) or.getDescrs().get( 1 );
        assertThat(not.getDescrs().size()).isEqualTo(1);
        PatternDescr person = (PatternDescr) not.getDescrs().get( 0 );
        assertThat(person.getObjectType()).isEqualTo("Person");
        assertThat(and.getDescrs().size()).isEqualTo(3);
        PatternDescr cheese = (PatternDescr) and.getDescrs().get( 0 );
        assertThat(cheese.getObjectType()).isEqualTo("Cheese");
        PatternDescr meat = (PatternDescr) and.getDescrs().get( 1 );
        assertThat(meat.getObjectType()).isEqualTo("Meat");
        PatternDescr wine = (PatternDescr) and.getDescrs().get( 2 );
        assertThat(wine.getObjectType()).isEqualTo("Wine");

    }

    @Test
    public void parse_AccumulateMultiPattern() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "accumulate_multi_pattern.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accum = (AccumulateDescr) outPattern.getSource();
        assertThat(outPattern.getIdentifier()).isEqualToIgnoringWhitespace( "$counter"
                                       );
        assertThat(accum.getInitCode()).isEqualToIgnoringWhitespace( "int x = 0 ;"
                                       );
        assertThat(accum.getActionCode()).isEqualToIgnoringWhitespace( "x++;"
                                      );
        assertThat(accum.getResultCode()).isEqualToIgnoringWhitespace( "new Integer(x)"
                                       );

        final AndDescr and = (AndDescr) accum.getInput();
        assertThat(and.getDescrs().size()).isEqualTo(2);
        final PatternDescr person = (PatternDescr) and.getDescrs().get( 0 );
        final PatternDescr cheese = (PatternDescr) and.getDescrs().get( 1 );
        assertThat(person.getObjectType()).isEqualTo("Person");
        assertThat(cheese.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    public void parse_PluggableOperators() throws Exception {

        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "pluggable_operators.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(5);

        final PatternDescr eventA = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(eventA.getIdentifier()).isEqualTo("$a");
        assertThat(eventA.getObjectType()).isEqualTo("EventA");

        final PatternDescr eventB = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(eventB.getIdentifier()).isEqualTo("$b");
        assertThat(eventB.getObjectType()).isEqualTo("EventB");
        assertThat(eventB.getConstraint().getDescrs().size()).isEqualTo(1);
        assertThat(eventB.getConstraint().getDescrs().size()).isEqualTo(1);

        final ExprConstraintDescr fcdB = (ExprConstraintDescr) eventB.getConstraint().getDescrs().get( 0 );
        assertThat(fcdB.getExpression()).isEqualTo("this after[1,10] $a || this not after[15,20] $a");

        final PatternDescr eventC = (PatternDescr) rule.getLhs().getDescrs().get( 2 );
        assertThat(eventC.getIdentifier()).isEqualTo("$c");
        assertThat(eventC.getObjectType()).isEqualTo("EventC");
        assertThat(eventC.getConstraint().getDescrs().size()).isEqualTo(1);
        final ExprConstraintDescr fcdC = (ExprConstraintDescr) eventC.getConstraint().getDescrs().get( 0 );
        assertThat(fcdC.getExpression()).isEqualTo("this finishes $b");

        final PatternDescr eventD = (PatternDescr) rule.getLhs().getDescrs().get( 3 );
        assertThat(eventD.getIdentifier()).isEqualTo("$d");
        assertThat(eventD.getObjectType()).isEqualTo("EventD");
        assertThat(eventD.getConstraint().getDescrs().size()).isEqualTo(1);
        final ExprConstraintDescr fcdD = (ExprConstraintDescr) eventD.getConstraint().getDescrs().get( 0 );
        assertThat(fcdD.getExpression()).isEqualTo("this not starts $a");

        final PatternDescr eventE = (PatternDescr) rule.getLhs().getDescrs().get( 4 );
        assertThat(eventE.getIdentifier()).isEqualTo("$e");
        assertThat(eventE.getObjectType()).isEqualTo("EventE");
        assertThat(eventE.getConstraint().getDescrs().size()).isEqualTo(1);

        ExprConstraintDescr fcdE = (ExprConstraintDescr) eventE.getConstraint().getDescrs().get( 0 );
        assertThat(fcdE.getExpression()).isEqualTo("this not before[1, 10] $b || after[1, 10] $c && this after[1, 5] $d");
    }

    @Test
    public void parse_RuleMetadata() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "Rule_with_Metadata.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        // @fooAttribute(barValue)
        // @fooAtt2(barVal2)
        RuleDescr rule = pkg.getRules().get( 0 );
        assertThat(rule.getAnnotationNames().contains("fooMeta1")).isTrue();
        assertThat(rule.getAnnotation("fooMeta1").getValue()).isEqualTo("barVal1");
        assertThat(rule.getAnnotationNames().contains("fooMeta2")).isTrue();
        assertThat(rule.getAnnotation("fooMeta2").getValue()).isEqualTo("barVal2");
        assertThat((String) rule.getConsequence()).isEqualToIgnoringWhitespace( "System.out.println(\"Consequence\");"
                                      );
    }

    @Test
    public void parse_RuleExtends() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "Rule_with_Extends.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get( 0 );
        assertThat(rule.getParentName() != null).isTrue();
        assertThat(rule.getParentName()).isEqualTo("rule1");

        AndDescr lhs = rule.getLhs();
        assertThat(lhs).isNotNull();
        assertThat(lhs.getDescrs().size()).isEqualTo(1);

        PatternDescr pattern = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("foo");
        assertThat(pattern.getIdentifier()).isEqualTo("$foo");

    }

    @Test
    public void parse_TypeDeclarationWithFields() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                               "declare_type_with_fields.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        List<TypeDeclarationDescr> td = pkg.getTypeDeclarations();
        assertThat(td.size()).isEqualTo(3);

        TypeDeclarationDescr d = td.get( 0 );
        assertThat(d.getTypeName()).isEqualTo("SomeFact");
        assertThat(d.getFields().size()).isEqualTo(2);
        assertThat(d.getFields().containsKey("name")).isTrue();
        assertThat(d.getFields().containsKey("age")).isTrue();

        TypeFieldDescr f = d.getFields().get("name" );
        assertThat(f.getPattern().getObjectType()).isEqualTo("String");

        f = d.getFields().get( "age" );
        assertThat(f.getPattern().getObjectType()).isEqualTo("Integer");

        d = td.get( 1 );
        assertThat(d.getTypeName()).isEqualTo("AnotherFact");

        TypeDeclarationDescr type = td.get( 2 );
        assertThat(type.getTypeName()).isEqualTo("Person");

        assertThat(type.getAnnotation("role").getValue()).isEqualTo("fact");
        assertThat(type.getAnnotation("doc").getValue("descr")).isEqualTo("\"Models a person\"");
        assertThat(type.getAnnotation("doc").getValue("author")).isEqualTo("\"Bob\"");
        assertThat(type.getAnnotation("doc").getValue("date")).isEqualTo("Calendar.getInstance().getDate()");

        assertThat(type.getFields().size()).isEqualTo(2);
        TypeFieldDescr field = type.getFields().get( "name" );
        assertThat(field.getFieldName()).isEqualTo("name");
        assertThat(field.getPattern().getObjectType()).isEqualTo("String");
        assertThat(field.getInitExpr()).isEqualTo("\"John Doe\"");
        assertThat(field.getAnnotation("length").getValue("max")).isEqualTo("50");
        assertThat( field.getAnnotation( "key" ) ).isNotNull();

        field = type.getFields().get( "age" );
        assertThat(field.getFieldName()).isEqualTo("age");
        assertThat(field.getPattern().getObjectType()).isEqualTo("int");
        assertThat(field.getInitExpr()).isEqualTo("-1");
        assertThat(field.getAnnotation("ranged").getValue("min")).isEqualTo("0");
        assertThat(field.getAnnotation("ranged").getValue("max")).isEqualTo("150");
        assertThat(field.getAnnotation("ranged").getValue("unknown")).isEqualTo("-1");

    }

    @Test
    public void parenthesesOneLevelNestWithThreeSiblings() throws Exception {
        final PackageDescr pkg = parseAndGetPackageDescrFromFile( "Rule_with_nested_LHS.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get( 0 );
        assertThat(rule.getName()).isEqualTo("test");

        AndDescr lhs = rule.getLhs();
        assertThat(lhs).isNotNull();
        assertThat(lhs.getDescrs().size()).isEqualTo(2);

        PatternDescr a = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(a.getObjectType()).isEqualTo("A");

        OrDescr or = (OrDescr) lhs.getDescrs().get( 1 );
        assertThat(or.getDescrs().size()).isEqualTo(3);

        AndDescr and1 = (AndDescr) or.getDescrs().get( 0 );
        assertThat(and1.getDescrs().size()).isEqualTo(2);
        PatternDescr b = (PatternDescr) and1.getDescrs().get( 0 );
        PatternDescr c = (PatternDescr) and1.getDescrs().get( 1 );
        assertThat(b.getObjectType()).isEqualTo("B");
        assertThat(c.getObjectType()).isEqualTo("C");

        AndDescr and2 = (AndDescr) or.getDescrs().get( 1 );
        assertThat(and2.getDescrs().size()).isEqualTo(2);
        PatternDescr d = (PatternDescr) and2.getDescrs().get( 0 );
        PatternDescr e = (PatternDescr) and2.getDescrs().get( 1 );
        assertThat(d.getObjectType()).isEqualTo("D");
        assertThat(e.getObjectType()).isEqualTo("E");

        AndDescr and3 = (AndDescr) or.getDescrs().get( 2 );
        assertThat(and3.getDescrs().size()).isEqualTo(2);
        PatternDescr f = (PatternDescr) and3.getDescrs().get( 0 );
        PatternDescr g = (PatternDescr) and3.getDescrs().get( 1 );
        assertThat(f.getObjectType()).isEqualTo("F");
        assertThat(g.getObjectType()).isEqualTo("G");
    }

    @Test
    public void parse_EntryPoint() throws Exception {
        final String text = "rule X when StockTick( symbol==\"ACME\") from entry-point StreamA then end";

        PackageDescr pkg = parser.parse(
                                                 text );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get( 0 );
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("symbol==\"ACME\"");

        assertThat(pattern.getSource()).isNotNull();
        EntryPointDescr entry = (EntryPointDescr) pattern.getSource();
        assertThat(entry.getEntryId()).isEqualTo("StreamA");
    }

    @Test
    public void parse_EntryPoint2() throws Exception {
        final String text = "rule X when StockTick( symbol==\"ACME\") from entry-point \"StreamA\" then end";

        PackageDescr pkg = parser.parse(
                                                 text );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get( 0 );
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("symbol==\"ACME\"");

        assertThat(pattern.getSource()).isNotNull();
        EntryPointDescr entry = (EntryPointDescr) pattern.getSource();
        assertThat(entry.getEntryId()).isEqualTo("StreamA");
    }

    @Test
    public void parse_SlidingWindow() throws Exception {
        final String text = "rule X when StockTick( symbol==\"ACME\") over window:length(10) then end";

        PackageDescr pkg = parser.parse( text );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get( 0 );
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("symbol==\"ACME\"");

        List<BehaviorDescr> behaviors = pattern.getBehaviors();
        assertThat(behaviors).isNotNull();
        assertThat(behaviors.size()).isEqualTo(1);
        BehaviorDescr descr = behaviors.get( 0 );
        assertThat(descr.getType()).isEqualTo("window");
        assertThat(descr.getSubType()).isEqualTo("length");
        assertThat(descr.getParameters().get(0)).isEqualTo("10");
    }

    @Test
    public void parse_RuleOldSyntax1() throws Exception {
        final String source = "rule \"Test\" when ( not $r :LiteralRestriction( operator == Operator.EQUAL ) ) then end";

        PackageDescr pkg = parser.parse(
                                                 source );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        assertThat(rule.getName()).isEqualTo("Test");
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);
        assertThat(((NotDescr) rule.getLhs().getDescrs().get(0)).getDescrs().size()).isEqualTo(1);
        NotDescr notDescr = (NotDescr) rule.getLhs().getDescrs().get( 0 );
        PatternDescr patternDescr = (PatternDescr) notDescr.getDescrs().get( 0 );
        assertThat(patternDescr.getIdentifier()).isEqualTo("$r");
        assertThat(patternDescr.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fieldConstraintDescr = (ExprConstraintDescr) patternDescr.getDescrs().get( 0 );
        assertThat(fieldConstraintDescr.getExpression()).isEqualToIgnoringWhitespace("operator == Operator.EQUAL");
    }

    @Test
    public void parse_RuleOldSyntax2() throws Exception {
        final String source = "rule \"Test\" when ( $r :LiteralRestriction( operator == Operator.EQUAL ) ) then end";

        PackageDescr pkg = parser.parse(
                                                 source );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        assertThat(rule.getName()).isEqualTo("Test");
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);
        PatternDescr patternDescr = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(patternDescr.getIdentifier()).isEqualTo("$r");
        assertThat(patternDescr.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fieldConstraintDescr = (ExprConstraintDescr) patternDescr.getDescrs().get( 0 );
        assertThat(fieldConstraintDescr.getExpression()).isEqualToIgnoringWhitespace("operator == Operator.EQUAL");
    }

    @Test
    public void parse_TypeWithMetaData() throws Exception {

        PackageDescr pkg = parseAndGetPackageDescrFromFile(
                                                         "type_with_meta.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        final List<TypeDeclarationDescr> declarations = pkg.getTypeDeclarations();

        assertThat(declarations.size()).isEqualTo(3);
    }

    @Test
    public void parse_NullConstraints() throws Exception {
        final String text = "rule X when Person( name == null ) then end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(
                                                                  text ).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("name == null");
        assertThat(fcd.getPosition()).isEqualTo(0);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
    }

    @Test
    public void parse_PositionalConstraintsOnly() throws Exception {
        final String text = "rule X when Person( \"Mark\", 42; ) then end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(
                                                                  text ).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(2);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("\"Mark\"");
        assertThat(fcd.getPosition()).isEqualTo(0);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.POSITIONAL);
        fcd = (ExprConstraintDescr) pattern.getDescrs().get( 1 );
        assertThat(fcd.getExpression()).isEqualTo("42");
        assertThat(fcd.getPosition()).isEqualTo(1);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.POSITIONAL);
    }

    @Test
    public void parse_IsQuery() throws Exception {
        final String text = "rule X when ?person( \"Mark\", 42; ) then end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(
                                                                  text ).getLhs().getDescrs().get( 0 );

        assertThat(pattern.isQuery()).isTrue();

        assertThat(pattern.getDescrs().size()).isEqualTo(2);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("\"Mark\"");
        assertThat(fcd.getPosition()).isEqualTo(0);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.POSITIONAL);
        fcd = (ExprConstraintDescr) pattern.getDescrs().get( 1 );
        assertThat(fcd.getExpression()).isEqualTo("42");
        assertThat(fcd.getPosition()).isEqualTo(1);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.POSITIONAL);
    }

    @Test
    public void parse_FromFollowedByQuery() throws Exception {
        // the 'from' expression requires a ";" to disambiguate the "?" 
        // prefix for queries from the ternary operator "? :"
        final String text = "rule X when Cheese() from $cheesery ?person( \"Mark\", 42; ) then end";
        RuleDescr rule = parseAndGetFirstRuleDescr(
                                            text );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
        assertThat(pattern.getSource().getText()).isEqualTo("from $cheesery");
        assertThat(pattern.isQuery()).isFalse();

        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("person");
        assertThat(pattern.isQuery()).isTrue();

    }

    @Test
    public void parse_FromWithTernaryFollowedByQuery() throws Exception {
        // the 'from' expression requires a ";" to disambiguate the "?" 
        // prefix for queries from the ternary operator "? :"
        final String text = "rule X when Cheese() from (isFull ? $cheesery : $market) ?person( \"Mark\", 42; ) then end";
        RuleDescr rule = parseAndGetFirstRuleDescr(
                                            text );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
        assertThat(pattern.getSource().getText()).isEqualToIgnoringWhitespace("from (isFull ? $cheesery : $market)");
        assertThat(pattern.isQuery()).isFalse();

        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("person");
        assertThat(pattern.isQuery()).isTrue();

    }

    @Disabled("Priority : Low | Implement multi-value annotation. Not written in docs")
    @Test
    public void parse_MultiValueAnnotationsBackwardCompatibility() throws Exception {
        // multiple values with no keys are parsed as a single value
        final String text = "rule X @ann1( val1, val2 ) @ann2( \"val1\", \"val2\" ) when then end";
        RuleDescr rule = parseAndGetFirstRuleDescr(
                                            text );

        AnnotationDescr ann = rule.getAnnotation("ann1" );
        assertThat(ann).isNotNull();
        assertThat(ann.getValue()).isEqualTo("val1, val2");

        ann = rule.getAnnotation( "ann2" );
        assertThat(ann).isNotNull();
        assertThat(ann.getValue()).isEqualTo("\"val1\", \"val2\"");
    }

    @Test
    public void parse_PositionalsAndNamedConstraints() throws Exception {
        final String text = "rule X when Person( \"Mark\", 42; location == \"atlanta\" ) then end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(
                                                                  text ).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(3);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("\"Mark\"");
        assertThat(fcd.getPosition()).isEqualTo(0);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.POSITIONAL);
        fcd = (ExprConstraintDescr) pattern.getDescrs().get( 1 );
        assertThat(fcd.getExpression()).isEqualTo("42");
        assertThat(fcd.getPosition()).isEqualTo(1);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.POSITIONAL);

        fcd = (ExprConstraintDescr) pattern.getDescrs().get( 2 );
        assertThat(fcd.getExpression()).isEqualTo("location == \"atlanta\"");
        assertThat(fcd.getPosition()).isEqualTo(2);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);

    }

    @Test
    public void parse_UnificationBinding() throws Exception {
        final String text = "rule X when $p := Person( $name := name, $loc : location ) then end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(
                                                                  text ).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getIdentifier()).isEqualTo("$p");
        assertThat(pattern.isUnification()).isTrue();

        assertThat(pattern.getDescrs().size()).isEqualTo(2);
        ExprConstraintDescr bindingDescr = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(bindingDescr.getExpression()).isEqualTo("$name := name");

        bindingDescr = (ExprConstraintDescr) pattern.getDescrs().get( 1 );
        assertThat(bindingDescr.getExpression()).isEqualTo("$loc : location");

    }

    @Test
    public void parse_BigLiterals() {
        final String text = "rule X when Primitives( bigInteger == (10I), " +
                "                        bigDecimal == (10B), " +
                "                        bigInteger < 50I, " +
                "                        bigDecimal < 50.2B ) then end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(
                                                                  text ).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(4);
        ExprConstraintDescr ecd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(ecd.getExpression()).isEqualTo("bigInteger == (10I)");

        ecd = (ExprConstraintDescr) pattern.getDescrs().get( 1 );
        assertThat(ecd.getExpression()).isEqualTo("bigDecimal == (10B)");

        ecd = (ExprConstraintDescr) pattern.getDescrs().get( 2 );
        assertThat(ecd.getExpression()).isEqualTo("bigInteger < 50I");

        ecd = (ExprConstraintDescr) pattern.getDescrs().get( 3 );
        assertThat(ecd.getExpression()).isEqualTo("bigDecimal < 50.2B");
    }

    @Test
    public void parse_BindingComposite() throws Exception {
        final String text = "rule X when Person( $name : name == \"Bob\" || $loc : location == \"Montreal\" ) then end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(
                                                                  text ).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getObjectType()).isEqualTo("Person");
        assertThat(pattern.isUnification()).isFalse();

        // embedded bindings are extracted at compile time
        List< ? > constraints = pattern.getDescrs();
        assertThat(constraints.size()).isEqualTo(1);
        assertThat(((ExprConstraintDescr) constraints.get(0)).getExpression()).isEqualTo("$name : name == \"Bob\" || $loc : location == \"Montreal\"");
    }

    @Test
    public void parse_BindingCompositeWithMethods() throws Exception {
        final String text = "rule X when Person( $name : name.toUpperCase() == \"Bob\" || $loc : location[0].city == \"Montreal\" ) then end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(
                                                                  text ).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getObjectType()).isEqualTo("Person");
        assertThat(pattern.isUnification()).isFalse();

        // embedded bindings are extracted at compile time
        List< ? > constraints = pattern.getDescrs();
        assertThat(constraints.size()).isEqualTo(1);
        assertThat(((ExprConstraintDescr) constraints.get(0)).getExpression()).isEqualTo("$name : name.toUpperCase() == \"Bob\" || $loc : location[0].city == \"Montreal\"");
    }

    @Test
    public void parse_PluggableOperators2() throws Exception {
        final String text = "rule \"tt\"\n" +
                "    dialect \"mvel\"\n" +
                "when\n" +
                "    exists (TelephoneCall( this finishes [1m] \"25-May-2011\" ))\n" +
                "then\n" +
                "end";
        PatternDescr pattern = (PatternDescr) ((ExistsDescr) parseAndGetFirstRuleDescr(
                                                                                 text ).getLhs().getDescrs().get( 0 )).getDescrs().get( 0 );

        assertThat(pattern.getObjectType()).isEqualTo("TelephoneCall");
        ExprConstraintDescr constr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertThat(constr.getText()).isEqualTo("this finishes [1m] \"25-May-2011\"");

    }

    @Test
    public void parse_InlineEval() throws Exception {
        final String text = "rule \"inline eval\"\n" +
                "when\n" +
                "    Person( eval( name.startsWith(\"b\") && name.finishesWith(\"b\")) )\n" +
                "then\n" +
                "end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(
                                                                  text ).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getObjectType()).isEqualTo("Person");
        ExprConstraintDescr constr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertThat(constr.getText()).isEqualToIgnoringWhitespace("eval( name.startsWith(\"b\") && name.finishesWith(\"b\"))");

    }

    @Test
    public void parse_InfinityLiteral() throws Exception {
        final String text = "rule \"infinity\"\n" +
                "when\n" +
                "    StockTick( this after[-*,*] $another )\n" +
                "then\n" +
                "end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(
                                                                  text ).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getObjectType()).isEqualTo("StockTick");
        ExprConstraintDescr constr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertThat(constr.getText()).isEqualTo("this after[-*,*] $another");

    }

    @Test
    public void parse_EntryPointDeclaration() throws Exception {
        final String text = "package org.drools\n" +
                "declare entry-point eventStream\n" +
                "    @source(\"jndi://queues/events\")\n" +
                "    @foo( true )\n" +
                "end";
        PackageDescr pkg = parser.parse(
                                                 text );

        assertThat(pkg.getName()).isEqualTo("org.drools");
        assertThat(pkg.getEntryPointDeclarations().size()).isEqualTo(1);

        EntryPointDeclarationDescr epd = pkg.getEntryPointDeclarations().iterator().next();

        assertThat(epd.getEntryPointId()).isEqualTo("eventStream");
        assertThat(epd.getAnnotations().size()).isEqualTo(2);
        assertThat(epd.getAnnotation("source").getValue()).isEqualTo("\"jndi://queues/events\"");
        assertThat(epd.getAnnotation("foo").getValue()).isEqualTo("true");
    }

    @Test
    public void parse_WindowDeclaration() throws Exception {
        final String text = "package org.drools\n" +
                "declare window Ticks\n" +
                "    @doc(\"last 10 stock ticks\")\n" +
                "    $s : StockTick( source == \"NYSE\" )\n" +
                "        over window:length( 10, $s.symbol )\n" +
                "        from entry-point stStream\n" +
                "end";
        PackageDescr pkg = parser.parse(
                                                 text );

        assertThat(pkg.getName()).isEqualTo("org.drools");
        assertThat(pkg.getWindowDeclarations().size()).isEqualTo(1);

        WindowDeclarationDescr wdd = pkg.getWindowDeclarations().iterator().next();

        assertThat(wdd.getName()).isEqualTo("Ticks");
        assertThat(wdd.getAnnotations().size()).isEqualTo(1);
        assertThat(wdd.getAnnotation("doc").getValue()).isEqualTo("\"last 10 stock ticks\"");

        PatternDescr pd = wdd.getPattern();
        assertThat(pd).isNotNull();
        assertThat(pd.getIdentifier()).isEqualTo("$s");
        assertThat(pd.getObjectType()).isEqualTo("StockTick");
        assertThat(pd.getSource().getText()).isEqualTo("stStream");

        assertThat(pd.getBehaviors().size()).isEqualTo(1);
        BehaviorDescr bd = pd.getBehaviors().get( 0 );
        assertThat(bd.getType()).isEqualTo("window");
        assertThat(bd.getSubType()).isEqualTo("length");
        assertThat(bd.getParameters().size()).isEqualTo(2);
        assertThat(bd.getParameters().get(0)).isEqualTo("10");
        assertThat(bd.getParameters().get(1)).isEqualTo("$s.symbol");
    }

    @Disabled("Priority : Mid | Implement using declared window. Not written in docs, but unit tests found.")
    @Test
    public void parse_WindowUsage() throws Exception {
        final String text = "package org.drools\n" +
                "rule X\n" +
                "when\n" +
                "    StockTick() from window Y\n" +
                "then\n" +
                "end\n";
        PackageDescr pkg = parser.parse(
                                                 text );

        assertThat(pkg.getName()).isEqualTo("org.drools");
        assertThat(pkg.getRules().size()).isEqualTo(1);

        RuleDescr rd = pkg.getRules().get(0);

        assertThat(rd.getName()).isEqualTo("X");
        assertThat(rd.getLhs().getDescrs().size()).isEqualTo(1);

        PatternDescr pd = (PatternDescr) rd.getLhs().getDescrs().get(0);
        assertThat(pd).isNotNull();
        assertThat(pd.getObjectType()).isEqualTo("StockTick");
        assertThat(pd.getSource().getText()).isEqualTo("Y");
    }

    @Test
    public void endInRhs() throws Exception {
        final String text = "package org.drools\n" +
                "rule X\n" +
                "when\n" +
                "    $s : String()\n" +
                "then\n" +
                "    System.out.println($s.endsWith(\"xyz\"));\n" +
                "end\n";
        PackageDescr packageDescr = parser.parse(text );

        RuleDescr ruleDescr = packageDescr.getRules().get(0);
        assertThat(ruleDescr.getConsequence().toString()).isEqualToIgnoringWhitespace("System.out.println($s.endsWith(\"xyz\"));");
    }

    @Test
    public void endTokenInRhs() throws Exception {
        final String text = "package org.drools\n" +
                "rule X\n" +
                "when\n" +
                "    $s : String()\n" +
                "then\n" +
                "    int end = 10;\n" +
                "end\n";
        PackageDescr packageDescr = parser.parse(text );

        RuleDescr ruleDescr = packageDescr.getRules().get(0);
        assertThat(ruleDescr.getConsequence().toString()).isEqualToIgnoringWhitespace("int end = 10;");
    }

    @Test
    public void ruleTokenInRhs() throws Exception {
        final String text = "package org.drools\n" +
                "rule X\n" +
                "when\n" +
                "    $s : String()\n" +
                "then\n" +
                "    int rule = 10;\n" +
                "end\n";
        PackageDescr packageDescr = parser.parse(text );

        RuleDescr ruleDescr = packageDescr.getRules().get(0);
        assertThat(ruleDescr.getConsequence().toString()).isEqualToIgnoringWhitespace("int rule = 10;");
    }

    @Test
    void semicolonEnd() {
        final String text = "package org.drools\n" +
                "rule X\n" +
                "when\n" +
                "    $s : String()\n" +
                "then\n" +
                "    delete($s);end\n"; // no space after semicolon
        PackageDescr packageDescr = parser.parse(text );

        RuleDescr ruleDescr = packageDescr.getRules().get(0);
        assertThat(ruleDescr.getConsequence().toString()).isEqualToIgnoringWhitespace("delete($s);");
    }

    @Test
    void braceEnd() {
        final String text = "package org.drools\n" +
                "rule X\n" +
                "when\n" +
                "    $p : Person()\n" +
                "then\n" +
                "    modify($p) { setAge(2) }end\n"; // no space after right brace
        PackageDescr packageDescr = parser.parse(text );

        RuleDescr ruleDescr = packageDescr.getRules().get(0);
        assertThat(ruleDescr.getConsequence().toString()).isEqualToIgnoringWhitespace("modify($p) { setAge(2) }");
    }

    @Test
    void parenthesisEnd() {
        final String text = "package org.drools\n" +
                "rule X\n" +
                "when\n" +
                "    $p : Person()\n" +
                "then\n" +
                "    retract($p)end\n"; // no space after right parenthesis
        PackageDescr packageDescr = parser.parse(text);

        RuleDescr ruleDescr = packageDescr.getRules().get(0);
        assertThat(ruleDescr.getConsequence().toString()).isEqualToIgnoringWhitespace("retract($p)");
    }

    @Test
    void singleQuoteInRhsWithSpace() {
        String consequence = getResultConsequence("    System.out.println( 'singleQuoteInRhs' );\n");
        assertThat(consequence)
                .as("Single quote should be converted to double quote")
                .isEqualToIgnoringWhitespace("System.out.println( \"singleQuoteInRhs\" );");
    }

    @Test
    void singleQuoteInRhsWithoutSpace() {
        String consequence = getResultConsequence("    System.out.println('singleQuoteInRhs');\n");
        assertThat(consequence)
                .as("Single quote should be converted to double quote")
                .isEqualToIgnoringWhitespace("System.out.println( \"singleQuoteInRhs\" );");
    }

    @Test
    void singleQuoteInDoubleQuoteInRhsWithoutSpace() {
        String consequence = getResultConsequence("    System.out.println(\"There is '\" + $s + \"' in the workspace.\");\n");
        assertThat(consequence)
                .as("Single quote should not be converted to double quote in case of inside double quotes")
                .isEqualToIgnoringWhitespace("System.out.println(\"There is '\" + $s + \"' in the workspace.\");");
    }

    private String getResultConsequence(String rhs) {
        final String text = "package org.drools\n" +
                "rule X\n" +
                "when\n" +
                "    $s : String()\n" +
                "then\n" +
                rhs +
                "end\n";
        PackageDescr packageDescr = parser.parse(text);

        RuleDescr ruleDescr = packageDescr.getRules().get(0);
        return ruleDescr.getConsequence().toString();
    }

    void ruleDescrProperties() {
        final String text = "package org.drools\n" +
                "rule R1\n" +
                "when\n" +
                "    $p : Person()\n" +
                "then\n" +
                "    retract($p);\n" +
                "end\n";

        PackageDescr packageDescr = parser.parse(text);

        RuleDescr ruleDescr = packageDescr.getRules().get(0);

        assertThat(ruleDescr.getName()).isEqualTo("R1");
        assertThat(ruleDescr.getNamespace()).isEqualTo("org.drools");
    }
}
