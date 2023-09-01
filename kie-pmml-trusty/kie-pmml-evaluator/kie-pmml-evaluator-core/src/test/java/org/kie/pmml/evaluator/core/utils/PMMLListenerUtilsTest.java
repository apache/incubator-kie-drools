package org.kie.pmml.evaluator.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.models.PMMLStep;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.evaluator.core.PMMLRuntimeContextImpl;

import static org.assertj.core.api.Assertions.assertThat;

class PMMLListenerUtilsTest {

    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    @BeforeAll
    static void setUp() {
        memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void stepExecuted() {
        final Map<Integer, PMMLStep> listenerFeedback = new HashMap<>();
        int size = 3;
        PMMLRuntimeContext pmmlContext = getPMMLContext(size, listenerFeedback);
        AtomicBoolean invoked = new AtomicBoolean(false);
        PMMLListenerUtils.stepExecuted(() -> new PMMLStepTest(invoked), pmmlContext);
        assertThat(invoked).isTrue();
        assertThat(listenerFeedback).hasSize(size);
        final PMMLStep retrieved = listenerFeedback.get(0);
        IntStream.range(1, size).forEach(i -> assertThat(listenerFeedback.get(i)).isEqualTo(retrieved));
    }

    @Test
    void stepNotExecuted() {
        PMMLRuntimeContext pmmlContext = new PMMLRuntimeContextImpl(new PMMLRequestData(), "filename", memoryCompilerClassLoader);
        AtomicBoolean invoked = new AtomicBoolean(false);
        PMMLListenerUtils.stepExecuted(() -> new PMMLStepTest(invoked), pmmlContext);
        assertThat(invoked).isFalse();
    }

    private PMMLRuntimeContext getPMMLContext(int size, Map<Integer, PMMLStep> listenerFeedback) {
        PMMLRuntimeContext toReturn = new PMMLRuntimeContextImpl(new PMMLRequestData(), "filename", memoryCompilerClassLoader);
        IntStream.range(0, size).forEach(i -> toReturn.addEfestoListener(getPMMLListener(i, listenerFeedback)));
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