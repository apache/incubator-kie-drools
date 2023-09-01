package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.False;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.predicates.KiePMMLFalsePredicate;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLFalsePredicateFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLFalsePredicateFactoryTest_01.txt";

    @Test
    void getFalsePredicateVariableDeclaration() throws IOException {
        String variableName = "variableName";
        BlockStmt retrieved = KiePMMLFalsePredicateFactory.getFalsePredicateVariableDeclaration(variableName,
                                                                                                new False());
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLFalsePredicate.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}