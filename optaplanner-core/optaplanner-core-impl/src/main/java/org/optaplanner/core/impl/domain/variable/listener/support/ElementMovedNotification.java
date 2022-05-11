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

package org.optaplanner.core.impl.domain.variable.listener.support;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.ListVariableListener;

final class ElementMovedNotification<Solution_> extends AbstractNotification implements ListVariableNotification<Solution_> {

    private final Object destinationEntity;
    private final int destinationIndex;

    ElementMovedNotification(Object sourceEntity, int sourceIndex, Object destinationEntity, int destinationIndex) {
        super(sourceEntity, sourceIndex);
        this.destinationEntity = destinationEntity;
        this.destinationIndex = destinationIndex;
    }

    @Override
    public void triggerBefore(ListVariableListener<Solution_, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.beforeElementMoved(scoreDirector, entity, index, destinationEntity, destinationIndex);
    }

    @Override
    public void triggerAfter(ListVariableListener<Solution_, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.afterElementMoved(scoreDirector, entity, index, destinationEntity, destinationIndex);
    }

    @Override
    public String toString() {
        return "ElementMoved(" + entity + "[" + index + "]->" + destinationEntity + "[" + destinationIndex + "])";
    }
}
