package org.optaplanner.core.config.solver.monitoring;

import java.util.List;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "solverMetricList",
})
public class MonitoringConfig extends AbstractConfig<MonitoringConfig> {
    @XmlElement(name = "metric")
    protected List<SolverMetric> solverMetricList = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************
    public List<SolverMetric> getSolverMetricList() {
        return solverMetricList;
    }

    public void setSolverMetricList(List<SolverMetric> solverMetricList) {
        this.solverMetricList = solverMetricList;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public MonitoringConfig withSolverMetricList(List<SolverMetric> solverMetricList) {
        this.solverMetricList = solverMetricList;
        return this;
    }

    @Override
    public MonitoringConfig inherit(MonitoringConfig inheritedConfig) {
        solverMetricList = ConfigUtils.inheritMergeableListProperty(solverMetricList, inheritedConfig.solverMetricList);
        return this;
    }

    @Override
    public MonitoringConfig copyConfig() {
        return new MonitoringConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        // No referenced classes currently
        // If we add custom metrics here, then this should
        // register the custom metrics
    }
}
