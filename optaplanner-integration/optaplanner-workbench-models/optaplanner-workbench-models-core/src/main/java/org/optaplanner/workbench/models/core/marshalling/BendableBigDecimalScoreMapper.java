/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;

@CustomMapping(BendableBigDecimalScore.class)
public class BendableBigDecimalScoreMapper extends MappingDefinition {

    public BendableBigDecimalScoreMapper() throws NoSuchMethodException {
        super(BendableBigDecimalScore.class);

        SimpleFactoryMapping factoryMapping = new SimpleFactoryMapping();
        factoryMapping.setMethod(new JavaReflectionMethod(BendableBigDecimalScore.class.getMethod("valueOfUninitialized",
                                                                                                  int.class,
                                                                                                  BigDecimal[].class,
                                                                                                  BigDecimal[].class)));
        factoryMapping.mapParmToIndex("initScore",
                                      0,
                                      int.class);
        factoryMapping.mapParmToIndex("hardScores",
                                      1,
                                      BigDecimal[].class);
        factoryMapping.mapParmToIndex("softScores",
                                      2,
                                      BigDecimal[].class);

        setInstantiationMapping(factoryMapping);

        addMemberMapping(new ReadMapping("initScore",
                                         int.class,
                                         "getInitScore"));
        addMemberMapping(new ReadMapping("hardScores",
                                         BigDecimal[].class,
                                         "getHardScores"));
        addMemberMapping(new ReadMapping("softScores",
                                         BigDecimal[].class,
                                         "getSoftScores"));
    }
}
