/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.common.experimental;

public class Break<ValueType_, DifferenceType_> {
    private ValueType_ beforeItem;
    private ValueType_ afterItem;
    private DifferenceType_ length;

    public Break(ValueType_ beforeItem, ValueType_ afterItem, DifferenceType_ length) {
        this.beforeItem = beforeItem;
        this.afterItem = afterItem;
        this.length = length;
    }

    public ValueType_ getBeforeItem() {
        return beforeItem;
    }

    public ValueType_ getAfterItem() {
        return afterItem;
    }

    public DifferenceType_ getLength() {
        return length;
    }

    public void setBeforeItem(ValueType_ beforeItem) {
        this.beforeItem = beforeItem;
    }

    public void setAfterItem(ValueType_ afterItem) {
        this.afterItem = afterItem;
    }

    public void setLength(DifferenceType_ length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Break{" +
                "beforeItem=" + beforeItem +
                ", afterItem=" + afterItem +
                ", length=" + length +
                '}';
    }
}
