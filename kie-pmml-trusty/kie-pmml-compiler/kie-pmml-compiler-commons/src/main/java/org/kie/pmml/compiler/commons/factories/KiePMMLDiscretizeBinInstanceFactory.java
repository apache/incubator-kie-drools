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

import org.dmg.pmml.DiscretizeBin;
import org.kie.pmml.commons.model.expressions.KiePMMLDiscretizeBin;
import org.kie.pmml.commons.model.expressions.KiePMMLInterval;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLDiscretizeBin</code> instance
 * out of <code>DiscretizeBin</code>s
 */
public class KiePMMLDiscretizeBinInstanceFactory {

    private KiePMMLDiscretizeBinInstanceFactory() {
        // Avoid instantiation
    }

    static List<KiePMMLDiscretizeBin> getKiePMMLDiscretizeBins(final List<DiscretizeBin> discretizeBins) {
        return discretizeBins.stream().map(KiePMMLDiscretizeBinInstanceFactory::getKiePMMLDiscretizeBin).collect(Collectors.toList());
    }

    static KiePMMLDiscretizeBin getKiePMMLDiscretizeBin(final DiscretizeBin discretizeBin) {
        KiePMMLInterval interval = KiePMMLIntervalInstanceFactory.getKiePMMLInterval(discretizeBin.getInterval());
        String binValue = discretizeBin.getBinValue() != null ? discretizeBin.getBinValue().toString() : null;
        return new KiePMMLDiscretizeBin(UUID.randomUUID().toString(),
                                        getKiePMMLExtensions(discretizeBin.getExtensions()),
                                        binValue,
                                        interval);
    }
}
