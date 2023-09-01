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
package org.kie.pmml.commons.testingutility;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.kie.api.pmml.PMMLRequestData;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;

public class PMMLRuntimeContextTest implements PMMLRuntimeContext {

    private final Map<String, Object> outputFieldsMap = new HashMap<>();
    private LinkedHashMap<String, Double> probabilityResultMap;

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public String getFileNameNoSuffix() {
        return null;
    }

    @Override
    public PMMLRequestData getRequestData() {
        return null;
    }

    @Override
    public void addMissingValueReplaced(String fieldName, Object missingValueReplaced) {

    }

    @Override
    public void addCommonTranformation(String fieldName, Object commonTranformation) {

    }

    @Override
    public void addLocalTranformation(String fieldName, Object commonTranformation) {

    }

    @Override
    public Map<String, Object> getMissingValueReplacedMap() {
        return null;
    }

    @Override
    public Map<String, Object> getCommonTransformationMap() {
        return null;
    }

    @Override
    public Map<String, Object> getLocalTransformationMap() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Object get(String s) {
        return null;
    }

    @Override
    public void set(String s, Object o) {

    }

    @Override
    public void remove(String s) {

    }

    @Override
    public boolean has(String s) {
        return false;
    }

    @Override
    public Object getPredictedDisplayValue() {
        return null;
    }

    @Override
    public void setPredictedDisplayValue(Object predictedDisplayValue) {

    }

    @Override
    public Object getEntityId() {
        return null;
    }

    @Override
    public void setEntityId(Object entityId) {

    }

    @Override
    public Object getAffinity() {
        return null;
    }

    @Override
    public void setAffinity(Object affinity) {

    }

    @Override
    public Map<String, Double> getProbabilityMap() {
        return null;
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

    }

    @Override
    public void removeEfestoListener(PMMLListener toRemove) {

    }

    @Override
    public Set<PMMLListener> getEfestoListeners() {
        return null;
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return null;
    }

    @Override
    public ServiceLoader<KieRuntimeService> getKieRuntimeService() {
        return null;
    }
}
