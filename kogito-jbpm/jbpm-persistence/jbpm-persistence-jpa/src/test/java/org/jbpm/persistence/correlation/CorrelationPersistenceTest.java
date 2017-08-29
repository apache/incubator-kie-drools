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

package org.jbpm.persistence.correlation;

import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.api.runtime.EnvironmentName;

@RunWith(Parameterized.class)
public class CorrelationPersistenceTest extends AbstractBaseTest {
    
    private HashMap<String, Object> context;
    
    public CorrelationPersistenceTest(boolean locking) { 
        this.useLocking = locking; 
     }
     
     @Parameters
     public static Collection<Object[]> persistence() {
         Object[][] data = new Object[][] { { false }, { true } };
         return Arrays.asList(data);
     };
         
    @Before
    public void before() throws Exception {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        CorrelationKeyFactory factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
        // populate table with test data
        EntityManagerFactory emf = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        try {
            EntityManager em = emf.createEntityManager();
            em.persist(factory.newCorrelationKey("test123"));

            List<String> props = new ArrayList<String>();
            props.add("test123");
            props.add("123test");

            em.persist(factory.newCorrelationKey(props));
            ut.commit();
        } catch (Exception ex) {
            ut.rollback();
            Assert.fail("Exception thrown while trying to prepare correlation data.");
        }
    }
    
    @After
    public void after() throws Exception {
        EntityManagerFactory emf = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        try {
            EntityManager em = emf.createEntityManager();
            em.createQuery("delete from CorrelationPropertyInfo").executeUpdate();
            em.createQuery("delete from CorrelationKeyInfo").executeUpdate();
            ut.commit();
        } catch (Exception ex) {
            ut.rollback();
            Assert.fail("Exception thrown while trying to cleanup correlation data.");
        }
        cleanUp(context);
    }

    @Test
    public void testCreateCorrelation() throws Exception {
        EntityManagerFactory emf = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        EntityManager em = emf.createEntityManager();

        Query query = em.createNamedQuery("GetProcessInstanceIdByCorrelation");
        query.setParameter("ckey", "test123");        
        
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
        query.setParameter("ckey", "test1234"); 
        
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
        query.setParameter("ckey", "test123:123test");
        
        List<Long> processInstances = query.getResultList();
        em.close();
        assertNotNull(processInstances);
        assertEquals(1, processInstances.size());
    }
}
