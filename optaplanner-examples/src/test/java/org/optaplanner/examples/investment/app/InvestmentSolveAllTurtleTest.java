/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.investment.app;

import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.solver.score.InvestmentEasyScoreCalculator;

public class InvestmentSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<InvestmentSolution> {

    @Override
    protected CommonApp<InvestmentSolution> createCommonApp() {
        return new InvestmentApp();
    }

    @Override
    protected Class<? extends EasyScoreCalculator> overwritingEasyScoreCalculatorClass() {
        return InvestmentEasyScoreCalculator.class;
    }
}
