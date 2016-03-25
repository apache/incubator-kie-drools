/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.domain.solution.drools;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;

import org.kie.api.runtime.KieSession;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies that a property (or a field) on a {@link PlanningSolution} class is a {@link Collection} of planning facts.
 * <p>
 * The planning facts will be added as facts in the Drools {@link KieSession} of the {@link DroolsScoreDirector}.
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface PlanningFactCollectionProperty {

}
