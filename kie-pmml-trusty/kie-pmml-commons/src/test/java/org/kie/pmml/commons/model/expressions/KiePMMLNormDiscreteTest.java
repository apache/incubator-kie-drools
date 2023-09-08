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

import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.CommonTestingUtility.getProcessingDTO;

public class KiePMMLNormDiscreteTest {

    @Test
    void evaluateMissingValue() {
        String fieldName = "fieldName";
        String fieldValue = "fieldValue";
        Number mapMissingTo = 1.0;
        KiePMMLNormDiscrete kiePMMLNormContinuous = getKiePMMLNormDiscrete(fieldName, fieldValue, mapMissingTo);
        ProcessingDTO processingDTO = getProcessingDTO(Collections.emptyList());
        Object retrieved = kiePMMLNormContinuous.evaluate(processingDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(mapMissingTo);
    }

    @Test
    void evaluateSameValue() {
        String fieldName = "fieldName";
        String fieldValue = "fieldValue";
        Number mapMissingTo = null;
        KiePMMLNormDiscrete kiePMMLNormContinuous = getKiePMMLNormDiscrete(fieldName, fieldValue, mapMissingTo);
        ProcessingDTO processingDTO = getProcessingDTO(Collections.singletonList(new KiePMMLNameValue(fieldName,
                                                                                                      fieldValue)));
        Object retrieved = kiePMMLNormContinuous.evaluate(processingDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(1.0);
    }

    @Test
    void evaluateDifferentValue() {
        String fieldName = "fieldName";
        String fieldValue = "fieldValue";
        Number mapMissingTo = null;
        KiePMMLNormDiscrete kiePMMLNormContinuous = getKiePMMLNormDiscrete(fieldName, fieldValue, mapMissingTo);
        ProcessingDTO processingDTO = getProcessingDTO(Collections.singletonList(new KiePMMLNameValue(fieldName,
                                                                                                      "anotherValue")));
        Object retrieved = kiePMMLNormContinuous.evaluate(processingDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(0.0);
    }

    private KiePMMLNormDiscrete getKiePMMLNormDiscrete(String name,
                                                       String value,
                                                       Number mapMissingTo) {
        return new KiePMMLNormDiscrete(name, Collections.emptyList(), value, mapMissingTo);
    }

}