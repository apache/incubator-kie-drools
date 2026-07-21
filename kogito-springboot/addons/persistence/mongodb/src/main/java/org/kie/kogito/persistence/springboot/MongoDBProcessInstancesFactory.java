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
package org.kie.kogito.persistence.springboot;

import java.util.List;

import org.kie.kogito.internal.process.runtime.HeadersPersistentConfig;
import org.kie.kogito.mongodb.AbstractProcessInstancesFactory;
import org.kie.kogito.mongodb.transaction.AbstractTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoClient;

@Component
public class MongoDBProcessInstancesFactory extends AbstractProcessInstancesFactory {

    @Autowired
    public MongoDBProcessInstancesFactory(MongoClient mongoClient,
            AbstractTransactionManager transactionManager,
            @Value("${kogito.persistence.optimistic.lock:false}") Boolean lock,
            @Value("${spring.data.mongodb.database:kogito}") String dbName,
            @Value("${kogito.persistence.headers.enabled:false}") Boolean headersEnabled,
            @Value("${kogito.persistence.headers.excluded:}") List<String> headersExcluded) {
        super(mongoClient, dbName, lock, transactionManager, new HeadersPersistentConfig(headersEnabled, headersExcluded));
    }

}
