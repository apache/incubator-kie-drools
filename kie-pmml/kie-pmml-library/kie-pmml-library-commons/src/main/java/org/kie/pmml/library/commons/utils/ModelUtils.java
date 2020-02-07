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
package org.kie.pmml.library.commons.utils;

import java.util.Optional;

import org.dmg.pmml.MiningField;
import org.dmg.pmml.Model;

/**
 * Class to provide common methods to interact with <code>Model</code>
 */
public class ModelUtils {

    /**
     * Return an <code>Optional</code> with the name of the field whose <b>usageType</b> is <code>TARGET</code> or <code>PREDICTED</code>
     *
     * While the xsd schema does not strictly enforce this, it seems that <b>by convention</b> majority of models has only one target.
     *
     * (see https://github.com/jpmml/jpmml-evaluator/issues/64 discussion)
     * @param model
     * @return
     */
    public static Optional<String> getTargetField(Model model) {
        return model.getMiningSchema().getMiningFields().stream()
                .filter(miningField -> MiningField.UsageType.TARGET.equals(miningField.getUsageType()) || MiningField.UsageType.PREDICTED.equals(miningField.getUsageType()))
                .map(miningField -> miningField.getName().getValue())
                .findFirst();
    }

}
