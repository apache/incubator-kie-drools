package org.jbpm.runtime.manager.impl;

import org.jbpm.task.api.TaskServiceEntryPoint;
import org.kie.event.process.DefaultProcessEventListener;
import org.kie.event.process.ProcessCompletedEvent;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.runtime.KieSession;
import org.kie.runtime.manager.Context;
import org.kie.runtime.manager.Disposable;
import org.kie.runtime.manager.Mapper;
import org.kie.runtime.manager.RuntimeEnvironment;
import org.kie.runtime.manager.SessionFactory;
import org.kie.runtime.manager.SessionNotFoundException;
import org.kie.runtime.manager.TaskServiceFactory;
import org.kie.runtime.manager.context.ProcessInstanceIdContext;

public class PerProcessInstanceRuntimeManager extends AbstractRuntimeManager {

    private SessionFactory factory;
    private TaskServiceFactory<TaskServiceEntryPoint> taskServiceFactory;
    
    private Mapper mapper;
    
    public PerProcessInstanceRuntimeManager(RuntimeEnvironment environment, SessionFactory factory, TaskServiceFactory taskServiceFactory) {
        super(environment);
        this.factory = factory;
        this.taskServiceFactory = taskServiceFactory;
        this.mapper = environment.getMapper();
    }
    
    @Override
    public org.kie.runtime.manager.Runtime getRuntime(Context context) {
  
        Object contextId = context.getContextId();
        KieSession ksession = null;
        Integer ksessionId = null;
        if (contextId == null) {            
            ksession = factory.newKieSession();
            ksessionId = ksession.getId();                 
        } else {
            ksessionId = mapper.findMapping(context);
            if (ksessionId == null) {
                throw new SessionNotFoundException("No session found for context " + context);
            }
            ksession = factory.findKieSessionById(ksessionId);
        }
        
        org.kie.runtime.manager.Runtime runtime = new RuntimeImpl(ksession, taskServiceFactory.newTaskService());
        registerDisposeCallback(runtime);
        registerItems(runtime);
        
        ksession.addEventListener(new MaintainMappingListener(ksessionId));
        return runtime;
    }

    @Override
    public void disposeRuntime(org.kie.runtime.manager.Runtime runtime) {
        if (runtime instanceof Disposable) {
            ((Disposable) runtime).dispose();
        }
    }

    @Override
    public void close() {
        factory.close();
    }

    
    public boolean validate(Integer ksessionId, Long processInstanceId) {
        Integer mapped = this.mapper.findMapping(ProcessInstanceIdContext.get(processInstanceId));
        if (mapped == ksessionId) {
            return true;
        }
        
        return false;
    }


    private class MaintainMappingListener extends DefaultProcessEventListener {

        private Integer ksessionId;
        
        MaintainMappingListener(Integer ksessionId) {
            this.ksessionId = ksessionId;
        }
        @Override
        public void afterProcessCompleted(ProcessCompletedEvent event) {
            mapper.removeMapping(ProcessInstanceIdContext.get(event.getProcessInstance().getId()));
        }

        @Override
        public void beforeProcessStarted(ProcessStartedEvent event) {
            mapper.saveMapping(ProcessInstanceIdContext.get(event.getProcessInstance().getId()), ksessionId);                    
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
}
