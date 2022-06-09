package org.optaplanner.persistence.jsonb.api;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.adapter.JsonbAdapter;

import org.optaplanner.persistence.jsonb.api.score.buildin.bendable.BendableScoreJsonbAdapter;
import org.optaplanner.persistence.jsonb.api.score.buildin.bendablebigdecimal.BendableBigDecimalScoreJsonbAdapter;
import org.optaplanner.persistence.jsonb.api.score.buildin.bendablelong.BendableLongScoreJsonbAdapter;
import org.optaplanner.persistence.jsonb.api.score.buildin.hardmediumsoft.HardMediumSoftScoreJsonbAdapter;
import org.optaplanner.persistence.jsonb.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScoreJsonbAdapter;
import org.optaplanner.persistence.jsonb.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreJsonbAdapter;
import org.optaplanner.persistence.jsonb.api.score.buildin.hardsoft.HardSoftScoreJsonbAdapter;
import org.optaplanner.persistence.jsonb.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreJsonbAdapter;
import org.optaplanner.persistence.jsonb.api.score.buildin.hardsoftlong.HardSoftLongScoreJsonbAdapter;
import org.optaplanner.persistence.jsonb.api.score.buildin.simple.SimpleScoreJsonbAdapter;
import org.optaplanner.persistence.jsonb.api.score.buildin.simplebigdecimal.SimpleBigDecimalScoreJsonbAdapter;
import org.optaplanner.persistence.jsonb.api.score.buildin.simplelong.SimpleLongScoreJsonbAdapter;

/**
 * This class adds all JSON-B adapters.
 */
public class OptaPlannerJsonbConfig {

    /**
     * @return never null, use it to create a {@link Jsonb} instance with {@link JsonbBuilder#create(JsonbConfig)}.
     */
    public static JsonbConfig createConfig() {
        JsonbConfig config = new JsonbConfig()
                .withAdapters(new BendableScoreJsonbAdapter(),
                        new BendableBigDecimalScoreJsonbAdapter(),
                        new BendableLongScoreJsonbAdapter(),
                        new HardMediumSoftScoreJsonbAdapter(),
                        new HardMediumSoftBigDecimalScoreJsonbAdapter(),
                        new HardMediumSoftLongScoreJsonbAdapter(),
                        new HardSoftScoreJsonbAdapter(),
                        new HardSoftBigDecimalScoreJsonbAdapter(),
                        new HardSoftLongScoreJsonbAdapter(),
                        new SimpleScoreJsonbAdapter(),
                        new SimpleBigDecimalScoreJsonbAdapter(),
                        new SimpleLongScoreJsonbAdapter());

        return config;
    }

    /**
     * @return never null, use it to customize a {@link JsonbConfig} instance with
     *         {@link JsonbConfig#withAdapters(JsonbAdapter[])}.
     */
    public static JsonbAdapter[] getScoreJsonbAdapters() {
        return new JsonbAdapter[] {
                new BendableScoreJsonbAdapter(),
                new BendableBigDecimalScoreJsonbAdapter(),
                new BendableLongScoreJsonbAdapter(),
                new HardMediumSoftScoreJsonbAdapter(),
                new HardMediumSoftBigDecimalScoreJsonbAdapter(),
                new HardMediumSoftLongScoreJsonbAdapter(),
                new HardSoftScoreJsonbAdapter(),
                new HardSoftBigDecimalScoreJsonbAdapter(),
                new HardSoftLongScoreJsonbAdapter(),
                new SimpleScoreJsonbAdapter(),
                new SimpleBigDecimalScoreJsonbAdapter(),
                new SimpleLongScoreJsonbAdapter() };
    }
}
