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
package org.drools.workbench.models.datamodel.rule.util;

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class FieldConstraintArrayUtilTest {

    @Test
    public void up() {
        final FieldConstraint[] fieldConstraints = new FieldConstraint[2];
        fieldConstraints[0] = mock(FieldConstraint.class);
        final FieldConstraint bottom = mock(FieldConstraint.class);
        fieldConstraints[1] = bottom;

        FieldConstraintArrayUtil.moveUp(1, fieldConstraints);

        assertEquals(bottom, fieldConstraints[0]);
    }

    @Test
    public void upWhenAlreadyTop() {
        final FieldConstraint[] fieldConstraints = new FieldConstraint[2];
        final FieldConstraint top = mock(FieldConstraint.class);
        fieldConstraints[0] = top;
        fieldConstraints[1] = mock(FieldConstraint.class);

        FieldConstraintArrayUtil.moveUp(0, fieldConstraints);

        assertEquals(top, fieldConstraints[0]);
    }

    @Test
    public void shouldNotMoveHigherThanUsedVariables() {
        final FieldConstraint[] fieldConstraints = new FieldConstraint[2];
        final SingleFieldConstraint top = new SingleFieldConstraint();
        top.setFieldBinding("a");
        fieldConstraints[0] = top;
        final SingleFieldConstraint bottom = new SingleFieldConstraint();
        bottom.setConstraintValueType(BaseSingleFieldConstraint.TYPE_VARIABLE);
        bottom.setValue("a");
        fieldConstraints[1] = bottom;

        FieldConstraintArrayUtil.moveUp(1, fieldConstraints);

        assertEquals(top, fieldConstraints[0]);
        assertEquals(bottom, fieldConstraints[1]);
    }

    @Test
    public void down() {
        final FieldConstraint[] fieldConstraints = new FieldConstraint[2];
        final FieldConstraint top = mock(FieldConstraint.class);
        fieldConstraints[0] = top;
        fieldConstraints[1] = mock(FieldConstraint.class);

        FieldConstraintArrayUtil.moveDown(0, fieldConstraints);

        assertEquals(top, fieldConstraints[1]);
    }

    @Test
    public void downWhenAlreadyBottom() {
        final FieldConstraint[] fieldConstraints = new FieldConstraint[2];
        fieldConstraints[0] = mock(FieldConstraint.class);
        final FieldConstraint bottom = mock(FieldConstraint.class);
        fieldConstraints[1] = bottom;

        FieldConstraintArrayUtil.moveDown(1, fieldConstraints);

        assertEquals(bottom, fieldConstraints[1]);
    }

    @Test
    public void shouldNotMoveLowerThanUsedVariables() {
        final FieldConstraint[] fieldConstraints = new FieldConstraint[2];
        final SingleFieldConstraint top = new SingleFieldConstraint();
        top.setFieldBinding("a");
        fieldConstraints[0] = top;
        final SingleFieldConstraint bottom = new SingleFieldConstraint();
        bottom.setConstraintValueType(BaseSingleFieldConstraint.TYPE_VARIABLE);
        bottom.setValue("a");
        fieldConstraints[1] = bottom;

        FieldConstraintArrayUtil.moveDown(0, fieldConstraints);

        assertEquals(top, fieldConstraints[0]);
        assertEquals(bottom, fieldConstraints[1]);
    }

    @Test

    public void shouldNotMoveUpperThanUsedVariables_MoveToMiddle() {

        final FieldConstraint[] fieldConstraints = new FieldConstraint[3];
        final SingleFieldConstraint top = new SingleFieldConstraint();
        top.setConstraintValueType(BaseSingleFieldConstraint.TYPE_VARIABLE);
        top.setValue("b");
        fieldConstraints[0] = top;
        final SingleFieldConstraint middle = new SingleFieldConstraint();
        middle.setConstraintValueType(BaseSingleFieldConstraint.TYPE_VARIABLE);
        middle.setValue("a");
        fieldConstraints[1] = middle;
        final SingleFieldConstraint bottom = new SingleFieldConstraint();
        bottom.setFieldBinding("a");
        fieldConstraints[2] = bottom;

        FieldConstraintArrayUtil.moveUp(2, fieldConstraints);
        FieldConstraintArrayUtil.moveUp(1, fieldConstraints);

        assertEquals(bottom, fieldConstraints[0]);
        assertEquals(top, fieldConstraints[1]);
        assertEquals(middle, fieldConstraints[2]);
    }

    @Test
    public void shouldNotMoveLowerThanUsedVariables_MoveToMiddle() {

        final FieldConstraint[] fieldConstraints = new FieldConstraint[3];
        final SingleFieldConstraint top = new SingleFieldConstraint();
        top.setFieldBinding("a");
        fieldConstraints[0] = top;
        final SingleFieldConstraint middle = new SingleFieldConstraint();
        middle.setConstraintValueType(BaseSingleFieldConstraint.TYPE_VARIABLE);
        middle.setValue("a");
        fieldConstraints[1] = middle;
        final SingleFieldConstraint bottom = new SingleFieldConstraint();
        bottom.setConstraintValueType(BaseSingleFieldConstraint.TYPE_VARIABLE);
        bottom.setValue("b");
        fieldConstraints[2] = bottom;

        FieldConstraintArrayUtil.moveDown(0, fieldConstraints);
        FieldConstraintArrayUtil.moveDown(1, fieldConstraints);

        assertEquals(top, fieldConstraints[0]);
        assertEquals(bottom, fieldConstraints[1]);
        assertEquals(middle, fieldConstraints[2]);
    }

    @Test
    public void testMove_DifferentBindings() {

        final FieldConstraint[] fieldConstraints = new FieldConstraint[3];
        final SingleFieldConstraint top = new SingleFieldConstraint();
        top.setFieldBinding("a");
        fieldConstraints[0] = top;
        final SingleFieldConstraint middle = new SingleFieldConstraint();
        middle.setFieldBinding("b");
        fieldConstraints[1] = middle;
        final SingleFieldConstraint bottom = new SingleFieldConstraint();
        bottom.setFieldBinding("c");
        fieldConstraints[2] = bottom;

        // a, b, c -> b, a, c
        FieldConstraintArrayUtil.moveDown(0, fieldConstraints);
        // b, a, c -> b, c, a
        FieldConstraintArrayUtil.moveUp(2, fieldConstraints);

        assertEquals(middle, fieldConstraints[0]);
        assertEquals(bottom, fieldConstraints[1]);
        assertEquals(top, fieldConstraints[2]);
    }
}