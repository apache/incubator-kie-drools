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

import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ActionSetFieldCol52Test {

    @Test
    public void testSameActions() {
        final ActionSetFieldCol52 setAge = new ActionSetFieldCol52();
        setAge.setBoundName("$p");
        setAge.setFactField("age");
        setAge.setUpdate(false);
        setAge.setValueList("18,19,20");
        setAge.setType(DataType.TYPE_NUMERIC_INTEGER);

        final ActionSetFieldCol52 setSameAge = new ActionSetFieldCol52();
        setSameAge.setBoundName("$p");
        setSameAge.setFactField("age");
        setSameAge.setUpdate(false);
        setSameAge.setValueList("18,19,20");
        setSameAge.setType(DataType.TYPE_NUMERIC_INTEGER);

        assertEquals(setAge, setSameAge);
    }

    @Test
    public void testDifferentBinding() {
        final ActionSetFieldCol52 setAge = new ActionSetFieldCol52();
        setAge.setBoundName("$p1");
        setAge.setFactField("age");
        setAge.setUpdate(false);
        setAge.setValueList("18,19,20");
        setAge.setType(DataType.TYPE_NUMERIC_INTEGER);

        final ActionSetFieldCol52 setAgeOfSomeoneElse = new ActionSetFieldCol52();
        setAgeOfSomeoneElse.setBoundName("$p2");
        setAgeOfSomeoneElse.setFactField("age");
        setAgeOfSomeoneElse.setUpdate(false);
        setAgeOfSomeoneElse.setValueList("18,19,20");
        setAgeOfSomeoneElse.setType(DataType.TYPE_NUMERIC_INTEGER);

        assertNotEquals(setAge, setAgeOfSomeoneElse);
    }

    @Test
    public void testDifferentFields() {
        final ActionSetFieldCol52 setAge = new ActionSetFieldCol52();
        setAge.setBoundName("$p");
        setAge.setFactField("age");
        setAge.setUpdate(false);
        setAge.setValueList("18,19,20");
        setAge.setType(DataType.TYPE_NUMERIC_INTEGER);

        final ActionSetFieldCol52 setHeight = new ActionSetFieldCol52();
        setHeight.setBoundName("$p");
        setHeight.setFactField("height");
        setHeight.setUpdate(false);
        setHeight.setValueList("18,19,20");
        setHeight.setType(DataType.TYPE_NUMERIC_INTEGER);

        assertNotEquals(setAge, setHeight);
    }

    @Test
    public void testDifferentPossibilities() {
        final ActionSetFieldCol52 setAge = new ActionSetFieldCol52();
        setAge.setBoundName("$p");
        setAge.setFactField("age");
        setAge.setUpdate(false);
        setAge.setValueList("18,19,20");
        setAge.setType(DataType.TYPE_NUMERIC_INTEGER);

        final ActionSetFieldCol52 setHigherAge = new ActionSetFieldCol52();
        setHigherAge.setBoundName("$p");
        setHigherAge.setFactField("age");
        setHigherAge.setUpdate(false);
        setHigherAge.setValueList("19,20");
        setHigherAge.setType(DataType.TYPE_NUMERIC_INTEGER);

        assertNotEquals(setAge, setHigherAge);
    }

    @Test
    public void testDifferentDataType() {
        final ActionSetFieldCol52 setAge = new ActionSetFieldCol52();
        setAge.setBoundName("$p1");
        setAge.setUpdate(false);
        setAge.setFactField("age");
        setAge.setType(DataType.TYPE_NUMERIC_INTEGER);

        final ActionSetFieldCol52 setAgeWithBiggerRange = new ActionSetFieldCol52();
        setAgeWithBiggerRange.setBoundName("$p1");
        setAgeWithBiggerRange.setUpdate(false);
        setAgeWithBiggerRange.setFactField("age");
        setAgeWithBiggerRange.setType(DataType.TYPE_NUMERIC_BIGINTEGER);

        assertNotEquals(setAge, setAgeWithBiggerRange);
    }

    @Test
    public void testDifferentHandling() {
        final ActionSetFieldCol52 justSet = new ActionSetFieldCol52();
        justSet.setBoundName("$p1");
        justSet.setUpdate(false);
        justSet.setFactField("age");
        justSet.setType(DataType.TYPE_NUMERIC_INTEGER);

        final ActionSetFieldCol52 setAndUpdate = new ActionSetFieldCol52();
        setAndUpdate.setBoundName("$p1");
        setAndUpdate.setUpdate(true);
        setAndUpdate.setFactField("age");
        setAndUpdate.setType(DataType.TYPE_NUMERIC_INTEGER);

        assertNotEquals(justSet, setAndUpdate);
    }

    @Test
    public void testDifferentActions() {
        final ActionSetFieldCol52 insertAction = new ActionSetFieldCol52();
        final ActionRetractFactCol52 retractAction = new ActionRetractFactCol52();

        assertNotEquals(insertAction, retractAction);
    }
}
