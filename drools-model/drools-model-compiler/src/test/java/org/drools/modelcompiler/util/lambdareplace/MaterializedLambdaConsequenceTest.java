package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;

import org.junit.Test;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.*;

public class MaterializedLambdaConsequenceTest {

    @Test
    public void createConsequence() {
        CreatedClass aClass = new MaterializedLambdaConsequence("org.drools.modelcompiler.util.lambdareplace", "rulename")
                .create("(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) -> result.setValue( p1.getName() + \" is older than \" + p2.getName())", new ArrayList<>(), new ArrayList<>());

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace;\n" +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "\n"+
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaConsequenceF20037424A777A005A60E661AB21E036 implements org.drools.model.functions.Block2<org.drools.modelcompiler.domain.Person, org.drools.modelcompiler.domain.Person>  {\n" +
                "INSTANCE;\n" +
                "public static final String EXPRESSION_HASH = \"8305FF24AC76CB49E7AAE2C10356A105\";" +
                "        @Override()\n" +
                "        public void execute(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) throws java.lang.Exception {\n" +
                "            result.setValue(p1.getName() + \" is older than \" + p2.getName());\n" +
                "        }\n" +
                "    }\n";

        assertThat(aClass.getCompilationUnitAsString(), equalToIgnoringWhiteSpace(expectedResult));

    }
}