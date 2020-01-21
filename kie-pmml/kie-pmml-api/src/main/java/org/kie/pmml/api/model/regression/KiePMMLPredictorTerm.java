/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.api.model.regression;

import java.util.List;

import org.kie.pmml.api.model.KiePMMLExtension;
import org.kie.pmml.api.model.abstracts.KiePMMLExtensionedTerm;
import org.kie.pmml.api.model.expressions.KiePMMLFieldRef;

public class KiePMMLPredictorTerm extends KiePMMLExtensionedTerm {

    private static final long serialVersionUID = 4077271967051895553L;
    private List<KiePMMLFieldRef> fieldRefs;
    private Number coefficient;

    public KiePMMLPredictorTerm(String name, List<KiePMMLFieldRef> fieldRefs, Number coefficient, List<KiePMMLExtension> extensions) {
        super(name, extensions);
        this.fieldRefs = fieldRefs;
        this.coefficient = coefficient;
    }

    @Override
    public Number getCoefficient() {
        return coefficient;
    }

    public List<KiePMMLFieldRef> getFieldRefs() {
        return fieldRefs;
    }
}
