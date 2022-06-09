package org.optaplanner.examples.investment.persistence;

import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class InvestmentXmlSolutionFileIO extends XStreamSolutionFileIO<InvestmentSolution> {

    public InvestmentXmlSolutionFileIO() {
        super(InvestmentSolution.class);
    }
}
