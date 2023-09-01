package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.SimplePredicate;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionary;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLSimplePredicateFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLSimplePredicateFactoryTest_01.txt";

    @Test
    void getSimplePredicateVariableDeclaration() throws IOException {
        String variableName = "variableName";
        final SimplePredicate simplePredicate = new SimplePredicate();
        simplePredicate.setField(FieldName.create("CUSTOM_FIELD"));
        simplePredicate.setValue("235.435");
        simplePredicate.setOperator(SimplePredicate.Operator.EQUAL);
        String operatorString = OPERATOR.class.getName() + "." + OPERATOR.byName(simplePredicate.getOperator().value());
        DataField dataField = new DataField();
        dataField.setName(simplePredicate.getField());
        dataField.setDataType(DataType.DOUBLE);
        DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField);

        BlockStmt retrieved = KiePMMLSimplePredicateFactory.getSimplePredicateVariableDeclaration(variableName, simplePredicate, getFieldsFromDataDictionary(dataDictionary));
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName,
                                                                      simplePredicate.getField().getValue(),
                                                                      operatorString,
                                                                      simplePredicate.getValue()));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLSimplePredicate.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}