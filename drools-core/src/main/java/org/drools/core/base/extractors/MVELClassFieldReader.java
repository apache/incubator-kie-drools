/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.base.extractors;

import org.drools.core.base.ValueType;
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
