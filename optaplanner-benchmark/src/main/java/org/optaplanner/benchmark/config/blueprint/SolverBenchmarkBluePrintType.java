/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchType;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;

public enum SolverBenchmarkBluePrintType {
    /**
     * @deprecated in favor of {@link #EVERY_CONSTRUCTION_HEURISTIC_TYPE}. This will be removed in 7.0.
     */
    @Deprecated
    ALL_CONSTRUCTION_HEURISTIC_TYPES,
    /**
     * Run every {@link ConstructionHeuristicType}.
     */
    EVERY_CONSTRUCTION_HEURISTIC_TYPE,
    /**
     * Run the default {@link ConstructionHeuristicType} with every {@link LocalSearchType}.
     */
    EVERY_LOCAL_SEARCH_TYPE,
    /**
     * Run every {@link ConstructionHeuristicType} with every {@link LocalSearchType}.
     */
    EVERY_CONSTRUCTION_HEURISTIC_TYPE_WITH_EVERY_LOCAL_SEARCH_TYPE;

    protected List<SolverBenchmarkConfig> buildSolverBenchmarkConfigList() {
        switch (this) {
            case EVERY_CONSTRUCTION_HEURISTIC_TYPE:
                return buildEveryConstructionHeuristicType();
            case EVERY_LOCAL_SEARCH_TYPE:
                return buildEveryLocalSearchType();
            case EVERY_CONSTRUCTION_HEURISTIC_TYPE_WITH_EVERY_LOCAL_SEARCH_TYPE:
                return buildEveryConstructionHeuristicTypeWithEveryLocalSearchType();
            default:
                throw new IllegalStateException("The solverBenchmarkBluePrintType ("
                        + this + ") is not implemented.");
        }
    }

    private List<SolverBenchmarkConfig> buildEveryConstructionHeuristicType() {
        ConstructionHeuristicType[] chTypes = ConstructionHeuristicType.getBluePrintTypes();
        List<SolverBenchmarkConfig> solverBenchmarkConfigList = new ArrayList<SolverBenchmarkConfig>(chTypes.length);
        for (ConstructionHeuristicType chType : chTypes) {
            solverBenchmarkConfigList.add(buildSolverBenchmarkConfig(chType, null));
        }
        return solverBenchmarkConfigList;
    }

    private List<SolverBenchmarkConfig> buildEveryLocalSearchType() {
        LocalSearchType[] lsTypes = LocalSearchType.getBluePrintTypes();
        List<SolverBenchmarkConfig> solverBenchmarkConfigList = new ArrayList<SolverBenchmarkConfig>(lsTypes.length);
        for (LocalSearchType lsType : lsTypes) {
            solverBenchmarkConfigList.add(buildSolverBenchmarkConfig(null, lsType));
        }
        return solverBenchmarkConfigList;
    }

    private List<SolverBenchmarkConfig> buildEveryConstructionHeuristicTypeWithEveryLocalSearchType() {
        ConstructionHeuristicType[] chTypes = ConstructionHeuristicType.getBluePrintTypes();
        LocalSearchType[] lsTypes = LocalSearchType.getBluePrintTypes();
        List<SolverBenchmarkConfig> solverBenchmarkConfigList = new ArrayList<SolverBenchmarkConfig>(
                chTypes.length * lsTypes.length);
        for (ConstructionHeuristicType chType : chTypes) {
            for (LocalSearchType lsType : lsTypes) {
                solverBenchmarkConfigList.add(buildSolverBenchmarkConfig(chType, lsType));
            }
        }
        return solverBenchmarkConfigList;
    }

    protected SolverBenchmarkConfig buildSolverBenchmarkConfig(
            ConstructionHeuristicType constructionHeuristicType, LocalSearchType localSearchType) {
        SolverBenchmarkConfig solverBenchmarkConfig = new SolverBenchmarkConfig();
        String name = localSearchType == null ? constructionHeuristicType.name() :
                constructionHeuristicType == null ? localSearchType.name() :
                constructionHeuristicType.name() + "-" + localSearchType.name();
        solverBenchmarkConfig.setName(name);
        SolverConfig solverConfig = new SolverConfig();
        List<PhaseConfig> phaseConfigList = new ArrayList<PhaseConfig>(2);
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        if (constructionHeuristicType != null) {
            constructionHeuristicPhaseConfig.setConstructionHeuristicType(constructionHeuristicType);
        }
        phaseConfigList.add(constructionHeuristicPhaseConfig);
        if (localSearchType != null) {
            LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
            localSearchPhaseConfig.setLocalSearchType(localSearchType);
            phaseConfigList.add(localSearchPhaseConfig);
        }
        solverConfig.setPhaseConfigList(phaseConfigList);
        solverBenchmarkConfig.setSolverConfig(solverConfig);
        return solverBenchmarkConfig;
    }

}
