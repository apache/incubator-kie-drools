package org.optaplanner.examples.investment.app;

import java.util.Collections;
import java.util.Set;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.persistence.InvestmentImporter;
import org.optaplanner.examples.investment.persistence.InvestmentXmlSolutionFileIO;
import org.optaplanner.examples.investment.swingui.InvestmentPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class InvestmentApp extends CommonApp<InvestmentSolution> {

    public static final String SOLVER_CONFIG = "org/optaplanner/examples/investment/investmentSolverConfig.xml";

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
    protected Set<AbstractSolutionImporter<InvestmentSolution>> createSolutionImporters() {
        return Collections.singleton(new InvestmentImporter());
    }

}
