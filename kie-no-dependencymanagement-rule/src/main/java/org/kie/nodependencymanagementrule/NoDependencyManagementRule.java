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
package org.kie.nodependencymanagementrule;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.Set;
import org.apache.maven.enforcer.rule.api.AbstractEnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.project.MavenProject;

/**
 * No DependencyManagement Enforcer Rule
 * This rule is meant to forbid dependencyManagement tag in pom
 */
@Named("noDependencyManagementRule")
public class NoDependencyManagementRule extends AbstractEnforcerRule {

    // Inject needed Maven components
    private final MavenProject project;

    /**
     * Set of allowed poms.
     */
    private Set<String> allowedPoms;

    @Inject
    public NoDependencyManagementRule(MavenProject project) {
        this.project = Objects.requireNonNull(project);
    }

    public void execute() throws EnforcerRuleException {
        if (!isAllowed(project.getGroupId(), project.getArtifactId())) {
            checkForDependencyManagement();
        }
    }

    private void checkForDependencyManagement() throws EnforcerRuleException {
        if (project.getDependencyManagement() != null && project.getDependencyManagement().getLocation("") != null) { // The getLocation("") retrieve the position, in the pom, of that specific dependencyManagement element; it is null when such an element is inherited
            throw new EnforcerRuleException(String.format("The current pom %s:%s:%s has dependencyManagement tag!", project.getGroupId(), project.getArtifactId(), project.getVersion()));
        }
    }

    private boolean isAllowed(String groupId, String artifactId) {
        String ga = String.format("%s:%s", groupId, artifactId);
        return allowedPoms != null && !allowedPoms.isEmpty() && allowedPoms.contains(ga);
    }

    /*    *//**
             * If your rule is cacheable, you must return a unique id when parameters or conditions
             * change that would cause the result to be different. Multiple cached results are stored
             * based on their id.
             * <p>
             * The easiest way to do this is to return a hash computed from the values of your parameters.
             * <p>
             * If your rule is not cacheable, then you don't need to override this method or return null
             *//*
                * @Override
                * public String getCacheId() {
                * //no hash on boolean...only parameter so no hash is needed.
                * return Boolean.toString(shouldIfail);
                * }
                */

    /**
     * A good practice is provided toString method for Enforcer Rule.
     * <p>
     * Output is used in verbose Maven logs, can help during investigate problems.
     *
     * @return rule description
     */
    @Override
    public String toString() {
        return "NoDependencyManagementRule";
    }

}