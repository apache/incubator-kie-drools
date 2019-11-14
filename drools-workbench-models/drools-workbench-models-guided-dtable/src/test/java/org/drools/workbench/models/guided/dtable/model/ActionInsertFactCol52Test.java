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
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ActionInsertFactCol52Test {

    @Test
    public void testSameActions() {
        final ActionInsertFactCol52 insertPerson = new ActionInsertFactCol52();
        insertPerson.setBoundName("$p");
        insertPerson.setFactType("Person");
        insertPerson.setFactField("age");
        insertPerson.setType(DataType.TYPE_NUMERIC_INTEGER);

        final ActionInsertFactCol52 insertSamePerson = new ActionInsertFactCol52();
        insertSamePerson.setBoundName("$p");
        insertSamePerson.setFactType("Person");
        insertSamePerson.setFactField("age");
        insertSamePerson.setType(DataType.TYPE_NUMERIC_INTEGER);

        assertEquals(insertPerson, insertSamePerson);
    }

    @Test
    public void testDifferentBinding() {
        final ActionInsertFactCol52 insertPerson = new ActionInsertFactCol52();
        insertPerson.setBoundName("$p1");
        insertPerson.setFactType("Person");
        insertPerson.setFactField("age");
        insertPerson.setType(DataType.TYPE_NUMERIC_INTEGER);

        final ActionInsertFactCol52 insertDifferentPerson = new ActionInsertFactCol52();
        insertDifferentPerson.setBoundName("$p2");
        insertDifferentPerson.setFactType("Person");
        insertDifferentPerson.setFactField("age");
        insertDifferentPerson.setType(DataType.TYPE_NUMERIC_INTEGER);

        assertNotEquals(insertPerson, insertDifferentPerson);
    }

    @Test
    public void testDifferentFacts() {
        final ActionInsertFactCol52 insertPerson = new ActionInsertFactCol52();
        insertPerson.setBoundName("$p1");
        insertPerson.setFactType("Person");
        insertPerson.setFactField("age");
        insertPerson.setType(DataType.TYPE_NUMERIC_INTEGER);

        final ActionInsertFactCol52 insertDifferentPerson = new ActionInsertFactCol52();
        insertDifferentPerson.setBoundName("$p1");
        insertDifferentPerson.setFactType("Human");
        insertDifferentPerson.setFactField("age");
        insertDifferentPerson.setType(DataType.TYPE_NUMERIC_INTEGER);

        assertNotEquals(insertPerson, insertDifferentPerson);
    }

    @Test
    public void testDifferentFields() {
        final ActionInsertFactCol52 insertPerson = new ActionInsertFactCol52();
        insertPerson.setBoundName("$p1");
        insertPerson.setFactType("Person");
        insertPerson.setFactField("age");
        insertPerson.setType(DataType.TYPE_NUMERIC_INTEGER);

        final ActionInsertFactCol52 insertDifferentPerson = new ActionInsertFactCol52();
        insertDifferentPerson.setBoundName("$p1");
        insertDifferentPerson.setFactType("Person");
        insertDifferentPerson.setFactField("childrenCount");
        insertDifferentPerson.setType(DataType.TYPE_NUMERIC_INTEGER);

        assertNotEquals(insertPerson, insertDifferentPerson);
    }

    @Test
    public void testDifferentDataType() {
        final ActionInsertFactCol52 insertPerson = new ActionInsertFactCol52();
        insertPerson.setBoundName("$p1");
        insertPerson.setFactType("Person");
        insertPerson.setFactField("age");
        insertPerson.setType(DataType.TYPE_NUMERIC_INTEGER);

        final ActionInsertFactCol52 insertDifferentPerson = new ActionInsertFactCol52();
        insertDifferentPerson.setBoundName("$p1");
        insertDifferentPerson.setFactType("Person");
        insertDifferentPerson.setFactField("age");
        insertDifferentPerson.setType(DataType.TYPE_NUMERIC_BIGINTEGER);

        assertNotEquals(insertPerson, insertDifferentPerson);
    }

    @Test
    public void testDifferentActions() {
        final ActionInsertFactCol52 insertAction = new ActionInsertFactCol52();
        final ActionRetractFactCol52 retractAction = new ActionRetractFactCol52();

        assertNotEquals(insertAction, retractAction);
    }
}
