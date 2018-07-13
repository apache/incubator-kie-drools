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

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.filter.FilterFactory.in;

import java.util.List;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.def.DataSetPreprocessor;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.jbpm.kie.services.impl.security.DeploymentRolesManager;
import org.kie.internal.identity.IdentityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploymentIdsPreprocessor implements DataSetPreprocessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentIdsPreprocessor.class);

    private DeploymentRolesManager deploymentRolesManager;
    private IdentityProvider identityProvider;
    private String columnId;
    private String impossibleConditionColumnId;

    public DeploymentIdsPreprocessor(DeploymentRolesManager deploymentRolesManager,
                                     IdentityProvider identityProvider,
                                     String columnId,
                                     String impossibleConditionColumnId) {
        this.deploymentRolesManager = deploymentRolesManager;
        this.identityProvider = identityProvider;
        this.columnId = columnId;
        this.impossibleConditionColumnId = impossibleConditionColumnId;
    }

    @Override
    public void preprocess(DataSetLookup lookup) {
        if (identityProvider == null) {
            return;
        }

        final List<String> deploymentIds = deploymentRolesManager.getDeploymentsForUser(identityProvider);
        ColumnFilter columnFilter;
        if(deploymentIds != null  && !deploymentIds.isEmpty()){
            columnFilter = in(columnId, deploymentIds);
        }  else {
            columnFilter = equalsTo(impossibleConditionColumnId,-1);
        }

        LOGGER.debug("Adding column filter: {}", columnFilter);

        if (lookup.getFirstFilterOp() != null) {
            lookup.getFirstFilterOp().addFilterColumn(columnFilter);
        } else {
            DataSetFilter filter = new DataSetFilter();
            filter.addFilterColumn(columnFilter);
            lookup.addOperation(filter);
        }
    }
}
