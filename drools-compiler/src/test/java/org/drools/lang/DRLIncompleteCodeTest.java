package org.drools.lang;

import org.antlr.runtime.RecognitionException;
import org.drools.base.evaluators.EvaluatorRegistry;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.descr.PackageDescr;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DRLIncompleteCodeTest {

    @Before
    public void setup() {
        // just initialising the static operator definitions
        new EvaluatorRegistry();
    }
    
    @Test
    @Ignore
    public void testIncompleteCode1() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c import a.b.c.* rule MyRule when Class ( property memberOf collexction ";
        DrlParser parser = new DrlParser(5);
        PackageDescr descr = parser.parse(true, input);
        System.out.println(parser.getErrors());

        assertNotNull(descr);
        assertEquals("a.b.c", descr.getNamespace());
        assertEquals("a.b.c.*", descr.getImports().get(0)
                .getTarget());

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_END,
                getLastIntegerValue(parser.getEditorSentences().get(2)
                        .getContent()));
    }

    @Test
    @Ignore
    public void testIncompleteCode2() throws DroolsParserException,
            RecognitionException {
        String input = "rule MyRule when Class ( property memberOf collection ";
        DrlParser parser = new DrlParser(5);
        PackageDescr descr = parser.parse(true, input);

        assertNotNull(descr);
        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_END,
                getLastIntegerValue(parser.getEditorSentences().get(0)
                        .getContent()));
    }

    @Test
    public void testIncompleteCode3() throws DroolsParserException,
            RecognitionException {
        String input = "rule MyRule when Class ( property > somevalue ) then end query MyQuery Class ( property == collection ) end ";
        DrlParser parser = new DrlParser(5);
        PackageDescr descr = parser.parse(true, input);

        assertNotNull(descr);
        assertEquals("MyRule", descr.getRules().get(0).getName());

        assertNotNull(descr);
        assertEquals("MyQuery", descr.getRules().get(1).getName());

        assertEquals(Location.LOCATION_RHS, getLastIntegerValue(parser
                .getEditorSentences().get(0).getContent()));
    }

    @Test
    public void testIncompleteCode4() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c import a.b.c.*"
                + " rule MyRule when Class ( property == collection ) then end "
                + " query MyQuery Class ( property == collection ) end ";
        DrlParser parser = new DrlParser(5);
        PackageDescr descr = parser.parse(true, input);

        assertEquals("a.b.c", descr.getNamespace());
        assertEquals("a.b.c.*", descr.getImports().get(0)
                .getTarget());

        assertNotNull(descr);
        assertEquals("MyRule", descr.getRules().get(0).getName());

        assertNotNull(descr);
        assertEquals("MyQuery", descr.getRules().get(1).getName());
    }

    @Test
    public void testIncompleteCode5() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c import a.b.c.*"
                + " rule MyRule when Class ( property memberOf collection ) then end "
                + " query MyQuery Class ( property memberOf collection ) end ";
        DrlParser parser = new DrlParser(5);
        PackageDescr descr = parser.parse(true, input);

        assertNotNull(descr);
    }

    @Test
    public void testIncompleteCode6() throws DroolsParserException,
            RecognitionException {
        String input = "packe 1111.111 import a.b.c.*"
                + " rule MyRule when Class ( property memberOf collection ) then end "
                + " query MyQuery Class ( property memberOf collection ) end ";
        DrlParser parser = new DrlParser(5);
        PackageDescr descr = parser.parse(true, input);

        assertNotNull(descr);
    }

    @Test
    public void testIncompleteCode7() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c imrt a.b.c.*"
                + " rule MyRule when Class ( property memberOf collection ) then end "
                + " query MyQuery Class ( property memberOf collection ) end ";
        DrlParser parser = new DrlParser(5);
        PackageDescr descr = parser.parse(true, input);

        assertNotNull(descr);
    }

    @Test
    public void testIncompleteCode8() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c import a.1111.c.*"
                + " rule MyRule when Class ( property memberOf collection ) then end "
                + " query MyQuery Class ( property memberOf collection ) end ";
        DrlParser parser = new DrlParser(5);
        PackageDescr descr = parser.parse(true, input);
        System.out.println(parser.getErrors());

        assertEquals("a.b.c", descr.getNamespace());
        // FIXME: assertEquals(2, descr.getRules().size());
        assertEquals(true, parser.hasErrors());
    }

    @Test @Ignore
    public void testIncompleteCode9() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c import a.b.c.*"
                + " rule MyRule xxxxx Class ( property memberOf collection ) then end "
                + " query MyQuery Class ( property memberOf collection ) end ";
        DrlParser parser = new DrlParser(5);
        PackageDescr descr = parser.parse(true, input);

        assertEquals("a.b.c", descr.getNamespace());
        assertEquals("a.b.c.*", descr.getImports().get(0)
                .getTarget());

        assertEquals(1, descr.getRules().size());
        assertEquals("MyQuery", descr.getRules().get(0).getName());
    }

    @Test @Ignore
    public void testIncompleteCode10() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c import a.b.c.*"
                + " rule MyRule xxxxx Class ( property memberOf "
                + " query MyQuery Class ( property memberOf collection ) end ";
        DrlParser parser = new DrlParser(5);
        PackageDescr descr = parser.parse(true, input);

        assertEquals("a.b.c", descr.getNamespace());
        assertEquals("a.b.c.*", descr.getImports().get(0)
                .getTarget());

        assertEquals(0, descr.getRules().size());
    }

    @Test @Ignore
    public void testIncompleteCode11() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c import a.b.c.*"
                + " rule MyRule when Class ( property memberOf collection ) then end "
                + " qzzzzuery MyQuery Class ( property ";
        DrlParser parser = new DrlParser(5);
        PackageDescr descr = parser.parse(true, input);

        assertEquals("a.b.c", descr.getNamespace());
        assertEquals("a.b.c.*", descr.getImports().get(0)
                .getTarget());

        assertNotNull(descr);
        assertEquals("MyRule", descr.getRules().get(0).getName());
    }

    @Test
    public void testIncompleteCode12() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c " + "import a.b.c.* " + "rule MyRule"
                + "  when " + "    m: Message(  ) " + "    " + "  then"
                + "end ";
        DrlParser parser = new DrlParser(5);
        PackageDescr descr = parser.parse(true, input);
        assertNotNull(descr);

        assertEquals("a.b.c", descr.getNamespace());
        assertEquals("a.b.c.*", descr.getImports().get(0)
                .getTarget());
    }

    @Test
    public void testIncompleteCode13() throws DroolsParserException,
            RecognitionException {
        String input = "package com.sample "
                + "import com.sample.DroolsTest.Message; "
                + "rule \"Hello World\"" + "  when " + "  then" + "     \\\" "
                + "end ";
        DrlParser parser = new DrlParser(5);
        PackageDescr descr = parser.parse(true, input);
        assertNotNull(descr);
    }

    @SuppressWarnings("unchecked")
    private int getLastIntegerValue(LinkedList list) {
        // System.out.println(list.toString());
        int lastIntergerValue = -1;
        for (Object object : list) {
            if (object instanceof Integer) {
                lastIntergerValue = (Integer) object;
            }
        }
        return lastIntergerValue;
    }
}
