/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.dmg.pmml.DerivedField;
import org.dmg.pmml.LocalTransformations;
import org.junit.Test;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLLocalTransformations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomLocalTransformations;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLDerivedField;

public class KiePMMLLocalTransformationsInstanceFactoryTest {

    @Test
    public void getKiePMMLLocalTransformations() {
        final LocalTransformations toConvert = getRandomLocalTransformations();
        KiePMMLLocalTransformations retrieved =
                KiePMMLLocalTransformationsInstanceFactory.getKiePMMLLocalTransformations(toConvert,
                                                                                                                          Collections.emptyList());
        assertThat(retrieved).isNotNull();

        List<DerivedField> derivedFields = toConvert.getDerivedFields();
        List<KiePMMLDerivedField> derivedFieldsToVerify = retrieved.getDerivedFields();
        assertThat(derivedFieldsToVerify).hasSameSizeAs(derivedFields);
        derivedFields.forEach(derivedFieldSource -> {
            Optional<KiePMMLDerivedField> derivedFieldToVerify =
                    derivedFieldsToVerify.stream().filter(param -> param.getName().equals(derivedFieldSource.getName().getValue()))
                            .findFirst();
            assertThat(derivedFieldToVerify).isPresent();
            commonVerifyKiePMMLDerivedField(derivedFieldToVerify.get(), derivedFieldSource);
        });
    }
}