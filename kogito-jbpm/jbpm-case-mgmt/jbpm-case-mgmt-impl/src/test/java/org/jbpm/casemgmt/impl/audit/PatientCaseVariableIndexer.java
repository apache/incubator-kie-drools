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

package org.jbpm.casemgmt.impl.audit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jbpm.casemgmt.api.audit.CaseFileData;
import org.jbpm.casemgmt.api.audit.CaseVariableIndexer;
import org.jbpm.casemgmt.impl.objects.Patient;


public class PatientCaseVariableIndexer implements CaseVariableIndexer {

    @Override
    public boolean accept(Object variable) {        
        return Patient.class.isAssignableFrom(variable.getClass());
    }

    @Override
    public List<CaseFileData> index(String name, Object variable) {
        List<CaseFileData> indexed = new ArrayList<CaseFileData>();
        
        CaseFileDataLog caseVariable = new CaseFileDataLog();
        caseVariable.setItemName(name);
        caseVariable.setItemValue(variable == null ? "" : variable.toString());
        caseVariable.setItemType(variable.getClass().getName());
        
        indexed.add(caseVariable);
        
        // add mapped information as another entry
        CaseFileDataLog caseVariableMapped = new CaseFileDataLog();
        caseVariableMapped.setItemName(name + "_name");
        caseVariableMapped.setItemValue(((Patient) variable).getName());
        caseVariableMapped.setItemType(String.class.getName());
        
        indexed.add(caseVariableMapped);
        
        return indexed;
    }

    @Override
    public List<String> getIndexNames(String name) {
        
        return Arrays.asList(name, name + "_name");
    }

}
