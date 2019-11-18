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
                        "                        on(oldestV).execute(p -> result.setValue( \"Oldest person is \" + p.getName()))\n" +
                        "                );";

        Statement expression = StaticJavaParser.parseStatement(dslInput);

        PostProcessedExecModel postProcessedExecModel = new ExecModelLambdaPostProcessor().convertLambdas("mypackage", expression);

        String expectedResult =
                "        Rule rule = rule(\"not\")\n" +
                        "                .build(\n" +
                        "                        pattern( oldestV ),\n" +
                        "                        not( pattern( otherV ).expr( \"exprA\", oldestV, new mypackage.LambdaE1D438AAC3AEAAFEE61CB8AFB5512703())),\n" +
                        "                        on(oldestV).execute(p -> result.setValue( \"Oldest person is \" + p.getName()))\n" +
                        "                );";

        assertEquals(StaticJavaParser.parseStatement(expectedResult), StaticJavaParser.parseStatement(postProcessedExecModel.getConvertedBlockAsString()));

    }

    @Test
    public void convertPatternLambdaExprLambdaWithIndexing() {
        String dslInput = "   org.drools.model.Rule rule = D.rule(\"R\").build(D.pattern(var_$pattern_Person$2$).expr(\"593440B7603CA900F1A34F18497AB0EA\",\n" +
                "                                                                                              (Person _this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getName(),\n" +
                "                                                                                                                                                                        \"Mario\"),\n" +
                "                                                                                              D.alphaIndexedBy(java.lang.String.class,\n" +
                "                                                                                                               org.drools.model.Index.ConstraintType.EQUAL,\n" +
                "                                                                                                               DomainClassesMetadataED199E24E1068066E9407E5F7B67AB13.org_drools_modelcompiler_domain_Person_Metadata_INSTANCE.getPropertyIndex(\"name\"),\n" +
                "                                                                                                               _this -> _this.getName(),\n" +
                "                                                                                                               \"Mario\"),\n" +
                "                                                                                              D.reactOn(\"name\")).bind(var_$name,\n" +
                "                                                                                                                      (_this) -> _this.getName(),\n" +
                "                                                                                                                      D.reactOn(\"name\")),\n" +
                "                                                       D.on(var_$name).execute((drools, $name) -> {\n" +
                "                                                           drools.insert(new Result($name));\n" +
                "                                                       }));";

        Statement expression = StaticJavaParser.parseStatement(dslInput);

        PostProcessedExecModel postProcessedExecModel = new ExecModelLambdaPostProcessor().convertLambdas("mypackage", expression);

        String expectedResult =
                "   org.drools.model.Rule rule = D.rule(\"R\").build(D.pattern(var_$pattern_Person$2$).expr(\"593440B7603CA900F1A34F18497AB0EA\",\n" +
                        "                                                                                              new mypackage.LambdaBEBA11F0E0DF8347E05E477F0678A25E(),\n" +
                        "                                                                                              D.alphaIndexedBy(java.lang.String.class,\n" +
                        "                                                                                                               org.drools.model.Index.ConstraintType.EQUAL,\n" +
                        "                                                                                                               DomainClassesMetadataED199E24E1068066E9407E5F7B67AB13.org_drools_modelcompiler_domain_Person_Metadata_INSTANCE.getPropertyIndex(\"name\"),\n" +
                        "                                                                                                               _this -> _this.getName(),\n" +
                        "                                                                                                               \"Mario\"),\n" +
                        "                                                                                              D.reactOn(\"name\")).bind(var_$name,\n" +
                        "                                                                                                                      (_this) -> _this.getName(),\n" +
                        "                                                                                                                      D.reactOn(\"name\")),\n" +
                        "                                                       D.on(var_$name).execute((drools, $name) -> {\n" +
                        "                                                           drools.insert(new Result($name));\n" +
                        "                                                       }));";

        assertEquals(StaticJavaParser.parseStatement(expectedResult), StaticJavaParser.parseStatement(postProcessedExecModel.getConvertedBlockAsString()));

    }



}