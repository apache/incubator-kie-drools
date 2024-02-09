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
package org.kie.pmml.models.scorecard.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.model.predicates.KiePMMLFalsePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLTruePredicate;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLAttributeTest {

    private static final String ATTRIBUTE = "ATTRIBUTE";
    private static final String CUSTOM_FIELD = "CUSTOM_FIELD";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;
    private static final Double value2 = 5.0;


    @Test
    void evaluateNotMatchingAttribute() {
        KiePMMLAttribute attribute = KiePMMLAttribute.builder(ATTRIBUTE, Collections.emptyList(), KiePMMLFalsePredicate.builder(Collections.emptyList()).build())
                .withPartialScore(value1)
                .build();
        assertThat(attribute.evaluate(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyMap())).isNull();
    }

    @Test
    void evaluateMatchingAttributePartialScore() {
        KiePMMLAttribute attribute = KiePMMLAttribute.builder(ATTRIBUTE, Collections.emptyList(), KiePMMLTruePredicate.builder(Collections.emptyList()).build())
                .withPartialScore(value1)
                .build();
        assertThat(attribute.evaluate(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyMap())).isEqualTo(value1);
    }

    @Test
    void evaluateMatchingAttributeComplexPartialScore() {
        KiePMMLAttribute attribute = KiePMMLAttribute.builder(ATTRIBUTE, Collections.emptyList(), KiePMMLTruePredicate.builder(Collections.emptyList()).build())
                .withComplexPartialScore(getKiePMMLComplexPartialScore())
                .build();
        Object expected = value1 / value2;
        assertThat(attribute.evaluate(Collections.emptyList(), getDerivedFields(), Collections.emptyList(),
                Collections.emptyMap())).isEqualTo(expected);
    }

    @Test
    void evaluateMatchingAttributePartialScoreComplexPartialScore() {
        KiePMMLAttribute attribute = KiePMMLAttribute.builder(ATTRIBUTE, Collections.emptyList(), KiePMMLTruePredicate.builder(Collections.emptyList()).build())
                .withPartialScore(value1)
                .withComplexPartialScore(getKiePMMLComplexPartialScore())
                .build();
        Object expected = value1 / value2;
        assertThat(attribute.evaluate(Collections.emptyList(), getDerivedFields(), Collections.emptyList(),
                Collections.emptyMap())).isEqualTo(expected);
    }

    private KiePMMLComplexPartialScore getKiePMMLComplexPartialScore() {
        // <ComplexPartialScore>
        //     <Apply function="/">
        //        <FieldRef>PARAM_1</FieldRef>
        //        <FieldRef>PARAM_2</FieldRef>
        //      </Apply>
        // </ComplexPartialScore>
        final KiePMMLFieldRef kiePMMLFieldRef1 = new KiePMMLFieldRef(PARAM_1, Collections.emptyList(), null);
        final KiePMMLFieldRef kiePMMLFieldRef2 = new KiePMMLFieldRef(PARAM_2, Collections.emptyList(), null);
        final KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), "/")
                .withKiePMMLExpressions(Arrays.asList(kiePMMLFieldRef1, kiePMMLFieldRef2))
                .build();
        return new KiePMMLComplexPartialScore(CUSTOM_FIELD, Collections.emptyList(), kiePMMLApply);
    }

    private List<KiePMMLDerivedField> getDerivedFields() {
        // <DerivedField name="PARAM_1" optype="continuous" dataType="double">
        //     <Constant>100.0</Constant>
        // </DerivedField>
        final KiePMMLConstant kiePMMLConstant1 = new KiePMMLConstant(PARAM_1, Collections.emptyList(), value1, null);
        final KiePMMLDerivedField derivedField1 = KiePMMLDerivedField.builder(PARAM_1, Collections.emptyList(),
                                                                              DATA_TYPE.DOUBLE,
                                                                              OP_TYPE.CONTINUOUS,
                                                                              kiePMMLConstant1)
                .build();
        // <DerivedField name="PARAM_1" optype="continuous" dataType="double">
        //     <Constant>5.0</Constant>
        // </DerivedField>
        final KiePMMLConstant kiePMMLConstant2 = new KiePMMLConstant(PARAM_2, Collections.emptyList(), value2, null);
        final KiePMMLDerivedField derivedField2 = KiePMMLDerivedField.builder(PARAM_2, Collections.emptyList(),
                                                                              DATA_TYPE.DOUBLE,
                                                                              OP_TYPE.CONTINUOUS,
                                                                              kiePMMLConstant2)
                .build();
        return Arrays.asList(derivedField1, derivedField2);
    }
}