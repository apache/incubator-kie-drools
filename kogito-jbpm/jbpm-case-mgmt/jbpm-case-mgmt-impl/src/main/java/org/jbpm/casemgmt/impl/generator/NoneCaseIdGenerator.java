/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.impl.generator;

import java.util.Map;

import org.jbpm.casemgmt.api.generator.CaseIdGenerator;
import org.jbpm.casemgmt.api.generator.CasePrefixNotFoundException;

/**
 * Generator that in general does not generate but rely on given case ids.
 * By default it expects to have "CaseId" parameter given that represents case id. 
 * The name of the property can be changed by system property:<br/>
 * <code>org.jbpm.cases.generator.caseid.param</code>
 *
 */
public class NoneCaseIdGenerator implements CaseIdGenerator {

    private static final String CASE_ID_PARAM = System.getProperty("org.jbpm.cases.generator.caseid.param", "CaseId");
    private static final String IDENTIFIER = "None";
   
    @Override
    public void register(String prefix) {
        // no-op as it completely relies on given CaseId as parameter
    }

    @Override
    public void unregister(String prefix) {
        // no-op as it completely relies on given CaseId as parameter   
    }

    @Override
    public String generate(String prefix, Map<String, Object> optionalParameters) throws CasePrefixNotFoundException {
        if (optionalParameters == null || !optionalParameters.containsKey(CASE_ID_PARAM)) {
            throw new CasePrefixNotFoundException("No case identifier found in parameters");
        }
        
        return optionalParameters.get(CASE_ID_PARAM).toString();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

}
