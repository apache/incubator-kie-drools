package org.optaplanner.core.impl.domain.variable.supply;

/**
 * Provides a {@link Supply} for subsystems that submit a {@link Demand}.
 */
public interface SupplyManager {

    /**
     * Returns the {@link Supply} for a {@link Demand}, preferably an existing one.
     * If the {@link Supply} doesn't exist yet (as part of the domain model or externalized), it creates and attaches it.
     * If two {@link Demand} instances {@link Object#equals(Object) are equal},
     * they will result in the same {@link Supply} instance.
     * Each supply instance keeps a counter of how many times it was requested,
     * which can be decremented by {@link #cancel(Demand)}.
     *
     * @param demand never null
     * @param <Supply_> Subclass of {@link Supply}
     * @return never null
     */
    <Supply_ extends Supply> Supply_ demand(Demand<Supply_> demand);

    /**
     * Cancel an active {@link #demand(Demand)}.
     * Once the number of active demands reaches zero, the {@link Supply} in question is removed.
     * <p>
     * This operation is optional.
     * Supplies with active demands will live for as long as the {@link SupplyManager} lives,
     * and get garbage-collected together with it.
     *
     * @param demand never null
     * @param <Supply_>
     * @return true if the counter was decremented, false if there is no such supply
     */
    <Supply_ extends Supply> boolean cancel(Demand<Supply_> demand);

    /**
     * @param demand
     * @return 0 when there is no active {@link Supply} for the given {@link Demand}, more when there is one.
     * @param <Supply_>
     */
    <Supply_ extends Supply> long getActiveCount(Demand<Supply_> demand);

}
