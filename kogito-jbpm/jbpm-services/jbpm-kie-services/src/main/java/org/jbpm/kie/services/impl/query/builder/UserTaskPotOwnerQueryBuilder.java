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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.jbpm.runtime.manager.impl.identity.UserDataServiceProvider;
import org.jbpm.services.api.query.QueryParamBuilder;
import org.kie.api.task.UserGroupCallback;

public class UserTaskPotOwnerQueryBuilder implements QueryParamBuilder<ColumnFilter>{

    private Map<String, Object> parameters;
    private boolean built = false;
    
    public UserTaskPotOwnerQueryBuilder(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public ColumnFilter build() {
        // return null if it was already invoked
        if (built) {
            return null;
        }
         
        String columnName = "potOwner";
        UserGroupCallback userGroupCallback = UserDataServiceProvider.getUserGroupCallback();
        List<String> potOwners = (List<String>)parameters.get(columnName);
        List<String> potOwnersList = new ArrayList<String>();
        for(String potOwner: potOwners) {
            potOwnersList.add(potOwner);
            
            List<String> groups = userGroupCallback.getGroupsForUser(potOwner);
            
            if(groups != null) {
                potOwnersList.addAll(groups);
            }
        }
        
        
        ColumnFilter filter = FilterFactory.AND(
                              FilterFactory.in(columnName,potOwnersList));
        filter.setColumnId(columnName);
        
        built = true;
        return filter;
    }    
}
