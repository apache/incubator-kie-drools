/**
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
package org.kie.pmml.evaluator.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;

public class PMMLRequestDataBuilder {

    private String correlationId;
    private String modelName;
    private List<ParameterInfo<?>> parameters;

    public PMMLRequestDataBuilder(String correlationId, String modelName) {
        this.correlationId = correlationId;
        this.modelName = modelName;
        parameters = new ArrayList<>();
    }

    public <T> PMMLRequestDataBuilder addParameter(String paramName, T value, Class<T> clazz) {
        parameters.add(new ParameterInfo<>(correlationId, paramName, clazz, value));
        return this;
    }

    public PMMLRequestData build() {
        PMMLRequestData data = new PMMLRequestData(correlationId, modelName);
        parameters.forEach(data::addRequestParam);
        return data;
    }
}
