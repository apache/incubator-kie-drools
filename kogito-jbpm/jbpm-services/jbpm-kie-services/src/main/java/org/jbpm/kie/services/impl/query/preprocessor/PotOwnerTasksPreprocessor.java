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

package org.jbpm.kie.services.impl.query.preprocessor;

import static org.dashbuilder.dataset.filter.FilterFactory.OR;
import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_ACTUALOWNER;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_ORGANIZATIONAL_ENTITY;

import java.util.ArrayList;
import java.util.List;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.def.DataSetPreprocessor;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.kie.internal.identity.IdentityProvider;

public class PotOwnerTasksPreprocessor implements DataSetPreprocessor {

    private IdentityProvider identityProvider;
    
    
    public PotOwnerTasksPreprocessor(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }


    @SuppressWarnings("rawtypes")
    @Override
    public void preprocess(DataSetLookup lookup) {
        if (identityProvider == null) {
            return;
        }
        
        List<Comparable> orgEntities = new ArrayList<Comparable>(identityProvider.getRoles());
        orgEntities.add(identityProvider.getName());

        List<ColumnFilter> condList = new ArrayList<ColumnFilter>();
        
        condList.add(equalsTo(COLUMN_ACTUALOWNER, identityProvider.getName()));
        condList.add(equalsTo(COLUMN_ORGANIZATIONAL_ENTITY, orgEntities));
        
        if (lookup.getFirstFilterOp() != null) {
            lookup.getFirstFilterOp().addFilterColumn(OR(condList));
        } else {
            DataSetFilter filter = new DataSetFilter();
            filter.addFilterColumn(OR(condList));
            lookup.addOperation(filter);
        }

    }

}
