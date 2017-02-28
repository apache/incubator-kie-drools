/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.constructionheuristic.placer;

import java.util.Collections;
import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

public class QueuedValuePlacer extends AbstractEntityPlacer implements EntityPlacer {

    protected final EntityIndependentValueSelector valueSelector;
    protected final MoveSelector moveSelector;

    public QueuedValuePlacer(EntityIndependentValueSelector valueSelector, MoveSelector moveSelector) {
        this.valueSelector = valueSelector;
        this.moveSelector = moveSelector;
        phaseLifecycleSupport.addEventListener(valueSelector);
        phaseLifecycleSupport.addEventListener(moveSelector);
    }

    @Override
    public Iterator<Placement> iterator() {
        return new QueuedValuePlacingIterator();
    }

    private class QueuedValuePlacingIterator extends UpcomingSelectionIterator<Placement> {

        private Iterator<Object> valueIterator;

        private QueuedValuePlacingIterator() {
            valueIterator = Collections.emptyIterator();
        }

        @Override
        protected Placement createUpcomingSelection() {
            // If all values are used, there can still be entities uninitialized
            if (!valueIterator.hasNext()) {
                valueIterator = valueSelector.iterator();
                if (!valueIterator.hasNext()) {
                    return noUpcomingSelection();
                }
            }
            valueIterator.next();
            Iterator<Move> moveIterator = moveSelector.iterator();
            // Because the valueSelector is entity independent, there is always a move if there's still an entity
            if (!moveIterator.hasNext()) {
                return noUpcomingSelection();
            }
            return new Placement(moveIterator);
        }

    }

}
