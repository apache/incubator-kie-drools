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
