/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
                "package org.drools.modelcompiler.util.lambdareplace.PEE;\n" +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaPredicateEE863708F0E52E1DD7FBE6537EB76024 implements org.drools.model.functions.Predicate1<org.drools.modelcompiler.domain.Person>, org.drools.model.functions.HashedExpression {\n" +
                " INSTANCE; \n" +
                "public static final String EXPRESSION_HASH = \"4DEB93975D9859892B1A5FD4B38E2155\";" +
                "    public java.lang.String getExpressionHash() {\n" +
                "        return EXPRESSION_HASH;\n" +
                "    } " +
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
                "package org.drools.modelcompiler.util.lambdareplace.PB4;\n" +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaPredicateB43A7DAEE6E92A2B4A203826B1336F22 implements org.drools.model.functions.Predicate2<org.drools.modelcompiler.domain.Person, org.drools.modelcompiler.domain.Person>, org.drools.model.functions.HashedExpression  {\n" +
                " INSTANCE; \n" +
                "public static final String EXPRESSION_HASH = \"DC57C20B4AF3C2BFEB2552943994B6F7\";" +
                "       public java.lang.String getExpressionHash() {\n" +
                "           return EXPRESSION_HASH;\n" +
                "       }" +
                "        @Override()\n" +
                "        public boolean test(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) throws java.lang.Exception {\n" +
                "            return p1.getAge() > p2.getAge();\n" +
                "        }\n" +
                "    }\n";

        verifyCreatedClass(aClass, expectedResult);
    }

}