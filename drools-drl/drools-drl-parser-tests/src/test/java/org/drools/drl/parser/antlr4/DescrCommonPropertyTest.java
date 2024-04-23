/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.parser.antlr4;

import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.AccumulateImportDescr;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.AnnotationDescr;
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
import org.drools.drl.ast.descr.FunctionImportDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.OrDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.QueryDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.drl.ast.descr.TypeFieldDescr;
import org.drools.drl.ast.descr.UnitDescr;
import org.drools.drl.ast.descr.WindowDeclarationDescr;
import org.drools.drl.ast.descr.WindowReferenceDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.DroolsParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This test class is to assert Descr common properties
 */
class DescrCommonPropertyTest {

    private DrlParser parser;

    @BeforeEach
    public void setUp() {
        parser = ParserTestUtils.getParser();
    }

    private PackageDescr parseAndGetPackageDescr(String drl) {
        try {
            PackageDescr pkg =  parser.parse(null, drl);
            assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
            return pkg;
        } catch (DroolsParserException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertProperties(BaseDescr descr, int startCharacter, int endCharacter, int line, int column, int endLine, int endColumn) {
        assertThat(descr.getStartCharacter()).isEqualTo(startCharacter); // first character of the start token. character is 0-based
        assertThat(descr.getEndCharacter()).isEqualTo(endCharacter); // last character of the end token. character is 0-based
        assertThat(descr.getLine()).isEqualTo(line); // line of the start token. line is 1-based
        assertThat(descr.getColumn()).isEqualTo(column); // first column of the start token. column is 0-based
        assertThat(descr.getEndLine()).isEqualTo(endLine); // line of the end token. line is 1-based
        assertThat(descr.getEndColumn()).isEqualTo(endColumn); // last column of the end token. column is 0-based
    }

    @Test
    void packageDescr() {
        final String source = "package foo.bar.baz";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        assertProperties(pkg, 0, 19, 1, 0, 1, 18);
    }

    @Test
    void ruleDescr() {
        final String source = "rule \"MyRule\"\n" +
                "  when\n" +
                "  then\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        assertProperties(rule, 0, 31, 1, 0, 4, 2);
    }

    @Test
    void unitDescr() {
        final String source = "package abc;\n" +
                "unit Foo;";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final UnitDescr unit = pkg.getUnit();
        assertProperties(unit, 13, 22, 2, 0, 2, 8);
    }

    @Test
    void queryDescr() {
        final String source = "query \"MyQuery\"\n" +
                "  Foo()\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final QueryDescr query = (QueryDescr) pkg.getRules().get(0);
        assertProperties(query, 0, 27, 1, 0, 3, 2);
    }

    @Test
    void functionDescr() {
        final String source = "function void myFunction(String data) {\n" +
                "  foo();\n" +
                "}";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final FunctionDescr function = pkg.getFunctions().get(0);
        assertProperties(function, 0, 50, 1, 0, 3, 0);
    }

    @Test
    void globalDescr() {
        final String source = "global java.util.List myList";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final GlobalDescr global = pkg.getGlobals().get(0);
        assertProperties(global, 0, 28, 1, 0, 1, 27);
    }

    @Test
    void functionImportDescr() {
        final String source = "import function org.drools.core.util.DateUtils.*";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final FunctionImportDescr functionImport = pkg.getFunctionImports().get(0);
        assertProperties(functionImport, 0, 48, 1, 0, 1, 47);
    }

    @Test
    void importDescr() {
        final String source = "import org.drools.core.util.DateUtils";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final ImportDescr importDescr = pkg.getImports().get(0);
        assertProperties(importDescr, 0, 37, 1, 0, 1, 36);
    }

    @Test
    void accumulateImportDescr() {
        final String source = "import accumulate org.example.MyAccUtils.sum mySum";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final AccumulateImportDescr accumulateImport = pkg.getAccumulateImports().get(0);
        assertProperties(accumulateImport, 0, 50, 1, 0, 1, 49);
    }

    @Test
    void typeDeclarationDescr() {
        final String source = "declare MyType\n" +
                "  name : String\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final TypeDeclarationDescr typeDeclaration = pkg.getTypeDeclarations().get(0);

        // startCharacter = 8 looks a little odd ("declare" is not included in the Descr), but it keeps the same as the old implementation. We may change it in the future.
        assertProperties(typeDeclaration, 8, 34, 1, 8, 3, 2);
    }

    @Test
    void entryPointDeclarationDescr() {
        final String source = "declare entry-point MyEntryPoint\n" +
                "  @foo( true )\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final EntryPointDeclarationDescr entryPointDeclaration = pkg.getEntryPointDeclarations().stream().findFirst().get();
        assertProperties(entryPointDeclaration, 8, 51, 1, 8, 3, 2);
    }

    @Test
    void windowDeclarationDescr() {
        final String source = "declare window MyWindow\n" +
                "  $s : StockTick( source == \"NYSE\" )\n" +
                "       over window:length( 10, $s.symbol )\n" +
                "       from entry-point stStream\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final WindowDeclarationDescr windowDeclaration = pkg.getWindowDeclarations().stream().findFirst().get();
        assertProperties(windowDeclaration, 8, 140, 1, 8, 5, 2);
    }

    @Test
    void annotationDescr() {
        final String source = "package org.drools\n" +
                "rule R1\n" +
                "when\n" +
                "  $p : Person( name == \"Mario\" ) @watch(!*, age)\n" +
                "then\n" +
                "end\n";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        AnnotationDescr annotation = rule.getLhs().getAllPatternDescr().get(0).getAnnotations().stream().findFirst().get();
        assertProperties(annotation, 65, 80, 4, 33, 4, 47);
    }

    @Test
    void typeFieldDescr() {
        final String source = "declare MyType\n" +
                "  name : String\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final TypeDeclarationDescr typeDeclaration = pkg.getTypeDeclarations().get(0);
        TypeFieldDescr typeField = typeDeclaration.getFields().get("name");
        // Backward Compatibility Notes:
        //   The old DRL6Parser uses only the attribute value token for common properties (seem to be wrong), so startCharacter and column are different between old and new parser.
        //   Backward compatibility doesn't seem to be required in this case.
        if (DrlParser.ANTLR4_PARSER_ENABLED) {
            assertProperties(typeField, 17, 30, 2, 2, 2, 14);
        } else {
            assertProperties(typeField, 24, 30, 2, 9, 2, 14);
        }
    }

    @Test
    void attributeDescr() {
        final String source = "rule R1\n" +
                "  salience 42\n" +
                "  agenda-group \"my_group\"\n" +
                "  when\n" +
                "  then\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);

        // Backward Compatibility Notes:
        //   The old DRL6Parser uses only the attribute value token for common properties (seem to be wrong), so startCharacter and column are different between old and new parser.
        //   Backward compatibility doesn't seem to be required in this case. (If do it, the code would be unnecessarily complicated.)
        if (DrlParser.ANTLR4_PARSER_ENABLED) {
            assertProperties(rule.getAttributes().get("salience"), 10, 21, 2, 2, 2, 12);
            assertProperties(rule.getAttributes().get("agenda-group"), 24, 47, 3, 2, 3, 24);
        } else {
            assertProperties(rule.getAttributes().get("salience"), 19, 21, 2, 11, 2, 12);
            assertProperties(rule.getAttributes().get("agenda-group"), 37, 47, 3, 15, 3, 24);
        }
    }

    @Test
    void patternDescr() {
        final String source = "rule R1\n" +
                "  when\n" +
                "    $p : Person( name == \"Mario\" )\n" +
                "  then\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);
        assertProperties(pattern, 19, 49, 3, 4, 3, 33);
    }

    @Test
    void orDescr() {
        final String source = "rule R1\n" +
                "  when\n" +
                "    ( $p : Person( name == \"Mario\" ) or $p : Person( name == \"Luigi\" ) )\n" +
                "  then\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        OrDescr or = (OrDescr) rule.getLhs().getDescrs().get(0);
        assertProperties(or, 21, 85, 3, 6, 3, 69);
    }

    @Test
    void andDescr() {
        final String source = "rule R1\n" +
                "  when\n" +
                "    ( $p1 : Person( name == \"Mario\" ) and $p2 : Person( name == \"Luigi\" ) )\n" +
                "  then\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        AndDescr and = rule.getLhs();
        assertProperties(and, 19, 90, 3, 4, 3, 74);
    }

    @Test
    void forallDescr() {
        final String source = "rule R1\n" +
                "  when\n" +
                "    forall( $p : Person( name == \"Mario\" ) )\n" +
                "  then\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        ForallDescr forall = (ForallDescr) rule.getLhs().getDescrs().get(0);
        assertProperties(forall, 19, 59, 3, 4, 3, 43);
    }

    @Test
    void accumulateDescr() {
        final String source = "rule R1\n" +
                "  when\n" +
                "    accumulate( $p : Person( name == \"Mario\" ), $sum : sum($p.getAge()) )\n" +
                "  then\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);
        AccumulateDescr accumulate = (AccumulateDescr) pattern.getSource();
        assertProperties(accumulate, 19, 88, 3, 4, 3, 72);
    }

    @Test
    void behaviorDescr() {
        final String source = "rule X when StockTick( symbol==\"ACME\") over window:length(10) then end";;
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);
        BehaviorDescr behavior = pattern.getBehaviors().get(0);

        // "over" is not included in BehaviorDescr
        assertProperties(behavior, 44, 61, 1, 44, 1, 60);
    }

    @Test
    void fromDescr() {
        final String source = "rule X\n" +
                "  when\n" +
                "    Adult( $children : children)\n" +
                "    Child() from $children\n" +
                "  then\n" +
                "end";;
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(1);
        FromDescr from = (FromDescr)  pattern.getSource();

        // Backward Compatibility Notes:
        //   The old DRL6Parser doesn't populate common properties of FromDescr (seem to be wrong).
        //   Backward compatibility doesn't seem to be required in this case.
        if (DrlParser.ANTLR4_PARSER_ENABLED) {
            // "from" is not included in FromDescr
            assertProperties(from, 64, 73, 4, 17, 4, 25);
        } else {
            assertProperties(from, -1, -1, -1, -1, -1, -1);
        }
    }

    @Test
    void collectDescr() {
        final String source = "rule X\n" +
                "  when\n" +
                "    ArrayList() from collect( Person( age > 21 ) );\n" +
                "  then\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);
        CollectDescr collect = (CollectDescr)  pattern.getSource();

        assertProperties(collect, 35, 64, 3, 21, 3, 49);
    }

    @Test
    void entryPointDescr() {
        final String source = "rule X\n" +
                "  when\n" +
                "    StockTick() from entry-point \"stream\"\n" +
                "  then\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);
        EntryPointDescr entryPoint = (EntryPointDescr) pattern.getSource();

        // Backward Compatibility Notes:
        //   The old DRL6Parser doesn't populate common properties of EntryPointDescr (seem to be wrong).
        //   Backward compatibility doesn't seem to be required in this case.
        if (DrlParser.ANTLR4_PARSER_ENABLED) {
            assertProperties(entryPoint, 35, 55, 3, 21, 3, 40);
        } else {
            assertProperties(entryPoint, -1, -1, -1, -1, -1, -1);
        }
    }

    @Test
    void windowReferenceDescr() {
        final String source = "rule X\n" +
                "  when\n" +
                "    StockTick() from window MyWindow\n" +
                "  then\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);
        WindowReferenceDescr windowReference = (WindowReferenceDescr) pattern.getSource();
        // Backward Compatibility Notes:
        //   The old DRL6Parser doesn't populate common properties of WindowReferenceDescr (seem to be wrong).
        //   Backward compatibility doesn't seem to be required in this case.
        if (DrlParser.ANTLR4_PARSER_ENABLED) {
            assertProperties(windowReference, 35, 50, 3, 21, 3, 35);
        } else {
            assertProperties(windowReference, -1, -1, -1, -1, -1, -1);
        }
    }

    @Test
    void exprConstraintDescr() {
        final String source = "rule X\n" +
                "  when\n" +
                "    Person( age > 21 )\n" +
                "  then\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get(0);
        ExprConstraintDescr exprConstraint = (ExprConstraintDescr) pattern.getDescrs().get(0);
        assertProperties(exprConstraint, 26, 33, 3, 12, 3, 18);
    }

    @Test
    void existDescr() {
        final String source = "rule X\n" +
                "  when\n" +
                "    exists( Person( age > 21 ) )\n" +
                "  then\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        ExistsDescr exists = (ExistsDescr) rule.getLhs().getDescrs().get(0);
        assertProperties(exists, 18, 46, 3, 4, 3, 31);
    }

    @Test
    void notDescr() {
        final String source = "rule X\n" +
                "  when\n" +
                "    not( Person( age > 21 ) )\n" +
                "  then\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        NotDescr not = (NotDescr) rule.getLhs().getDescrs().get(0);
        assertProperties(not, 18, 43, 3, 4, 3, 28);
    }

    @Test
    void evalDescr() {
        final String source = "rule X\n" +
                "  when\n" +
                "    eval( 1 + 1 == 2 )\n" +
                "  then\n" +
                "end";
        final PackageDescr pkg = parseAndGetPackageDescr(source);
        final RuleDescr rule = pkg.getRules().get(0);
        EvalDescr eval = (EvalDescr) rule.getLhs().getDescrs().get(0);
        assertProperties(eval, 18, 36, 3, 4, 3, 21);
    }
}
