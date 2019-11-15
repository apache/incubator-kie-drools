package org.drools.modelcompiler.util.lambdareplace;

import org.drools.modelcompiler.domain.Person;
import org.junit.Test;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.*;


public class LambdaTransformTest {

    @Test
    public void createLambdaClass() {
        CreatedClass aClass = new LambdaTransform("org.drools.modelcompiler.util.lambdareplace")
                .createClass("(p1, p2) -> p1.getAge() > p2.getAge()", Person.class, Person.class);

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace;\n" +
                "" +
                "public class Expression implements java.util.function.BiFunction<org.drools.modelcompiler.domain.Person, org.drools.modelcompiler.domain.Person, java.lang.Boolean> {\n" +
                "\n" +
                "        @Override()\n" +
                "        public java.lang.Boolean apply(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) {\n" +
                "            return p1.getAge() > p2.getAge();\n" +
                "        }\n" +
                "    }\n";

        assertThat(aClass.getCompilationUnitAsString(), equalToIgnoringWhiteSpace(expectedResult));

    }
}