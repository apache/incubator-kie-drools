package org.drools.parser;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.junit.Test;

import static org.drools.parser.DRLParserHelper.createParseTree;
import static org.drools.parser.DRLParserHelper.findNodeAtPosition;
import static org.drools.parser.DRLParserHelper.findParentOfType;
import static org.drools.parser.DRLParserHelper.isAfterSymbol;
import static org.drools.parser.DRLParserHelper.parse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DRLParserTest {

    private static final String drl =
            "package org.test;\n" +
            "import org.test.model.Person;\n" +
            "global String result;\n" +
            "rule TestRule @Test(true) no-loop salience 15 when \n" +
            "  $p:Person()\n" +
            "then\n" +
            "  System.out.println($p.getName());\n" +
            "end\n";

    @Test
    public void testParse() {
        PackageDescr packageDescr = parse(drl);
        assertEquals("org.test", packageDescr.getName());

        assertEquals(1, packageDescr.getImports().size());
        assertEquals("org.test.model.Person", packageDescr.getImports().get(0).getTarget());

        assertEquals(1, packageDescr.getGlobals().size());
        GlobalDescr globalDescr = packageDescr.getGlobals().get(0);
        assertEquals("String", globalDescr.getType());
        assertEquals("result", globalDescr.getIdentifier());

        assertEquals(1, packageDescr.getRules().size());
        RuleDescr ruleDescr = packageDescr.getRules().get(0);

        AnnotationDescr annotationDescr = ruleDescr.getAnnotation("Test");
        assertNotNull(annotationDescr);
        assertEquals("true", annotationDescr.getValue());

        assertEquals( 2, ruleDescr.getAttributes().size() );
        assertNotNull( ruleDescr.getAttributes().get("no-loop") );
        AttributeDescr salience = ruleDescr.getAttributes().get("salience");
        assertNotNull( salience );
        assertEquals( "15", salience.getValue() );

        assertEquals("TestRule", ruleDescr.getName());

        assertEquals( 1, ruleDescr.getLhs().getDescrs().size() );
        PatternDescr patternDescr = (PatternDescr) ruleDescr.getLhs().getDescrs().get(0);
        assertEquals("$p", patternDescr.getIdentifier());
        assertEquals("Person", patternDescr.getObjectType());

        assertEquals("System.out.println($p.getName());", ruleDescr.getConsequence());
    }

    @Test
    public void testCursorPosition() {
        ParseTree parseTree = createParseTree(drl);
        ParseTree node = findNodeAtPosition(parseTree, 5, 7);
        assertEquals("Person", node.getText());
        ParseTree lhs = findParentOfType(node, DRLParser.RULE_lhs);
        assertEquals(DRLParser.RULE_lhs, ((RuleContext) lhs).getRuleIndex());
        assertEquals("$p:Person()", lhs.getText());
    }

    @Test
    public void testCursorPosition2() {
        ParseTree parseTree = createParseTree(drl);
        ParseTree node = findNodeAtPosition(parseTree, 4, 51);
        assertEquals("when", node.getText());
        assertTrue(isAfterSymbol(node, DRLParser.WHEN, 4, 51));
    }
}
