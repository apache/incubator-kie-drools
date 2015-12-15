/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.phase.custom.scope;

import org.optaplanner.core.impl.phase.scope.AbstractStepScope;

public class CustomStepScope extends AbstractStepScope {

    private final CustomPhaseScope phaseScope;

    private int uninitializedVariableCount = -1;

    public CustomStepScope(CustomPhaseScope phaseScope) {
        this(phaseScope, phaseScope.getNextStepIndex());
    }

    public CustomStepScope(CustomPhaseScope phaseScope, int stepIndex) {
        super(stepIndex);
        this.phaseScope = phaseScope;
    }

    @Override
    public CustomPhaseScope getPhaseScope() {
        return phaseScope;
    }

    public int getUninitializedVariableCount() {
        return uninitializedVariableCount;
    }

    public void setUninitializedVariableCount(int uninitializedVariableCount) {
        this.uninitializedVariableCount = uninitializedVariableCount;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

}
