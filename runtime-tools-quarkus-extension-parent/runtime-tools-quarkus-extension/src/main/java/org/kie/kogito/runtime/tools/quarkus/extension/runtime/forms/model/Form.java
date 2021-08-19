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

public class Form {

    public static String SOURCE_KEY = "source-content";

    private String name;
    private JsonObject source = new JsonObject();
    private FormConfiguration formConfiguration;

    public Form() {
    }

    public Form(String source, FormConfiguration formConfiguration, String name) {
        this.name = name;
        this.source.put(SOURCE_KEY, source);
        this.formConfiguration = formConfiguration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return this.source.getString(SOURCE_KEY);
    }

    public void setSource(String source) {
        this.source.put(SOURCE_KEY, source);
    }

    public JsonObject getFormConfiguration() {
        return formConfiguration;
    }

    public void setFormConfiguration(FormConfiguration formConfiguration) {
        this.formConfiguration = formConfiguration;
    }

    @Override
    public String toString() {
        return "{ \"Form\": {" +
                "\"name\": \"" + name + "\"," +
                "\"source\": " + source + "," +
                "\"formConfiguration\": " + formConfiguration.toString() +
                "}" +
                "}";
    }
}
