package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;

import org.drools.model.functions.PredicateInformation;
import org.junit.Test;

import static org.drools.modelcompiler.util.lambdareplace.MaterializedLambdaTestUtils.verifyCreatedClass;


public class MaterializedLambdaPredicateTest {

    @Test
    public void createClassWithOneParameter() {
        CreatedClass aClass = new MaterializedLambdaPredicate("org.drools.modelcompiler.util.lambdareplace",
                                                              "rulename",
                                                              new PredicateInformation("p.age > 35", "rule1", "rulefilename.drl"))
                .create("(org.drools.modelcompiler.domain.Person p) -> p.getAge() > 35", new ArrayList<>(), new ArrayList());

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace.P19;\n" +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaPredicate199715077FD778E73FC7EC5041C3B4ED implements org.drools.model.functions.Predicate1<org.drools.modelcompiler.domain.Person> {\n" +
                " INSTANCE; \n" +
                "public static final String EXPRESSION_HASH = \"4DEB93975D9859892B1A5FD4B38E2155\";" +
                "        @Override()\n" +
                "        public boolean test(org.drools.modelcompiler.domain.Person p) throws java.lang.Exception {\n" +
                "            return p.getAge() > 35;\n" +
                "        }\n" +
                "        @Override()\n" +
                "        public org.drools.model.functions.PredicateInformation predicateInformation() {\n" +
                "            return new org.drools.model.functions.PredicateInformation(\"p.age > 35\", \"rule1\", \"rulefilename.drl\");" +
                "        }\n" +
                "    }\n";

        verifyCreatedClass(aClass, expectedResult);
    }

    @Test
    public void createClassWithTwoParameters() {
        CreatedClass aClass = new MaterializedLambdaPredicate("org.drools.modelcompiler.util.lambdareplace", "rulename", PredicateInformation.EMPTY_PREDICATE_INFORMATION)
                .create("(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) -> p1.getAge() > p2.getAge()", new ArrayList<>(), new ArrayList());

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace.PF5;\n" +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaPredicateF51471B33192ACC0504CA26C719BFCB7 implements org.drools.model.functions.Predicate2<org.drools.modelcompiler.domain.Person, org.drools.modelcompiler.domain.Person>  {\n" +
                " INSTANCE; \n" +
                "public static final String EXPRESSION_HASH = \"DC57C20B4AF3C2BFEB2552943994B6F7\";" +
                "        @Override()\n" +
                "        public boolean test(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) throws java.lang.Exception {\n" +
                "            return p1.getAge() > p2.getAge();\n" +
                "        }\n" +
                "    }\n";

        verifyCreatedClass(aClass, expectedResult);
    }

}