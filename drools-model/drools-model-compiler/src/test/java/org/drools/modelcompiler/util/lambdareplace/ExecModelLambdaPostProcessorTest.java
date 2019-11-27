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
    public void convertPatternLambda() throws Exception {

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

    @Test
    public void convertFlowLambda() throws Exception {

        CompilationUnit inputCU = StaticJavaParser.parseResource("org/drools/modelcompiler/util/lambdareplace/FlowTestHarness.java");

        CompilationUnit clone = inputCU.clone();

        new ExecModelLambdaPostProcessor(new HashMap<>(), "mypackage", "rulename", new ArrayList<>(), new ArrayList<>(), clone).convertLambdas();

        MethodDeclaration expectedResult = inputCU.getClassByName("FlowTestHarness")
                .map(c -> c.getMethodsByName("expectedOutput"))
                .flatMap(methods -> methods.stream().findFirst())
                .map(m -> m.setName("testMethod"))
                .orElseThrow(RuntimeException::new);

        MethodDeclaration actual = clone.getClassByName("FlowTestHarness")
                .map(c -> c.getMethodsByName("inputMethod"))
                .flatMap(methods -> methods.stream().findFirst())
                .map(m -> m.setName("testMethod"))
                .orElseThrow(RuntimeException::new);

        assertThat(actual.toString(), equalToIgnoringWhiteSpace(expectedResult.toString()));

    }


    @Test
    public void convertFlowLambdaDoNotConvertConsequence() throws Exception {

        CompilationUnit inputCU = StaticJavaParser.parseResource("org/drools/modelcompiler/util/lambdareplace/FlowDoNotConvertConsequenceTestHarness.java");

        CompilationUnit clone = inputCU.clone();

        new ExecModelLambdaPostProcessor(new HashMap<>(), "mypackage", "rulename", new ArrayList<>(), new ArrayList<>(), clone).convertLambdas();

        MethodDeclaration expectedResult = inputCU.getClassByName("FlowDoNotConvertConsequenceTestHarness")
                .map(c -> c.getMethodsByName("expectedOutput"))
                .flatMap(methods -> methods.stream().findFirst())
                .map(m -> m.setName("testMethod"))
                .orElseThrow(RuntimeException::new);

        MethodDeclaration actual = clone.getClassByName("FlowDoNotConvertConsequenceTestHarness")
                .map(c -> c.getMethodsByName("inputMethod"))
                .flatMap(methods -> methods.stream().findFirst())
                .map(m -> m.setName("testMethod"))
                .orElseThrow(RuntimeException::new);

        assertThat(actual.toString(), equalToIgnoringWhiteSpace(expectedResult.toString()));

    }
}