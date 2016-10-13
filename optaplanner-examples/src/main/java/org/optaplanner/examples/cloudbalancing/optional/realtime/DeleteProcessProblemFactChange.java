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

package org.optaplanner.examples.cloudbalancing.optional.realtime;

import java.util.Iterator;
import java.util.Objects;

import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

public class DeleteProcessProblemFactChange implements ProblemFactChange<CloudBalance> {

    private final CloudProcess process;

    public DeleteProcessProblemFactChange(CloudProcess process) {
        this.process = process;
    }

    public void doChange(ScoreDirector<CloudBalance> scoreDirector) {
        CloudBalance cloudBalance = scoreDirector.getWorkingSolution();
        // Remove the planning entity itself
        for (Iterator<CloudProcess> it = cloudBalance.getProcessList().iterator(); it.hasNext(); ) {
            CloudProcess workingProcess = it.next();
            if (Objects.equals(workingProcess, process)) {
                scoreDirector.beforeEntityRemoved(workingProcess);
                it.remove(); // remove from list
                scoreDirector.afterEntityRemoved(workingProcess);
                break;
            }
        }
        scoreDirector.triggerVariableListeners();
    }

}
