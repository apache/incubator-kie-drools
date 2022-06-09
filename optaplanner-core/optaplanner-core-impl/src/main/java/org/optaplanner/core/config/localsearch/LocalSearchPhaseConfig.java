package org.optaplanner.core.config.localsearch;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.CartesianProductMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveIteratorFactoryConfig;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveListFactoryConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.TailChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.LocalSearchAcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "localSearchType",
        "moveSelectorConfig",
        "acceptorConfig",
        "foragerConfig"
})
public class LocalSearchPhaseConfig extends PhaseConfig<LocalSearchPhaseConfig> {

    public static final String XML_ELEMENT_NAME = "localSearch";

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected LocalSearchType localSearchType = null;

    @XmlElements({
            @XmlElement(name = CartesianProductMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = CartesianProductMoveSelectorConfig.class),
            @XmlElement(name = ChangeMoveSelectorConfig.XML_ELEMENT_NAME, type = ChangeMoveSelectorConfig.class),
            @XmlElement(name = MoveIteratorFactoryConfig.XML_ELEMENT_NAME, type = MoveIteratorFactoryConfig.class),
            @XmlElement(name = MoveListFactoryConfig.XML_ELEMENT_NAME, type = MoveListFactoryConfig.class),
            @XmlElement(name = PillarChangeMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = PillarChangeMoveSelectorConfig.class),
            @XmlElement(name = PillarSwapMoveSelectorConfig.XML_ELEMENT_NAME, type = PillarSwapMoveSelectorConfig.class),
            @XmlElement(name = SubChainChangeMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = SubChainChangeMoveSelectorConfig.class),
            @XmlElement(name = SubChainSwapMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = SubChainSwapMoveSelectorConfig.class),
            @XmlElement(name = SwapMoveSelectorConfig.XML_ELEMENT_NAME, type = SwapMoveSelectorConfig.class),
            @XmlElement(name = TailChainSwapMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = TailChainSwapMoveSelectorConfig.class),
            @XmlElement(name = UnionMoveSelectorConfig.XML_ELEMENT_NAME, type = UnionMoveSelectorConfig.class)
    })
    private MoveSelectorConfig moveSelectorConfig = null;
    @XmlElement(name = "acceptor")
    private LocalSearchAcceptorConfig acceptorConfig = null;
    @XmlElement(name = "forager")
    private LocalSearchForagerConfig foragerConfig = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public LocalSearchType getLocalSearchType() {
        return localSearchType;
    }

    public void setLocalSearchType(LocalSearchType localSearchType) {
        this.localSearchType = localSearchType;
    }

    public MoveSelectorConfig getMoveSelectorConfig() {
        return moveSelectorConfig;
    }

    public void setMoveSelectorConfig(MoveSelectorConfig moveSelectorConfig) {
        this.moveSelectorConfig = moveSelectorConfig;
    }

    public LocalSearchAcceptorConfig getAcceptorConfig() {
        return acceptorConfig;
    }

    public void setAcceptorConfig(LocalSearchAcceptorConfig acceptorConfig) {
        this.acceptorConfig = acceptorConfig;
    }

    public LocalSearchForagerConfig getForagerConfig() {
        return foragerConfig;
    }

    public void setForagerConfig(LocalSearchForagerConfig foragerConfig) {
        this.foragerConfig = foragerConfig;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public LocalSearchPhaseConfig withLocalSearchType(LocalSearchType localSearchType) {
        this.localSearchType = localSearchType;
        return this;
    }

    public LocalSearchPhaseConfig withMoveSelectorConfig(MoveSelectorConfig moveSelectorConfig) {
        this.moveSelectorConfig = moveSelectorConfig;
        return this;
    }

    public LocalSearchPhaseConfig withAcceptorConfig(LocalSearchAcceptorConfig acceptorConfig) {
        this.acceptorConfig = acceptorConfig;
        return this;
    }

    public LocalSearchPhaseConfig withForagerConfig(LocalSearchForagerConfig foragerConfig) {
        this.foragerConfig = foragerConfig;
        return this;
    }

    @Override
    public LocalSearchPhaseConfig inherit(LocalSearchPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        localSearchType = ConfigUtils.inheritOverwritableProperty(localSearchType,
                inheritedConfig.getLocalSearchType());
        setMoveSelectorConfig(ConfigUtils.inheritOverwritableProperty(
                getMoveSelectorConfig(), inheritedConfig.getMoveSelectorConfig()));
        acceptorConfig = ConfigUtils.inheritConfig(acceptorConfig, inheritedConfig.getAcceptorConfig());
        foragerConfig = ConfigUtils.inheritConfig(foragerConfig, inheritedConfig.getForagerConfig());
        return this;
    }

    @Override
    public LocalSearchPhaseConfig copyConfig() {
        return new LocalSearchPhaseConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        if (getTerminationConfig() != null) {
            getTerminationConfig().visitReferencedClasses(classVisitor);
        }
        if (moveSelectorConfig != null) {
            moveSelectorConfig.visitReferencedClasses(classVisitor);
        }
        if (acceptorConfig != null) {
            acceptorConfig.visitReferencedClasses(classVisitor);
        }
        if (foragerConfig != null) {
            foragerConfig.visitReferencedClasses(classVisitor);
        }
    }

}
