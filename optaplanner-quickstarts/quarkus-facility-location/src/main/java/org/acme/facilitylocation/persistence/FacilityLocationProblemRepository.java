package org.acme.facilitylocation.persistence;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.acme.facilitylocation.domain.FacilityLocationProblem;

@ApplicationScoped
public class FacilityLocationProblemRepository {

    private FacilityLocationProblem facilityLocationProblem;

    public Optional<FacilityLocationProblem> solution() {
        return Optional.ofNullable(facilityLocationProblem);
    }

    public void update(FacilityLocationProblem facilityLocationProblem) {
        this.facilityLocationProblem = facilityLocationProblem;
    }
}
