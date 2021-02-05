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

package org.kie.pmml.compiler.commons.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.OpType;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.ParameterField;
import org.junit.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.enums.RESULT_FEATURE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getDataField;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getDataTypes;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getMiningField;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getParameterFields;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomOutputField;

public class ModelUtilsTest {

    private static Map<String, String> expectedBoxedClassName;

    static {
        expectedBoxedClassName = new HashMap<>();
        expectedBoxedClassName.put("string", String.class.getName());
        expectedBoxedClassName.put("integer", Integer.class.getName());
        expectedBoxedClassName.put("float", Float.class.getName());
        expectedBoxedClassName.put("double", Double.class.getName());
        expectedBoxedClassName.put("boolean", Boolean.class.getName());
        expectedBoxedClassName.put("date", Date.class.getName());
        expectedBoxedClassName.put("time", Date.class.getName());
        expectedBoxedClassName.put("dateTime", Date.class.getName());
        expectedBoxedClassName.put("dateDaysSince[0]", Long.class.getName());
        expectedBoxedClassName.put("dateDaysSince[1960]", Long.class.getName());
        expectedBoxedClassName.put("dateDaysSince[1970]", Long.class.getName());
        expectedBoxedClassName.put("dateDaysSince[1980]", Long.class.getName());
        expectedBoxedClassName.put("timeSeconds", Long.class.getName());
        expectedBoxedClassName.put("dateTimeSecondsSince[0]", Long.class.getName());
        expectedBoxedClassName.put("dateTimeSecondsSince[1960]", Long.class.getName());
        expectedBoxedClassName.put("dateTimeSecondsSince[1970]", Long.class.getName());
        expectedBoxedClassName.put("dateTimeSecondsSince[1980]", Long.class.getName());
    }

    @Test
    public void convertToKieMiningField() {
        final String fieldName =  "fieldName";
        final MiningField.UsageType usageType = MiningField.UsageType.ACTIVE;
        final MiningField toConvert = getMiningField(fieldName, usageType);
        final DataField dataField = getDataField(fieldName, OpType.CATEGORICAL, DataType.STRING);
        org.kie.pmml.api.models.MiningField retrieved = ModelUtils.convertToKieMiningField(toConvert, dataField);
        assertNotNull(retrieved);
        assertEquals(fieldName, retrieved.getName());
        assertEquals(FIELD_USAGE_TYPE.ACTIVE, retrieved.getUsageType());
        assertEquals(DATA_TYPE.STRING, retrieved.getDataType());
        assertNull(retrieved.getOpType());
        toConvert.setOpType(OpType.CATEGORICAL);
        retrieved = ModelUtils.convertToKieMiningField(toConvert, dataField);
        assertEquals(OP_TYPE.CATEGORICAL, retrieved.getOpType());
    }

    @Test
    public void convertToKieOutputField() {
        final OutputField toConvert = getRandomOutputField();
        org.kie.pmml.api.models.OutputField retrieved = ModelUtils.convertToKieOutputField(toConvert, null);
        assertNotNull(retrieved);
        assertEquals(toConvert.getName().getValue() , retrieved.getName());
        OP_TYPE expectedOpType = OP_TYPE.byName(toConvert.getOpType().value());
        assertEquals(expectedOpType, retrieved.getOpType());
        DATA_TYPE expectedDataType = DATA_TYPE.byName(toConvert.getDataType().value());
        assertEquals(expectedDataType, retrieved.getDataType());
        assertEquals(toConvert.getTargetField().getValue(), retrieved.getTargetField());
        RESULT_FEATURE expectedResultFeature = RESULT_FEATURE.byName(toConvert.getResultFeature().value());
        assertEquals(expectedResultFeature, retrieved.getResultFeature());
        toConvert.setOpType(null);
        toConvert.setTargetField(null);
        retrieved = ModelUtils.convertToKieOutputField(toConvert, null);
        assertNull(retrieved.getOpType());
        assertNull(retrieved.getTargetField());
    }


    @Test
    public void getBoxedClassNameByParameterFields() {
        List<ParameterField> parameterFields = getParameterFields();
        parameterFields.forEach(parameterField -> {
            String retrieved = ModelUtils.getBoxedClassName(parameterField);
            commonVerifyEventuallyBoxedClassName(retrieved, parameterField.getDataType());
        });
    }

    @Test
    public void getBoxedClassNameByDataTypes() {
        List<DataType> dataTypes = getDataTypes();
        dataTypes.forEach(dataType -> {
            String retrieved = ModelUtils.getBoxedClassName(dataType);
            commonVerifyEventuallyBoxedClassName(retrieved, dataType);
        });
    }


    private void commonVerifyEventuallyBoxedClassName(String toVerify, DataType dataType) {
        assertEquals(expectedBoxedClassName.get(dataType.value()), toVerify);
    }
}