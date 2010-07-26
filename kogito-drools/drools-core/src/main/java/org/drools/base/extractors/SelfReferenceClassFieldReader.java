/**
 * Copyright 2010 JBoss Inc
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

package org.drools.base.extractors;

import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;

/**
 * A special field extractor for the self reference "this".
 *  
 * @author etirelli
 */
public class SelfReferenceClassFieldReader extends BaseObjectClassFieldReader {

    private static final long serialVersionUID = 400L;

    public SelfReferenceClassFieldReader(final Class<?> clazz,
                                         final String fieldName) {
        super( -1, // index
               clazz, // fieldType
               ValueType.determineValueType( clazz ) ); // value type
    }

    public Object getValue(InternalWorkingMemory workingMemory, final Object object) {
        //return (object instanceof ShadowProxy) ? ((ShadowProxy) object).getShadowedObject() : object;
        return object;
    }   
    
    public boolean isNullValue(InternalWorkingMemory workingMemory, final Object object) {
        return getValue( workingMemory, object ) == null;
    }
    
    @Override
    public boolean isSelfReference() {
        return true;
    }
}
