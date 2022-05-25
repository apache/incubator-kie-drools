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

import java.util.Arrays;

final class ManyIndexProperties implements IndexProperties {

    private final Object[] properties;

    ManyIndexProperties(Object... properties) {
        this.properties = properties;
    }

    @Override
    public <Type_> Type_ getProperty(int index) {
        return (Type_) properties[index];
    }

    @Override
    public int maxLength() {
        return properties.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(properties);
    }
}
