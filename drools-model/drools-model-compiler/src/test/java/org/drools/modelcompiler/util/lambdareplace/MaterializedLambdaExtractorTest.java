package org.drools.modelcompiler.util.lambdareplace;

import org.junit.Test;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.*;

public class MaterializedLambdaExtractorTest {

    @Test
    public void createExtractor() {
        CreatedClass aClass = new MaterializedLambdaExtractor("org.drools.modelcompiler.util.lambdareplace")
                .create("(org.drools.modelcompiler.domain.Person p1) -> p1.getName()", String.class.getCanonicalName());

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace;\n" +
                "" +
                "public class LambdaExtractor133AF281814F16840FE105EF6D339F8A implements org.drools.model.functions.Function1<org.drools.modelcompiler.domain.Person, java.lang.String>  {\n" +
                "\n" +
                "        @Override()\n" +
                "        public java.lang.String test(org.drools.modelcompiler.domain.Person p1) {\n" +
                "            return p1.getName();\n" +
                "        }\n" +
                "    }\n";

        assertThat(aClass.getCompilationUnitAsString(), equalToIgnoringWhiteSpace(expectedResult));

    }
}