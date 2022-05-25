/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bavet.common.index;

final class TwoIndexProperties implements IndexProperties {

    private final Object propertyA;
    private final Object propertyB;

    TwoIndexProperties(Object propertyA, Object propertyB) {
        this.propertyA = propertyA;
        this.propertyB = propertyB;
    }

    @Override
    public <Type_> Type_ getProperty(int index) {
        switch (index) {
            case 0:
                return (Type_) propertyA;
            case 1:
                return (Type_) propertyB;
            default:
                throw new IllegalArgumentException("Impossible state: index (" + index + ") != 0");
        }
    }

    @Override
    public int maxLength() {
        return 2;
    }

    @Override
    public String toString() {
        return "[" + propertyA + ", " + propertyB + "]";
    }

}
