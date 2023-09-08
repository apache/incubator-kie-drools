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
package org.kie.pmml.commons.transformations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.kie.pmml.commons.CommonTestingUtility.getProcessingDTO;

public class KiePMMLDefineFunctionTest {

    private static final String CUSTOM_FUNCTION = "CUSTOM_FUNCTION";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;
    private static final Double value2 = 5.0;

    @Test
    void evaluateNoParamValues() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final KiePMMLParameterField parameterField1 =
                    KiePMMLParameterField.builder(PARAM_1, Collections.emptyList()).build();
            final KiePMMLParameterField parameterField2 =
                    KiePMMLParameterField.builder(PARAM_2, Collections.emptyList()).build();
            final KiePMMLDefineFunction defineFunction = new KiePMMLDefineFunction(CUSTOM_FUNCTION,
                                                                                   Collections.emptyList(),
                                                                                   null,
                                                                                   OP_TYPE.CONTINUOUS,
                                                                                   Arrays.asList(parameterField1,
                                                                                                 parameterField2),
                                                                                   null);
            ProcessingDTO processingDTO = getProcessingDTO(Collections.emptyList());
            defineFunction.evaluate(processingDTO, null);
        });
    }

    @Test
    void evaluateEmptyParamValues() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final KiePMMLParameterField parameterField1 = KiePMMLParameterField.builder(PARAM_1, Collections.emptyList
                    ()).build();
            final KiePMMLParameterField parameterField2 = KiePMMLParameterField.builder(PARAM_2, Collections.emptyList
                    ()).build();
            final KiePMMLDefineFunction defineFunction = new KiePMMLDefineFunction(CUSTOM_FUNCTION, Collections
                    .emptyList(),
                                                                                   null,
                                                                                   OP_TYPE.CONTINUOUS,
                                                                                   Arrays.asList(parameterField1,
                                                                                                 parameterField2),
                                                                                   null);
            ProcessingDTO processingDTO = getProcessingDTO(Collections.emptyList());
            defineFunction.evaluate(processingDTO, Collections.emptyList());
        });
    }

    @Test
    void evaluateFromConstant() {
        // <DefineFunction name="CUSTOM_FUNCTION" optype="continuous" dataType="double">
        //     <Constant>100.0</Constant>
        // </DefineFunction>
        final KiePMMLConstant kiePMMLConstant1 = new KiePMMLConstant(PARAM_1, Collections.emptyList(), value1, null);
        final KiePMMLDefineFunction defineFunction = new KiePMMLDefineFunction(CUSTOM_FUNCTION, Collections.emptyList(),
                                                                               null,
                                                                               OP_TYPE.CONTINUOUS,
                                                                               Collections.emptyList(),
                                                                               kiePMMLConstant1);
        ProcessingDTO processingDTO = getProcessingDTO(Collections.emptyList());
        Object retrieved = defineFunction.evaluate(processingDTO, Collections.emptyList());
        assertThat(retrieved).isEqualTo(value1);
    }

    @Test
    void evaluateFromFieldRef() {
        // <DefineFunction name="CUSTOM_FUNCTION" optype="continuous" dataType="double">
        //     <ParameterField name="PARAM_1"/>
        //     <FieldRef field="PARAM_1"/>
        // </DefineFunction>
        final KiePMMLFieldRef kiePMMLFieldRef = new KiePMMLFieldRef(PARAM_1, Collections.emptyList(), null);
        final KiePMMLDefineFunction defineFunction = new KiePMMLDefineFunction(CUSTOM_FUNCTION, Collections.emptyList(),
                                                                               DATA_TYPE.DOUBLE,
                                                                               OP_TYPE.CONTINUOUS,
                                                                               Collections.singletonList(KiePMMLParameterField.builder(PARAM_1, Collections.emptyList()).build()),
                                                                               kiePMMLFieldRef);
        ProcessingDTO processingDTO = getProcessingDTO(new ArrayList<>());
        Object retrieved = defineFunction.evaluate(processingDTO, Collections.singletonList(value1));
        assertThat(retrieved).isEqualTo(value1);
    }

    @Test
    void evaluateFromApply() {
        // <DefineFunction name="CUSTOM_FUNCTION" optype="continuous" dataType="double">
        //     <ParameterField name="PARAM_1"/>
        //     <ParameterField field="PARAM_2"/>
        //     <Apply function="/">
        //        <FieldRef>PARAM_1</FieldRef>
        //        <FieldRef>PARAM_2</FieldRef>
        //      </Apply>
        // </DefineFunction>
        final KiePMMLFieldRef kiePMMLFieldRef1 = new KiePMMLFieldRef(PARAM_1, Collections.emptyList(), null);
        final KiePMMLFieldRef kiePMMLFieldRef2 = new KiePMMLFieldRef(PARAM_2, Collections.emptyList(), null);
        final KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), "/")
                .withKiePMMLExpressions(Arrays.asList(kiePMMLFieldRef1, kiePMMLFieldRef2))
                .build();
        final KiePMMLParameterField parameterField1 =
                KiePMMLParameterField.builder(PARAM_1, Collections.emptyList()).build();
        final KiePMMLParameterField parameterField2 =
                KiePMMLParameterField.builder(PARAM_2, Collections.emptyList()).build();
        final KiePMMLDefineFunction defineFunction = new KiePMMLDefineFunction(CUSTOM_FUNCTION, Collections.emptyList(),
                                                                               DATA_TYPE.DOUBLE,
                                                                               OP_TYPE.CONTINUOUS,
                                                                               Arrays.asList(parameterField1,
                                                                                             parameterField2),
                                                                               kiePMMLApply);
        ProcessingDTO processingDTO = getProcessingDTO(new ArrayList<>());
        Object retrieved = defineFunction.evaluate(processingDTO, Arrays.asList(value1, value2));
        Object expected = value1 / value2;
        assertThat(retrieved).isEqualTo(expected);
    }

}