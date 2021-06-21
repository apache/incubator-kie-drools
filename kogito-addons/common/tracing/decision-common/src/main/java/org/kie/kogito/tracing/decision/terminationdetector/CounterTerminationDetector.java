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
package org.kie.kogito.tracing.decision.terminationdetector;

import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;

public class CounterTerminationDetector implements TerminationDetector {

    int openEvents = 0;
    int totalEvents = 0;

    @Override
    public void add(EvaluateEvent event) {
        totalEvents++;
        if (event.getType().isBefore()) {
            openEvents++;
        } else {
            openEvents--;
        }
    }

    @Override
    public boolean isTerminated() {
        return totalEvents > 0 && openEvents == 0;
    }
}
