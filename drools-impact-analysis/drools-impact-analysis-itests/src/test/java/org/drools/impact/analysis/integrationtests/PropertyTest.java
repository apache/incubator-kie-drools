package org.drools.impact.analysis.integrationtests;

import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.ModelToGraphConverter;
import org.drools.impact.analysis.graph.ReactivityType;
import org.drools.impact.analysis.integrationtests.domain.Person;
import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.ModelBuilder;
import org.junit.Test;

/**
 * 
 * Tests related to property analysis. Nested, function, etc.
 *
 */
public class PropertyTest extends AbstractGraphTest {

    @Test
    public void testNestedProperty() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person()\n" +
                     "then\n" +
                     "  modify ($p) {getAddress().setNumber(10)};" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(address.number == 10)\n" +
                     "then\n" +
                     "end\n";

        // runRule(str, new Person("John", 20, new Address()));

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        // [modify ($p) {getAddress().setNumber(10)};] is parsed to modifiedProperties=[ModifiedProperty{property='address', value=null}]
        // [address.number == 10] is parsed to Constraint{type=EQUAL, property='null', value=10}
        // Currently, it results in UNKNOWN impact. Do we want to analyze this to POSITIVE? (TODO: Confirm customer's expectation)

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.UNKNOWN);
    }

    @Test
    public void testPropertyInFunction() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person()\n" +
                     "then\n" +
                     "  modify ($p) {setAge(42)};" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(calculateToMonth(age) > 480)\n" +
                     "then\n" +
                     "end\n";

        // runRule(str, new Person("John"));

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.UNKNOWN);
    }

    @Test
    public void testUnaryBoolean() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";\n" +
                     "rule R1 when\n" +
                     "  $p : Person(age >= 20)\n" +
                     "then\n" +
                     "  modify ($p) {setEmployed(true)};\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(age < 20)\n" +
                     "then\n" +
                     "  modify ($p) {setEmployed(false)};\n" +
                     "end\n" +
                     "rule R3 when\n" +
                     "  $p : Person(employed)\n" +
                     "then\n" +
                     "end\n" +
                     "rule R4 when\n" +
                     "  $p : Person(!employed)\n" +
                     "then\n" +
                     "end\n";

        // Person person = new Person("John", 30);
        // person.setEmployed(false);
        // runRule(str, person);

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R3", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R4", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R2", "mypkg.R3", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R2", "mypkg.R4", ReactivityType.POSITIVE);
    }
}
