/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.events.mongodb;

import javax.annotation.PostConstruct;

import org.kie.kogito.mongodb.transaction.AbstractTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoClient;

@Component
public class SpringbootMongoDBEventPublisher extends MongoDBEventPublisher {

    @Autowired
    MongoClient springMongoClient;

    @Autowired
    AbstractTransactionManager springTransactionManager;

    @Value("${kogito.events.processinstances.enabled:true}")
    boolean springEnableProcessInstancesEvents;

    @Value("${kogito.events.usertasks.enabled:true}")
    boolean springEnableUserTasksEvents;

    @Value("${kogito.events.variables.enabled:true}")
    boolean springEnableVariablesEvents;

    @Value("${kogito.events.database:kogito-events}")
    String springEventsDatabaseName;

    @Value("${kogito.events.processinstances.collection:kogitoprocessinstancesevents}")
    String springProcessInstancesEventsCollectionName;

    @Value("${kogito.events.usertasks.collection:kogitousertaskinstancesevents}")
    String springUserTasksEventsCollectionName;

    @Value("${kogito.events.variables.collection:kogitovariablesevents}")
    String springVariablesEventsCollectionName;

    @PostConstruct
    public void setupSpringbootMongoDBEventPublisher() {
        super.configure();
    }

    @Override
    protected MongoClient mongoClient() {
        return this.springMongoClient;
    }

    @Override
    protected AbstractTransactionManager transactionManager() {
        return this.springTransactionManager;
    }

    @Override
    protected boolean processInstancesEvents() {
        return this.springEnableProcessInstancesEvents;
    }

    @Override
    protected boolean userTasksEvents() {
        return this.springEnableUserTasksEvents;
    }

    @Override
    protected boolean variablesEvents() {
        return this.springEnableVariablesEvents;
    }

    @Override
    protected String eventsDatabaseName() {
        return this.springEventsDatabaseName;
    }

    @Override
    protected String processInstancesEventsCollection() {
        return this.springProcessInstancesEventsCollectionName;
    }

    @Override
    protected String userTasksEventsCollection() {
        return this.springUserTasksEventsCollectionName;
    }

    @Override
    protected String variablesEventsCollection() {
        return this.springVariablesEventsCollectionName;
    }
}
