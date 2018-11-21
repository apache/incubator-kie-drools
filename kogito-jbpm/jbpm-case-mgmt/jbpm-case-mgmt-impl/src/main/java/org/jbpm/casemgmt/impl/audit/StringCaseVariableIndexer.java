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
import java.util.List;

import org.jbpm.casemgmt.api.audit.CaseFileData;
import org.jbpm.casemgmt.api.audit.CaseVariableIndexer;

/**
 * Simple and default indexer for all case variables. It will keep the same name of the variable
 * and will generate string representation based on <code>variable.toString()</code> method
 *
 * this indexer is always added at the end of the chain of indexers to allow custom implementations take
 * precedence over this one.
 */
public class StringCaseVariableIndexer implements CaseVariableIndexer {

    @Override
    public boolean accept(Object variable) {
        return true;
    }

    @Override
    public List<CaseFileData> index(String name, Object variable) {
        List<CaseFileData> indexed = new ArrayList<CaseFileData>();
        
        CaseFileDataLog caseVariable = new CaseFileDataLog();
        caseVariable.setItemName(name);
        caseVariable.setItemValue(variable == null ? "" : variable.toString());
        caseVariable.setItemType(variable == null ? "" : variable.getClass().getName());
        indexed.add(caseVariable);
        
        return indexed;
    }

}
