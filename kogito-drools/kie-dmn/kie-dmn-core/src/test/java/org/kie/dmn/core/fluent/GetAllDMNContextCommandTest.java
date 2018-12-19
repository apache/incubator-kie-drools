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

import org.drools.core.command.impl.ContextImpl;
import org.junit.Test;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.core.impl.DMNContextImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.internal.command.RegistryContext;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

public class GetAllDMNContextCommandTest {

    @Test
    public void execute() {
        RegistryContext registryContext = new ContextImpl();
        GetAllDMNContextCommand getAllDMNContextCommand = new GetAllDMNContextCommand();

        assertThatThrownBy(() -> getAllDMNContextCommand.execute(registryContext))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("There is not DMNResult available");

        DMNResultImpl dmnResult = new DMNResultImpl(null);
        dmnResult.setContext(new DMNContextImpl());

        registryContext.register(DMNResult.class, dmnResult);

        Map<String, Object> result = getAllDMNContextCommand.execute(registryContext);
        assertEquals(0, result.size());
    }
}