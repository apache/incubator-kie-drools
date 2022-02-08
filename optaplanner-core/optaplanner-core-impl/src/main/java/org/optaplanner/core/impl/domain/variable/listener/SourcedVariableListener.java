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

package org.optaplanner.core.impl.domain.variable.listener;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.Supply;

/**
 * Used to externalize data for a {@link Supply} from the domain model itself.
 */
public interface SourcedVariableListener<Solution_, Entity_> extends VariableListener<Solution_, Entity_>, Supply {

    VariableDescriptor<Solution_> getSourceVariableDescriptor();

}
