package org.drools.parser;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.drools.drl.ast.descr.PackageDescr;
import org.junit.Test;

import static org.drools.parser.DRLParserHelper.createParseTree;
import static org.drools.parser.DRLParserHelper.findNodeAtPosition;
import static org.drools.parser.DRLParserHelper.findParentOfType;
import static org.drools.parser.DRLParserHelper.isAfterSymbol;
import static org.drools.parser.DRLParserHelper.parse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DRLParserTest {

    private static final String drl =
            "package org.test;\n" +
            "import org.test.model.Person;\n" +
            "rule TestRule when \n" +
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

        assertEquals(1, packageDescr.getRules().size());
        assertEquals("TestRule", packageDescr.getRules().get(0).getName());
        assertEquals("System.out.println($p.getName());", packageDescr.getRules().get(0).getConsequence());
    }

    @Test
    public void testCursorPosition() {
        ParseTree parseTree = createParseTree(drl);
        ParseTree node = findNodeAtPosition(parseTree, 4, 7);
        assertEquals("Person", node.getText());
        ParseTree lhs = findParentOfType(node, DRLParser.RULE_lhs);
        assertEquals(DRLParser.RULE_lhs, ((RuleContext) lhs).getRuleIndex());
        assertEquals("$p:Person()", lhs.getText());
    }

    @Test
    public void testCursorPosition2() {
        ParseTree parseTree = createParseTree(drl);
        ParseTree node = findNodeAtPosition(parseTree, 3, 19);
        assertEquals("when", node.getText());
        assertTrue(isAfterSymbol(node, DRLParser.WHEN, 3, 19));
    }
}
