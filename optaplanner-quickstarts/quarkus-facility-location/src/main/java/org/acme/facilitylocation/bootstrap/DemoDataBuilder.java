package org.acme.facilitylocation.bootstrap;

import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.acme.facilitylocation.domain.Consumer;
import org.acme.facilitylocation.domain.Facility;
import org.acme.facilitylocation.domain.FacilityLocationProblem;
import org.acme.facilitylocation.domain.Location;

public class DemoDataBuilder {

    private static final AtomicLong sequence = new AtomicLong();

    private long capacity;
    private long demand;
    private int facilityCount;
    private int consumerCount;
    private long averageSetupCost;
    private long setupCostStandardDeviation;
    private Location southWestCorner;
    private Location northEastCorner;

    private DemoDataBuilder() {
    }

    public static DemoDataBuilder builder() {
        return new DemoDataBuilder();
    }

    public DemoDataBuilder setCapacity(long capacity) {
        this.capacity = capacity;
        return this;
    }

    public DemoDataBuilder setDemand(long demand) {
        this.demand = demand;
        return this;
    }

    public DemoDataBuilder setFacilityCount(int facilityCount) {
        this.facilityCount = facilityCount;
        return this;
    }

    public DemoDataBuilder setConsumerCount(int consumerCount) {
        this.consumerCount = consumerCount;
        return this;
    }

    public DemoDataBuilder setAverageSetupCost(long averageSetupCost) {
        this.averageSetupCost = averageSetupCost;
        return this;
    }

    public DemoDataBuilder setSetupCostStandardDeviation(long setupCostStandardDeviation) {
        this.setupCostStandardDeviation = setupCostStandardDeviation;
        return this;
    }

    public DemoDataBuilder setSouthWestCorner(Location southWestCorner) {
        this.southWestCorner = southWestCorner;
        return this;
    }

    public DemoDataBuilder setNorthEastCorner(Location northEastCorner) {
        this.northEastCorner = northEastCorner;
        return this;
    }

    public FacilityLocationProblem build() {
        if (demand < 1) {
            throw new IllegalStateException("Demand (" + demand + ") must be greater than zero.");
        }
        if (capacity < 1) {
            throw new IllegalStateException("Capacity (" + capacity + ") must be greater than zero.");
        }
        if (facilityCount < 1) {
            throw new IllegalStateException("Number of facilities (" + facilityCount + ") must be greater than zero.");
        }
        if (consumerCount < 1) {
            throw new IllegalStateException("Number of consumers (" + consumerCount + ") must be greater than zero.");
        }
        if (demand > capacity) {
            throw new IllegalStateException("Overconstrained problem not supported. The total capacity ("
                    + capacity + ") must be greater than or equal to the total demand (" + demand + ").");
        }
        // TODO SW<NE

        Random random = new Random(0);
        PrimitiveIterator.OfDouble latitudes = random.doubles(southWestCorner.latitude, northEastCorner.latitude)
                .iterator();
        PrimitiveIterator.OfDouble longitudes = random.doubles(southWestCorner.longitude, northEastCorner.longitude)
                .iterator();
        Supplier<Location> locationSupplier = () -> new Location(latitudes.nextDouble(), longitudes.nextDouble());
        List<Facility> facilities = Stream.generate(locationSupplier)
                .map(location -> new Facility(
                        sequence.incrementAndGet(),
                        location,
                        averageSetupCost + (long) (setupCostStandardDeviation * random.nextGaussian()),
                        capacity / facilityCount))
                .limit(facilityCount)
                .collect(Collectors.toList());
        List<Consumer> consumers = Stream.generate(locationSupplier)
                .map(location -> new Consumer(
                        sequence.incrementAndGet(),
                        location,
                        demand / consumerCount))
                .limit(consumerCount)
                .collect(Collectors.toList());

        return new FacilityLocationProblem(facilities, consumers, southWestCorner, northEastCorner);
    }
}
