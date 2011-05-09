/*
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

package org.drools.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.mvel.MVELCompilationUnit.DroolsVarFactory;
import org.drools.spi.KnowledgeHelper;
import org.mvel2.ast.ASTNode;
import org.mvel2.ast.WithNode;
import org.mvel2.integration.Interceptor;
import org.mvel2.integration.VariableResolverFactory;

public class ModifyInterceptor
    implements
    Interceptor,
    Externalizable {
    private static final long serialVersionUID = 510l;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public int doBefore(ASTNode node,
                        VariableResolverFactory factory) {
        return 0;
    }

    public int doAfter(Object value,
                       ASTNode node,
                       VariableResolverFactory factory) {
        while ( factory != null && !(factory instanceof DroolsVarFactory)) {
            factory =  factory.getNextFactory();
        }
        
        if ( factory == null ) {
            throw new RuntimeException( "Unable to find DroolsMVELIndexedFactory" );
        }
        
        ((DroolsVarFactory)factory).getKnowledgeHelper().update( value );
        return 0;
    }
}
