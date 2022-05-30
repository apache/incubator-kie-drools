/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.correlation;

public class CorrelationInstance {

    private String correlationId;// encoded based on correlations
    private String correlatedId;// == processInstanceId
    private Correlation<?> correlation;

    public CorrelationInstance(String correlationId, String correlatedId, Correlation<?> correlation) {
        this.correlationId = correlationId;
        this.correlatedId = correlatedId;
        this.correlation = correlation;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getCorrelatedId() {
        return correlatedId;
    }

    public Correlation<?> getCorrelation() {
        return correlation;
    }
}
