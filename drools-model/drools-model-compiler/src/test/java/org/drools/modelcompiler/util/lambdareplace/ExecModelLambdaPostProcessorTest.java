package org.drools.modelcompiler.util.lambdareplace;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.Before;
import org.junit.Test;

import static com.github.javaparser.StaticJavaParser.parseResource;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertThat;

public class ExecModelLambdaPostProcessorTest {

    @Before
    public void configJP() {
        StaticJavaParser.getConfiguration().setCharacterEncoding(Charset.defaultCharset());
    }

    @Test
    public void convertPatternLambda() throws Exception {

        CompilationUnit inputCU = parseResource("org/drools/modelcompiler/util/lambdareplace/PatternTestHarness.java");
        CompilationUnit clone = inputCU.clone();

        new ExecModelLambdaPostProcessor(new HashMap<>(), "mypackage", "rulename", new ArrayList<>(), new ArrayList<>(), clone).convertLambdas();

        MethodDeclaration expectedResult = getMethodChangingName(inputCU, "PatternTestHarness", "expectedOutput");
        MethodDeclaration actual = getMethodChangingName(clone, "PatternTestHarness", "inputMethod");

        assertThat(actual.toString(), equalToIgnoringWhiteSpace(expectedResult.toString()));
    }

    @Test
    public void convertFlowLambda() throws Exception {

        CompilationUnit inputCU = parseResource("org/drools/modelcompiler/util/lambdareplace/FlowTestHarness.java");
        CompilationUnit clone = inputCU.clone();

        new ExecModelLambdaPostProcessor(new HashMap<>(), "mypackage", "rulename", new ArrayList<>(), new ArrayList<>(), clone).convertLambdas();

        MethodDeclaration expectedResult = getMethodChangingName(inputCU, "FlowTestHarness", "expectedOutput");
        MethodDeclaration actual = getMethodChangingName(clone, "FlowTestHarness", "inputMethod");

        assertThat(actual.toString(), equalToIgnoringWhiteSpace(expectedResult.toString()));
    }

    @Test
    public void convertFlowLambdaDoNotConvertConsequence() throws Exception {

        CompilationUnit inputCU = parseResource("org/drools/modelcompiler/util/lambdareplace/FlowDoNotConvertConsequenceTestHarness.java");
        CompilationUnit clone = inputCU.clone();

        new ExecModelLambdaPostProcessor(new HashMap<>(), "mypackage", "rulename", new ArrayList<>(), new ArrayList<>(), clone).convertLambdas();

        MethodDeclaration expectedResultNotConverted = getMethodChangingName(inputCU, "FlowDoNotConvertConsequenceTestHarness", "expectedOutputNotConverted");
        MethodDeclaration actualNotConverted = getMethodChangingName(clone, "FlowDoNotConvertConsequenceTestHarness", "inputMethodNotConverted");

        assertThat(actualNotConverted.toString(), equalToIgnoringWhiteSpace(expectedResultNotConverted.toString()));

        MethodDeclaration expectedResultConverted = getMethodChangingName(inputCU, "FlowDoNotConvertConsequenceTestHarness", "expectedOutputConverted");
        MethodDeclaration actualConverted = getMethodChangingName(clone, "FlowDoNotConvertConsequenceTestHarness", "inputMethodConverted");

        assertThat(actualConverted.toString(), equalToIgnoringWhiteSpace(expectedResultConverted.toString()));
    }

    private MethodDeclaration getMethodChangingName(CompilationUnit inputCU, String className, String methodName) {
        return inputCU.getClassByName(className)
                .map(c -> c.getMethodsByName(methodName))
                .flatMap(methods -> methods.stream().findFirst())
                .map(m -> m.setName("testMethod"))
                .orElseThrow(RuntimeException::new);
    }
}