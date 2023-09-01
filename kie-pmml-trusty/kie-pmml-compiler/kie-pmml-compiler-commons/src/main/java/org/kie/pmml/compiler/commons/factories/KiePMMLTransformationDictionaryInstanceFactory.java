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
package org.kie.pmml.compiler.commons.factories;

import java.util.List;
import java.util.UUID;

import org.dmg.pmml.Field;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLTransformationDictionary;

import static org.kie.pmml.compiler.commons.factories.KiePMMLDefineFunctionInstanceFactory.getKiePMMLDefineFunctions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLDerivedFieldInstanceFactory.getKiePMMLDerivedFields;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> methods to retrieve <code>KiePMMLTransformationDictionary</code> instance
 * out of <code>TransformationDictionary</code>s
 */
public class KiePMMLTransformationDictionaryInstanceFactory {

    private KiePMMLTransformationDictionaryInstanceFactory() {
        // Avoid instantiation
    }

    public static KiePMMLTransformationDictionary getKiePMMLTransformationDictionary(final TransformationDictionary toConvert,
                                                                                     final List<Field<?>> fields) {
        final List<KiePMMLDerivedField> kiePMMLDerivedFields = getKiePMMLDerivedFields(toConvert.getDerivedFields(),
                                                                                       fields);
        final List<KiePMMLDefineFunction> kiePMMLDefineFunctions =
                getKiePMMLDefineFunctions(toConvert.getDefineFunctions());
        return KiePMMLTransformationDictionary.builder(UUID.randomUUID().toString(), getKiePMMLExtensions(toConvert.getExtensions()))
                .withDefineFunctions(kiePMMLDefineFunctions)
                .withDerivedFields(kiePMMLDerivedFields)
                .build();
    }
}
