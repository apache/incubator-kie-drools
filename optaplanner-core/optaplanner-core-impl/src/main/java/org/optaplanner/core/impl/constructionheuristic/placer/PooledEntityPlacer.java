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

package org.optaplanner.core.impl.constructionheuristic.placer;

import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class PooledEntityPlacer<Solution_> extends AbstractEntityPlacer<Solution_> implements EntityPlacer<Solution_> {

    protected final MoveSelector<Solution_> moveSelector;

    public PooledEntityPlacer(MoveSelector<Solution_> moveSelector) {
        this.moveSelector = moveSelector;
        phaseLifecycleSupport.addEventListener(moveSelector);
    }

    @Override
    public Iterator<Placement<Solution_>> iterator() {
        return new PooledEntityPlacingIterator();
    }

    private class PooledEntityPlacingIterator extends UpcomingSelectionIterator<Placement<Solution_>> {

        private PooledEntityPlacingIterator() {
        }

        @Override
        protected Placement<Solution_> createUpcomingSelection() {
            Iterator<Move<Solution_>> moveIterator = moveSelector.iterator();
            if (!moveIterator.hasNext()) {
                return noUpcomingSelection();
            }
            return new Placement<Solution_>(moveIterator);
        }

    }

}
