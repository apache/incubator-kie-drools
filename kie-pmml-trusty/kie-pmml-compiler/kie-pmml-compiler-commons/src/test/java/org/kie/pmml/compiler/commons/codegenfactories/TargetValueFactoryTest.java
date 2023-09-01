package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.models.TargetValue;
import org.kie.pmml.commons.model.KiePMMLTargetValue;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomTargetValue;
import static org.kie.pmml.compiler.api.utils.ModelUtils.convertToKieTargetValue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class TargetValueFactoryTest {

    private static final String TEST_01_SOURCE = "TargetValueFactoryTest_01.txt";

    @Test
    void getTargetValueVariableInitializer() throws IOException {
        TargetValue targetValue = convertToKieTargetValue(getRandomTargetValue());
        ObjectCreationExpr retrieved = TargetValueFactory.getTargetValueVariableInitializer(targetValue);
        String text = getFileContent(TEST_01_SOURCE);
        Expression expected = JavaParserUtils.parseExpression(String.format(text,
                                                                            targetValue.getValue(),
                                                                            targetValue.getDisplayValue(),
                                                                            targetValue.getPriorProbability(),
                                                                            targetValue.getDefaultValue()));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(Arrays.class, Collections.class, KiePMMLTargetValue.class,
                                               TargetValue.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}