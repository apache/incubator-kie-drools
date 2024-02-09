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

import org.drools.model.codegen.ExecutableModelProject;
import org.drools.reliability.infinispan.InfinispanStorageManagerFactory;
import org.drools.reliability.test.util.TestConfigurationUtils;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.internal.utils.KieHelper;
import org.test.domain.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Example class to demonstrate how to use RemoteCacheManager.
 * <p>
 * Test with an Infinispan server running on localhost:11222
 * <p>
 * After running this example, you can run RemoteCacheManagerExampleAfterFailOver to see the results.
 * <p>
 * So the steps are:
 * docker run -p 11222:11222 -e USER="admin" -e PASS="secret" quay.io/infinispan/server:14.0
 * Run RemoteCacheManagerExample
 * Run RemoteCacheManagerExampleAfterFailOver
 */
public class RemoteCacheManagerExample {

    public static final String BASIC_RULE =
            "import " + Person.class.getCanonicalName() + ";" +
                    "global java.util.List results;" +
                    "rule X when\n" +
                    "  $s: String()\n" +
                    "  $p: Person( getName().startsWith($s) )\n" +
                    "then\n" +
                    "  results.add( $p.getName() );\n" +
                    "end";

    public static void main(String[] args) {
        // These 2 lines are needed because drools-reliability-tests contains both drools-reliability-h2mvstore and drools-reliability-infinispan modules
        // If only drools-reliability-infinispan is used for persistence, they are not needed
        System.setProperty(TestConfigurationUtils.DROOLS_RELIABILITY_MODULE_TEST, "INFINISPAN");
        TestConfigurationUtils.configureServicePriorities();

        KieSession session = getKieSession(PersistedSessionOption.newSession().withPersistenceStrategy(PersistedSessionOption.PersistenceStrategy.STORES_ONLY));

        long savedSessionId = session.getIdentifier();
        System.out.println("savedSessionId = " + savedSessionId);

        session.insert("M");
        session.insert(new Person("Mario", 40));

        //--- Simulate a crash
        System.out.println("Simulating a crash");
    }

    public static KieSession getKieSession(PersistedSessionOption option) {
        System.setProperty(InfinispanStorageManagerFactory.INFINISPAN_STORAGE_MODE, "REMOTE");
        System.setProperty(InfinispanStorageManagerFactory.INFINISPAN_STORAGE_REMOTE_HOST, "localhost");
        System.setProperty(InfinispanStorageManagerFactory.INFINISPAN_STORAGE_REMOTE_PORT, "11222");
        System.setProperty(InfinispanStorageManagerFactory.INFINISPAN_STORAGE_REMOTE_USER, "admin");
        System.setProperty(InfinispanStorageManagerFactory.INFINISPAN_STORAGE_REMOTE_PASS, "secret");

        System.setProperty(InfinispanStorageManagerFactory.INFINISPAN_STORAGE_ALLOWED_PACKAGES, "org.test.domain");

        KieBase kbase = new KieHelper().addContent(BASIC_RULE, ResourceType.DRL).build(ExecutableModelProject.class);
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption(option);
        KieSession session = kbase.newKieSession(conf, null);
        List<Object> results = new ArrayList<>();
        session.setGlobal("results", results);
        return session;
    }
}
