package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.NormDiscrete;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLNormDiscrete;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLNormDiscreteFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLNormDiscreteFactoryTest_01.txt";

    @Test
    void getNormDiscreteVariableDeclaration() throws IOException {
        String variableName = "variableName";
        String fieldName = "fieldName";
        String fieldValue = "fieldValue";
        double mapMissingTo = 45.32;

        NormDiscrete normDiscrete = new NormDiscrete();
        normDiscrete.setField(FieldName.create(fieldName));
        normDiscrete.setValue(fieldValue);
        normDiscrete.setMapMissingTo(mapMissingTo);

        BlockStmt retrieved = KiePMMLNormDiscreteFactory.getNormDiscreteVariableDeclaration(variableName,
                                                                                            normDiscrete);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName, fieldName, fieldValue, mapMissingTo));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(Collections.class, KiePMMLNormDiscrete.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

}