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

package org.jbpm.kie.services.impl.query.builder;

import java.util.Map;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.jbpm.services.api.query.QueryParamBuilder;
import org.jbpm.services.api.query.QueryParamBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserTaskPotOwnerQueryBuilderFactory implements QueryParamBuilderFactory{

    @Override
    public boolean accept(String identifier) {
        
        if ("potOwnerBuilder".equalsIgnoreCase(identifier)) {   
            return true;
        }
        return false;
    }

    @Override
    public QueryParamBuilder<ColumnFilter> newInstance(Map<String, Object> parameters) {
        return new UserTaskPotOwnerQueryBuilder(parameters);
    }

}
