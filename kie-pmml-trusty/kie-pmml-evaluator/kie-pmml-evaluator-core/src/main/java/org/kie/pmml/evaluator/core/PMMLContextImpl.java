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
package org.kie.pmml.evaluator.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.command.impl.ContextImpl;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.api.runtime.PMMLContext;

public class PMMLContextImpl extends ContextImpl implements PMMLContext {

    private static final String PMML_REQUEST_DATA = "PMML_REQUEST_DATA";
    private final Map<String, Object> missingValueReplacedMap = new HashMap<>();
    private final Map<String, Object> commonTransformationMap = new HashMap<>();
    private final Map<String, Object> localTransformationMap = new HashMap<>();

    public PMMLContextImpl(final PMMLRequestData pmmlRequestData) {
        super();
        set(PMML_REQUEST_DATA, pmmlRequestData);
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

    @Override
    public Map<String, Object> getMissingValueReplacedMap() {
        return Collections.unmodifiableMap(missingValueReplacedMap);
    }

    @Override
    public Map<String, Object> getCommonTransformationMap() {
        return Collections.unmodifiableMap(commonTransformationMap);
    }

    @Override
    public Map<String, Object> getLocalTransformationMap() {
        return Collections.unmodifiableMap(localTransformationMap);
    }
}
