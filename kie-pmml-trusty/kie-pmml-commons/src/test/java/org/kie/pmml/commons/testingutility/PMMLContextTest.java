/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.commons.testingutility;

import java.util.Map;

import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.api.runtime.PMMLContext;

public class PMMLContextTest implements PMMLContext {

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
}
