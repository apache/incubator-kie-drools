/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.assignment.impl.strategy;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.drools.core.ClassObjectFilter;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.task.TaskContext;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.assignment.Assignment;
import org.kie.internal.task.api.assignment.AssignmentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BusinessRuleAssignmentStrategy implements AssignmentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BusinessRuleAssignmentStrategy.class);    
    private static final String IDENTIFIER = "BusinessRule";
    
    private boolean active = IDENTIFIER.equals(System.getProperty("org.jbpm.task.assignment.strategy"));

    private String releaseId = System.getProperty("org.jbpm.task.assignment.rules.releaseId");
    private String scannerInterval = System.getProperty("org.jbpm.task.assignment.rules.scan");
    
    
    private KieServices kieServices = KieServices.Factory.get();
    private KieContainer kieContainer;
    private KieScanner kieScanner;
        
    public BusinessRuleAssignmentStrategy() {
        
        if (active) {                   
            if (releaseId == null) {
                throw new IllegalArgumentException("BusinessRule assignment strategy requires release id to be given via system property 'org.jbpm.task.assignment.rules.releaseId'");
            }
            String[] gav = releaseId.split(":");
            logger.debug("Creating KieContainer for {} to be used for task assignments", releaseId);
            this.kieContainer = kieServices.newKieContainer(kieServices.newReleaseId(gav[0], gav[1], gav[2]));
            
            if (scannerInterval != null) {
                Long pollingInterval = Long.parseLong(scannerInterval);
                logger.debug("Scanner to be enabled for {} container with polling interval set to {}", kieContainer, pollingInterval);
                
                this.kieScanner = this.kieServices.newKieScanner(kieContainer);
                this.kieScanner.start(pollingInterval);
                logger.debug("Scanner for container {} started at {}", kieContainer, new Date());
            }
        }
    }
    
    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Assignment apply(Task task, TaskContext context, String excludedUser) {
        if (!active) {
            logger.debug("{} strategy is not active", this);
            return null;
        }
        logger.debug("Using rules to assign actual owner to task {}", task);
        KieSession kieSession = this.kieContainer.newKieSession();
        try {
            context.loadTaskVariables(task);
            
            kieSession.insert(task);
            kieSession.fireAllRules();
            Set<Assignment> assignments = new TreeSet<>();
            String queryName = System.getProperty("org.jbpm.task.assignment.rules.query");
            if (queryName != null) {
                logger.debug("Query {} is going to be used to retrieve results from working memory", queryName);
                QueryResults results = kieSession.getQueryResults(queryName);
                results.forEach(row -> 
                    assignments.add((Assignment)row.get("assignment")));
            } else {
                logger.debug("No query defined, retrieving all facts of type Assignment");
                Collection<Assignment> results = (Collection<Assignment>) kieSession.getObjects(new ClassObjectFilter(Assignment.class));
                
                assignments.addAll(results);
            }
            logger.debug("Rule evaluation completed with selected assignments of {}", assignments);
            if (assignments.isEmpty()) {
                logger.debug("No assignments found by BusinessRule strategy");
                return null;
            }
            
            Assignment selected = assignments.iterator().next();
            logger.debug("Selected assignment is {} for task {}", selected, task);
            return selected;
            
        } finally {
            kieSession.dispose();
            logger.debug("KieSession in BusinessRule disposed");
        }       
    }

    @Override
    public String toString() {
        return "BusinessRuleAssignmentStrategy [releaseId=" + releaseId + "]";
    }

}
