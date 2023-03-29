package org.kie.openrewrite.recipe.jpmml;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.java.Assertions;
import org.openrewrite.java.Java11Parser;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import java.nio.file.Path;
import java.util.List;

class JPMMLCodeRecipeTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        List<Path> paths = JavaParser.runtimeClasspath();
        spec.recipe(new JPMMLCodeRecipe("org.dmg.pmml.ScoreDistribution",
                                        "org.dmg.pmml.ComplexScoreDistribution"));
        spec.parser(Java11Parser.builder()
                            .classpath(paths)
                            .logCompilationWarningsAndErrors(true));
    }

    @Test
    void removeFieldNameCreate() {
        @Language("java")
        String before = "package com.yourorg;\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "class FooBar {\n" +
                "static void method() {\n" +
                "System.out.println(FieldName.create(\"OUTPUT_\"));\n" +
                "}\n" +
                "}";
        @Language("java")
        String after = "package com.yourorg;\n" +
                "class FooBar {\n" +
                "static void method() {\n" +
                "System.out.println(\"OUTPUT_\");\n" +
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
                "static void method() {\n" +
                "ScoreDistribution toReturn = new ScoreDistribution();\n" +
                "}\n" +
                "}";
        @Language("java")
        String after = "package com.yourorg;\n" +
                "import org.dmg.pmml.ComplexScoreDistribution;\n" +
                "import org.dmg.pmml.ScoreDistribution;\n" +
                "\n" +
                "class FooBar {\n" +
                "static void method() {\n" +
                "ScoreDistribution toReturn = new ComplexScoreDistribution();\n" +
                "}\n" +
                "}";
        rewriteRun(
                Assertions.java(before, after)
        );
    }

    @Test
    public void changeInstantiation_DataDictionary() {
        @Language("java")
        String before = "package com.yourorg;\n" +
                "import java.util.List;\n" +
                "import org.dmg.pmml.DataDictionary;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "class FooBar {\n" +
                "static void method(List<DataField> dataFields) {\n" +
                "DataDictionary dataDictionary = new DataDictionary(dataFields);\n" +
                "}\n" +
                "}";
        @Language("java")
        String after = "package com.yourorg;\n" +
                "import java.util.List;\n" +
                "import org.dmg.pmml.DataDictionary;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "class FooBar {\n" +
                "static void method(List<DataField> dataFields) {\n" +
                "DataDictionary dataDictionary = new DataDictionary().addDataFields(dataFields.toArray(new org.dmg.pmml.DataField[0]));\n" +
                "}\n" +
                "}";
        rewriteRun(
                Assertions.java(before, after)
        );
    }

    @Test
    public void changeUsage_FieldNameCreateWithBinary() {
        @Language("java")
        String before = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.DataField;\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "\n" +
                "public class Stub {\n" +
                "      \n" +
                "    public void hello(DataField dataField) {\n" +
                "        System.out.println(FieldName.create(\"OUTPUT_\" + dataField.getName().getValue()));\n" +
                "    }\n" +
                "\n" +
                "}";
        @Language("java")
        String after = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.DataField;\n" +
                "\n" +
                "public class Stub {\n" +
                "      \n" +
                "    public void hello(DataField dataField) {\n" +
                "        System.out.println(\"OUTPUT_\" +dataField.getName());\n" +
                "    }\n" +
                "\n" +
                "}";
        rewriteRun(
                Assertions.java(before, after)
        );
    }

}