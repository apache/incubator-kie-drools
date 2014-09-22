/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.benchmark.config.blueprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;

@XStreamAlias("solverBenchmarkBluePrint")
public class SolverBenchmarkBluePrintConfig {

    protected SolverBenchmarkBluePrintType solverBenchmarkBluePrintType = null;

    public SolverBenchmarkBluePrintType getSolverBenchmarkBluePrintType() {
        return solverBenchmarkBluePrintType;
    }

    public void setSolverBenchmarkBluePrintType(SolverBenchmarkBluePrintType solverBenchmarkBluePrintType) {
        this.solverBenchmarkBluePrintType = solverBenchmarkBluePrintType;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public List<SolverBenchmarkConfig> buildSolverBenchmarkConfigList() {
        validate();
        List<SolverBenchmarkConfig> solverBenchmarkConfigList;
        switch (solverBenchmarkBluePrintType) {
            case ALL_CONSTRUCTION_HEURISTIC_TYPES:
                solverBenchmarkConfigList = buildAllConstructionHeuristicTypes();
                break;
            default:
                throw new IllegalStateException("The solverBenchmarkBluePrintType ("
                        + solverBenchmarkBluePrintType + ") is not implemented.");
        }
        return solverBenchmarkConfigList;
    }

    protected void validate() {
        if (solverBenchmarkBluePrintType == null) {
            throw new IllegalArgumentException(
                    "The solverBenchmarkBluePrint must have"
                            + " a solverBenchmarkBluePrintType (" + solverBenchmarkBluePrintType + ").");
        }
    }

    protected List<SolverBenchmarkConfig> buildAllConstructionHeuristicTypes() {
        ConstructionHeuristicType[] types = ConstructionHeuristicType.values();
        List<SolverBenchmarkConfig> solverBenchmarkConfigList = new ArrayList<SolverBenchmarkConfig>(types.length);
        for (ConstructionHeuristicType type : types) {
            SolverBenchmarkConfig solverBenchmarkConfig = new SolverBenchmarkConfig();
            solverBenchmarkConfig.setName(type.name());
            SolverConfig solverConfig = new SolverConfig();
            ConstructionHeuristicPhaseConfig phaseConfig = new ConstructionHeuristicPhaseConfig();
            phaseConfig.setConstructionHeuristicType(type);
            solverConfig.setPhaseConfigList(Collections.<PhaseConfig>singletonList(phaseConfig));
            solverBenchmarkConfig.setSolverConfig(solverConfig);
            solverBenchmarkConfigList.add(solverBenchmarkConfig);
        }
        return solverBenchmarkConfigList;
    }

}
