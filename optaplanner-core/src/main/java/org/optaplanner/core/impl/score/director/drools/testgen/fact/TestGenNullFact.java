/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.core.impl.score.director.drools.testgen.fact;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestGenNullFact implements TestGenFact {

    public static final TestGenNullFact INSTANCE = new TestGenNullFact();

    private TestGenNullFact() {
    }

    @Override
    public void setUp(Map<Object, TestGenFact> existingInstances) {
    }

    @Override
    public List<TestGenFactField> getFields() {
        return Collections.emptyList();
    }

    @Override
    public List<TestGenFact> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public List<Class<?>> getImports() {
        return Collections.emptyList();
    }

    @Override
    public void reset() {
    }

    @Override
    public void printInitialization(StringBuilder sb) {
    }

    @Override
    public void printSetup(StringBuilder sb) {
    }

    @Override
    public Object getInstance() {
        return null;
    }

    @Override
    public String toString() {
        return "null";
    }

}
