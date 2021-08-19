/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.model;

import io.vertx.core.json.JsonObject;

public class FormConfiguration extends JsonObject {

    private static String RESOURCES_KEY = "resources";
    private static String SCHEMA_KEY = "schema";

    private static String SCRIPTS_KEY = "scripts";
    private static String STYLES_KEY = "styles";

    public FormConfiguration() {
    }

    public FormConfiguration(String content) {
        super(content);
    }

    public FormConfiguration(String schema, JsonObject resources) {
        this.put(SCHEMA_KEY, schema);
        this.put(RESOURCES_KEY, resources);
    }

    public void setSchema(String schema) {
        this.put(SCHEMA_KEY, schema);
    }

    public void setResources(JsonObject resources) {
        this.put(RESOURCES_KEY, resources);
    }

    public String getSchema() {
        return this.getString(SCHEMA_KEY);
    }

    public JsonObject getResources() {
        return this.getJsonObject(RESOURCES_KEY);
    }

    public JsonObject getScripts() {
        return getResources().getJsonObject(SCRIPTS_KEY);
    }

    public JsonObject getStyles() {
        return getResources().getJsonObject(STYLES_KEY);
    }

    public String popScripts(String key) {
        return getScripts().getString(key);
    }

    public String popStyles(String key) {
        return getStyles().getString(key);
    }

    public void putScripts(String key, String scripts) {
        getScripts().put(key, scripts);
    }

    public void putStyles(String key, String styles) {
        getStyles().put(key, styles);
    }
}
