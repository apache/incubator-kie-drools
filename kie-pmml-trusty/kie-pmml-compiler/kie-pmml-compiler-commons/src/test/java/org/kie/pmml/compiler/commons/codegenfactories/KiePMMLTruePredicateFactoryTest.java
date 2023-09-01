package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.True;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.predicates.KiePMMLTruePredicate;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLTruePredicateFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLTruePredicateFactoryTest_01.txt";

    @Test
    void getTruePredicateVariableDeclaration() throws IOException {
        String variableName = "variableName";
        BlockStmt retrieved = KiePMMLTruePredicateFactory.getTruePredicateVariableDeclaration(variableName, new True());
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLTruePredicate.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}