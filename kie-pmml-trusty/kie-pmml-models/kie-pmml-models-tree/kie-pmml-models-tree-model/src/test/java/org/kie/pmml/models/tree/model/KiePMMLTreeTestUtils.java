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
package org.kie.pmml.models.tree.model;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;

public class KiePMMLTreeTestUtils {

    public static List<KiePMMLScoreDistribution> getRandomKiePMMLScoreDistributions(boolean withProbability) {
        List<Double> probabilities = withProbability ? Arrays.asList(0.1, 0.3, 0.6) : Arrays.asList(null, null, null);
        return IntStream.range(0, 3)
                .mapToObj(i -> getRandomKiePMMLScoreDistribution(probabilities.get(i)))
                .collect(Collectors.toList());
    }

    public static KiePMMLScoreDistribution getRandomKiePMMLScoreDistribution(Double probability) {
        Random random = new Random();
        return new KiePMMLScoreDistribution(RandomStringUtils.random(6, true, false),
                                            null,
                                            RandomStringUtils.random(6, true, false),
                                            random.nextInt(100),
                                            (double) random.nextInt(1) / 100,
                                            probability);
    }
}