package org.jbpm.runtime.manager.impl;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.manager.Context;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.Mapper;
import org.kie.internal.runtime.manager.Runtime;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.SessionFactory;
import org.kie.internal.runtime.manager.SessionNotFoundException;
import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public class PerProcessInstanceRuntimeManager extends AbstractRuntimeManager {

    private SessionFactory factory;
    private TaskServiceFactory taskServiceFactory;
    
    private static ThreadLocal<Map<Object, org.kie.internal.runtime.manager.Runtime>> local = new ThreadLocal<Map<Object, org.kie.internal.runtime.manager.Runtime>>();
    
    private Mapper mapper;
    
    public PerProcessInstanceRuntimeManager(RuntimeEnvironment environment, SessionFactory factory, TaskServiceFactory taskServiceFactory, String identifier) {
        super(environment, identifier);
        this.factory = factory;
        this.taskServiceFactory = taskServiceFactory;
        this.mapper = environment.getMapper();
    }
    
    @Override
    public org.kie.internal.runtime.manager.Runtime getRuntime(Context context) {
  
        Object contextId = context.getContextId();
        KieSession ksession = null;
        Integer ksessionId = null;
        if (contextId == null) {            
            ksession = factory.newKieSession();
            ksessionId = ksession.getId();                 
        } else {
            Runtime localRuntime = findLocalRuntime(contextId);
            if (localRuntime != null) {
                return localRuntime;
            }
            ksessionId = mapper.findMapping(context);
            if (ksessionId == null) {
                throw new SessionNotFoundException("No session found for context " + context.getContextId());
            }
            ksession = factory.findKieSessionById(ksessionId);
        }
        
        org.kie.internal.runtime.manager.Runtime runtime = new RuntimeImpl(ksession, taskServiceFactory.newTaskService());
        ((RuntimeImpl) runtime).setManager(this);
        registerDisposeCallback(runtime);
        registerItems(runtime);
        
        saveLocalRuntime(contextId, runtime);
        
        ksession.addEventListener(new MaintainMappingListener(ksessionId, runtime));
        return runtime;
    }

    @Override
    public void disposeRuntime(org.kie.internal.runtime.manager.Runtime runtime) {
        removeLocalRuntime(runtime);
        if (runtime instanceof Disposable) {
            ((Disposable) runtime).dispose();
        }
    }

    @Override
    public void close() {
        super.close();
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
        private Runtime runtime;
        
        MaintainMappingListener(Integer ksessionId, Runtime runtime) {
            this.ksessionId = ksessionId;
            this.runtime = runtime;
        }
        @Override
        public void afterProcessCompleted(ProcessCompletedEvent event) {
            mapper.removeMapping(ProcessInstanceIdContext.get(event.getProcessInstance().getId()));
            removeLocalRuntime(runtime);
        }

        @Override
        public void beforeProcessStarted(ProcessStartedEvent event) {
            mapper.saveMapping(ProcessInstanceIdContext.get(event.getProcessInstance().getId()), ksessionId);  
            saveLocalRuntime(event.getProcessInstance().getId(), runtime);
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
    
    protected org.kie.internal.runtime.manager.Runtime findLocalRuntime(Object processInstanceId) {
        if (processInstanceId == null) {
            return null;
        }
        Map<Object, org.kie.internal.runtime.manager.Runtime> map = local.get();
        if (map == null) {
            return null;
        } else {
            return map.get(processInstanceId);
        }
    }
    
    protected void saveLocalRuntime(Object processInstanceId, Runtime runtime) {
        if (processInstanceId == null) {
            return;
        }
        Map<Object, org.kie.internal.runtime.manager.Runtime> map = local.get();
        if (map == null) {
            map = new HashMap<Object, Runtime>();
            local.set(map);
        } 
        
        map.put(processInstanceId, runtime);
        
    }
    
    protected void removeLocalRuntime(Runtime runtime) {
        Map<Object, org.kie.internal.runtime.manager.Runtime> map = local.get();
        Object keyToRemove = -1l;
        if (map != null) {
            for (Map.Entry<Object, Runtime> entry : map.entrySet()) {
                if (runtime.equals(entry.getValue())) {
                    keyToRemove = entry.getKey();
                    break;
                }
            }
            
            map.remove(keyToRemove);
        }
    }
}
