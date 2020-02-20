package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;

import org.junit.Test;

import static org.drools.modelcompiler.util.lambdareplace.Util.newLine;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.*;

public class MaterializedLambdaExtractorTest {

    @Test
    public void createExtractor() {
        CreatedClass aClass = new MaterializedLambdaExtractor("org.drools.modelcompiler.util.lambdareplace", "rulename", String.class.getCanonicalName())
                .create("(org.drools.modelcompiler.domain.Person p1) -> p1.getName()", new ArrayList<>(), new ArrayList<>());

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace;" + newLine()  +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaExtractor0016105A319476235509F3705EF72E4F implements org.drools.model.functions.Function1<org.drools.modelcompiler.domain.Person, java.lang.String>  {" + newLine()  +
                " INSTANCE; " + newLine()  +
                "public static final String EXPRESSION_HASH = \"133AF281814F16840FE105EF6D339F8A\";" +
                "        @Override()" + newLine()  +
                "        public java.lang.String apply(org.drools.modelcompiler.domain.Person p1) {" + newLine()  +
                "            return p1.getName();" + newLine()  +
                "        }" + newLine()  +
                "    }" + newLine() ;

        assertThat(aClass.getCompilationUnitAsString(), equalToIgnoringWhiteSpace(expectedResult));
    }
}