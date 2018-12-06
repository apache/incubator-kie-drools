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
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.internal.command.RegistryContext;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class NewDMNRuntimeCommandTest {

    @Test
    public void execute() {
        RegistryContext registryContext = new ContextImpl();
        NewDMNRuntimeCommand newDMNRuntimeCommand = new NewDMNRuntimeCommand();

        try {
            newDMNRuntimeCommand.execute(registryContext);
            fail();
        } catch (IllegalStateException ignored) {

        }

        registryContext.register(KieContainer.class, new KieHelper().getKieContainer());

        newDMNRuntimeCommand.execute(registryContext);

        assertNotNull(registryContext.lookup(DMNRuntime.class));
    }
}