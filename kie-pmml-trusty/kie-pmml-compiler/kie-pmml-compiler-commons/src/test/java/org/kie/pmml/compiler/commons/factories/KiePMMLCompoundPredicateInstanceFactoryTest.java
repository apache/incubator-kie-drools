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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.Field;
import org.junit.Test;
import org.kie.pmml.commons.model.predicates.KiePMMLCompoundPredicate;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomCompoundPredicate;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomDataField;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKKiePMMLCompoundPredicate;

public class KiePMMLCompoundPredicateInstanceFactoryTest {

    @Test
    public void getKiePMMLCompoundPredicate() {
        List<Field<?>> fields = IntStream.range(0, 3).mapToObj(i -> getRandomDataField()).collect(Collectors.toList());
        final CompoundPredicate toConvert = getRandomCompoundPredicate(fields);
        final KiePMMLCompoundPredicate retrieved =
                KiePMMLCompoundPredicateInstanceFactory.getKiePMMLCompoundPredicate(toConvert, fields);
        commonVerifyKKiePMMLCompoundPredicate(retrieved, toConvert);
    }
}