/*
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

package org.jbpm.process.instance.impl;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import org.kie.api.runtime.KieRuntime;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

public class EmtpyKogitoProcessContext implements KogitoProcessContext {

    private Function<String, Object> resolver;

    public EmtpyKogitoProcessContext(Function<String, Object> resolver) {
        this.resolver = resolver;
    }

    @Override
    public KogitoProcessInstance getProcessInstance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public KogitoNodeInstance getNodeInstance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getVariable(String variableName) {
        return resolver.apply(variableName);
    }

    @Override
    public void setVariable(String variableName, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public KieRuntime getKieRuntime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public KogitoProcessRuntime getKogitoProcessRuntime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getContextData() {
        return Collections.emptyMap();
    }

}