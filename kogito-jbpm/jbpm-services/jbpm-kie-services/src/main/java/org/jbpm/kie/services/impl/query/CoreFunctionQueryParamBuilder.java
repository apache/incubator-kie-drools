/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.kie.services.impl.query;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.jbpm.services.api.query.QueryParamBuilder;
import org.jbpm.services.api.query.model.QueryParam;


public class CoreFunctionQueryParamBuilder implements QueryParamBuilder<ColumnFilter> {

    private QueryParam[] filterParams;
    private int index = 0;
    
    public CoreFunctionQueryParamBuilder(QueryParam...filterParams) {
        this.filterParams = filterParams;
    }
    @Override
    public ColumnFilter build() {
        if (filterParams.length == 0 || filterParams.length <= index) {
            return null;
        }
        QueryParam param = filterParams[index];
        index++;
        
        CoreFunctionType type = CoreFunctionType.getByName(param.getOperator());
        if (type == null) {
            throw new IllegalArgumentException("Not supported core function type - " + param.getOperator());
        }
        return new CoreFunctionFilter(param.getColumn(), type, param.getValue());
    }

}
