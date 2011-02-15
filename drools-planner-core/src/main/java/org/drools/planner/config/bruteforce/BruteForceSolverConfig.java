/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.config.bruteforce;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.AbstractSolverConfig;
import org.drools.planner.core.bruteforce.BruteForceSolver;
import org.drools.planner.core.bruteforce.DefaultBruteForceSolver;

/**
 * @author Geoffrey De Smet
 */
@XStreamAlias("bruteForceSolver")
public class BruteForceSolverConfig extends AbstractSolverConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public BruteForceSolver buildSolver() {
        DefaultBruteForceSolver bruteForceSolver = new DefaultBruteForceSolver();
        configureAbstractSolver(bruteForceSolver);
        return bruteForceSolver;
    }

    public void inherit(BruteForceSolverConfig inheritedConfig) {
        super.inherit(inheritedConfig);
    }

}
