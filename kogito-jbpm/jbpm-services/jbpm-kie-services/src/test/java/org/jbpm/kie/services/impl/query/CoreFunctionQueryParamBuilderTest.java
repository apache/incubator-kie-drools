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

package org.jbpm.kie.services.impl.query;

import java.util.ArrayList;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.LogicalExprFilter;
import org.dashbuilder.dataset.filter.LogicalExprType;
import org.jbpm.services.api.query.model.QueryParam;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.services.api.query.QueryResultMapper.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CoreFunctionQueryParamBuilderTest {

    CoreFunctionQueryParamBuilder coreFunctionQueryParamBuilder;

    @Before
    public void init() {
    }

    @Test
    public void testLogicalExprPassedAsQueryParam() {
        ArrayList values = new ArrayList();
        values.add("value1");
        values.add("value2");

        final ColumnFilter filter1 = likeTo(COLUMN_PROCESSNAME, "%processName%");
        final ColumnFilter filter2 = likeTo(COLUMN_PROCESSID, "%processName%");

        ArrayList terms = new ArrayList();
        terms.add(filter1);
        terms.add(filter2);
        QueryParam queryParam = new QueryParam("", LogicalExprType.OR.toString(), terms);

        ArrayList queryParams = new ArrayList();
        queryParams.add(queryParam);
        coreFunctionQueryParamBuilder = new CoreFunctionQueryParamBuilder(queryParam);

        Object builded = coreFunctionQueryParamBuilder.build();

        assertTrue(builded instanceof LogicalExprFilter);
        assertEquals("(" + COLUMN_PROCESSNAME + " like %processName%, true OR " + COLUMN_PROCESSID + " like %processName%, true)", builded.toString());
    }


}
