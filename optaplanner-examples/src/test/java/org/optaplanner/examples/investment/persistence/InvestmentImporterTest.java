package org.optaplanner.examples.investment.persistence;

import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.ImportDataFilesTest;
import org.optaplanner.examples.investment.app.InvestmentApp;
import org.optaplanner.examples.investment.domain.InvestmentSolution;

class InvestmentImporterTest extends ImportDataFilesTest<InvestmentSolution> {

    @Override
    protected AbstractSolutionImporter<InvestmentSolution> createSolutionImporter() {
        return new InvestmentImporter();
    }

    @Override
    protected String getDataDirName() {
        return InvestmentApp.DATA_DIR_NAME;
    }
}
