package org.optaplanner.core.config.solver.random;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Defines the pseudo random number generator.
 * See the <a href="http://commons.apache.org/proper/commons-math/userguide/random.html#a2.7_PRNG_Pluggability">PRNG</a>
 * documentation in commons-math.
 */
@XmlEnum
public enum RandomType {
    /**
     * This is the default.
     */
    JDK,
    MERSENNE_TWISTER,
    WELL512A,
    WELL1024A,
    WELL19937A,
    WELL19937C,
    WELL44497A,
    WELL44497B;
}
