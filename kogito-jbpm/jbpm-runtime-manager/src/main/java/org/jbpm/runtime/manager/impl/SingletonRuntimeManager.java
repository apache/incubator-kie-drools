package org.jbpm.runtime.manager.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.manager.Context;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.RuntimeEngine;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.SessionFactory;
import org.kie.internal.runtime.manager.TaskServiceFactory;

public class SingletonRuntimeManager extends AbstractRuntimeManager {
    

    
    private RuntimeEngine singleton;
    private SessionFactory factory;
    private TaskServiceFactory taskServiceFactory;

    public SingletonRuntimeManager() {
        super(null, null);
        // no-op just for cdi, spring and other frameworks
    }
    
    public SingletonRuntimeManager(RuntimeEnvironment environment, SessionFactory factory, TaskServiceFactory taskServiceFactory, String identifier) {
        super(environment, identifier);
        this.factory = factory;
        this.taskServiceFactory = taskServiceFactory;
        this.identifier = identifier;
    }
    
    public void init() {

        // TODO should we proxy/wrap the ksession so we capture dispose.destroy method calls?
        String location = getLocation();
        Integer knownSessionId = getPersistedSessionId(location, identifier);
        if (knownSessionId > 0) {
            try {
                this.singleton = new SynchronizedRuntimeImpl(factory.findKieSessionById(knownSessionId), taskServiceFactory.newTaskService());
            } catch (RuntimeException e) {
                // in case session with known id was found
            }
        } 
        
        if (this.singleton == null) {
            this.singleton = new SynchronizedRuntimeImpl(factory.newKieSession(), taskServiceFactory.newTaskService());
            persistSessionId(location, identifier, singleton.getKieSession().getId());
        }
        ((RuntimeEngineImpl) singleton).setManager(this);
        registerItems(this.singleton);
        attachManager(this.singleton);
        activeManagers.add(identifier);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public RuntimeEngine getRuntimeEngine(Context context) {        
        // always return the same instance
        return this.singleton;
    }


    @Override
    public void validate(KieSession ksession, Context<?> context) throws IllegalStateException {
        if (this.singleton != null && this.singleton.getKieSession().getId() != ksession.getId()) {
            throw new IllegalStateException("Invalid session was used for this context " + context);
        }
    }
    
    @Override
    public void disposeRuntimeEngine(org.kie.internal.runtime.manager.RuntimeEngine runtime) {
        // no-op, singleton session is always active
    }

    @Override
    public void close() {
        super.close();
        // dispose singleton session only when manager is closing
        if (this.singleton instanceof Disposable) {
            ((Disposable) this.singleton).dispose();
        }
        factory.close();
        this.singleton = null;   
    }
    
    /**
     * Retrieves session id from serialized file named jbpmSessionId.ser from given location.
     * @param location directory where jbpmSessionId.ser file should be
     * @param identifier of the manager owning this ksessionId
     * @return sessionId if file was found otherwise 0
     */
    protected int getPersistedSessionId(String location, String identifier) {
        File sessionIdStore = new File(location + File.separator + identifier+ "-jbpmSessionId.ser");
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
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                    }
                }
 
            }
            
        } else {
            return 0;
        }
    }
    
    /**
     * Stores gives ksessionId in a serialized file in given location under jbpmSessionId.ser file name
     * @param location directory where serialized file should be stored
     * @param identifier of the manager owning this ksessionId
     * @param ksessionId value of ksessionId to be stored
     */
    protected void persistSessionId(String location, String identifier, int ksessionId) {
        if (location == null) {
            return;
        }
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(location + File.separator + identifier + "-jbpmSessionId.ser");
            out = new ObjectOutputStream(fos);
            out.writeObject(Integer.valueOf(ksessionId));
            out.close();
        } catch (IOException ex) {
//            logger.warn("Error when persisting known session id", ex);
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
    
    protected String getLocation() {
        String location = System.getProperty("jbpm.data.dir", System.getProperty("jboss.server.data.dir"));
        if (location == null) {
            location = System.getProperty("java.io.tmpdir");
        }
        return location;
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

}
