/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.persistence.quarkus;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.mongodb.AbstractProcessInstancesFactory;
import org.kie.kogito.mongodb.transaction.AbstractTransactionManager;

import com.mongodb.client.MongoClient;

@ApplicationScoped
public class MongoDBProcessInstancesFactory extends AbstractProcessInstancesFactory {

    public MongoDBProcessInstancesFactory() {
        super(null, null, null, null);
    }

    @Inject
    public MongoDBProcessInstancesFactory(MongoClient mongoClient,
            AbstractTransactionManager transactionManager,
            @ConfigProperty(name = "quarkus.mongodb.database", defaultValue = "kogito") String dbName,
            @ConfigProperty(name = "kogito.persistence.optimistic.lock", defaultValue = "false") Boolean lock) {
        super(mongoClient, dbName, lock, transactionManager);
    }

}
