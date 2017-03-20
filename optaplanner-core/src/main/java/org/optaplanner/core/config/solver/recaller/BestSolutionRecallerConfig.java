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

package org.optaplanner.core.config.solver.recaller;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;

// Currently not yet supported as being nested, so no XStreamAlias
public class BestSolutionRecallerConfig extends AbstractConfig<BestSolutionRecallerConfig> {

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public <Solution_> BestSolutionRecaller<Solution_> buildBestSolutionRecaller(EnvironmentMode environmentMode) {
        BestSolutionRecaller<Solution_> bestSolutionRecaller = new BestSolutionRecaller<>();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            bestSolutionRecaller.setAssertInitialScoreFromScratch(true);
            bestSolutionRecaller.setAssertShadowVariablesAreNotStale(true);
            bestSolutionRecaller.setAssertBestScoreIsUnmodified(true);
        }
        return bestSolutionRecaller;
    }

    @Override
    public void inherit(BestSolutionRecallerConfig inheritedConfig) {
    }

}
