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

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.persistence.InvestmentImporter;
import org.optaplanner.examples.investment.persistence.InvestmentXmlSolutionFileIO;
import org.optaplanner.examples.investment.swingui.InvestmentPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class InvestmentApp extends CommonApp<InvestmentSolution> {

    public static final String SOLVER_CONFIG = "org/optaplanner/examples/investment/solver/investmentSolverConfig.xml";

    public static final String DATA_DIR_NAME = "investment";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new InvestmentApp().init();
    }

    public InvestmentApp() {
        super("Investment allocation",
                "Decide the percentage of the investor's budget to invest in each asset class.\n\n"
                        + "Maximize expected return.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                InvestmentPanel.LOGO_PATH);
    }

    @Override
    protected InvestmentPanel createSolutionPanel() {
        return new InvestmentPanel();
    }

    @Override
    public SolutionFileIO<InvestmentSolution> createSolutionFileIO() {
        return new InvestmentXmlSolutionFileIO();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[] {
                new InvestmentImporter()
        };
    }

}
