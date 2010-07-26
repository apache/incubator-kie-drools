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

package org.drools.marshalling.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.util.ClassUtils;
import org.drools.marshalling.ObjectMarshallingStrategyAcceptor;

public class ClassObjectMarshallingStrategyAcceptor implements ObjectMarshallingStrategyAcceptor {
    public static final ClassObjectMarshallingStrategyAcceptor DEFAULT = new ClassObjectMarshallingStrategyAcceptor(new String[] { "*.*" } );
    
    private final Map<String, Object> patterns;
    
    public ClassObjectMarshallingStrategyAcceptor(String[] patterns) {
        this.patterns = new HashMap<String, Object>();
        for (String pattern : patterns ) {
            addPattern( pattern );
        }
    }
    
    public ClassObjectMarshallingStrategyAcceptor() {
        this.patterns = new HashMap<String, Object>();
    }
    
    private void addPattern(String pattern) {
        
        ClassUtils.addImportStylePatterns( this.patterns, pattern );
    }

    public boolean accept(Object object) {
        return ClassUtils.isMatched( this.patterns, object.getClass().getName() );
    }

}
