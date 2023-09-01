package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLFieldRefFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLFieldRefFactoryTest_01.txt";

    @Test
    void getFieldRefVariableDeclaration() throws IOException {
        String variableName = "variableName";
        String fieldName = "fieldName";
        String mapMissingTo = "mapMissingTo";
        FieldRef fieldRef = new FieldRef();
        fieldRef.setField(FieldName.create(fieldName));
        fieldRef.setMapMissingTo(mapMissingTo);
        BlockStmt retrieved = KiePMMLFieldRefFactory.getFieldRefVariableDeclaration(variableName, fieldRef);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName, fieldName, mapMissingTo));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLFieldRef.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}