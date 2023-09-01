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

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.dmg.pmml.Field;
import org.dmg.pmml.LocalTransformations;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLLocalTransformations;

import static org.kie.pmml.compiler.commons.factories.KiePMMLDerivedFieldInstanceFactory.getKiePMMLDerivedField;

/**
 * Class meant to provide <i>helper</i> methods to retrieve <code>KiePMMLLocalTransformations</code> code-generators
 * out of <code>LocalTransformations</code>s
 */
public class KiePMMLLocalTransformationsInstanceFactory {

    private KiePMMLLocalTransformationsInstanceFactory() {
        // Avoid instantiation
    }

    public static KiePMMLLocalTransformations getKiePMMLLocalTransformations(final LocalTransformations localTransformations,
                                                                      final List<Field<?>> fields) {
        final List<KiePMMLDerivedField> kiePMMLDerivedFields =
                localTransformations.getDerivedFields().stream()
                        .map(derivedField -> getKiePMMLDerivedField(derivedField, fields))
                        .collect(Collectors.toList());
        return KiePMMLLocalTransformations.builder(UUID.randomUUID().toString(), Collections.emptyList())
                .withDerivedFields(kiePMMLDerivedFields)
                .build();
    }

}
