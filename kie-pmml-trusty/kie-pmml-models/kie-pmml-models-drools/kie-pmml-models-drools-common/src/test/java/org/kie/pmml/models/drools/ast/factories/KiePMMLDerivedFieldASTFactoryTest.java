/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.models.drools.ast.factories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.FieldName;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

public class KiePMMLDerivedFieldASTFactoryTest {

    private Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;
    private KiePMMLDerivedFieldASTFactory fieldASTFactory;

    @Before
    public void setup() {
        fieldTypeMap = new HashMap<>();
        fieldASTFactory = KiePMMLDerivedFieldASTFactory.factory(fieldTypeMap);
        assertNotNull(fieldASTFactory);
    }

    @Test
    public void declareTypes() {
        List<DerivedField> derivedFields = IntStream.range(0, 5)
                .mapToObj(value -> getDerivedField("FieldName-" + value))
                .collect(Collectors.toList());
        List<KiePMMLDroolsType> retrieved = fieldASTFactory.declareTypes(derivedFields);
        assertEquals(derivedFields.size(), retrieved.size());
        for (int i = 0; i < derivedFields.size(); i++)  {
            commonValidateKiePMMLDroolsType(retrieved.get(i), derivedFields.get(i));
        }
    }

    @Test
    public void declareType() {
        DerivedField derivedField = getDerivedField("FieldName");
        KiePMMLDroolsType retrieved = fieldASTFactory.declareType(derivedField);
        commonValidateKiePMMLDroolsType(retrieved, derivedField);
    }

    private void commonValidateKiePMMLDroolsType(KiePMMLDroolsType toValidate, DerivedField derivedField) {
        String derivedFieldName = derivedField.getName().getValue();
        String expectedName = getSanitizedClassName(derivedFieldName.toUpperCase());
        assertEquals(expectedName, toValidate.getName());
        String expectedType = DATA_TYPE.byName(derivedField.getDataType().value()).getMappedClass().getSimpleName();
        assertEquals(expectedType, toValidate.getType());
        assertTrue(fieldTypeMap.containsKey(derivedFieldName));
        KiePMMLOriginalTypeGeneratedType retrieved = fieldTypeMap.get(derivedFieldName);
        assertEquals(derivedField.getDataType().value(), retrieved.getOriginalType());
        assertEquals(expectedName, retrieved.getGeneratedType());
    }

    private DerivedField getDerivedField(String fieldName) {
        DerivedField toReturn = new DerivedField();
        toReturn.setName(FieldName.create(fieldName));
        final DATA_TYPE[] values = DATA_TYPE.values();
        int rndInt = new Random().nextInt(values.length - 1);
        DATA_TYPE dataType = values[rndInt];
        toReturn.setDataType(DataType.fromValue(dataType.getName()));
        return toReturn;
    }
}