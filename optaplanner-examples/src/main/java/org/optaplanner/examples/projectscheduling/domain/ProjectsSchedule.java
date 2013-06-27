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

package org.optaplanner.examples.projectscheduling.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.projectscheduling.domain.resource.GlobalResource;
import org.optaplanner.examples.projectscheduling.domain.resource.LocalResource;
import org.optaplanner.examples.projectscheduling.domain.resource.Resource;
import org.optaplanner.persistence.xstream.XStreamBendableScoreConverter;

@PlanningSolution
@XStreamAlias("PsProjectsSchedule")
public class ProjectsSchedule extends AbstractPersistable implements Solution<BendableScore> {

    private List<GlobalResource> globalResourceList;
    private List<Project> projectList;
    private List<Job> jobList;
    private List<LocalResource> localResourceList;

    private List<Allocation> allocationList;

    @XStreamConverter(value = XStreamBendableScoreConverter.class, ints = {1, 2})
    private BendableScore score;

    public List<GlobalResource> getGlobalResourceList() {
        return globalResourceList;
    }

    public void setGlobalResourceList(List<GlobalResource> globalResourceList) {
        this.globalResourceList = globalResourceList;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    public List<Job> getJobList() {
        return jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    public List<LocalResource> getLocalResourceList() {
        return localResourceList;
    }

    public void setLocalResourceList(List<LocalResource> localResourceList) {
        this.localResourceList = localResourceList;
    }

    @PlanningEntityCollectionProperty
    public List<Allocation> getAllocationList() {
        return allocationList;
    }

    public void setAllocationList(List<Allocation> allocationList) {
        this.allocationList = allocationList;
    }

    public BendableScore getScore() {
        return score;
    }

    public void setScore(BendableScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.addAll(globalResourceList);
        facts.addAll(projectList);
        facts.addAll(jobList);
        facts.addAll(localResourceList);
        // Do not add the planning entity's (allocationList) because that will be done automatically
        return facts;
    }

}
