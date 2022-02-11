/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.models.scorecard.compiler;

import java.util.Map;

import org.dmg.pmml.scorecard.Characteristics;
import org.dmg.pmml.scorecard.Scorecard;
import org.kie.pmml.api.enums.REASONCODE_ALGORITHM;
import org.kie.pmml.commons.utils.KiePMMLModelUtils;
import org.kie.pmml.compiler.commons.dto.AbstractSpecificCompilationDTO;
import org.kie.pmml.compiler.api.dto.CompilationDTO;

import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;

public class ScorecardCompilationDTO extends AbstractSpecificCompilationDTO<Scorecard> {

    private static final long serialVersionUID = -1797184111198269074L;
    private final Scorecard scorecardModel;
    private final Scorecard.ReasonCodeAlgorithm reasonCodeAlgorithm;
    private final String characteristicsClassName;
    private final String packageCharacteristicsClassName;

    /**
     * Private constructor that use given <code>CommonCompilationDTO</code>
     * @param source
     */
    private ScorecardCompilationDTO(final CompilationDTO<Scorecard> source) {
        super(source);
        this.scorecardModel = source.getModel();
        this.reasonCodeAlgorithm = scorecardModel.getReasonCodeAlgorithm();
        characteristicsClassName = KiePMMLModelUtils.getGeneratedClassName("Characteristics");
        packageCharacteristicsClassName = String.format(PACKAGE_CLASS_TEMPLATE, getPackageName(),
                                                        characteristicsClassName);
    }

    /**
     * Builder that use given <code>CommonCompilationDTO</code>
     * @param source
     */
    public static ScorecardCompilationDTO fromCompilationDTO(final CompilationDTO<Scorecard> source) {
        return new ScorecardCompilationDTO(source);
    }

    /**
     * Compile the given sources and add them to given <code>Classloader</code> of the current instance.
     * Returns the <code>Class</code> with the current <b>packageCharacteristicsClassName</b>
     * @param sourcesMap
     * @return
     */
    public Class<?> compileAndLoadCharacteristicsClass(Map<String, String> sourcesMap) {
        return getHasClassloader().compileAndLoadClass(sourcesMap, packageCharacteristicsClassName);
    }

    public Scorecard getScorecardModel() {
        return scorecardModel;
    }

    public Number getInitialScore() {
        return scorecardModel.getInitialScore();
    }

    public boolean isUseReasonCodes() {
        return scorecardModel.isUseReasonCodes();
    }

    public Scorecard.ReasonCodeAlgorithm getReasonCodeAlgorithm() {
        return reasonCodeAlgorithm;
    }

    public REASONCODE_ALGORITHM getREASONCODE_ALGORITHM() {
        return REASONCODE_ALGORITHM.byName(reasonCodeAlgorithm.value());
    }

    public Number getBaselineScore() {
        return scorecardModel.getBaselineScore();
    }

    public Characteristics getCharacteristics() {
        return scorecardModel.getCharacteristics();
    }

    public String getCharacteristicsClassName() {
        return characteristicsClassName;
    }

    public String getPackageCanonicalCharacteristicsClassName() {
        return packageCharacteristicsClassName;
    }
}
