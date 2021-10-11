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
        PredicateInformation predicateInformation = new PredicateInformation("p.age > 35", "rule1", "rulefilename1.drl");
        predicateInformation.addRuleNames("rule2", "rulefilename2.drl");
        predicateInformation.addRuleNames("rule3", "rulefilename3.drl");
        CreatedClass aClass = new MaterializedLambdaPredicate("org.drools.modelcompiler.util.lambdareplace",
                "rulename",
                predicateInformation)
                .create("(org.drools.modelcompiler.domain.Person p) -> p.getAge() > 35", new ArrayList<>(), new ArrayList());
        String classNameWithPackage = aClass.getClassNameWithPackage();
        String expectedPackageName = classNameWithPackage.substring(0, classNameWithPackage.lastIndexOf('.'));
        String expectedClassName = classNameWithPackage.substring(classNameWithPackage.lastIndexOf('.')+1);
        //language=JAVA
        String expectedResult = "" +
                "package PACKAGE_TOREPLACE;\n" +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum CLASS_TOREPLACE implements org.drools.model.functions.Predicate1<org.drools.modelcompiler.domain.Person>, org.drools.model.functions.HashedExpression {\n" +
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
                "            org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation(\"p.age > 35\");\n" +
                "            info.addRuleNames(\"rule1\", \"rulefilename1.drl\", \"rule2\", \"rulefilename2.drl\", \"rule3\", \"rulefilename3.drl\");" +
                "            return info;\n" +
                "        }\n" +
                "" +
                "    }\n";
        expectedResult = expectedResult
                .replace("PACKAGE_TOREPLACE", expectedPackageName)
                .replace("CLASS_TOREPLACE", expectedClassName);
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