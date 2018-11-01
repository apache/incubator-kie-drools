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

package org.jbpm.persistence.scripts;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.persistence.jta.JtaTransactionManager;
import org.jbpm.persistence.map.impl.ProcessCreatorForHelp;
import org.jbpm.persistence.scripts.oldentities.ProcessInstanceInfo;
import org.jbpm.persistence.scripts.oldentities.SessionInfo;
import org.jbpm.persistence.scripts.oldentities.TaskImpl;
import org.jbpm.persistence.scripts.util.SQLCommandUtil;
import org.jbpm.persistence.scripts.util.SQLScriptUtil;
import org.jbpm.persistence.scripts.util.TestsUtil;
import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.test.util.PoolingDataSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalI18NText;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central context that hides persistence from tests, so there is no need to work with persistence in the tests
 * (transactions etc).
 */
public final class TestPersistenceContext {

    private static final Logger logger = LoggerFactory.getLogger(TestPersistenceContext.class);
    private HashMap<String, Object> context;
    private EntityManagerFactory entityManagerFactory;
    private JtaTransactionManager transactionManager;
    private Environment environment;

    private final Properties dataSourceProperties;
    private final DatabaseType databaseType;

    public TestPersistenceContext() {
        this.dataSourceProperties = PersistenceUtil.getDatasourceProperties();
        this.databaseType = TestsUtil.getDatabaseType(dataSourceProperties);
    }

    /**
     * Initializes persistence context from specified persistence unit.
     * @param persistenceUnit Persistence unit which is used to initialize this persistence context.
     */
    public void init(final PersistenceUnit persistenceUnit) {
        try {
            context = PersistenceUtil.setupWithPoolingDataSource(persistenceUnit.getName(), persistenceUnit
                    .getDataSourceName());
            entityManagerFactory = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
            environment = PersistenceUtil.createEnvironment(context);
            Object tm = this.environment.get(EnvironmentName.TRANSACTION_MANAGER);
            transactionManager = new JtaTransactionManager(environment.get(EnvironmentName.TRANSACTION),
                    environment.get(EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY),
                    tm);
        } catch (RuntimeException ex) {
            // log the whole exception stacktrace as for some reason junit is not able to do so and only prints
            // the highest level exception, which makes debugging very hard
            logger.error("Failed to initialize persistence unit {}", persistenceUnit, ex);
            if (entityManagerFactory != null) {
                entityManagerFactory.close();
            }
            throw ex;
        }
    }

    /**
     * Cleans up this persistence context. Closes all instances that need to be closed.
     */
    public void clean() {
        PersistenceUtil.cleanUp(context);
    }

    /**
     * Executes SQL scripts from specified root SQL scripts folder. Selects appropriate scripts from root folder
     * by using dialect that is defined in datasource.properties file.
     * @param scriptsRootFolder Root folder containing folders with SQL scripts for all supported database systems.
     * @throws IOException
     */
    public void executeScripts(final File scriptsRootFolder) throws IOException, SQLException {
        executeScripts(scriptsRootFolder, null);
    }
    
    public void executeScripts(final File scriptsRootFolder, String type) throws IOException, SQLException {
        testIsInitialized();
        final File[] sqlScripts = TestsUtil.getDDLScriptFilesByDatabaseType(scriptsRootFolder, databaseType, true);
        final Connection connection = ((PoolingDataSource) context.get(PersistenceUtil.DATASOURCE)).getConnection();
        connection.setAutoCommit(false);
        try {
            for (File script : sqlScripts) {
                if (type == null || script.getName().startsWith(type)) {
                    logger.debug("Executing script {}", script.getName());
                    final List<String> scriptCommands = SQLScriptUtil.getCommandsFromScript(script, databaseType);
                    for (String command : scriptCommands) {
                        logger.debug(command);
                        final PreparedStatement statement;
                        if (databaseType == DatabaseType.SQLSERVER || databaseType == DatabaseType.SQLSERVER2008) {
                            statement = connection.prepareStatement(
                                    SQLCommandUtil.preprocessCommandSqlServer(command, dataSourceProperties));
                        } else {
                            statement = connection.prepareStatement(command);
                        }
                        statement.execute();
                        statement.close();
                    }
                }
            }
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            connection.close();
        }
    }

    /**
     * Starts and persists a basic simple process using current database entities.
     * @param processId Process identifier. This identifier is also used to generate KieBase
     * (process with this identifier is part of generated KieBase).
     */
    public void startAndPersistSomeProcess(final String processId) {
        testIsInitialized();
        final StatefulKnowledgeSession session;
        final KieBase kbase = createKieBase(processId);

        session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, environment);
        session.startProcess(processId);
    }
    
    public void createSomeTask() {
        testIsInitialized();
        TaskImpl task = new TaskImpl();
        InternalI18NText name = (InternalI18NText) TaskModelProvider.getFactory().newI18NText();
        name.setText("Some Task");
        List<I18NText> names = new ArrayList<I18NText>();
        names.add(name);
        task.setNames(names);
        InternalTaskData taskData = (InternalTaskData) TaskModelProvider.getFactory().newTaskData();        
        taskData.setWorkItemId(12);
        taskData.setProcessInstanceId(1);
        taskData.setProcessId("someprocess");
        taskData.setDeploymentId("org.jbpm.test:someprocess:1.0");
        taskData.setProcessSessionId(1);
        task.setTaskData(taskData);
        InternalPeopleAssignments peopleAssignments = 
            (InternalPeopleAssignments) TaskModelProvider.getFactory().newPeopleAssignments();
        peopleAssignments.setPotentialOwners(new ArrayList<OrganizationalEntity>());
        peopleAssignments.setBusinessAdministrators(new ArrayList<OrganizationalEntity>());
        peopleAssignments.setExcludedOwners(new ArrayList<OrganizationalEntity>());
        peopleAssignments.setRecipients(new ArrayList<OrganizationalEntity>());
        peopleAssignments.setTaskStakeholders(new ArrayList<OrganizationalEntity>());
        InternalOrganizationalEntity jdoe = 
            (InternalOrganizationalEntity) TaskModelProvider.getFactory().newUser();
        jdoe.setId("jdoe");
        peopleAssignments.getPotentialOwners().add(jdoe);
        peopleAssignments.getBusinessAdministrators().add(jdoe);
        task.setPeopleAssignments(peopleAssignments);
        final boolean txOwner = transactionManager.begin();
        try {
            EntityManager em = entityManagerFactory.createEntityManager();
            em.persist(jdoe);
            em.persist(task);
            transactionManager.commit(txOwner);
        } catch (Exception ex) {
            ex.printStackTrace();
            transactionManager.rollback(txOwner);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * Loads persisted session from database.
     * @param sessionId Unique identifier of the session.
     * @param processIdForKieBase Process identifier for KieBase generation. A KieBase is generated for
     * loaded session and this KieBase contains process with this identifier.
     * @return Session that is stored in database.
     */
    public StatefulKnowledgeSession loadPersistedSession(final Long sessionId, final String processIdForKieBase) {
        testIsInitialized();
        return JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, createKieBase(processIdForKieBase),
                null, environment);
    }

    /**
     * Persists a process and a session using entites from jBPM 6.0. Persists
     * process and session using their database identifiers so be aware that you can
     * rewrite some of your data. This method should be used only to populate inital data for tests.
     * @param sessionId Unique identifier of the session.
     * @param processId Identifier of the process (name).
     * @param processInstanceId Unique identifier of the process.
     */
    public void persistOldProcessAndSession(final Integer sessionId, final String processId,
            final Long processInstanceId) {
        testIsInitialized();
        final boolean txOwner = transactionManager.begin();
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.merge(getOldProcessInstanceInfo(processId, processInstanceId));
            entityManager.merge(getOldSessionInfo(sessionId));
            entityManager.flush();
            entityManager.close();
            transactionManager.commit(txOwner);
        } catch (Exception ex) {
            ex.printStackTrace();
            transactionManager.rollback(txOwner);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * Reads stored processes count from database.
     * @return Stored processes count.
     */
    public int getStoredProcessesCount() {
        return getStoredEntitiesCount("ProcessInstanceInfo");
    }

    /**
     * Reads stored sessions count from database.
     * @return Stored sessions count.
     */
    public int getStoredSessionsCount() {
        return getStoredEntitiesCount("SessionInfo");
    }

    /**
     * Reads stored entities count from database.
     * @param entityClassName Class name of entity.
     * @return Stored entities count.
     */
    private int getStoredEntitiesCount(final String entityClassName) {
        testIsInitialized();
        final boolean txOwner = transactionManager.begin();
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            final List entitiesList = entityManager.createQuery("SELECT p FROM " + entityClassName + " p")
                    .getResultList();
            if (entitiesList == null) {
                return 0;
            } else {
                return entitiesList.size();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            transactionManager.rollback(txOwner);
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            entityManager.close();
            transactionManager.commit(txOwner);
        }
    }

    /**
     * Checks if this persistence context is initialized.
     */
    private void testIsInitialized() {
        if (context == null) {
            throw new IllegalStateException("TestContext is not initialized! Call TestContext.init() before using it.");
        }
    }

    /**
     * Creates very basic KieBase that contains processes with specified processIds.
     * @param processIds ProcessIds of processes that are contained within resulting KieBase.
     * @return Basic KieBase.
     */
    private KieBase createKieBase(final String... processIds) {
        final KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        for (String processId : processIds) {
            ((KnowledgeBaseImpl) kbase).addProcess(ProcessCreatorForHelp.newSimpleEventProcess(processId, "test"));
        }
        return kbase;
    }

    /**
     * Returns ProcessInstanceInfo entity for jBPM 6.0 version.
     * @param processId Process identifier (name).
     * @param processInstanceId Unique identifier of the process. Database identifier.
     * @return ProcessInstanceInfo entity for jBPM 6.0 version filled with default data.
     * @throws ParseException
     */
    private ProcessInstanceInfo getOldProcessInstanceInfo(final String processId, final Long processInstanceId)
            throws ParseException {
        final DateFormat dateFormat = getDateFormat();
        final ProcessInstanceInfo result = new ProcessInstanceInfo();
        result.setProcessInstanceId(processInstanceId);
        result.setEventTypes(getOldProcessEventTypes());
        result.setLastModificationDate(dateFormat.parse("2015-08-25 13:43:25.760"));
        result.setLastReadDate(dateFormat.parse("2015-08-25 13:43:25.210"));
        result.setProcessId(processId);
        result.setStartDate(dateFormat.parse("2015-08-25 13:43:25.190"));
        result.setState(1);
        result.setVersion(2);
        result.setProcessInstanceByteArray(
                TestsUtil.hexStringToByteArray(
                        "ACED00057769000852756C65466C6F770A0608061004180052550A0852756C65466C6F7710011A0E6D696E696D616C50726F63657373200128023A0A0801100222020805280160006A0E5F6A62706D2D756E697175652D3072120A0E5F6A62706D2D756E697175652D311001800101"));
        return result;
    }

    /**
     * Return default process event types.
     * @return Default process event types.
     */
    private Set<String> getOldProcessEventTypes() {
        final Set<String> resultSet = new HashSet<String>(1);
        resultSet.add("test");
        return resultSet;
    }

    /**
     * Returns SessionInfo entity for jBPM 6.0 version.
     * @param sessionId Unique identifier of the session. Database identifier.
     * @return SessionInfo entity for jBPM 6.0 version filled with default data.
     * @throws ParseException
     */
    private SessionInfo getOldSessionInfo(final Integer sessionId) throws ParseException {
        final DateFormat dateFormat = getDateFormat();
        final SessionInfo result = new SessionInfo();
        result.setId(sessionId);
        result.setLastModificationDate(dateFormat.parse("2015-08-25 13:43:25.248"));
        result.setStartDate(dateFormat.parse("2015-08-25 13:43:24.858"));
        result.setVersion(2);
        result.setData(
                TestsUtil.hexStringToByteArray(
                        "ACED0005777C0A060806100418005272080010001A6818002000320608011000180042231A190A044D41494E10001801200028FFFFFFFFFFFFFFFFFF01400022060A044D41494E52350A0744454641554C54222A0A266F72672E64726F6F6C732E636F72652E726574656F6F2E496E697469616C46616374496D706C100022026800"));
        return result;
    }

    private DateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    }
}
