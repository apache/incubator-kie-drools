package org.drools.base.extractors;

import org.drools.base.ValueType;
import org.mvel2.compiler.ExecutableStatement;

public interface MVELClassFieldReader {
    public String getClassName();
    
    public boolean isTypeSafe();
    
    public String getExpression();
    
    public void setFieldType(Class< ? > fieldType);
    
    public void setValueType(ValueType valueType);
    
    public void setExecutableStatement(ExecutableStatement expression);
}
