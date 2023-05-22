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

import io.serverlessworkflow.api.actions.Action;
import io.serverlessworkflow.api.branches.Branch;

public class BranchActionBuilder {

    private ParallelStateBuilder parent;
    private Branch branch;
    private List<Action> actions = new ArrayList<>();

    public BranchActionBuilder(ParallelStateBuilder parent, Branch branch) {
        this.parent = parent;
        this.branch = branch.withActions(actions);
    }

    public BranchActionBuilder name(String name) {
        branch.withName(name);
        return this;
    }

    public BranchActionBuilder action(ActionBuilder action) {
        action.getFunction().ifPresent(parent.getFunctions()::add);
        action.getEvent().ifPresent(parent.getEvents()::add);
        actions.add(action.build());
        return this;
    }

    public ParallelStateBuilder endBranch() {
        return parent;
    }
}
