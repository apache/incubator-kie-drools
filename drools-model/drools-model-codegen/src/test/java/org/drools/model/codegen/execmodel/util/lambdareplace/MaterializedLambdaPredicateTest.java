/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.util.lambdareplace;

import java.util.ArrayList;

import org.drools.model.functions.PredicateInformation;
import org.junit.Test;

import static org.drools.model.codegen.execmodel.util.lambdareplace.MaterializedLambdaTestUtils.verifyCreatedClass;


public class MaterializedLambdaPredicateTest {

    @Test
    public void createClassWithOneParameter() {
        PredicateInformation predicateInformation = new PredicateInformation("p.age > 35", "rule1", "rulefilename1.drl");
        predicateInformation.addRuleNames("rule2", "rulefilename2.drl");
        predicateInformation.addRuleNames("rule3", "rulefilename3.drl");
        CreatedClass aClass = new MaterializedLambdaPredicate("org.drools.modelcompiler.util.lambdareplace",
                "rulename",
                predicateInformation)
                .create("(org.drools.model.codegen.execmodel.domain.Person p) -> p.getAge() > 35", new ArrayList<>(), new ArrayList());
        String classNameWithPackage = aClass.getClassNameWithPackage();
        String expectedPackageName = classNameWithPackage.substring(0, classNameWithPackage.lastIndexOf('.'));
        String expectedClassName = classNameWithPackage.substring(classNameWithPackage.lastIndexOf('.')+1);
        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace.PEA;\n" +
                "\n" +
                "import static rulename.*;\n" +
                "import org.drools.modelcompiler.dsl.pattern.D;\n" +
                "\n" +
                "@org.drools.compiler.kie.builder.MaterializedLambda()\n" +
                "public enum LambdaPredicateEA4398DA4D9410AA6946A437AA1BBE61 implements org.drools.model.functions.Predicate1<org.drools.model.codegen.execmodel.domain.Person>, org.drools.model.functions.HashedExpression {\n" +
                "\n" +
                "    INSTANCE;\n" +
                "\n" +
                "    public static final String EXPRESSION_HASH = \"547DB838ABC95ED2C85D276E81E88DFE\";\n" +
                "\n" +
                "    public java.lang.String getExpressionHash() {\n" +
                "        return EXPRESSION_HASH;\n" +
                "    }\n" +
                "\n" +
                "    @Override()\n" +
                "    public boolean test(org.drools.model.codegen.execmodel.domain.Person p) throws java.lang.Exception {\n" +
                "        return p.getAge() > 35;\n" +
                "    }\n" +
                "\n" +
                "    @Override()\n" +
                "    public org.drools.model.functions.PredicateInformation predicateInformation() {\n" +
                "        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation(\"p.age > 35\");\n" +
                "        info.addRuleNames(\"rule1\", \"rulefilename1.drl\", \"rule2\", \"rulefilename2.drl\", \"rule3\", \"rulefilename3.drl\");\n" +
                "        return info;\n" +
                "    }\n" +
                "}\n";
        expectedResult = expectedResult
                .replace("PACKAGE_TOREPLACE", expectedPackageName)
                .replace("CLASS_TOREPLACE", expectedClassName);
        verifyCreatedClass(aClass, expectedResult);
    }

    @Test
    public void createClassWithTwoParameters() {
        CreatedClass aClass = new MaterializedLambdaPredicate("org.drools.modelcompiler.util.lambdareplace", "rulename", PredicateInformation.EMPTY_PREDICATE_INFORMATION)
                .create("(org.drools.model.codegen.execmodel.domain.Person p1, org.drools.model.codegen.execmodel.domain.Person p2) -> p1.getAge() > p2.getAge()", new ArrayList<>(), new ArrayList());

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace.PC0;\n" +
                "\n" +
                "import static rulename.*;\n" +
                "import org.drools.modelcompiler.dsl.pattern.D;\n" +
                "\n" +
                "@org.drools.compiler.kie.builder.MaterializedLambda()\n" +
                "public enum LambdaPredicateC0B44DB8E400F27965585A7108E25888 implements org.drools.model.functions.Predicate2<org.drools.model.codegen.execmodel.domain.Person, org.drools.model.codegen.execmodel.domain.Person>, org.drools.model.functions.HashedExpression {\n" +
                "\n" +
                "    INSTANCE;\n" +
                "\n" +
                "    public static final String EXPRESSION_HASH = \"B6C692B2A176A26709B13F34DDFA9BA6\";\n" +
                "\n" +
                "    public java.lang.String getExpressionHash() {\n" +
                "        return EXPRESSION_HASH;\n" +
                "    }\n" +
                "\n" +
                "    @Override()\n" +
                "    public boolean test(org.drools.model.codegen.execmodel.domain.Person p1, org.drools.model.codegen.execmodel.domain.Person p2) throws java.lang.Exception {\n" +
                "        return p1.getAge() > p2.getAge();\n" +
                "    }\n" +
                "}\n";

        verifyCreatedClass(aClass, expectedResult);
    }

}