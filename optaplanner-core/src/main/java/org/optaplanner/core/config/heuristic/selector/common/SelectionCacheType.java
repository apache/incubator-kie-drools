/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.heuristic.selector.common;

import javax.xml.bind.annotation.XmlEnum;

/**
 * There is no INHERIT by design because 2 sequential caches provides no benefit, only memory overhead.
 */

@XmlEnum
public enum SelectionCacheType {
    /**
     * Just in time, when the move is created. This is effectively no caching. This is the default for most selectors.
     */
    JUST_IN_TIME,
    /**
     * When the step is started.
     */
    STEP,
    /**
     * When the phase is started.
     */
    PHASE,
    /**
     * When the solver is started.
     */
    SOLVER;

    public static SelectionCacheType resolve(SelectionCacheType cacheType, SelectionCacheType minimumCacheType) {
        if (cacheType == null) {
            return JUST_IN_TIME;
        }
        if (cacheType != JUST_IN_TIME && cacheType.compareTo(minimumCacheType) < 0) {
            throw new IllegalArgumentException("The cacheType (" + cacheType
                    + ") is wasteful because an ancestor has a higher cacheType (" + minimumCacheType + ").");
        }
        return cacheType;
    }

    public boolean isCached() {
        switch (this) {
            case JUST_IN_TIME:
                return false;
            case STEP:
            case PHASE:
            case SOLVER:
                return true;
            default:
                throw new IllegalStateException("The cacheType (" + this + ") is not implemented.");
        }
    }

    public boolean isNotCached() {
        return !isCached();
    }

    public static SelectionCacheType max(SelectionCacheType a, SelectionCacheType b) {
        if (a.compareTo(b) >= 0) {
            return a;
        } else {
            return b;
        }
    }

}
