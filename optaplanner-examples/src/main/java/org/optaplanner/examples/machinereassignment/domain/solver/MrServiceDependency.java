package org.optaplanner.examples.machinereassignment.domain.solver;

import org.optaplanner.examples.machinereassignment.domain.MrService;

public class MrServiceDependency {

    private MrService fromService;
    private MrService toService;

    public MrServiceDependency() {
    }

    public MrServiceDependency(MrService fromService, MrService toService) {
        this.fromService = fromService;
        this.toService = toService;
    }

    public MrService getFromService() {
        return fromService;
    }

    public void setFromService(MrService fromService) {
        this.fromService = fromService;
    }

    public MrService getToService() {
        return toService;
    }

    public void setToService(MrService toService) {
        this.toService = toService;
    }

}
