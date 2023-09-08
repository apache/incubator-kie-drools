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
package org.kie.internal.task.api.prediction;

import java.util.Map;

import org.kie.api.task.model.Task;

/**
 * Interface which allows a prediction or recommendation service implementation to be trained with task data
 * and return outcome predictions based on task input.
 * <p>
 * This interface is still considered subject to change.
 */
public interface PredictionService {

    String getIdentifier();

    /**
     * Return an outcome prediction for a set of input attributes.
     * @param task Task information to be optionally used by the predictive model
     * @param inputData A map of input attributes with the attribute name as key and the attribute value as value.
     * @return PredictionOutcome object which encapsulates the results from a prediction
     */
    PredictionOutcome predict(Task task, Map<String, Object> inputData);

    /**
     * Train a predictive model using task data and a set of input and outcome attributes.
     * @param task Task information to be optionally used by the predictive model
     * @param inputData A map of input attributes with the attribute name as key and the attribute value as value.
     * @param outputData A map of output attributes (outcomes) with the attribute name as key and the attribute value as value.
     */
    void train(Task task, Map<String, Object> inputData, Map<String, Object> outputData);
}
