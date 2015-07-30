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

package org.drools.core.base.dataproviders;

import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELCompileable;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.ArrayIterator;
import org.drools.core.util.MVELSafeHelper;
import org.mvel2.integration.VariableResolverFactory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MVELDataProvider
    implements
    DataProvider,
    MVELCompileable,
    Externalizable {

    private static final long       serialVersionUID = 510l;

    private MVELCompilationUnit     unit;
    private String                  id;

    private Serializable            expr;

    private List<MVELDataProvider>  clones;

    public MVELDataProvider() {

    }

    public MVELDataProvider(final MVELCompilationUnit unit,
                            final String id) {
        this.unit = unit;
        this.id = id;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        id = in.readUTF();
        unit = (MVELCompilationUnit) in.readObject();
        clones = (List<MVELDataProvider>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( id );
        out.writeObject(unit);
        out.writeObject( clones );
    }

    @SuppressWarnings("unchecked")
    public void compile(MVELDialectRuntimeData runtimeData) {
        expr = unit.getCompiledExpression( runtimeData );
        if (clones != null) {
            for (MVELDataProvider clone : clones) {
                clone.expr = clone.unit.getCompiledExpression(runtimeData);
            }
        }
//        @TODO URGENT DO NOT FORGET!!!!
//        Map previousDeclarations = this.unit.getFactory().getPreviousDeclarations();
//        this.requiredDeclarations = (Declaration[]) previousDeclarations.values().toArray( new Declaration[previousDeclarations.size()] );
    }

    public void compile(MVELDialectRuntimeData runtimeData, RuleImpl rule) {
        expr = unit.getCompiledExpression( runtimeData, rule.toRuleNameAndPathString() );
    }

    public Declaration[] getRequiredDeclarations() {
        return this.unit.getPreviousDeclarations();
    }

    public void replaceDeclaration(Declaration declaration,
                                   Declaration resolved) {
        this.unit.replaceDeclaration( declaration,
                                      resolved );
    }

    public Object createContext() {
        return null;
    }

    public Iterator getResults(final LeftTuple tuple,
                               final InternalWorkingMemory wm,
                               final PropagationContext ctx,
                               final Object executionContext) {
        VariableResolverFactory factory = unit.getFactory( null, null, null, null, tuple, null, wm, wm.getGlobalResolver()  );

        //this.expression.
        final Object result = MVELSafeHelper.getEvaluator().executeExpression( this.expr, factory );

        if ( result == null ) {
            return Collections.EMPTY_LIST.iterator();
        } else if ( result instanceof Collection ) {
            return ((Collection) result).iterator();
        } else if ( result instanceof Iterator ) {
            return (Iterator) result;
        } else if ( result.getClass().isArray() ) {
            return new ArrayIterator( result );
        } else {
            return Collections.singletonList( result ).iterator();
        }
    }

    public DataProvider clone() {
        MVELDataProvider clone = new MVELDataProvider(unit.clone(), id);
        clone.expr = expr;
        if (clones == null) {
            clones = new ArrayList<MVELDataProvider>();
        }
        clones.add(clone);
        return clone;
    }
}
