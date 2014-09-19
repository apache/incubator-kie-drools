package org.jbpm.process.audit.query;

import static org.kie.internal.query.QueryParameterIdentifiers.OLD_VALUE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.VALUE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.VARIABLE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.VARIABLE_INSTANCE_ID_LIST;

import java.util.List;

import org.jbpm.process.audit.JPAAuditLogService;
import org.kie.api.query.ParametrizedQuery;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.manager.audit.query.VariableInstanceLogQueryBuilder;
import org.kie.internal.query.data.QueryData;

public class VarInstLogQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<VariableInstanceLogQueryBuilder> implements VariableInstanceLogQueryBuilder {

    public VarInstLogQueryBuilderImpl(CommandExecutor cmdExecutor ) {
        super(cmdExecutor);
    }
  
    public VarInstLogQueryBuilderImpl(JPAAuditLogService jpaAuditService) { 
       super(jpaAuditService);
    }
    
    @Override
    public VariableInstanceLogQueryBuilder variableInstanceId( String... variableInstanceId ) {
        addObjectParameter(VARIABLE_INSTANCE_ID_LIST, "variable instance id", variableInstanceId);
        return this;
    }

    @Override
    public VariableInstanceLogQueryBuilder variableId( String... variableId ) {
        addObjectParameter(VARIABLE_ID_LIST, "variable id", variableId);
        return this;
    }

    @Override
    public VariableInstanceLogQueryBuilder value( String... value ) {
        addObjectParameter(VALUE_LIST, "value", value);
        return this;
    }

    @Override
    public VariableInstanceLogQueryBuilder oldValue( String... oldVvalue ) {
        addObjectParameter(OLD_VALUE_LIST, "old value", oldVvalue);
        return this;
    }

    @Override
    public VariableInstanceLogQueryBuilder orderBy( OrderBy field ) {
        this.queryData.getQueryContext().setOrderBy(field.toString());
        return this;
    }
    
    @Override
    public ParametrizedQuery<VariableInstanceLog> buildQuery() {
        return new ParametrizedQuery<VariableInstanceLog>() {
            private QueryData queryData = new QueryData(getQueryData()); 
            @Override
            public List<VariableInstanceLog> getResultList() {
                List<org.jbpm.process.audit.VariableInstanceLog> internalResult 
                    = getJpaAuditLogService().queryVariableInstanceLogs(queryData);
                return convertListToInterfaceList(internalResult, VariableInstanceLog.class);
            }
        };
    }

}
