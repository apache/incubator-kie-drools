package org.jbpm.services.task.query;

import static org.jbpm.query.QueryBuilderCoverageTestUtil.hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath;
import static org.jbpm.query.QueryBuilderCoverageTestUtil.queryBuilderCoverageTest;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jbpm.query.QueryBuilderCoverageTestUtil.ModuleSpecificInputFiller;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.jbpm.services.task.impl.model.PeopleAssignmentsImpl_;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.impl.model.TaskImpl_;
import org.jbpm.services.task.impl.model.UserImpl_;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.task.model.Status;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.kie.internal.task.api.model.TaskEvent.TaskEventType;
import org.kie.internal.task.query.TaskQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class TaskQueryBuilderCoverageTest extends HumanTaskServicesBaseTest {
   
    private static final Logger logger = LoggerFactory.getLogger(TaskQueryBuilderCoverageTest.class);
   
    private static PoolingDataSource pds;
    private static EntityManagerFactory emf;
    private InternalTaskService taskService;
    
    @BeforeClass
    public static void configure() { 
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory( "org.jbpm.services.task" );
        
        hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath();
    }
  
    @Before
    public void setup() { 
        this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
                                                .entityManagerFactory(emf)
                                                .getTaskService();
    }
    
    @AfterClass
    public static void reset() { 
        if( emf != null ) { 
            emf.close();
            emf = null;
        }
        if( pds != null ) { 
            pds.close();
            pds = null;
        }
    }

    private static ModuleSpecificInputFiller inputFiller = new ModuleSpecificInputFiller() {
       
        private int taskQueryOrderBy = 0;
        
        @Override
        public Object fillInput( Class type ) {
            if( type.equals(TaskQueryBuilder.OrderBy.class) ) { 
                int typeCase = taskQueryOrderBy++ % 6;
                switch(typeCase) { 
                case 0: 
                    return TaskQueryBuilder.OrderBy.createdBy;
                case 1:
                    return TaskQueryBuilder.OrderBy.createdOn;
                case 2:
                    return TaskQueryBuilder.OrderBy.processInstanceId;
                case 3: 
                    return TaskQueryBuilder.OrderBy.taskId;
                case 4: 
                    return TaskQueryBuilder.OrderBy.taskName;
                case 5: 
                    return TaskQueryBuilder.OrderBy.taskStatus;
                }
            }  else if( type.isArray() ) {  
                Class elemType = type.getComponentType();
                if( elemType.equals(TaskEventType.class) ) { 
                    TaskEventType [] typeArr = { 
                            TaskEventType.ACTIVATED,
                            TaskEventType.ADDED,
                            TaskEventType.CLAIMED
                    };
                    return typeArr;
                } else if( elemType.equals(Status.class) ) { 
                    Status [] statusArr = { Status.Completed, Status.Suspended };
                    return statusArr;
                } else if( elemType.equals(SubTasksStrategy.class) ) { 
                    SubTasksStrategy [] strategyArr = { SubTasksStrategy.EndParentOnAllSubTasksEnd, SubTasksStrategy.NoAction };
                    return strategyArr;
                }
            }
            return null;
        }
    };

    @Test
    public void taskQueryBuilderCoverageTest() { 
       TaskQueryBuilder queryBuilder = taskService.taskQuery("userId");
       Class builderClass = TaskQueryBuilder.class;
       
       queryBuilderCoverageTest(queryBuilder, builderClass, inputFiller);
    }
    
}