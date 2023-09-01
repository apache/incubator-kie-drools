
package org.drools.compiler.compiler;

import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.base.rule.Function;
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
    protected String getOptionKey() {
        return KEY;
    }

}
