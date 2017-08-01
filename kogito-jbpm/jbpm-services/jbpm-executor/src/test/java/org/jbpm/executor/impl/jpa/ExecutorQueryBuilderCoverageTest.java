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

package org.jbpm.executor.impl.jpa;

import static org.jbpm.query.QueryBuilderCoverageTestUtil.hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath;
import static org.jbpm.query.QueryBuilderCoverageTestUtil.queryBuilderCoverageTest;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.query.QueryBuilderCoverageTestUtil.ModuleSpecificInputFiller;
import org.jbpm.test.util.ExecutorTestUtil;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.executor.STATUS;
import org.kie.internal.runtime.manager.audit.query.ErrorInfoQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.RequestInfoQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorQueryBuilderCoverageTest {
   
    private static final Logger logger = LoggerFactory.getLogger(ExecutorQueryBuilderCoverageTest.class);
    

    private static PoolingDataSource pds;
    private static EntityManagerFactory emf;
    
    private ExecutorJPAAuditService auditService;
    
    @BeforeClass
    public static void configure() { 
        pds = ExecutorTestUtil.setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");
        hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath();
    }
        
    @Before
    public void setup() { 
        auditService = new ExecutorJPAAuditService(emf);
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
      
        private int errorInfoOrderByType = 0;
        private int requestInfoOrderByType = 0;
        
        @Override
        public Object fillInput( Class type ) {
            if( ErrorInfoQueryBuilder.OrderBy.class.equals(type) ) { 
               return ( errorInfoOrderByType++ % 2 == 0 ?
                       ErrorInfoQueryBuilder.OrderBy.id
                       : ErrorInfoQueryBuilder.OrderBy.time );
            } else if( RequestInfoQueryBuilder.OrderBy.class.equals(type) ) { 
               switch(requestInfoOrderByType++ % 6)  { 
               case 0:
                   return RequestInfoQueryBuilder.OrderBy.deploymentId;
               case 1:
                   return RequestInfoQueryBuilder.OrderBy.executions;
               case 2:
                   return RequestInfoQueryBuilder.OrderBy.id;
               case 3:
                   return RequestInfoQueryBuilder.OrderBy.retries;
               case 4:
                   return RequestInfoQueryBuilder.OrderBy.status;
               case 5:
                   return RequestInfoQueryBuilder.OrderBy.time;
               }
            } else if( type.isArray() ) { 
                Class elemType = type.getComponentType();
                if( STATUS.class.equals(elemType) ) { 
                   STATUS [] statusArr = { 
                           STATUS.DONE, 
                           STATUS.CANCELLED, 
                           STATUS.ERROR
                   };
                   return statusArr;
                }
            }
           return null; 
        }
    };

    @Test
    public void errorInfoQueryBuilderCoverageTest() { 
       ErrorInfoQueryBuilder queryBuilder = auditService.errorInfoQueryBuilder();
       Class builderClass = ErrorInfoQueryBuilder.class;
       
       queryBuilderCoverageTest(queryBuilder, builderClass, inputFiller);
    }
    
    @Test
    public void requestInfoQueryBuilderCoverageTest() { 
       RequestInfoQueryBuilder queryBuilder = auditService.requestInfoQueryBuilder();
       Class builderClass = RequestInfoQueryBuilder.class;
       
       queryBuilderCoverageTest(queryBuilder, builderClass, inputFiller);
    }
    
}
