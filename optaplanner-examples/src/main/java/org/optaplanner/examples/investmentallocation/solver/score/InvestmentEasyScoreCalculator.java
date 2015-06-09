/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.examples.investmentallocation.solver.score;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.examples.investmentallocation.domain.InvestmentSolution;

public class InvestmentEasyScoreCalculator implements EasyScoreCalculator<InvestmentSolution> {

    public HardSoftLongScore calculateScore(InvestmentSolution solution) {
        long hardScore = 0L;
        long softScore = 0L;
        long squaredFemtosMaximum = solution.getParametrization().calculateSquaredStandardDeviationFemtosMaximum();
        long squaredFemtos = solution.calculateStandardDeviationSquaredFemtos();
        if (squaredFemtos > squaredFemtosMaximum) {
            hardScore -= squaredFemtos - squaredFemtosMaximum;
        }
        softScore += solution.calculateExpectedReturnMicros();
        return HardSoftLongScore.valueOf(hardScore, softScore);
    }

}
