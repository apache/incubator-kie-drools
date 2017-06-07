/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.examples.workitemconsequence;

import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.FactHandle;

/**
 * A simple demonstration of using a Work Item handler as an action
 */
public class WorkItemConsequenceExample1 {

    public static void main(final String[] args) {

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "WorkItemConsequence1.drl",
                                                            WorkItemConsequenceExample1.class ),
                                                            ResourceType.DRL );

        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "Compilation error.\n" + kbuilder.getErrors().toString() );
        }

        final KieSession ksession = kbase.newKieSession();
        ksession.getWorkItemManager().registerWorkItemHandler( "EmailWorkItemHandler",
                                                               new EmailWorkItemHandler() );

        //        KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "log/WorkItemConsequence.log");

        //Brains does not cause concern
        PersonLocation brains = new PersonLocation( "Brains",
                                                    5 );
        ksession.insert( brains );
        ksession.fireAllRules();

        //Gargamel is too far away to care
        PersonLocation gargamel = new PersonLocation( "Gargamel",
                                                      10 );
        FactHandle gargamelFactHandle = ksession.insert( gargamel );
        ksession.fireAllRules();

        //Uh oh, Gargamel moves closer
        gargamel.setDistance( 5 );
        ksession.update( gargamelFactHandle,
                         gargamel );
        ksession.fireAllRules();

        //        logger.close();

        ksession.dispose(); // Stateful rule session must always be disposed when finished

    }

    public static class PersonLocation {

        private String name;
        private long   distance;

        public PersonLocation(String name,
                              long distance) {
            this.name = name;
            this.distance = distance;
        }

        public String getName() {
            return name;
        }

        public void setDistance(long distance) {
            this.distance = distance;
        }

        public long getDistance() {
            return distance;
        }

    }

    public static class Email {

        private String emailAddress;
        private String message;

        public Email(String emailAddress,
                     String message) {
            this.emailAddress = emailAddress;
            this.message = message;
        }

        public String getEmailAddress() {
            return this.emailAddress;
        }

        public String getMessage() {
            return this.message;
        }

    }

    public static class EmailWorkItemHandler
        implements
        WorkItemHandler {

        public void executeWorkItem(WorkItem workItem,
                                    WorkItemManager manager) {
            String emailAddress = (String) workItem.getParameter( "emailAddress" );
            String message = (String) workItem.getParameter( "message" );
            //Simulate sending an email
            System.out.println( "to: " + emailAddress + " --> " + message );
        }

        public void abortWorkItem(WorkItem workItem,
                                  WorkItemManager manager) {
        }

    }

}
