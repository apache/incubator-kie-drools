/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
        if ( configuration != null && configuration.IsUsingExternalTypes() ) {
            scorecardCompiler.setDrlType(ScorecardCompiler.DrlType.EXTERNAL_OBJECT_MODEL);
        }
        String inputTypeExcel = ScoreCardConfiguration.SCORECARD_INPUT_TYPE.EXCEL.toString();
        if ( configuration== null || configuration.getInputType() == null || inputTypeExcel.equalsIgnoreCase(configuration.getInputType())) {
            if ( configuration == null || StringUtils.isEmpty( configuration.getWorksheetName() ) ) {
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
