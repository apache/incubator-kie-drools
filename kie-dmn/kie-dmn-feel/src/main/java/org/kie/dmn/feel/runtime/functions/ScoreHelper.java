/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.runtime.functions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to evaluate <code>Method</code> score based on the given inputs.
 * It compares the original input with the one "adapted" to match a given <code>Method</code>.
 * For each condition, a value is provided. The top score is obtained when all the conditions are met.
 * <p>
 * Conditions considered (from most  to less relevant):
 * Condition                                   Score
 * 1. number of parameters                  ->     16
 * 2. last parameter not array              ->      8
 * 3. type identity of all parameters ->    ->      4
 * 4. null  counts                          ->      2
 * 5. type being Object                     ->      1
 */
public class ScoreHelper {

    private final static Logger logger = LoggerFactory.getLogger(ScoreHelper.class);

    private static final Map<Predicate<Compares>, Integer> SCORER_MAP;

    private static final Predicate<Compares> numberOfParameters =
            compares -> compares.adaptedInput.length == compares.parameterTypes.length;
    private static final Predicate<Compares> lastParameterNotArray =
            compares -> compares.adaptedInput.length > 0 &&
                    compares.adaptedInput[compares.adaptedInput.length -1] != null &&
                    !compares.adaptedInput[compares.adaptedInput.length -1].getClass().isArray();

    private static final Predicate<Compares> typeIdentityOfParameters = compares -> {
        int index = Math.min(compares.originalInput.length, compares.adaptedInput.length);
        for (int i = 0; i < index; i++) {
            if (!compares.adaptedInput[i].equals(compares.adaptedInput[i].getClass())) {
                return false;
            }
        }
        return true;
    };
    private static final Predicate<Compares> nullCount =
            compares -> nullCount(compares.originalInput) < nullCount(compares.adaptedInput);
    private static final Predicate<Compares> objectType = compares -> {
        if (compares.parameterTypes.length == 1) {
            // If there is one parameter, the != Object has bigger score, being more narrowed
            return !compares.parameterTypes[0].equals(Object.class);
        } else {
            return true;
        }
    };

    static {
        SCORER_MAP = new HashMap<>();
        SCORER_MAP.put(numberOfParameters, 16);
        SCORER_MAP.put(lastParameterNotArray, 8);
        SCORER_MAP.put(typeIdentityOfParameters, 4);
        SCORER_MAP.put(nullCount, 2);
        SCORER_MAP.put(objectType, 1);
    }

    static long nullCount(Object[] params) {
        logger.trace("nullCount {}", params);
        return Stream.of(params).filter(Objects::isNull).count();
    }

    private ScoreHelper() {
    }

    static class Compares {

        private final Object[] originalInput;
        private final Object[] adaptedInput;
        private final Class<?>[] parameterTypes;

        public Compares(Object[] originalInput, Object[] adaptedInput, Class<?>[] parameterTypes) {
            this.originalInput = originalInput;
            this.adaptedInput = adaptedInput;
            this.parameterTypes = parameterTypes;
        }
    }
}