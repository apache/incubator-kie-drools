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

import java.util.List;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

import static org.kie.pmml.commons.model.expressions.ExpressionsUtils.getFromPossibleSources;

public class KiePMMLNormDiscrete extends AbstractKiePMMLComponent implements KiePMMLExpression {

    private static final long serialVersionUID = -7935602676734880795L;
    
    private final String value;
    private final Number mapMissingTo;

    public KiePMMLNormDiscrete(final String name,
                               final List<KiePMMLExtension> extensions,
                               final String value,
                               final Number mapMissingTo) {
        super(name, extensions);
        this.value = value;
        this.mapMissingTo = mapMissingTo;
    }

    public String getValue() {
        return value;
    }

    public Number getMapMissingTo() {
        return mapMissingTo;
    }

    @Override
    public Object evaluate(final ProcessingDTO processingDTO) {
        String input = (String) getFromPossibleSources(name, processingDTO)
                .orElse(null);
        if (input == null) {
            return mapMissingTo;
        }
        return input.equals(value) ? 1.0 : 0.0;
    }

}
