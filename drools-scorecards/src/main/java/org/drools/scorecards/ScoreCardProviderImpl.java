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
        if ( StringUtils.isEmpty( configuration.getWorksheetName() ) ) {
            boolean compileResult = scorecardCompiler.compileFromExcel( is );
            return scorecardCompiler.getDRL();
        }

        boolean compileResult = scorecardCompiler.compileFromExcel( is, configuration.getWorksheetName() );
        return scorecardCompiler.getDRL();
    }

}
