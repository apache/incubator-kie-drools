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

package org.jbpm.process.audit.query;

import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.process.audit.query.AuditQueryDataUtil.cleanDB;
import static org.jbpm.process.audit.query.AuditQueryDataUtil.createTestNodeInstanceLogData;
import static org.jbpm.process.audit.query.AuditQueryDataUtil.createTestProcessInstanceLogData;
import static org.jbpm.process.audit.query.AuditQueryDataUtil.createTestVariableInstanceLogData;
import static org.jbpm.query.QueryBuilderCoverageTestUtil.afterClass;
import static org.jbpm.query.QueryBuilderCoverageTestUtil.beforeClass;
import static org.jbpm.query.QueryBuilderCoverageTestUtil.queryBuilderCoverageTest;

import javax.persistence.EntityManagerFactory;

import org.jbpm.persistence.correlation.JPACorrelationKeyFactory;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.audit.strategy.StandaloneJtaStrategy;
import org.jbpm.query.QueryBuilderCoverageTestUtil.ModuleSpecificInputFiller;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.manager.audit.query.AuditLogQueryBuilder.OrderBy;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditQueryCoverageTest extends JPAAuditLogService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditQueryCoverageTest.class);
  
    private static EntityManagerFactory emf;
    
    private ProcessInstanceLog [] pilTestData;
    private VariableInstanceLog [] vilTestData;
    private NodeInstanceLog [] nilTestData;
   
    @BeforeClass
    public static void configure() { 
        emf = beforeClass(JBPM_PERSISTENCE_UNIT_NAME);
    }
    
    @AfterClass
    public static void reset() { 
        cleanDB(emf);
        afterClass();
    }

    @Before
    public void setUp() throws Exception {
        if( pilTestData == null ) { 
            // this is not really necessary.. 
            pilTestData = createTestProcessInstanceLogData(emf);
            vilTestData = createTestVariableInstanceLogData(emf);
            nilTestData = createTestNodeInstanceLogData(emf);
        }
        this.persistenceStrategy = new StandaloneJtaStrategy(emf);
    }
  
    private static ModuleSpecificInputFiller inputFiller = new ModuleSpecificInputFiller() {
       
        private final JPACorrelationKeyFactory correlationKeyFactory = new JPACorrelationKeyFactory();
       
        private int orderByType = 0;
        
        @Override
        public Object fillInput( Class type ) {
            if( type.equals(CorrelationKey.class) ) { 
                return correlationKeyFactory.newCorrelationKey("business-key");
            } else if( type.equals(OrderBy.class) ) { 
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
    public void processInstanceLogQueryCoverageTest() {
       ProcessInstanceLogQueryBuilder queryBuilder = this.processInstanceLogQuery(); 
       Class builderClass = ProcessInstanceLogQueryBuilder.class;
       
       queryBuilderCoverageTest(queryBuilder, builderClass, inputFiller);
    }
   
    @Test
    public void variableInstanceLogQueryBuilderCoverageTest() {
       VariableInstanceLogQueryBuilder queryBuilder = this.variableInstanceLogQuery();
       Class builderClass = VariableInstanceLogQueryBuilder.class;
       
       queryBuilderCoverageTest(queryBuilder, builderClass, inputFiller);
    }
    
    @Test
    public void nodeInstanceLogQueryBuilderCoverageTest() {
       NodeInstanceLogQueryBuilder queryBuilder = this.nodeInstanceLogQuery();
       Class builderClass = NodeInstanceLogQueryBuilder.class;
       
       queryBuilderCoverageTest(queryBuilder, builderClass, inputFiller);
    }
    
}