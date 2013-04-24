package org.jbpm.services.task;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jbpm.services.task.deadlines.DeadlinesDecorator;
import org.jbpm.services.task.identity.MvelUserGroupCallbackImpl;
import org.jbpm.services.task.identity.UserGroupLifeCycleManagerDecorator;
import org.jbpm.services.task.identity.UserGroupTaskInstanceServiceDecorator;
import org.jbpm.services.task.identity.UserGroupTaskQueryServiceDecorator;
import org.jbpm.services.task.impl.TaskAdminServiceImpl;
import org.jbpm.services.task.impl.TaskContentServiceImpl;
import org.jbpm.services.task.impl.TaskDeadlinesServiceImpl;
import org.jbpm.services.task.impl.TaskIdentityServiceImpl;
import org.jbpm.services.task.impl.TaskInstanceServiceImpl;
import org.jbpm.services.task.impl.TaskQueryServiceImpl;
import org.jbpm.services.task.impl.TaskServiceEntryPointImpl;
import org.jbpm.services.task.internals.lifecycle.LifeCycleManager;
import org.jbpm.services.task.internals.lifecycle.MVELLifeCycleManager;
import org.jbpm.services.task.subtask.SubTaskDecorator;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.api.JbpmServicesTransactionManager;
import org.jbpm.shared.services.impl.JbpmLocalTransactionManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
import org.kie.api.task.TaskService;
import org.kie.internal.task.api.TaskAdminService;
import org.kie.internal.task.api.TaskContentService;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.TaskIdentityService;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.TaskQueryService;
import org.kie.internal.task.api.UserGroupCallback;

public class HumanTaskConfigurator {

    private TaskService service;
    
    private EntityManagerFactory emf;
    
    private Logger logger = LogManager.getLogManager().getLogger("");
    
    private JbpmServicesTransactionManager jbpmTransactionManager = new JbpmLocalTransactionManager();
    
    private JbpmServicesPersistenceManager pm = new JbpmServicesPersistenceManagerImpl();   
    
    private TaskQueryService queryService = new TaskQueryServiceImpl();
    
    private TaskIdentityService identityService = new TaskIdentityServiceImpl();
    
    private TaskAdminService adminService = new TaskAdminServiceImpl();
    
    private TaskContentService contentService = new TaskContentServiceImpl();
    
    private TaskDeadlinesService deadlinesService = new TaskDeadlinesServiceImpl();
    
    private TaskInstanceService instanceService =  new TaskInstanceServiceImpl();
    
    private LifeCycleManager lifeCycleManager = new MVELLifeCycleManager();
    
    private UserGroupLifeCycleManagerDecorator userGroupLifeCycleDecorator = new UserGroupLifeCycleManagerDecorator();
    
    private UserGroupCallback userGroupCallback = new MvelUserGroupCallbackImpl();
    
    public HumanTaskConfigurator transactionManager(JbpmServicesTransactionManager tm) {
        this.jbpmTransactionManager = tm;
        
        return this;
    }

    public HumanTaskConfigurator entityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
        
        return this;
    }
    
    public HumanTaskConfigurator persistenceManager(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
        
        return this;
    }
    
    public HumanTaskConfigurator queryService(TaskQueryService queryService) {
        this.queryService = queryService;
        
        return this;
    }
    
    public HumanTaskConfigurator identityService(TaskIdentityService identityService) {
        this.identityService = identityService;
        
        return this;
    }
    
    public HumanTaskConfigurator adminService(TaskAdminService adminService) {
        this.adminService = adminService;
        
        return this;
    }
    
    public HumanTaskConfigurator contentService(TaskContentService contentService) {
        this.contentService = contentService;
        
        return this;
    }
    
    public HumanTaskConfigurator deadlinesService(TaskDeadlinesService deadlinesService) {
        this.deadlinesService = deadlinesService;
        
        return this;
    }
    
    public HumanTaskConfigurator instanceService(TaskInstanceService instanceService) {
        this.instanceService = instanceService;
        
        return this;
    }
    
    public HumanTaskConfigurator lifeCycleManager(LifeCycleManager lifeCycleManager) {
        this.lifeCycleManager = lifeCycleManager;
        
        return this;
    }
    
    public HumanTaskConfigurator userGroupCallback(UserGroupCallback userGroupCallback) {
        if (userGroupCallback == null) {
            return this;
        }
        this.userGroupCallback = userGroupCallback;
        
        return this;
    }
    
    public TaskService getTaskService() {
        if (service == null) {
            service = new TaskServiceEntryPointImpl();
            // Persistence and Transactions
            configurePersistenceManager();
    
            // Task Query
            configureTaskQueryService(pm);
            TaskQueryService userGroupQueryServiceDecorator = configureUserGroupQueryServiceDecorator(queryService, userGroupCallback);
            ((TaskServiceEntryPointImpl)service).setTaskQueryService(userGroupQueryServiceDecorator);
            
            // Task Identity
            configureTaskIdentityService(pm);
            ((TaskServiceEntryPointImpl)service).setTaskIdentityService(identityService);
            
            // Task Admin
            configureTaskAdminService(pm);
            ((TaskServiceEntryPointImpl)service).setTaskAdminService(adminService);
            
            // Task Content
            configureTaskContentService(pm);
            ((TaskServiceEntryPointImpl)service).setTaskContentService(contentService);
            
            // Task Deadlines
            configureTaskDeadlinesService(pm);
    
            // Task Instance
            configureTaskInstanceService(pm, queryService);
            
            // Task Instance  - Lifecycle Manager
            configureLifeCycleManager(pm, identityService, queryService, contentService);
            
            // User/Group Callbacks
            
            configureUserGroupLifeCycleManagerDecorator(pm, lifeCycleManager, userGroupCallback);
        
            ((TaskInstanceServiceImpl)instanceService).setLifeCycleManager(userGroupLifeCycleDecorator);
            
            TaskInstanceService userGroupTaskInstanceServiceDecorator = configureUserGroupTaskInstanceServiceDecorator(instanceService, userGroupCallback);
                    
            // Task Decorators - Sub Tasks
            SubTaskDecorator subTaskDecorator = createSubTaskDecorator(pm, userGroupTaskInstanceServiceDecorator, queryService);
            
            // Task Decorators - Deadlines
            DeadlinesDecorator deadlinesDecorator = createDeadlinesDecorator(pm, queryService, deadlinesService, subTaskDecorator);
            
            ((TaskServiceEntryPointImpl)service).setTaskInstanceService(deadlinesDecorator);
        }
        return service;
    }
    
    protected void configurePersistenceManager(){
        EntityManager em = emf.createEntityManager();
        // Persistence and Transactions
        ((JbpmServicesPersistenceManagerImpl)pm).setEm(em);
        ((JbpmServicesPersistenceManagerImpl)pm).setTransactionManager(jbpmTransactionManager);
        
    }
    
   protected void configureTaskQueryService(JbpmServicesPersistenceManager pm){
        
        ((TaskQueryServiceImpl)queryService).setPm(pm);
    }
    
    protected void configureTaskIdentityService(JbpmServicesPersistenceManager pm){
        ((TaskIdentityServiceImpl)identityService).setPm(pm);
    }
    
    protected void configureTaskAdminService(JbpmServicesPersistenceManager pm){
        ((TaskAdminServiceImpl)adminService).setPm(pm);
        
    }
    
    protected void configureTaskContentService(JbpmServicesPersistenceManager pm){
        ((TaskContentServiceImpl)contentService).setPm(pm);
    }
    
    protected void configureTaskDeadlinesService(JbpmServicesPersistenceManager pm){
        ((TaskDeadlinesServiceImpl)deadlinesService).setPm(pm);
        ((TaskDeadlinesServiceImpl)deadlinesService).setLogger(logger);
        ((TaskDeadlinesServiceImpl)deadlinesService).setNotificationEvents(((TaskServiceEntryPointImpl)service).getTaskNotificationEventListeners());
        ((TaskDeadlinesServiceImpl)deadlinesService).init();
    }
    
    protected void configureTaskInstanceService(JbpmServicesPersistenceManager pm, TaskQueryService queryService){
        ((TaskInstanceServiceImpl)instanceService).setPm(pm);
        ((TaskInstanceServiceImpl)instanceService).setTaskQueryService(queryService);
        ((TaskInstanceServiceImpl)instanceService).setTaskEvents(((TaskServiceEntryPointImpl)service).getTaskLifecycleEventListeners());
    }
    
    protected void configureLifeCycleManager(JbpmServicesPersistenceManager pm, 
                    TaskIdentityService identityService, TaskQueryService queryService, TaskContentService contentService){
        
        ((MVELLifeCycleManager)lifeCycleManager).setPm(pm);
        ((MVELLifeCycleManager)lifeCycleManager).setTaskIdentityService(identityService);
        ((MVELLifeCycleManager)lifeCycleManager).setTaskQueryService(queryService);
        ((MVELLifeCycleManager)lifeCycleManager).setTaskContentService(contentService);
        ((MVELLifeCycleManager)lifeCycleManager).setTaskEvents(((TaskServiceEntryPointImpl)service).getTaskLifecycleEventListeners());
        ((MVELLifeCycleManager)lifeCycleManager).setLogger(logger);
        ((MVELLifeCycleManager)lifeCycleManager).initMVELOperations();
        
        
    }
    
    protected void configureUserGroupLifeCycleManagerDecorator(JbpmServicesPersistenceManager pm, 
                                                            LifeCycleManager lifeCycleManager, UserGroupCallback userGroupCallback){
        
        
        userGroupLifeCycleDecorator.setManager(lifeCycleManager);
        userGroupLifeCycleDecorator.setPm(pm);
        userGroupLifeCycleDecorator.setUserGroupCallback(userGroupCallback);
        
    }
    
    protected TaskQueryService configureUserGroupQueryServiceDecorator(TaskQueryService queryService, UserGroupCallback userGroupCallback) {
        UserGroupTaskQueryServiceDecorator userGroupTaskQueryServiceDecorator = new UserGroupTaskQueryServiceDecorator();
        userGroupTaskQueryServiceDecorator.setPm(pm);
        userGroupTaskQueryServiceDecorator.setUserGroupCallback(userGroupCallback);
        userGroupTaskQueryServiceDecorator.setDelegate(queryService);
        return userGroupTaskQueryServiceDecorator;
        
    }
    
    protected TaskInstanceService configureUserGroupTaskInstanceServiceDecorator(TaskInstanceService instanceService, UserGroupCallback userGroupCallback) {
        UserGroupTaskInstanceServiceDecorator userGroupTaskInstanceDecorator = new UserGroupTaskInstanceServiceDecorator();
        userGroupTaskInstanceDecorator.setPm(pm);
        userGroupTaskInstanceDecorator.setUserGroupCallback(userGroupCallback);
        userGroupTaskInstanceDecorator.setDelegate(instanceService);
        return userGroupTaskInstanceDecorator;
    }
    
    protected SubTaskDecorator createSubTaskDecorator(JbpmServicesPersistenceManager pm,
            TaskInstanceService instanceService, TaskQueryService queryService) {
        SubTaskDecorator subTaskDecorator = new SubTaskDecorator();
        subTaskDecorator.setPm(pm);
        subTaskDecorator.setInstanceService(instanceService);
        subTaskDecorator.setQueryService(queryService);
        return subTaskDecorator;
    }

    protected DeadlinesDecorator createDeadlinesDecorator(JbpmServicesPersistenceManager pm, TaskQueryService queryService,
            TaskDeadlinesService deadlinesService,
            SubTaskDecorator subTaskDecorator) {
        DeadlinesDecorator deadlinesDecorator = new DeadlinesDecorator();
        deadlinesDecorator.setPm(pm);
        deadlinesDecorator.setQueryService(queryService);
        deadlinesDecorator.setDeadlineService(deadlinesService);
        deadlinesDecorator.setInstanceService(subTaskDecorator);
        return deadlinesDecorator;

    }    
}
