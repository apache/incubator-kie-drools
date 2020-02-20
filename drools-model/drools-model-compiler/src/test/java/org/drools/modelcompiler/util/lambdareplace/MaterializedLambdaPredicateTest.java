package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;

import org.junit.Test;

import static org.drools.modelcompiler.util.lambdareplace.Util.newLine;


public class MaterializedLambdaPredicateTest {

    PostProcessedCompare postProcessedCompare = new PostProcessedCompare();

    @Test
    public void createClassWithOneParameter() {
        CreatedClass result = new MaterializedLambdaPredicate("org.drools.modelcompiler.util.lambdareplace", "rulename")
                .create("(org.drools.modelcompiler.domain.Person p) -> p.getAge() > 35", new ArrayList<>(), new ArrayList());

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace;" + newLine() +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaPredicateE80C351B847736E5C59FD9AEAA280D96 implements org.drools.model.functions.Predicate1<org.drools.modelcompiler.domain.Person> {" + newLine() +
                " INSTANCE; " + newLine()  +
                "public static final String EXPRESSION_HASH = \"4DEB93975D9859892B1A5FD4B38E2155\";" +
                "        @Override()" + newLine()  +
                "        public boolean test(org.drools.modelcompiler.domain.Person p) {" + newLine()  +
                "            return p.getAge() > 35;" + newLine()  +
                "        }" + newLine()  +
                "    }" + newLine() ;

        postProcessedCompare.compareIgnoringHash(result.getCompilationUnitAsString(), expectedResult);

    }

    @Test
    public void createClassWithTwoParameters() {
        CreatedClass result = new MaterializedLambdaPredicate("org.drools.modelcompiler.util.lambdareplace", "rulename")
                .create("(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) -> p1.getAge() > p2.getAge()", new ArrayList<>(), new ArrayList());

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace;" + newLine()  +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaPredicate6E2AF92FF1E00863A2D794A7A592B911 implements org.drools.model.functions.Predicate2<org.drools.modelcompiler.domain.Person, org.drools.modelcompiler.domain.Person>  {" + newLine()  +
                " INSTANCE; " + newLine()  +
                "public static final String EXPRESSION_HASH = \"DC57C20B4AF3C2BFEB2552943994B6F7\";" +
                "        @Override()" + newLine()  +
                "        public boolean test(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) {" + newLine()  +
                "            return p1.getAge() > p2.getAge();" + newLine()  +
                "        }" + newLine()  +
                "    }" + newLine() ;

        postProcessedCompare.compareIgnoringHash(result.getCompilationUnitAsString(), expectedResult);

    }

}