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

final class NoneIndexProperties implements IndexProperties {

    static final NoneIndexProperties INSTANCE = new NoneIndexProperties();

    private NoneIndexProperties() {

    }

    @Override
    public <Type_> Type_ getProperty(int index) {
        throw new IllegalArgumentException("Impossible state: none index property requested");
    }

    @Override
    public <Type_> Type_ getIndexerKey(int fromInclusive, int toExclusive) {
        throw new IllegalArgumentException("Impossible state: none indexer key requested");
    }

    @Override
    public String toString() {
        return "[]";
    }

}
