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

package org.jbpm.kie.services.impl.query;

import java.util.List;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.LogicalExprFilter;
import org.dashbuilder.dataset.filter.LogicalExprType;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.jbpm.services.api.query.QueryParamBuilder;
import org.jbpm.services.api.query.model.QueryParam;


public class CoreFunctionQueryParamBuilder implements QueryParamBuilder<Object> {

    private QueryParam[] filterParams;
    private int index = 0;
    
    public CoreFunctionQueryParamBuilder(QueryParam...filterParams) {
        this.filterParams = filterParams;
    }
    @Override
    public Object build() {
        if (filterParams.length == 0 || filterParams.length <= index) {
            return null;
        }
        QueryParam param = filterParams[index];
        index++;
        if ("group".equalsIgnoreCase(param.getOperator())) {
            // if operator is group consider it as group functions
            if (param.getValue().size() > 1) {
                return new GroupColumnFilter(param.getColumn(), (String)param.getValue().get(0), (String)param.getValue().get(1), (Integer)param.getValue().get(2));
            } else {
                return new GroupColumnFilter(param.getColumn(), (String)param.getValue().get(0), null, -1);
            }
        }
        // check core functions
        CoreFunctionType type = CoreFunctionType.getByName(param.getOperator());
        if (type != null) {
            return new CoreFunctionFilter(param.getColumn(), type, param.getValue());
        }
        LogicalExprType  logicalExprType = LogicalExprType.getByName(param.getOperator());
        if (logicalExprType != null) {
            return new LogicalExprFilter(param.getColumn(), logicalExprType, (List<ColumnFilter>)param.getValue());
        }
        // check aggregate functions
        AggregateFunctionType aggregationType = AggregateFunctionType.getByName(param.getOperator());
        
        if (aggregationType != null) {
            
            return new AggregateColumnFilter(aggregationType, param.getColumn(), (String)param.getValue().get(0));
        }

        return new ExtraColumnFilter(param.getColumn(), (String)param.getValue().get(0));
    }

}
