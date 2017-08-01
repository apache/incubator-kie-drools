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

package org.jbpm.test;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.h2.tools.Server;
import org.jbpm.services.task.HumanTaskConfigurator;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.PoolingDataSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.manager.context.EmptyContext;

/**
 * Since version 6.0 this class is deprecated. Instead <code>RuntimeManager</code> should be used directly.
 * See documentation on how to use <code>RuntimeManager</code>
 */
public final class JBPMHelper {

    public static String[] processStateName = {"PENDING", "ACTIVE", "COMPLETED", "ABORTED", "SUSPENDED"};
    public static String[] txStateName = {"ACTIVE",
        "MARKED_ROLLBACK",
        "PREPARED",
        "COMMITTED",
        "ROLLEDBACK",
        "UNKNOWN",
        "NO_TRANSACTION",
        "PREPARING",
        "COMMITTING",
        "ROLLING_BACK"};

    private JBPMHelper() {
    }

    @Deprecated
    public static void startUp() {
        cleanupSingletonSessionId();
        Properties properties = getProperties();
        String driverClassName = properties.getProperty("persistence.datasource.driverClassName", "org.h2.Driver");
        if (driverClassName.startsWith("org.h2")) {
            JBPMHelper.startH2Server();
        }
        String persistenceEnabled = properties.getProperty("persistence.enabled", "false");
        String humanTaskEnabled = properties.getProperty("taskservice.enabled", "false");
        if ("true".equals(persistenceEnabled) || "true".equals(humanTaskEnabled)) {
            JBPMHelper.setupDataSource();
        }
        if ("true".equals(humanTaskEnabled)) {
            JBPMHelper.startTaskService();
        }
    }

    public static Server startH2Server() {
        try {
            // start h2 in memory database
            Server server = Server.createTcpServer(new String[0]);
            server.start();
            return server;
        } catch (Throwable t) {
            throw new RuntimeException("Could not start H2 server", t);
        }
    }

    public static PoolingDataSource setupDataSource() {
        Properties properties = getProperties();
        // create data source
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName(properties.getProperty("persistence.datasource.name", "jdbc/jbpm-ds"));
        pds.setClassName("org.h2.jdbcx.JdbcDataSource");
        pds.getDriverProperties().put("user", properties.getProperty("persistence.datasource.user", "sa"));
        pds.getDriverProperties().put("password", properties.getProperty("persistence.datasource.password", ""));
        pds.getDriverProperties().put("url", properties.getProperty("persistence.datasource.url", "jdbc:h2:tcp://localhost/~/jbpm-db;MVCC=TRUE"));
        pds.getDriverProperties().put("driverClassName", properties.getProperty("persistence.datasource.driverClassName", "org.h2.Driver"));
        pds.init();
        return pds;
    }

    @Deprecated
    public static TaskService startTaskService() {
        Properties properties = getProperties();
        String dialect = properties.getProperty("persistence.persistenceunit.dialect", "org.hibernate.dialect.H2Dialect");
        Map<String, String> map = new HashMap<String, String>();
        map.put("hibernate.dialect", dialect);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(properties.getProperty("taskservice.datasource.name", "org.jbpm.services.task"), map);
        System.setProperty("jbpm.user.group.mapping", properties.getProperty("taskservice.usergroupmapping", "classpath:/usergroups.properties"));

        TaskService taskService = new HumanTaskConfigurator()
                                        .entityManagerFactory(emf)
                                        .userGroupCallback(getUserGroupCallback())
                                        .getTaskService();
        return taskService;
    }

    @Deprecated
    public static void registerTaskService(StatefulKnowledgeSession ksession) {
        // no-op HT work item handler is already registered when using RuntimeManager

    }

    @Deprecated
    public static StatefulKnowledgeSession newStatefulKnowledgeSession(KieBase kbase) {
        return loadStatefulKnowledgeSession(kbase, -1);
    }

    @Deprecated
    public static StatefulKnowledgeSession loadStatefulKnowledgeSession(KieBase kbase, int sessionId) {
        Properties properties = getProperties();
        String persistenceEnabled = properties.getProperty("persistence.enabled", "false");
        RuntimeEnvironmentBuilder builder = null;
        if ("true".equals(persistenceEnabled)) {
            String dialect = properties.getProperty("persistence.persistenceunit.dialect", "org.hibernate.dialect.H2Dialect");
            Map<String, String> map = new HashMap<String, String>();
            map.put("hibernate.dialect", dialect);
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(properties.getProperty("persistence.persistenceunit.name", "org.jbpm.persistence.jpa"), map);
            
            
            builder = RuntimeEnvironmentBuilder.Factory.get()
        			.newDefaultBuilder()
                .entityManagerFactory(emf)                
                .addEnvironmentEntry(EnvironmentName.TRANSACTION_MANAGER, com.arjuna.ats.jta.TransactionManager.transactionManager());


        } else {            
            builder = RuntimeEnvironmentBuilder.Factory.get()
        			.newDefaultInMemoryBuilder();
        }
        builder.knowledgeBase(kbase);
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(builder.get());
        return (StatefulKnowledgeSession) manager.getRuntimeEngine(EmptyContext.get()).getKieSession();
    }
    
    @Deprecated
    @SuppressWarnings("unchecked")
    public static UserGroupCallback getUserGroupCallback() {
        Properties properties = getProperties();
        String className = properties.getProperty("taskservice.usergroupcallback");
        if (className != null) {
            try {
                Class<UserGroupCallback> clazz = (Class<UserGroupCallback>) Class.forName(className);
           
                return clazz.newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot create instance of UserGroupCallback " + className, e);
            }
        } else {
            return new JBossUserGroupCallbackImpl("classpath:/usergroups.properties");
        }
    }

    @Deprecated
    public static Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(JBPMHelper.class.getResourceAsStream("/jBPM.properties"));
        } catch (Throwable t) {
            // do nothing, use defaults
        }
        return properties;
    }
    
    @Deprecated
    protected static void cleanupSingletonSessionId() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (tempDir.exists()) {
            
            String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {
                
                @Override
                public boolean accept(File dir, String name) {
                    
                    return name.endsWith("-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {
                
                new File(tempDir, file).delete();
            }
        }
    }
}
