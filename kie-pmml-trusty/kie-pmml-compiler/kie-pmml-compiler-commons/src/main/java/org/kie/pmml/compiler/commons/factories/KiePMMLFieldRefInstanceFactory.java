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

import org.dmg.pmml.FieldRef;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLFieldRef</code> instance
 * out of <code>FieldRef</code>s
 */
public class KiePMMLFieldRefInstanceFactory {

    private KiePMMLFieldRefInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLFieldRef getKiePMMLFieldRef(final FieldRef fieldRef) {
        return new KiePMMLFieldRef(fieldRef.getField().getValue(),
                                   KiePMMLExtensionInstanceFactory.getKiePMMLExtensions(fieldRef.getExtensions()),
                                   fieldRef.getMapMissingTo());
    }
}
