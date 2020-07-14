package org.acme.facilitylocation.bootstrap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.acme.facilitylocation.domain.FacilityLocationProblem;
import org.acme.facilitylocation.domain.Location;
import org.acme.facilitylocation.persistence.FacilityLocationProblemRepository;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class DemoDataGenerator {

    private final FacilityLocationProblemRepository repository;

    public DemoDataGenerator(FacilityLocationProblemRepository repository) {
        this.repository = repository;
    }

    public void generateDemoData(@Observes StartupEvent startupEvent) {
        FacilityLocationProblem problem = DemoDataBuilder.builder()
                .setCapacity(4500)
                .setDemand(900)
                .setFacilityCount(30)
                .setConsumerCount(60)
                .setSouthWestCorner(new Location(51.44, -0.16))
                .setNorthEastCorner(new Location(51.56, -0.01))
                .setAverageSetupCost(50_000)
                .setSetupCostStandardDeviation(10_000)
                .build();
        repository.update(problem);
    }
}
