/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.definition;

import java.io.Serializable;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;

/**
 * Abstract superclass for {@link ScoreDefinition}.
 * @see ScoreDefinition
 * @see HardSoftScoreDefinition
 */
public abstract class AbstractScoreDefinition<S extends Score<S>> implements ScoreDefinition<S>, Serializable {

    private final String[] levelLabels;

    /**
     * @param levelLabels never null, as defined by {@link ScoreDefinition#getLevelLabels()}
     */
    public AbstractScoreDefinition(String[] levelLabels) {
        this.levelLabels = levelLabels;
    }

    @Override
    public String getInitLabel() {
        return "init score";
    }

    @Override
    public int getLevelsSize() {
        return levelLabels.length;
    }

    @Override
    public String[] getLevelLabels() {
        return levelLabels;
    }

    @Override
    public String formatScore(S score) {
        return score.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
