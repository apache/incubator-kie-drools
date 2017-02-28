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
import java.util.List;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class QueuedEntityPlacer extends AbstractEntityPlacer implements EntityPlacer {

    protected final EntitySelector entitySelector;
    protected final List<MoveSelector> moveSelectorList;

    public QueuedEntityPlacer(EntitySelector entitySelector, List<MoveSelector> moveSelectorList) {
        this.entitySelector = entitySelector;
        this.moveSelectorList = moveSelectorList;
        phaseLifecycleSupport.addEventListener(entitySelector);
        for (MoveSelector moveSelector : moveSelectorList) {
            phaseLifecycleSupport.addEventListener(moveSelector);
        }
    }

    @Override
    public Iterator<Placement> iterator() {
        return new QueuedEntityPlacingIterator(entitySelector.iterator());
    }

    private class QueuedEntityPlacingIterator extends UpcomingSelectionIterator<Placement> {

        private final Iterator<Object> entityIterator;
        private Iterator<MoveSelector> moveSelectorIterator;

        private QueuedEntityPlacingIterator(Iterator<Object> entityIterator) {
            this.entityIterator = entityIterator;
            moveSelectorIterator = Collections.emptyIterator();
        }

        @Override
        protected Placement createUpcomingSelection() {
            Iterator<Move> moveIterator = null;
            // Skip empty placements to avoid no-operation steps
            while (moveIterator == null || !moveIterator.hasNext()) {
                // If a moveSelector's iterator is empty, it might not be empty the next time
                // (because the entity changes)
                while (!moveSelectorIterator.hasNext()) {
                    if (!entityIterator.hasNext()) {
                        return noUpcomingSelection();
                    }
                    entityIterator.next();
                    moveSelectorIterator = moveSelectorList.iterator();
                }
                MoveSelector moveSelector = moveSelectorIterator.next();
                moveIterator = moveSelector.iterator();
            }
            return new Placement(moveIterator);
        }

    }

}
