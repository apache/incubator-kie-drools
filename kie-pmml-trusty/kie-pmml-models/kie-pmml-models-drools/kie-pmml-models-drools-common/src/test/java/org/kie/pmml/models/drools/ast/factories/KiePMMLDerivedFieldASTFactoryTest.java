/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.drools.ast.factories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

public class KiePMMLDerivedFieldASTFactoryTest {

    private Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;
    private KiePMMLDerivedFieldASTFactory fieldASTFactory;

    @BeforeEach
    public void setup() {
        fieldTypeMap = new HashMap<>();
        fieldASTFactory = KiePMMLDerivedFieldASTFactory.factory(fieldTypeMap);
        assertThat(fieldASTFactory).isNotNull();
    }

    @Test
    void declareTypes() {
        List<DerivedField> derivedFields = IntStream.range(0, 5)
                .mapToObj(value -> getDerivedField("FieldName-" + value))
                .collect(Collectors.toList());
        List<KiePMMLDroolsType> retrieved = fieldASTFactory.declareTypes(derivedFields);
        assertThat(retrieved).hasSameSizeAs(derivedFields);
        for (int i = 0; i < derivedFields.size(); i++)  {
            commonValidateKiePMMLDroolsType(retrieved.get(i), derivedFields.get(i));
        }
    }

    @Test
    void declareType() {
        DerivedField derivedField = getDerivedField("FieldName");
        KiePMMLDroolsType retrieved = fieldASTFactory.declareType(derivedField);
        commonValidateKiePMMLDroolsType(retrieved, derivedField);
    }

    private void commonValidateKiePMMLDroolsType(KiePMMLDroolsType toValidate, DerivedField derivedField) {
        String derivedFieldName =derivedField.getName();
        String expectedName = getSanitizedClassName(derivedFieldName.toUpperCase());
        assertThat(toValidate.getName()).isEqualTo(expectedName);
        String expectedType = DATA_TYPE.byName(derivedField.getDataType().value()).getMappedClass().getSimpleName();
        assertThat(toValidate.getType()).isEqualTo(expectedType);
        assertThat(fieldTypeMap).containsKey(derivedFieldName);
        KiePMMLOriginalTypeGeneratedType retrieved = fieldTypeMap.get(derivedFieldName);
        assertThat(retrieved.getOriginalType()).isEqualTo(derivedField.getDataType().value());
        assertThat(retrieved.getGeneratedType()).isEqualTo(expectedName);
    }

    private DerivedField getDerivedField(String fieldName) {
        DerivedField toReturn = new DerivedField();
        toReturn.setName(fieldName);
        final DATA_TYPE[] values = DATA_TYPE.values();
        int rndInt = new Random().nextInt(values.length - 1);
        DATA_TYPE dataType = values[rndInt];
        toReturn.setDataType(DataType.fromValue(dataType.getName()));
        return toReturn;
    }
}