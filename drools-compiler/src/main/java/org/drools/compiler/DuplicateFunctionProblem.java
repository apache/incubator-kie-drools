/*
* Copyright 2011 JBoss Inc
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
package org.drools.compiler;

import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.lang.descr.FunctionDescr;
import org.drools.rule.Function;




public class DuplicateFunctionProblem extends ConfigurableSeverityProblem {
    public static final String KEY = "duplicateFunction";
    
    private String functionName;
    private String functionNamespace;
    
    public DuplicateFunctionProblem(FunctionDescr func, KnowledgeBuilderConfiguration config) {
        super(config);
        functionName = func.getName();
        functionNamespace = func.getNamespace();
    }
    
    public DuplicateFunctionProblem(Function func, KnowledgeBuilderConfiguration config) {
        super(config);
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
    public int[] getErrorLines() {
        return null;
    }

    @Override
    String getOptionKey() {
        return KEY;
    }

}
