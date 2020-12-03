/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ruleunit.command.pmml;

import java.util.ArrayList;

import org.drools.core.command.impl.ContextImpl;
import org.junit.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;

import static org.junit.Assert.*;

public class ApplyPmmlModelCommandExecutorImplTest {

    @Test(expected = IllegalStateException.class)
    public void executeWithjPMMML() {
        ApplyPmmlModelCommandExecutorImpl cmdExecutor = new ApplyPmmlModelCommandExecutorImplMock(true);
        cmdExecutor.execute(new ContextImpl(), new PMMLRequestData(), new ArrayList<>(), "packageName", true);
    }

    @Test
    public void executeWithoutjPMMML() {
        ApplyPmmlModelCommandExecutorImpl cmdExecutor = new ApplyPmmlModelCommandExecutorImplMock(false);
        PMML4Result retrieved = cmdExecutor.execute(new ContextImpl(), new PMMLRequestData(), new ArrayList<>(), "packageName", true);
        assertNotNull(retrieved);
    }

    @Test(expected = IllegalStateException.class)
    public void executeWithoutRequestData() {
        ApplyPmmlModelCommandExecutorImpl cmdExecutor = new ApplyPmmlModelCommandExecutorImplMock(false);
        cmdExecutor.execute(new ContextImpl(), null, new ArrayList<>(), "packageName", true);
    }

    @Test
    public void executeWithRequestData() {
        ApplyPmmlModelCommandExecutorImpl cmdExecutor = new ApplyPmmlModelCommandExecutorImplMock(false);
        PMML4Result retrieved = cmdExecutor.execute(new ContextImpl(), new PMMLRequestData(), new ArrayList<>(), "packageName", true);
        assertNotNull(retrieved);
    }

    private class ApplyPmmlModelCommandExecutorImplMock extends ApplyPmmlModelCommandExecutorImpl {

        private final boolean jPMMLAvailableToClassLoader;

        public ApplyPmmlModelCommandExecutorImplMock(boolean jPMMLAvailableToClassLoader) {
            this.jPMMLAvailableToClassLoader = jPMMLAvailableToClassLoader;
        }

        @Override
        protected boolean isjPMMLAvailableToClassLoader(ClassLoader classLoader) {
            return jPMMLAvailableToClassLoader;
        }
    }
}