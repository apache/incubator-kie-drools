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
package org.kie.pmml.compiler.commons.mocks;

import java.util.Map;

import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.memorycompiler.KieMemoryCompilerException;
import org.kie.pmml.api.compilation.PMMLCompilationContext;

public class PMMLCompilationContextMock implements PMMLCompilationContext {

    private final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    public PMMLCompilationContextMock() {
        this.memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public Map<String, byte[]> compileClasses(Map<String, String> sourcesMap) {
        return KieMemoryCompiler.compileNoLoad(sourcesMap, memoryCompilerClassLoader);
    }

    @Override
    public void loadClasses(Map<String, byte[]> compiledClassesMap) {
        for (Map.Entry<String, byte[]> entry : compiledClassesMap.entrySet()) {
            memoryCompilerClassLoader.addCode(entry.getKey(), entry.getValue());
            try {
                memoryCompilerClassLoader.loadClass(entry.getKey());
            } catch (ClassNotFoundException e) {
                throw new KieMemoryCompilerException(e.getMessage(), e);
            }
        }
    }
//
//    @Override
//    public Class<?> loadClass(String className) throws ClassNotFoundException {
//        return memoryCompilerClassLoader.loadClass(className);
//    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Object get(String identifier) {
        return null;
    }

    @Override
    public void set(String identifier, Object value) {

    }

    @Override
    public void remove(String identifier) {

    }

    @Override
    public boolean has(String identifier) {
        return false;
    }

//    @Override
//    public PMMLRequestData getRequestData() {
//        return null;
//    }
//
//    @Override
//    public String getFileName() {
//        return null;
//    }
//
//    @Override
//    public String getFileNameNoSuffix() {
//        return null;
//    }
//
//    @Override
//    public void addMissingValueReplaced(String fieldName, Object missingValueReplaced) {
//
//    }
//
//    @Override
//    public void addCommonTranformation(String fieldName, Object commonTranformation) {
//
//    }
//
//    @Override
//    public void addLocalTranformation(String fieldName, Object commonTranformation) {
//
//    }
//
//    @Override
//    public Map<String, Object> getMissingValueReplacedMap() {
//        return null;
//    }
//
//    @Override
//    public Map<String, Object> getCommonTransformationMap() {
//        return null;
//    }
//
//    @Override
//    public Map<String, Object> getLocalTransformationMap() {
//        return null;
//    }
//
//    @Override
//    public Object getPredictedDisplayValue() {
//        return null;
//    }
//
//    @Override
//    public void setPredictedDisplayValue(Object predictedDisplayValue) {
//
//    }
//
//    @Override
//    public Object getEntityId() {
//        return null;
//    }
//
//    @Override
//    public void setEntityId(Object entityId) {
//
//    }
//
//    @Override
//    public Object getAffinity() {
//        return null;
//    }
//
//    @Override
//    public void setAffinity(Object affinity) {
//
//    }
//
//    @Override
//    public Map<String, Double> getProbabilityMap() {
//        return null;
//    }
//
//    @Override
//    public LinkedHashMap<String, Double> getProbabilityResultMap() {
//        return null;
//    }
//
//    @Override
//    public void setProbabilityResultMap(LinkedHashMap<String, Double> probabilityResultMap) {
//
//    }
//
//    @Override
//    public Map<String, Object> getOutputFieldsMap() {
//        return null;
//    }
}
