/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class PooledEntityPlacer extends AbstractEntityPlacer implements EntityPlacer {

    protected final MoveSelector moveSelector;

    public PooledEntityPlacer(MoveSelector moveSelector) {
        this.moveSelector = moveSelector;
        phaseLifecycleSupport.addEventListener(moveSelector);
    }

    @Override
    public Iterator<Placement> iterator() {
        return new PooledEntityPlacingIterator();
    }

    private class PooledEntityPlacingIterator extends UpcomingSelectionIterator<Placement> {

        private PooledEntityPlacingIterator() {
        }

        @Override
        protected Placement createUpcomingSelection() {
            Iterator<Move> moveIterator = moveSelector.iterator();
            if (!moveIterator.hasNext()) {
                return noUpcomingSelection();
            }
            return new Placement(moveIterator);
        }

    }

}
