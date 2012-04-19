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

package org.drools.base.mvel;

import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.definition.rule.Rule;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.spi.Activation;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Salience;
import org.drools.spi.Tuple;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;

import java.io.*;

public class MVELObjectExpression
    implements
    MVELCompileable,
    Externalizable {

    private static final long   serialVersionUID = 510l;

    private MVELCompilationUnit unit;
    private String              id;

    private Serializable        expr;

    public MVELObjectExpression() {
    }

    public MVELObjectExpression(final MVELCompilationUnit unit,
                                final String id) {
        this.unit = unit;
        this.id = id;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        unit = (MVELCompilationUnit) in.readObject();
        id = in.readUTF();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( unit );
        out.writeUTF( id );
    }
    
    public MVELCompilationUnit getMVELCompilationUnit() {
        return this.unit;
    }    

    public void compile(MVELDialectRuntimeData runtimeData) {
        expr = unit.getCompiledExpression( runtimeData );
    }

    public Object getValue(final Activation item,
                           final Declaration[] declrs,
                           final Rule rule,
                           final WorkingMemory workingMemory) {
        VariableResolverFactory factory = unit.getFactory( null, declrs,
                                                           rule, null, (LeftTuple)item.getTuple(), null, (InternalWorkingMemory) workingMemory, workingMemory.getGlobalResolver() );
        
        // do we have any functions for this namespace?
        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( this.id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        return MVEL.executeExpression(this.expr,
                                      factory);
    }
    
    public String toString() {
        return this.unit.getExpression();
    }

}
