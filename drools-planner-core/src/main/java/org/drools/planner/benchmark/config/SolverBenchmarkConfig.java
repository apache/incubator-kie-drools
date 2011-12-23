/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.benchmark.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.drools.planner.benchmark.core.SolverBenchmark;
import org.drools.planner.config.solver.SolverConfig;

@XStreamAlias("solverBenchmark")
public class SolverBenchmarkConfig {

    private String name = null;

    @XStreamAlias("solver")
    private SolverConfig solverConfig = null;
    @XStreamImplicit(itemFieldName = "unsolvedSolutionFile")
    private List<File> unsolvedSolutionFileList = null;

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

    public List<File> getUnsolvedSolutionFileList() {
        return unsolvedSolutionFileList;
    }

    public void setUnsolvedSolutionFileList(List<File> unsolvedSolutionFileList) {
        this.unsolvedSolutionFileList = unsolvedSolutionFileList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public SolverBenchmark buildSolverBenchmark() {
        validate();
        SolverBenchmark solverBenchmark = new SolverBenchmark();
        solverBenchmark.setName(name);
        solverBenchmark.setSolverConfig(solverConfig);
        solverBenchmark.setUnsolvedSolutionFileList(unsolvedSolutionFileList);
        return solverBenchmark;
    }

    private void validate() {
        if (unsolvedSolutionFileList == null || unsolvedSolutionFileList.isEmpty()) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <unsolvedSolutionFile> for the <solverBenchmark> (" + name
                            + ") directly or indirectly by inheriting it.");
        }
    }

    public void inherit(SolverBenchmarkConfig inheritedConfig) {
        if (solverConfig == null) {
            solverConfig = inheritedConfig.getSolverConfig();
        } else if (inheritedConfig.getSolverConfig() != null) {
            solverConfig.inherit(inheritedConfig.getSolverConfig());
        }
        if (unsolvedSolutionFileList == null) {
            unsolvedSolutionFileList = inheritedConfig.getUnsolvedSolutionFileList();
        } else if (inheritedConfig.getUnsolvedSolutionFileList() != null) {
            // The inherited unsolvedSolutionFiles should be before the non-inherited one
            List<File> mergedList = new ArrayList<File>(inheritedConfig.getUnsolvedSolutionFileList());
            for (File unsolvedSolutionFile : unsolvedSolutionFileList) {
                if (!mergedList.contains(unsolvedSolutionFile)) {
                    mergedList.add(unsolvedSolutionFile);
                }
            }
            unsolvedSolutionFileList = mergedList;
        }
    }

}
