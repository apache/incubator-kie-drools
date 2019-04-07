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

package org.jbpm.kie.services.impl.query.preprocessor;

import static org.dashbuilder.dataset.filter.FilterFactory.AND;
import static org.dashbuilder.dataset.filter.FilterFactory.OR;
import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.filter.FilterFactory.isNull;
import static org.dashbuilder.dataset.filter.FilterFactory.notEqualsTo;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_ACTUALOWNER;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_EXCLUDED_OWNER;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_ORGANIZATIONAL_ENTITY;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.identity.IdentityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PotOwnerTasksPreprocessor extends UserTasksPreprocessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PotOwnerTasksPreprocessor.class);

    private IdentityProvider identityProvider;

    private UserGroupCallback userGroupCallback;

    public PotOwnerTasksPreprocessor(IdentityProvider identityProvider,
                                     UserGroupCallback userGroupCallback,
                                     DataSetMetadata metadata) {
        super(metadata);
        this.identityProvider = identityProvider;
        this.userGroupCallback = userGroupCallback;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void preprocess(DataSetLookup lookup) {
        if (identityProvider == null || userGroupCallback == null) {
            return;
        }

        final List<String> groupsForUser = Optional.ofNullable(userGroupCallback.getGroupsForUser(identityProvider.getName())).orElse(new ArrayList<>());
        final List<Comparable> orgEntities = new ArrayList<>(groupsForUser);
        orgEntities.add(identityProvider.getName());

        final ColumnFilter myGroupFilter = AND(
                equalsTo(COLUMN_ORGANIZATIONAL_ENTITY, orgEntities),
                OR(equalsTo(COLUMN_ACTUALOWNER, ""), isNull(COLUMN_ACTUALOWNER)));

        final ColumnFilter columnFilter = AND(
                OR(isNull(COLUMN_EXCLUDED_OWNER), notEqualsTo(COLUMN_EXCLUDED_OWNER, identityProvider.getName())),
                OR(myGroupFilter, equalsTo(COLUMN_ACTUALOWNER, identityProvider.getName())));

        LOGGER.debug("Adding column filter: {}", columnFilter);

        if (lookup.getFirstFilterOp() != null) {
            lookup.getFirstFilterOp().addFilterColumn(columnFilter);
        } else {
            DataSetFilter filter = new DataSetFilter();
            filter.addFilterColumn(columnFilter);
            lookup.addOperation(filter);
        }

        super.preprocess(lookup);
    }

}
