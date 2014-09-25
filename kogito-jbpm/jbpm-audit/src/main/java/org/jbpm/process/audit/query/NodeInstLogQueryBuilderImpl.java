package org.jbpm.process.audit.query;

import static org.kie.internal.query.QueryParameterIdentifiers.NODE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_TYPE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.WORK_ITEM_ID_LIST;

import java.util.List;

import org.jbpm.process.audit.JPAAuditLogService;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.internal.query.ParametrizedQuery;
import org.kie.internal.query.data.QueryData;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder;

public class NodeInstLogQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<NodeInstanceLogQueryBuilder> implements NodeInstanceLogQueryBuilder {

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
        addObjectParameter(NODE_TYPE_LIST, "node type", type);
        return this;
    }
    
    @Override
    public NodeInstanceLogQueryBuilder workItemId( long... workItemId ) {
        addLongParameter(WORK_ITEM_ID_LIST, "work item id", workItemId);
        return this;
    }

    @Override
    public NodeInstanceLogQueryBuilder orderBy( OrderBy field ) {
        this.queryData.getQueryContext().setOrderBy(field.toString());
        return this;
    }
    
    @Override
    public ParametrizedQuery<NodeInstanceLog> buildQuery() {
        return new ParametrizedQuery<NodeInstanceLog>() {
            private QueryData queryData = new QueryData(getQueryData()); 
            @Override
            public List<NodeInstanceLog> getResultList() {
                return getJpaAuditLogService().queryNodeInstanceLogs(queryData);
            }
        };
    }

}