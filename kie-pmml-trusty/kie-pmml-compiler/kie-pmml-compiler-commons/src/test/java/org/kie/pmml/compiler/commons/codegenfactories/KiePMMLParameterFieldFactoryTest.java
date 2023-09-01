package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.OpType;
import org.dmg.pmml.ParameterField;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.transformations.KiePMMLParameterField;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getDATA_TYPEString;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getOP_TYPEString;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLParameterFieldFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLParameterFieldFactoryTest_01.txt";

    @Test
    void getParameterFieldVariableDeclaration() throws IOException {
        String variableName = "variableName";
        ParameterField parameterField = new ParameterField(FieldName.create(variableName));
        parameterField.setDataType(DataType.DOUBLE);
        parameterField.setOpType(OpType.CONTINUOUS);
        parameterField.setDisplayName("displayName");
        String dataType = getDATA_TYPEString(parameterField.getDataType());
        String opType = getOP_TYPEString(parameterField.getOpType());

        BlockStmt retrieved = KiePMMLParameterFieldFactory.getParameterFieldVariableDeclaration(variableName,
                                                                                                parameterField);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName,
                                                                      dataType,
                                                                      opType,
                                                                      parameterField.getDisplayName()));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLParameterField.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}