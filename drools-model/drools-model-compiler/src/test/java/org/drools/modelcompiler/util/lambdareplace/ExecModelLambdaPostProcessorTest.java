package org.drools.modelcompiler.util.lambdareplace;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.stmt.Statement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExecModelLambdaPostProcessorTest {

    @Test
    public void convertPatternLambdaExprLambda() {
        String dslInput =
                        "        Rule rule = rule(\"not\")\n" +
                        "                .build(\n" +
                        "                        pattern( oldestV ),\n" +
                        "                        not( pattern( otherV ).expr( \"exprA\", oldestV, (Person p1, Person p2) -> p1.getAge() > p2.getAge()) ),\n" +
                                "                                                           D.on(var_result).execute(defaultpkg.LambdaConsequenceA275DE7DF16C84609DF94D2C495A1094.INSTANCE));\n";


        Statement expression = StaticJavaParser.parseStatement(dslInput);

        PostProcessedExecModel postProcessedExecModel = new ExecModelLambdaPostProcessor().convertLambdas("mypackage", expression);

        String expectedResult =
                "        Rule rule = rule(\"not\")\n" +
                        "                .build(\n" +
                        "                        pattern( oldestV ),\n" +
                        "                        not( pattern( otherV ).expr( \"exprA\", oldestV, mypackage.LambdaPredicateE1D438AAC3AEAAFEE61CB8AFB5512703.INSTANCE)),\n" +
                        "                                                           D.on(var_result).execute(defaultpkg.LambdaConsequenceA275DE7DF16C84609DF94D2C495A1094.INSTANCE));\n";

        assertEquals(StaticJavaParser.parseStatement(expectedResult), StaticJavaParser.parseStatement(postProcessedExecModel.getConvertedBlockAsString()));

    }

    @Test
    public void convertPatternLambdaExprLambdaWithIndexing() {
        String dslInput = "   org.drools.model.Rule rule = D.rule(\"R\").build(D.pattern(var_$pattern_Person$2$).expr(\"593440B7603CA900F1A34F18497AB0EA\",\n" +
                "                                                                                              (Person _this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getName(),\n" +
                "                                                                                                                                                                        \"Mario\"),\n" +
                "                                                                                              D.reactOn(\"name\")).bind(var_$name,\n" +
                "                                                                                                                      (_this) -> _this.getName(),\n" +
                "                                                                                                                      D.reactOn(\"name\")),\n" +
"                                                           D.on(var_result).execute(defaultpkg.LambdaConsequenceA275DE7DF16C84609DF94D2C495A1094.INSTANCE));\n";

        Statement expression = StaticJavaParser.parseStatement(dslInput);

        PostProcessedExecModel postProcessedExecModel = new ExecModelLambdaPostProcessor().convertLambdas("mypackage", expression);

        String expectedResult =
                "   org.drools.model.Rule rule = D.rule(\"R\").build(D.pattern(var_$pattern_Person$2$).expr(\"593440B7603CA900F1A34F18497AB0EA\",\n" +
                        "                                                                                              mypackage.LambdaPredicateBEBA11F0E0DF8347E05E477F0678A25E.INSTANCE,\n" +
                        "                                                                                              D.reactOn(\"name\")).bind(var_$name,\n" +
                        "                                                                                                                      (_this) -> _this.getName(),\n" +
                        "                                                                                                                      D.reactOn(\"name\")),\n" +
                        "                                                           D.on(var_result).execute(defaultpkg.LambdaConsequenceA275DE7DF16C84609DF94D2C495A1094.INSTANCE));\n";


        assertEquals(StaticJavaParser.parseStatement(expectedResult), StaticJavaParser.parseStatement(postProcessedExecModel.getConvertedBlockAsString()));

    }

    @Test
    public void convertAlphaIndexedBy() {
        String dslInput = "org.drools.model.Rule rule = D.rule(\"rule1\").build(D.pattern(var_$pattern_DataType$1$).expr(\"2BEF0A093C29ADEB403C89AC2C2C807F\",\n" +
                "                                                                                                    defaultpkg.Lambda1F1B9676022CAF5489FB1C678F4D657E.INSTANCE,\n" +
                "                                                                                                    D.alphaIndexedBy(java.lang.String.class,\n" +
                "                                                                                                                     org.drools.model.Index.ConstraintType.EQUAL,\n" +
                "                                                                                                                     DomainClassesMetadataFA071C8FD678264E16EE56C749A15772.org_drools_modelcompiler_DataType_Metadata_INSTANCE.getPropertyIndex(\"field1\"),\n" +
                "                                                                                                                     (org.drools.modelcompiler.DataType _this) -> _this.getField1(),\n" +
                "                                                                                                                     \"FF\"),\n" +
                "                                                                                                    D.reactOn(\"field1\")).expr(\"77E38C041F31083DCD4B15C970628D1F\",\n" +
                "                                                                                                                              defaultpkg.LambdaAB4D983A6C15ED8C0C1BA898F5CDB6DD.INSTANCE,\n" +
                "                                                                                                                              D.alphaIndexedBy(java.lang.String.class,\n" +
                "                                                                                                                                               org.drools.model.Index.ConstraintType.EQUAL,\n" +
                "                                                                                                                                               DomainClassesMetadataFA071C8FD678264E16EE56C749A15772.org_drools_modelcompiler_DataType_Metadata_INSTANCE.getPropertyIndex(\"field2\"),\n" +
                "                                                                                                                                               (org.drools.modelcompiler.DataType _this) -> _this.getField2(),\n" +
                "                                                                                                                                               \"BBB\"),\n" +
                "                                                                                                                              D.reactOn(\"field2\")),\n" +
                "                                                           D.on(var_result).execute(defaultpkg.LambdaConsequenceA275DE7DF16C84609DF94D2C495A1094.INSTANCE));\n";


        Statement expression = StaticJavaParser.parseStatement(dslInput);

        PostProcessedExecModel postProcessedExecModel = new ExecModelLambdaPostProcessor().convertLambdas("mypackage", expression);

        String expectedResult = "org.drools.model.Rule rule = D.rule(\"rule1\").build(D.pattern(var_$pattern_DataType$1$).expr(\"2BEF0A093C29ADEB403C89AC2C2C807F\",\n" +
                "                                                                                                    defaultpkg.Lambda1F1B9676022CAF5489FB1C678F4D657E.INSTANCE,\n" +
                "                                                                                                    D.alphaIndexedBy(java.lang.String.class,\n" +
                "                                                                                                                     org.drools.model.Index.ConstraintType.EQUAL,\n" +
                "                                                                                                                     DomainClassesMetadataFA071C8FD678264E16EE56C749A15772.org_drools_modelcompiler_DataType_Metadata_INSTANCE.getPropertyIndex(\"field1\"),\n" +
                "                                                                                                                     mypackage.LambdaExtractor080E769B3FB0B51C41CF78CF95BDA10B.INSTANCE,\n" +
                "                                                                                                                     \"FF\"),\n" +
                "                                                                                                    D.reactOn(\"field1\")).expr(\"77E38C041F31083DCD4B15C970628D1F\",\n" +
                "                                                                                                                              defaultpkg.LambdaAB4D983A6C15ED8C0C1BA898F5CDB6DD.INSTANCE,\n" +
                "                                                                                                                              D.alphaIndexedBy(java.lang.String.class,\n" +
                "                                                                                                                                               org.drools.model.Index.ConstraintType.EQUAL,\n" +
                "                                                                                                                                               DomainClassesMetadataFA071C8FD678264E16EE56C749A15772.org_drools_modelcompiler_DataType_Metadata_INSTANCE.getPropertyIndex(\"field2\"),\n" +
                "                                                                                                                                                     mypackage.LambdaExtractor3C616E80B72DC643C4A5B5C0CF12EBAC.INSTANCE,\n" +
                "                                                                                                                                               \"BBB\"),\n" +
                "                                                                                                                              D.reactOn(\"field2\")),\n" +
                "                                                           D.on(var_result).execute(defaultpkg.LambdaConsequenceA275DE7DF16C84609DF94D2C495A1094.INSTANCE));\n";


        assertEquals(StaticJavaParser.parseStatement(expectedResult), StaticJavaParser.parseStatement(postProcessedExecModel.getConvertedBlockAsString()));

    }

    @Test
    public void convertConsequence() {
        String dslInput = "org.drools.model.Rule rule = D.rule(\"rule2\").build(D.pattern(var_$pattern_DataType$2$).expr(\"77E38C041F31083DCD4B15C970628D1F\",\n" +
                "                                                                                                    new defaultpkg.LambdaPredicateAB4D983A6C15ED8C0C1BA898F5CDB6DD(),\n" +
                "                                                                                                    D.alphaIndexedBy(java.lang.String.class,\n" +
                "                                                                                                                     org.drools.model.Index.ConstraintType.EQUAL,\n" +
                "                                                                                                                     DomainClassesMetadataD72B4FA1839F9A294537411DCC4F3646.org_drools_modelcompiler_DataType_Metadata_INSTANCE.getPropertyIndex(\"field2\"),\n" +
                "                                                                                                                     new defaultpkg.LambdaExtractor3C616E80B72DC643C4A5B5C0CF12EBAC(),\n" +
                "                                                                                                                     \"BBB\"),\n" +
                "                                                                                                    D.reactOn(\"field2\")),\n" +
                "                                                           D.on(var_result).execute((org.drools.modelcompiler.domain.Result result) -> {\n" +
                "                                                               result.setValue(0);\n" +
                "                                                           }));";

        Statement expression = StaticJavaParser.parseStatement(dslInput);

        PostProcessedExecModel postProcessedExecModel = new ExecModelLambdaPostProcessor().convertLambdas("mypackage", expression);

        String expectedResult = "org.drools.model.Rule rule = D.rule(\"rule2\").build(D.pattern(var_$pattern_DataType$2$).expr(\"77E38C041F31083DCD4B15C970628D1F\",\n" +
                "                                                                                                    new defaultpkg.LambdaPredicateAB4D983A6C15ED8C0C1BA898F5CDB6DD(),\n" +
                "                                                                                                    D.alphaIndexedBy(java.lang.String.class,\n" +
                "                                                                                                                     org.drools.model.Index.ConstraintType.EQUAL,\n" +
                "                                                                                                                     DomainClassesMetadataD72B4FA1839F9A294537411DCC4F3646.org_drools_modelcompiler_DataType_Metadata_INSTANCE.getPropertyIndex(\"field2\"),\n" +
                "                                                                                                                     new defaultpkg.LambdaExtractor3C616E80B72DC643C4A5B5C0CF12EBAC(),\n" +
                "                                                                                                                     \"BBB\"),\n" +
                "                                                                                                    D.reactOn(\"field2\")),\n" +
                "                                                           D.on(var_result).execute(mypackage.LambdaConsequenceA275DE7DF16C84609DF94D2C495A1094.INSTANCE));";

        assertEquals(StaticJavaParser.parseStatement(expectedResult), StaticJavaParser.parseStatement(postProcessedExecModel.getConvertedBlockAsString()));

    }



}