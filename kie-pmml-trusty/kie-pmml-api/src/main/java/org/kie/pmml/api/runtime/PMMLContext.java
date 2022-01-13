/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.api.runtime;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.Context;

public interface PMMLContext extends Context {

    PMMLRequestData getRequestData();

    void addMissingValueReplaced(final String fieldName, final Object missingValueReplaced);

    void addCommonTranformation(final String fieldName, final Object commonTranformation);

    void addLocalTranformation(final String fieldName, final Object commonTranformation);

    Map<String, Object> getMissingValueReplacedMap();

    Map<String, Object> getCommonTransformationMap();

    Map<String, Object> getLocalTransformationMap();

    Object getPredictedDisplayValue();

    void setPredictedDisplayValue(Object predictedDisplayValue);

    Object getEntityId();

    void setEntityId(Object entityId);

    Object getAffinity();

    void setAffinity(Object affinity);

    Map<String, Double> getProbabilityMap();

    /**
     * Returns the <b>probability map</b> evaluated by the model
     * @return
     */
    LinkedHashMap<String, Double> getProbabilityResultMap();

    void setProbabilityResultMap(LinkedHashMap<String, Double> probabilityResultMap);

    Map<String, Object> getOutputFieldsMap();

    /**
     * Add the given <code>PMMLListener</code> to the current <code>PMMLContext</code>
     * That listener, in turn, will be available only for evaluation of that specific <code>PMMLContext</code>
     * @param toAdd
     */
    void addPMMLListener(final PMMLListener toAdd);

    Set<PMMLListener> getPMMLListeners();

}
