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
package org.kie.pmml.compiler.commons.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.dmg.pmml.Model;
import org.dmg.pmml.OutputField;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.api.enums.RESULT_FEATURE;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;

public class KiePMMLOutputFieldFactory {

    private KiePMMLOutputFieldFactory() {
    }

    public static List<KiePMMLOutputField> getOutputFields(Model model) {
        List<KiePMMLOutputField> outputFields = new ArrayList<>();
        if (model.getOutput() != null) {
            outputFields.addAll(model.getOutput().getOutputFields().stream().map(KiePMMLOutputFieldFactory::getKiePMMLOutputField).collect(Collectors.toList()));
        }
        return outputFields;
    }

    public static KiePMMLOutputField getKiePMMLOutputField(OutputField outputField) {
        return KiePMMLOutputField.builder(outputField.getName().getValue(), getKiePMMLExtensions(outputField.getExtensions()))
                .withResultFeature(RESULT_FEATURE.byName(outputField.getResultFeature().value()))
                .withTargetField(outputField.getTargetField() != null ? outputField.getTargetField().getValue() : null)
                .withValue(outputField.getValue())
                .withRank(outputField.getRank())
                .build();
    }
}
