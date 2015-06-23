/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.process.audit.query;

import static org.kie.internal.query.QueryParameterIdentifiers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jbpm.process.audit.JPAAuditLogService;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.internal.query.ParametrizedQuery;
import org.kie.internal.query.data.QueryData;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogQueryBuilder;

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
    public VariableInstanceLogQueryBuilder variableValue( String variableId, String value ) {
        if( queryData.isRange() ) { 
            throw new IllegalArgumentException("Range values are not supported for the .variableValue(..) method");
        }
        if( variableId == null ) { 
            throw new IllegalArgumentException("A null variable Id criteria is invalid." );
        }
        if( value == null ) { 
            throw new IllegalArgumentException("A null variable value criteria is invalid." );
        }
        String varValStr = variableId.length() + VAR_VAL_SEPARATOR + variableId + VAR_VAL_SEPARATOR + value;
        addObjectParameter(VAR_VALUE_ID_LIST, "value for variable", varValStr);
        return this;
    }
    
    @Override
    public VariableInstanceLogQueryBuilder externalId( String... externalId ) {
        addObjectParameter(EXTERNAL_ID_LIST, "external id", externalId);
        return this;
    }

    @Override
    public VariableInstanceLogQueryBuilder last() {
        List<? extends Object> params = queryData.getIntersectParameters().get(LAST_VARIABLE_LIST);
        if( params == null ) { 
           params = new ArrayList<Boolean>(Arrays.asList(Boolean.TRUE));
           queryData.getIntersectParameters().put(LAST_VARIABLE_LIST, params);
        }
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
                return getJpaAuditLogService().queryVariableInstanceLogs(queryData);
            }
        };
    }

}
