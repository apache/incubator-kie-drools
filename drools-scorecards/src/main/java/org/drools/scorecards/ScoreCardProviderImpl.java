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
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.ScoreCardConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

public class ScoreCardProviderImpl
        implements
        ScoreCardProvider {

    @Deprecated
    public String loadFromInputStream(InputStream is,
                                      ScoreCardConfiguration configuration) {

        return compileStream(is,
                             configuration);
    }

    @Override
    public KieBase getKieBaseFromInputStream(InputStream is, ScoreCardConfiguration configuration) {
        String pmmlString = getPMMLStringFromInputStream(is, configuration);
        if (pmmlString != null && !pmmlString.isEmpty()) {
            Resource resource = ResourceFactory.newByteArrayResource(pmmlString.getBytes());
            KieBase kbase = new KieHelper().addResource(resource, ResourceType.PMML).build();
            return kbase;
        }
        return null;
    }

    @Override
    public String getPMMLStringFromInputStream(InputStream is, ScoreCardConfiguration configuration) {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler();
        if (configuration != null && configuration.IsUsingExternalTypes()) {
            scorecardCompiler.setDrlType(ScorecardCompiler.DrlType.EXTERNAL_OBJECT_MODEL);
        }
        String inputTypeExcel = ScoreCardConfiguration.SCORECARD_INPUT_TYPE.EXCEL.toString();
        if (configuration == null || configuration.getInputType() == null || inputTypeExcel.equalsIgnoreCase(configuration.getInputType())) {
            boolean compileResult = false;
            if (configuration == null || StringUtils.isEmpty(configuration.getWorksheetName())) {
                compileResult = scorecardCompiler.compileFromExcel(is);
            } else {
                compileResult = scorecardCompiler.compileFromExcel(is, configuration.getWorksheetName());
            }
            if (compileResult) {
                return scorecardCompiler.getPMML();
            }
        }
        return null;
    }

    @Deprecated
    private String compileStream(InputStream is,
                                 ScoreCardConfiguration configuration) {

        ScorecardCompiler scorecardCompiler = new ScorecardCompiler();
        if (configuration != null && configuration.IsUsingExternalTypes()) {
            scorecardCompiler.setDrlType(ScorecardCompiler.DrlType.EXTERNAL_OBJECT_MODEL);
        }
        String inputTypeExcel = ScoreCardConfiguration.SCORECARD_INPUT_TYPE.EXCEL.toString();
        if (configuration == null || configuration.getInputType() == null || inputTypeExcel.equalsIgnoreCase(configuration.getInputType())) {
            if (configuration == null || StringUtils.isEmpty(configuration.getWorksheetName())) {
                scorecardCompiler.compileFromExcel(is);
                return scorecardCompiler.getDRL();
            }

            scorecardCompiler.compileFromExcel(is, configuration.getWorksheetName());
        } else if (ScoreCardConfiguration.SCORECARD_INPUT_TYPE.PMML.toString().equalsIgnoreCase(configuration.getInputType())) {
            scorecardCompiler.compileFromPMML(is);
        }
        return scorecardCompiler.getDRL();
    }
}
