package org.drools.lang;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.drools.base.evaluators.EvaluatorRegistry;
import org.drools.compiler.DroolsParserException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DRLContextTest {

    @Before
    public void setUp() throws Exception {
        // initializes pluggable operators
        new EvaluatorRegistry();
    }

    @Test
    public void testCheckLHSLocationDetermination_OPERATORS_AND_COMPLEMENT1()
            throws DroolsParserException, RecognitionException {
        String input = "rule MyRule when Class ( property memberOf collection ";
        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_END,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_OPERATORS_AND_COMPLEMENT2()
            throws DroolsParserException, RecognitionException {
        String input = "rule MyRule when Class ( property not memberOf collection";
        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_COMPOSITE_OPERATOR1()
            throws DroolsParserException, RecognitionException {
        String input = "rule MyRule when Class ( property in ( ";
        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION1()
            throws DroolsParserException, RecognitionException {
        String input = "rule MyRule \n" + "	when \n" + "		";
        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION2() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class( condition == true ) \n" + "		";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION3() {
        String input = "rule MyRule \n" + "	when \n"
                + "		class: Class( condition == true, condition2 == null ) \n"
                + "		";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION4() {
        String input = "rule MyRule \n" + "	when \n" + "		Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION5() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class( condition == true ) \n" + "		Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION6() {
        String input = "rule MyRule \n" + "	when \n" + "		class: Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION7() {
        String input = "rule MyRule \n" + "	when \n" + "		class:Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    /** Inside of condition: start */
    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START1() {
        String input = "rule MyRule \n" + "	when \n" + "		Class (";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START2() {
        String input = "rule MyRule \n" + "	when \n" + "		Class ( na";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START3() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( name.subProperty['test'].subsu";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START4() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( condition == true, ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START5() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( condition == true, na";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START6() {
        String input = "rule MyRule \n" + "	when \n" + "		Class ( \n" + "			";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START7() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( condition == true, \n" + "			";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START8() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( c: condition, \n" + "			";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        DroolsToken token = (DroolsToken) parser.getEditorInterface().get(0)
                .getContent().get(8);

        assertEquals(DroolsEditorType.IDENTIFIER_VARIABLE, token
                .getEditorType());

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START9a() {
        String input = "rule MyRule \n" + "   when \n" + "       Class ( name:";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START9b() {
        String input = "rule MyRule \n" + "	when \n" + "		Class ( name: ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START10() {
        String input = "rule MyRule \n" + "	when \n" + "		Class ( name:";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    /** Inside of condition: Operator */
    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR1() {
        String input = "rule MyRule \n" + "	when \n" + "		Class ( property ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR2() {
        String input = "rule MyRule \n" + "	when \n" + "		Class(property ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR3() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( name : property ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR4() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class (name:property ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR5() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class (name:property   ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR6() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( name1 : property1, name : property ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR7() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( name1 : property1 == \"value\", name : property ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR8() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( name1 : property1 == \"value\",property ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR9() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( name1 : property1, \n" + "			name : property ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    /** Inside of condition: argument */
    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT1() {
        String input = "rule MyRule \n" + "	when \n" + "		Class ( property == ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT2() {
        String input = "rule MyRule \n" + "	when \n" + "		Class ( property== ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT3() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( name : property <= ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT4() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( name:property != ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT5() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( name1 : property1, property2 == ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT6() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class (name:property== ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT7a() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property == otherPropertyN";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT7b() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property == otherPropertyN ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_END,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT8() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property == \"someth";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT9a() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property contains ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT9b() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property not contains ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT10() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property excludes ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT11() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property matches \"prop";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT12() {
        String input = "rule MyRule \n" + "	when \n" + "		Class ( property in ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_END1() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property in ('1', '2') ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_END,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START11() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property in ('1', '2'), ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT13() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property not in ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_END2() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property not in ('1', '2') ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_END,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START12() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property not in ('1', '2'), ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT14() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property memberOf ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test @Ignore
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_END3() {
        // FIXME for now it will be a limitation of the parser... memberOf is a
        // soft-keyword and this sentence cannot be parsed correctly if
        // misspelling
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property memberOf collection ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_END,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START13() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property memberOf collection, ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT15() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property not memberOf ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_END4() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property not memberOf collection ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_END,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START14() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property not memberOf collection, ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        LinkedList list = parser.getEditorInterface().get(0).getContent();
//		for (Object o: list) {
//			System.out.println(o);
//		}
        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    /** EXISTS */
    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_EXISTS1() {
        String input = "rule MyRule \n" + "	when \n" + "		exists ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_EXISTS2() {
        String input = "rule MyRule \n" + "	when \n" + "		exists ( ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_EXISTS3() {
        String input = "rule MyRule \n" + "	when \n" + "		exists(";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_EXISTS4() {
        String input = "rule MyRule \n" + "	when \n" + "		exists Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_EXISTS5() {
        String input = "rule MyRule \n" + "	when \n" + "		exists ( Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_EXISTS6() {
        String input = "rule MyRule \n" + "	when \n" + "		exists ( name : Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDeterminationINSIDE_CONDITION_START16() {
        String input = "rule MyRule \n" + "	when \n" + "		exists Class (";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION() {
        String input = "rule MyRule \n" + "	when \n" + "		exists Class ( ) \n"
                + "       ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    /** NOT */
    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_NOT1() {
        String input = "rule MyRule \n" + "	when \n" + "		not ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_NOT2() {
        String input = "rule MyRule \n" + "	when \n" + "		not Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_EXISTS7() {
        String input = "rule MyRule \n" + "	when \n" + "		not exists ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_EXISTS8() {
        String input = "rule MyRule \n" + "	when \n" + "		not exists Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START21() {
        String input = "rule MyRule \n" + "	when \n" + "		not Class (";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START22() {
        String input = "rule MyRule \n" + "	when \n" + "		not exists Class (";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START23() {
        String input = "rule MyRule \n" + "	when \n"
                + "		not exists name : Class (";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION9() {
        String input = "rule MyRule \n" + "	when \n" + "		not Class () \n"
                + "		";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    /** AND */
    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR1() {
        String input = "rule MyRule \n" + "	when \n" + "		Class ( ) and ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR2() {
        String input = "rule MyRule \n" + "	when \n" + "		Class ( ) and  ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR3() {
        String input = "rule MyRule \n" + "	when \n" + "		Class () and   ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR4() {
        String input = "rule MyRule \n" + "	when \n"
                + "		name : Class ( name: property ) and ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        DroolsToken token = (DroolsToken) parser.getEditorInterface().get(0)
                .getContent().get(5);
        assertEquals(DroolsEditorType.IDENTIFIER_VARIABLE, token
                .getEditorType());

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR5() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( name: property ) \n" + "       and ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR6() {
        String input = "rule MyRule \n" + "	when \n" + "		Class ( ) and Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR7() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) and name : Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR8() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) and name : Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION31() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) and Class ( ) \n" + "       ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION32() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) and not Class ( ) \n" + "       ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION33() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) and exists Class ( ) \n" + "       ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START20() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) and Class ( ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR21() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) and Class ( name ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR22() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) and Class ( name == ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_NOT() {
        String input = "rule MyRule \n" + "	when \n"
                + "		exists Class ( ) and not ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_EXISTS() {
        String input = "rule MyRule \n" + "	when \n"
                + "		exists Class ( ) and exists ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION30() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) and not Class ( ) \n" + "       ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    /** OR */
    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR21() {
        String input = "rule MyRule \n" + "	when \n" + "		Class ( ) or ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR22() {
        String input = "rule MyRule \n" + "	when \n" + "		Class ( ) or ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR23() {
        String input = "rule MyRule \n" + "	when \n" + "		Class () or   ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR24() {
        String input = "rule MyRule \n" + "	when \n"
                + "		name : Class ( name: property ) or ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR25() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( name: property ) \n" + "       or ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR26() {
        String input = "rule MyRule \n" + "	when \n" + "		Class ( ) or Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR27() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) or name : Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_AND_OR28() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) or name : Cl";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION40() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) or Class ( ) \n" + "       ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START40() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) or Class ( ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) or Class ( name ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT30() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( ) or Class ( name == ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_EGIN_OF_CONDITION_NOT() {
        String input = "rule MyRule \n" + "	when \n"
                + "		exists Class ( ) or not ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION_EXISTS40() {
        String input = "rule MyRule \n" + "	when \n"
                + "		exists Class ( ) or exists ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    /** EVAL */
    @Test
    public void testCheckLHSLocationDetermination_INSIDE_EVAL1() {
        String input = "rule MyRule \n" + "	when \n" + "		eval ( ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_EVAL,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_EVAL2() {
        String input = "rule MyRule \n" + "	when \n" + "		eval(";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_EVAL,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_EVAL3() {
        String input = "rule MyRule \n" + "	when \n" + "		eval( myCla";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_EVAL,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_EVAL4() {
        String input = "rule MyRule \n" + "	when \n" + "		eval( param.getMetho";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_EVAL,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_EVAL5() {
        String input = "rule MyRule \n" + "	when \n"
                + "		eval( param.getMethod(";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_EVAL,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_EVAL6() {
        String input = "rule MyRule \n" + "	when \n"
                + "		eval( param.getMethod().get";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_EVAL,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_EVAL7() {
        String input = "rule MyRule \n" + "	when \n"
                + "		eval( param.getMethod(\"someStringWith)))\").get";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_EVAL,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_EVAL8() {
        String input = "rule MyRule \n" + "	when \n"
                + "		eval( param.getMethod(\"someStringWith(((\").get";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_EVAL,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_EVAL9() {
        String input = "rule MyRule \n" + "	when \n" + "		eval( true )";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION50() {
        String input = "rule MyRule \n" + "	when \n"
                + "		eval( param.getProperty(name).isTrue() )";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION51() {
        String input = "rule MyRule \n" + "	when \n"
                + "		eval( param.getProperty(\"someStringWith(((\").isTrue() )";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_EVAL10() {
        String input = "rule MyRule \n" + "	when \n"
                + "		eval( param.getProperty((((String) s) )";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_EVAL,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION52() {
        String input = "rule MyRule \n" + "	when \n"
                + "		eval( param.getProperty((((String) s))))";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION53() {
        String input = "rule MyRule \n" + "	when \n" + "		eval( true ) \n"
                + "       ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    /** MULTIPLE RESTRICTIONS */
    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR12() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 && ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR13() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( name : property1, property2 > 0 && ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR14() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property1 < 20, property2 > 0 && ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT20() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 && < ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_END6() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 && < 10 ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_END,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START41() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 && < 10, ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR60() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 || ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR61() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 && \n" + "       ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR62() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( name : property1, property2 > 0 || ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR63() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property1 < 20, property2 > 0 || ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_END10() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_END,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_END11() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 \n" + "       ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_END,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_END12() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 && < 10 ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_END,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_END13() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 || < 10 ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_END,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_END14() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property == \"test\" || == \"test2\" ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_END,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    /** FROM */
    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION60() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION61() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) fr";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_FROM1() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM, getLastIntegerValue(parser
                .getEditorInterface().get(0).getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_FROM2() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from myGlob";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM, getLastIntegerValue(parser
                .getEditorInterface().get(0).getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_FROM3() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from myGlobal.get";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM, getLastIntegerValue(parser
                .getEditorInterface().get(0).getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION75() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from myGlobal.getList() \n"
                + "       ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION71() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from getDroolsFunction() \n"
                + "       ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    /** FROM ACCUMULATE */
    @Test
    public void testCheckLHSLocationDetermination_FROM_ACCUMULATE1() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from accumulate ( ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM_ACCUMULATE,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_FROM_ACCUMULATE2() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from accumulate(";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM_ACCUMULATE,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION73() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from accumulate( \n"
                + "			$cheese : Cheese( type == $likes ), \n"
                + "			init( int total = 0; ), \n"
                + "			action( total += $cheese.getPrice(); ), \n"
                + "           result( new Integer( total ) ) \n" + "		) \n"
                + "		";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_FROM_ACCUMULATE_INIT_INSIDE() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from accumulate( \n"
                + "			$cheese : Cheese( type == $likes ), \n" + "			init( ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_FROM_ACCUMULATE_ACTION_INSIDE() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from accumulate( \n"
                + "			$cheese : Cheese( type == $likes ), \n"
                + "			init( int total = 0; ), \n" + "			action( ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_FROM_ACCUMULATE_ACTION_INSIDE3() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from accumulate( \n"
                + "			$cheese : Cheese( type == $likes ), \n"
                + "			init( int total = 0; ), \n" + "			action( ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_FROM_ACCUMULATE_RESULT_INSIDE() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from accumulate( \n"
                + "			$cheese : Cheese( type == $likes ), \n"
                + "			init( int total = 0; ), \n"
                + "			action( total += $cheese.getPrice(); ), \n"
                + "           result( ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_FROM_ACCUMULATE_INIT_INSIDE2() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from accumulate( \n"
                + "			$cheese : Cheese( type == $likes ), \n"
                + "			init( int total =";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_FROM_ACCUMULATE_ACTION_INSIDE2() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from accumulate( \n"
                + "			$cheese : Cheese( type == $likes ), \n"
                + "			init( int total = 0; ), \n" + "			action( total += $ch";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_FROM_ACCUMULATE_RESULT_INSIDE2() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from accumulate( \n"
                + "			$cheese : Cheese( type == $likes ), \n"
                + "			init( int total = 0; ), \n"
                + "			action( total += $cheese.getPrice(); ), \n"
                + "           result( new Integer( tot";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from accumulate( \n"
                + "			$cheese : Cheese( ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR40() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from accumulate( \n"
                + "			$cheese : Cheese( type ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from accumulate( \n"
                + "			$cheese : Cheese( type == ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    /** FROM COLLECT */
    @Test
    public void testCheckLHSLocationDetermination_FROM_COLLECT1() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from collect ( ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM_COLLECT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_FROM_COLLECT2() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from collect(";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM_COLLECT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION67() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from collect ( \n"
                + "			Cheese( type == $likes )" + "		) \n" + "		";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START31() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from collect ( \n" + "			Cheese( ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR31() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from collect ( \n"
                + "			Cheese( type ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT21() {
        String input = "rule MyRule \n" + "	when \n"
                + "		Class ( property > 0 ) from collect ( \n"
                + "			Cheese( type == ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    /** NESTED FROM */
    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION68() {
        String input = "rule MyRule \n"
                + "	when \n"
                + "		ArrayList(size > 50) from collect( Person( disabled == \"yes\", income > 100000 ) ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_FROM5() {
        String input = "rule MyRule \n"
                + "	when \n"
                + "		ArrayList(size > 50) from collect( Person( disabled == \"yes\", income > 100000 ) from ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM, getLastIntegerValue(parser
                .getEditorInterface().get(0).getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION69() {
        String input = "rule MyRule \n"
                + "	when \n"
                + "		ArrayList(size > 50) from collect( Person( disabled == \"yes\", income > 100000 ) from town.getPersons() )";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION70() {
        String input = "rule MyRule \n"
                + "	when \n"
                + "		ArrayList(size > 50) from accumulate( Person( disabled == \"yes\", income > 100000 ) ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_FROM6() {
        String input = "rule MyRule \n"
                + "	when \n"
                + "		ArrayList(size > 50) from accumulate( Person( disabled == \"yes\", income > 100000 ) from ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_FROM, getLastIntegerValue(parser
                .getEditorInterface().get(0).getContent()));
    }

    /** FORALL */
    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION81() {
        String input = "rule MyRule \n" + "	when \n" + "		forall ( ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START32() {
        String input = "rule MyRule \n" + "	when \n" + "		forall ( "
                + "           Class ( pr";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_OPERATOR32() {
        String input = "rule MyRule \n" + "	when \n" + "		forall ( "
                + "           Class ( property ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_ARGUMENT22() {
        String input = "rule MyRule \n" + "	when \n" + "		forall ( "
                + "           Class ( property == ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION76() {
        String input = "rule MyRule \n" + "	when \n" + "		forall ( "
                + "           Class ( property == \"test\")" + "           C";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION77a() {
        String input = "rule MyRule \n"
                + "	when \n"
                + "		ArrayList(size > 50) from accumulate( Person( disabled == \"yes\", income > 100000 ) from town.getPersons() ) ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_BEGIN_OF_CONDITION77b() {
        String input = "rule MyRule \n"
                + "   when \n"
                + "       ArrayList(size > 50) from accumulate( Person( disabled == \"yes\", income > 100000 ) from town.getPersons() )";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START45a() {
        String input = "rule MyRule \n" + "   when \n"
                + "       Class ( name :";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckLHSLocationDetermination_INSIDE_CONDITION_START45b() {
        String input = "rule MyRule \n" + "   when \n"
                + "       Class ( name : ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckRHSLocationDetermination_firstLineOfLHS() {
        String input = "rule MyRule \n" + "	when\n" + "		Class ( )\n"
                + "   then\n" + "       ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_RHS, getLastIntegerValue(parser
                .getEditorInterface().get(0).getContent()));
    }

    @Test
    public void testCheckRHSLocationDetermination_startOfNewlINE() {
        String input = "rule MyRule \n" + "	when\n" + "		Class ( )\n"
                + "   then\n" + "       assert(null);\n" + "       ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_RHS, getLastIntegerValue(parser
                .getEditorInterface().get(0).getContent()));
    }

    @Test
    public void testCheckRHSLocationDetermination3() {
        String input = "rule MyRule \n" + "	when\n" + "		Class ( )\n"
                + "   then\n" + "       meth";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_RHS, getLastIntegerValue(parser
                .getEditorInterface().get(0).getContent()));
    }

    @Test
    public void testCheckRuleHeaderLocationDetermination() {
        String input = "rule MyRule ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_RULE_HEADER, getLastIntegerValue(parser
                .getEditorInterface().get(0).getContent()));
    }

    @Test
    public void testCheckRuleHeaderLocationDetermination2() {
        String input = "rule MyRule \n" + "	salience 12 activation-group \"my";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        DroolsToken token = getLastTokenOnList(parser.getEditorInterface().get(
                0).getContent());
        assertEquals("group", token.getText().toLowerCase());
        assertEquals(DroolsEditorType.KEYWORD, token.getEditorType());

        assertEquals(Location.LOCATION_RULE_HEADER_KEYWORD,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckRuleHeaderLocationDetermination3() {
        String input = "rule \"Hello World\" ruleflow-group \"hello\" s";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_RULE_HEADER, getLastIntegerValue(parser
                .getEditorInterface().get(0).getContent()));
    }

    @Test
    public void testCheckRuleHeaderLocationDetermination_dialect1() {
        String input = "rule MyRule \n" + "	dialect \"java\"";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_RULE_HEADER, getLastIntegerValue(parser
                .getEditorInterface().get(0).getContent()));
    }

    @Test
    public void testCheckRuleHeaderLocationDetermination_dialect2() {
        String input = "rule MyRule \n" + "	dialect \"mvel\"";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_RULE_HEADER, getLastIntegerValue(parser
                .getEditorInterface().get(0).getContent()));
    }

    @Test
    public void testCheckRuleHeaderLocationDetermination_dialect3() {
        String input = "rule MyRule \n" + "	dialect ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        DroolsToken token = getLastTokenOnList(parser.getEditorInterface().get(
                0).getContent());
        assertEquals("dialect", token.getText().toLowerCase());
        assertEquals(DroolsEditorType.KEYWORD, token.getEditorType());

        assertEquals(Location.LOCATION_RULE_HEADER_KEYWORD,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckRuleHeaderLocationDetermination_dialect4() {
        String input = "rule MyRule \n" + "	dialect \"";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        DroolsToken token = getLastTokenOnList(parser.getEditorInterface().get(
                0).getContent());
        assertEquals("dialect", token.getText().toLowerCase());
        assertEquals(DroolsEditorType.KEYWORD, token.getEditorType());

        assertEquals(Location.LOCATION_RULE_HEADER_KEYWORD,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    // TODO: add tests for dialect defined at package header level

    @Test
    public void testCheckQueryLocationDetermination_RULE_HEADER1() {
        String input = "query MyQuery ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_RULE_HEADER, getLastIntegerValue(parser
                .getEditorInterface().get(0).getContent()));
    }

    @Test
    public void testCheckQueryLocationDetermination_RULE_HEADER2() {
        String input = "query \"MyQuery\" ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_RULE_HEADER, getLastIntegerValue(parser
                .getEditorInterface().get(0).getContent()));
    }

    @Test
    public void testCheckQueryLocationDetermination_LHS_BEGIN_OF_CONDITION() {
        String input = "query MyQuery() ";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_BEGIN_OF_CONDITION,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @Test
    public void testCheckQueryLocationDetermination_LHS_INSIDE_CONDITION_START() {
        String input = "query MyQuery \n" + "	Class (";

        DRLParser parser = getParser(input);
        parser.enableEditorInterface();
        try {
            parser.compilationUnit();
        } catch (Exception ex) {
        }

        assertEquals(Location.LOCATION_LHS_INSIDE_CONDITION_START,
                getLastIntegerValue(parser.getEditorInterface().get(0)
                        .getContent()));
    }

    @SuppressWarnings("unchecked")
    private int getLastIntegerValue(LinkedList list) {
        //System.out.println(list.toString());
        int lastIntergerValue = -1;
        for (Object object : list) {
            if (object instanceof Integer) {
                lastIntergerValue = (Integer) object;
            }
        }
        return lastIntergerValue;
    }

    @SuppressWarnings("unchecked")
    private DroolsToken getLastTokenOnList(LinkedList list) {
        DroolsToken lastToken = null;
        for (Object object : list) {
            if (object instanceof DroolsToken) {
                lastToken = (DroolsToken) object;
            }
        }
        return lastToken;
    }

    /**
     * @return An instance of a RuleParser should you need one (most folks will
     *         not).
     */
    private DRLParser getParser(final String text) {
        DRLParser parser = new DRLParser(new CommonTokenStream(new DRLLexer(
                new ANTLRStringStream(text))));
        return parser;
    }
}
