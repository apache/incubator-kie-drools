package org.kie.pmml.evaluator.core.utils;/*
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import org.junit.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.api.models.PMMLStep;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.evaluator.core.PMMLContextImpl;

import static org.assertj.core.api.Assertions.assertThat;

public class PMMLListenerUtilsTest {

    @Test
    public void stepExecuted() {
        final Map<Integer, PMMLStep> listenerFeedback = new HashMap<>();
        int size = 3;
        PMMLContext pmmlContext = getPMMLContext(size, listenerFeedback);
        AtomicBoolean invoked = new AtomicBoolean(false);
        PMMLListenerUtils.stepExecuted(() -> new PMMLStepTest(invoked), pmmlContext);
        assertThat(invoked).isTrue();
        assertThat(listenerFeedback).hasSize(size);
        final PMMLStep retrieved = listenerFeedback.get(0);
        IntStream.range(1, size).forEach(i -> assertThat(listenerFeedback.get(i)).isEqualTo(retrieved));
    }

    @Test
    public void stepNotExecuted() {
        PMMLContext pmmlContext = new PMMLContextImpl(new PMMLRequestData());
        AtomicBoolean invoked = new AtomicBoolean(false);
        PMMLListenerUtils.stepExecuted(() -> new PMMLStepTest(invoked), pmmlContext);
        assertThat(invoked).isFalse();
    }

    private PMMLContext getPMMLContext(int size, Map<Integer, PMMLStep> listenerFeedback) {
        PMMLContext toReturn = new PMMLContextImpl(new PMMLRequestData());
        IntStream.range(0, size).forEach(i -> toReturn.addPMMLListener(getPMMLListener(i, listenerFeedback)));
        return toReturn;
    }

    private PMMLListener getPMMLListener(int id, Map<Integer, PMMLStep> listenerFeedback) {
        return step -> listenerFeedback.put(id, step);
    }

    private static class PMMLStepTest implements PMMLStep {

        private static final long serialVersionUID = -2348602567874639224L;
        private AtomicBoolean invoked;

        public PMMLStepTest(AtomicBoolean invoked) {
            this.invoked = invoked;
            this.invoked.set(true);
        }

        @Override
        public void addInfo(String infoName, Object infoValue) {

        }

        @Override
        public Map<String, Object> getInfo() {
            return null;
        }
    }
}