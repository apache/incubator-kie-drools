/*
 *
 *  * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.optaplanner.constraint.streams.bavet.common;

public final class Group<OutTuple_ extends Tuple, GroupKey_, ResultContainer_> {

    public final GroupKey_ groupKey;
    public final ResultContainer_ resultContainer;
    public int parentCount = 0;
    public boolean dirty = false;
    public boolean dying = false;
    public OutTuple_ tuple = null;

    public Group(GroupKey_ groupKey, ResultContainer_ resultContainer) {
        this.groupKey = groupKey;
        this.resultContainer = resultContainer;
    }
}
