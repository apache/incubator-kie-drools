package org.drools.modelcompiler.util.lambdareplace;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ExecModelLambdaPostProcessorTest {

    @Before
    public void configJP() {
        StaticJavaParser.getConfiguration().setCharacterEncoding(Charset.defaultCharset());
    }

    @Test
    public void convertPatternLambdaExprLambda() throws Exception {

        CompilationUnit inputCU = StaticJavaParser.parseResource("org/drools/modelcompiler/util/lambdareplace/PatternTestHarness.java");

        CompilationUnit clone = inputCU.clone();

        new ExecModelLambdaPostProcessor(new HashMap<>(), "mypackage", "rulename", new ArrayList<>(), new ArrayList<>(), clone).convertLambdas();

        MethodDeclaration expectedResult = inputCU.getClassByName("PatternTestHarness")
                .map(c -> c.getMethodsByName("expectedOutput"))
                .flatMap(methods -> methods.stream().findFirst())
                .map(m -> m.setName("testMethod"))
                .orElseThrow(RuntimeException::new);

        MethodDeclaration actual = clone.getClassByName("PatternTestHarness")
                .map(c -> c.getMethodsByName("inputMethod"))
                .flatMap(methods -> methods.stream().findFirst())
                .map(m -> m.setName("testMethod"))
                .orElseThrow(RuntimeException::new);

        assertThat(actual.toString(), equalToIgnoringWhiteSpace(expectedResult.toString()));

    }

//    @Test
//    public void doNotConvertConsequence() {
//        String dslInput = "        org.drools.model.Rule rule = D.rule(\"R\").build(expr,\n" +
//                "                                                       D.on(var_$p).execute((org.drools.model.Drools drools, Person $p) -> {\n" +
//                "                                                           $p.setAge($p.getAge() + 1);\n" +
//                "                                                           drools.update($p, mask_$p);\n" +
//                "                                                       }));";
//
//        Statement expression = StaticJavaParser.parseStatement(dslInput);
//
//        PostProcessedExecModel postProcessedExecModel = new ExecModelLambdaPostProcessor(new HashMap<>(), "mypackage", "rulename", new ArrayList<>(), new ArrayList(), expression).convertLambdas();
//
//        String expectedResult = "        org.drools.model.Rule rule = D.rule(\"R\").build(expr,\n" +
//                                                                        "D.on(var_$p).execute((org.drools.model.Drools drools, Person $p) -> {" +
//                "                                                           $p.setAge($p.getAge() + 1);\n" +
//                "                                                           drools.update($p, mask_$p);\n" +
//                "                                                       }));";
//
//        assertEquals(StaticJavaParser.parseStatement(expectedResult), StaticJavaParser.parseStatement(postProcessedExecModel.getConvertedBlockAsString()));
//
//    }
}