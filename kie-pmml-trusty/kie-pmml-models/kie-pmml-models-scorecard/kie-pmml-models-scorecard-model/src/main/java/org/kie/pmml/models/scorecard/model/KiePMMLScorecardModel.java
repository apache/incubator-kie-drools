/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.scorecard.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kie.pmml.api.enums.REASONCODE_ALGORITHM;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;

public class KiePMMLScorecardModel extends KiePMMLModel {

    private static final long serialVersionUID = 1798360806171346217L;

    /**
     * The first <code>Map</code> is the input data, the second <code>Map</code> is the <b>outputfieldsmap</b>
     */
    protected final KiePMMLCharacteristics characteristics;
    protected final Number initialScore;
    protected final boolean useReasonCodes;
    protected final REASONCODE_ALGORITHM reasonCodeAlgorithm ;
    protected final Number baselineScore;

    public KiePMMLScorecardModel(final String fileName,
                                 final String modelName,
                                 final List<KiePMMLExtension> extensions,
                                 final KiePMMLCharacteristics characteristics,
                                 final Number initialScore,
                                 final boolean useReasonCodes,
                                 final REASONCODE_ALGORITHM reasonCodeAlgorithm,
                                 final Number baselineScore) {
        super(fileName, modelName, extensions);
        this.characteristics = characteristics;
        this.initialScore = initialScore;
        this.useReasonCodes = useReasonCodes;
        this.reasonCodeAlgorithm = reasonCodeAlgorithm;
        this.baselineScore = baselineScore;
    }

    @Override
    public Object evaluate(final Map<String, Object> requestData,
                           final PMMLRuntimeContext context) {
        final List<KiePMMLDefineFunction> defineFunctions = transformationDictionary != null ?
                transformationDictionary.getDefineFunctions() : Collections.emptyList();
        final List<KiePMMLDerivedField> derivedFields = new ArrayList<>();
        if (transformationDictionary != null) {
            derivedFields.addAll(transformationDictionary.getDerivedFields());
        }
        if (localTransformations != null) {
            derivedFields.addAll(localTransformations.getDerivedFields());
        }
        return characteristics.evaluate(defineFunctions, derivedFields, kiePMMLOutputFields, requestData,
                                        context,
                                        initialScore,
                                        reasonCodeAlgorithm,
                                        useReasonCodes,
                                        baselineScore).orElse(null);
    }

}
