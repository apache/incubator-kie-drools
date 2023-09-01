package org.drools.mvel.extractors;

import org.drools.base.base.ValueType;
import org.mvel2.compiler.ExecutableStatement;

public interface MVELClassFieldReader {
    String getClassName();
    
    boolean isTypeSafe();
    
    String getExpression();
    
    void setFieldType(Class< ? > fieldType);
    
    void setValueType(ValueType valueType);
    
    void setExecutableStatement(ExecutableStatement expression);

    Object getEvaluationContext();
    void setEvaluationContext(Object evaluationContext);
}
