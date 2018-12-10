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

import org.drools.core.command.impl.ContextImpl;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.internal.command.RegistryContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ExecuteDMNCommandTest {

    @Test
    public void execute() {
        RegistryContext registryContext = new ContextImpl();
        ExecuteDMNCommand executeDMNCommand = new ExecuteDMNCommand();

        try {
            executeDMNCommand.execute(registryContext);
            fail();
        } catch (IllegalStateException ignored) {

        }

        registryContext.register(DMNModel.class, new DMNModelImpl(null));

        try {
            executeDMNCommand.execute(registryContext);
            fail();
        } catch (IllegalStateException ignored) {

        }

        DMNContext dmnContext = DMNFactory.newContext();
        dmnContext.set("example", 10);

        registryContext.register(DMNRuntime.class, new DMNRuntimeImpl(null));
        registryContext.register(DMNContext.class, dmnContext);

        DMNResult result = executeDMNCommand.execute(registryContext);
        assertNotNull(result);
        DMNContext newDmnContext = registryContext.lookup(DMNContext.class);
        assertEquals(1, dmnContext.getAll().size());
        assertEquals(0, newDmnContext.getAll().size());
    }
}