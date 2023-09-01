package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Constant;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLConstantFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLConstantFactoryTest_01.txt";

    @Test
    void getConstantVariableDeclaration() throws IOException {
        String variableName = "variableName";
        Object value = 2342.21;
        Constant constant = new Constant();
        constant.setValue(value);
        BlockStmt retrieved = KiePMMLConstantFactory.getConstantVariableDeclaration(variableName, constant);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName, value));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLConstant.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}