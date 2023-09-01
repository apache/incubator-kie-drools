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
package org.kie.dmn.core.runtime;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.KieBase;
import org.kie.api.internal.runtime.KieRuntimeService;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.impl.DMNRuntimeKBWrappingIKB;

public class DMNRuntimeService implements KieRuntimeService<DMNRuntime> {

    @Override
    public DMNRuntime newKieRuntime(KieBase kieBase) {
        InternalKnowledgeBase kb = (InternalKnowledgeBase) kieBase;
        return new DMNRuntimeImpl(new DMNRuntimeKBWrappingIKB(kb));
    }

    @Override
    public Class getServiceInterface() {
        return DMNRuntime.class;
    }
}
