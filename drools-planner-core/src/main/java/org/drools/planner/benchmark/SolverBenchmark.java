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

package org.drools.planner.benchmark;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.drools.planner.config.localsearch.LocalSearchSolverConfig;
import org.drools.planner.core.score.Score;

@XStreamAlias("solverBenchmark")
public class SolverBenchmark {

    private String name = null;

    @XStreamAlias("localSearchSolver")
    private LocalSearchSolverConfig localSearchSolverConfig = null;
    @XStreamImplicit(itemFieldName = "unsolvedSolutionFile")
    private List<File> unsolvedSolutionFileList = null;

    @XStreamImplicit(itemFieldName = "solverBenchmarkResult")
    private List<SolverBenchmarkResult> solverBenchmarkResultList = null;

    // Ranking starts from 0
    private Integer ranking = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalSearchSolverConfig getLocalSearchSolverConfig() {
        return localSearchSolverConfig;
    }

    public void setLocalSearchSolverConfig(LocalSearchSolverConfig localSearchSolverConfig) {
        this.localSearchSolverConfig = localSearchSolverConfig;
    }

    public List<File> getUnsolvedSolutionFileList() {
        return unsolvedSolutionFileList;
    }

    public void setUnsolvedSolutionFileList(List<File> unsolvedSolutionFileList) {
        this.unsolvedSolutionFileList = unsolvedSolutionFileList;
    }

    public List<SolverBenchmarkResult> getSolverBenchmarkResultList() {
        return solverBenchmarkResultList;
    }

    public void setSolverBenchmarkResultList(List<SolverBenchmarkResult> solverBenchmarkResultList) {
        this.solverBenchmarkResultList = solverBenchmarkResultList;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public void inherit(SolverBenchmark inheritedSolverBenchmark) {
        if (localSearchSolverConfig == null) {
            localSearchSolverConfig = inheritedSolverBenchmark.getLocalSearchSolverConfig();
        } else if (inheritedSolverBenchmark.getLocalSearchSolverConfig() != null) {
            localSearchSolverConfig.inherit(inheritedSolverBenchmark.getLocalSearchSolverConfig());
        }
        if (unsolvedSolutionFileList == null) {
            unsolvedSolutionFileList = inheritedSolverBenchmark.getUnsolvedSolutionFileList();
        } else if (inheritedSolverBenchmark.getUnsolvedSolutionFileList() != null) {
            // The inherited unsolvedSolutionFiles should be before the non-inherited unsolvedSolutionFiles.
            List<File> mergedList = new ArrayList<File>(inheritedSolverBenchmark.getUnsolvedSolutionFileList());
            for (File unsolvedSolutionFile : unsolvedSolutionFileList) {
                if (!mergedList.contains(unsolvedSolutionFile)) {
                    mergedList.add(unsolvedSolutionFile);
                }
            }
            unsolvedSolutionFileList = mergedList;
        }
    }

    public void resetSolverBenchmarkResultList() {
        solverBenchmarkResultList = new ArrayList<SolverBenchmarkResult>();
        for (File unsolvedSolutionFile : unsolvedSolutionFileList) {
            SolverBenchmarkResult result = new SolverBenchmarkResult();
            result.setUnsolvedSolutionFile(unsolvedSolutionFile);
            solverBenchmarkResultList.add(result);
        }
    }

    public List<Score> getScoreList() {
        List<Score> scoreList = new ArrayList<Score>(solverBenchmarkResultList.size());
        for (SolverBenchmarkResult solverBenchmarkResult : solverBenchmarkResultList) {
            scoreList.add(solverBenchmarkResult.getScore());
        }
        return scoreList;
    }

    /**
     * @return the total score
     */
    public Score getTotalScore() {
        Score totalScore = null;
        for (SolverBenchmarkResult solverBenchmarkResult : solverBenchmarkResultList) {
            if (totalScore == null) {
                totalScore = solverBenchmarkResult.getScore();
            } else {
                totalScore = totalScore.add(solverBenchmarkResult.getScore());
            }
        }
        return totalScore;
    }

    /**
     * @return the average score
     */
    public Score getAverageScore() {
        return getTotalScore().divide(solverBenchmarkResultList.size());
    }

}
