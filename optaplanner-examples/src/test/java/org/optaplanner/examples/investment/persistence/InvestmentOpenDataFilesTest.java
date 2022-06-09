package org.optaplanner.examples.investment.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.investment.app.InvestmentApp;
import org.optaplanner.examples.investment.domain.InvestmentSolution;

class InvestmentOpenDataFilesTest extends OpenDataFilesTest<InvestmentSolution> {

    @Override
    protected CommonApp<InvestmentSolution> createCommonApp() {
        return new InvestmentApp();
    }
}
