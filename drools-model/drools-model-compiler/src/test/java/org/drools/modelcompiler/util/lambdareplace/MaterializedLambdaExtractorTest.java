package org.drools.modelcompiler.util.lambdareplace;

import org.junit.Test;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.*;

public class MaterializedLambdaExtractorTest {

    @Test
    public void createExtractor() {
        CreatedClass aClass = new MaterializedLambdaPredicate("org.drools.modelcompiler.util.lambdareplace")
                .createPredicate("(org.drools.modelcompiler.domain.Person p1) -> p1.getName()");

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace;\n" +
                "" +
                "public class LambdaDC57C20B4AF3C2BFEB2552943994B6F7 implements org.drools.model.functions.Function1<org.drools.modelcompiler.domain.Person, java.lang.String>  {\n" +
                "\n" +
                "        @Override()\n" +
                "        public String test(org.drools.modelcompiler.domain.Person p1) {\n" +
                "            return p1.getName();\n" +
                "        }\n" +
                "    }\n";

        assertThat(aClass.getCompilationUnitAsString(), equalToIgnoringWhiteSpace(expectedResult));

    }
}