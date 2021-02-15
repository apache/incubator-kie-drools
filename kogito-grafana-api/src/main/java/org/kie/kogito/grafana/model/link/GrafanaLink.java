/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.grafana.model.link;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GrafanaLink {

    @JsonProperty("icon")
    public String icon = "external link";

    @JsonProperty("tags")
    public List<String> tags;

    @JsonProperty("includeVars")
    public boolean includeVars = true;

    @JsonProperty("keepTime")
    public boolean keepTime = true;

    @JsonProperty("targetBlank")
    public boolean targetBlank = true;

    @JsonProperty("title")
    public String title;

    @JsonProperty("tooltip")
    public String tooltip = "";

    @JsonProperty("type")
    public String type = "link";

    @JsonProperty("url")
    public String url;

    public GrafanaLink() {
    }

    public GrafanaLink(String title, String url) {
        this.title = title;
        this.url = url;
    }
}