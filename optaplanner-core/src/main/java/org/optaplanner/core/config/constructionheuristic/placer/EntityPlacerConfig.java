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

package org.optaplanner.core.config.constructionheuristic.placer;

import java.util.Collection;

import com.thoughtworks.xstream.annotations.XStreamInclude;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.impl.constructionheuristic.placer.EntityPlacer;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.solver.termination.Termination;

/**
 * General superclass for {@link QueuedEntityPlacerConfig} and {@link PooledEntityPlacerConfig}.
 */
@XStreamInclude({
        QueuedEntityPlacerConfig.class,
        PooledEntityPlacerConfig.class
})
public abstract class EntityPlacerConfig {

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    protected EntityDescriptor deduceEntityDescriptor(SolutionDescriptor solutionDescriptor) {
        Collection<EntityDescriptor> entityDescriptors = solutionDescriptor.getGenuineEntityDescriptors();
        if (entityDescriptors.size() != 1) {
            throw new IllegalArgumentException("The entityPlacerConfig (" + this
                    + ") has no entitySelector configured"
                    + " and because there are multiple in the entityClassSet (" + solutionDescriptor.getEntityClassSet()
                    + "), it can not be deducted automatically.");
        }
        return entityDescriptors.iterator().next();
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public abstract EntityPlacer buildEntityPlacer(HeuristicConfigPolicy configPolicy, Termination phaseTermination);

    protected void inherit(EntityPlacerConfig inheritedConfig) {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "()";
    }

}
