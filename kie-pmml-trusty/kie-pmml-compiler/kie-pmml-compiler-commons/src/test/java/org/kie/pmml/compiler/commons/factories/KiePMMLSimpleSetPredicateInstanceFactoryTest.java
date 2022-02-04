package org.kie.pmml.compiler.commons.factories;/*
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

import org.dmg.pmml.SimpleSetPredicate;
import org.junit.Test;
import org.kie.pmml.commons.model.predicates.KiePMMLSimpleSetPredicate;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomSimpleSetPredicate;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLSimpleSetPredicate;

public class KiePMMLSimpleSetPredicateInstanceFactoryTest {

    @Test
    public void getKiePMMLSimpleSetPredicate() {
        final SimpleSetPredicate toConvert = getRandomSimpleSetPredicate();
        final KiePMMLSimpleSetPredicate retrieved = KiePMMLSimpleSetPredicateInstanceFactory.getKiePMMLSimpleSetPredicate(toConvert);
        commonVerifyKiePMMLSimpleSetPredicate(retrieved, toConvert);
    }
}