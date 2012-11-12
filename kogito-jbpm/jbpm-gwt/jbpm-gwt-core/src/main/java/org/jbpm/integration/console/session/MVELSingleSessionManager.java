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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.audit.WorkingMemoryLogger;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.jbpm.integration.console.HumanTaskService;
import org.jbpm.integration.console.TaskClientFactory;
import org.jbpm.integration.console.Utils;
import org.jbpm.integration.console.shared.PropertyLoader;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.service.local.LocalTaskService;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.rule.AgendaEventListener;
import org.kie.event.rule.WorkingMemoryEventListener;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItemHandler;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default (MVEL) based implementation of <code>SessionManager</code> that relies on MVEL file aka SessionTemplate.
 * Session template can be given in two ways:
 * <ul>
 *  <li>from classpath - default.session.template that is shipped with jbpm console</li>
 *  <li>from file system - session.template that is custom defined and location (directory) is given 
 *  as system property jbpm.conf.dir that if not given defaults to value fo another system property jboss.server.config.dir</li>
 * </ul>
 *
 */
public class MVELSingleSessionManager extends AbstractSessionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(MVELSingleSessionManager.class);
    private static final String defaultSessionTemplate = "/default.session.template";
    
    private StatefulKnowledgeSession session = null;
    private KnowledgeBase kbase;

    private SessionTemplate template;
    
    public MVELSingleSessionManager(KnowledgeBase kbase) {
        this.kbase = kbase;
    }

    protected SessionTemplate loadSessionTemplate() {
        try {
            InputStream templateFile = PropertyLoader.getStreamForConfigFile("/session.template", defaultSessionTemplate);
            
            Reader reader = new InputStreamReader(templateFile);
            HashMap<String, Object> variables = new HashMap<String, Object>();
            
            SessionTemplate template = (SessionTemplate) eval(toString(reader), variables);
            
            return template;
        } catch (IOException e) {
            logger.error("Error loading session template file", e);
        }
        return null;
    }
    
    protected StatefulKnowledgeSession loadSessionFromTemplate(SessionTemplate template, KnowledgeBase kbase) {
        
        if (template.isImported()) {
            return lookUpInJNDI(template.getBusinessKey());
        }
        
        HashMap<String, Object> variables = new HashMap<String, Object>();
        Properties consoleProperties = PropertyLoader.getJbpmConsoleProperties();
        // try to restore known session id for reuse
        int ksessionId = getPersistedSessionId(consoleProperties.getProperty("jbpm.console.tmp.dir", System.getProperty("jboss.server.temp.dir")));
        
        StatefulKnowledgeSession ksession = null;
        
        // Set up persistence
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(template.getPersistenceUnit());
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        
        // process environment entries
        Map<String, String> environmentEntires = template.getEnvironmentEntries();
        if (environmentEntires != null) {
            for (Entry<String, String> entry : environmentEntires.entrySet()) {
                try {
                    Object value = eval(entry.getValue(), variables);
                    env.set(entry.getKey(), value);
                } catch (Exception e) {
                    logger.warn("Error while seting envirnment entry " + entry.getKey(), e);
                }
            }
        }
        
        // setup session properties
        Properties sessionconfigproperties = new Properties();
        
        Map<String, String> properties = template.getProperties();
        if (properties != null) {
            Iterator<String> propertyNames = properties.keySet().iterator();
            while (propertyNames.hasNext()) {
                String propertyName = (String) propertyNames.next();
                
                sessionconfigproperties.setProperty(propertyName, properties.get(propertyName));
            }
        }
        KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(sessionconfigproperties);
        
        // try to load session if id is given as part of template
        if (ksessionId > 0) {
            try {
                ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase, config, env);
            } catch (Exception e) {
                logger.warn("Loading of knowledge session with id " + ksessionId + " failed due to " + e.getMessage() + " new session will be created, e");
                ksession = null;
            }
        } 
        
        // if session is still null create new one
        if (ksession == null) {
            ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
            ksessionId = ksession.getId();
            persistSessionId(consoleProperties.getProperty("jbpm.console.tmp.dir", System.getProperty("jboss.server.temp.dir")), ksessionId);
        }
        // set ksession as variable
        variables.put("ksession", ksession);
        
        try {
            // prepare task client based on jbpm.console.properties
            org.jbpm.task.TaskService syncTaskService = null;
            AsyncTaskService asyncTaskService = null;
            
            if ("Local".equalsIgnoreCase(consoleProperties.getProperty("jbpm.console.task.service.strategy", Utils.DEFAULT_TASK_SERVICE_STRATEGY))) {
                syncTaskService = new LocalTaskService(HumanTaskService.getService());
                // set task client as variable
                variables.put("taskClient", syncTaskService);
            } else  {
                asyncTaskService = TaskClientFactory.newAsyncInstance(consoleProperties, "taskClient:"+template.getBusinessKey(), false);
                // set task client as variable
                variables.put("taskClient", asyncTaskService);
            }
        } catch (Exception e) {
            logger.error("Error when building task client, disabling task service interaction", e);
            variables.put("taskClient", null);
        }
        
        // register work item handlers
        processWorkItemHandlers(ksession, variables, consoleProperties, template);
        
        // add event listeners
        processEventListeners(ksession, variables, template);
        
        return ksession;
    }
    
    protected void processWorkItemHandlers(StatefulKnowledgeSession ksession, Map<String, Object> variables, Properties consoleProperties, SessionTemplate template) {
        Map<String, String> handlers = template.getWorkItemHandlers();
        if (handlers != null) {
            
            Iterator<String> workItemNames = handlers.keySet().iterator();
            while (workItemNames.hasNext()) {
                String workItemName = (String) workItemNames.next();
                
                String handlerDef = handlers.get(workItemName);
                try {
                    WorkItemHandler handler = (WorkItemHandler) eval(handlerDef, variables);
                    if (workItemName.equalsIgnoreCase("Human Task")) {
                        invokeMethod("setIpAddress", handler, new Class[]{String.class}, Utils.getTaskServiceHost(consoleProperties));
                        invokeMethod("setPort", handler, new Class[]{int.class}, Utils.getTaskServicePort(consoleProperties));
                        invokeMethod("connect", handler, new Class[0], new Object[0]);
                    }
                    ksession.getWorkItemManager().registerWorkItemHandler(workItemName, handler);
                } catch (Exception e) {
                    logger.error("Registration of work item handler " + workItemName + " failed due to " + e.getMessage(), e);
                }
            }
        }
    }
    
    protected void processEventListeners(StatefulKnowledgeSession ksession, Map<String, Object> variables, SessionTemplate template) {
        List<String> eventListeners = template.getEventListeners();
        if (eventListeners != null) {
            for (String eventListenerDef : eventListeners) {
                try {
                    EventListener listener = (EventListener) eval(eventListenerDef, variables);
                    if (listener instanceof WorkingMemoryLogger) {
                        // it is enough to create instance of it as it will register all the listeners internally
                    } else if (listener instanceof ProcessEventListener) {
                        ksession.addEventListener((ProcessEventListener) listener);
                        
                    } else if (listener instanceof AgendaEventListener) {
                        ksession.addEventListener((AgendaEventListener) listener);
                        
                    } else if (listener instanceof WorkingMemoryEventListener) {
                        ksession.addEventListener((WorkingMemoryEventListener) listener);
                        
                    } else if (listener instanceof org.drools.event.AgendaEventListener) {
                        ((StatefulKnowledgeSessionImpl)  ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                                .getCommandService().getContext()).getStatefulKnowledgesession() )
                                .session.addEventListener((org.drools.event.AgendaEventListener)listener);
                    }
                } catch (Exception e) {
                    logger.error("Addition of event listener " + eventListenerDef + " failed due to " + e.getMessage(), e);
                }
            }
        }
    }
    
    protected String toString(Reader reader) throws IOException {
        int charValue  ;
        StringBuffer sb = new StringBuffer(1024);
        while ((charValue = reader.read()) != -1) {
            sb.append((char) charValue);
        }
        return sb.toString();
    }
    
    protected Object eval(String str, Map<String, Object> vars) {
        ParserConfiguration pconf = new ParserConfiguration();
        pconf.addPackageImport("org.jbpm.integration.console.session");
        pconf.addPackageImport("java.util");
        pconf.addImport("SessionTemplate", SessionTemplate.class); 
        

        ParserContext context = new ParserContext(pconf);
        Serializable s = MVEL.compileExpression(str.trim(), context);

        if (vars != null) {
            return MVEL.executeExpression(s, vars);
        } else {
            return MVEL.executeExpression(s);
        }
    }
    
    @SuppressWarnings("rawtypes")
    private void invokeMethod(String methodName, Object object, Class[] classes, Object...objects) {
        try {
            Method method = object.getClass().getMethod(methodName, classes);
            method.invoke(object, objects);
        } catch (NoSuchMethodException e) {
            //do nothing
        } catch (Exception e) {
            logger.warn("Error while invoking method " + methodName + " on " + object, e);
        }
    }

    public void disposeSession(StatefulKnowledgeSession session) {
       
       
       if (this.template != null && !this.template.isImported()) {
           removeFromJNDI(template.getBusinessKey());
           session.dispose();
       }
       
       this.session = null;
       
    }

    public StatefulKnowledgeSession getSession() {
        if (this.session == null) {
            this.template = loadSessionTemplate();
            this.session = loadSessionFromTemplate(template, kbase);
            try {
                // since all was just registered fire all rules
                this.session.fireAllRules();
            } catch (Exception e) {
               logger.error("Error when invoking fireAllRules on session initialization", e);
            }
        }
        return this.session;
    }

    public StatefulKnowledgeSession getSession(String businessKey) {
        throw new UnsupportedOperationException("This manager does not support business keys for session");
    }

    public StatefulKnowledgeSession getSession(int sessionId) {
        if (this.session != null && sessionId == this.session.getId()) {
            return this.session;
        }
        return null;
    }
    
    
}
