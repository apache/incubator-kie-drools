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

/**
 * Interface for a grafana/prometheus function. See <a href="https://prometheus.io/docs/prometheus/latest/querying/functions/">https://prometheus.io/docs/prometheus/latest/querying/functions/</>
 */
public interface GrafanaFunction {

    /**
     * Gets the function name.
     *
     * @return The function name.
     */
    String getFunction();

    /**
     * Returns a boolean representing the presence of a time parameter for the function.
     *
     * @return true if the function has a time parameter, false otherwise.
     */
    boolean hasTimeParameter();

    /**
     * Gets the time parameter.
     *
     * @return The time parameter.
     */
    String getTimeParameter();
}
