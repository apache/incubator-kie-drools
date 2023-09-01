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
import java.util.stream.Collectors;

import org.dmg.pmml.LinearNorm;
import org.kie.pmml.commons.model.expressions.KiePMMLLinearNorm;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLLinearNorm</code> instance
 * out of <code>LinearNorm</code>s
 */
public class KiePMMLLinearNormInstanceFactory {

    private KiePMMLLinearNormInstanceFactory() {
        // Avoid instantiation
    }

    static List<KiePMMLLinearNorm> getKiePMMLLinearNorms(List<LinearNorm> linearNorms) {
        return linearNorms.stream().map(KiePMMLLinearNormInstanceFactory::getKiePMMLLinearNorm).collect(Collectors.toList());
    }

    static KiePMMLLinearNorm getKiePMMLLinearNorm(LinearNorm linearNorm) {
        return new KiePMMLLinearNorm(UUID.randomUUID().toString(), getKiePMMLExtensions(linearNorm.getExtensions()),
                                     linearNorm.getOrig().doubleValue(),
                                     linearNorm.getNorm().doubleValue());
    }
}
