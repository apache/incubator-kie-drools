/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.variable.anchor;

import org.optaplanner.core.impl.domain.variable.supply.Supply;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * Only supported for chained variables.
 * <p>
 * To get an instance, demand a {@link AnchorVariableDemand} from {@link InnerScoreDirector#getSupplyManager()}.
 */
public interface AnchorVariableSupply extends Supply {

    /**
     * @param entity never null
     * @return sometimes null, the anchor for the entity
     */
    Object getAnchor(Object entity);

}
