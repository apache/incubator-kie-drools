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

import java.util.Arrays;
import java.util.Collections;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.internal.identity.IdentityProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PotOwnerTasksPreprocessorTest {

    @Mock
    IdentityProvider identityProvider;
    
    @Mock
    DataSetMetadata metaData;

    DataSetLookup dataSetLookup;

    @InjectMocks
    PotOwnerTasksPreprocessor potOwnerTasksPreprocessor;

    @Before
    public void init() {
        dataSetLookup = new DataSetLookup();
    }

    @Test
    public void testSetUser() {
        String role1 = "role1";
        String role2 = "role2";
        String userId = "userId";

        when(identityProvider.getRoles()).thenReturn(Arrays.asList(role1, role2));
        when(identityProvider.getName()).thenReturn(userId);

        potOwnerTasksPreprocessor.preprocess(dataSetLookup);

        assertEquals("((ENTITY_ID is_null  OR ENTITY_ID != " + userId + ") AND ((ID = " + role1 + ", " + role2 + ", " + userId + " AND (ACTUALOWNER =  OR ACTUALOWNER is_null )) OR ACTUALOWNER = " + userId + "))",
                dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }

    @Test
    public void testSetUserWithoutRoles() {
        String userId = "userId";

        when(identityProvider.getRoles()).thenReturn(Collections.emptyList());
        when(identityProvider.getName()).thenReturn(userId);

        potOwnerTasksPreprocessor.preprocess(dataSetLookup);

        assertEquals("((ENTITY_ID is_null  OR ENTITY_ID != " + userId + ") AND ((ID = " + userId + " AND (ACTUALOWNER =  OR ACTUALOWNER is_null )) OR ACTUALOWNER = " + userId + "))",
                dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }

}
