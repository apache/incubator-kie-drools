package org.drools.drl10.parser;

import java.util.List;

import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.drl10.parser.DRLParserHelper.computeTokenIndex;
import static org.drools.drl10.parser.DRLParserHelper.createDrlParser;
import static org.drools.drl10.parser.DRLParserHelper.parse;

class DRLParserTest {

    private static final String drl =
            "package org.test;\n" +
                    "import org.test.model.Person;\n" +
                    "global String result;\n" +
                    "rule TestRule @Test(true) no-loop salience 15 when \n" +
                    "  $p:Person( age >= 18 )\n" +
                    "then\n" +
                    "  int a = 4;\n" +
                    "  System.out.println($p.getName());\n" +
                    "end\n";

    @Test
    void parse_basicRule() {
        PackageDescr packageDescr = parse(drl);
        assertThat(packageDescr.getName()).isEqualTo("org.test");

        assertThat(packageDescr.getImports().size()).isEqualTo(1);
        assertThat(packageDescr.getImports().get(0).getTarget()).isEqualTo("org.test.model.Person");

        assertThat(packageDescr.getGlobals().size()).isEqualTo(1);
        GlobalDescr globalDescr = packageDescr.getGlobals().get(0);
        assertThat(globalDescr.getType()).isEqualTo("String");
        assertThat(globalDescr.getIdentifier()).isEqualTo("result");

        assertThat(packageDescr.getRules().size()).isEqualTo(1);
        RuleDescr ruleDescr = packageDescr.getRules().get(0);

        AnnotationDescr annotationDescr = ruleDescr.getAnnotation("Test");
        assertThat(annotationDescr).isNotNull();
        assertThat(annotationDescr.getValue()).isEqualTo("true");

        assertThat(ruleDescr.getAttributes().size()).isEqualTo(2);
        assertThat(ruleDescr.getAttributes().get("no-loop")).isNotNull();
        AttributeDescr salience = ruleDescr.getAttributes().get("salience");
        assertThat(salience).isNotNull();
        assertThat(salience.getValue()).isEqualTo("15");

        assertThat(ruleDescr.getName()).isEqualTo("TestRule");

        assertThat(ruleDescr.getLhs().getDescrs().size()).isEqualTo(1);
        PatternDescr patternDescr = (PatternDescr) ruleDescr.getLhs().getDescrs().get(0);
        assertThat(patternDescr.getIdentifier()).isEqualTo("$p");
        assertThat(patternDescr.getObjectType()).isEqualTo("Person");

        List<? extends BaseDescr> constraints = patternDescr.getConstraint().getDescrs();
        assertThat(constraints.size()).isEqualTo(1);
        ExprConstraintDescr expr = (ExprConstraintDescr) constraints.get(0);
        assertThat(expr.getExpression()).isEqualTo("age >= 18");

        assertThat(ruleDescr.getConsequence().toString()).isEqualToIgnoringWhitespace("int a = 4; System.out.println($p.getName());");
    }

    @Test
    void computeTokenIndex_basicRule() {
        DRLParser parser = createDrlParser(drl);
        parser.compilationUnit();

        assertThat((int) computeTokenIndex(parser, 1, 0)).isEqualTo(0);
        assertThat((int) computeTokenIndex(parser, 1, 1)).isEqualTo(0);
        assertThat((int) computeTokenIndex(parser, 1, 7)).isEqualTo(0);
        assertThat((int) computeTokenIndex(parser, 1, 8)).isEqualTo(1);
        assertThat((int) computeTokenIndex(parser, 1, 9)).isEqualTo(2);
        assertThat((int) computeTokenIndex(parser, 1, 9)).isEqualTo(2);
        assertThat((int) computeTokenIndex(parser, 1, 12)).isEqualTo(3);
        assertThat((int) computeTokenIndex(parser, 1, 13)).isEqualTo(4);
        assertThat((int) computeTokenIndex(parser, 1, 17)).isEqualTo(5);
        assertThat((int) computeTokenIndex(parser, 1, 18)).isEqualTo(6);
        assertThat((int) computeTokenIndex(parser, 2, 0)).isEqualTo(6);
        assertThat((int) computeTokenIndex(parser, 2, 1)).isEqualTo(7);
        assertThat((int) computeTokenIndex(parser, 2, 6)).isEqualTo(7);
        assertThat((int) computeTokenIndex(parser, 2, 7)).isEqualTo(8);
        // Skip RHS token assertion as it is fluid part at the moment.
    }
}
