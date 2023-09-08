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
package org.drools.persistence.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.drools.core.impl.RuleBaseFactory;
import org.drools.mvel.compiler.command.MoreBatchExecutionTest;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import static org.drools.persistence.util.DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.DroolsPersistenceUtil.OPTIMISTIC_LOCKING;
import static org.drools.persistence.util.DroolsPersistenceUtil.PESSIMISTIC_LOCKING;
import static org.drools.persistence.util.DroolsPersistenceUtil.cleanUp;
import static org.drools.persistence.util.DroolsPersistenceUtil.createEnvironment;

@RunWith(Parameterized.class)
public class MoreBatchExecutionPersistenceTest extends MoreBatchExecutionTest {

    private Map<String, Object> context;
    private boolean locking;
    
    @Parameters(name="{0}")
    public static Collection<Object[]> persistence() {
        Object[][] locking = new Object[][] { 
                { OPTIMISTIC_LOCKING }, 
                { PESSIMISTIC_LOCKING } 
                };
        return Arrays.asList(locking);
    };
    
    public MoreBatchExecutionPersistenceTest(String locking) { 
        this.locking = PESSIMISTIC_LOCKING.equals(locking);
    }
    
    @After
    public void cleanUpPersistence() throws Exception {
        disposeKSession();
        cleanUp(context);
        context = null;
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KieBase kbase) {
        if( context == null ) { 
            context = DroolsPersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        }
        KieSessionConfiguration ksconf = RuleBaseFactory.newKnowledgeSessionConfiguration();
        Environment env = createEnvironment(context);
        if( this.locking ) { 
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        }
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, ksconf, env);    }  
    
}
