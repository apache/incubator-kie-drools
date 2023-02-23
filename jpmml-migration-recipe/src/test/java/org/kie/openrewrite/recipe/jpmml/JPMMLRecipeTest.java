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


class JPMMLRecipeTest implements RewriteTest {

    private static final String JPMML_RECIPE_NAME = "org.kie.openrewrite.recipe.jpmml.JPMMLRecipe";

    @Override
    public void defaults(RecipeSpec spec) {
        List<Path> paths =JavaParser.runtimeClasspath();
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/rewrite/rewrite.yml")) {
            spec.recipe(inputStream, JPMML_RECIPE_NAME);
            spec.parser(Java11Parser.builder()
                    .classpath(paths)
                    .logCompilationWarningsAndErrors(true));
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
                "        new DataDictionary().addStrings(dataFields.toArray(new String[0]));\n" +
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
                "import org.dmg.pmml.Model;\n" +
                "\n" +
                "public class Stub extends Model {\n" +
                "\n" +
                "}";
        @Language("java")
        String after = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.Model;\n" +
                "\n" +
                "public class Stub extends Model {\n" +
                "    @Override\n" +
                "    public MiningFunction requireMiningFunction() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public MiningSchema requireMiningSchema() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "}";
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
                "import org.dmg.pmml.FieldName;\n" +
                "class FooBar {\n" +
                "static public void method() {\n" +
                " String fieldName =  String.valueOf(\"OUTPUT_\");\n" +
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
                "import org.dmg.pmml.FieldName;\n" +
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
                "import org.dmg.pmml.FieldName;\n" +
                "class FooBar {\n" +
                "static public void method() {\n" +
                "System.out.println( String.valueOf(\"OUTPUT_\"));\n" +
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