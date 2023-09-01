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
package org.kie.pmml.evaluator.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.kie.api.pmml.PMMLRequestData;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextImpl;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;

import static org.kie.pmml.commons.Constants.PMML_SUFFIX;

public class PMMLRuntimeContextImpl extends EfestoRuntimeContextImpl<PMMLListener> implements EfestoRuntimeContext<PMMLListener>,
                                                                                              PMMLRuntimeContext {

    private static final String PMML_REQUEST_DATA = "PMML_REQUEST_DATA";

    public static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final String name;

    private final String fileName;

    private final String fileNameNoSuffix;

    private final Map<String, Object> map = new ConcurrentHashMap<>();
    private final Map<String, Object> missingValueReplacedMap = new HashMap<>();
    private final Map<String, Object> commonTransformationMap = new HashMap<>();
    private final Map<String, Object> localTransformationMap = new HashMap<>();
    private final Map<String, Object> outputFieldsMap = new HashMap<>();
    private final Set<PMMLListener> pmmlListeners = new HashSet<>();

    private Object predictedDisplayValue;
    private Object entityId;
    private Object affinity;
    private LinkedHashMap<String, Double> probabilityResultMap;

    public PMMLRuntimeContextImpl(final PMMLRequestData pmmlRequestData,
                                  final String fileName,
                                  final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        super(memoryCompilerClassLoader);
        name = "Context_" + ID_GENERATOR.incrementAndGet();
        set(PMML_REQUEST_DATA, pmmlRequestData);
        if (!fileName.endsWith(PMML_SUFFIX)) {
            this.fileName = fileName + PMML_SUFFIX;
        } else {
            this.fileName = fileName;
        }
        this.fileNameNoSuffix = this.fileName.substring(0, this.fileName.lastIndexOf('.'));
    }

    public PMMLRuntimeContextImpl(final PMMLRequestData pmmlRequestData,
                                  final String fileName,
                                  final Set<PMMLListener> pmmlListeners,
                                  final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        this(pmmlRequestData, fileName, memoryCompilerClassLoader);
        this.pmmlListeners.addAll(pmmlListeners);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getFileNameNoSuffix() {
        return fileNameNoSuffix;
    }

    @Override
    public Object get(String identifier) {
        if (identifier == null || identifier.equals("")) {
            return null;
        }

        Object object = null;
        if (map.containsKey(identifier)) {
            object = map.get(identifier);
        }
        return object;
    }

    @Override
    public void set(String identifier, Object value) {
        map.put(identifier, value);
    }

    @Override
    public void remove(String identifier) {
        map.remove(identifier);
    }

    public boolean has(String identifier) {
        return map.containsKey(identifier);
    }

    @Override
    public PMMLRequestData getRequestData() {
        return (PMMLRequestData) get(PMML_REQUEST_DATA);
    }

    @Override
    public void addMissingValueReplaced(final String fieldName, final Object missingValueReplaced) {
        missingValueReplacedMap.put(fieldName, missingValueReplaced);
    }

    @Override
    public void addCommonTranformation(final String fieldName, final Object commonTranformation) {
        localTransformationMap.put(fieldName, commonTranformation);
    }

    @Override
    public void addLocalTranformation(final String fieldName, final Object commonTranformation) {
        commonTransformationMap.put(fieldName, commonTranformation);
    }

    /**
     * Returns an <b>unmodifiable map</b> of <code>missingValueReplacedMap</code>
     *
     * @return
     */
    @Override
    public Map<String, Object> getMissingValueReplacedMap() {
        return Collections.unmodifiableMap(missingValueReplacedMap);
    }

    /**
     * Returns an <b>unmodifiable map</b> of <code>commonTransformationMap</code>
     *
     * @return
     */
    @Override
    public Map<String, Object> getCommonTransformationMap() {
        return Collections.unmodifiableMap(commonTransformationMap);
    }

    /**
     * Returns an <b>unmodifiable map</b> of <code>localTransformationMap</code>
     *
     * @return
     */
    @Override
    public Map<String, Object> getLocalTransformationMap() {
        return Collections.unmodifiableMap(localTransformationMap);
    }

    static LinkedHashMap<String, Double> getFixedProbabilityMap(final LinkedHashMap<String, Double> probabilityResultMap) {
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        String[] resultMapKeys = probabilityResultMap.keySet().toArray(new String[0]);
        AtomicReference<Double> sumCounter = new AtomicReference<>(0.0);
        for (int i = 0; i < probabilityResultMap.size(); i++) {
            String key = resultMapKeys[i];
            double value = probabilityResultMap.get(key);
            if (i < resultMapKeys.length - 1) {
                sumCounter.accumulateAndGet(value, Double::sum);
                toReturn.put(key, value);
            } else {
                // last element
                toReturn.put(key, 1 - sumCounter.get());
            }
        }
        return toReturn;
    }

    @Override
    public Object getPredictedDisplayValue() {
        return predictedDisplayValue;
    }

    @Override
    public void setPredictedDisplayValue(Object predictedDisplayValue) {
        this.predictedDisplayValue = predictedDisplayValue;
    }

    @Override
    public Object getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(Object entityId) {
        this.entityId = entityId;
    }

    @Override
    public Object getAffinity() {
        return affinity;
    }

    @Override
    public void setAffinity(Object affinity) {
        this.affinity = affinity;
    }

    /**
     * Returns an <b>unmodifiable map</b> of probabilities, or an empty one
     *
     * @return
     */
    @Override
    public Map<String, Double> getProbabilityMap() {
        final LinkedHashMap<String, Double> toReturn = getProbabilityResultMap();
        return toReturn != null ?
                Collections.unmodifiableMap(getFixedProbabilityMap(toReturn)) : Collections.emptyMap();
    }

    @Override
    public LinkedHashMap<String, Double> getProbabilityResultMap() {
        return probabilityResultMap;
    }

    @Override
    public void setProbabilityResultMap(LinkedHashMap<String, Double> probabilityResultMap) {
        this.probabilityResultMap = probabilityResultMap;
    }

    @Override
    public Map<String, Object> getOutputFieldsMap() {
        return outputFieldsMap;
    }

    @Override
    public void addEfestoListener(PMMLListener toAdd) {
        pmmlListeners.add(toAdd);
    }

    @Override
    public void removeEfestoListener(PMMLListener toRemove) {
        pmmlListeners.remove(toRemove);
    }

    @Override
    public Set<PMMLListener> getEfestoListeners() {
        return Collections.unmodifiableSet(pmmlListeners);
    }
}
