/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.heuristic.selector.entity.cached;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.solver.DefaultSolverScope;

public class FilteringEntitySelector extends CachingEntitySelector {

    protected List<Object> cachedEntityList = null;
    // TODO filter class

    public FilteringEntitySelector(SelectionCacheType cacheType) {
        super(cacheType);
    }

    // ************************************************************************
    // Cache lifecycle methods
    // ************************************************************************

    protected void constructCache(DefaultSolverScope solverScope) {
        long childSize = childEntitySelector.getSize();
        cachedEntityList = new ArrayList<Object>((int) childSize);
        CollectionUtils.addAll(cachedEntityList, childEntitySelector.iterator());
    }

    protected void disposeCache(DefaultSolverScope solverScope) {
        cachedEntityList = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public long getSize() {
        return cachedEntityList.size();
    }

    @Override
    public String toString() {
        return "Filtering(" + childEntitySelector + ")";
    }

    public Iterator<Object> iterator() {
        return cachedEntityList.iterator();
    }

}
