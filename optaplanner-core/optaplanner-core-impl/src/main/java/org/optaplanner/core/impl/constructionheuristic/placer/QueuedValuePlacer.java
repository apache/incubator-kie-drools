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

import java.util.Collections;
import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

public class QueuedValuePlacer<Solution_> extends AbstractEntityPlacer<Solution_> implements EntityPlacer<Solution_> {

    protected final EntityIndependentValueSelector<Solution_> valueSelector;
    protected final MoveSelector<Solution_> moveSelector;

    public QueuedValuePlacer(EntityIndependentValueSelector<Solution_> valueSelector,
            MoveSelector<Solution_> moveSelector) {
        this.valueSelector = valueSelector;
        this.moveSelector = moveSelector;
        phaseLifecycleSupport.addEventListener(valueSelector);
        phaseLifecycleSupport.addEventListener(moveSelector);
    }

    @Override
    public Iterator<Placement<Solution_>> iterator() {
        return new QueuedValuePlacingIterator();
    }

    private class QueuedValuePlacingIterator extends UpcomingSelectionIterator<Placement<Solution_>> {

        private Iterator<Object> valueIterator;

        private QueuedValuePlacingIterator() {
            valueIterator = Collections.emptyIterator();
        }

        @Override
        protected Placement<Solution_> createUpcomingSelection() {
            // If all values are used, there can still be entities uninitialized
            if (!valueIterator.hasNext()) {
                valueIterator = valueSelector.iterator();
                if (!valueIterator.hasNext()) {
                    return noUpcomingSelection();
                }
            }
            valueIterator.next();
            Iterator<Move<Solution_>> moveIterator = moveSelector.iterator();
            // Because the valueSelector is entity independent, there is always a move if there's still an entity
            if (!moveIterator.hasNext()) {
                return noUpcomingSelection();
            }
            return new Placement<>(moveIterator);
        }

    }

}
