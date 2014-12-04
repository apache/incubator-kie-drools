/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.impl.domain.variable.listener;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * A stateful {@link VariableListener},
 * often used to externalize data for a supply from the domain model itself.
 */
public interface StatefulVariableListener<EntityG> extends VariableListener<EntityG> {

    void resetWorkingSolution(ScoreDirector scoreDirector, Solution workingSolution);

    void clearWorkingSolution(ScoreDirector scoreDirector);

}
