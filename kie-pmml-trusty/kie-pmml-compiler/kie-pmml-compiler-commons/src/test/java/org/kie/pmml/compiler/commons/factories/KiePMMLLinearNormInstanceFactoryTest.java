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
package org.kie.pmml.compiler.commons.factories;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.LinearNorm;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLLinearNorm;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomLinearNorm;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLLinearNorm;

public class KiePMMLLinearNormInstanceFactoryTest {

    @Test
    void getKiePMMLLinearNorms() {
        List<LinearNorm> toConvert =
                IntStream.range(0, 3).mapToObj(i -> getRandomLinearNorm()).collect(Collectors.toList());
        List<KiePMMLLinearNorm> retrieved = KiePMMLLinearNormInstanceFactory.getKiePMMLLinearNorms(toConvert);
        IntStream.range(0, 3).forEach(i -> commonVerifyKiePMMLLinearNorm(retrieved.get(i), toConvert.get(i)));
    }

    @Test
    void getKiePMMLLinearNorm() {
        final LinearNorm toConvert = getRandomLinearNorm();
        final KiePMMLLinearNorm retrieved = KiePMMLLinearNormInstanceFactory.getKiePMMLLinearNorm(toConvert);
        commonVerifyKiePMMLLinearNorm(retrieved, toConvert);
    }
}