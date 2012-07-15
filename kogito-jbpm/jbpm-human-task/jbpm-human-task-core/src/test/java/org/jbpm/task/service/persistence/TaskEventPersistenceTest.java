package org.jbpm.task.service.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.task.BaseTest;

public class TaskEventPersistenceTest extends BaseTest {

    @Override
    protected EntityManagerFactory createEntityManagerFactory() { 
        Map<String, String> properties = new HashMap<String, String>();
        properties.put( "hibernate.hbm2ddl.auto", "update");
        return Persistence.createEntityManagerFactory("org.jbpm.task", properties);
    }
    
    public void testTaskEventPersistence() { 
        
    }
}
