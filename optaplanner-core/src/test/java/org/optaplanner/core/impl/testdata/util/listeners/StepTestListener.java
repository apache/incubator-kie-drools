/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.core.impl.testdata.util.listeners;

import org.optaplanner.core.impl.phase.scope.AbstractStepScope;

import java.util.ArrayList;
import java.util.List;

public class StepTestListener extends AbstractStepTestListener{

    private List<AbstractStepScope> recordedSteps = new ArrayList<AbstractStepScope>();

    @Override
    public void stepStarted(AbstractStepScope stepScope) {
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        recordedSteps.add(stepScope);
    }

    public List<AbstractStepScope> getRecordedSteps() {
        return recordedSteps;
    }

}
