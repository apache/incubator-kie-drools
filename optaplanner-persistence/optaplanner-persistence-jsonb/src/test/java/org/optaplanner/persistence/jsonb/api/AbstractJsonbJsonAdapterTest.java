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

package org.optaplanner.persistence.jsonb.api;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;

public abstract class AbstractJsonbJsonAdapterTest {

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    protected static <W> W serializeAndDeserialize(W input) {
        return serializeAndDeserialize(JsonbBuilder.create(), input);
    }

    protected static <W> W serializeAndDeserialize(Jsonb jsonb, W input) {
        String jsonString;
        W output;
        try {
            jsonString = jsonb.toJson(input);
            output = (W) jsonb.fromJson(jsonString, input.getClass());
        } catch (JsonbException e) {
            throw new IllegalStateException("Marshalling or unmarshalling for input (" + input + ") failed.", e);
        }

        return output;
    }
}
