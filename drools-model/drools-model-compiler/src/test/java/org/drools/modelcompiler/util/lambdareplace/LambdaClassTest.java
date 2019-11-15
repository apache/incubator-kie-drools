package org.drools.modelcompiler.util.lambdareplace;

import org.junit.Test;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertThat;


public class LambdaClassTest {

    @Test
    public void createClassWithOneParameter() {
        CreatedClass aClass = new LambdaClass("org.drools.modelcompiler.util.lambdareplace")
                .createClass("(org.drools.modelcompiler.domain.Person p) -> p.getAge() > 35");

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace;\n" +
                "" +
                "public class Lambda4DEB93975D9859892B1A5FD4B38E2155 {\n" +
                "\n" +
                "        public java.lang.Boolean apply(org.drools.modelcompiler.domain.Person p) {\n" +
                "            return p.getAge() > 35;\n" +
                "        }\n" +
                "    }\n";

        assertThat(aClass.getCompilationUnitAsString(), equalToIgnoringWhiteSpace(expectedResult));

    }

    @Test
    public void createClassWithTwoParameters() {
        CreatedClass aClass = new LambdaClass("org.drools.modelcompiler.util.lambdareplace")
                .createClass("(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) -> p1.getAge() > p2.getAge()");

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace;\n" +
                "" +
                "public class LambdaDC57C20B4AF3C2BFEB2552943994B6F7 {\n" +
                "\n" +
                "        public java.lang.Boolean apply(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) {\n" +
                "            return p1.getAge() > p2.getAge();\n" +
                "        }\n" +
                "    }\n";

        assertThat(aClass.getCompilationUnitAsString(), equalToIgnoringWhiteSpace(expectedResult));

    }
}