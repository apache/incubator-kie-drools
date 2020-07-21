/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.spi;

import java.util.Collection;

import org.drools.core.ClassObjectFilter;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.runtime.process.CaseData;

public class ProcessContext extends AbstractProcessContext {
    
    public ProcessContext(KieRuntime kruntime) {
        super(kruntime);
    }
    public CaseData getCaseData() {

        Collection<? extends Object> objects = getKieRuntime().getObjects(new ClassObjectFilter(CaseData.class));
        if (objects.size() == 0) {
            return null;
        }

        return (CaseData) objects.iterator().next();
    }

    public CaseAssignment getCaseAssignment() {
        Collection<? extends Object> objects = getKieRuntime().getObjects(new ClassObjectFilter(CaseAssignment.class));
        if (objects.size() == 0) {
            return null;
        }

        return (CaseAssignment) objects.iterator().next();
    }
}
