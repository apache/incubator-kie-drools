/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bavet;

import org.junit.jupiter.api.Assumptions;
import org.optaplanner.constraint.streams.common.ConstraintStreamImplSupport;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public final class BavetConstraintStreamImplSupport
        implements ConstraintStreamImplSupport {

    private final boolean constraintMatchEnabled;

    public BavetConstraintStreamImplSupport(boolean constraintMatchEnabled) {
        this.constraintMatchEnabled = constraintMatchEnabled;
    }

    @Override
    public void assumeBavet() {
        // This is Bavet.
    }

    @Override
    public void assumeDrools() {
        Assumptions.assumeTrue(false, "This functionality is not yet supported in Bavet constraint streams.");
    }

    @Override
    public boolean isConstreamMatchEnabled() {
        return constraintMatchEnabled;
    }

    @Override
    public <Score_ extends Score<Score_>, Solution_> InnerScoreDirector<Solution_, Score_> buildScoreDirector(
            SolutionDescriptor<Solution_> solutionDescriptorSupplier, ConstraintProvider constraintProvider) {
        return (InnerScoreDirector<Solution_, Score_>) new BavetConstraintStreamScoreDirectorFactory<>(
                solutionDescriptorSupplier, constraintProvider)
                        .buildScoreDirector(false, constraintMatchEnabled);
    }
}
