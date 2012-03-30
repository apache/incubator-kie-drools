/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.score.director;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.solution.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link ScoreDirector}.
 * @see ScoreDirector
 */
public abstract class AbstractScoreDirector<F extends ScoreDirectorFactory> implements ScoreDirector {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected final F scoreDirectorFactory;

    protected Solution workingSolution;

    protected long calculateCount = 0L;

    protected AbstractScoreDirector(F scoreDirectorFactory) {
        this.scoreDirectorFactory = scoreDirectorFactory;
    }

    public SolutionDescriptor getSolutionDescriptor() {
        return scoreDirectorFactory.getSolutionDescriptor();
    }

    public ScoreDefinition getScoreDefinition() {
        return scoreDirectorFactory.getScoreDefinition();
    }

    public Solution getWorkingSolution() {
        return workingSolution;
    }

    public long getCalculateCount() {
        return calculateCount;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public List<Object> getWorkingPlanningEntityList() {
        return getSolutionDescriptor().getPlanningEntityList(workingSolution);
    }

    public boolean isWorkingSolutionInitialized() {
        return getSolutionDescriptor().isInitialized(workingSolution);
    }

    public Map<Object, List<Object>> getVariableToEntitiesMap(PlanningVariableDescriptor variableDescriptor) {
        List<Object> entityList = variableDescriptor.getPlanningEntityDescriptor().extractEntities(
                workingSolution);
        Map<Object, List<Object>> variableToEntitiesMap = new HashMap<Object, List<Object>>(entityList.size());
        for (Object entity : entityList) {
            Object variable = variableDescriptor.getValue(entity);
            List<Object> subEntities = variableToEntitiesMap.get(variable);
            if (subEntities == null) {
                subEntities = new ArrayList<Object>();
                variableToEntitiesMap.put(variable, subEntities);
            }
            subEntities.add(entity);
        }
        return variableToEntitiesMap;
    }

    public void assertWorkingScore(Score workingScore) {
        ScoreDirector uncorruptedScoreDirector = scoreDirectorFactory.buildScoreDirector();
        uncorruptedScoreDirector.setWorkingSolution(workingSolution);
        Score uncorruptedScore = uncorruptedScoreDirector.calculateScore();
        if (!workingScore.equals(uncorruptedScore)) {
            String scoreCorruptionAnalysis = buildScoreCorruptionAnalysis(uncorruptedScoreDirector);
            uncorruptedScoreDirector.dispose();
            throw new IllegalStateException(
                    "Score corruption: the workingScore (" + workingScore + ") is not the uncorruptedScore ("
                            + uncorruptedScore + "):\n"
                            + scoreCorruptionAnalysis);
        } else {
            uncorruptedScoreDirector.dispose();
        }
    }

    protected abstract String buildScoreCorruptionAnalysis(ScoreDirector uncorruptedScoreDirector);

}
