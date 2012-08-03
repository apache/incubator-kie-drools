/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.config.localsearch.decider.selector;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.localsearch.decider.selector.CompositeSelector;
import org.drools.planner.core.localsearch.decider.selector.MoveFactorySelector;
import org.drools.planner.core.localsearch.decider.selector.Selector;
import org.drools.planner.core.localsearch.decider.selector.TopListSelector;
import org.drools.planner.core.move.factory.MoveFactory;
import org.drools.planner.core.score.definition.ScoreDefinition;

@XStreamAlias("selector")
@Deprecated
public class SelectorConfig {

    @XStreamImplicit(itemFieldName = "selector")
    private List<SelectorConfig> selectorConfigList = null;

    private MoveFactory moveFactory = null;
    private Class<? extends MoveFactory> moveFactoryClass = null;
    protected Boolean shuffle = null;

    private Integer topSize = null;

    public List<SelectorConfig> getSelectorConfigList() {
        return selectorConfigList;
    }

    public void setSelectorConfigList(List<SelectorConfig> selectorConfigList) {
        this.selectorConfigList = selectorConfigList;
    }

    public MoveFactory getMoveFactory() {
        return moveFactory;
    }

    public void setMoveFactory(MoveFactory moveFactory) {
        this.moveFactory = moveFactory;
    }

    public Class<? extends MoveFactory> getMoveFactoryClass() {
        return moveFactoryClass;
    }

    public void setMoveFactoryClass(Class<? extends MoveFactory> moveFactoryClass) {
        this.moveFactoryClass = moveFactoryClass;
    }

    public Boolean getShuffle() {
        return shuffle;
    }

    public void setShuffle(Boolean shuffle) {
        this.shuffle = shuffle;
    }

    public Integer getTopSize() {
        return topSize;
    }

    public void setTopSize(Integer topSize) {
        this.topSize = topSize;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public Selector buildSelector(ScoreDefinition scoreDefinition) {
        if (selectorConfigList != null) {
            List<Selector> selectorList = new ArrayList<Selector>(selectorConfigList.size());
            for (SelectorConfig selectorConfig : selectorConfigList) {
                selectorList.add(selectorConfig.buildSelector(scoreDefinition));
            }
            CompositeSelector selector = new CompositeSelector();
            selector.setSelectorList(selectorList);
            return selector;
        } else if (moveFactory != null || moveFactoryClass != null) {
            MoveFactory initializedMoveFactory;
            if (moveFactory != null) {
                initializedMoveFactory = moveFactory;
            } else {
                initializedMoveFactory  = ConfigUtils.newInstance(this, "moveFactoryClass", moveFactoryClass);
            }
            MoveFactorySelector selector = new MoveFactorySelector();
            selector.setMoveFactory(initializedMoveFactory);
            boolean shuffleValue = (shuffle == null) ?  true : shuffle.booleanValue();
            selector.setShuffle(shuffleValue);
            return selector;
        } else if (topSize != null) {
            TopListSelector selector = new TopListSelector();
            selector.setTopSize(topSize);
            return selector;
        } else {
            throw new IllegalArgumentException("A selector requires configuration, for example a moveFactoryClass.");
        }
    }

    public void inherit(SelectorConfig inheritedConfig) {
        if (moveFactory == null && moveFactoryClass == null) {
            moveFactory = inheritedConfig.getMoveFactory();
            moveFactoryClass = inheritedConfig.getMoveFactoryClass();
        }
        if (selectorConfigList == null) {
            selectorConfigList = inheritedConfig.getSelectorConfigList();
        } else {
            List<SelectorConfig> inheritedSelectorConfigList = inheritedConfig.getSelectorConfigList();
            if (inheritedSelectorConfigList != null) {
                for (SelectorConfig selectorConfig : inheritedSelectorConfigList) {
                    selectorConfigList.add(selectorConfig);
                }
            }
        }
        if (shuffle == null) {
            shuffle = inheritedConfig.getShuffle();
        }
        if (topSize == null) {
            topSize = inheritedConfig.getTopSize();
        }
    }

}
