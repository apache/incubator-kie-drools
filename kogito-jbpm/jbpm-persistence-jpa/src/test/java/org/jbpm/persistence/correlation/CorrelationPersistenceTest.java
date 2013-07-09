package org.jbpm.persistence.correlation;

import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.api.runtime.EnvironmentName;

public class CorrelationPersistenceTest extends AbstractBaseTest {
    
    private HashMap<String, Object> context;
    
    @Before
    public void before() throws Exception {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME, false);
        CorrelationKeyFactory factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
        // populate table with test data
        EntityManagerFactory emf = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        EntityManager em = emf.createEntityManager();

        em.persist(factory.newCorrelationKey("test123"));

        List<String> props = new ArrayList<String>();
        props.add("test123");
        props.add("123test");
        em.persist(factory.newCorrelationKey(props));
        
        ut.commit();
    }
    
    @After
    public void after() {  
        try {
            EntityManagerFactory emf = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
            UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
            ut.begin();
            EntityManager em = emf.createEntityManager();
            em.createQuery("delete from CorrelationPropertyInfo").executeUpdate();
            em.createQuery("delete from CorrelationKeyInfo").executeUpdate();
            ut.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cleanUp(context);
    }

    @Test
    public void testCreateCorrelation() throws Exception {
        EntityManagerFactory emf = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        EntityManager em = emf.createEntityManager();

        
        Query query = em.createNamedQuery("GetProcessInstanceIdByCorrelation");
        query.setParameter("properties", Arrays.asList(new String[] {"test123"}));
        query.setParameter("elem_count", new Long(1));
        
        List<Long> processInstances = query.getResultList();
        em.close();
        assertNotNull(processInstances);
        assertEquals(1, processInstances.size());
    }
    
    @Test
    public void testCreateCorrelationMultiValueDoesNotMatch() throws Exception {
        EntityManagerFactory emf = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        EntityManager em = emf.createEntityManager();
        
        Query query = em.createNamedQuery("GetProcessInstanceIdByCorrelation");
        query.setParameter("properties", Arrays.asList(new String[] {"test123"}));
        query.setParameter("elem_count", new Long(2));
        
        List<Long> processInstances = query.getResultList();
        em.close();
        assertNotNull(processInstances);
        assertEquals(0, processInstances.size());
    }
    
    @Test
    public void testCreateCorrelationMultiValueDoesMatch() throws Exception {
        EntityManagerFactory emf = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        EntityManager em = emf.createEntityManager();
        
        Query query = em.createNamedQuery("GetProcessInstanceIdByCorrelation");
        query.setParameter("properties", Arrays.asList(new String[] {"test123", "123test"}));
        query.setParameter("elem_count", new Long(2));
        
        List<Long> processInstances = query.getResultList();
        em.close();
        assertNotNull(processInstances);
        assertEquals(1, processInstances.size());
    }
}
