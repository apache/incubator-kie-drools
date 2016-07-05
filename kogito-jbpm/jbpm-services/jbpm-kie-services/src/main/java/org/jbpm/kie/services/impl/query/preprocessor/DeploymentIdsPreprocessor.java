/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.kie.services.impl.query.preprocessor;

import static org.dashbuilder.dataset.filter.FilterFactory.in;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_EXTERNALID;

import java.util.List;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.def.DataSetPreprocessor;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.jbpm.kie.services.impl.security.DeploymentRolesManager;
import org.kie.internal.identity.IdentityProvider;

public class DeploymentIdsPreprocessor implements DataSetPreprocessor {

    private DeploymentRolesManager deploymentRolesManager;

    private IdentityProvider identityProvider;

    public DeploymentIdsPreprocessor(DeploymentRolesManager deploymentRolesManager, IdentityProvider identityProvider) {
        this.deploymentRolesManager = deploymentRolesManager;
        this.identityProvider = identityProvider;
    }

    @Override
    public void preprocess(DataSetLookup lookup) {
        if (identityProvider == null) {
            return;
        }
        List<String> deploymentIds = deploymentRolesManager.getDeploymentsForUser(identityProvider);
                
        
        if (lookup.getFirstFilterOp() != null) {
            lookup.getFirstFilterOp().addFilterColumn(in(COLUMN_EXTERNALID, deploymentIds));
        } else {
            DataSetFilter filter = new DataSetFilter();
            filter.addFilterColumn(in(COLUMN_EXTERNALID, deploymentIds));
            lookup.addOperation(filter);
        }
    }
}
