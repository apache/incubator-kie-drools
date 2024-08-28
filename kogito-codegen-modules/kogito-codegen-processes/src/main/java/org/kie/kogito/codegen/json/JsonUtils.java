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
package org.kie.kogito.codegen.json;

import org.kie.kogito.jackson.utils.MergeUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonUtils {

    /* see https://stackoverflow.com/questions/9895041/merging-two-json-documents-using-jackson for alternative approaches to merge */
    private JsonUtils() {
    }

    /**
     * Merge two JSON documents.
     *
     * @param src JsonNode to be merged
     * @param target JsonNode to merge to
     */
    public static JsonNode merge(JsonNode src, JsonNode target) {
        return MergeUtils.merge(src, target, true);
    }

}
