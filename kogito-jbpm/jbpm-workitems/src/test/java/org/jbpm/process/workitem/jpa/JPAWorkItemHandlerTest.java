/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.process.workitem.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.TaskService;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class JPAWorkItemHandlerTest {
    
    private static final String P_UNIT = "org.jbpm.test.jpaWIH";
    private static EntityManagerFactory emf;
    private static WorkItemHandler handler;

    private static TestH2Server h2Server;
    private static UserTransaction ut;


    @BeforeClass
    public static void configure() throws InterruptedException, NamingException {
        setupPoolingDataSource();
        ClassLoader classLoader = JPAWorkItemHandler.class.getClassLoader();
        handler = new JPAWorkItemHandler(P_UNIT, classLoader);
        emf = Persistence.createEntityManagerFactory(P_UNIT);
        ut = getUserTransaction();
    }


    @Test
    public void createOnProcessTest() throws Exception {
        String DESC = "Table";
        Product p = new Product(DESC, 10f);
        startJPAWIHProcess(JPAWorkItemHandler.CREATE_ACTION, p);
        UserTransaction ut = getUserTransaction();
        ut.begin();
        EntityManager em = emf.createEntityManager();
        TypedQuery<Product> products = em.createQuery("select p from Product p where p.description = :desc", Product.class);
        products.setParameter("desc", DESC);
        List<Product> resultList = products.getResultList();
        Product result = resultList.iterator().next();
        assertEquals(DESC, result.getDescription());
        em.remove(result);
        em.flush();
        em.close();
        ut.commit();
    }
    
    @Test
    public void removeOnProcessTest() throws Exception {
        Product p = new Product("A Product", 10f);
        ut.begin();
        EntityManager em = emf.createEntityManager();
        em.persist(p);
        long id = p.getId();
        em.close();
        ut.commit();
        startJPAWIHProcess(JPAWorkItemHandler.DELETE_ACTION, p);
        ut.begin();
        em = emf.createEntityManager();
        p = em.find(Product.class, id);
        assertNull(p);
        ut.commit();
    }
    
    @Test
    public void updateOnProcessTest() throws Exception {
        String DESC = "Table";
        String NEW_DESC = "Red Table";
        Product p = new Product(DESC, 10f);
        ut.begin();
        EntityManager em = emf.createEntityManager();
        em.persist(p);
        long id = p.getId();
        em.close();
        ut.commit();
        p.setDescription(NEW_DESC);
        startJPAWIHProcess(JPAWorkItemHandler.UPDATE_ACTION, p);
        ut.begin();
        em = emf.createEntityManager();
        p = em.find(Product.class, id);
        assertEquals(p.getDescription(), NEW_DESC);
        ut.commit();
        removeProduct(p);
    }
    
    @Test
    public void getActionTest() throws Exception {
        Product newProd = create(new Product("some product", 0.1f));
        String id = String.valueOf(newProd.getId());
        Product product = getProduct(id);
        assertEquals(product.getDescription(), newProd.getDescription());
        removeProduct(product);
    }


    @Test
    public void queryActionTest() throws Exception {
        Product p1 = create(new Product("some prod", 2f));
        Product p2 = create(new Product("other prod", 3f));
        List<Product> products = getAllProducts();
        assertEquals(2, products.size());
        removeProduct(p1);
        removeProduct(p2);
    }
    
    @Test
    public void queryWithParameterActionTest() throws Exception {
        String DESC = "Cheese";
        Product p1 = new Product("Bread", 2f);
        Product p2 = new Product("Milk", 3f);
        Product p3 = new Product(DESC, 5f);
        create(p1);
        create(p2);
        create(p3);
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter(JPAWorkItemHandler.P_ACTION,
                JPAWorkItemHandler.QUERY_ACTION);
        Map<String, Object> params = new HashMap<>();
        params.put("desc", DESC);
        workItem.setParameter(JPAWorkItemHandler.P_QUERY,
                "SELECT p FROM Product p where p.description = :desc");
        workItem.setParameter(JPAWorkItemHandler.P_QUERY_PARAMS, params);
        UserTransaction ut = getUserTransaction();
        ut.begin();
        handler.executeWorkItem(workItem, new TestWorkItemManager(workItem));
        ut.commit();
        @SuppressWarnings("unchecked")
        List<Product> products = (List<Product>) workItem
                .getResult(JPAWorkItemHandler.P_QUERY_RESULTS);
        assertEquals(1, products.size());
        products = getAllProducts();
        assertEquals(3, products.size());
        for (Product product : products) {
            removeProduct(product);
        }
        products = getAllProducts();
        assertEquals(0, products.size());
    }
    
    private void removeProduct(Product prod) throws Exception {
        ut.begin();
        EntityManager em = emf.createEntityManager();
        prod = em.find(Product.class, prod.getId());
        em.remove(prod);
        em.close();
        ut.commit();
    }
    
    private Product create(Product newProd) throws Exception {
        ut.begin();
        EntityManager em = emf.createEntityManager();
        em.persist(newProd);
        em.close();
        ut.commit();
        return newProd;
    }

    private List<Product> getAllProducts() throws Exception {
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter(JPAWorkItemHandler.P_ACTION,
                JPAWorkItemHandler.QUERY_ACTION);
        workItem.setParameter(JPAWorkItemHandler.P_QUERY,
                "SELECT p FROM Product p");
        UserTransaction ut = getUserTransaction();
        ut.begin();
        handler.executeWorkItem(workItem, new TestWorkItemManager(workItem));
        ut.commit();
        @SuppressWarnings("unchecked")
        List<Product> products = (List<Product>) workItem
        .getResult(JPAWorkItemHandler.P_QUERY_RESULTS);
        return products;
    }
    
    private Product getProduct(String id) throws Exception {
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter(JPAWorkItemHandler.P_ACTION,
                JPAWorkItemHandler.GET_ACTION);
        workItem.setParameter(JPAWorkItemHandler.P_TYPE,
                "org.jbpm.process.workitem.jpa.Product");
        workItem.setParameter(JPAWorkItemHandler.P_ID, id);
        UserTransaction ut = getUserTransaction();
        ut.begin();
        handler.executeWorkItem(workItem, new TestWorkItemManager(workItem));
        ut.commit();
        Product product = (Product) workItem
                .getResult(JPAWorkItemHandler.P_RESULT);
        return product;
    }
    
    
    private void startJPAWIHProcess(String action, Product prod) throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        RuntimeEnvironment env = 
             RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
             .entityManagerFactory(emf) 
             .addEnvironmentEntry("org.kie.internal.runtime.manager.TaskServiceFactory", new TaskServiceFactory(){

                @Override
                public TaskService newTaskService() {
                    return null;
                }

                @Override
                public void close() {
                    
                }
                 
             })
             .addAsset(ResourceFactory.newClassPathResource("JPAWIH.bpmn2"),ResourceType.BPMN2)
             .get();        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(env);
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        KieSession kSession = engine.getKieSession();

        JPAWorkItemHandler jpaWih = new JPAWorkItemHandler(P_UNIT, this.getClass().getClassLoader());
        kSession.getWorkItemManager().registerWorkItemHandler("JPAWIH", jpaWih);
        
        HashMap<String, Object> params = new HashMap<>();
        params.put("action", action);
        params.put("obj", prod);
        params.put("id", prod.getId());
        kSession.startProcess("org.jbpm.JPA_WIH", params);
        manager.disposeRuntimeEngine(engine);
        manager.close();
    }

    private static UserTransaction getUserTransaction() throws NamingException {
        return (UserTransaction) InitialContext.doLookup("java:comp/UserTransaction");
    }

    private class TestWorkItemManager implements WorkItemManager {

        private WorkItem workItem;

        TestWorkItemManager(WorkItem workItem) {
            this.workItem = workItem;
        }

        public void completeWorkItem(long id, Map<String, Object> results) {
            ((WorkItemImpl) workItem).setResults(results);

        }

        public void abortWorkItem(long id) {

        }

        public void registerWorkItemHandler(String workItemName,
                WorkItemHandler handler) {
        }

    }

    public static PoolingDataSource setupPoolingDataSource() {
        h2Server = new TestH2Server();
        h2Server.start();
        PoolingDataSource pds = new PoolingDataSource();
        pds.setMaxPoolSize(10);
        pds.setMinPoolSize(10);
        pds.setUniqueName("jpaWIH");
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", "sa");
        pds.getDriverProperties().put("url", "jdbc:h2:mem:jpa-wih;MVCC=true");
        pds.getDriverProperties().put("driverClassName", "org.h2.Driver");
        pds.init();
        return pds;
    }

    private static class TestH2Server {
        private Server realH2Server;

        public void start() {
            if (realH2Server == null || !realH2Server.isRunning(false)) {
                try {
                    realH2Server = Server.createTcpServer(new String[0]);
                    realH2Server.start();
                    System.out.println("Started H2 Server...");
                } catch (SQLException e) {
                    throw new RuntimeException("can't start h2 server db", e);
                }
            }
        }

        @Override
        protected void finalize() throws Throwable {
            if (realH2Server != null) {
                System.out.println("Stopping H2 Server...");
                realH2Server.stop();
            }
            DeleteDbFiles.execute("", "target/jpa-wih", true);
            super.finalize();
        }

    }

}