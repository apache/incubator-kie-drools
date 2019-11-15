package org.drools.modelcompiler.util.lambdareplace;

import org.drools.modelcompiler.domain.Person;
import org.junit.Test;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.*;


public class LambdaTransformTest {

    @Test
    public void createClassWithOneParameter() {
        CreatedClass aClass = new LambdaTransform("org.drools.modelcompiler.util.lambdareplace")
                .createClass("(p) -> p.getAge() > 35", Person.class);

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace;\n" +
                "" +
                "public class Lambda1215CE50D171DD25AC89D919C31CB479 {\n" +
                "\n" +
                "        @Override()\n" +
                "        public java.lang.Boolean apply(org.drools.modelcompiler.domain.Person p) {\n" +
                "            return p.getAge() > 35;\n" +
                "        }\n" +
                "    }\n";

        assertThat(aClass.getCompilationUnitAsString(), equalToIgnoringWhiteSpace(expectedResult));

    }

    @Test
    public void createClassWithTwoParameters() {
        CreatedClass aClass = new LambdaTransform("org.drools.modelcompiler.util.lambdareplace")
                .createClass("(p1, p2) -> p1.getAge() > p2.getAge()", Person.class, Person.class);

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace;\n" +
                "" +
                "public class Lambda5899FA70FFBD0AB136E1673C97CB1EAB {\n" +
                "\n" +
                "        @Override()\n" +
                "        public java.lang.Boolean apply(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) {\n" +
                "            return p1.getAge() > p2.getAge();\n" +
                "        }\n" +
                "    }\n";

        assertThat(aClass.getCompilationUnitAsString(), equalToIgnoringWhiteSpace(expectedResult));

    }
}