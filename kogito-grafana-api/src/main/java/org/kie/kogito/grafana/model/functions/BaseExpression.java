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
package org.kie.kogito.grafana.model.functions;

import java.util.List;
import java.util.stream.Collectors;

public class BaseExpression implements GrafanaFunction {

    private static final String RENDER_TEMPLATE = "%s_%s_%s{%s}";
    private String prefix;
    private String suffix;

    public BaseExpression(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String render(String metricBody, List<Label> labels) {
        return String.format(RENDER_TEMPLATE, prefix, metricBody, suffix, labels.stream().map(Label::render).collect(Collectors.joining(",")));
    }
}
