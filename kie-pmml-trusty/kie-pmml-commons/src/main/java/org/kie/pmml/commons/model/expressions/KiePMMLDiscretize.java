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
package org.kie.pmml.commons.model.expressions;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

import static org.kie.pmml.commons.model.expressions.ExpressionsUtils.getFromPossibleSources;

public class KiePMMLDiscretize extends AbstractKiePMMLComponent implements KiePMMLExpression {

    private static final long serialVersionUID = -7935602676734880795L;

    private final List<KiePMMLDiscretizeBin> discretizeBins;
    private final String mapMissingTo;
    private final String defaultValue;
    private final DATA_TYPE dataType;

    public KiePMMLDiscretize(final String name,
                             final List<KiePMMLExtension> extensions,
                             final List<KiePMMLDiscretizeBin> discretizeBins,
                             final String mapMissingTo,
                             final String defaultValue,
                             final DATA_TYPE dataType) {
        super(name, extensions);
        this.discretizeBins = discretizeBins;
        this.mapMissingTo = mapMissingTo;
        this.defaultValue = defaultValue;
        this.dataType = dataType;
    }

    public List<KiePMMLDiscretizeBin> getDiscretizeBins() {
        return Collections.unmodifiableList(discretizeBins);
    }

    public String getMapMissingTo() {
        return mapMissingTo;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public DATA_TYPE getDataType() {
        return dataType;
    }

    @Override
    public Object evaluate(final ProcessingDTO processingDTO) {
        Number input = (Number) getFromPossibleSources(name, processingDTO)
                .orElse(null);
        if (input == null) {
            return mapMissingTo;
        }
        return getFromDiscretizeBins(input).orElse(defaultValue);
    }

    Optional<String> getFromDiscretizeBins(Number toEvaluate) {
        return discretizeBins
                .stream()
                .map(kiePMMLNameValue -> kiePMMLNameValue.evaluate(toEvaluate))
                .filter(Optional::isPresent)
                .findFirst()
                .map(Optional::get);
    }
}
