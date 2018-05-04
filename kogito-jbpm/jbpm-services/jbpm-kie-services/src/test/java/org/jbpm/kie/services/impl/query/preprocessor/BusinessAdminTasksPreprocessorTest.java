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
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.identity.IdentityProvider;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BusinessAdminTasksPreprocessorTest {

    @Mock
    IdentityProvider identityProvider;

    @Mock
    UserGroupCallback userGroupCallback;

    @Mock
    DataSetMetadata metaData;

    DataSetLookup dataSetLookup;

    BusinessAdminTasksPreprocessor preprocessor;

    @Before
    public void init() {
        System.setProperty("org.jbpm.ht.admin.group",
                           "admins");
        System.setProperty("org.jbpm.ht.admin.user",
                           "admin");
        preprocessor = new BusinessAdminTasksPreprocessor(identityProvider,
                                                          userGroupCallback,
                                                          metaData);
    }

    @Test
    public void testQueryByGroupBusinessAdmin() {
        dataSetLookup = spy(new DataSetLookup());
        String role1 = "admins";
        String role2 = "role2";
        String userId = "userId";

        when(userGroupCallback.getGroupsForUser(userId)).thenReturn(Arrays.asList(role1,
                                                                                  role2));
        when(identityProvider.getName()).thenReturn(userId);

        preprocessor.preprocess(dataSetLookup);

        verifyNoMoreInteractions(dataSetLookup);
        assertNull(dataSetLookup.getFirstFilterOp());
    }

    @Test
    public void testQueryByUserBusinessAdmin() {
        dataSetLookup = spy(new DataSetLookup());
        String role1 = "role1";
        String role2 = "role2";
        String userId = "admin";

        when(userGroupCallback.getGroupsForUser(userId)).thenReturn(Arrays.asList(role1,
                                                                                  role2));
        when(identityProvider.getName()).thenReturn(userId);

        preprocessor.preprocess(dataSetLookup);

        verifyNoMoreInteractions(dataSetLookup);
        assertNull(dataSetLookup.getFirstFilterOp());
    }

    @Test
    public void testQueryByNoBusinessAdmin() {
        dataSetLookup = new DataSetLookup();
        String role1 = "role1";
        String role2 = "role2";
        String userId = "userId";

        when(userGroupCallback.getGroupsForUser(userId)).thenReturn(Arrays.asList(role1,
                                                                                  role2));
        when(identityProvider.getName()).thenReturn(userId);

        preprocessor.preprocess(dataSetLookup);

        assertEquals("ID = role1, role2, userId",
                     dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }

    @Test
    public void testSetUserWithoutRoles() {
        dataSetLookup = new DataSetLookup();
        String userId = "userId";

        when(userGroupCallback.getGroupsForUser(userId)).thenReturn(Collections.emptyList());
        when(identityProvider.getName()).thenReturn(userId);

        preprocessor.preprocess(dataSetLookup);

        assertEquals("ID = userId",
                     dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }

    @Test
    public void testNullGroups() {
        dataSetLookup = new DataSetLookup();
        String userId = "userId";

        when(userGroupCallback.getGroupsForUser(userId)).thenReturn(null);
        when(identityProvider.getName()).thenReturn(userId);

        preprocessor.preprocess(dataSetLookup);

        assertEquals("ID = userId",
                     dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }
}
