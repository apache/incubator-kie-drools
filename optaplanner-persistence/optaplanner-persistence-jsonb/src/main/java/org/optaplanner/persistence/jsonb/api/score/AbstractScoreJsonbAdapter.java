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

package org.optaplanner.persistence.jsonb.api.score;

import javax.json.bind.adapter.JsonbAdapter;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.persistence.jsonb.api.OptaPlannerJsonbConfig;

/**
 * JSON-B binding support for a {@link Score} type.
 * <p>
 * For example: use {@code @JsonbTypeAdapter(HardSoftScoreJsonbAdapter.class)}
 * on a {@code HardSoftScore score} field and it will be serialized to JSON as {@code "score":"-999hard/-999soft"}.
 * Or better yet, use {@link OptaPlannerJsonbConfig} instead.
 *
 * @see Score
 * @param <Score_> the actual score type
 */
public abstract class AbstractScoreJsonbAdapter<Score_ extends Score<Score_>> implements JsonbAdapter<Score_, String> {

    @Override
    public String adaptToJson(Score_ score) {
        return score.toString();
    }
}
