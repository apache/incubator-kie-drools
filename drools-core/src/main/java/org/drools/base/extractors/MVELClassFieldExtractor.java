/*
 * Copyright 2006 JBoss Inc
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
 *
 * Created on Jun 12, 2007
 */
package org.drools.base.extractors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassFieldExtractorFactory;
import org.drools.base.ValueType;
import org.drools.spi.Extractor;
import org.drools.spi.FieldExtractor;
import org.mvel.CompiledExpression;
import org.mvel.ExpressionCompiler;
import org.mvel.MVEL;

/**
 * A class field extractor that uses MVEL engine to extract the actual value for a given
 * expression. We use MVEL to resolve nested accessor expressions.
 * 
 * @author etirelli
 */
public class MVELClassFieldExtractor extends BaseObjectClassFieldExtractor {
    
    private static final long serialVersionUID = 1435386838162518010L;

    private CompiledExpression mvelExpression = null;
    private Map extractors = null;
    private Map variables = null; 

    public MVELClassFieldExtractor(Class clazz,
                                   String fieldName,
                                   ClassLoader classLoader) {
        super( -1, // index
               Object.class, // fieldType
               ValueType.determineValueType( Object.class ) ); // value type
        this.extractors = new HashMap();
        this.variables = new HashMap();

        ExpressionCompiler compiler = new ExpressionCompiler( fieldName );
        this.mvelExpression = compiler.compile();
        
        Set inputs = compiler.getInputs();
        for( Iterator it = inputs.iterator(); it.hasNext(); ) {
            String basefield = (String) it.next();
                        
            Extractor extr = ClassFieldExtractorCache.getExtractor(  clazz, basefield, classLoader );
            this.extractors.put( basefield, extr );
        }
    }

    /* (non-Javadoc)
     * @see org.drools.base.extractors.BaseObjectClassFieldExtractor#getValue(java.lang.Object)
     */
    public Object getValue(Object object) {
        for( Iterator it = this.extractors.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            String var = (String) entry.getKey();
            FieldExtractor extr = (FieldExtractor) entry.getValue();
            
            this.variables.put( var, extr.getValue( object ));
        }
        return MVEL.executeExpression( mvelExpression, this.variables );
    }

}
