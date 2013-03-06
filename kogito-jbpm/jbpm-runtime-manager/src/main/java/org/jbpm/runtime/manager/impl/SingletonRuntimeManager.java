package org.jbpm.runtime.manager.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.annotation.PostConstruct;

import org.jbpm.task.api.TaskServiceEntryPoint;
import org.kie.runtime.manager.Context;
import org.kie.runtime.manager.Disposable;
import org.kie.runtime.manager.Runtime;
import org.kie.runtime.manager.RuntimeEnvironment;
import org.kie.runtime.manager.SessionFactory;
import org.kie.runtime.manager.TaskServiceFactory;

public class SingletonRuntimeManager extends AbstractRuntimeManager {
    
    private Runtime<TaskServiceEntryPoint> singleton;
    private SessionFactory factory;
    private TaskServiceFactory<TaskServiceEntryPoint> taskServiceFactory;
    
    public SingletonRuntimeManager() {
        super(null);
        // no-op just for cdi, spring and other frameworks
    }
    
    public SingletonRuntimeManager(RuntimeEnvironment environment, SessionFactory factory, TaskServiceFactory<TaskServiceEntryPoint> taskServiceFactory) {
        super(environment);
        this.factory = factory;
        this.taskServiceFactory = taskServiceFactory;
    }
    @PostConstruct
    public void init() {
        // TODO should we proxy/wrap the ksession so we capture dispose.destroy method calls?
        String location = System.getProperty("jbpm.conf.dir", System.getProperty("java.io.tmpdir"));
        Integer knownSessionId = getPersistedSessionId(location);
        if (knownSessionId > 0) {
            try {
                this.singleton = new SynchronizedRuntimeImpl(factory.findKieSessionById(knownSessionId), taskServiceFactory.newTaskService());
            } catch (RuntimeException e) {
                // in case session with known id was found
            }
        } 
        
        if (this.singleton == null) {
            this.singleton = new SynchronizedRuntimeImpl(factory.newKieSession(), taskServiceFactory.newTaskService());
            persistSessionId(location, singleton.getKieSession().getId());
        }
        
        registerItems(this.singleton);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Runtime getRuntime(Context context) {
        // always return the same instance
        return this.singleton;
    }

    @Override
    public void disposeRuntime(org.kie.runtime.manager.Runtime runtime) {
        // no-op, singleton session is always active
    }

    @Override
    public void close() {
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
     * @return sessionId if file was found otherwise 0
     */
    protected int getPersistedSessionId(String location) {
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
    
    /**
     * Stores gives ksessionId in a serialized file in given location under jbpmSessionId.ser file name
     * @param location directory where serialized file should be stored
     * @param ksessionId value of ksessionId to be stored
     */
    protected void persistSessionId(String location, int ksessionId) {
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
