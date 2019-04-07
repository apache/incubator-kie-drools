/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.api;

import java.util.Collection;

import org.drools.core.ClassObjectFilter;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.kie.api.runtime.KieRuntime;

public class CaseUtils {

    public static String getCaseId(KieRuntime kruntime) {
        Collection<? extends Object> caseFiles = kruntime.getObjects(new ClassObjectFilter(CaseFileInstance.class));
        if (caseFiles.size() != 1) {
            throw new IllegalStateException("Not able to find distinct case file - found case files " + caseFiles.size());
        }
        CaseFileInstance caseFile = (CaseFileInstance) caseFiles.iterator().next();
        
        return caseFile.getCaseId();
    }
}
