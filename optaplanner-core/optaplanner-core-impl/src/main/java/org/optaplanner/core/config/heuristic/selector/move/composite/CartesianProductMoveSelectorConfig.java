package org.optaplanner.core.config.heuristic.selector.move.composite;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveIteratorFactoryConfig;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveListFactoryConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.TailChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "moveSelectorConfigList",
        "ignoreEmptyChildIterators"
})
public class CartesianProductMoveSelectorConfig extends MoveSelectorConfig<CartesianProductMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "cartesianProductMoveSelector";

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
    private List<MoveSelectorConfig> moveSelectorConfigList = null;

    private Boolean ignoreEmptyChildIterators = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public CartesianProductMoveSelectorConfig() {
    }

    public CartesianProductMoveSelectorConfig(List<MoveSelectorConfig> moveSelectorConfigList) {
        this.moveSelectorConfigList = moveSelectorConfigList;
    }

    /**
     * @deprecated in favor of {@link #getMoveSelectorList()}.
     * @return sometimes null
     */
    @Deprecated
    public List<MoveSelectorConfig> getMoveSelectorConfigList() {
        return getMoveSelectorList();
    }

    /**
     * @deprecated in favor of {@link #setMoveSelectorList(List)}.
     * @param moveSelectorConfigList sometimes null
     */
    @Deprecated
    public void setMoveSelectorConfigList(List<MoveSelectorConfig> moveSelectorConfigList) {
        setMoveSelectorList(moveSelectorConfigList);
    }

    public List<MoveSelectorConfig> getMoveSelectorList() {
        return moveSelectorConfigList;
    }

    public void setMoveSelectorList(List<MoveSelectorConfig> moveSelectorConfigList) {
        this.moveSelectorConfigList = moveSelectorConfigList;
    }

    public Boolean getIgnoreEmptyChildIterators() {
        return ignoreEmptyChildIterators;
    }

    public void setIgnoreEmptyChildIterators(Boolean ignoreEmptyChildIterators) {
        this.ignoreEmptyChildIterators = ignoreEmptyChildIterators;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public CartesianProductMoveSelectorConfig withMoveSelectorList(List<MoveSelectorConfig> moveSelectorConfigList) {
        this.moveSelectorConfigList = moveSelectorConfigList;
        return this;
    }

    public CartesianProductMoveSelectorConfig withMoveSelectors(MoveSelectorConfig... moveSelectorConfigs) {
        this.moveSelectorConfigList = Arrays.asList(moveSelectorConfigs);
        return this;
    }

    public CartesianProductMoveSelectorConfig withIgnoreEmptyChildIterators(Boolean ignoreEmptyChildIterators) {
        this.ignoreEmptyChildIterators = ignoreEmptyChildIterators;
        return this;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void extractLeafMoveSelectorConfigsIntoList(List<MoveSelectorConfig> leafMoveSelectorConfigList) {
        for (MoveSelectorConfig moveSelectorConfig : moveSelectorConfigList) {
            moveSelectorConfig.extractLeafMoveSelectorConfigsIntoList(leafMoveSelectorConfigList);
        }
    }

    @Override
    public CartesianProductMoveSelectorConfig inherit(CartesianProductMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        moveSelectorConfigList =
                ConfigUtils.inheritMergeableListConfig(moveSelectorConfigList, inheritedConfig.getMoveSelectorList());
        ignoreEmptyChildIterators = ConfigUtils.inheritOverwritableProperty(
                ignoreEmptyChildIterators, inheritedConfig.getIgnoreEmptyChildIterators());
        return this;
    }

    @Override
    public CartesianProductMoveSelectorConfig copyConfig() {
        return new CartesianProductMoveSelectorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        visitCommonReferencedClasses(classVisitor);
        if (moveSelectorConfigList != null) {
            moveSelectorConfigList.forEach(ms -> ms.visitReferencedClasses(classVisitor));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + moveSelectorConfigList + ")";
    }

}
