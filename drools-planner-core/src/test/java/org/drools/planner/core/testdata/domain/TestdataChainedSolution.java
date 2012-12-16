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

package org.drools.planner.core.testdata.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.planner.api.domain.solution.PlanningEntityCollectionProperty;
import org.drools.planner.api.domain.solution.PlanningSolution;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.score.buildin.simple.SimpleScore;
import org.drools.planner.core.solution.Solution;

import static org.mockito.Mockito.mock;

@PlanningSolution
public class TestdataChainedSolution extends TestdataObject implements Solution<SimpleScore> {

    public static SolutionDescriptor buildSolutionDescriptor() {
        SolutionDescriptor solutionDescriptor = new SolutionDescriptor(TestdataChainedSolution.class);
        solutionDescriptor.processAnnotations();
        PlanningEntityDescriptor entityDescriptor = new PlanningEntityDescriptor(
                solutionDescriptor, TestdataChainedEntity.class);
        entityDescriptor.processAnnotations();
        solutionDescriptor.addPlanningEntityDescriptor(
                entityDescriptor);
        return solutionDescriptor;
    }

    private List<TestdataChainedAnchor> chainedAnchorList;
    private List<TestdataChainedEntity> chainedEntityList;

    private SimpleScore score;

    public TestdataChainedSolution() {
    }

    public TestdataChainedSolution(String code) {
        super(code);
    }

    public List<TestdataChainedAnchor> getChainedAnchorList() {
        return chainedAnchorList;
    }

    public void setChainedAnchorList(List<TestdataChainedAnchor> chainedAnchorList) {
        this.chainedAnchorList = chainedAnchorList;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataChainedEntity> getChainedEntityList() {
        return chainedEntityList;
    }

    public void setChainedEntityList(List<TestdataChainedEntity> chainedEntityList) {
        this.chainedEntityList = chainedEntityList;
    }

    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public Collection<? extends Object> getProblemFacts() {
        return chainedAnchorList;
    }

}
