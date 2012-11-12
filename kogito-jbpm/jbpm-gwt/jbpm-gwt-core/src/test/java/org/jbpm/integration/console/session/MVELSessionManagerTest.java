/**
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
package org.jbpm.integration.console.session;

import org.jbpm.integration.JbpmGwtCoreTestCase;
import org.junit.Test;
import org.kie.KnowledgeBaseFactory;
import org.kie.runtime.StatefulKnowledgeSession;

public class MVELSessionManagerTest extends JbpmGwtCoreTestCase {

    @Test
    public void testLoadDefaultSessionTemplate() {
        MVELSingleSessionManager manager = new MVELSingleSessionManager(null);
        
        SessionTemplate template = manager.loadSessionTemplate();
        assertNotNull(template);
        assertEquals("jbpmConsole", template.getBusinessKey());
        assertEquals("org.jbpm.persistence.jpa", template.getPersistenceUnit());
        assertFalse(template.isImported());
        assertEquals(2, template.getProperties().size());
        assertEquals(2, template.getWorkItemHandlers().size());
        assertEquals(2, template.getEventListeners().size());
    }
    
    @Test
    public void testLoadDefaultSessionFromTemplate() {
        MVELSingleSessionManager manager = new MVELSingleSessionManager(KnowledgeBaseFactory.newKnowledgeBase());
        
        SessionTemplate template = manager.loadSessionTemplate();
        assertNotNull(template);
        assertNotNull(template);
        assertEquals("jbpmConsole", template.getBusinessKey());
        assertEquals("org.jbpm.persistence.jpa", template.getPersistenceUnit());
        assertFalse(template.isImported());
        assertEquals(2, template.getProperties().size());
        assertEquals(2, template.getWorkItemHandlers().size());
        assertEquals(2, template.getEventListeners().size());
        
        StatefulKnowledgeSession session = manager.loadSessionFromTemplate(template, KnowledgeBaseFactory.newKnowledgeBase());
        assertNotNull(session);
        assertEquals(2, session.getAgendaEventListeners().size());
        assertEquals(1, session.getProcessEventListeners().size());
        assertEquals(0, session.getWorkingMemoryEventListeners().size());
    }
    
    @Test
    public void testGetSession() {
        SessionManager manager = new MVELSingleSessionManager(KnowledgeBaseFactory.newKnowledgeBase());
        
        StatefulKnowledgeSession session = manager.getSession();
        assertNotNull(session);
        assertEquals(2, session.getAgendaEventListeners().size());
        assertEquals(1, session.getProcessEventListeners().size());
        assertEquals(0, session.getWorkingMemoryEventListeners().size());
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void testGetSessionByBusinessKey() {
        SessionManager manager = new MVELSingleSessionManager(KnowledgeBaseFactory.newKnowledgeBase());
        
        manager.getSession("businesskey");
    }
}
