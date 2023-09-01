package org.drools.model.codegen.execmodel.util.lambdareplace;

import java.util.ArrayList;

import org.junit.Test;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.model.codegen.execmodel.util.lambdareplace.MaterializedLambdaTestUtils.verifyCreatedClass;

public class MaterializedLambdaExtractorTest {

    @Test
    public void createExtractor() {
        CreatedClass aClass = new MaterializedLambdaExtractor("org.drools.model.codegen.execmodel.util.lambdareplace", "rulename", toClassOrInterfaceType(String.class))
                .create("(org.drools.model.codegen.execmodel.domain.Person p1) -> p1.getName()", new ArrayList<>(), new ArrayList<>());

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.model.codegen.execmodel.util.lambdareplace.P21;\n" +
                "import static rulename.*;\n" +
                "import org.drools.modelcompiler.dsl.pattern.D;\n" +
                "" +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaExtractor217E0777029653C7CDBC9BB1C80E36A2 implements org.drools.model.functions.Function1<org.drools.model.codegen.execmodel.domain.Person, java.lang.String>, org.drools.model.functions.HashedExpression  {\n" +
                " INSTANCE; \n" +
                "public static final String EXPRESSION_HASH = \"3346D1DB33D0928AC9890D01D89B5615\";" +
                "    public java.lang.String getExpressionHash() {\n" +
                "        return EXPRESSION_HASH;\n" +
                "    }" +
                "        @Override()\n" +
                "        public java.lang.String apply(org.drools.model.codegen.execmodel.domain.Person p1) {\n" +
                "            return p1.getName();\n" +
                "        }\n" +
                "    }\n";

        verifyCreatedClass(aClass, expectedResult);
    }
}