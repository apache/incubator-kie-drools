package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.solver.ClassInstanceCache;

/**
 * Calculating {@link NearbyDistanceMatrix} is very expensive,
 * therefore we want to reuse it as much as possible.
 * <p>
 * In cases where the demand represents the same nearby selector (as defined by
 * {@link AbstractNearbyDistanceMatrixDemand#equals(Object)})
 * the {@link SupplyManager} ensures that the same supply instance is returned
 * with the pre-computed {@link NearbyDistanceMatrix}.
 *
 * @param <Origin_> planning entities
 * @param <Destination_> planning entities XOR planning values
 * @param <ChildSelector_>
 * @param <ReplayingSelector_>
 */
public abstract class AbstractNearbyDistanceMatrixDemand<Origin_, Destination_, ChildSelector_, ReplayingSelector_>
        implements Demand<NearbyDistanceMatrix<Origin_, Destination_>> {

    protected final NearbyDistanceMeter<Origin_, Destination_> meter;
    protected final NearbyRandom random;
    protected final ChildSelector_ childSelector;
    protected final ReplayingSelector_ replayingSelector;

    protected AbstractNearbyDistanceMatrixDemand(NearbyDistanceMeter<Origin_, Destination_> meter, NearbyRandom random,
            ChildSelector_ childSelector, ReplayingSelector_ replayingSelector) {
        this.meter = meter;
        this.random = random;
        this.childSelector = childSelector;
        this.replayingSelector = replayingSelector;
    }

    @Override
    public final NearbyDistanceMatrix<Origin_, Destination_> createExternalizedSupply(SupplyManager supplyManager) {
        return supplyNearbyDistanceMatrix();
    }

    protected abstract NearbyDistanceMatrix<Origin_, Destination_> supplyNearbyDistanceMatrix();

    /**
     * Two instances of this class are considered equal if and only if:
     *
     * <ul>
     * <li>Their meter instances are equal.</li>
     * <li>Their nearby randoms represent the same distribution.</li>
     * <li>Their child selectors are equal.</li>
     * <li>Their replaying origin entity selectors are equal.</li>
     * </ul>
     *
     * Otherwise as defined by {@link Object#equals(Object)}.
     *
     * @see ClassInstanceCache for how we ensure equality for meter instances in particular and selectors in general.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AbstractNearbyDistanceMatrixDemand<?, ?, ?, ?> that = (AbstractNearbyDistanceMatrixDemand<?, ?, ?, ?>) o;
        return Objects.equals(meter, that.meter)
                && Objects.equals(random, that.random)
                && Objects.equals(childSelector, that.childSelector)
                && Objects.equals(replayingSelector, that.replayingSelector);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(meter, random, childSelector, replayingSelector);
    }
}
