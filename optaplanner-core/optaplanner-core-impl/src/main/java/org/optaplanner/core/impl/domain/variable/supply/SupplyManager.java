package org.optaplanner.core.impl.domain.variable.supply;

/**
 * Provides a {@link Supply} for subsystems that submit a {@link Demand}.
 */
public interface SupplyManager {

    /**
     * Returns the {@link Supply} for a {@link Demand}, preferably an existing one.
     * If the {@link Supply} doesn't exist yet (as part of the domain model or externalized), it creates and attaches it.
     *
     * @param demand never null
     * @param <Supply_> Subclass of {@link Supply}
     * @return never null
     */
    <Supply_ extends Supply> Supply_ demand(Demand<Supply_> demand);

}
