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

import java.util.Objects;

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;

public class FieldConstraintArrayUtil {

    private FieldConstraintArrayUtil() {
    }

    public static void moveDown(int index, FieldConstraint[] fieldConstraints) {
        if (index < (fieldConstraints.length - 1)) {
            final FieldConstraint first = fieldConstraints[index];
            final FieldConstraint second = fieldConstraints[index + 1];

            if (blockSwap(first, second)) {
                return;
            }

            fieldConstraints[index] = second;
            fieldConstraints[index + 1] = first;
        }
    }

    private static boolean blockSwap(FieldConstraint first, FieldConstraint second) {
        if (first instanceof SingleFieldConstraint && second instanceof SingleFieldConstraint) {
            final SingleFieldConstraint sfcFirst = (SingleFieldConstraint) first;
            final SingleFieldConstraint sfcSecond = (SingleFieldConstraint) second;

            if (Objects.equals(BaseSingleFieldConstraint.TYPE_VARIABLE, sfcSecond.getConstraintValueType())
                    && Objects.equals(sfcFirst.getFieldBinding(), sfcSecond.getValue())) {
                return true;
            }
        }
        return false;
    }

    public static void moveUp(int index, FieldConstraint[] fieldConstraints) {

        if (index > 0) {

            final FieldConstraint first = fieldConstraints[index - 1];
            final FieldConstraint second = fieldConstraints[index];

            if (blockSwap(first, second)) {
                return;
            }

            fieldConstraints[index - 1] = second;
            fieldConstraints[index] = first;
        }
    }
}
