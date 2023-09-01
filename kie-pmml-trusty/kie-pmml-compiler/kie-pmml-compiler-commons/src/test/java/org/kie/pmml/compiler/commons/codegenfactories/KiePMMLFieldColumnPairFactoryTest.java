package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.FieldColumnPair;
import org.dmg.pmml.FieldName;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldColumnPair;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLFieldColumnPairFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLFieldColumnPairFactoryTest_01.txt";

    @Test
    void getRowVariableDeclaration() throws IOException {
        String variableName = "variableName";
        String fieldName = "fieldName";
        String column = "column";
        FieldColumnPair fieldColumnPair = new FieldColumnPair();
        fieldColumnPair.setField(FieldName.create(fieldName));
        fieldColumnPair.setColumn(column);

        BlockStmt retrieved = KiePMMLFieldColumnPairFactory.getFieldColumnPairVariableDeclaration(variableName,
                                                                                                  fieldColumnPair);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName, fieldName, column));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(Collections.class, KiePMMLFieldColumnPair.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}