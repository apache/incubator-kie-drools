/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.fluent;

import java.util.Map;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.dmn.api.core.DMNResult;
import org.kie.internal.command.RegistryContext;

public class GetAllDMNContextCommand implements ExecutableCommand<Map<String, Object>> {

    @Override
    public Map<String, Object> execute(Context context) {
        RegistryContext registryContext = (RegistryContext) context;
        DMNResult dmnResult = registryContext.lookup(DMNResult.class);
        if(dmnResult == null) {
            throw new IllegalStateException("There is not DMNResult available");
        }

        return dmnResult.getContext().getAll();
    }
}
