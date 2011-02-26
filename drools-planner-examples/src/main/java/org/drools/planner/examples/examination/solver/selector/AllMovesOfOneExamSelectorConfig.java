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

package org.drools.planner.examples.examination.solver.selector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.localsearch.decider.selector.SelectorConfig;
import org.drools.planner.core.localsearch.decider.selector.Selector;
import org.drools.planner.core.score.definition.ScoreDefinition;

/**
 * A custom selector configuration for the Examination example.
 * @see AllMovesOfOneExamSelector
 */
public class AllMovesOfOneExamSelectorConfig extends SelectorConfig {

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public Selector buildSelector(ScoreDefinition scoreDefinition) {
        // Note that all properties of SelectorConfig are ignored.
        return new AllMovesOfOneExamSelector();
    }

    @Override
    public void inherit(SelectorConfig inheritedConfig) {
        // Note that all inherited properties are ignored because all properties of SelectorConfig are ignored.
        super.inherit(inheritedConfig);
        if (inheritedConfig instanceof AllMovesOfOneExamSelectorConfig) {
            AllMovesOfOneExamSelectorConfig allMovesOfOneExamSelectorConfig
                    = (AllMovesOfOneExamSelectorConfig) inheritedConfig;
            // Nothing specifically inheritable at the moment
        }
    }

}
