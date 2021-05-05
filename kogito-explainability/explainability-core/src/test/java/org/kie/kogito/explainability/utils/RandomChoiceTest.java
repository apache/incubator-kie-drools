/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.explainability.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class RandomChoiceTest {
    List<String> obj = List.of("a", "b", "c", "d", "e");

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testOnlyOneWeight(int seed) {
        Random rn = new Random();
        rn.setSeed(seed);
        // if only one weight is nonzero, all samples should be equal to the object corresponding to that weight
        List<String> output = List.of("c", "c", "c");
        List<Double> weights = List.of(0., 0., 1., 0., 0.);
        RandomChoice<String> rc = new RandomChoice<>(obj, weights);
        assertEquals(output, rc.sample(3, rn));
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testTwoWeight(int seed) {
        Random rn = new Random();
        rn.setSeed(seed);
        // if two weights are nonzero, all samples should correspond to one of those two objects
        List<Double> weights = List.of(1., 0., 1., 0., 0.);
        RandomChoice<String> rc = new RandomChoice<>(obj, weights);
        List<String> sample = rc.sample(5, rn);
        for (int i = 0; i < sample.size(); i++) {
            assertTrue((sample.get(i).equals("a") || sample.get(i).equals("c")));
        }
        ;
    }

    @Test
    void weightMismatch() {
        // if the weights don't match, raise error
        List<Double> weights = List.of(1., 1., 0.);
        assertThrows(IllegalArgumentException.class, () -> new RandomChoice<>(obj, weights));
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testUniform(int seed) {
        RandomChoice<String> rc = new RandomChoice<>(obj);
        Random rn = new Random();
        rn.setSeed(seed);

        for (int test = 0; test < 100; test++) {
            List<String> sample = rc.sample(1000, rn);

            //find the total number of samples of each object
            HashMap<String, Integer> results = new HashMap<>();
            for (String ith : sample) {
                results.putIfAbsent(ith, 0);
                results.put(ith, results.get(ith) + 1);
            }

            // assuming the above samples are drawn from a uniform binomial distribution,
            // the counts of each object will fall outside these bounds only once every 1e30 tests.
            // if we ran one test a second, it'd take an octillion years for a single one to fail
            for (String ith : obj) {
                assertTrue(results.get(ith) > 70);
                assertTrue(results.get(ith) < 324);
            }

        }
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testMultiWeight(int seed) {
        List<Double> weights = List.of(5., 4., 3., 2., 1.);
        RandomChoice<String> rc = new RandomChoice<>(obj, weights);
        Random rn = new Random();
        rn.setSeed(seed);

        for (int test = 0; test < 100; test++) {
            List<String> sample = rc.sample(1000, rn);

            //find the total number of samples of each object
            HashMap<String, Integer> results = new HashMap<>();
            for (String ith : sample) {
                results.putIfAbsent(ith, 0);
                results.put(ith, results.get(ith) + 1);
            }

            // assuming the above samples are drawn from a binomial distribution,
            // the counts of each object will fall outside these bounds only once every 1e30 tests.
            // if we ran one test a second, it'd take an octillion years for a single one to fail
            assertTrue(results.get("a") > 171);
            assertTrue(results.get("a") < 475);

            assertTrue(results.get("b") > 118);
            assertTrue(results.get("b") < 401);

            assertTrue(results.get("c") > 70);
            assertTrue(results.get("c") < 324);

            assertTrue(results.get("d") > 28);
            assertTrue(results.get("d") < 242);

            assertTrue(results.get("e") > 28);
            assertTrue(results.get("e") < 151);

        }
    }
}
