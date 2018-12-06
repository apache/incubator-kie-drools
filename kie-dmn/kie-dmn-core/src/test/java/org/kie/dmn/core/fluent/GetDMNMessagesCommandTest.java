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

import java.util.List;

import org.drools.core.command.impl.ContextImpl;
import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.core.impl.DMNContextImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.internal.command.RegistryContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GetDMNMessagesCommandTest {

    @Test
    public void execute() {
        RegistryContext registryContext = new ContextImpl();
        GetDMNMessagesCommand getDMNMessagesCommand = new GetDMNMessagesCommand();

        try {
            getDMNMessagesCommand.execute(registryContext);
            fail();
        } catch (IllegalStateException ignored) {

        }
        DMNResultImpl dmnResult = new DMNResultImpl(null);
        dmnResult.setContext(new DMNContextImpl());

        registryContext.register(DMNResult.class, dmnResult);

        List<DMNMessage> result = getDMNMessagesCommand.execute(registryContext);
        assertEquals(0, result.size());
    }
}