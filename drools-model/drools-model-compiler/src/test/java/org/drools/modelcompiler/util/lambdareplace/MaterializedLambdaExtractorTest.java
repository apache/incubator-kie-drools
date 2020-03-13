package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;

import org.junit.Test;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.*;

public class MaterializedLambdaExtractorTest {

    @Test
    public void createExtractor() {
        CreatedClass aClass = new MaterializedLambdaExtractor("org.drools.modelcompiler.util.lambdareplace", "rulename", String.class.getCanonicalName())
                .create("(org.drools.modelcompiler.domain.Person p1) -> p1.getName()", new ArrayList<>(), new ArrayList<>());

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace.PCB;\n" +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaExtractorCBFCD0FE557DC6A623D0DF78A531F1EA implements org.drools.model.functions.Function1<org.drools.modelcompiler.domain.Person, java.lang.String>  {\n" +
                " INSTANCE; \n" +
                "public static final String EXPRESSION_HASH = \"133AF281814F16840FE105EF6D339F8A\";" +
                "        @Override()\n" +
                "        public java.lang.String apply(org.drools.modelcompiler.domain.Person p1) {\n" +
                "            return p1.getName();\n" +
                "        }\n" +
                "    }\n";

        assertThat(aClass.getCompilationUnitAsString(), equalToIgnoringWhiteSpace(expectedResult));
    }
}