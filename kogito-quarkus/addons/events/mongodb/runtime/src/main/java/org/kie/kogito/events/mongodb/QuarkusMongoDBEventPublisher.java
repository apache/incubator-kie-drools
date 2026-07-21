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
package org.kie.kogito.events.mongodb;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.mongodb.transaction.AbstractTransactionManager;

import com.mongodb.client.MongoClient;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class QuarkusMongoDBEventPublisher extends MongoDBEventPublisher {

    @Inject
    MongoClient quarkusMongoClient;

    @Inject
    AbstractTransactionManager quarkusTransactionManager;

    @Inject
    @ConfigProperty(name = "kogito.events.processinstances.enabled", defaultValue = "true")
    boolean quarkusEnableProcessInstancesEvents;

    @Inject
    @ConfigProperty(name = "kogito.events.usertasks.enabled", defaultValue = "true")
    boolean quarkusEnableUserTasksEvents;

    @Inject
    @ConfigProperty(name = "kogito.events.database", defaultValue = "kogito-events")
    String quarkusEventsDatabaseName;

    @Inject
    @ConfigProperty(name = "kogito.events.processinstances.collection", defaultValue = "kogitoprocessinstancesevents")
    String quarkusProcessInstancesEventsCollectionName;

    @Inject
    @ConfigProperty(name = "kogito.events.usertasks.collection", defaultValue = "kogitousertaskinstancesevents")
    String quarkusUserTasksEventsCollectionName;

    @PostConstruct
    public void setupQuarkusMongoDBEventPublisher() {
        super.configure();
    }

    @Override
    protected MongoClient mongoClient() {
        return this.quarkusMongoClient;
    }

    @Override
    protected AbstractTransactionManager transactionManager() {
        return this.quarkusTransactionManager;
    }

    @Override
    protected boolean processInstancesEvents() {
        return this.quarkusEnableProcessInstancesEvents;
    }

    @Override
    protected boolean userTasksEvents() {
        return this.quarkusEnableUserTasksEvents;
    }

    @Override
    protected String eventsDatabaseName() {
        return this.quarkusEventsDatabaseName;
    }

    @Override
    protected String processInstancesEventsCollection() {
        return this.quarkusProcessInstancesEventsCollectionName;
    }

    @Override
    protected String userTasksEventsCollection() {
        return this.quarkusUserTasksEventsCollectionName;
    }
}
