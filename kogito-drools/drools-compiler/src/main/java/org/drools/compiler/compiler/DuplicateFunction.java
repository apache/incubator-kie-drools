/*
* Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.compiler;

import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.core.rule.Function;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;




public class DuplicateFunction extends ConfigurableSeverityResult {
    public static final String KEY = "duplicateFunction";
    
    private String functionName;
    private String functionNamespace;
    
    public DuplicateFunction(FunctionDescr func, KnowledgeBuilderConfiguration config) {
        super(func.getResource(), config);
        functionName = func.getName();
        functionNamespace = func.getNamespace();
    }
    
    public DuplicateFunction(Function func, KnowledgeBuilderConfiguration config) {
        super(func.getResource(), config);
        functionName = func.getName();
        functionName = func.getNamespace();
    }

    @Override
    public String getMessage() {
        return functionName 
        + " in namespace " + functionNamespace 
        + " is about to be redefined";
    }

    @Override
    public int[] getLines() {
        return null;
    }

    @Override
    String getOptionKey() {
        return KEY;
    }

}
