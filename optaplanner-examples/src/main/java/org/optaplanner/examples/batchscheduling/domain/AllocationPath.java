package org.optaplanner.examples.batchscheduling.domain;

import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PlanningEntity
@XStreamAlias("PipeAllocationPath")
public class AllocationPath extends AbstractPersistable {

    private Batch batch;
    private List<RoutePath> routePathList;

    // Planning variable, changes during planning, between score calculation
    private RoutePath routePath;

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public List<RoutePath> getRoutePathList() {
        return routePathList;
    }

    public void setRoutePathList(List<RoutePath> routePathList) {
        this.routePathList = routePathList;
    }

    @PlanningVariable(nullable = false, valueRangeProviderRefs = { "routePathList" })
    public RoutePath getRoutePath() {
        return routePath;
    }

    public void setRoutePath(RoutePath routePath) {
        this.routePath = routePath;
    }

    public String getLabel() {
        return "Label:: " + batch.getName();
    }

    // ************************************************************************
    // Ranges
    // ************************************************************************
    @ValueRangeProvider(id = "routePathList")
    public List<RoutePath> getRoutePathListArray() {
        return routePathList;
    }

}
