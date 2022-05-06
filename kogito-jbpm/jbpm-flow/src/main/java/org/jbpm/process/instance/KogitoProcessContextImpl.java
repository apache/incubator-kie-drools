/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.instance;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.process.AbstractProcessContext;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.runtime.process.CaseData;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

public class KogitoProcessContextImpl extends AbstractProcessContext implements KogitoProcessContext {

    private Map<String, Object> contextData;

    public KogitoProcessContextImpl(KieRuntime kruntime) {
        super(kruntime);
        contextData = new HashMap<>();
    }

    @Override
    public KogitoProcessInstance getProcessInstance() {
        return (KogitoProcessInstance) super.getProcessInstance();
    }

    @Override
    public KogitoNodeInstance getNodeInstance() {
        return (KogitoNodeInstance) super.getNodeInstance();
    }

    @Override
    public KogitoProcessRuntime getKogitoProcessRuntime() {
        return InternalProcessRuntime.asKogitoProcessRuntime(getKieRuntime());
    }

    @Override
    public CaseAssignment getCaseAssignment() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CaseData getCaseData() {
        throw new UnsupportedOperationException();
    }

    public void setContextData(Map<String, Object> contextData) {
        this.contextData = contextData;

    }

    @Override
    public Map<String, Object> getContextData() {
        return contextData;
    }
}
