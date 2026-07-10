/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.cloudbalancing.swingui.realtime;

import java.util.function.LongFunction;

import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

public class AddProcessProblemChange implements ProblemChange<CloudBalance> {

    private final LongFunction<CloudProcess> generator;

    public AddProcessProblemChange(LongFunction<CloudProcess> generator) {
        this.generator = generator;
    }

    @Override
    public void doChange(CloudBalance cloudBalance, ProblemChangeDirector problemChangeDirector) {
        // Set a unique id on the new process
        long nextProcessId = 0L;
        for (CloudProcess otherProcess : cloudBalance.getProcessList()) {
            if (nextProcessId <= otherProcess.getId()) {
                nextProcessId = otherProcess.getId() + 1L;
            }
        }
        CloudProcess process = generator.apply(nextProcessId);
        // A SolutionCloner clones planning entity lists (such as processList), so no need to clone the processList here
        // Add the planning entity itself
        problemChangeDirector.addEntity(process, cloudBalance.getProcessList()::add);
    }

}
