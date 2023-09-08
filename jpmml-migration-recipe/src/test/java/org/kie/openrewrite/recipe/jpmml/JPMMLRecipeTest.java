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
package org.kie.openrewrite.recipe.jpmml;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.java.Assertions;
import org.openrewrite.java.Java11Parser;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import static org.kie.openrewrite.recipe.jpmml.JPMMLVisitor.getNewJPMMLModelPath;


public class JPMMLRecipeTest implements RewriteTest {

    private static final String JPMML_RECIPE_NAME = "org.kie.openrewrite.recipe.jpmml.JPMMLRecipe";

    @Override
    public void defaults(RecipeSpec spec) {
        List<Path> paths = JavaParser.runtimeClasspath();
        Path newJpmmlModel = getNewJPMMLModelPath();
        paths.add(newJpmmlModel);
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/rewrite/rewrite.yml")) {
            assert inputStream != null;
            spec.recipe(inputStream, JPMML_RECIPE_NAME);
            spec.parser(Java11Parser.builder()
                    .classpath(paths)
                    .logCompilationWarningsAndErrors(true)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void changeInstantiation_DataDictionary() {
        @Language("java")
        String before = "package com.yourorg;\n" +
                "\n" +
                "import java.util.List;\n" +
                "import org.dmg.pmml.DataDictionary;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "\n" +
                "public class Stub {\n" +
                "\n" +
                "    public String hello(List<DataField> dataFields) {\n" +
                "        new DataDictionary(dataFields);\n" +
                "        return \"Hello from com.yourorg.FooLol!\";\n" +
                "    }\n" +
                "\n" +
                "}";
        @Language("java")
        String after = "package com.yourorg;\n" +
                "\n" +
                "import java.util.List;\n" +
                "import org.dmg.pmml.DataDictionary;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "\n" +
                "public class Stub {\n" +
                "\n" +
                "    public String hello(List<DataField> dataFields) {\n" +
                "        new DataDictionary().addDataFields(dataFields.toArray(new org.dmg.pmml.DataField[0]));\n" +
                "        return \"Hello from com.yourorg.FooLol!\";\n" +
                "    }\n" +
                "\n" +
                "}";
        rewriteRun(
                Assertions.java(before, after)
        );
    }

    @Test
    public void addMissingMethods_Model() {
        @Language("java")
        String before = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.LocalTransformations;\n" +
                "import org.dmg.pmml.MathContext;\n" +
                "import org.dmg.pmml.MiningFunction;\n" +
                "import org.dmg.pmml.MiningSchema;\n" +
                "import org.dmg.pmml.Model;\n" +
                "import org.dmg.pmml.Visitor;\n" +
                "import org.dmg.pmml.VisitorAction;\n" +
                "\n" +
                "public class SubModel extends Model {\n" +
                "    @Override\n" +
                "    public String getModelName() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Model setModelName(String modelName) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public MiningFunction getMiningFunction() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Model setMiningFunction(MiningFunction miningFunction) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public String getAlgorithmName() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Model setAlgorithmName(String algorithmName) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public boolean isScorable() {\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Model setScorable(Boolean scorable) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public MathContext getMathContext() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Model setMathContext(MathContext mathContext) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public MiningSchema getMiningSchema() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Model setMiningSchema(MiningSchema miningSchema) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public LocalTransformations getLocalTransformations() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Model setLocalTransformations(LocalTransformations localTransformations) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public VisitorAction accept(Visitor visitor) {\n" +
                "        return null;\n" +
                "    }\n" +
                "}\n";
        @Language("java")
        String after = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.LocalTransformations;\n" +
                "import org.dmg.pmml.MathContext;\n" +
                "import org.dmg.pmml.MiningFunction;\n" +
                "import org.dmg.pmml.MiningSchema;\n" +
                "import org.dmg.pmml.Model;\n" +
                "import org.dmg.pmml.Visitor;\n" +
                "import org.dmg.pmml.VisitorAction;\n" +
                "\n" +
                "public class SubModel extends Model {\n" +
                "    @Override\n" +
                "    public String getModelName() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Model setModelName(String modelName) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public MiningFunction getMiningFunction() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Model setMiningFunction(MiningFunction miningFunction) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public String getAlgorithmName() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Model setAlgorithmName(String algorithmName) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public boolean isScorable() {\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Model setScorable(Boolean scorable) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public MathContext getMathContext() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Model setMathContext(MathContext mathContext) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public MiningSchema getMiningSchema() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Model setMiningSchema(MiningSchema miningSchema) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public LocalTransformations getLocalTransformations() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Model setLocalTransformations(LocalTransformations localTransformations) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public VisitorAction accept(Visitor visitor) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public MiningFunction requireMiningFunction() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public MiningSchema requireMiningSchema() {\n" +
                "        return null;\n" +
                "    }\n" +
                "}\n";
        rewriteRun(
                Assertions.java(before, after)
        );
    }

    @Test
    public void changeImports() {
        @Language("java")
        String before = "package com.yourorg;\n" +
                "import org.jpmml.model.inlinetable.InputCell;\n" +
                "class FooBar {\n" +
                "static public void method() {\n" +
                "InputCell input = null;\n" +
                "}\n" +
                "}";
        @Language("java")
        String after = "package com.yourorg;\n" +
                "\n" +
                "import org.jpmml.model.cells.InputCell;\n" +
                "\n" +
                "class FooBar {\n" +
                "static public void method() {\n" +
                "InputCell input = null;\n" +
                "}\n" +
                "}";
        rewriteRun(
                Assertions.java(before, after)
        );
    }

    @Test
    public void changeFieldNameVariableDeclaration() {
        @Language("java")
        String before = "package com.yourorg;\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "class FooBar {\n" +
                "static public void method() {\n" +
                "FieldName fieldName = FieldName.create(\"OUTPUT_\");\n" +
                "}\n" +
                "}";
        @Language("java")
        String after = "package com.yourorg;\n" +
                "class FooBar {\n" +
                "static public void method() {\n" +
                " String fieldName =\"OUTPUT_\";\n" +
                "}\n" +
                "}";
        rewriteRun(
                Assertions.java(before, after)
        );
    }

    @Test
    public void changeFieldNameVariableNull() {
        @Language("java")
        String before = "package com.yourorg;\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "class FooBar {\n" +
                "static public void method() {\n" +
                "FieldName fieldName = null;\n" +
                "}\n" +
                "}";
        @Language("java")
        String after = "package com.yourorg;\n" +
                "class FooBar {\n" +
                "static public void method() {\n" +
                " String fieldName = null;\n" +
                "}\n" +
                "}";
        rewriteRun(
                Assertions.java(before, after)
        );
    }

    @Test
    public void removeFieldNameCreate() {
        @Language("java")
        String before = "package com.yourorg;\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "class FooBar {\n" +
                "static public void method() {\n" +
                "System.out.println(FieldName.create(\"OUTPUT_\"));\n" +
                "}\n" +
                "}";
        @Language("java")
        String after = "package com.yourorg;\n" +
                "class FooBar {\n" +
                "static public void method() {\n" +
                "System.out.println(\"OUTPUT_\");\n" +
                "}\n" +
                "}";
        rewriteRun(
                Assertions.java(before, after)
        );
    }

    @Test
    public void removeFieldNameGetValue() {
        @Language("java")
        String before = "package com.yourorg;\n" +
                "import org.dmg.pmml.OutputField;\n" +
                "class FooBar {\n" +
                "static public void method(OutputField toConvert) {\n" +
                "final String name = toConvert.getName() != null ? toConvert.getName().getValue() : null;\n" +
                "}\n" +
                "}";
        String after = "package com.yourorg;\n" +
                "import org.dmg.pmml.OutputField;\n" +
                "class FooBar {\n" +
                "static public void method(OutputField toConvert) {\n" +
                "final String name = toConvert.getName() != null ?toConvert.getName() : null;\n" +
                "}\n" +
                "}";
        rewriteRun(
                Assertions.java(before, after)
        );
    }


    @Test
    public void changeInstantiation_ScoreDistribution() {
        @Language("java")
        String before = "package com.yourorg;\n" +
                "import org.dmg.pmml.ScoreDistribution;\n" +
                "class FooBar {\n" +
                "static public void method() {\n" +
                "ScoreDistribution toReturn = new ScoreDistribution();\n" +
                "}\n" +
                "}";
        @Language("java")
        String after = "package com.yourorg;\n" +
                "import org.dmg.pmml.ComplexScoreDistribution;\n" +
                "import org.dmg.pmml.ScoreDistribution;\n" +
                "\n" +
                "class FooBar {\n" +
                "static public void method() {\n" +
                "ScoreDistribution toReturn = new ComplexScoreDistribution();\n" +
                "}\n" +
                "}";
        rewriteRun(
                Assertions.java(before, after)
        );
    }

}