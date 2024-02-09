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

import java.util.UUID;

import org.dmg.pmml.MapValues;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.commons.model.expressions.KiePMMLMapValues;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLFieldColumnPairInstanceFactory.getKiePMMLFieldColumnPairs;
import static org.kie.pmml.compiler.commons.factories.KiePMMLInlineTableInstanceFactory.getKiePMMLInlineTable;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLMapValues</code> instance
 * out of <code>MapValues</code>s
 */
public class KiePMMLMapValuesInstanceFactory {

    private KiePMMLMapValuesInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLMapValues getKiePMMLMapValues(final MapValues mapValues) {
        DATA_TYPE dataType = mapValues.getDataType() != null ? DATA_TYPE.byName(mapValues.getDataType().value()) : null;
        KiePMMLMapValues.Builder builder = KiePMMLMapValues.builder(UUID.randomUUID().toString(),
                                                                    getKiePMMLExtensions(mapValues.getExtensions()),
                                                                    mapValues.getOutputColumn())
                .withKiePMMLInlineTable(getKiePMMLInlineTable(mapValues.getInlineTable()))
                .withDataType(dataType);
        if (mapValues.getDefaultValue() != null) {
            builder = builder.withDefaultValue(mapValues.getDefaultValue().toString());
        }
        if (mapValues.getMapMissingTo() != null) {
            builder = builder.withMapMissingTo(mapValues.getMapMissingTo().toString());
        }
        if (mapValues.hasFieldColumnPairs()) {
            builder = builder.withKiePMMLFieldColumnPairs(getKiePMMLFieldColumnPairs(mapValues.getFieldColumnPairs()));
        }
        return builder.build();
    }
}
