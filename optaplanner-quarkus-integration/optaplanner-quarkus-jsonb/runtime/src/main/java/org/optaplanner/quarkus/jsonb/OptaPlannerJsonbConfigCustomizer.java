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

package org.optaplanner.quarkus.jsonb;

import javax.inject.Singleton;
import javax.json.bind.JsonbConfig;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.persistence.jsonb.api.OptaPlannerJsonbConfig;

import io.quarkus.jsonb.JsonbConfigCustomizer;

/**
 * OptaPlanner doesn't use JSON-B, but it does have optional JSON-B support for {@link Score}, etc.
 */
@Singleton
public class OptaPlannerJsonbConfigCustomizer implements JsonbConfigCustomizer {

    @Override
    public void customize(JsonbConfig config) {
        config.withAdapters(OptaPlannerJsonbConfig.getScoreJsonbAdapters());
    }
}
