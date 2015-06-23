/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.bpmn2.concurrency.persistence;

import static org.jbpm.persistence.util.PersistenceUtil.*;

import java.util.HashMap;

import org.jbpm.bpmn2.concurrency.OneProcessPerThreadTest;
import org.jbpm.persistence.util.PersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.kie.api.runtime.Environment;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;

/**
 * Class to reproduce bug with multiple threads using persistence and each
 * configures its own entity manager.
 * 
 * This test takes time and resources, please only run it locally
 */
@Ignore
public class OneProcessPerThreadPersistenceTest extends OneProcessPerThreadTest {

    private static HashMap<String, Object> context;

    @Before
    public void setup() {
        context = setupWithPoolingDataSource(PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME);
    }

    @After
    public void tearDown() throws Exception {
        BitronixTransactionManager txm = TransactionManagerServices.getTransactionManager();
        assertTrue("There is still a transaction running!", txm.getCurrentTransaction() == null );
        
        cleanUp(context);
    }

    protected StatefulKnowledgeSession createStatefulKnowledgeSession(KnowledgeBase kbase) {
        Environment env = createEnvironment(context);
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
    }

}
