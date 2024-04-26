/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.parser.antlr4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.AccumulateImportDescr;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.BehaviorDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.QueryDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.ast.descr.WindowDeclarationDescr;
import org.drools.drl.ast.descr.WindowReferenceDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.parser.impl.Operator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.drl.parser.antlr4.ParserTestUtils.enableOldParser;

/*
 * This test class is to test parse rules to accept drlIdentifier insteadof IDENTIFIER. (e.g. drl keyword like "contains")
 */
class DRLParserIdentifierTest {

    private DrlParser parser;

    @BeforeEach
    public void setUp() {
        parser = ParserTestUtils.getParser();
    }

    private String readResource(final String filename) {
        Path path;
        try {
            path = Paths.get(getClass().getResource(filename).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        final StringBuilder sb = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            for (String line; (line = br.readLine()) != null; ) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return sb.toString();
    }

    private PackageDescr parseAndGetPackageDescr(String drl) {
        try {
            PackageDescr pkg = parser.parse(null, drl);
            assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
            return pkg;
        } catch (DroolsParserException e) {
            throw new RuntimeException(e);
        }
    }

    private PackageDescr parseAndGetPackageDescrWithoutErrorCheck(String drl) {
        try {
            return parser.parse(null, drl);
        } catch (DroolsParserException e) {
            throw new RuntimeException(e);
        }
    }

    private RuleDescr parseAndGetFirstRuleDescr(String drl) {
        PackageDescr pkg = parseAndGetPackageDescr(drl);
        assertThat(pkg.getRules()).isNotEmpty();
        return pkg.getRules().get(0);
    }

    private QueryDescr parseAndGetFirstQueryDescr(String drl) {
        PackageDescr pkg = parseAndGetPackageDescr(drl);
        assertThat(pkg.getRules()).isNotEmpty();
        Optional<QueryDescr> optQuery = pkg.getRules().stream().filter(QueryDescr.class::isInstance).map(QueryDescr.class::cast).findFirst();
        assertThat(optQuery).isPresent();
        return optQuery.get();
    }

    @Test
    void importAccumulate() {
        final String source = "import accumulate org.example.MyFunction contains;";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        AccumulateImportDescr accumulateImportDescr = pkg.getAccumulateImports().get(0);
        assertThat(accumulateImportDescr.getTarget()).isEqualTo("org.example.MyFunction");
        assertThat(accumulateImportDescr.getFunctionName()).isEqualTo("contains");
    }

    @Test
    void windowDeclaration() {
        final String text =
                "declare window contains\n" +
                        "    $s : StockTick( source == \"NYSE\" )\n" +
                        "        over window:length( 10 )\n" +
                        "end";
        PackageDescr pkg = parseAndGetPackageDescr(text);
        WindowDeclarationDescr windowDeclarationDescr = pkg.getWindowDeclarations().iterator().next();
        assertThat(windowDeclarationDescr.getName()).isEqualTo("contains");
    }

    @Test
    void nestedConstraint() {
        final String text =
                "rule R\n" +
                        "when\n" +
                        " Person( contains.matches.( city == \"london\", country == \"uk\"))\n" +
                        "then\n" +
                        "end";
        RuleDescr rule = parseAndGetFirstRuleDescr(text);
        assertThat(rule.getLhs().getDescrs().get(0)).isInstanceOfSatisfying(PatternDescr.class, patternDescr -> {
            assertThat(patternDescr.getConstraint().getDescrs().get(0)).isInstanceOfSatisfying(ExprConstraintDescr.class, exprConstraintDescr -> {
                assertThat(exprConstraintDescr.getExpression()).isEqualTo("contains.matches.city == \"london\"");
            });
            assertThat(patternDescr.getConstraint().getDescrs().get(1)).isInstanceOfSatisfying(ExprConstraintDescr.class, exprConstraintDescr -> {
                assertThat(exprConstraintDescr.getExpression()).isEqualTo("contains.matches.country == \"uk\"");
            });
        });
    }

    @Test
    public void function() {
        final String text = "function boolean contains(String s) { return true; }";
        PackageDescr packageDescr = parseAndGetPackageDescr(text);
        FunctionDescr function = packageDescr.getFunctions().get(0);
        assertThat(function.getName()).isEqualTo("contains");
    }

    @Test
    public void patternFilter() {
        final String text = "rule X when StockTick( symbol==\"ACME\") over window:contains(10) then end";
        RuleDescr rule = parseAndGetFirstRuleDescr(text);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);
        BehaviorDescr behavior = pattern.getBehaviors().get(0);
        assertThat(behavior.getType()).isEqualTo("window");
        assertThat(behavior.getSubType()).isEqualTo("contains");
        assertThat(behavior.getParameters().get(0)).isEqualTo("10");
    }

    @Test
    public void accumulateFunction() {
        final String text = "rule R1\n" +
                "when\n" +
                "     accumulate( Person( $age : age > 21 ), $ave : contains( $age ) );\n" +
                "then\n" +
                "end";
        RuleDescr rule = parseAndGetFirstRuleDescr(text);
        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get(0);
        final AccumulateDescr accumulate = (AccumulateDescr) out.getSource();
        assertThat(accumulate.getFunctions().get(0).getFunction()).isEqualToIgnoringWhitespace("contains");
    }

    @Test
    public void fromWindow() {
        final String text =
                "rule X\n" +
                        "when\n" +
                        "    StockTick() from window contains\n" +
                        "then\n" +
                        "end\n";
        RuleDescr rule = parseAndGetFirstRuleDescr(text);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);
        assertThat(pattern.getSource()).isInstanceOfSatisfying(WindowReferenceDescr.class, windowReferenceDescr -> {
            assertThat(windowReferenceDescr.getName()).isEqualTo("contains");
        });
    }

    @Test
    public void type() {
        final String text = "global contains.matches bbb";
        PackageDescr pkg = parseAndGetPackageDescr(text);
        GlobalDescr global = pkg.getGlobals().get(0);
        assertThat(global.getType()).isEqualTo("contains.matches");
        assertThat(global.getIdentifier()).isEqualTo("bbb");
    }

    @Test
    public void unification() throws Exception {
        final String text = "rule X\n" +
                "when\n" +
                "  contains := Person()\n" +
                "then\n" +
                "end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(text).getLhs().getDescrs().get(0);
        assertThat(pattern.getIdentifier()).isEqualTo("contains");
        assertThat(pattern.isUnification()).isTrue();
    }

    @Test
    public void annotation() {
        final String text = "rule R\n" +
                "@contains.matches(soundslike=\"abc\")\n" +
                "when\n" +
                "then\n" +
                "end";
        RuleDescr rule = parseAndGetFirstRuleDescr(text);
        AnnotationDescr annotation = rule.getAnnotation("contains.matches");
        assertThat(annotation.getValue("soundslike")).isEqualTo("\"abc\"");
    }

    @Test
    public void constraintBoundVariable() {
        final String text = "rule X\n" +
                "when\n" +
                "  Person( contains : age > 20)\n" +
                "then\n" +
                "end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(text).getLhs().getDescrs().get(0);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get(0);
        assertThat(exprConstraintDescr.getExpression()).isEqualTo("contains : age > 20");
    }

    @Test
    public void constraintBoundVariableUnify() {
        final String text = "rule X\n" +
                "when\n" +
                "  Person( contains := age > 20)\n" +
                "then\n" +
                "end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(text).getLhs().getDescrs().get(0);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get(0);
        assertThat(exprConstraintDescr.getExpression()).isEqualTo("contains := age > 20");
    }

    @Test
    void xpathChunk() {
        final String text =
                "rule R when\n" +
                        " $man: Man( /contains.matches#soundslike[memberOf > 10] )\n" +
                        "then\n" +
                        "end\n";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(text).getLhs().getDescrs().get(0);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get(0);
        assertThat(exprConstraintDescr.getExpression()).isEqualTo("/contains.matches#soundslike[memberOf > 10]");
    }

    @Test
    void createdName() {
        final String text =
                "rule X\n" +
                        "when\n" +
                        "  Person( age > new contains.matches(10))\n" +
                        "then\n" +
                        "end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(text).getLhs().getDescrs().get(0);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get(0);
        assertThat(exprConstraintDescr.getExpression()).isEqualTo("age > new contains.matches(10)");
    }

    @Test
    void explicitGenericInvocationSuffix() {
        final String text =
                "rule X\n" +
                        "when\n" +
                        "  Person( name == <String>contains())\n" +
                        "then\n" +
                        "end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(text).getLhs().getDescrs().get(0);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get(0);
        assertThat(exprConstraintDescr.getExpression()).isEqualTo("name == <String>contains()");
    }

    @Disabled("Old parser does not support this syntax")
    @Test
    void innerCreator() {
        final String text =
                "rule X\n" +
                        "when\n" +
                        "  Person( outer.new contains() != null )\n" +
                        "then\n" +
                        "end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(text).getLhs().getDescrs().get(0);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get(0);
        assertThat(exprConstraintDescr.getExpression()).isEqualTo("outer.new contains() != null");
    }

    @Test
    void superSuffix() {
        final String text =
                "rule X\n" +
                        "when\n" +
                        "  Person( address.super.contains() == 10 )\n" +
                        "then\n" +
                        "end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(text).getLhs().getDescrs().get(0);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get(0);
        assertThat(exprConstraintDescr.getExpression()).isEqualTo("address.super.contains() == 10");
    }

    @Disabled("To be done by https://github.com/apache/incubator-kie-drools/issues/5874")
    @Test
    void operator_key() {
        final String text =
                "rule X\n" +
                        "when\n" +
                        "  Person( age provides 10 )\n" +
                        "then\n" +
                        "end";
        // PROVIDES is not IDENTIFIER, but included in drlIdentifier
        Operator.addOperatorToRegistry("provides", false);
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(text).getLhs().getDescrs().get(0);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get(0);
        assertThat(exprConstraintDescr.getExpression()).isEqualTo("age provides 10");
    }

    @Disabled("To be done by https://github.com/apache/incubator-kie-drools/issues/5874")
    @Test
    void neg_operator_key() {
        final String text =
                "rule X\n" +
                        "when\n" +
                        "  Person( age not provides 10 )\n" +
                        "then\n" +
                        "end";
        // PROVIDES is not IDENTIFIER, but included in drlIdentifier
        Operator.addOperatorToRegistry("provides", true);
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(text).getLhs().getDescrs().get(0);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get(0);
        assertThat(exprConstraintDescr.getExpression()).isEqualTo("age not provides 10");
    }

    @Test
    void operator_key_temporal() {
        // This test (and MiscDRLParserTest.parse_PluggableOperators) fails when adapting drlIdentifier to operator_key and neg_operator_key in DRL6Expressions.g4
        // See https://github.com/apache/incubator-kie-drools/issues/5874
        final String text =
                "rule X\n" +
                        "when\n" +
                        "  Person( this after[1,10] $a || this not after[15,20] $a )\n" +
                        "then\n" +
                        "end";
        PatternDescr pattern = (PatternDescr) parseAndGetFirstRuleDescr(text).getLhs().getDescrs().get(0);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get(0);
        assertThat(exprConstraintDescr.getExpression()).isEqualTo("this after[1,10] $a || this not after[15,20] $a");
    }
}
