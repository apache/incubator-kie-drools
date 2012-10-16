/*
 * Copyright 2012 JBoss Inc
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
package org.drools.core.workitem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.impl.EnvironmentFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.junit.Test;

public class CustomWorkItemHandlerTest {

    @Test
    public void testRegisterHandlerWithKsessionUsingConfiguration() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        Properties props = new Properties();
        props.setProperty("drools.workItemHandlers", "CustomWorkItemHandlers.conf");
        KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(props);
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(config, EnvironmentFactory.newEnvironment());
        assertNotNull(ksession);
        // this test would fail on creation of the work item manager if injecting session is not supported
        WorkItemManager manager = ksession.getWorkItemManager();
        assertNotNull(manager);
        
        Map<String, WorkItemHandler> handlers = ((SessionConfiguration)config).getWorkItemHandlers();
        assertNotNull(handlers);
        assertEquals(1, handlers.size());
        assertTrue(handlers.containsKey("Custom"));
    }

}
