/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.grafana.model.functions;

public class IncreaseFunction implements GrafanaFunction {

    private static final String FUNCTION = "increase";

    private String timeParameter;

    public IncreaseFunction(String timeParameter) {
        if (timeParameter == null) {
            throw new IllegalArgumentException("timeParameter for Increase function can't be null");
        }
        this.timeParameter = timeParameter;
    }

    @Override
    public String getFunction() {
        return FUNCTION;
    }

    @Override
    public boolean hasTimeParameter() {
        return true;
    }

    @Override
    public String getTimeParameter() {
        return timeParameter;
    }
}
