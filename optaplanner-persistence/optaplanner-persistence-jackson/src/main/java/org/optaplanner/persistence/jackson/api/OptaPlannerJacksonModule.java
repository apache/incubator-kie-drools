/*
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

package org.optaplanner.persistence.jackson.api;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.persistence.jackson.api.score.PolymorphicScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.PolymorphicScoreJacksonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.bendable.BendableScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.bendable.BendableScoreJacksonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.bendablebigdecimal.BendableBigDecimalScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.bendablebigdecimal.BendableBigDecimalScoreJacksonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.bendablelong.BendableLongScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.bendablelong.BendableLongScoreJacksonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardmediumsoft.HardMediumSoftScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardmediumsoft.HardMediumSoftScoreJsonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScoreJacksonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreJacksonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardsoft.HardSoftScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardsoft.HardSoftScoreJacksonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreJacksonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardsoftlong.HardSoftLongScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardsoftlong.HardSoftLongScoreJacksonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.simple.SimpleScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.simple.SimpleScoreJacksonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.simplebigdecimal.SimpleBigDecimalScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.simplebigdecimal.SimpleBigDecimalScoreJacksonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.simplelong.SimpleLongScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.simplelong.SimpleLongScoreJacksonSerializer;
import org.optaplanner.persistence.jackson.impl.domain.solution.JacksonSolutionFileIO;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * This class adds all Jackson serializers and deserializers.
 */
public class OptaPlannerJacksonModule extends SimpleModule {

    /**
     * Jackson modules can be loaded automatically via {@link java.util.ServiceLoader}.
     * This will happen if you use {@link JacksonSolutionFileIO}.
     * Otherwise, register the module with {@link ObjectMapper#registerModule(Module)}.
     *
     * @return never null
     */
    public static Module createModule() {
        return new OptaPlannerJacksonModule();

    }

    /**
     * @deprecated Have the module loaded automatically via {@link JacksonSolutionFileIO} or use {@link #createModule()}.
     *             This constructor will be hidden in a future major version of OptaPlanner.
     */
    @Deprecated(forRemoval = true)
    public OptaPlannerJacksonModule() {
        super("OptaPlanner");
        // For non-subtype Score fields/properties, we also need to record the score type
        addSerializer(Score.class, new PolymorphicScoreJacksonSerializer());
        addDeserializer(Score.class, new PolymorphicScoreJacksonDeserializer());

        addSerializer(SimpleScore.class, new SimpleScoreJacksonSerializer());
        addDeserializer(SimpleScore.class, new SimpleScoreJacksonDeserializer());
        addSerializer(SimpleLongScore.class, new SimpleLongScoreJacksonSerializer());
        addDeserializer(SimpleLongScore.class, new SimpleLongScoreJacksonDeserializer());
        addSerializer(SimpleBigDecimalScore.class, new SimpleBigDecimalScoreJacksonSerializer());
        addDeserializer(SimpleBigDecimalScore.class, new SimpleBigDecimalScoreJacksonDeserializer());
        addSerializer(HardSoftScore.class, new HardSoftScoreJacksonSerializer());
        addDeserializer(HardSoftScore.class, new HardSoftScoreJacksonDeserializer());
        addSerializer(HardSoftLongScore.class, new HardSoftLongScoreJacksonSerializer());
        addDeserializer(HardSoftLongScore.class, new HardSoftLongScoreJacksonDeserializer());
        addSerializer(HardSoftBigDecimalScore.class, new HardSoftBigDecimalScoreJacksonSerializer());
        addDeserializer(HardSoftBigDecimalScore.class, new HardSoftBigDecimalScoreJacksonDeserializer());
        addSerializer(HardMediumSoftScore.class, new HardMediumSoftScoreJsonSerializer());
        addDeserializer(HardMediumSoftScore.class, new HardMediumSoftScoreJacksonDeserializer());
        addSerializer(HardMediumSoftLongScore.class, new HardMediumSoftLongScoreJacksonSerializer());
        addDeserializer(HardMediumSoftLongScore.class, new HardMediumSoftLongScoreJacksonDeserializer());
        addSerializer(HardMediumSoftBigDecimalScore.class, new HardMediumSoftBigDecimalScoreJacksonSerializer());
        addDeserializer(HardMediumSoftBigDecimalScore.class, new HardMediumSoftBigDecimalScoreJacksonDeserializer());
        addSerializer(BendableScore.class, new BendableScoreJacksonSerializer());
        addDeserializer(BendableScore.class, new BendableScoreJacksonDeserializer());
        addSerializer(BendableLongScore.class, new BendableLongScoreJacksonSerializer());
        addDeserializer(BendableLongScore.class, new BendableLongScoreJacksonDeserializer());
        addSerializer(BendableBigDecimalScore.class, new BendableBigDecimalScoreJacksonSerializer());
        addDeserializer(BendableBigDecimalScore.class, new BendableBigDecimalScoreJacksonDeserializer());
    }

}
