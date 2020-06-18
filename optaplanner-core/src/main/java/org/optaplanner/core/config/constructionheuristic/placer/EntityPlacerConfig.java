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

package org.optaplanner.core.config.constructionheuristic.placer;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.impl.constructionheuristic.placer.EntityPlacer;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;

import com.thoughtworks.xstream.annotations.XStreamInclude;

/**
 * General superclass for {@link QueuedEntityPlacerConfig} and {@link PooledEntityPlacerConfig}.
 */

@XmlSeeAlso({
        QueuedEntityPlacerConfig.class,
        QueuedValuePlacerConfig.class,
        PooledEntityPlacerConfig.class
})
@XStreamInclude({
        QueuedEntityPlacerConfig.class,
        QueuedValuePlacerConfig.class,
        PooledEntityPlacerConfig.class
})
public abstract class EntityPlacerConfig<C extends EntityPlacerConfig> extends AbstractConfig<C> {

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public abstract EntityPlacer buildEntityPlacer(HeuristicConfigPolicy configPolicy);

}
