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
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.assertNotNull;
import static org.kie.api.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TYPE_LIST;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.AuditQueryCriteriaUtil;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.query.jpa.data.QueryWhere;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditQueryCriteriaUtilTest {

    private static HashMap<String, Object> context;
    private static EntityManagerFactory emf;
    private static AuditQueryCriteriaUtil util;
    private static JPAAuditLogService auditLogService;

    private static final Logger logger = LoggerFactory.getLogger(AuditQueryCriteriaUtilTest.class);
   
    @BeforeClass
    public static void configure() { 
        AbstractBaseTest.hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath();
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);
        auditLogService = new JPAAuditLogService(emf);
        util = new AuditQueryCriteriaUtil(auditLogService);
    }
    
    @AfterClass
    public static void reset() { 
        cleanUp(context);
    }

    @Test
    public void auditQueryCriteriaWhereTest() { 
        QueryWhere where = new QueryWhere();
        
        // OR 
        where.setToUnion();
        where.addParameter(NODE_ID_LIST, "node.id");
        where.addParameter(NODE_INSTANCE_ID_LIST, "node-inst");
        where.addParameter(TYPE_LIST, "type");
        
        // OR ( 
        where.newGroup();
        where.setToLike();
        where.addParameter(NODE_NAME_LIST, "n*ends.X" );
        where.setToNormal();
        where.setToIntersection();
        where.addParameter(TYPE_LIST, "oneOf3", "twoOf3", "thrOf3" );
        where.endGroup();
        
        where.setToIntersection();
        where.addRangeParameter(PROCESS_INSTANCE_ID_LIST, 0l, true);
        where.addRangeParameter(PROCESS_INSTANCE_ID_LIST, 10l, false);
        where.addParameter(PROCESS_ID_LIST, "org.process.id");
        
        List<NodeInstanceLog> result = util.doCriteriaQuery(where, NodeInstanceLog.class);
        assertNotNull( "Null result from 1rst query.", result );
    }

    @Test
    public void auditQueryCriteriaMetaTest() { 
        QueryWhere where = new QueryWhere();
        
        where.setAscending(QueryParameterIdentifiers.NODE_INSTANCE_ID_LIST); 
        where.setCount(10);
        where.setOffset(2);
        
        List<NodeInstanceLog> result = util.doCriteriaQuery(where, NodeInstanceLog.class);
        assertNotNull( "Null result from 1rst query.", result );
    }
}
