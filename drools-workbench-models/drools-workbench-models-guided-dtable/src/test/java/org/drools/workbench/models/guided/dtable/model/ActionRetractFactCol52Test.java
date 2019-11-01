/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.guided.dtable.model;

import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ActionRetractFactCol52Test {

    @Test
    public void testSameActions() {
        final ActionRetractFactCol52 retractAction = new ActionRetractFactCol52();
        final ActionRetractFactCol52 sameRetractAction = new ActionRetractFactCol52();

        assertEquals(retractAction, sameRetractAction);
    }

    @Test
    public void testDifferentActions() {
        final ActionInsertFactCol52 insertAction = new ActionInsertFactCol52();
        final ActionRetractFactCol52 retractAction = new ActionRetractFactCol52();

        assertNotEquals(insertAction, retractAction);
    }
}
