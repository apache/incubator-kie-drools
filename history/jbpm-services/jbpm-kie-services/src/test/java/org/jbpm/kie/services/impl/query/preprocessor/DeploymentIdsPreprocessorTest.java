/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Arrays;

import org.dashbuilder.dataset.DataSetLookup;
import org.jbpm.kie.services.impl.security.DeploymentRolesManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.internal.identity.IdentityProvider;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentIdsPreprocessorTest {
    private static String COL_ID = "DEPLOYMENTID";
    private static String FAIL_COL_ID = "TASKID";

    @Mock
    IdentityProvider identityProvider;

    @Mock
    DeploymentRolesManager deploymentRolesManager;

    DataSetLookup dataSetLookup;

    DeploymentIdsPreprocessor preprocessor;

    @Before
    public void init() {
        preprocessor = new  DeploymentIdsPreprocessor(deploymentRolesManager,
                                  identityProvider,
                                  COL_ID,
                                  FAIL_COL_ID);
    }

    @Test
    public void testQueryWithUserAvailableDeployments() {
        dataSetLookup = spy(new DataSetLookup());
        String deploymentId1 = "deployment1";
        String deploymentId2 = "deployment2";

        when(deploymentRolesManager.getDeploymentsForUser(identityProvider)).thenReturn( Arrays.asList(deploymentId1,deploymentId2));

        preprocessor.preprocess(dataSetLookup);

        assertEquals("DEPLOYMENTID in deployment1, deployment2",
                     dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }

    @Test
    public void testQueryWithoutUserAvailableDeployments() {
        dataSetLookup = spy(new DataSetLookup());

        when(deploymentRolesManager.getDeploymentsForUser(identityProvider)).thenReturn(new ArrayList<>());

        preprocessor.preprocess(dataSetLookup);

        assertEquals("TASKID = -1",
                     dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }


}
