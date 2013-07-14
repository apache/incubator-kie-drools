package org.drools.scorecards;

import java.io.InputStream;

import org.drools.compiler.compiler.ScoreCardProvider;
import org.drools.core.util.StringUtils;
import org.kie.internal.builder.ScoreCardConfiguration;

public class ScoreCardProviderImpl
        implements
        ScoreCardProvider {

    public String loadFromInputStream( InputStream is,
                                       ScoreCardConfiguration configuration ) {

        return compileStream( is,
                              configuration );
    }

    private String compileStream( InputStream is,
                                  ScoreCardConfiguration configuration ) {

        ScorecardCompiler scorecardCompiler = new ScorecardCompiler();
        if ( configuration.IsUsingExternalTypes() ) {
            scorecardCompiler.setDrlType(ScorecardCompiler.DrlType.EXTERNAL_OBJECT_MODEL);
        }
        String inputTypeExcel = ScoreCardConfiguration.SCORECARD_INPUT_TYPE.EXCEL.toString();
        if ( configuration.getInputType() == null || inputTypeExcel.equalsIgnoreCase(configuration.getInputType())) {
            if ( StringUtils.isEmpty( configuration.getWorksheetName() ) ) {
                boolean compileResult = scorecardCompiler.compileFromExcel( is );
                return scorecardCompiler.getDRL();
            }

            boolean compileResult = scorecardCompiler.compileFromExcel( is, configuration.getWorksheetName() );

        } else if (ScoreCardConfiguration.SCORECARD_INPUT_TYPE.PMML.toString().equalsIgnoreCase(configuration.getInputType())){
            scorecardCompiler.compileFromPMML(is);
        }
        return scorecardCompiler.getDRL();
    }

}
