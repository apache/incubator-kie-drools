package org.optaplanner.core.impl.solver.random;

import java.util.Random;

/**
 * @see DefaultRandomFactory
 */
public interface RandomFactory {

    /**
     * @return never null
     */
    Random createRandom();

}
