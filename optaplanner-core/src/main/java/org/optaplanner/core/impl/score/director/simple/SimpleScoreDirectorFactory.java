/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.impl.score.director.simple;

import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;

/**
 * Simple implementation of {@link ScoreDirectorFactory}.
 * @see SimpleScoreDirector
 * @see ScoreDirectorFactory
 */
public class SimpleScoreDirectorFactory extends AbstractScoreDirectorFactory {

    private final SimpleScoreCalculator simpleScoreCalculator;

    public SimpleScoreDirectorFactory(SimpleScoreCalculator simpleScoreCalculator) {
        this.simpleScoreCalculator = simpleScoreCalculator;
    }

    public SimpleScoreCalculator getSimpleScoreCalculator() {
        return simpleScoreCalculator;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public SimpleScoreDirector buildScoreDirector() {
        return new SimpleScoreDirector(this, simpleScoreCalculator);
    }

}
