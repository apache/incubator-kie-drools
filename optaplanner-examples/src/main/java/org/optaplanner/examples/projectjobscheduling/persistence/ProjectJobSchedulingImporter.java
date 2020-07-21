/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.projectjobscheduling.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.projectjobscheduling.app.ProjectJobSchedulingApp;
import org.optaplanner.examples.projectjobscheduling.domain.Allocation;
import org.optaplanner.examples.projectjobscheduling.domain.ExecutionMode;
import org.optaplanner.examples.projectjobscheduling.domain.Job;
import org.optaplanner.examples.projectjobscheduling.domain.JobType;
import org.optaplanner.examples.projectjobscheduling.domain.Project;
import org.optaplanner.examples.projectjobscheduling.domain.ResourceRequirement;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;
import org.optaplanner.examples.projectjobscheduling.domain.resource.GlobalResource;
import org.optaplanner.examples.projectjobscheduling.domain.resource.LocalResource;
import org.optaplanner.examples.projectjobscheduling.domain.resource.Resource;

public class ProjectJobSchedulingImporter extends AbstractTxtSolutionImporter<Schedule> {

    public static void main(String[] args) {
        SolutionConverter<Schedule> converter = SolutionConverter.createImportConverter(
                ProjectJobSchedulingApp.DATA_DIR_NAME, new ProjectJobSchedulingImporter(), Schedule.class);
        converter.convertAll();
    }

    @Override
    public TxtInputBuilder<Schedule> createTxtInputBuilder() {
        return new ProjectJobSchedulingInputBuilder();
    }

    public static class ProjectJobSchedulingInputBuilder extends TxtInputBuilder<Schedule> {

        private Schedule schedule;

        private int projectListSize;
        private int resourceListSize;
        private int globalResourceListSize;

        private long projectId = 0L;
        private long resourceId = 0L;
        private long jobId = 0L;
        private long executionModeId = 0L;
        private long resourceRequirementId = 0L;

        private Map<Project, File> projectFileMap;

        @Override
        public Schedule readSolution() throws IOException {
            schedule = new Schedule();
            schedule.setId(0L);
            readProjectList();
            readResourceList();
            for (Map.Entry<Project, File> entry : projectFileMap.entrySet()) {
                readProjectFile(entry.getKey(), entry.getValue());
            }
            removePointlessExecutionModes();
            createAllocationList();
            logger.info("Schedule {} has {} projects, {} jobs, {} execution modes, {} resources"
                    + " and {} resource requirements.",
                    getInputId(),
                    schedule.getProjectList().size(),
                    schedule.getJobList().size(),
                    schedule.getExecutionModeList().size(),
                    schedule.getResourceList().size(),
                    schedule.getResourceRequirementList().size());
            return schedule;
        }

        private void readProjectList() throws IOException {
            projectListSize = readIntegerValue();
            List<Project> projectList = new ArrayList<>(projectListSize);
            projectFileMap = new LinkedHashMap<>(projectListSize);
            for (int i = 0; i < projectListSize; i++) {
                Project project = new Project();
                project.setId(projectId);
                project.setReleaseDate(readIntegerValue());
                project.setCriticalPathDuration(readIntegerValue());
                File projectFile = new File(inputFile.getParentFile(), readStringValue());
                if (!projectFile.exists()) {
                    throw new IllegalArgumentException("The projectFile (" + projectFile + ") does not exist.");
                }
                projectFileMap.put(project, projectFile);
                projectList.add(project);
                projectId++;
            }
            schedule.setProjectList(projectList);
            schedule.setJobList(new ArrayList<>(projectListSize * 10));
            schedule.setExecutionModeList(new ArrayList<>(projectListSize * 10 * 5));
        }

        private void readResourceList() throws IOException {
            resourceListSize = readIntegerValue();
            String[] tokens = splitBySpacesOrTabs(readStringValue(), resourceListSize);
            List<Resource> resourceList = new ArrayList<>(resourceListSize * projectListSize * 10);
            for (int i = 0; i < resourceListSize; i++) {
                int capacity = Integer.parseInt(tokens[i]);
                if (capacity != -1) {
                    GlobalResource resource = new GlobalResource();
                    resource.setId(resourceId);
                    resource.setCapacity(capacity);
                    resourceList.add(resource);
                    resourceId++;
                }
            }
            globalResourceListSize = resourceList.size();
            schedule.setResourceList(resourceList);
            schedule.setResourceRequirementList(new ArrayList<>(
                    projectListSize * 10 * 5 * resourceListSize));
        }

        private void readProjectFile(Project project, File projectFile) {
            try (BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(projectFile), "UTF-8"))) {
                ProjectFileInputBuilder projectFileInputBuilder = new ProjectFileInputBuilder(schedule, project);
                projectFileInputBuilder.setInputFile(projectFile);
                projectFileInputBuilder.setBufferedReader(bufferedReader);
                try {
                    projectFileInputBuilder.readSolution();
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Exception in projectFile (" + projectFile + ")", e);
                } catch (IllegalStateException e) {
                    throw new IllegalStateException("Exception in projectFile (" + projectFile + ")", e);
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not read the projectFile (" + projectFile + ").", e);
            }
        }

        public class ProjectFileInputBuilder extends TxtInputBuilder {

            private Schedule schedule;
            private Project project;

            private int jobListSize;
            private int renewableLocalResourceSize;
            private int nonrenewableLocalResourceSize;

            public ProjectFileInputBuilder(Schedule schedule, Project project) {
                this.schedule = schedule;
                this.project = project;
            }

            @Override
            public Schedule readSolution() throws IOException {
                readHeader();
                readResourceList();
                readProjectInformation();
                readPrecedenceRelations();
                readRequestDurations();
                readResourceAvailabilities();
                detectPointlessSuccessor();
                return null; // Hack so the code can reuse read methods from TxtInputBuilder
            }

            private void readHeader() throws IOException {
                readConstantLine("\\*+");
                readStringValue("file with basedata *:");
                readStringValue("initial value random generator *:");
                readConstantLine("\\*+");
                int projects = readIntegerValue("projects *:");
                if (projects != 1) {
                    throw new IllegalArgumentException("The projects value (" + projects + ") should always be 1.");
                }
                jobListSize = readIntegerValue("jobs \\(incl\\. supersource/sink *\\) *:");
                int horizon = readIntegerValue("horizon *:");
                // Ignore horizon
            }

            private void readResourceList() throws IOException {
                readConstantLine("RESOURCES");
                int renewableResourceSize = readIntegerValue("\\- renewable *:", "R");
                if (renewableResourceSize < globalResourceListSize) {
                    throw new IllegalArgumentException("The renewableResourceSize (" + renewableResourceSize
                            + ") cannot be less than globalResourceListSize (" + globalResourceListSize + ").");
                }
                renewableLocalResourceSize = renewableResourceSize - globalResourceListSize;
                nonrenewableLocalResourceSize = readIntegerValue("\\- nonrenewable *:", "N");
                int doublyConstrainedResourceSize = readIntegerValue("\\- doubly constrained *:", "D");
                if (doublyConstrainedResourceSize != 0) {
                    throw new IllegalArgumentException("The doublyConstrainedResourceSize ("
                            + doublyConstrainedResourceSize + ") should always be 0.");
                }
                List<LocalResource> localResourceList = new ArrayList<>(
                        globalResourceListSize + renewableLocalResourceSize + nonrenewableLocalResourceSize);
                for (int i = 0; i < renewableLocalResourceSize; i++) {
                    LocalResource localResource = new LocalResource();
                    localResource.setId(resourceId);
                    localResource.setProject(project);
                    localResource.setRenewable(true);
                    resourceId++;
                    localResourceList.add(localResource);
                }
                for (int i = 0; i < nonrenewableLocalResourceSize; i++) {
                    LocalResource localResource = new LocalResource();
                    localResource.setId(resourceId);
                    localResource.setProject(project);
                    localResource.setRenewable(false);
                    resourceId++;
                    localResourceList.add(localResource);
                }
                project.setLocalResourceList(localResourceList);
                schedule.getResourceList().addAll(localResourceList);
                readConstantLine("\\*+");
            }

            private void readProjectInformation() throws IOException {
                readConstantLine("PROJECT INFORMATION:");
                readConstantLine("pronr\\. +\\#jobs +rel\\.date +duedate +tardcost +MPM\\-Time");
                String[] tokens = splitBySpacesOrTabs(readStringValue(), 6);
                if (Integer.parseInt(tokens[0]) != 1) {
                    throw new IllegalArgumentException("The project information tokens (" + Arrays.toString(tokens)
                            + ") index 0 should be 1.");
                }
                if (Integer.parseInt(tokens[1]) != jobListSize - 2) {
                    throw new IllegalArgumentException("The project information tokens (" + Arrays.toString(tokens)
                            + ") index 1 should be " + (jobListSize - 2) + ".");
                }
                // Ignore releaseDate, dueDate, tardinessCost and mpmTime
                readConstantLine("\\*+");
            }

            private void readPrecedenceRelations() throws IOException {
                readConstantLine("PRECEDENCE RELATIONS:");
                readConstantLine("jobnr\\. +\\#modes +\\#successors +successors");
                List<Job> jobList = new ArrayList<>(jobListSize);
                for (int i = 0; i < jobListSize; i++) {
                    Job job = new Job();
                    job.setId(jobId);
                    job.setProject(project);
                    if (i == 0) {
                        job.setJobType(JobType.SOURCE);
                    } else if (i == jobListSize - 1) {
                        job.setJobType(JobType.SINK);
                    } else {
                        job.setJobType(JobType.STANDARD);
                    }
                    jobList.add(job);
                    jobId++;
                }
                project.setJobList(jobList);
                schedule.getJobList().addAll(jobList);
                for (int i = 0; i < jobListSize; i++) {
                    Job job = jobList.get(i);
                    String[] tokens = splitBySpacesOrTabs(readStringValue());
                    if (tokens.length < 3) {
                        throw new IllegalArgumentException("The tokens (" + Arrays.toString(tokens)
                                + ") should be at least 3 in length.");
                    }
                    if (Integer.parseInt(tokens[0]) != i + 1) {
                        throw new IllegalArgumentException("The tokens (" + Arrays.toString(tokens)
                                + ") index 0 should be " + (i + 1) + ".");
                    }
                    int executionModeListSize = Integer.parseInt(tokens[1]);
                    List<ExecutionMode> executionModeList = new ArrayList<>(executionModeListSize);
                    for (int j = 0; j < executionModeListSize; j++) {
                        ExecutionMode executionMode = new ExecutionMode();
                        executionMode.setId(executionModeId);
                        executionMode.setJob(job);
                        executionModeList.add(executionMode);
                        executionModeId++;
                    }
                    job.setExecutionModeList(executionModeList);
                    schedule.getExecutionModeList().addAll(executionModeList);
                    int successorJobListSize = Integer.parseInt(tokens[2]);
                    if (tokens.length != 3 + successorJobListSize) {
                        throw new IllegalArgumentException("The tokens (" + Arrays.toString(tokens)
                                + ") should be " + (3 + successorJobListSize) + " in length.");
                    }
                    List<Job> successorJobList = new ArrayList<>(successorJobListSize);
                    for (int j = 0; j < successorJobListSize; j++) {
                        int successorIndex = Integer.parseInt(tokens[3 + j]);
                        Job successorJob = project.getJobList().get(successorIndex - 1);
                        successorJobList.add(successorJob);
                    }
                    job.setSuccessorJobList(successorJobList);
                }
                readConstantLine("\\*+");
            }

            private void readRequestDurations() throws IOException {
                readConstantLine("REQUESTS/DURATIONS:");
                splitBySpacesOrTabs(readStringValue());
                readConstantLine("\\-+");
                int resourceSize = globalResourceListSize + renewableLocalResourceSize + nonrenewableLocalResourceSize;
                for (int i = 0; i < jobListSize; i++) {
                    Job job = project.getJobList().get(i);
                    int executionModeSize = job.getExecutionModeList().size();
                    for (int j = 0; j < executionModeSize; j++) {
                        ExecutionMode executionMode = job.getExecutionModeList().get(j);
                        boolean first = j == 0;
                        String[] tokens = splitBySpacesOrTabs(readStringValue(), (first ? 3 : 2) + resourceSize);
                        if (first && Integer.parseInt(tokens[0]) != i + 1) {
                            throw new IllegalArgumentException("The tokens (" + Arrays.toString(tokens)
                                    + ") index 0 should be " + (i + 1) + ".");
                        }
                        if (Integer.parseInt(tokens[first ? 1 : 0]) != j + 1) {
                            throw new IllegalArgumentException("The tokens (" + Arrays.toString(tokens)
                                    + ") index " + (first ? 1 : 0) + " should be " + (j + 1) + ".");
                        }
                        int duration = Integer.parseInt(tokens[first ? 2 : 1]);
                        executionMode.setDuration(duration);
                        List<ResourceRequirement> resourceRequirementList = new ArrayList<>(
                                resourceSize);
                        for (int k = 0; k < resourceSize; k++) {
                            int requirement = Integer.parseInt(tokens[(first ? 3 : 2) + k]);
                            if (requirement != 0) {
                                ResourceRequirement resourceRequirement = new ResourceRequirement();
                                resourceRequirement.setId(resourceRequirementId);
                                resourceRequirement.setExecutionMode(executionMode);
                                Resource resource;
                                if (k < globalResourceListSize) {
                                    resource = schedule.getResourceList().get(k);
                                } else {
                                    resource = project.getLocalResourceList().get(k - globalResourceListSize);
                                }
                                resourceRequirement.setResource(resource);
                                resourceRequirement.setRequirement(requirement);
                                resourceRequirementList.add(resourceRequirement);
                                resourceRequirementId++;
                            }
                        }
                        executionMode.setResourceRequirementList(resourceRequirementList);
                        schedule.getResourceRequirementList().addAll(resourceRequirementList);
                    }
                }
                readConstantLine("\\*+");
            }

            private void readResourceAvailabilities() throws IOException {
                readConstantLine("RESOURCEAVAILABILITIES:");
                splitBySpacesOrTabs(readStringValue());
                int resourceSize = globalResourceListSize + renewableLocalResourceSize + nonrenewableLocalResourceSize;
                String[] tokens = splitBySpacesOrTabs(readStringValue(), resourceSize);
                for (int i = 0; i < resourceSize; i++) {
                    int capacity = Integer.parseInt(tokens[i]);
                    if (i < globalResourceListSize) {
                        // Overwritten by global resource
                    } else {
                        Resource resource = project.getLocalResourceList().get(i - globalResourceListSize);
                        resource.setCapacity(capacity);
                    }
                }
                readConstantLine("\\*+");
            }

            private void detectPointlessSuccessor() {
                for (Job baseJob : project.getJobList()) {
                    Set<Job> baseSuccessorJobSet = new HashSet<>(baseJob.getSuccessorJobList());
                    Set<Job> checkedSuccessorSet = new HashSet<>(project.getJobList().size());
                    Queue<Job> uncheckedSuccessorQueue = new ArrayDeque<>(project.getJobList().size());
                    for (Job baseSuccessorJob : baseJob.getSuccessorJobList()) {
                        uncheckedSuccessorQueue.addAll(baseSuccessorJob.getSuccessorJobList());
                    }
                    while (!uncheckedSuccessorQueue.isEmpty()) {
                        Job uncheckedJob = uncheckedSuccessorQueue.remove();
                        if (checkedSuccessorSet.contains(uncheckedJob)) {
                            continue;
                        }
                        if (baseSuccessorJobSet.contains(uncheckedJob)) {
                            throw new IllegalStateException("The baseJob (" + baseJob
                                    + ") has a direct successor (" + uncheckedJob
                                    + ") that is also an indirect successor. That's pointless.");
                        }
                        uncheckedSuccessorQueue.addAll(uncheckedJob.getSuccessorJobList());
                    }
                }
            }

        }

        private void removePointlessExecutionModes() {
            // TODO iterate through schedule.getJobList(), find pointless ExecutionModes
            // and delete them both from the job and from schedule.getExecutionModeList()
        }

        private void createAllocationList() {
            List<Job> jobList = schedule.getJobList();
            List<Allocation> allocationList = new ArrayList<>(jobList.size());
            Map<Job, Allocation> jobToAllocationMap = new HashMap<>(jobList.size());
            Map<Project, Allocation> projectToSourceAllocationMap = new HashMap<>(projectListSize);
            Map<Project, Allocation> projectToSinkAllocationMap = new HashMap<>(projectListSize);
            for (Job job : jobList) {
                Allocation allocation = new Allocation();
                allocation.setId(job.getId());
                allocation.setJob(job);
                allocation.setPredecessorAllocationList(new ArrayList<>(job.getSuccessorJobList().size()));
                allocation.setSuccessorAllocationList(new ArrayList<>(job.getSuccessorJobList().size()));
                // Uninitialized allocations take no time, but don't break the predecessorsDoneDate cascade to sink.
                allocation.setPredecessorsDoneDate(job.getProject().getReleaseDate());
                if (job.getJobType() == JobType.SOURCE) {
                    allocation.setDelay(0);
                    if (job.getExecutionModeList().size() != 1) {
                        throw new IllegalArgumentException("The job (" + job
                                + ")'s executionModeList (" + job.getExecutionModeList()
                                + ") is expected to be a singleton.");
                    }
                    allocation.setExecutionMode(job.getExecutionModeList().get(0));
                    projectToSourceAllocationMap.put(job.getProject(), allocation);
                } else if (job.getJobType() == JobType.SINK) {
                    allocation.setDelay(0);
                    if (job.getExecutionModeList().size() != 1) {
                        throw new IllegalArgumentException("The job (" + job
                                + ")'s executionModeList (" + job.getExecutionModeList()
                                + ") is expected to be a singleton.");
                    }
                    allocation.setExecutionMode(job.getExecutionModeList().get(0));
                    projectToSinkAllocationMap.put(job.getProject(), allocation);
                }
                allocationList.add(allocation);
                jobToAllocationMap.put(job, allocation);
            }
            for (Allocation allocation : allocationList) {
                Job job = allocation.getJob();
                allocation.setSourceAllocation(projectToSourceAllocationMap.get(job.getProject()));
                allocation.setSinkAllocation(projectToSinkAllocationMap.get(job.getProject()));
                for (Job successorJob : job.getSuccessorJobList()) {
                    Allocation successorAllocation = jobToAllocationMap.get(successorJob);
                    allocation.getSuccessorAllocationList().add(successorAllocation);
                    successorAllocation.getPredecessorAllocationList().add(allocation);
                }
            }
            for (Allocation sourceAllocation : projectToSourceAllocationMap.values()) {
                for (Allocation allocation : sourceAllocation.getSuccessorAllocationList()) {
                    allocation.setPredecessorsDoneDate(sourceAllocation.getEndDate());
                }
            }
            schedule.setAllocationList(allocationList);
        }

    }

}
