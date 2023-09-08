/**
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
package org.drools.reliability.test.example.infinispan;

import org.drools.reliability.test.util.TestConfigurationUtils;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.PersistedSessionOption;

/**
 * Example class to demonstrate how to use RemoteCacheManager.
 * <p>
 * See RemoteCacheManagerExample for more details.
 */
public class RemoteCacheManagerExampleAfterFailOver {

    public static void main(String[] args) {
        System.setProperty(TestConfigurationUtils.DROOLS_RELIABILITY_MODULE_TEST, "INFINISPAN");
        TestConfigurationUtils.configureServicePriorities();

        // Here we use the savedSessionId to retrieve the session. Explicitly 0 for now, but it could be different.
        KieSession session = RemoteCacheManagerExample.getKieSession(PersistedSessionOption.fromSession(0).withPersistenceStrategy(PersistedSessionOption.PersistenceStrategy.STORES_ONLY));

        try {
            System.out.println("fireAllRules");
            session.fireAllRules();
            Object results = session.getGlobal("results");
            System.out.println("results = " + results);
        } finally {
            session.dispose();
        }
    }
}
