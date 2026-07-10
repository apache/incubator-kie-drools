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

package org.optaplanner.quarkus.testdata.gizmo;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

public class DummyVariableListener implements VariableListener {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Object o) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Object o) {

    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Object o) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Object o) {

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Object o) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Object o) {

    }
}
