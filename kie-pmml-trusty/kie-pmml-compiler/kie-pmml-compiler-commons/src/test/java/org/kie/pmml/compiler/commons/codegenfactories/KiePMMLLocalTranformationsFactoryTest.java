/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.compiler.commons.codegenfactories;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.OpType;
import org.junit.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLLocalTransformations;
import org.kie.pmml.compiler.commons.codegenfactories.KiePMMLLocalTransformationsFactory;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;

public class KiePMMLLocalTranformationsFactoryTest {

    private static final String CUSTOM_FUNCTION = "CUSTOM_FUNCTION";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;

    @Test
    public void getKiePMMLTransformationDictionaryVariableDeclaration() {
        LocalTransformations localTransformations = new LocalTransformations();
        localTransformations.addDerivedFields(getDerivedFields());

        BlockStmt retrieved = KiePMMLLocalTransformationsFactory.getKiePMMLLocalTransformationsVariableDeclaration(localTransformations);
        Statement expected = JavaParserUtils
                .parseBlock("{\n" +
                                    "    KiePMMLConstant localTransformationsDerivedField_0_0 = new KiePMMLConstant" +
                                    "(\"localTransformationsDerivedField_0_0\", Collections.emptyList(), 100.0);\n" +
                                    "    KiePMMLDerivedField localTransformationsDerivedField_0 = KiePMMLDerivedField" +
                                    ".builder(\"PARAM_20\", Collections.emptyList(), \"double\", \"continuous\", " +
                                    "localTransformationsDerivedField_0_0).withDisplayName(null).build();\n" +
                                    "    KiePMMLConstant localTransformationsDerivedField_1_0 = new KiePMMLConstant" +
                                    "(\"localTransformationsDerivedField_1_0\", Collections.emptyList(), 100.0);\n" +
                                    "    KiePMMLDerivedField localTransformationsDerivedField_1 = KiePMMLDerivedField" +
                                    ".builder(\"PARAM_21\", Collections.emptyList(), \"double\", \"continuous\", " +
                                    "localTransformationsDerivedField_1_0).withDisplayName(null).build();\n" +
                                    "    KiePMMLLocalTransformations localTransformations = " +
                                    "KiePMMLLocalTransformations.builder(\"localTransformations\", Collections" +
                                    ".emptyList()).withDerivedFields(Arrays.asList" +
                                    "(localTransformationsDerivedField_0, localTransformationsDerivedField_1)).build" +
                                    "();\n" +
                                    "}");
        List<Class<?>> imports = Arrays.asList(KiePMMLConstant.class,
                                               KiePMMLApply.class,
                                               KiePMMLDerivedField.class,
                                               KiePMMLLocalTransformations.class,
                                               Arrays.class,
                                               Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    private DerivedField[] getDerivedFields() {
        return IntStream.range(0, 2)
                .mapToObj(this::getDerivedField)
                .toArray(DerivedField[]::new);
    }

    private DerivedField getDerivedField(int counter) {
        Constant constant = new Constant();
        constant.setValue(value1);
        DerivedField toReturn = new DerivedField();
        toReturn.setName(FieldName.create(PARAM_2 + counter));
        toReturn.setDataType(DataType.DOUBLE);
        toReturn.setOpType(OpType.CONTINUOUS);
        toReturn.setExpression(constant);
        return toReturn;
    }
}