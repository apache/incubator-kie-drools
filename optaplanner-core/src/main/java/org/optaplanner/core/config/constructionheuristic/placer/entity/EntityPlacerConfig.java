/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.config.constructionheuristic.placer.entity;

import com.thoughtworks.xstream.annotations.XStreamInclude;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.constructionheuristic.placer.PlacerConfig;
import org.optaplanner.core.impl.constructionheuristic.placer.entity.EntityPlacer;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.termination.Termination;

/**
 * General superclass for {@link QueuedEntityPlacerConfig} // TODO and {@link PooledEntityPlacerConfig}.
 */
@XStreamInclude({
        QueuedEntityPlacerConfig.class
})
public abstract class EntityPlacerConfig extends PlacerConfig {

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public abstract EntityPlacer buildEntityPlacer(EnvironmentMode environmentMode,
            SolutionDescriptor solutionDescriptor, Termination phaseTermination);

    protected void inherit(EntityPlacerConfig inheritedConfig) {
        super.inherit(inheritedConfig);
    }

}
