package org.optaplanner.core.config.heuristic.selector.common.nearby;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum NearbySelectionDistributionType {
    /**
     * Only the n nearest are selected, with an equal probability.
     */
    BLOCK_DISTRIBUTION,
    /**
     * Nearest elements are selected with a higher probability. The probability decreases linearly.
     */
    LINEAR_DISTRIBUTION,
    /**
     * Nearest elements are selected with a higher probability. The probability decreases quadratically.
     */
    PARABOLIC_DISTRIBUTION,
    /**
     * Selection according to a beta distribution. Slows down the solver significantly.
     */
    BETA_DISTRIBUTION;

}
