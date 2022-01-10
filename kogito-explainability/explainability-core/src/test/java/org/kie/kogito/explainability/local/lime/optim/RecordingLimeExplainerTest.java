/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.local.lime.optim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RecordingLimeExplainerTest {

    @Test
    void testRecordedPredictions() {
        RecordingLimeExplainer recordingLimeExplainer = new RecordingLimeExplainer(10);
        List<Prediction> allPredictions = new ArrayList<>();
        PredictionProvider model = mock(PredictionProvider.class);
        for (int i = 0; i < 15; i++) {
            Prediction prediction = mock(Prediction.class);
            allPredictions.add(prediction);
            try {
                recordingLimeExplainer.explainAsync(prediction, model).get(Config.DEFAULT_ASYNC_TIMEOUT,
                        Config.DEFAULT_ASYNC_TIMEUNIT);
            } catch (Exception e) {
                // ignored for the sake of the test
            }
        }
        assertThat(allPredictions).hasSize(15);
        List<Prediction> recordedPredictions = recordingLimeExplainer.getRecordedPredictions();
        assertThat(recordedPredictions).hasSize(10);
        // only the last 10 predictions are kept
        assertThat(allPredictions.subList(5, 15)).isEqualTo(recordedPredictions);
    }

    @Test
    void testParallel() throws InterruptedException, ExecutionException, TimeoutException {
        int capacity = 10;
        RecordingLimeExplainer recordingLimeExplainer = new RecordingLimeExplainer(capacity);
        PredictionProvider model = mock(PredictionProvider.class);

        Callable<?> callable = () -> {
            for (int i = 0; i < 10000; i++) {
                Prediction prediction = mock(Prediction.class);
                try {
                    recordingLimeExplainer.explainAsync(prediction, model).get(Config.DEFAULT_ASYNC_TIMEOUT,
                            Config.DEFAULT_ASYNC_TIMEUNIT);
                } catch (Exception e) {
                    // ignored for the sake of the test
                }
            }
            return null;
        };
        Collection<Future<?>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 4; i++) {
            futures.add(executorService.submit(callable));
        }
        for (Future<?> f : futures) {
            f.get(1, TimeUnit.MINUTES);
        }
        assertThat(recordingLimeExplainer.getRecordedPredictions().size()).isEqualTo(capacity);
    }

    @Test
    void testQueue() {
        RecordingLimeExplainer.FixedSizeConcurrentLinkedDeque<String> queue = new RecordingLimeExplainer.FixedSizeConcurrentLinkedDeque<>(5);
        String[] strings = "a b c d e f g f".split(" ");
        for (String s : strings) {
            queue.offer(s);
        }
        assertThat(queue).containsExactly("c d e f g".split(" "));
    }
}
