package org.optaplanner.core.config.phase;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.partitionedsearch.PartitionedSearchPhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlSeeAlso({
        ConstructionHeuristicPhaseConfig.class,
        CustomPhaseConfig.class,
        ExhaustiveSearchPhaseConfig.class,
        LocalSearchPhaseConfig.class,
        NoChangePhaseConfig.class,
        PartitionedSearchPhaseConfig.class
})
@XmlType(propOrder = {
        "terminationConfig"
})
public abstract class PhaseConfig<Config_ extends PhaseConfig<Config_>> extends AbstractConfig<Config_> {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    @XmlElement(name = "termination")
    private TerminationConfig terminationConfig = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public TerminationConfig getTerminationConfig() {
        return terminationConfig;
    }

    public void setTerminationConfig(TerminationConfig terminationConfig) {
        this.terminationConfig = terminationConfig;
    }

    @Override
    public Config_ inherit(Config_ inheritedConfig) {
        terminationConfig = ConfigUtils.inheritConfig(terminationConfig, inheritedConfig.getTerminationConfig());
        return (Config_) this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
