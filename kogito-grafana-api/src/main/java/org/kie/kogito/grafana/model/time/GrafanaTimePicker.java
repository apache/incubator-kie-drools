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
package org.kie.kogito.grafana.model.time;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrafanaTimePicker {

    @JsonProperty("time_options")
    public List<String> timeOptions = Arrays.asList("5m",
            "15m",
            "1h",
            "6h",
            "12h",
            "24h",
            "2d",
            "7d",
            "30d");

    @JsonProperty("refresh_intervals")
    public List<String> refreshIntervals = Arrays.asList("5s",
            "10s",
            "30s",
            "1m",
            "5m",
            "15m",
            "30m",
            "1h",
            "2h",
            "1d");

    @JsonProperty("type")
    public String type;

    @JsonProperty("status")
    public String status;

    @JsonProperty("now")
    public boolean now;

    @JsonProperty("notice")
    public boolean notice;

    @JsonProperty("enable")
    public boolean enable;

    @JsonProperty("collapse")
    public boolean collapse;
}