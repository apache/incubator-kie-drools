package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DataField;
import org.dmg.pmml.MiningField;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.commons.model.KiePMMLMiningField;
import org.kie.pmml.commons.model.expressions.KiePMMLInterval;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomDataField;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLMiningFieldFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLMiningFieldFactoryTest_01.txt";
    private static final String TEST_02_SOURCE = "KiePMMLMiningFieldFactoryTest_02.txt";
    private static final String TEST_03_SOURCE = "KiePMMLMiningFieldFactoryTest_03.txt";
    private static final String VARIABLE_NAME = "variableName";

    @Test
    void getMiningFieldVariableDeclarationNoAllowedValuesNoIntervals() throws IOException {
        DataField dataField = getRandomDataField();
        dataField.getValues().clear();
        dataField.getIntervals().clear();
        MiningField miningField = new MiningField();
        miningField.setName(dataField.getName());
        miningField.setUsageType(MiningField.UsageType.TARGET);
        BlockStmt retrieved =
                org.kie.pmml.compiler.commons.codegenfactories.KiePMMLMiningFieldFactory.getMiningFieldVariableDeclaration(VARIABLE_NAME, miningField,
                                                                                                                                         Collections.singletonList(dataField));
        String dataTypeString =
                DATA_TYPE.class.getName() + "." + DATA_TYPE.byName(dataField.getDataType().value()).name();
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, VARIABLE_NAME,
                                                                      miningField.getName().getValue(),
                                                                      dataTypeString));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(Arrays.class, Collections.class, KiePMMLInterval.class,
                                               KiePMMLMiningField.class, DATA_TYPE.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    void getMiningFieldVariableDeclarationWithAllowedValuesNoIntervals() throws IOException {
        DataField dataField = getRandomDataField();
        dataField.getIntervals().clear();
        MiningField miningField = new MiningField();
        miningField.setName(dataField.getName());
        miningField.setUsageType(MiningField.UsageType.TARGET);
        BlockStmt retrieved =
                org.kie.pmml.compiler.commons.codegenfactories.KiePMMLMiningFieldFactory.getMiningFieldVariableDeclaration(VARIABLE_NAME, miningField,
                                                                                                                                         Collections.singletonList(dataField));
        String dataTypeString =
                DATA_TYPE.class.getName() + "." + DATA_TYPE.byName(dataField.getDataType().value()).name();
        String text = getFileContent(TEST_02_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, VARIABLE_NAME,
                                                                      miningField.getName().getValue(),
                                                                      dataTypeString,
                                                                      dataField.getValues().get(0).getValue(),
                                                                      dataField.getValues().get(1).getValue(),
                                                                      dataField.getValues().get(2).getValue()));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(Arrays.class, Collections.class, KiePMMLInterval.class,
                                               KiePMMLMiningField.class, DATA_TYPE.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    void getMiningFieldVariableDeclarationWithAllowedValuesAndIntervals() throws IOException {
        DataField dataField = getRandomDataField();
        MiningField miningField = new MiningField();
        miningField.setName(dataField.getName());
        miningField.setUsageType(MiningField.UsageType.TARGET);
        BlockStmt retrieved = KiePMMLMiningFieldFactory.getMiningFieldVariableDeclaration(VARIABLE_NAME, miningField,
                                                                                          Collections.singletonList(dataField));
        String dataTypeString =
                DATA_TYPE.class.getName() + "." + DATA_TYPE.byName(dataField.getDataType().value()).name();
        String text = getFileContent(TEST_03_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, VARIABLE_NAME,
                                                                      miningField.getName().getValue(),
                                                                      dataTypeString,
                                                                      dataField.getValues().get(0).getValue(),
                                                                      dataField.getValues().get(1).getValue(),
                                                                      dataField.getValues().get(2).getValue(),

                                                                      dataField.getIntervals().get(0).getLeftMargin(),
                                                                      dataField.getIntervals().get(0).getRightMargin(),
                                                                      dataField.getIntervals().get(0).getClosure().name(),

                                                                      dataField.getIntervals().get(1).getLeftMargin(),
                                                                      dataField.getIntervals().get(1).getRightMargin(),
                                                                      dataField.getIntervals().get(1).getClosure().name(),

                                                                      dataField.getIntervals().get(2).getLeftMargin(),
                                                                      dataField.getIntervals().get(2).getRightMargin(),
                                                                      dataField.getIntervals().get(2).getClosure().name()));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(Arrays.class, Collections.class, KiePMMLInterval.class,
                                               KiePMMLMiningField.class, DATA_TYPE.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}