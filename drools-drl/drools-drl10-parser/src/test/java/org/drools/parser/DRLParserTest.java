package org.drools.parser;

import org.drools.drl.ast.descr.*;
import org.junit.Test;

import static org.drools.parser.DRLParserHelper.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DRLParserTest {

    private static final String drl =
            "package org.test;\n" +
            "import org.test.model.Person;\n" +
            "global String result;\n" +
            "rule TestRule @Test(true) no-loop salience 15 when \n" +
            "  $p:Person()\n" +
            "then\n" +
            "  int a = 4;\n" +
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

        assertEquals(2, ruleDescr.getAttributes().size());
        assertNotNull(ruleDescr.getAttributes().get("no-loop"));
        AttributeDescr salience = ruleDescr.getAttributes().get("salience");
        assertNotNull(salience);
        assertEquals("15", salience.getValue());

        assertEquals("TestRule", ruleDescr.getName());

        assertEquals(1, ruleDescr.getLhs().getDescrs().size());
        PatternDescr patternDescr = (PatternDescr) ruleDescr.getLhs().getDescrs().get(0);
        assertEquals("$p", patternDescr.getIdentifier());
        assertEquals("Person", patternDescr.getObjectType());

        assertEquals("inta=4;System.out.println($p.getName());", ruleDescr.getConsequence());
    }

    @Test
    public void testComputeTokenIndex() {
        DRLParser parser = createDrlParser(drl);
        parser.compilationUnit();

        assertEquals(0, (int) computeTokenIndex(parser, 1, 0));
        assertEquals(0, (int) computeTokenIndex(parser, 1, 1));
        assertEquals(0, (int) computeTokenIndex(parser, 1, 7));
        assertEquals(1, (int) computeTokenIndex(parser, 1, 8));
        assertEquals(2, (int) computeTokenIndex(parser, 1, 9));
        assertEquals(2, (int) computeTokenIndex(parser, 1, 9));
        assertEquals(3, (int) computeTokenIndex(parser, 1, 12));
        assertEquals(4, (int) computeTokenIndex(parser, 1, 13));
        assertEquals(5, (int) computeTokenIndex(parser, 1, 17));
        assertEquals(6, (int) computeTokenIndex(parser, 1, 18));
        assertEquals(6, (int) computeTokenIndex(parser, 2, 0));
        assertEquals(7, (int) computeTokenIndex(parser, 2, 1));
        assertEquals(7, (int) computeTokenIndex(parser, 2, 6));
        assertEquals(8, (int) computeTokenIndex(parser, 2, 7));
        assertEquals(73, (int) computeTokenIndex(parser, 9, 0));
        assertEquals(74, (int) computeTokenIndex(parser, 9, 1));
        assertEquals(75, (int) computeTokenIndex(parser, 9, 4));
        assertEquals(75, (int) computeTokenIndex(parser, 9, 5));
        assertEquals(75, (int) computeTokenIndex(parser, 10, 0));  // EOF
    }

}
