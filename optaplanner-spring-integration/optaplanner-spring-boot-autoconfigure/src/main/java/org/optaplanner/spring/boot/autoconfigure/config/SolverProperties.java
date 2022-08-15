package org.optaplanner.spring.boot.autoconfigure.config;

import org.optaplanner.core.api.domain.common.DomainAccessType;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class SolverProperties {

    /**
     * Enable runtime assertions to detect common bugs in your implementation during development.
     * Defaults to "REPRODUCIBLE".
     */
    private EnvironmentMode environmentMode;

    /**
     * Enable daemon mode. In daemon mode, non-early termination pauses the solver instead of stopping it,
     * until the next problem fact change arrives. This is often useful for real-time planning.
     * Defaults to "false".
     */
    private Boolean daemon;
    /**
     * Enable multithreaded solving for a single problem, which increases CPU consumption.
     * Defaults to "NONE".
     * Other options include "AUTO", a number or formula based on the available processor count.
     */
    private String moveThreadCount;

    /**
     * Determines how to access the fields and methods of domain classes.
     * Defaults to {@link DomainAccessType#REFLECTION}.
     * <p>
     * To use {@link DomainAccessType#GIZMO}, {@code io.quarkus.gizmo:gizmo} must be in your classpath,
     * and all planning annotations must be on public members.
     */
    private DomainAccessType domainAccessType;

    /**
     * What constraint stream implementation to use. Defaults to {@link ConstraintStreamImplType#DROOLS}.
     */
    private ConstraintStreamImplType constraintStreamImplType;

    @NestedConfigurationProperty
    private TerminationProperties termination;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public EnvironmentMode getEnvironmentMode() {
        return environmentMode;
    }

    public void setEnvironmentMode(EnvironmentMode environmentMode) {
        this.environmentMode = environmentMode;
    }

    public Boolean getDaemon() {
        return daemon;
    }

    public void setDaemon(Boolean daemon) {
        this.daemon = daemon;
    }

    public String getMoveThreadCount() {
        return moveThreadCount;
    }

    public void setMoveThreadCount(String moveThreadCount) {
        this.moveThreadCount = moveThreadCount;
    }

    public DomainAccessType getDomainAccessType() {
        return domainAccessType;
    }

    public void setDomainAccessType(DomainAccessType domainAccessType) {
        this.domainAccessType = domainAccessType;
    }

    public ConstraintStreamImplType getConstraintStreamImplType() {
        return constraintStreamImplType;
    }

    public void setConstraintStreamImplType(ConstraintStreamImplType constraintStreamImplType) {
        this.constraintStreamImplType = constraintStreamImplType;
    }

    public TerminationProperties getTermination() {
        return termination;
    }

    public void setTermination(TerminationProperties termination) {
        this.termination = termination;
    }

}
