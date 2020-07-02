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

import org.dmg.pmml.DataType;
import org.dmg.pmml.ParameterField;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getDataTypes;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getParameterFields;

public class ModelUtilsTest {

    private static Map<String, String> expectedEventuallyBoxedClassName;

    static {
        expectedEventuallyBoxedClassName = new HashMap<>();
        expectedEventuallyBoxedClassName.put("string", String.class.getName());
        expectedEventuallyBoxedClassName.put("integer", Integer.class.getName());
        expectedEventuallyBoxedClassName.put("float", Float.class.getName());
        expectedEventuallyBoxedClassName.put("double", Double.class.getName());
        expectedEventuallyBoxedClassName.put("boolean", Boolean.class.getName());
        expectedEventuallyBoxedClassName.put("date", Date.class.getName());
        expectedEventuallyBoxedClassName.put("time", Date.class.getName());
        expectedEventuallyBoxedClassName.put("dateTime", Date.class.getName());
        expectedEventuallyBoxedClassName.put("dateDaysSince[0]", Long.class.getName());
        expectedEventuallyBoxedClassName.put("dateDaysSince[1960]", Long.class.getName());
        expectedEventuallyBoxedClassName.put("dateDaysSince[1970]", Long.class.getName());
        expectedEventuallyBoxedClassName.put("dateDaysSince[1980]", Long.class.getName());
        expectedEventuallyBoxedClassName.put("timeSeconds", Long.class.getName());
        expectedEventuallyBoxedClassName.put("dateTimeSecondsSince[0]", Long.class.getName());
        expectedEventuallyBoxedClassName.put("dateTimeSecondsSince[1960]", Long.class.getName());
        expectedEventuallyBoxedClassName.put("dateTimeSecondsSince[1970]", Long.class.getName());
        expectedEventuallyBoxedClassName.put("dateTimeSecondsSince[1980]", Long.class.getName());
    }

    @Test
    public void getEventuallyBoxedClassNameByParameterFields() {
        List<ParameterField> parameterFields = getParameterFields();
        parameterFields.forEach(parameterField -> {
            String retrieved = ModelUtils.getEventuallyBoxedClassName(parameterField);
            commonVerifyEventuallyBoxedClassName(retrieved, parameterField.getDataType());
        });
    }

    @Test
    public void getEventuallyBoxedClassNameByDataTypes() {
        List<DataType> dataTypes = getDataTypes();
        dataTypes.forEach(dataType -> {
            String retrieved = ModelUtils.getEventuallyBoxedClassName(dataType);
            commonVerifyEventuallyBoxedClassName(retrieved, dataType);
        });
    }


    private void commonVerifyEventuallyBoxedClassName(String toVerify, DataType dataType) {
        assertEquals(expectedEventuallyBoxedClassName.get(dataType.value()), toVerify);
    }
}