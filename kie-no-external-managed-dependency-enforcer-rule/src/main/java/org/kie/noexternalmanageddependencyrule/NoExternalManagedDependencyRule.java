/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.noexternalmanageddependencyrule;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.Set;
import org.apache.maven.enforcer.rule.api.AbstractEnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;

/**
 * No External Managed Dependency Enforcer Rule
 * This rule is meant to forbid declaration of managed dependencies that are not part of the current multi-module maven project.
 */
@Named("noExternalManagedDependencyRule")
public class NoExternalManagedDependencyRule extends AbstractEnforcerRule {

    // Inject needed Maven components
    private final MavenProject project;
    private final ProjectBuilder projectBuilder;
    private final MavenSession mavenSession;

    /**
     * Comma-separated set of group ids to check for
     */
    private Set<String> filteredGroupIds;

    /**
     * Comma-separated set of group:artifact that are allowed
     */
    private Set<String> allowedGA;
    /**
     * The relative path to the root of the current multimodule project.
     */
    private String rootPom;

    @Inject
    public NoExternalManagedDependencyRule(MavenProject project, ProjectBuilder projectBuilder, MavenSession mavenSession) {
        this.project = Objects.requireNonNull(project);
        this.projectBuilder = Objects.requireNonNull(projectBuilder);
        this.mavenSession = Objects.requireNonNull(mavenSession);
    }

    public void execute() throws EnforcerRuleException {
        if (rootPom == null || rootPom.isEmpty()) {
            throw new EnforcerRuleException("The rootPom parameter is required and cannot be empty.");
        }
        if (filteredGroupIds == null || filteredGroupIds.isEmpty()) {
            throw new EnforcerRuleException("The filteredGroupIds parameter is required and cannot be empty.");
        }
        if (project.getOriginalModel().getDependencyManagement() == null && (project.getOriginalModel().getProfiles() == null || project.getOriginalModel().getProfiles().stream().allMatch(profile -> profile.getDependencyManagement() == null))) {
            // If there is no dependency management there is no need to check for managed dependencies
            return;
        }
        checkForManagedDependencies();
    }

    private void checkForManagedDependencies() throws EnforcerRuleException {
        MavenProject rootMavenProject = getRootMavenProject();
        Set<Dependency> implementedDependencies = getImplementedDependencies(rootMavenProject);
        checkForManagedDependenciesInProject(implementedDependencies);
        checkForManagedDependenciesInProfiles(implementedDependencies);
    }

    private void checkForManagedDependenciesInProject(Set<Dependency> implementedDependencies) throws EnforcerRuleException {
        Set<String> invalidManagedDependencies = invalidManagedDependencies(project.getOriginalModel().getDependencyManagement(), implementedDependencies);
        if (!invalidManagedDependencies.isEmpty()) {
            String invalidDependencies = String.join(",\r\n", invalidManagedDependencies);
            throw new EnforcerRuleException(String.format("The current pom %s:%s:%s has the following invalid managed dependencies:\n" +
                    "%s", project.getGroupId(), project.getArtifactId(), project.getVersion(), invalidDependencies));
        }
    }

    private void checkForManagedDependenciesInProfiles(Set<Dependency> implementedDependencies) throws EnforcerRuleException {
        if (project.getOriginalModel().getProfiles() != null) {
            for (Profile profile : project.getModel().getProfiles()) {
                checkForManagedDependenciesInProfile(profile, implementedDependencies);
            }
        }
    }

    private void checkForManagedDependenciesInProfile(Profile profile, Set<Dependency> implementedDependencies) throws EnforcerRuleException {
        Set<String> invalidManagedDependencies = invalidManagedDependencies(profile.getDependencyManagement(), implementedDependencies);
        if (!invalidManagedDependencies.isEmpty()) {
            String invalidDependencies = String.join(",\r\n", invalidManagedDependencies);
            throw new EnforcerRuleException(String.format("The profile %s in the current pom %s:%s:%s has the following invalid managed dependencies:\r\n%s", profile.getId(), project.getGroupId(),
                    project.getArtifactId(), project.getVersion(), invalidDependencies));
        }
    }

    private Set<String> invalidManagedDependencies(DependencyManagement dependencyManagement, Set<Dependency> implementedDependencies) {
        Set<String> toReturn = new HashSet<>();
        if (dependencyManagement != null) {
            for (Dependency dependency : dependencyManagement.getDependencies()) {
                if (!isAllowedGA(dependency) && isFiltered(dependency) && isNotAllowed(dependency, implementedDependencies)) {
                    toReturn.add(String.format("%s:%s:%s", dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion()));
                }
            }
        }
        return toReturn;
    }

    private boolean isAllowedGA(Dependency dependency) {
        return allowedGA != null && allowedGA.stream()
                .anyMatch(allowedGA -> allowedGA.equals(dependency.getGroupId() + ":" + dependency.getArtifactId()));
    }

    private boolean isFiltered(Dependency dependency) {
        return filteredGroupIds.contains(dependency.getGroupId());
    }

    private boolean isNotAllowed(Dependency dependency, Set<Dependency> implementedDependencies) {
        return implementedDependencies.stream()
                .noneMatch(implementedDependency -> implementedDependency.getGroupId().equals(dependency.getGroupId()) && implementedDependency.getArtifactId().equals(dependency.getArtifactId()));
    }

    private Set<Dependency> getImplementedDependencies(MavenProject project) throws EnforcerRuleException {
        Set<Dependency> toReturn = new HashSet<>();
        populateImplementedDependencies(project, toReturn);
        return toReturn;
    }

    private void populateImplementedDependencies(MavenProject project, Set<Dependency> toPopulate) throws EnforcerRuleException {
        Set<String> nestedModules = new HashSet<>();
        if (project.getModules() != null) {
            nestedModules.addAll(project.getModules());
        }
        if (project.getModel().getProfiles() != null) {
            for (Profile profile : project.getModel().getProfiles()) {
                if (profile.getModules() != null) {
                    nestedModules.addAll(profile.getModules());
                }
            }
        }
        if (!nestedModules.isEmpty()) {
            for (String module : nestedModules) {
                String nestedModulePom = project.getBasedir().getAbsolutePath() + File.separator + module + File.separator + "pom.xml";
                File nestedPom = new File(nestedModulePom);
                MavenProject nestedMavenProject = getMavenProject(nestedPom);
                Dependency dependency = new Dependency();
                dependency.setGroupId(nestedMavenProject.getGroupId());
                dependency.setArtifactId(nestedMavenProject.getArtifactId());
                dependency.setVersion(nestedMavenProject.getVersion());
                toPopulate.add(dependency);
                populateImplementedDependencies(nestedMavenProject, toPopulate);
            }
        }
    }

    private MavenProject getRootMavenProject() throws EnforcerRuleException {
        File rootPomFile = new File(project.getBasedir(), rootPom);
        return getMavenProject(rootPomFile);
    }

    private MavenProject getMavenProject(File pomFile) throws EnforcerRuleException {
        if (!pomFile.exists() || !pomFile.isFile() || !pomFile.canRead()) {
            throw new EnforcerRuleException(String.format("The pomFile file %s does not exist or cannot be read.", pomFile.getAbsolutePath()));
        }
        try {
            return projectBuilder.build(pomFile, mavenSession.getProjectBuildingRequest()).getProject();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            getLog().error(exceptionAsString);
            throw new EnforcerRuleException(String.format("Failed to build the Maven project from the pomFile file %s.", pomFile.getAbsolutePath()), e);
        }
    }

    /**
     * A good practice is provided toString method for Enforcer Rule.
     * <p>
     * Output is used in verbose Maven logs, can help during investigate problems.
     *
     * @return rule description
     */
    @Override
    public String toString() {
        return "NoExternalManagedDependencyRule";
    }

}