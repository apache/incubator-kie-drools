/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.core.domain;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.security.PrivateKey;
import java.util.Comparator;

import org.drools.planner.core.domain.entity.PlanningEntityDifficultyWeightFactory;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Specifies that the class is a planning entity.
 * Each planning entity must have at least on {@link PlanningVariable} property.
 * <p/>
 * The class should have a public no-arg constructor, so it can be instantiated by Drools Planner.
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface PlanningEntity {

    /**
     * Allows a collection of planning entities to be sorted by difficulty.
     * <p/>
     * Do not use together with {@link #difficultyWeightFactoryClass()}.
     * @return {@link NullDifficultyComparator} when it is null (workaround for annotation limitation)
     */
    public Class<? extends Comparator> difficultyComparatorClass() default NullDifficultyComparator.class;
    interface NullDifficultyComparator extends Comparator {}

    /**
     * Allows a collection of planning entities to be sorted by difficulty.
     * <p/>
     * Do not use together with {@link #difficultyComparatorClass()}.
     * @return {@link NullDifficultyWeightFactory} when it is null (workaround for annotation limitation)
     * @see PlanningEntityDifficultyWeightFactory
     */
    public Class<? extends PlanningEntityDifficultyWeightFactory> difficultyWeightFactoryClass()
            default NullDifficultyWeightFactory.class;
    interface NullDifficultyWeightFactory extends PlanningEntityDifficultyWeightFactory {}

}
