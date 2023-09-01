package org.drools.impact.analysis.parser;

import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.domain.Person;
import org.junit.Test;

public class ParserTest {

    @Test
    public void test() {
        String str =
                "package mypkg;\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { setAge( 18 ) };" +
                "  insert(\"Done\");\n" +
                "end";

        AnalysisModel analysisModel = new ModelBuilder().build( str );
        System.out.println(analysisModel);
    }

    @Test
    public void testInsert() {
        String str =
                "package mypkg;\n" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule R1 when\n" +
                        "  String(this == \"Start\")\n" +
                        "then\n" +
                        "  Person p = new Person();\n" +
                        "  p.setName(\"John\");\n" +
                        "  insert(p);\n" +
                        "end";

        AnalysisModel analysisModel = new ModelBuilder().build( str );
        System.out.println(analysisModel);
    }
}
