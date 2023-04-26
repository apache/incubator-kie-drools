/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.fluent;

import java.util.ArrayList;
import java.util.List;

import io.serverlessworkflow.api.branches.Branch;
import io.serverlessworkflow.api.states.DefaultState.Type;
import io.serverlessworkflow.api.states.ParallelState;
import io.serverlessworkflow.api.states.ParallelState.CompletionType;

public class ParallelStateBuilder extends StateBuilder<ParallelStateBuilder, ParallelState> {

    protected ParallelStateBuilder() {
        super(new ParallelState().withType(Type.PARALLEL));
        state.withBranches(branches);
    }

    public ParallelStateBuilder atLeast(int numCompleted) {
        state.withCompletionType(CompletionType.AT_LEAST).withNumCompleted(Integer.toString(numCompleted));
        return this;
    }

    private List<Branch> branches = new ArrayList<>();

    public BranchActionBuilder newBranch() {
        Branch branch = new Branch();
        branches.add(branch);
        return new BranchActionBuilder(this, branch);
    }
}
