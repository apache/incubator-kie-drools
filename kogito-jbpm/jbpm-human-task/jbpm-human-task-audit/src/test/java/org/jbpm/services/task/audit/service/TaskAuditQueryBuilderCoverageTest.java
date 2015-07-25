package org.jbpm.services.task.audit.service;

import static org.jbpm.query.QueryBuilderCoverageTestUtil.hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath;
import static org.jbpm.query.QueryBuilderCoverageTestUtil.queryBuilderCoverageTest;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.persistence.correlation.JPACorrelationKeyFactory;
import org.jbpm.query.QueryBuilderCoverageTestUtil.ModuleSpecificInputFiller;
import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.jbpm.services.task.audit.BAMTaskSummaryQueryBuilder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.task.model.Status;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.task.api.model.TaskEvent.TaskEventType;
import org.kie.internal.task.query.AuditTaskQueryBuilder;
import org.kie.internal.task.query.TaskEventQueryBuilder;
import org.kie.internal.task.query.AuditTaskQueryBuilder.OrderBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class TaskAuditQueryBuilderCoverageTest extends HumanTaskServicesBaseTest {
   
    private static final Logger logger = LoggerFactory.getLogger(TaskAuditQueryBuilderCoverageTest.class);
   
    private static PoolingDataSource pds;
    private static EntityManagerFactory emf;
    
    private TaskJPAAuditService auditService;
    
    @BeforeClass
    public static void configure() { 
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory( "org.jbpm.services.task" );

        hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath();
    }
  
    @Before
    public void setup() { 
        auditService = new TaskJPAAuditService(emf);
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
       
        private final JPACorrelationKeyFactory correlationKeyFactory = new JPACorrelationKeyFactory();
       
        private int auditTaskOrderByType = 0;
        private int taskEventOrderByType = 0;
        private int bamTaskSummaryOrderByType = 0;
        
        @Override
        public Object fillInput( Class type ) {
            if( type.equals(CorrelationKey.class) ) { 
                return correlationKeyFactory.newCorrelationKey("business-key");
            } if( type.equals(AuditTaskQueryBuilder.OrderBy.class) ) { 
               return ( auditTaskOrderByType++ % 2 == 0 ? 
                       AuditTaskQueryBuilder.OrderBy.processId 
                       : AuditTaskQueryBuilder.OrderBy.processInstanceId );
            } else if( type.equals(TaskEventQueryBuilder.OrderBy.class) ) { 
               int typeCase = taskEventOrderByType++ % 3;
               switch(typeCase) { 
               case 0: 
                   return TaskEventQueryBuilder.OrderBy.logTime;
               case 1:
                   return TaskEventQueryBuilder.OrderBy.processInstanceId;
               case 2:
                   return TaskEventQueryBuilder.OrderBy.taskId;
               }
            } else if( type.equals(BAMTaskSummaryQueryBuilder.OrderBy.class) ) { 
                int typeCase = bamTaskSummaryOrderByType++ % 6;
                switch(typeCase) { 
                case 0: 
                    return BAMTaskSummaryQueryBuilder.OrderBy.createdDate;
                case 1:
                    return BAMTaskSummaryQueryBuilder.OrderBy.endDate;
                case 2:
                    return BAMTaskSummaryQueryBuilder.OrderBy.processInstanceId;
                case 3: 
                    return BAMTaskSummaryQueryBuilder.OrderBy.startDate;
                case 4: 
                    return BAMTaskSummaryQueryBuilder.OrderBy.taskId;
                case 5: 
                    return BAMTaskSummaryQueryBuilder.OrderBy.taskName;
                }
            }  else if( type.isArray() ) {  
                Class elemType = type.getComponentType();
                if( elemType.equals(CorrelationKey.class) ) { 
                    CorrelationKey [] corrKeyArr = { 
                            correlationKeyFactory.newCorrelationKey("key:one"),
                            correlationKeyFactory.newCorrelationKey("key:two")
                    };
                    return corrKeyArr;
                } else if( elemType.equals(TaskEventType.class) ) { 
                    TaskEventType [] typeArr = { 
                            TaskEventType.ACTIVATED,
                            TaskEventType.ADDED,
                            TaskEventType.CLAIMED
                    };
                    return typeArr;
                } else if( elemType.equals(Status.class) ) { 
                    Status [] statusArr = { Status.Completed, Status.Suspended };
                    return statusArr;
                }
            }
            return null;
        }
    };

    @Test
    public void auditTaskQueryBuilderCoverageTest() { 
       AuditTaskQueryBuilder queryBuilder = auditService.auditTaskQuery();
       Class builderClass = AuditTaskQueryBuilder.class;
       
       queryBuilderCoverageTest(queryBuilder, builderClass, inputFiller);
    }
    
    @Test
    public void taskEventQueryBuilderCoverageTest() { 
       TaskEventQueryBuilder queryBuilder = auditService.taskEventQuery();
       Class builderClass = TaskEventQueryBuilder.class;
       
       queryBuilderCoverageTest(queryBuilder, builderClass, inputFiller);
    }

    @Test
    public void bamTaskSummaryQueryBuilderCoverageTest() { 
       BAMTaskSummaryQueryBuilder queryBuilder = auditService.bamTaskSummaryQuery();
       Class builderClass = BAMTaskSummaryQueryBuilder.class;
       
       queryBuilderCoverageTest(queryBuilder, builderClass, inputFiller);
    }
}
