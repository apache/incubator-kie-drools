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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.jbpm.kie.services.impl.query.CoreFunctionQueryParamBuilder;
import org.jbpm.services.api.query.model.QueryParam;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.identity.IdentityProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserAndGroupsTasksPreprocessorTest {

    DataSetLookup dataSetLookup;
    private static String COL_ID = "POTOWNER";

    @Mock
    IdentityProvider identityProvider;

    @Mock
    UserGroupCallback userGroupCallback;

    @Mock
    DataSetMetadata metaData;

    @InjectMocks
    UserAndGroupsTasksPreprocessor userAndGroupsTasksPreprocessor;

    @Before
    public void init() {
        dataSetLookup = new DataSetLookup();
        userAndGroupsTasksPreprocessor = new  UserAndGroupsTasksPreprocessor(identityProvider,
                                                                             userGroupCallback,
                                                                             COL_ID,
                                                                             metaData);
    }

    @Test
    public void testSetUser() {
        String role1 = "role1";
        String role2 = "role2";
        String userId = "userId";

        when(userGroupCallback.getGroupsForUser(userId)).thenReturn(Arrays.asList(role1,
                                                                                  role2));
        when(identityProvider.getName()).thenReturn(userId);

        userAndGroupsTasksPreprocessor.preprocess(dataSetLookup);

        assertEquals("(POTOWNER in " + role1 + ", " + role2 + ", " + userId + ")",
                     dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }

    @Test
    public void testSetUserWithoutRoles() {
        String userId = "userId";

        when(userGroupCallback.getGroupsForUser(userId)).thenReturn(Collections.emptyList());
        when(identityProvider.getName()).thenReturn(userId);

        userAndGroupsTasksPreprocessor.preprocess(dataSetLookup);

        assertEquals("(POTOWNER in " + userId + ")",
                     dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }

    @Test
    public void testNullGroups() {
        String userId = "userId";

        when(userGroupCallback.getGroupsForUser(userId)).thenReturn(null);
        when(identityProvider.getName()).thenReturn(userId);

        userAndGroupsTasksPreprocessor.preprocess(dataSetLookup);

        assertEquals("(POTOWNER in " + userId + ")",
                     dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }

    @Test
    public void testPotOwnerFilterNoIdentityProvider() {
        String userId = "userId";

        when(userGroupCallback.getGroupsForUser(userId)).thenReturn(null);
        when(identityProvider.getName()).thenReturn(userId);

        String potOwner = "potOwner";
        when(userGroupCallback.getGroupsForUser(potOwner)).thenReturn(Arrays.asList("role1",
                "role2"));

        List<String> potOwners = new ArrayList<String>();
        potOwners.add(potOwner);
        QueryParam queryParam = new QueryParam(COL_ID, "IN", potOwners);

        List<QueryParam> queryParams = new ArrayList<QueryParam>();
        queryParams.add(queryParam);
        CoreFunctionQueryParamBuilder coreFunctionQueryParamBuilder = new CoreFunctionQueryParamBuilder(queryParam);
        CoreFunctionFilter columnFilter = (CoreFunctionFilter) coreFunctionQueryParamBuilder.build();

        DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(columnFilter);
        dataSetLookup.addOperation(filter);

        userAndGroupsTasksPreprocessor.preprocess(dataSetLookup);

        assertEquals("(POTOWNER in role1, role2, " + potOwner + ")",
                     dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }

    @Test
    public void testPotOwnerFilterWithEmptyPotOwnerList() {
        String userId = "userId";

        when(userGroupCallback.getGroupsForUser(userId)).thenReturn(null);
        when(identityProvider.getName()).thenReturn(userId);

        List<String> potOwners = new ArrayList<String>();
        QueryParam queryParam = new QueryParam(COL_ID, "IN", potOwners);

        List<QueryParam> queryParams = new ArrayList<QueryParam>();
        queryParams.add(queryParam);
        CoreFunctionQueryParamBuilder coreFunctionQueryParamBuilder = new CoreFunctionQueryParamBuilder(queryParam);
        CoreFunctionFilter columnFilter = (CoreFunctionFilter) coreFunctionQueryParamBuilder.build();

        DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(columnFilter);
        dataSetLookup.addOperation(filter);

        userAndGroupsTasksPreprocessor.preprocess(dataSetLookup);

        assertEquals("(POTOWNER in " + userId + ")",
                     dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }

    @Test
    public void testPotOwnerFilterNoIdentityProviderAndNullGroups() {
        String userId = "userId";

        when(userGroupCallback.getGroupsForUser(userId)).thenReturn(null);
        when(identityProvider.getName()).thenReturn(userId);

        String potOwner = "potOwner";
        when(userGroupCallback.getGroupsForUser(potOwner)).thenReturn(null);

        List<String> potOwners = new ArrayList<String>();
        potOwners.add(potOwner);
        QueryParam queryParam = new QueryParam(COL_ID, "IN", potOwners);

        List<QueryParam> queryParams = new ArrayList<QueryParam>();
        queryParams.add(queryParam);
        CoreFunctionQueryParamBuilder coreFunctionQueryParamBuilder = new CoreFunctionQueryParamBuilder(queryParam);
        CoreFunctionFilter columnFilter = (CoreFunctionFilter) coreFunctionQueryParamBuilder.build();

        DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(columnFilter);
        dataSetLookup.addOperation(filter);

        userAndGroupsTasksPreprocessor.preprocess(dataSetLookup);

        assertEquals("(POTOWNER in " + potOwner + ")",
                     dataSetLookup.getFirstFilterOp().getColumnFilterList().get(0).toString());
    }
}
