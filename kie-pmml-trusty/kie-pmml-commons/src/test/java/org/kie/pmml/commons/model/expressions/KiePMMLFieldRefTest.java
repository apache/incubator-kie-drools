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
package org.kie.pmml.commons.model.expressions;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.CommonTestingUtility.getProcessingDTO;

public class KiePMMLFieldRefTest {

    private static final String FIELD_NAME = "FIELD_NAME";

    @Test
    void evaluateFromKiePMMLNameValues() {
        final Object value = 234.45;
        final List<KiePMMLNameValue> kiePMMLNameValues = Collections.singletonList(new KiePMMLNameValue(FIELD_NAME,
                                                                                                        value));
        final KiePMMLFieldRef kiePMMLFieldRef = new KiePMMLFieldRef(FIELD_NAME, Collections.emptyList(), null);
        ProcessingDTO processingDTO = getProcessingDTO(Collections.emptyList(), kiePMMLNameValues);
        final Object retrieved = kiePMMLFieldRef.evaluate(processingDTO);
        assertThat(retrieved).isEqualTo(value);
    }

    @Test
    void evaluateFromDerivedFields() {
        final Object value = 234.45;
        final KiePMMLConstant kiePMMLConstant = new KiePMMLConstant("NAME", Collections.emptyList(), value, null);
        final KiePMMLDerivedField kiePMMLDerivedField = KiePMMLDerivedField.builder(FIELD_NAME,
                                                                                    Collections.emptyList(),
                                                                                    DATA_TYPE.DOUBLE,
                                                                                    OP_TYPE.CONTINUOUS,
                                                                                    kiePMMLConstant)
                .build();
        final List<KiePMMLDerivedField> derivedFields = Collections.singletonList(kiePMMLDerivedField);
        final List<KiePMMLNameValue> kiePMMLNameValues = Collections.singletonList(new KiePMMLNameValue("UNKNOWN",
                                                                                                        "WRONG"));
        final KiePMMLFieldRef kiePMMLFieldRef = new KiePMMLFieldRef(FIELD_NAME, Collections.emptyList(), null);
        ProcessingDTO processingDTO = getProcessingDTO(derivedFields, kiePMMLNameValues);
        final Object retrieved = kiePMMLFieldRef.evaluate(processingDTO);
        assertThat(retrieved).isEqualTo(value);
    }

    @Test
    void evaluateFromMapMissingTo() {
        final String value = "234.45";
        final KiePMMLConstant kiePMMLConstant = new KiePMMLConstant("NAME", Collections.emptyList(), "WRONG-CONSTANT"
                , null);
        final KiePMMLDerivedField kiePMMLDerivedField = KiePMMLDerivedField.builder("ANOTHER_FIELD",
                                                                                    Collections.emptyList(),
                                                                                    DATA_TYPE.DOUBLE,
                                                                                    OP_TYPE.CONTINUOUS,
                                                                                    kiePMMLConstant)
                .build();
        final List<KiePMMLDerivedField> derivedFields = Collections.singletonList(kiePMMLDerivedField);
        final List<KiePMMLNameValue> kiePMMLNameValues = Collections.singletonList(new KiePMMLNameValue("UNKNOWN",
                                                                                                        "WRONG"));
        final KiePMMLFieldRef kiePMMLFieldRef = new KiePMMLFieldRef(FIELD_NAME, Collections.emptyList(), value);
        ProcessingDTO processingDTO = getProcessingDTO(derivedFields, kiePMMLNameValues);
        final Object retrieved = kiePMMLFieldRef.evaluate(processingDTO);
        assertThat(retrieved).isEqualTo(value);
    }

    @Test
    void evaluateNull() {
        final KiePMMLConstant kiePMMLConstant = new KiePMMLConstant("NAME", Collections.emptyList(), "WRONG-CONSTANT", null);
        final KiePMMLDerivedField kiePMMLDerivedField = KiePMMLDerivedField.builder("ANOTHER_FIELD",
                                                                                    Collections.emptyList(),
                                                                                    DATA_TYPE.DOUBLE,
                                                                                    OP_TYPE.CONTINUOUS,
                                                                                    kiePMMLConstant)
                .build();
        final List<KiePMMLDerivedField> derivedFields = Collections.singletonList(kiePMMLDerivedField);
        final List<KiePMMLNameValue> kiePMMLNameValues = Collections.singletonList(new KiePMMLNameValue("UNKNOWN",
                                                                                                        "WRONG"));
        final KiePMMLFieldRef kiePMMLFieldRef = new KiePMMLFieldRef(FIELD_NAME, Collections.emptyList(), null);
        ProcessingDTO processingDTO = getProcessingDTO(derivedFields, kiePMMLNameValues);
        final Object retrieved = kiePMMLFieldRef.evaluate(processingDTO);
        assertThat(retrieved).isNull();
    }

}