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

package org.optaplanner.core.impl.testdata.domain;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

public abstract class DummyVariableListener<Solution_, Entity_> implements VariableListener<Solution_, Entity_> {

    @Override
    public void beforeEntityAdded(ScoreDirector<Solution_> scoreDirector, Entity_ entity_) {
        // Nothing to do.
    }

    @Override
    public void afterEntityAdded(ScoreDirector<Solution_> scoreDirector, Entity_ entity_) {
        // Nothing to do.
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<Solution_> scoreDirector, Entity_ entity_) {
        // Nothing to do.
    }

    @Override
    public void afterVariableChanged(ScoreDirector<Solution_> scoreDirector, Entity_ entity_) {
        // Nothing to do.
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<Solution_> scoreDirector, Entity_ entity_) {
        // Nothing to do.
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<Solution_> scoreDirector, Entity_ entity_) {
        // Nothing to do.
    }
}
