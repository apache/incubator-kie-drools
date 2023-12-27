/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.app.audit.jpa.queries;

import java.util.List;

import org.kie.kogito.app.audit.graphql.type.ProcessInstanceErrorTO;
import org.kie.kogito.app.audit.graphql.type.ProcessInstanceNodeTO;
import org.kie.kogito.app.audit.graphql.type.ProcessInstanceStateTO;
import org.kie.kogito.app.audit.graphql.type.ProcessInstanceVariableHistoryTO;
import org.kie.kogito.app.audit.graphql.type.ProcessInstanceVariableTO;
import org.kie.kogito.app.audit.jpa.queries.mapper.ProcessInstanceStateTOMapper;
import org.kie.kogito.app.audit.jpa.queries.mapper.ProcessInstanceVariableHistoryTOMapper;
import org.kie.kogito.app.audit.spi.GraphQLSchemaQuery;
import org.kie.kogito.app.audit.spi.GraphQLSchemaQueryProvider;

public class JPAGraphQLSchemaProcessInstancesQueryProvider implements GraphQLSchemaQueryProvider {

    @Override
    public List<GraphQLSchemaQuery<?>> queries() {
        return List.<GraphQLSchemaQuery<?>> of(
                new JPAComplexNamedQuery<ProcessInstanceStateTO, Object[]>("GetAllProcessInstancesState", new ProcessInstanceStateTOMapper()),
                new JPAComplexNamedQuery<ProcessInstanceStateTO, Object[]>("GetAllProcessInstancesStateByStatus", new ProcessInstanceStateTOMapper()),
                new JPAComplexNamedQuery<ProcessInstanceStateTO, Object[]>("GetAllProcessInstancesStateByProcessId", new ProcessInstanceStateTOMapper()),
                new JPAComplexNamedQuery<ProcessInstanceStateTO, Object[]>("GetProcessInstancesStateHistory", new ProcessInstanceStateTOMapper()),
                new JPAComplexNamedQuery<ProcessInstanceStateTO, Object[]>("GetProcessInstancesStateHistoryByBusinessKey", new ProcessInstanceStateTOMapper()),
                new JPASimpleNamedQuery<ProcessInstanceNodeTO>("GetAllProcessInstancesNodeByProcessInstanceId", ProcessInstanceNodeTO.class),
                new JPASimpleNamedQuery<ProcessInstanceErrorTO>("GetAllProcessInstancesErrorByProcessInstanceId", ProcessInstanceErrorTO.class),
                new JPASimpleNamedQuery<ProcessInstanceVariableTO>("GetAllProcessInstancesVariableByProcessInstanceId", ProcessInstanceVariableTO.class),
                new JPAComplexNamedQuery<ProcessInstanceVariableHistoryTO, Object[]>("GetAllProcessInstancesVariableHistoryByProcessInstanceId",
                        new ProcessInstanceVariableHistoryTOMapper()));

    }

}
