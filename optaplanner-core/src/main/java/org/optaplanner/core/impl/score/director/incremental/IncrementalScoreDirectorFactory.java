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

package org.optaplanner.core.impl.score.director.incremental;

import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;

/**
 * Incremental implementation of {@link ScoreDirectorFactory}.
 * @see IncrementalScoreDirector
 * @see ScoreDirectorFactory
 */
public class IncrementalScoreDirectorFactory extends AbstractScoreDirectorFactory {

    private Class<? extends IncrementalScoreCalculator> incrementalScoreCalculatorClass;

    public IncrementalScoreDirectorFactory(Class<? extends IncrementalScoreCalculator> incrementalScoreCalculatorClass) {
        this.incrementalScoreCalculatorClass = incrementalScoreCalculatorClass;
    }

    public Class<? extends IncrementalScoreCalculator> getIncrementalScoreCalculatorClass() {
        return incrementalScoreCalculatorClass;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public IncrementalScoreDirector buildScoreDirector() {
        IncrementalScoreCalculator incrementalScoreCalculator = ConfigUtils.newInstance(this,
                "incrementalScoreCalculatorClass", incrementalScoreCalculatorClass);
        return new IncrementalScoreDirector(this, incrementalScoreCalculator);
    }

}
