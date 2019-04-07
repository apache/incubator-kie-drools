/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.kie.services.impl.query.preprocessor;

import static org.dashbuilder.dataset.filter.FilterFactory.AND;
import static org.dashbuilder.dataset.filter.FilterFactory.in;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_POTOWNER;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.identity.IdentityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAndGroupsTasksPreprocessor extends UserTasksPreprocessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAndGroupsTasksPreprocessor.class);

    private IdentityProvider identityProvider;

    private UserGroupCallback userGroupCallback;

    private String columnId;

    public UserAndGroupsTasksPreprocessor(IdentityProvider identityProvider, UserGroupCallback userGroupCallback, 
                                          String columnId, DataSetMetadata metadata) {
        super(metadata);
        this.identityProvider = identityProvider;
        this.userGroupCallback = userGroupCallback;
        this.columnId = columnId;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void preprocess(DataSetLookup lookup) {
        if (identityProvider == null || userGroupCallback == null) {
            return;
        }

        ColumnFilter columnFilter;
        List<Comparable> orgEntities = new ArrayList<>();

        if (lookup.getFirstFilterOp() != null) {
            List<String> potOwners = new ArrayList<String>();
            List<ColumnFilter> columnFilters = lookup.getFirstFilterOp().getColumnFilterList();
            Iterator<ColumnFilter> it= columnFilters.iterator();
            while (orgEntities.isEmpty() && it.hasNext()) {
                CoreFunctionFilter column = (CoreFunctionFilter)it.next();

                if (column.getColumnId().toUpperCase().equals(columnId)) {
                    potOwners.addAll(column.getParameters());

                    for (String potOwner : potOwners) {
                        addUserAndGroupsFromIdentityProvider(orgEntities, potOwner);
                    }

                    it.remove();
                }
            }

            if (orgEntities.isEmpty()) {
                addUserAndGroupsFromIdentityProvider(orgEntities, identityProvider.getName());
            }

            columnFilter = AND(in(COLUMN_POTOWNER, orgEntities));
            lookup.getFirstFilterOp().addFilterColumn(columnFilter);
        } else {
            DataSetFilter filter = new DataSetFilter();
            addUserAndGroupsFromIdentityProvider(orgEntities, identityProvider.getName());

            columnFilter = AND(in(COLUMN_POTOWNER, orgEntities));
            filter.addFilterColumn(columnFilter);
            lookup.addOperation(filter);
        }

        LOGGER.debug("Adding column filter: {}", columnFilter);

        super.preprocess(lookup);
    }

    private void addUserAndGroupsFromIdentityProvider(List<Comparable> orgEntities, String userId) {
        orgEntities.addAll(Optional.ofNullable(userGroupCallback.getGroupsForUser(userId)).orElse(new ArrayList<>()));
        orgEntities.add(userId);
    }
}
