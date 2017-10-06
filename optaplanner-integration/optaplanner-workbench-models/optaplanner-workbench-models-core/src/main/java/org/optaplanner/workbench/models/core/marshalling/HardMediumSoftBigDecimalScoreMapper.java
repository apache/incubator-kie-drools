/*
 * Copyright 2017 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.workbench.models.core.marshalling;

import java.math.BigDecimal;

import org.jboss.errai.codegen.meta.impl.java.JavaReflectionMethod;
import org.jboss.errai.marshalling.rebind.api.CustomMapping;
import org.jboss.errai.marshalling.rebind.api.model.MappingDefinition;
import org.jboss.errai.marshalling.rebind.api.model.impl.ReadMapping;
import org.jboss.errai.marshalling.rebind.api.model.impl.SimpleFactoryMapping;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;

@CustomMapping(HardMediumSoftBigDecimalScore.class)
public class HardMediumSoftBigDecimalScoreMapper extends MappingDefinition {

    public HardMediumSoftBigDecimalScoreMapper() throws NoSuchMethodException {
        super(HardMediumSoftBigDecimalScore.class);

        SimpleFactoryMapping factoryMapping = new SimpleFactoryMapping();
        factoryMapping.setMethod(new JavaReflectionMethod(HardMediumSoftBigDecimalScore.class.getMethod("valueOfUninitialized",
                                                                                                        int.class,
                                                                                                        BigDecimal.class,
                                                                                                        BigDecimal.class,
                                                                                                        BigDecimal.class)));
        factoryMapping.mapParmToIndex("initScore",
                                      0,
                                      int.class);
        factoryMapping.mapParmToIndex("hardScore",
                                      1,
                                      BigDecimal.class);
        factoryMapping.mapParmToIndex("mediumScore",
                                      2,
                                      BigDecimal.class);
        factoryMapping.mapParmToIndex("softScore",
                                      3,
                                      BigDecimal.class);

        setInstantiationMapping(factoryMapping);

        addMemberMapping(new ReadMapping("initScore",
                                         int.class,
                                         "getInitScore"));
        addMemberMapping(new ReadMapping("hardScore",
                                         BigDecimal.class,
                                         "getHardScore"));
        addMemberMapping(new ReadMapping("mediumScore",
                                         BigDecimal.class,
                                         "getMediumScore"));
        addMemberMapping(new ReadMapping("softScore",
                                         BigDecimal.class,
                                         "getSoftScore"));
    }
}
