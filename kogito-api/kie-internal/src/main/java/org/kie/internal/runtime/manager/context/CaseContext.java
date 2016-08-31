/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.internal.runtime.manager.context;

import org.kie.api.runtime.manager.Context;

/**
 * Context implementation to deliver capabilities to find proper <code>RuntimeEngine</code>
 * instances based on case identifier. Use by strategy:
 * <ul>
 * 	<li>PerCase</li>
 * </ul>
 * To obtain instances of this context use one of the following static methods:
 * <ul>
 * 	<li><code>get(String)</code> to get context for case</li>
 * </ul>
 *
 */
public class CaseContext implements Context<String> {

    private String caseIdentifier;
    
    public CaseContext(String caseIdentifier) {
        this.caseIdentifier = caseIdentifier;
    }
    
    @Override
    public String getContextId() {

        return caseIdentifier;
    }
    
    /**
     * Returns new instance of <code>CaseContext</code> with case identifier
     * @param caseIdentifier unique case identifier
     * @return
     */
    public static CaseContext get(String caseIdentifier) {
        return new CaseContext(caseIdentifier);
    }
}
