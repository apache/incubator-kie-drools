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

package org.drools.core.base.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.spi.Enabled;
import org.drools.core.spi.Tuple;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;

public class MVELEnabledExpression
    implements
    Enabled,
    MVELCompileable,
    Externalizable {

    private static final long   serialVersionUID = 510l;

    private MVELCompilationUnit unit;
    private String              id;

    private Serializable        expr;

    public MVELEnabledExpression() {
    }

    public MVELEnabledExpression(final MVELCompilationUnit unit,
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

    public boolean getValue(final Tuple tuple,
                            final Declaration[] declrs,
                            final RuleImpl rule,
                            final WorkingMemory workingMemory) {
        VariableResolverFactory factory = unit.getFactory( null, declrs,
                                                           rule, null, (LeftTuple) tuple, null, (InternalWorkingMemory) workingMemory, workingMemory.getGlobalResolver()  );

        // do we have any functions for this namespace?
        InternalKnowledgePackage pkg = workingMemory.getKnowledgeBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( this.id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        return ((Boolean) MVEL.executeExpression( this.expr,
                                                  null,
                                                  factory )).booleanValue();
    }
    
    public String toString() {
        return this.unit.getExpression();
    }

}
