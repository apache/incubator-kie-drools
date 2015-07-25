package org.jbpm.services.task.audit.service;

import static org.jbpm.query.QueryBuilderCoverageTestUtil.hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath;
import static org.jbpm.query.QueryBuilderCoverageTestUtil.queryBuilderCoverageTest;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.persistence.correlation.JPACorrelationKeyFactory;
import org.jbpm.query.QueryBuilderCoverageTestUtil.ModuleSpecificInputFiller;
import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.manager.audit.query.AuditQueryBuilder.OrderBy;
import org.kie.internal.runtime.manager.audit.query.AuditTaskQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class AuditTaskQueryBuilderCoverageTest extends HumanTaskServicesBaseTest {
   
    private static final Logger logger = LoggerFactory.getLogger(AuditTaskQueryBuilderCoverageTest.class);
   
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
       
        private int orderByType = 0;
        
        @Override
        public Object fillInput( Class type ) {
            if( type.equals(CorrelationKey.class) ) { 
                return correlationKeyFactory.newCorrelationKey("business-key");
            } if( type.equals(OrderBy.class) ) { 
               return ( orderByType++ % 2 == 0 ? 
                       OrderBy.processId 
                       : OrderBy.processInstanceId );
            }  else if( type.isArray() ) {  
                CorrelationKey [] corrKeyArr = { 
                        correlationKeyFactory.newCorrelationKey("key:one"),
                        correlationKeyFactory.newCorrelationKey("key:two")
                };
                return corrKeyArr;
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
}
