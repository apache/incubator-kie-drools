/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.cloudbalancing.swingui.realtime;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

public class DeleteProcessProblemFactChange implements ProblemFactChange<CloudBalance> {

    private final CloudProcess process;

    public DeleteProcessProblemFactChange(CloudProcess process) {
        this.process = process;
    }

    @Override
    public void doChange(ScoreDirector<CloudBalance> scoreDirector) {
        CloudBalance cloudBalance = scoreDirector.getWorkingSolution();
        // A SolutionCloner clones planning entity lists (such as processList), so no need to clone the processList here
        CloudProcess workingProcess = scoreDirector.lookUpWorkingObject(process);
        if (workingProcess == null) {
            // The process has already been deleted (the UI asked to changed the same process twice), so do nothing
            return;
        }
        // Remove the planning entity itself
        scoreDirector.beforeEntityRemoved(workingProcess);
        cloudBalance.getProcessList().remove(workingProcess);
        scoreDirector.afterEntityRemoved(workingProcess);
        scoreDirector.triggerVariableListeners();
    }

}
