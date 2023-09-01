package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Interval;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.CLOSURE;
import org.kie.pmml.commons.model.expressions.KiePMMLInterval;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLIntervalFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLIntervalFactoryTest_01.txt";

    @Test
    void getIntervalVariableDeclaration() throws IOException {
        String variableName = "variableName";
        double leftMargin = 45.32;

        Interval interval = new Interval();
        interval.setLeftMargin(leftMargin);
        interval.setRightMargin(null);
        interval.setClosure(Interval.Closure.CLOSED_OPEN);

        BlockStmt retrieved = KiePMMLIntervalFactory.getIntervalVariableDeclaration(variableName,
                                                                                    interval);
        String closureString =
                CLOSURE.class.getName() + "." + CLOSURE.byName(interval.getClosure().value()).name();
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName, leftMargin, closureString));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(Collections.class, KiePMMLInterval.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}