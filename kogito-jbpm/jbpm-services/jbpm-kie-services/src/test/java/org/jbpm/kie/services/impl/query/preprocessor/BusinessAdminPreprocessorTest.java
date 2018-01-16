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
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BusinessAdminPreprocessorTest {

    @Mock
    IdentityProvider identityProvider;
    
    @Mock
    DataSetMetadata metaData;

    DataSetLookup dataSetLookup;

    BusinessAdminPreprocessor businessAdminPreprocessor;

    @Before
    public void init() {
        System.setProperty("org.jbpm.ht.admin.group",
                           "admins");
        System.setProperty("org.jbpm.ht.admin.user",
                           "admin");
        businessAdminPreprocessor = new BusinessAdminPreprocessor(identityProvider, metaData);
    }

    @Test
    public void testQueryByGroupBusinessAdmin() {
        dataSetLookup = spy(new DataSetLookup());
        String role1 = "admins";
        String role2 = "role2";
        String userId = "userId";

        when(identityProvider.getRoles()).thenReturn(Arrays.asList(role1,
                                                                   role2));
        when(identityProvider.getName()).thenReturn(userId);

        businessAdminPreprocessor.preprocess(dataSetLookup);

        verifyNoMoreInteractions(dataSetLookup);
        assertNull(dataSetLookup.getFirstFilterOp());
    }

    @Test
    public void testQueryByUserBusinessAdmin() {
        dataSetLookup = spy(new DataSetLookup());
        String role1 = "role1";
        String role2 = "role2";
        String userId = "admin";

        when(identityProvider.getRoles()).thenReturn(Arrays.asList(role1,
                                                                   role2));
        when(identityProvider.getName()).thenReturn(userId);

        businessAdminPreprocessor.preprocess(dataSetLookup);

        verifyNoMoreInteractions(dataSetLookup);
        assertNull(dataSetLookup.getFirstFilterOp());
    }

    @Test
    public void testQueryByNoBusinessAdmin() {
        dataSetLookup = new DataSetLookup();
        String role1 = "role1";
        String role2 = "role2";
        String userId = "userId";

        when(identityProvider.getRoles()).thenReturn(Arrays.asList(role1,
                                                                   role2));
        when(identityProvider.getName()).thenReturn(userId);

        businessAdminPreprocessor.preprocess(dataSetLookup);

        assertEquals("TASKID = -1",
                     dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }

    @Test
    public void testSetUserWithoutRoles() {
        dataSetLookup = new DataSetLookup();
        String userId = "userId";

        when(identityProvider.getRoles()).thenReturn(Collections.emptyList());
        when(identityProvider.getName()).thenReturn(userId);

        businessAdminPreprocessor.preprocess(dataSetLookup);

        assertEquals("TASKID = -1",
                     dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }
}
