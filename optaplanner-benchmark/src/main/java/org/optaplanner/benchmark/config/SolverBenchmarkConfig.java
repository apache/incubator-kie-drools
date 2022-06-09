package org.optaplanner.benchmark.config;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "name",
        "solverConfig",
        "problemBenchmarksConfig",
        "subSingleCount"
})
public class SolverBenchmarkConfig extends AbstractConfig<SolverBenchmarkConfig> {

    private String name = null;

    @XmlElement(name = SolverConfig.XML_ELEMENT_NAME, namespace = SolverConfig.XML_NAMESPACE)
    private SolverConfig solverConfig = null;

    @XmlElement(name = "problemBenchmarks")
    private ProblemBenchmarksConfig problemBenchmarksConfig = null;

    private Integer subSingleCount = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SolverConfig getSolverConfig() {
        return solverConfig;
    }

    public void setSolverConfig(SolverConfig solverConfig) {
        this.solverConfig = solverConfig;
    }

    public ProblemBenchmarksConfig getProblemBenchmarksConfig() {
        return problemBenchmarksConfig;
    }

    public void setProblemBenchmarksConfig(ProblemBenchmarksConfig problemBenchmarksConfig) {
        this.problemBenchmarksConfig = problemBenchmarksConfig;
    }

    public Integer getSubSingleCount() {
        return subSingleCount;
    }

    public void setSubSingleCount(Integer subSingleCount) {
        this.subSingleCount = subSingleCount;
    }

    @Override
    public SolverBenchmarkConfig inherit(SolverBenchmarkConfig inheritedConfig) {
        solverConfig = ConfigUtils.inheritConfig(solverConfig, inheritedConfig.getSolverConfig());
        problemBenchmarksConfig = ConfigUtils.inheritConfig(problemBenchmarksConfig,
                inheritedConfig.getProblemBenchmarksConfig());
        subSingleCount = ConfigUtils.inheritOverwritableProperty(subSingleCount, inheritedConfig.getSubSingleCount());
        return this;
    }

    @Override
    public SolverBenchmarkConfig copyConfig() {
        return new SolverBenchmarkConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        if (solverConfig != null) {
            solverConfig.visitReferencedClasses(classVisitor);
        }
        if (problemBenchmarksConfig != null) {
            problemBenchmarksConfig.visitReferencedClasses(classVisitor);
        }
    }

}
