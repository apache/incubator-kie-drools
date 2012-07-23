/**
 * Copyright 2011 JBoss Inc
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
package org.jbpm.integration.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.WorkingMemory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.ProcessBuilderFactory;
import org.drools.core.util.StringUtils;
import org.drools.definition.KnowledgePackage;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.event.RuleFlowGroupDeactivatedEvent;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.marshalling.impl.ProcessMarshallerFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.integration.console.shared.GuvnorConnectionUtils;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.process.workitem.bpmn2.ServiceTaskHandler;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.jbpm.process.workitem.wsht.SyncWSHumanTaskHandler;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.local.LocalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This takes care of the (stateful knowledge) session initialization and holder logic 
 * for the {@link CommandDelegate} class. 
 * </p>
 * The class is designed to work as a static instance.
 * </p>
 * Lastly, parts of the drools/jbpm infrastructure need a Session instance to exist in order for certain
 * things, like timer job events, to be able to occur. (This may/hopefully will change in the future). This is
 * why we keep one static instance of a ksession open and available all the time. 
 */
public class StatefulKnowledgeSessionUtil {

    private static final Logger logger = LoggerFactory.getLogger(StatefulKnowledgeSessionUtil.class);
    
    private static int ksessionId = 0;
    private static Properties _jbpmConsoleProperties = new Properties();
    private static KnowledgeAgent kagent;
    private static Set<String> knownPackages;
   
    protected StatefulKnowledgeSessionUtil() {
    }
   
    @Override
    protected void finalize() throws Throwable { 
        dispose();
    }
    
    protected void dispose() { 
       SessionHolder.statefulKnowledgeSession.dispose(); 
       _jbpmConsoleProperties = null;
       SessionHolder.statefulKnowledgeSession = null;
    }
   
    /**
     * The following two methods illustrate the "Value Holder" design pattern, also known
     * as the "Lazy Initialization Holder" class idiom. <br\>
     * See http://en.wikipedia.org/wiki/Lazy_loading#Value_holder
     * </p> 
     * In the post jdk 1.4 world, this is the correct way to implement lazy initialization in 
     * a multi-threaded environment. 
     * </p>
     * Double-Checked-Locking is an antipattern!
     * </p>
     * See pp. 346-349 of "Java Concurrency in Practice" (B. Goetz) for more info. 
     */
    private static class SessionHolder { 
        public static StatefulKnowledgeSession statefulKnowledgeSession = initializeStatefulKnowledgeSession();
    }
    
    public static StatefulKnowledgeSession getStatefulKnowledgeSession() { 
        return SessionHolder.statefulKnowledgeSession;
    }
    
    /**
     * This method is meant to run within <b>1</b> thread (as is all logic in this class). 
     * </p>
     * @return
     */
    protected static StatefulKnowledgeSession initializeStatefulKnowledgeSession() {
        try {
            // Prepare knowledge base to create the knowledge session
            Properties jbpmConsoleProperties = getJbpmConsoleProperties();
            knownPackages = new CopyOnWriteArraySet<String>();
            KnowledgeBase localKBase = loadKnowledgeBase();
            addProcessesFromConsoleDirectory(localKBase, jbpmConsoleProperties);
            
            for (KnowledgePackage pkg : localKBase.getKnowledgePackages()) {
                knownPackages.add(pkg.getName());
            }
            
            // try to restore known session id for reuse
            ksessionId = getPersistedSessionId(jbpmConsoleProperties.getProperty("jbpm.console.tmp.dir", System.getProperty("jboss.server.temp.dir")));
            // Create knowledge session
            StatefulKnowledgeSession localKSession = createOrLoadStatefulKnowledgeSession(localKBase);
            persistSessionId(jbpmConsoleProperties.getProperty("jbpm.console.tmp.dir", System.getProperty("jboss.server.temp.dir")));
            // Additional necessary modifications to the knowledge session
            new JPAWorkingMemoryDbLogger(localKSession);
            registerWorkItemHandler(localKSession, jbpmConsoleProperties);
            addAgendaEventListener(localKSession);

            return localKSession;
        } catch (Throwable t) {
            throw new RuntimeException( "Could not initialize stateful knowledge session: " + t.getMessage(), t);
        }
    }

    /**
     * This method loads the jbpm console properties, if they haven't been loaded already. 
     */
    protected static Properties getJbpmConsoleProperties() { 
        if( ! _jbpmConsoleProperties.isEmpty() ) { 
            return _jbpmConsoleProperties;
        }
        try {
            _jbpmConsoleProperties.load(StatefulKnowledgeSessionUtil.class.getResourceAsStream("/jbpm.console.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Could not load jbpm.console.properties", e);
        }
        
        return _jbpmConsoleProperties;
    }
    
    /**
     * This method creates and fills the knowledge base, using a local guvnor instance if possible. 
     * @return a knowledge base. 
     */
    private static KnowledgeBase loadKnowledgeBase() { 
        KnowledgeBase kbase = null;
        GuvnorConnectionUtils guvnorUtils = new GuvnorConnectionUtils();
        if(guvnorUtils.guvnorExists()) {
            try {
                ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
                sconf.setProperty( "drools.resource.scanner.interval", "10" );
                ResourceFactory.getResourceChangeScannerService().configure( sconf );
                ResourceFactory.getResourceChangeScannerService().start();
                ResourceFactory.getResourceChangeNotifierService().start();
                KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
                aconf.setProperty("drools.agent.newInstance", "false");
                kagent = KnowledgeAgentFactory.newKnowledgeAgent("Guvnor default", aconf);
                kagent.applyChangeSet(ResourceFactory.newReaderResource(guvnorUtils.createChangeSet()));
                kbase = kagent.getKnowledgeBase();
            } catch (Throwable t) {
                logger.error("Could not load processes from Guvnor: " + t.getMessage(), t);
            }
        } else {
            logger.warn("Could not connect to Guvnor.");
        }

        // Create a kbase if we couldn't do that with Guvnor
        if (kbase == null) {
            kbase = KnowledgeBaseFactory.newKnowledgeBase();
        }

        return kbase;
    }
    
    /**
     * This method adds process from the console directory to the knowledge base
     * @param kbase the knowledge base
     */
    private static void addProcessesFromConsoleDirectory(KnowledgeBase kbase, Properties consoleProperties) { 
        String directory = System.getProperty("jbpm.console.directory") == null ? consoleProperties.getProperty("jbpm.console.directory") : System.getProperty("jbpm.console.directory");
        if (directory == null || directory.length() < 1 ) {
            logger.info("jbpm.console.directory property not found - processes from local file system will not be loaded");
        } else {
            File file = new File(directory);
            if (!file.exists()) {
                throw new IllegalArgumentException("Could not find " + directory);
            }
            if (!file.isDirectory()) {
                throw new IllegalArgumentException(directory + " is not a directory");
            }
            ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
            ProcessMarshallerFactory.setProcessMarshallerFactoryService(new ProcessMarshallerFactoryServiceImpl());
            ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
            BPMN2ProcessFactory.setBPMN2ProcessProvider(new BPMN2ProcessProviderImpl());
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            for (File subfile: file.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".bpmn") || name.endsWith("bpmn2");
                }})) {
                logger.info("Loading process from file system: " + subfile.getName());
                kbuilder.add(ResourceFactory.newFileResource(subfile), ResourceType.BPMN2);
            }
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        }
    }

    /**
     * This method will try to create a new stateful knowledge session or otherwise load it, depending
     * on the values of the {@link StatefulKnowledgeSessionUtil#ksessionId} value and whether or not
     * loading the session succeeds. 
     * </p>
     * This method has <i>purposefully</i> been made static <i>and</i> synchronized, because it modifies
     * the static (volatile) int ksessionId. Making the method static synchronized ensures that no 
     * race conditions will occur when checking or modifying the ksessionId value. 
     * 
     * @param kbase The knowledge base used to load the stateful knowledge session.
     * @return The stateful knowledge session. 
     */
    private static synchronized StatefulKnowledgeSession createOrLoadStatefulKnowledgeSession(KnowledgeBase kbase) { 
        // Set up persistence
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        
        // Set up jbpm process instance settings
        Properties sessionconfigproperties = new Properties();
        sessionconfigproperties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
        sessionconfigproperties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
        KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(sessionconfigproperties);

        boolean createNewKnowledgeSession = true;
        StatefulKnowledgeSession ksession = null;
        
        // Create or load knowledge session
        if (ksessionId > 0) { 
            createNewKnowledgeSession = false;
            try {
                logger.debug("Loading knowledge session with id " + ksessionId );
                ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase, config, env);
            } catch (RuntimeException e) {
                e.printStackTrace();
                logger.error("Error loading knowledge session : " + e.getMessage());
                if (e instanceof IllegalStateException) {
                    Throwable cause = ((IllegalStateException) e).getCause();
                    if (cause instanceof InvocationTargetException) {
                        cause = cause.getCause();
                        String exceptionMsg = "Could not find session data for id " + ksessionId;
                        if (cause != null && exceptionMsg.equals(cause.getMessage())) {
                            createNewKnowledgeSession = true;
                        } 
                    } 
                } 

                if (! createNewKnowledgeSession) { 
                    String exceptionMsg = e.getMessage();                    
                    if( e.getCause() != null && ! StringUtils.isEmpty(e.getCause().getMessage()) ) { 
                        exceptionMsg = e.getCause().getMessage();
                    }
                    logger.error("Error loading session data: " + exceptionMsg );
                    throw e;
                }
            }
        }

        if( createNewKnowledgeSession ) { 
            env = KnowledgeBaseFactory.newEnvironment();
            env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
            ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
            ksessionId = ksession.getId();
            logger.debug("Created new knowledge session with id " + ksessionId); 
        }

        return ksession;
    }
    
    /**
     * This method registers a work item handler in the work item manager attached to the given knowledge session .
     * @param ksession The (stateful) knowledge session .
     */
    private static void registerWorkItemHandler( StatefulKnowledgeSession ksession, Properties consoleProperties ) { 
        if ("Local".equalsIgnoreCase(consoleProperties.getProperty("jbpm.console.task.service.strategy", TaskClientFactory.DEFAULT_TASK_SERVICE_STRATEGY))) {
            TaskService taskService = HumanTaskService.getService();
            SyncWSHumanTaskHandler handler = new SyncWSHumanTaskHandler(new LocalTaskService(taskService), ksession);
            ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        } else  {
            CommandBasedWSHumanTaskHandler handler = new CommandBasedWSHumanTaskHandler(ksession);
            TaskClient client = TaskClientFactory.newAsyncInstance(consoleProperties, "org.drools.process.workitem.wsht.CommandBasedWSHumanTaskHandler");
            
            handler.configureClient(client);
            ksession.getWorkItemManager().registerWorkItemHandler( "Human Task", handler);
            handler.connect();
        }
        ksession.getWorkItemManager().registerWorkItemHandler( "Service Task", new ServiceTaskHandler(ksession));
    }
    
    /**
     * This method attaches an agenda event listener to the given knowledge session .
     * @param ksession The (stateful) knowledge session .
     */
    private static void addAgendaEventListener(final StatefulKnowledgeSession ksession) { 
        final org.drools.event.AgendaEventListener agendaEventListener = new org.drools.event.AgendaEventListener() {
            public void activationCreated(ActivationCreatedEvent event, WorkingMemory workingMemory){
            	ksession.fireAllRules();
            }
            public void activationCancelled(ActivationCancelledEvent event, WorkingMemory workingMemory){
            }
            public void beforeActivationFired(BeforeActivationFiredEvent event, WorkingMemory workingMemory) {
            }
            public void afterActivationFired(AfterActivationFiredEvent event, WorkingMemory workingMemory) {
            }
            public void agendaGroupPopped(AgendaGroupPoppedEvent event, WorkingMemory workingMemory) {
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event, WorkingMemory workingMemory) {
            }
            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event, WorkingMemory workingMemory) {
            }
            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event, WorkingMemory workingMemory) {
                workingMemory.fireAllRules();
            }
            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
            }
            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
            }
        };
        ((StatefulKnowledgeSessionImpl)  ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getStatefulKnowledgesession() )
                .session.addEventListener(agendaEventListener);
    	ksession.fireAllRules();
    }
    
    private static int getPersistedSessionId(String location) {
        File sessionIdStore = new File(location + File.separator + "jbpmSessionId.ser");
        if (sessionIdStore.exists()) {
            Integer knownSessionId = null; 
            FileInputStream fis = null;
            ObjectInputStream in = null;
            try {
                fis = new FileInputStream(sessionIdStore);
                in = new ObjectInputStream(fis);
                
                knownSessionId = (Integer) in.readObject();
                
                return knownSessionId.intValue();
                
            } catch (Exception e) {
                return 0;
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            }
            
        } else {
            return 0;
        }
    }
    
    private static void persistSessionId(String location) {
        if (location == null) {
            return;
        }
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(location + File.separator + "jbpmSessionId.ser");
            out = new ObjectOutputStream(fos);
            out.writeObject(Integer.valueOf(ksessionId));
            out.close();
        } catch (IOException ex) {
            logger.warn("Error when persisting known session id", ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    public static synchronized void checkPackagesFromGuvnor() {
        try {
            GuvnorConnectionUtils guvnorUtils = new GuvnorConnectionUtils();
            if(guvnorUtils.guvnorExists() && kagent != null) {
                List<String> guvnorPackages = guvnorUtils.getBuiltPackageNames();
                
                guvnorPackages.removeAll(knownPackages);
                
                if (guvnorPackages.size() > 0) {
                    kagent.applyChangeSet(ResourceFactory.newReaderResource(guvnorUtils.createChangeSet(guvnorPackages)));
                    knownPackages.addAll(guvnorPackages);
                }
            } 
        } catch (Exception e) {
            logger.error("Error while checking packages from Guvnor", e);
        }
    }
    
    public static KnowledgeAgent getKagent() {
        return kagent;
    }
}
