/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.common.persistence.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ProbabilisticDataGenerator {

    public static <E> E extractRandomElement(Random random, List<E> list) {
        return list.get(random.nextInt(list.size()));
    }

    public static <E> List<E> extractRandomSubList(Random random, List<E> list, double... thresholds) {
        int size = generateRandomIntFromThresholds(random, thresholds);
        if (size > list.size()) {
            size = list.size();
        }
        return extractRandomSubListOfSize(random, list, size);
    }

    public static <E> List<E> extractRandomSubListOfSize(Random random, List<E> list, int size) {
        List<E> subList = new ArrayList<>(list);
        Collections.shuffle(subList, random);
        // Remove elements not in the sublist (so it can be garbage collected)
        subList.subList(size, subList.size()).clear();
        return subList;
    }

    public static int generateRandomIntFromThresholds(Random random, double... thresholds) {
        double randomDouble = random.nextDouble();
        for (int i = 0; i < thresholds.length; i++) {
            if (randomDouble < thresholds[i]) {
                return i;
            }
        }
        return thresholds.length;
    }

    private ProbabilisticDataGenerator() {
    }

}
