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

package org.optaplanner.examples.projectscheduling.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.projectscheduling.domain.resource.GlobalResource;
import org.optaplanner.examples.projectscheduling.domain.Job;
import org.optaplanner.examples.projectscheduling.domain.Project;
import org.optaplanner.examples.projectscheduling.domain.ProjectsSchedule;
import org.optaplanner.examples.projectscheduling.domain.resource.LocalResource;
import org.optaplanner.examples.projectscheduling.domain.resource.Resource;

public class ProjectSchedulingSolutionImporter extends AbstractTxtSolutionImporter {

    public static void main(String[] args) {
        new ProjectSchedulingSolutionImporter().convertAll();
    }

    public ProjectSchedulingSolutionImporter() {
        super(new ProjectSchedulingDao());
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new ProjectSchedulingInputBuilder();
    }

    public class ProjectSchedulingInputBuilder extends TxtInputBuilder {

        private ProjectsSchedule projectsSchedule;

        private int projectListSize;
        private int resourceListSize;

        private Map<Project, File> projectFileMap;

        public Solution readSolution() throws IOException {
            projectsSchedule = new ProjectsSchedule();
            projectsSchedule.setId(0L);
            readProjectList();
            readResourceList();
            for (Map.Entry<Project, File> entry : projectFileMap.entrySet()) {
                readProjectFile(entry.getKey(), entry.getValue());
            }
//            BigInteger possibleSolutionSize = BigInteger.valueOf(projectsSchedule.getBedList().size()).pow(
//                    projectsSchedule.getAdmissionPartList().size());
//            String flooredPossibleSolutionSize = "10^" + (possibleSolutionSize.toString().length() - 1);
//            logger.info("PatientAdmissionSchedule {} has {} specialisms, {} equipments, {} departments, {} rooms, "
//                    + "{} beds, {} nights, {} patients and {} admissions with a search space of {}.",
//                    getInputId(),
//                    projectsSchedule.getSpecialismList().size(),
//                    projectsSchedule.getEquipmentList().size(),
//                    projectsSchedule.getDepartmentList().size(),
//                    projectsSchedule.getRoomList().size(),
//                    projectsSchedule.getBedList().size(),
//                    projectsSchedule.getNightList().size(),
//                    projectsSchedule.getPatientList().size(),
//                    projectsSchedule.getAdmissionPartList().size(),
//                    flooredPossibleSolutionSize);
            return projectsSchedule;
        }

        private void readProjectList() throws IOException {
            projectListSize = readIntegerValue();
            List<Project> projectList = new ArrayList<Project>(projectListSize);
            projectFileMap = new LinkedHashMap<Project, File>(projectListSize);
            long projectId = 0L;
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
            projectsSchedule.setProjectList(projectList);
            projectsSchedule.setJobList(new ArrayList<Job>(projectListSize * 10));
        }

        private void readResourceList() throws IOException {
            resourceListSize = readIntegerValue();
            String[] tokens = splitBySpacesOrTabs(readStringValue(), resourceListSize);
            List<GlobalResource> globalResourceList = new ArrayList<GlobalResource>(resourceListSize);
            long globalResourceId = 0L;
            for (int i = 0; i < resourceListSize; i++) {
                int capacity = Integer.parseInt(tokens[i]);
                if (capacity != -1) {
                    GlobalResource resource = new GlobalResource();
                    resource.setId(globalResourceId);
                    resource.setCapacity(capacity);
                    globalResourceList.add(resource);
                    globalResourceId++;
                }
            }
            projectsSchedule.setGlobalResourceList(globalResourceList);
            projectsSchedule.setLocalResourceList(new ArrayList<LocalResource>(
                    (resourceListSize - globalResourceList.size()) * projectListSize));
        }

        private void readProjectFile(Project project, File projectFile) {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(projectFile), "UTF-8"));
                ProjectFileInputBuilder projectFileInputBuilder = new ProjectFileInputBuilder(projectsSchedule, project);
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
            } finally {
                IOUtils.closeQuietly(bufferedReader);
            }
        }

    }

    public class ProjectFileInputBuilder extends TxtInputBuilder {

        private ProjectsSchedule projectsSchedule;
        private List<GlobalResource> globalResourceList;
        private Project project;

        private int jobListSize;

        public ProjectFileInputBuilder(ProjectsSchedule projectsSchedule, Project project) {
            this.projectsSchedule = projectsSchedule;
            globalResourceList = projectsSchedule.getGlobalResourceList();
            this.project = project;
        }

        public Solution readSolution() throws IOException {
            readHeader();
            return null; // Hack so the code can reuse read methods from TxtInputBuilder
        }

        private void readHeader() throws IOException {
            readRegexConstantLine("\\*+");
            readStringValue("file with basedata            :");
            readStringValue("initial value random generator:");
            readRegexConstantLine("\\*+");
            int projects = readIntegerValue("projects                      :");
            if (projects != 1) {
                throw new IllegalArgumentException("The projects value (" + projects + ") should always be 1.");
            }
            jobListSize = readIntegerValue("jobs (incl. supersource/sink ):");
            int horizon = readIntegerValue("horizon                       :");
            project.setHorizon(horizon);
            readConstantLine("RESOURCES");
            int globalResourceListSize = globalResourceList.size();
            int renewableResourceSize = readIntegerValue("- renewable                 :", "R");
            if (renewableResourceSize < globalResourceListSize) {
                throw new IllegalArgumentException("The renewableResourceSize (" + renewableResourceSize
                        + ") can not be less than globalResourceListSize (" + globalResourceListSize + ").");
            }
            int nonrenewableResourceSize = readIntegerValue("- nonrenewable              :", "N");
            int doublyConstrainedResourceSize = readIntegerValue("- doubly constrained        :", "D");
            if (doublyConstrainedResourceSize != 0) {
                throw new IllegalArgumentException("The doublyConstrainedResourceSize ("
                        + doublyConstrainedResourceSize + ") should always be 0.");
            }
            List<LocalResource> localResourceList = new ArrayList<LocalResource>(
                    renewableResourceSize - globalResourceListSize + nonrenewableResourceSize);
            long localResourceId = 0L;
            for (int i = globalResourceListSize; i < renewableResourceSize; i++) {
                LocalResource localResource = new LocalResource();
                localResource.setId(localResourceId);
                localResource.setProject(project);
                localResource.setRenewable(true);
                localResourceId++;
                localResourceList.add(localResource);
                // TODO
            }
            for (int i = 0; i < nonrenewableResourceSize; i++) {
                // TODO
            }
            project.setLocalResourceList(localResourceList);
            projectsSchedule.getLocalResourceList().addAll(localResourceList);


            readRegexConstantLine("\\*+");

            List<Job> jobList = new ArrayList<Job>(jobListSize);

            project.setJobList(jobList);
            projectsSchedule.getJobList().addAll(jobList);
        }

    }

}
