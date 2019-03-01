package org.kie.dmn.validation.dtanalysis;

import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.printer.PrettyPrinterConfiguration;
import org.kie.dmn.validation.AbstractValidatorTest;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.utils.DTAnalysisMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDTAnalysisTest extends AbstractValidatorTest {

    public static final Logger LOG = LoggerFactory.getLogger(AbstractDTAnalysisTest.class);

    protected void debugAnalysis(DTAnalysis analysis) {
        StringBuilder sbGaps = new StringBuilder("\nGaps:\n");
        for (Hyperrectangle gap : analysis.getGaps()) {
            sbGaps.append(gap.toString());
            sbGaps.append("\n");
        }
        LOG.debug(sbGaps.toString());
        Expression printGaps = DTAnalysisMeta.printGaps(analysis);
        PrettyPrinterConfiguration prettyPrintConfig = new PrettyPrinterConfiguration();
        prettyPrintConfig.setColumnAlignFirstMethodChain(true);
        prettyPrintConfig.setColumnAlignParameters(true);
        LOG.debug("\n" + printGaps.toString(prettyPrintConfig));
    }
}
