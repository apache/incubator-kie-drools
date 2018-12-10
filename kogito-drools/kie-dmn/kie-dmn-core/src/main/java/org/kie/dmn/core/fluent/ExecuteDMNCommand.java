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

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.internal.command.RegistryContext;

public class ExecuteDMNCommand implements ExecutableCommand<DMNResult> {

    @Override
    public DMNResult execute(Context context) {
        RegistryContext registryContext = (RegistryContext) context;
        DMNModel activeModel = registryContext.lookup(DMNModel.class);
        DMNRuntime dmnRuntime = registryContext.lookup(DMNRuntime.class);
        DMNContext dmnContext = registryContext.lookup(DMNContext.class);
        if (activeModel == null) {
            throw new IllegalStateException("No DMN active model defined");
        }
        if (dmnRuntime == null) {
            throw new IllegalStateException("No DMNRuntime available");
        }
        if (dmnContext == null) {
            dmnContext = dmnRuntime.newContext();
        }
        DMNResult dmnResult = dmnRuntime.evaluateAll(activeModel, dmnContext);
        registryContext.register(DMNResult.class, dmnResult);

        // reset context
        registryContext.register(DMNContext.class, dmnRuntime.newContext());

        return dmnResult;
    }
}
