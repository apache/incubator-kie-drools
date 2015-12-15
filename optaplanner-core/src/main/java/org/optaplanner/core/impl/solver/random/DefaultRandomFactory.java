/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.solver.random;

import java.util.Random;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomAdaptor;
import org.apache.commons.math3.random.Well1024a;
import org.apache.commons.math3.random.Well19937a;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.random.Well44497a;
import org.apache.commons.math3.random.Well44497b;
import org.apache.commons.math3.random.Well512a;
import org.optaplanner.core.config.solver.random.RandomType;

public class DefaultRandomFactory implements RandomFactory {

    protected final RandomType randomType;
    protected final Long randomSeed;

    /**
     * @param randomType never null
     * @param randomSeed null if no seed
     */
    public DefaultRandomFactory(RandomType randomType, Long randomSeed) {
        this.randomType = randomType;
        this.randomSeed = randomSeed;
    }

    @Override
    public Random createRandom() {
        switch (randomType) {
            case JDK:
                return randomSeed == null ? new Random() : new Random(randomSeed);
            case MERSENNE_TWISTER:
                return new RandomAdaptor(randomSeed == null ? new MersenneTwister() : new MersenneTwister(randomSeed));
            case WELL512A:
                return new RandomAdaptor(randomSeed == null ? new Well512a() : new Well512a(randomSeed));
            case WELL1024A:
                return new RandomAdaptor(randomSeed == null ? new Well1024a() : new Well1024a(randomSeed));
            case WELL19937A:
                return new RandomAdaptor(randomSeed == null ? new Well19937a() : new Well19937a(randomSeed));
            case WELL19937C:
                return new RandomAdaptor(randomSeed == null ? new Well19937c() : new Well19937c(randomSeed));
            case WELL44497A:
                return new RandomAdaptor(randomSeed == null ? new Well44497a() : new Well44497a(randomSeed));
            case WELL44497B:
                return new RandomAdaptor(randomSeed == null ? new Well44497b() : new Well44497b(randomSeed));
            default:
                throw new IllegalStateException("The randomType (" + randomType + ") is not implemented.");
        }
    }

    @Override
    public String toString() {
        return randomType.name() + (randomSeed == null ? "" : " with seed " + randomSeed);
    }

}
