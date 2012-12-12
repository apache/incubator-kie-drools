/*
 * Copyright 2011 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.command;

import static org.drools.persistence.util.PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.PersistenceUtil.cleanUp;
import static org.drools.persistence.util.PersistenceUtil.createEnvironment;

import java.util.HashMap;

import org.drools.persistence.util.PersistenceUtil;
import org.junit.After;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.KieSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;

public class SimpleBatchExecutionPersistenceTest extends SimpleBatchExecutionTest {

    private HashMap<String, Object> context;
 
    @After
    public void cleanUpPersistence() throws Exception {
        disposeKSession();
        cleanUp(context);
        context = null;
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) { 
        if( context == null ) { 
            context = PersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        }
        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, ksconf, createEnvironment(context));
    }  
}
