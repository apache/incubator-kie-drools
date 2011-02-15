/**
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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.core.score.Score;

/**
 * @author Geoffrey De Smet
 */
@XStreamAlias("solverBenchmarkResult")
public class SolverBenchmarkResult {

    private File unsolvedSolutionFile = null;
    private Score score = null;
    private Long timeMillisSpend = null;
    private File solvedSolutionFile = null;

    public File getUnsolvedSolutionFile() {
        return unsolvedSolutionFile;
    }

    public void setUnsolvedSolutionFile(File unsolvedSolutionFile) {
        this.unsolvedSolutionFile = unsolvedSolutionFile;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public Long getTimeMillisSpend() {
        return timeMillisSpend;
    }

    public void setTimeMillisSpend(Long timeMillisSpend) {
        this.timeMillisSpend = timeMillisSpend;
    }

    public File getSolvedSolutionFile() {
        return solvedSolutionFile;
    }

    public void setSolvedSolutionFile(File solvedSolutionFile) {
        this.solvedSolutionFile = solvedSolutionFile;
    }
    
}
