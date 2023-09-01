/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.efesto.runtimemanager.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;

import static org.kie.efesto.runtimemanager.core.service.RuntimeManagerUtils.getOptionalOutput;

public class RuntimeManagerImpl implements RuntimeManager {

    @Override
    public Collection<EfestoOutput> evaluateInput(EfestoRuntimeContext context, EfestoInput... toEvaluate) {
        if (toEvaluate.length == 1) { // minor optimization for the (most typical) case with 1 input
            return getOptionalOutput(context, toEvaluate[0]).map(Collections::singletonList).orElse(Collections.emptyList());
        }
        Collection<EfestoOutput> toReturn = new ArrayList<>();
        for (EfestoInput efestoInput : toEvaluate) {
            getOptionalOutput(context, efestoInput).ifPresent(toReturn::add);
        }
        return toReturn;
    }


}
