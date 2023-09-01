package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.InlineTable;
import org.dmg.pmml.MapValues;
import org.dmg.pmml.PMML;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLInlineTable;
import org.kie.pmml.commons.model.expressions.KiePMMLRow;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;
import static org.drools.util.FileUtils.getFileInputStream;

public class KiePMMLInlineTableFactoryTest {

    private static final String TRANSFORMATIONS_SAMPLE = "TransformationsSample.pmml";
    private static final String MAPVALUED = "mapvalued";
    private static final String TEST_01_SOURCE = "KiePMMLInlineTableFactoryTest_01.txt";
    private static InlineTable INLINETABLE;

    @BeforeAll
    public static void setup() throws Exception {
        PMML pmmlModel = KiePMMLUtil.load(getFileInputStream(TRANSFORMATIONS_SAMPLE), TRANSFORMATIONS_SAMPLE);
        DerivedField mapValued = pmmlModel.getTransformationDictionary()
                .getDerivedFields()
                .stream()
                .filter(derivedField -> MAPVALUED.equals(derivedField.getName().getValue()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Missing derived field " + MAPVALUED));
        INLINETABLE = ((MapValues) mapValued.getExpression()).getInlineTable();
    }

    @Test
    void getInlineTableVariableDeclaration() throws IOException {
        String variableName = "variableName";
        BlockStmt retrieved = KiePMMLInlineTableFactory.getInlineTableVariableDeclaration(variableName,
                                                                                          INLINETABLE);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(Arrays.class, Collections.class, Collectors.class,
                                               KiePMMLInlineTable.class, KiePMMLRow.class, Map.class, Stream.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}