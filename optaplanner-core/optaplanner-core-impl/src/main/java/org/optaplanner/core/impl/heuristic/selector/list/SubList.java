/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.list;

import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;

public final class SubList {

    private final Object entity;
    private final int fromIndex;
    private final int length;

    public SubList(Object entity, int fromIndex, int length) {
        this.entity = entity;
        this.fromIndex = fromIndex;
        this.length = length;
    }

    public Object getEntity() {
        return entity;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public int getLength() {
        return length;
    }

    public int getToIndex() {
        return fromIndex + length;
    }

    public SubList rebase(ScoreDirector<?> destinationScoreDirector) {
        return new SubList(destinationScoreDirector.lookUpWorkingObject(entity), fromIndex, length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubList other = (SubList) o;
        return fromIndex == other.fromIndex && length == other.length && entity.equals(other.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity, fromIndex, length);
    }

    @Override
    public String toString() {
        return entity + "[" + fromIndex + ".." + getToIndex() + "]";
    }
}
