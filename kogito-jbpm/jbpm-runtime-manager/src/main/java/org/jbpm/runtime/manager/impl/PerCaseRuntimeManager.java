/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.runtime.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.drools.core.command.SingleSessionCommandService;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.event.AbstractEventSupport;
import org.drools.core.time.TimerService;
import org.drools.persistence.api.OrderedTransactionSynchronization;
import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerHelper;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.jbpm.runtime.manager.impl.factory.LocalTaskServiceFactory;
import org.jbpm.runtime.manager.impl.mapper.EnvironmentAwareProcessInstanceContext;
import org.jbpm.runtime.manager.impl.mapper.InMemoryMapper;
import org.jbpm.runtime.manager.impl.mapper.InternalMapper;
import org.jbpm.runtime.manager.impl.mapper.JPAMapper;
import org.jbpm.runtime.manager.impl.tx.DisposeSessionTransactionSynchronization;
import org.jbpm.services.task.impl.TaskContentRegistry;
import org.jbpm.services.task.impl.command.CommandBasedTaskService;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.Mapper;
import org.kie.internal.runtime.manager.SessionFactory;
import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.kie.internal.runtime.manager.context.CaseContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.InternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A RuntimeManager implementation that is backed by the "Per Case" strategy. This means that every 
 * process instance that belongs to same case will be bound to a single (case scoped) ksession for it's entire life time.  
 * Once started, whenever other operations are invoked,this manager will ensure that the correct ksession will be provided.
 * 
 * <br/>
 * This implementation supports the following <code>Context</code> implementations:
 * <ul>
 *  <li>ProcessInstanceIdContext</li>
 *  <li>CaseContext</li>
 * </ul>
 */
public class PerCaseRuntimeManager extends AbstractRuntimeManager {

    private static final Logger logger = LoggerFactory.getLogger(PerCaseRuntimeManager.class);
    
    private boolean useLocking = Boolean.parseBoolean(System.getProperty("org.jbpm.runtime.manager.pc.lock", "true"));

    private SessionFactory factory;
    private TaskServiceFactory taskServiceFactory;

    private static ThreadLocal<Map<Object, RuntimeEngine>> local = new ThreadLocal<Map<Object, RuntimeEngine>>();

    private Mapper mapper;

    private AbstractEventSupport<? extends EventListener> caseEventSupport;

    public PerCaseRuntimeManager(RuntimeEnvironment environment, SessionFactory factory, TaskServiceFactory taskServiceFactory, String identifier) {
        super(environment, identifier);
        this.factory = factory;
        this.taskServiceFactory = taskServiceFactory;
        this.mapper = ((org.kie.internal.runtime.manager.RuntimeEnvironment) environment).getMapper();
        this.registry.register(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RuntimeEngine getRuntimeEngine(Context<?> context) {
        if (isClosed()) {
            throw new IllegalStateException("Runtime manager " + identifier + " is already closed");
        }
        checkPermission();
        RuntimeEngine runtime = null;
        Object contextId = context.getContextId();

        if (!(context instanceof ProcessInstanceIdContext || context instanceof CaseContext)) {
            logger.warn("ProcessInstanceIdContext or CaseContext shall be used when interacting with PerCase runtime manager");
        }
        
        if (engineInitEager) {
            KieSession ksession = null;
            Long ksessionId = null;

            RuntimeEngine localRuntime = findLocalRuntime(contextId);
            if (localRuntime != null) {
                return localRuntime;
            }
            synchronized (this) {
                ksessionId = mapper.findMapping(context, this.identifier);
                if (ksessionId == null) {
                    ksession = factory.newKieSession();
                    ksessionId = ksession.getIdentifier();

                    if (context instanceof CaseContext) {
                        ksession.execute(new SaveMappingCommand(mapper, context, ksessionId, getIdentifier()));
                    }
                } else {
                    ksession = factory.findKieSessionById(ksessionId);
                }
            }
            InternalTaskService internalTaskService = newTaskService(taskServiceFactory);
            runtime = new RuntimeEngineImpl(ksession, internalTaskService);
            ((RuntimeEngineImpl) runtime).setManager(this);
            ((RuntimeEngineImpl) runtime).setContext(context);
            configureRuntimeOnTaskService(internalTaskService, runtime);
            registerDisposeCallback(runtime, new DisposeSessionTransactionSynchronization(this, runtime), ksession.getEnvironment());
            registerItems(runtime);
            attachManager(runtime);
            ksession.addEventListener(new MaintainMappingListener(ksessionId, runtime, this.identifier, (String) contextId));

            if (context instanceof CaseContext) {
                ksession.getEnvironment().set("CaseId", context.getContextId());
            } else {
                Object contexts = mapper.findContextId(ksession.getIdentifier(), this.identifier);
                if (contexts instanceof Collection) {
                    RuntimeEngine finalRuntimeEngnie = runtime;
                    KieSession finalKieSession = ksession;
                    ((Collection<Object>) contexts).forEach(o -> {
                        try {
                            
                            saveLocalRuntime(null, Long.parseLong(o.toString()), finalRuntimeEngnie);
                        } catch (NumberFormatException e) {
                            saveLocalRuntime(o.toString(), null, finalRuntimeEngnie);
                            finalKieSession.getEnvironment().set("CaseId", o.toString());
                        }
                    });                    
                }
            }
        } else {
            RuntimeEngine localRuntime = findLocalRuntime(contextId);
            if (localRuntime != null) {
                return localRuntime;
            }
            // lazy initialization of ksession and task service

            runtime = new RuntimeEngineImpl(context, new PerCaseInitializer());
            ((RuntimeEngineImpl) runtime).setManager(this);
        }
        String caseId = null;
        Long processInstanceId = null;

        if (context instanceof CaseContext) {
            caseId = (String) contextId;
        } else if (context instanceof ProcessInstanceIdContext) {
            processInstanceId = (Long) contextId;
        }
        Long ksessionId = mapper.findMapping(context, this.identifier);
        createLockOnGetEngine(ksessionId, runtime);
        saveLocalRuntime(caseId, processInstanceId, runtime);        

        return runtime;
    }

    @Override
    public void signalEvent(String type, Object event) {

        // first signal with new context in case there are start event with signal
        RuntimeEngine runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get());
        runtimeEngine.getKieSession().signalEvent(type, event);
        if (canDispose(runtimeEngine)) {
            disposeRuntimeEngine(runtimeEngine);
        }
        // next find out all instances waiting for given event type
        List<String> processInstances = ((InternalMapper) mapper).findContextIdForEvent(type, getIdentifier());
        for (String piId : processInstances) {
            runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get(Long.parseLong(piId)));
            runtimeEngine.getKieSession().signalEvent(type, event);
            if (canDispose(runtimeEngine)) {
                disposeRuntimeEngine(runtimeEngine);
            }
        }

        // process currently active runtime engines
        Map<Object, RuntimeEngine> currentlyActive = local.get();
        if (currentlyActive != null && !currentlyActive.isEmpty()) {
            RuntimeEngine[] activeEngines = currentlyActive.values().toArray(new RuntimeEngine[currentlyActive.size()]);
            for (RuntimeEngine engine : activeEngines) {
                Context<?> context = ((RuntimeEngineImpl) engine).getContext();
                if (context != null && context instanceof ProcessInstanceIdContext && ((ProcessInstanceIdContext) context).getContextId() != null) {
                    engine.getKieSession().signalEvent(type, event, ((ProcessInstanceIdContext) context).getContextId());
                }
            }
        }
    }

    @Override
    public void validate(KieSession ksession, Context<?> context) throws IllegalStateException {
        if (isClosed()) {
            throw new IllegalStateException("Runtime manager " + identifier + " is already closed");
        }
        if (context == null || context.getContextId() == null) {
            return;
        }
        Long ksessionId = mapper.findMapping(context, this.identifier);
        if (ksessionId != null && ksession.getIdentifier() != ksessionId) {
            throw new IllegalStateException("Invalid session was used for this context " + context);
        }
    }

    @Override
    public void disposeRuntimeEngine(RuntimeEngine runtime) {
        if (isClosed()) {
            throw new IllegalStateException("Runtime manager " + identifier + " is already closed");
        }
        try {
            if (canDispose(runtime)) {
                removeLocalRuntime(runtime);                
                
                Long ksessionId = ((RuntimeEngineImpl)runtime).getKieSessionId();
                releaseAndCleanLock(ksessionId, runtime);
                if (runtime instanceof Disposable) {
                    // special handling for in memory to not allow to dispose if there is any context in the mapper
                    if (mapper instanceof InMemoryMapper && ((InMemoryMapper) mapper).hasContext(ksessionId)) {
                        return;
                    }
                    ((Disposable) runtime).dispose();
                }
                if (ksessionId != null) {
                    TimerService timerService = TimerServiceRegistry.getInstance().get(getIdentifier() + TimerServiceRegistry.TIMER_SERVICE_SUFFIX);
                    if (timerService != null) {
                        if (timerService instanceof GlobalTimerService) {
                            ((GlobalTimerService) timerService).clearTimerJobInstances(ksessionId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            releaseAndCleanLock(runtime);
            removeLocalRuntime(runtime);           
            throw new RuntimeException(e);
        }            
    }

    @Override
    public void softDispose(RuntimeEngine runtimeEngine) {
        super.softDispose(runtimeEngine);
        removeLocalRuntime(runtimeEngine);
    }

    @Override
    public void close() {
        try {
            if (!(taskServiceFactory instanceof LocalTaskServiceFactory)) {
                // if it's CDI based (meaning single application scoped bean) we need to unregister context
                removeRuntimeFromTaskService();
            }
        } catch (Exception e) {
            // do nothing 
        }
        super.close();
        factory.close();
    }

    public boolean validate(Long ksessionId, Long processInstanceId) {
        Long mapped = this.mapper.findMapping(ProcessInstanceIdContext.get(processInstanceId), this.identifier);
        if (Objects.equals(mapped, ksessionId)) {
            return true;
        }

        return false;
    }

    private class MaintainMappingListener extends DefaultProcessEventListener {

        private Long ksessionId;
        private RuntimeEngine runtime;
        private String managerId;
        private String caseId;

        MaintainMappingListener(Long ksessionId, RuntimeEngine runtime, String managerId, String caseId) {
            this.ksessionId = ksessionId;
            this.runtime = runtime;
            this.managerId = managerId;
            this.caseId = caseId;

        }

        @Override
        public void afterProcessCompleted(ProcessCompletedEvent event) {
            mapper.removeMapping(new EnvironmentAwareProcessInstanceContext(event.getKieRuntime().getEnvironment(), event.getProcessInstance().getId()), managerId);
            removeLocalRuntime(runtime, event.getProcessInstance().getId());                       
        }

        @Override
        public void beforeProcessStarted(ProcessStartedEvent event) {
            mapper.saveMapping(new EnvironmentAwareProcessInstanceContext(event.getKieRuntime().getEnvironment(), event.getProcessInstance().getId()), ksessionId, managerId);
            saveLocalRuntime(caseId, event.getProcessInstance().getId(), runtime);
            ((RuntimeEngineImpl) runtime).setContext(ProcessInstanceIdContext.get(event.getProcessInstance().getId()));            
        }

    }

    public SessionFactory getFactory() {
        return factory;
    }

    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    public TaskServiceFactory getTaskServiceFactory() {
        return taskServiceFactory;
    }

    public void setTaskServiceFactory(TaskServiceFactory taskServiceFactory) {
        this.taskServiceFactory = taskServiceFactory;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    protected RuntimeEngine findLocalRuntime(Object caseId) {
        if (caseId == null) {
            return null;
        }
        Map<Object, RuntimeEngine> map = local.get();
        if (map == null) {
            return null;
        } else {
            RuntimeEngine engine = map.get(caseId);
            // check if engine is not already disposed as afterCompletion might be issued from another thread
            if (engine != null && ((RuntimeEngineImpl) engine).isDisposed()) {
                map.remove(caseId);
                return null;
            }

            return engine;
        }
    }

    protected void saveLocalRuntime(Object caseId, Object processInstanceId, RuntimeEngine runtime) {

        Map<Object, RuntimeEngine> map = local.get();
        if (map == null) {
            map = new HashMap<Object, RuntimeEngine>();
            local.set(map);
        }
        if (caseId != null) {
            map.put(caseId, runtime);
        }
        if (processInstanceId != null) {
            map.put(processInstanceId, runtime);
        }

    }

    protected void removeLocalRuntime(RuntimeEngine runtime) {
        Map<Object, RuntimeEngine> map = local.get();
        List<Object> keyToRemoves = new ArrayList<Object>();
        if (map != null) {
            for (Map.Entry<Object, RuntimeEngine> entry : map.entrySet()) {
                if (runtime.equals(entry.getValue())) {
                    keyToRemoves.add(entry.getKey());

                }
            }
            for (Object keyToRemove : keyToRemoves) {
                map.remove(keyToRemove);
            }
        }
    }

    protected void removeLocalRuntime(RuntimeEngine runtime, Long processInstanceId) {
        Map<Object, RuntimeEngine> map = local.get();
        List<Object> keyToRemoves = new ArrayList<Object>();
        if (map != null) {
            for (Map.Entry<Object, RuntimeEngine> entry : map.entrySet()) {
                if (processInstanceId.equals(entry.getKey())) {
                    keyToRemoves.add(entry.getKey());

                }
            }
            for (Object keyToRemove : keyToRemoves) {
                map.remove(keyToRemove);
            }
        }
    }

    @Override
    public void init() {

        TaskContentRegistry.get().addMarshallerContext(getIdentifier(), new ContentMarshallerContext(environment.getEnvironment(), environment.getClassLoader()));
        boolean owner = false;
        TransactionManager tm = null; 
        if (environment.usePersistence()){
            tm = getTransactionManagerInternal(environment.getEnvironment());
            owner = tm.begin();
        }
        try {
            // need to init one session to bootstrap all case - such as start timers
            KieSession initialKsession = factory.newKieSession();
            // there is a need to call getProcessRuntime otherwise the start listeners are not registered
            initialKsession.execute(new ExecutableCommand<Void>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Void execute(org.kie.api.runtime.Context context) {
                    KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
                    ((InternalKnowledgeRuntime) ksession).getProcessRuntime();
                    return null;
                }
            });
            factory.onDispose(initialKsession.getIdentifier());
            initialKsession.execute(new DestroyKSessionCommand(initialKsession, this));
    
            if (!"false".equalsIgnoreCase(System.getProperty("org.jbpm.rm.init.timer"))) {
                if (mapper instanceof JPAMapper) {
                    List<Long> ksessionsToInit = ((JPAMapper) mapper).findKSessionToInit(this.identifier);
                    for (Long id : ksessionsToInit) {
                        initialKsession = factory.findKieSessionById(id);
                        initialKsession.execute(new DisposeKSessionCommand(initialKsession, this));
                    }
                }
            }
            if (tm != null) {
                tm.commit(owner);
            }
        } catch (Exception e) {
            if (tm != null) {
                tm.rollback(owner);
            }
            throw new RuntimeException("Exception while initializing runtime manager " + this.identifier, e);
        }
    }
    
    @Override
    public void activate() {
        super.activate();
        // need to init one session to bootstrap all case - such as start timers
        KieSession initialKsession = factory.newKieSession();
        initialKsession.execute(new DestroyKSessionCommand(initialKsession, this));
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }

    public void destroyCase(CaseContext caseContext) {
        KieSession kieSession = null;
        RuntimeEngine localRuntime = findLocalRuntime(caseContext.getContextId());
        if (localRuntime != null) {
            kieSession = localRuntime.getKieSession();
        } else {
            Long ksessionId = mapper.findMapping(caseContext, this.identifier);
            if (ksessionId != null) {
                kieSession = factory.findKieSessionById(ksessionId);
            }
        }
        factory.onDispose(kieSession.getIdentifier());
        List<ExecutableCommand<?>> cmds = new ArrayList<>();
        RemoveMappingCommand removeMapping = new RemoveMappingCommand(mapper, caseContext, getIdentifier());
        cmds.add(removeMapping);
        DestroyKSessionCommand destroy = new DestroyKSessionCommand(kieSession, this);
        cmds.add(destroy);

        BatchExecutionCommand batchCmd = new BatchExecutionCommandImpl(cmds);
        kieSession.execute(batchCmd);
    }

    public AbstractEventSupport<? extends EventListener> getCaseEventSupport() {
        return caseEventSupport;
    }

    public void setCaseEventSupport(AbstractEventSupport<? extends EventListener> caseEventSupport) {
        this.caseEventSupport = caseEventSupport;
    }

    private static class DestroyKSessionCommand implements ExecutableCommand<Void> {

        private static final long serialVersionUID = 1L;

        private KieSession initialKsession;
        private AbstractRuntimeManager manager;

        public DestroyKSessionCommand(KieSession initialKsession, AbstractRuntimeManager manager) {
            this.initialKsession = initialKsession;
            this.manager = manager;
        }

        @Override
        public Void execute(org.kie.api.runtime.Context context) {
            TransactionManager tm = (TransactionManager) initialKsession.getEnvironment().get(EnvironmentName.TRANSACTION_MANAGER);
            if (manager.hasEnvironmentEntry("IS_JTA_TRANSACTION", false)) {
                if (initialKsession instanceof CommandBasedStatefulKnowledgeSession) {
                    ExecutableRunner commandService = ((CommandBasedStatefulKnowledgeSession) initialKsession).getRunner();
                    ((SingleSessionCommandService) commandService).destroy();
                } else {
                    ((RegistryContext) context).lookup( KieSession.class ).destroy();
                }
                return null;
            }

            if (tm != null && tm.getStatus() != TransactionManager.STATUS_NO_TRANSACTION && tm.getStatus() != TransactionManager.STATUS_ROLLEDBACK && tm.getStatus() != TransactionManager.STATUS_COMMITTED) {
                TransactionManagerHelper.registerTransactionSyncInContainer(tm, new OrderedTransactionSynchronization(5, "PCRM-" + initialKsession.getIdentifier()) {

                    @Override
                    public void beforeCompletion() {
                        if (initialKsession instanceof CommandBasedStatefulKnowledgeSession) {
                            ExecutableRunner commandService = ((CommandBasedStatefulKnowledgeSession) initialKsession).getRunner();
                            ((SingleSessionCommandService) commandService).destroy();
                        }
                    }

                    @Override
                    public void afterCompletion(int arg0) {
                        initialKsession.dispose();

                    }
                });
            } else {
                initialKsession.destroy();
            }
            return null;
        }
    }

    private static class DisposeKSessionCommand implements ExecutableCommand<Void> {

        private static final long serialVersionUID = 1L;

        private KieSession initialKsession;
        private AbstractRuntimeManager manager;

        public DisposeKSessionCommand(KieSession initialKsession, AbstractRuntimeManager manager) {
            this.initialKsession = initialKsession;
            this.manager = manager;
        }

        @Override
        public Void execute(org.kie.api.runtime.Context context) {

            if (manager.hasEnvironmentEntry("IS_JTA_TRANSACTION", false)) {
                initialKsession.dispose();
                return null;
            }
            TransactionManager tm = (TransactionManager) initialKsession.getEnvironment().get(EnvironmentName.TRANSACTION_MANAGER);
            if (tm != null && tm.getStatus() != TransactionManager.STATUS_NO_TRANSACTION && tm.getStatus() != TransactionManager.STATUS_ROLLEDBACK && tm.getStatus() != TransactionManager.STATUS_COMMITTED) {
                TransactionManagerHelper.registerTransactionSyncInContainer(tm, new OrderedTransactionSynchronization(5, "PPIRM-" + initialKsession.getIdentifier()) {

                    @Override
                    public void beforeCompletion() {
                    }

                    @Override
                    public void afterCompletion(int arg0) {
                        initialKsession.dispose();

                    }
                });
            } else {
                initialKsession.dispose();
            }
            return null;
        }
    }

    private static class SaveMappingCommand implements ExecutableCommand<Void> {

        private static final long serialVersionUID = 1L;

        private Mapper mapper;
        private Context<?> caseContext;
        private Long ksessionId;
        private String ownerId;

        public SaveMappingCommand(Mapper mapper, Context<?> caseContext, Long ksessionId, String ownerId) {
            this.mapper = mapper;
            this.caseContext = caseContext;
            this.ksessionId = ksessionId;
            this.ownerId = ownerId;
        }

        @Override
        public Void execute(org.kie.api.runtime.Context context) {
            mapper.saveMapping(caseContext, ksessionId, ownerId);

            return null;
        }
    }

    private static class RemoveMappingCommand implements ExecutableCommand<Void> {

        private static final long serialVersionUID = 1L;

        private Mapper mapper;
        private Context<?> caseContext;
        private String ownerId;

        public RemoveMappingCommand(Mapper mapper, Context<?> caseContext, String ownerId) {
            this.mapper = mapper;
            this.caseContext = caseContext;
            this.ownerId = ownerId;
        }

        @Override
        public Void execute(org.kie.api.runtime.Context context) {
            mapper.removeMapping(caseContext, ownerId);

            return null;
        }
    }

    private class PerCaseInitializer implements RuntimeEngineInitlializer {
        @SuppressWarnings("unchecked")
        @Override
        public KieSession initKieSession(Context<?> context, InternalRuntimeManager manager, RuntimeEngine engine) {

            Object contextId = context.getContextId();
            if (contextId == null) {
                contextId = manager.getIdentifier();
            }

            KieSession ksession = null;
            Long ksessionId = null;

            RuntimeEngine localRuntime = ((PerCaseRuntimeManager) manager).findLocalRuntime(contextId);
            if (localRuntime != null && ((RuntimeEngineImpl) engine).internalGetKieSession() != null) {
                return localRuntime.getKieSession();
            }
            synchronized (manager) {

                ksessionId = mapper.findMapping(context, manager.getIdentifier());
                if (ksessionId == null) {
                    ksession = factory.newKieSession();
                    ksessionId = ksession.getIdentifier();
                    if (context instanceof CaseContext) {
                        ksession.execute(new SaveMappingCommand(mapper, context, ksessionId, manager.getIdentifier()));
                    }
                } else {
                    ksession = factory.findKieSessionById(ksessionId);
                }
            }
            ((RuntimeEngineImpl) engine).internalSetKieSession(ksession);
            registerItems(engine);
            attachManager(engine);
            registerDisposeCallback(engine, new DisposeSessionTransactionSynchronization(manager, engine), ksession.getEnvironment());
            ksession.addEventListener(new MaintainMappingListener(ksessionId, engine, manager.getIdentifier(), contextId.toString()));

            if (context instanceof CaseContext) {
                ksession.getEnvironment().set("CaseId", context.getContextId());
            } else {
                Object contexts = mapper.findContextId(ksession.getIdentifier(), manager.getIdentifier());
                if (contexts instanceof Collection) {
                    KieSession finalKieSession = ksession;
                    ((Collection<Object>) contexts).forEach(o -> {
                        try {
                            
                            saveLocalRuntime(null, Long.parseLong(o.toString()), engine);
                        } catch (NumberFormatException e) {
                            saveLocalRuntime(o.toString(), null, engine);
                            finalKieSession.getEnvironment().set("CaseId", o.toString());
                        }
                    });                    
                }
            }

            return ksession;
        }

        @Override
        public TaskService initTaskService(Context<?> context, InternalRuntimeManager manager, RuntimeEngine engine) {
            InternalTaskService internalTaskService = newTaskService(taskServiceFactory);
            if (internalTaskService != null) {
                registerDisposeCallback(engine, new DisposeSessionTransactionSynchronization(manager, engine), ((CommandBasedTaskService) internalTaskService).getEnvironment());
                configureRuntimeOnTaskService(internalTaskService, engine);
            }
            return internalTaskService;
        }

    }

    @Override
    protected boolean isUseLocking() {
        return useLocking;
    }

    @Override
    protected void registerItems(RuntimeEngine runtime) {
        super.registerItems(runtime);
        if (getCaseEventSupport() != null) {
            // add any process listeners from case event listeners
            List<? extends EventListener> eventListener = getCaseEventSupport().getEventListeners();
            for (EventListener listener : eventListener) {
                if (listener instanceof ProcessEventListener) {
                    runtime.getKieSession().addEventListener((ProcessEventListener) listener);
                }
            }
        }
    }

}
