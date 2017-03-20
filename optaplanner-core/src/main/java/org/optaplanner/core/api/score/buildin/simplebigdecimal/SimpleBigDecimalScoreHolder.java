/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.simplebigdecimal;

import java.math.BigDecimal;

import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * @see SimpleBigDecimalScore
 */
public class SimpleBigDecimalScoreHolder extends AbstractScoreHolder {

    protected BigDecimal score = null;

    public SimpleBigDecimalScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled, SimpleBigDecimalScore.ZERO);
    }

    public BigDecimal getScore() {
        return score;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param weight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addConstraintMatch(RuleContext kcontext, BigDecimal weight) {
        score = (score == null) ? weight : score.add(weight);
        registerConstraintMatch(kcontext,
                () -> score = score.subtract(weight),
                () -> SimpleBigDecimalScore.valueOf(weight));
    }

    @Override
    public Score extractScore(int initScore) {
        return SimpleBigDecimalScore.valueOfUninitialized(initScore,
                score == null ? BigDecimal.ZERO : score);
    }

}
