package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;

import org.junit.Test;

import static org.drools.modelcompiler.util.lambdareplace.MaterializedLambdaTestUtils.verifyCreatedClass;

public class MaterializedLambdaExtractorTest {

    @Test
    public void createExtractor() {
        CreatedClass aClass = new MaterializedLambdaExtractor("org.drools.modelcompiler.util.lambdareplace", "rulename", String.class.getCanonicalName())
                .create("(org.drools.modelcompiler.domain.Person p1) -> p1.getName()", new ArrayList<>(), new ArrayList<>());

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace.PE2;\n" +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaExtractorE266E7B47BEF772A569E939DF87A37BB implements org.drools.model.functions.Function1<org.drools.modelcompiler.domain.Person, java.lang.String>, org.drools.model.functions.HashedExpression  {\n" +
                " INSTANCE; \n" +
                "public static final String EXPRESSION_HASH = \"133AF281814F16840FE105EF6D339F8A\";" +
                "    public java.lang.String getExpressionHash() {\n" +
                "        return EXPRESSION_HASH;\n" +
                "    }" +
                "        @Override()\n" +
                "        public java.lang.String apply(org.drools.modelcompiler.domain.Person p1) {\n" +
                "            return p1.getName();\n" +
                "        }\n" +
                "    }\n";

        verifyCreatedClass(aClass, expectedResult);
    }
}