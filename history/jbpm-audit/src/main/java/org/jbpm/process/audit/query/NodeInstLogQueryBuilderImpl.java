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

import static org.kie.internal.query.QueryParameterIdentifiers.NODE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TYPE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.WORK_ITEM_ID_LIST;

import java.util.List;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.query.jpa.data.QueryWhere;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.internal.query.ParametrizedQuery;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder;

public class NodeInstLogQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<NodeInstanceLogQueryBuilder, NodeInstanceLog> implements NodeInstanceLogQueryBuilder {

    public NodeInstLogQueryBuilderImpl(CommandExecutor cmdService) { 
       super(cmdService);
    }
    
    public NodeInstLogQueryBuilderImpl(JPAAuditLogService jpaAuditService) { 
       super(jpaAuditService);
    }
    
    @Override
    public NodeInstanceLogQueryBuilder nodeInstanceId( String... nodeInstanceId ) {
        addObjectParameter(NODE_INSTANCE_ID_LIST, "node instance id", nodeInstanceId);
        return this;
    }

    @Override
    public NodeInstanceLogQueryBuilder nodeId( String... nodeId ) {
        addObjectParameter(NODE_ID_LIST, "node id", nodeId);
        return this;
    }

    @Override
    public NodeInstanceLogQueryBuilder nodeName( String... name ) {
        addObjectParameter(NODE_NAME_LIST, "node name", name);
        return this;
    }

    @Override
    public NodeInstanceLogQueryBuilder nodeType( String... type ) {
        addObjectParameter(TYPE_LIST, "node type", type);
        return this;
    }
    
    @Override
    public NodeInstanceLogQueryBuilder workItemId( long... workItemId ) {
        addLongParameter(WORK_ITEM_ID_LIST, "work item id", workItemId);
        return this;
    }

    @Override
    protected Class<NodeInstanceLog> getResultType() {
        return NodeInstanceLog.class;
    }

    @Override
    protected Class<org.jbpm.process.audit.NodeInstanceLog> getQueryType() {
        return org.jbpm.process.audit.NodeInstanceLog.class;
    }



}