/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.jackson.api;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.persistence.jackson.api.score.PolymorphicScoreJacksonJsonDeserializer;
import org.optaplanner.persistence.jackson.api.score.PolymorphicScoreJacksonJsonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.bendable.BendableScoreJacksonJsonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.bendable.BendableScoreJacksonJsonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.bendablebigdecimal.BendableBigDecimalScoreJacksonJsonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.bendablebigdecimal.BendableBigDecimalScoreJacksonJsonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.bendablelong.BendableLongScoreJacksonJsonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.bendablelong.BendableLongScoreJacksonJsonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardmediumsoft.HardMediumSoftScoreJacksonJsonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardmediumsoft.HardMediumSoftScoreJacksonJsonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreJacksonJsonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreJacksonJsonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardsoft.HardSoftScoreJacksonJsonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardsoft.HardSoftScoreJacksonJsonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreJacksonJsonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreJacksonJsonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardsoftlong.HardSoftLongScoreJacksonJsonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.hardsoftlong.HardSoftLongScoreJacksonJsonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.simple.SimpleScoreJacksonJsonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.simple.SimpleScoreJacksonJsonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.simplebigdecimal.SimpleBigDecimalScoreJacksonJsonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.simplebigdecimal.SimpleBigDecimalScoreJacksonJsonSerializer;
import org.optaplanner.persistence.jackson.api.score.buildin.simplelong.SimpleLongScoreJacksonJsonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.simplelong.SimpleLongScoreJacksonJsonSerializer;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * This class adds all Jackson serializers and deserializers.
 *
 */
public class OptaPlannerJacksonModule {

    /**
     * @return never null, register it with {@link ObjectMapper#registerModule(Module)}.
     */
    public static Module createModule() {
        SimpleModule module = new SimpleModule("OptaPlanner");

        // For non-subtype Score fields/properties, we also need to record the score type
        module.addSerializer(Score.class, new PolymorphicScoreJacksonJsonSerializer());
        module.addDeserializer(Score.class, new PolymorphicScoreJacksonJsonDeserializer());

        module.addSerializer(SimpleScore.class, new SimpleScoreJacksonJsonSerializer());
        module.addDeserializer(SimpleScore.class, new SimpleScoreJacksonJsonDeserializer());
        module.addSerializer(SimpleLongScore.class, new SimpleLongScoreJacksonJsonSerializer());
        module.addDeserializer(SimpleLongScore.class, new SimpleLongScoreJacksonJsonDeserializer());
        module.addSerializer(SimpleBigDecimalScore.class, new SimpleBigDecimalScoreJacksonJsonSerializer());
        module.addDeserializer(SimpleBigDecimalScore.class, new SimpleBigDecimalScoreJacksonJsonDeserializer());
        module.addSerializer(HardSoftScore.class, new HardSoftScoreJacksonJsonSerializer());
        module.addDeserializer(HardSoftScore.class, new HardSoftScoreJacksonJsonDeserializer());
        module.addSerializer(HardSoftLongScore.class, new HardSoftLongScoreJacksonJsonSerializer());
        module.addDeserializer(HardSoftLongScore.class, new HardSoftLongScoreJacksonJsonDeserializer());
        module.addSerializer(HardSoftBigDecimalScore.class, new HardSoftBigDecimalScoreJacksonJsonSerializer());
        module.addDeserializer(HardSoftBigDecimalScore.class, new HardSoftBigDecimalScoreJacksonJsonDeserializer());
        module.addSerializer(HardMediumSoftScore.class, new HardMediumSoftScoreJacksonJsonSerializer());
        module.addDeserializer(HardMediumSoftScore.class, new HardMediumSoftScoreJacksonJsonDeserializer());
        module.addSerializer(HardMediumSoftLongScore.class, new HardMediumSoftLongScoreJacksonJsonSerializer());
        module.addDeserializer(HardMediumSoftLongScore.class, new HardMediumSoftLongScoreJacksonJsonDeserializer());
        module.addSerializer(BendableScore.class, new BendableScoreJacksonJsonSerializer());
        module.addDeserializer(BendableScore.class, new BendableScoreJacksonJsonDeserializer());
        module.addSerializer(BendableLongScore.class, new BendableLongScoreJacksonJsonSerializer());
        module.addDeserializer(BendableLongScore.class, new BendableLongScoreJacksonJsonDeserializer());
        module.addSerializer(BendableBigDecimalScore.class, new BendableBigDecimalScoreJacksonJsonSerializer());
        module.addDeserializer(BendableBigDecimalScore.class, new BendableBigDecimalScoreJacksonJsonDeserializer());

        return module;
    }

}
