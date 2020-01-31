package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;

import org.junit.Test;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertThat;


public class MaterializedLambdaPredicateTest {

    @Test
    public void createClassWithOneParameter() {
        CreatedClass aClass = new MaterializedLambdaPredicate("org.drools.modelcompiler.util.lambdareplace", "rulename")
                .create("(org.drools.modelcompiler.domain.Person p) -> p.getAge() > 35", new ArrayList<>(), new ArrayList());

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace;\n" +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaPredicateE80C351B847736E5C59FD9AEAA280D96 implements org.drools.model.functions.Predicate1<org.drools.modelcompiler.domain.Person> {\n" +
                " INSTANCE; \n" +
                "public static final String EXPRESSION_HASH = \"4DEB93975D9859892B1A5FD4B38E2155\";" +
                "        @Override()\n" +
                "        public boolean test(org.drools.modelcompiler.domain.Person p) {\n" +
                "            return p.getAge() > 35;\n" +
                "        }\n" +
                "    }\n";

        assertThat(aClass.getCompilationUnitAsString(), equalToIgnoringWhiteSpace(expectedResult));

    }

    @Test
    public void createClassWithTwoParameters() {
        CreatedClass aClass = new MaterializedLambdaPredicate("org.drools.modelcompiler.util.lambdareplace", "rulename")
                .create("(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) -> p1.getAge() > p2.getAge()", new ArrayList<>(), new ArrayList());

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace;\n" +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaPredicate6E2AF92FF1E00863A2D794A7A592B911 implements org.drools.model.functions.Predicate2<org.drools.modelcompiler.domain.Person, org.drools.modelcompiler.domain.Person>  {\n" +
                " INSTANCE; \n" +
                "public static final String EXPRESSION_HASH = \"DC57C20B4AF3C2BFEB2552943994B6F7\";" +
                "        @Override()\n" +
                "        public boolean test(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) {\n" +
                "            return p1.getAge() > p2.getAge();\n" +
                "        }\n" +
                "    }\n";

        assertThat(aClass.getCompilationUnitAsString(), equalToIgnoringWhiteSpace(expectedResult));

    }
}