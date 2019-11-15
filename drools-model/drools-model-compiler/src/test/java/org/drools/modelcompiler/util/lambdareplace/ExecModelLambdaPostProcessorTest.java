package org.drools.modelcompiler.util.lambdareplace;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.stmt.Statement;
import org.junit.Test;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.*;

public class ExecModelLambdaPostProcessorTest {

    @Test
    public void convertPatternLambdaExprLambda() {
        String dslInput =
                        "        Rule rule = rule(\"not\")\n" +
                        "                .build(\n" +
                        "                        pattern( oldestV ),\n" +
                        "                        not( pattern( otherV ).expr( \"exprA\", oldestV, (p1, p2) -> p1.getAge() > p2.getAge()) ),\n" +
                        "                        on(oldestV).execute(p -> result.setValue( \"Oldest person is \" + p.getName()))\n" +
                        "                );";

        Statement expression = StaticJavaParser.parseStatement(dslInput);

        PostProcessedExecModel postProcessedExecModel = new ExecModelLambdaPostProcessor().convertLambdas("package", expression);

        String expectedResult =
                "        Rule rule = rule(\"not\")\n" +
                        "                .build(\n" +
                        "                        pattern( oldestV ),\n" +
                        "                        not( pattern( otherV ).expr( \"exprA\", oldestV, Lambda5899FA70FFBD0AB136E1673C97CB1EAB::apply)),\n" +
                        "                        on(oldestV).execute(p -> result.setValue( \"Oldest person is \" + p.getName()))\n" +
                        "                );";

        assertEquals(StaticJavaParser.parseStatement(expectedResult), StaticJavaParser.parseStatement(postProcessedExecModel.getConvertedBlockAsString()));

    }



}