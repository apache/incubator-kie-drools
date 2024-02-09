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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.OUTLIER_TREATMENT_METHOD;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLNormContinuousTest {

    @Test
    void sortLinearNorms() {
        KiePMMLLinearNorm ln0 = new KiePMMLLinearNorm("0", Collections.emptyList(), 34, 45);
        KiePMMLLinearNorm ln1 = new KiePMMLLinearNorm("1", Collections.emptyList(), 32, 5);
        KiePMMLLinearNorm ln2 = new KiePMMLLinearNorm("2", Collections.emptyList(), 33, 34);
        List<KiePMMLLinearNorm> linearNorms = Arrays.asList(ln0, ln1, ln2);
        assertThat(linearNorms.get(0)).isEqualTo(ln0);
        assertThat(linearNorms.get(1)).isEqualTo(ln1);
        assertThat(linearNorms.get(2)).isEqualTo(ln2);
        KiePMMLNormContinuous.sortLinearNorms(linearNorms);
        assertThat(linearNorms.get(0)).isEqualTo(ln1);
        assertThat(linearNorms.get(1)).isEqualTo(ln2);
        assertThat(linearNorms.get(2)).isEqualTo(ln0);
    }

    @Test
    void evaluate() {
        String fieldName = "fieldName";
        Number input = 24;
        KiePMMLNormContinuous kiePMMLNormContinuous = getKiePMMLNormContinuous(fieldName, null, null);
        ProcessingDTO processingDTO = new ProcessingDTO(Collections.emptyList(),
                                                        Collections.emptyList(),
                                                        Collections.emptyList(),
                                                        Collections.emptyList(),
                                                        Collections.singletonList(new KiePMMLNameValue(fieldName,
                                                                                                       input)),
                                                        Collections.emptyList(),
                                                        Collections.emptyList());
        Number retrieved = (Number) kiePMMLNormContinuous.evaluate(processingDTO);
        Number expected =
                kiePMMLNormContinuous.linearNorms.get(0).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(0).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(1).getOrig() - kiePMMLNormContinuous.linearNorms.get(0).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(1).getNorm() - kiePMMLNormContinuous.linearNorms.get(0).getNorm());
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void evaluateWithExpectedValue() {
        KiePMMLNormContinuous kiePMMLNormContinuous = getKiePMMLNormContinuous(null, null, null);
        Number input = 24;
        Number retrieved = kiePMMLNormContinuous.evaluate(input);
        Number expected =
                kiePMMLNormContinuous.linearNorms.get(0).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(0).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(1).getOrig() - kiePMMLNormContinuous.linearNorms.get(0).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(1).getNorm() - kiePMMLNormContinuous.linearNorms.get(0).getNorm());
        assertThat(retrieved).isEqualTo(expected);
        input = 28;
        expected =
                kiePMMLNormContinuous.linearNorms.get(0).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(0).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(1).getOrig() - kiePMMLNormContinuous.linearNorms.get(0).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(1).getNorm() - kiePMMLNormContinuous.linearNorms.get(0).getNorm());
        retrieved = kiePMMLNormContinuous.evaluate(input);
        assertThat(retrieved).isEqualTo(expected);
        input = 30;
        retrieved = kiePMMLNormContinuous.evaluate(input);
        expected =
                kiePMMLNormContinuous.linearNorms.get(1).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(1).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(2).getOrig() - kiePMMLNormContinuous.linearNorms.get(1).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(2).getNorm() - kiePMMLNormContinuous.linearNorms.get(1).getNorm());
        assertThat(retrieved).isEqualTo(expected);
        input = 31;
        retrieved = kiePMMLNormContinuous.evaluate(input);
        expected =
                kiePMMLNormContinuous.linearNorms.get(1).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(1).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(2).getOrig() - kiePMMLNormContinuous.linearNorms.get(1).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(2).getNorm() - kiePMMLNormContinuous.linearNorms.get(1).getNorm());
        assertThat(retrieved).isEqualTo(expected);
        input = 36;
        retrieved = kiePMMLNormContinuous.evaluate(input);
        expected =
                kiePMMLNormContinuous.linearNorms.get(2).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(3).getOrig() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(3).getNorm() - kiePMMLNormContinuous.linearNorms.get(2).getNorm());
        assertThat(retrieved).isEqualTo(expected);
        input = 37;
        retrieved = kiePMMLNormContinuous.evaluate(input);
        expected =
                kiePMMLNormContinuous.linearNorms.get(2).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(3).getOrig() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(3).getNorm() - kiePMMLNormContinuous.linearNorms.get(2).getNorm());
        assertThat(retrieved).isEqualTo(expected);
        input = 40;
        retrieved = kiePMMLNormContinuous.evaluate(input);
        expected =
                kiePMMLNormContinuous.linearNorms.get(2).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(3).getOrig() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(3).getNorm() - kiePMMLNormContinuous.linearNorms.get(2).getNorm());
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void evaluateWithOutlierValueAsIs() {
        KiePMMLNormContinuous kiePMMLNormContinuous = getKiePMMLNormContinuous(null, OUTLIER_TREATMENT_METHOD.AS_IS,
                                                                               null);
        Number input = 23;
        Number retrieved = kiePMMLNormContinuous.evaluate(input);
        Number expected =
                kiePMMLNormContinuous.linearNorms.get(0).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(0).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(1).getOrig() - kiePMMLNormContinuous.linearNorms.get(0).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(1).getNorm() - kiePMMLNormContinuous.linearNorms.get(0).getNorm());

        assertThat(retrieved).isEqualTo(expected);
        input = 41;
        retrieved = kiePMMLNormContinuous.evaluate(input);
        expected =
                kiePMMLNormContinuous.linearNorms.get(2).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(3).getOrig() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(3).getNorm() - kiePMMLNormContinuous.linearNorms.get(2).getNorm());
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void evaluateWithOutlierValueAsMissingValues() {
        Number missingValue = 45;
        KiePMMLNormContinuous kiePMMLNormContinuous = getKiePMMLNormContinuous(null,
                                                                               OUTLIER_TREATMENT_METHOD.AS_MISSING_VALUES, missingValue);
        Number input = 23;
        Number retrieved = kiePMMLNormContinuous.evaluate(input);
        assertThat(retrieved).isEqualTo(missingValue);
        input = 41;
        retrieved = kiePMMLNormContinuous.evaluate(input);
        assertThat(retrieved).isEqualTo(missingValue);
    }

    @Test
    void evaluateWithOutlierValueAsExtremeValues() {
        KiePMMLNormContinuous kiePMMLNormContinuous = getKiePMMLNormContinuous(null,
                                                                               OUTLIER_TREATMENT_METHOD.AS_EXTREME_VALUES, null);
        Number input = 23;
        Number retrieved = kiePMMLNormContinuous.evaluate(input);
        assertThat(retrieved).isEqualTo(kiePMMLNormContinuous.linearNorms.get(0).getNorm());
        input = 41;
        retrieved = kiePMMLNormContinuous.evaluate(input);
        assertThat(retrieved).isEqualTo(kiePMMLNormContinuous.linearNorms.get(3).getNorm());
    }

    @Test
    void evaluateExpectedValue() {
        KiePMMLNormContinuous kiePMMLNormContinuous = getKiePMMLNormContinuous(null, null, null);
        Number input = 24;
        Number retrieved = kiePMMLNormContinuous.evaluateExpectedValue(input);
        Number expected =
                kiePMMLNormContinuous.linearNorms.get(0).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(0).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(1).getOrig() - kiePMMLNormContinuous.linearNorms.get(0).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(1).getNorm() - kiePMMLNormContinuous.linearNorms.get(0).getNorm());
        assertThat(retrieved).isEqualTo(expected);
        input = 28;
        expected =
                kiePMMLNormContinuous.linearNorms.get(0).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(0).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(1).getOrig() - kiePMMLNormContinuous.linearNorms.get(0).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(1).getNorm() - kiePMMLNormContinuous.linearNorms.get(0).getNorm());
        retrieved = kiePMMLNormContinuous.evaluateExpectedValue(input);
        assertThat(retrieved).isEqualTo(expected);
        input = 30;
        retrieved = kiePMMLNormContinuous.evaluateExpectedValue(input);
        expected =
                kiePMMLNormContinuous.linearNorms.get(1).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(1).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(2).getOrig() - kiePMMLNormContinuous.linearNorms.get(1).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(2).getNorm() - kiePMMLNormContinuous.linearNorms.get(1).getNorm());
        assertThat(retrieved).isEqualTo(expected);
        input = 31;
        retrieved = kiePMMLNormContinuous.evaluateExpectedValue(input);
        expected =
                kiePMMLNormContinuous.linearNorms.get(1).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(1).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(2).getOrig() - kiePMMLNormContinuous.linearNorms.get(1).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(2).getNorm() - kiePMMLNormContinuous.linearNorms.get(1).getNorm());
        assertThat(retrieved).isEqualTo(expected);
        input = 36;
        retrieved = kiePMMLNormContinuous.evaluateExpectedValue(input);
        expected =
                kiePMMLNormContinuous.linearNorms.get(2).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(3).getOrig() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(3).getNorm() - kiePMMLNormContinuous.linearNorms.get(2).getNorm());
        assertThat(retrieved).isEqualTo(expected);
        input = 37;
        retrieved = kiePMMLNormContinuous.evaluateExpectedValue(input);
        expected =
                kiePMMLNormContinuous.linearNorms.get(2).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(3).getOrig() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(3).getNorm() - kiePMMLNormContinuous.linearNorms.get(2).getNorm());
        assertThat(retrieved).isEqualTo(expected);
        input = 40;
        retrieved = kiePMMLNormContinuous.evaluateExpectedValue(input);
        expected =
                kiePMMLNormContinuous.linearNorms.get(2).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(3).getOrig() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(3).getNorm() - kiePMMLNormContinuous.linearNorms.get(2).getNorm());
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void evaluateOutlierValueAsIs() {
        KiePMMLNormContinuous kiePMMLNormContinuous = getKiePMMLNormContinuous(null, OUTLIER_TREATMENT_METHOD.AS_IS,
                                                                               null);
        Number input = 23;
        Number retrieved = kiePMMLNormContinuous.evaluateOutlierValue(input);
        Number expected =
                kiePMMLNormContinuous.linearNorms.get(0).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(0).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(1).getOrig() - kiePMMLNormContinuous.linearNorms.get(0).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(1).getNorm() - kiePMMLNormContinuous.linearNorms.get(0).getNorm());

        assertThat(retrieved).isEqualTo(expected);
        input = 41;
        retrieved = kiePMMLNormContinuous.evaluateOutlierValue(input);
        expected =
                kiePMMLNormContinuous.linearNorms.get(2).getNorm() +
                        ((input.doubleValue() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()) / (kiePMMLNormContinuous.linearNorms.get(3).getOrig() - kiePMMLNormContinuous.linearNorms.get(2).getOrig()))
                                * (kiePMMLNormContinuous.linearNorms.get(3).getNorm() - kiePMMLNormContinuous.linearNorms.get(2).getNorm());
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void evaluateOutlierValueAsMissingValues() {
        Number missingValue = 45;
        KiePMMLNormContinuous kiePMMLNormContinuous = getKiePMMLNormContinuous(null,
                                                                               OUTLIER_TREATMENT_METHOD.AS_MISSING_VALUES, missingValue);
        Number input = 23;
        Number retrieved = kiePMMLNormContinuous.evaluateOutlierValue(input);
        assertThat(retrieved).isEqualTo(missingValue);
        input = 41;
        retrieved = kiePMMLNormContinuous.evaluateOutlierValue(input);
        assertThat(retrieved).isEqualTo(missingValue);
    }

    @Test
    void evaluateOutlierValueAsExtremeValues() {
        KiePMMLNormContinuous kiePMMLNormContinuous = getKiePMMLNormContinuous(null,
                                                                               OUTLIER_TREATMENT_METHOD.AS_EXTREME_VALUES, null);
        Number input = 23;
        Number retrieved = kiePMMLNormContinuous.evaluateOutlierValue(input);
        assertThat(retrieved).isEqualTo(kiePMMLNormContinuous.linearNorms.get(0).getNorm());
        input = 41;
        retrieved = kiePMMLNormContinuous.evaluateOutlierValue(input);
        assertThat(retrieved).isEqualTo(kiePMMLNormContinuous.linearNorms.get(3).getNorm());
    }

    @Test
    void getLimitExpectedValue() {
        KiePMMLNormContinuous kiePMMLNormContinuous = getKiePMMLNormContinuous(null, null, null);
        Number input = 24;
        KiePMMLLinearNorm[] retrieved = kiePMMLNormContinuous.getLimitExpectedValue(input);
        assertThat(retrieved[0]).isEqualTo(kiePMMLNormContinuous.linearNorms.get(0));
        assertThat(retrieved[1]).isEqualTo(kiePMMLNormContinuous.linearNorms.get(1));
        input = 28;
        retrieved = kiePMMLNormContinuous.getLimitExpectedValue(input);
        assertThat(retrieved[0]).isEqualTo(kiePMMLNormContinuous.linearNorms.get(0));
        assertThat(retrieved[1]).isEqualTo(kiePMMLNormContinuous.linearNorms.get(1));
        input = 30;
        retrieved = kiePMMLNormContinuous.getLimitExpectedValue(input);
        assertThat(retrieved[0]).isEqualTo(kiePMMLNormContinuous.linearNorms.get(1));
        assertThat(retrieved[1]).isEqualTo(kiePMMLNormContinuous.linearNorms.get(2));
        input = 31;
        retrieved = kiePMMLNormContinuous.getLimitExpectedValue(input);
        assertThat(retrieved[0]).isEqualTo(kiePMMLNormContinuous.linearNorms.get(1));
        assertThat(retrieved[1]).isEqualTo(kiePMMLNormContinuous.linearNorms.get(2));
        input = 36;
        retrieved = kiePMMLNormContinuous.getLimitExpectedValue(input);
        assertThat(retrieved[0]).isEqualTo(kiePMMLNormContinuous.linearNorms.get(2));
        assertThat(retrieved[1]).isEqualTo(kiePMMLNormContinuous.linearNorms.get(3));
        input = 37;
        retrieved = kiePMMLNormContinuous.getLimitExpectedValue(input);
        assertThat(retrieved[0]).isEqualTo(kiePMMLNormContinuous.linearNorms.get(2));
        assertThat(retrieved[1]).isEqualTo(kiePMMLNormContinuous.linearNorms.get(3));
        input = 40;
        retrieved = kiePMMLNormContinuous.getLimitExpectedValue(input);
        assertThat(retrieved[0]).isEqualTo(kiePMMLNormContinuous.linearNorms.get(2));
        assertThat(retrieved[1]).isEqualTo(kiePMMLNormContinuous.linearNorms.get(3));
    }

    @Test
    void evaluateInputAndLimitLinearNorms() {
        double startOrig = 2.1;
        double startNorm = 2.6;
        double endOrig = 7.4;
        double endNorm = 6.9;
        KiePMMLLinearNorm startLinearNorm = new KiePMMLLinearNorm("start",
                                                                  Collections.emptyList(),
                                                                  startOrig,
                                                                  startNorm);
        KiePMMLLinearNorm endLinearNorm = new KiePMMLLinearNorm("end",
                                                                Collections.emptyList(),
                                                                endOrig,
                                                                endNorm);
        KiePMMLLinearNorm[] limitLinearNorms = {startLinearNorm, endLinearNorm};
        Number input = 3.5;
        Number retrieved = KiePMMLNormContinuous.evaluate(input, limitLinearNorms);
        assertThat(retrieved).isNotNull();
        Number expected =
                startNorm + ((input.doubleValue() - startOrig) / (endOrig - startOrig)) * (endNorm - startNorm);
        assertThat(retrieved).isEqualTo(expected);
    }

    private KiePMMLNormContinuous getKiePMMLNormContinuous(String name,
                                                           OUTLIER_TREATMENT_METHOD outlierTreatmentMethod,
                                                           Number mapMissingTo) {
        KiePMMLLinearNorm ln0 = new KiePMMLLinearNorm("0", Collections.emptyList(), 24, 26);
        KiePMMLLinearNorm ln1 = new KiePMMLLinearNorm("1", Collections.emptyList(), 30, 32);
        KiePMMLLinearNorm ln2 = new KiePMMLLinearNorm("2", Collections.emptyList(), 36, 34);
        KiePMMLLinearNorm ln3 = new KiePMMLLinearNorm("3", Collections.emptyList(), 40, 39);
        List<KiePMMLLinearNorm> linearNorms = Arrays.asList(ln0, ln1, ln2, ln3);
        return new KiePMMLNormContinuous(name, Collections.emptyList(), linearNorms, outlierTreatmentMethod, mapMissingTo);
    }

}