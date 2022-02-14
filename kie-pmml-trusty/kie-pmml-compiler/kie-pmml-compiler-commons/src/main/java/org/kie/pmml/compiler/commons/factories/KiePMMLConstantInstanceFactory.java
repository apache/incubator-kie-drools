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
package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.UUID;

import org.dmg.pmml.Constant;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLConstant</code> code-generators
 * out of <code>Constant</code>s
 */
public class KiePMMLConstantInstanceFactory {

    private KiePMMLConstantInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLConstant getKiePMMLConstant(final Constant constant) {
        DATA_TYPE dataType = constant.getDataType() != null ? DATA_TYPE.byName(constant.getDataType().value()) : null;
        return new KiePMMLConstant(UUID.randomUUID().toString(), Collections.emptyList(), constant.getValue(), dataType);
    }

}
