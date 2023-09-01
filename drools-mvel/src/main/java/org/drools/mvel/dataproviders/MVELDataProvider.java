/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.dataproviders;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.drools.base.base.ValueResolver;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.DataProvider;
import org.drools.mvel.MVELDialectRuntimeData;
import org.drools.mvel.expr.MVELCompilationUnit;
import org.drools.mvel.expr.MVELCompileable;
import org.drools.mvel.expr.MvelEvaluator;
import org.mvel2.integration.VariableResolverFactory;

import static org.drools.mvel.expr.MvelEvaluator.createMvelEvaluator;

public class MVELDataProvider implements DataProvider, MVELCompileable, Externalizable {

    private static final long       serialVersionUID = 510l;

    private MVELCompilationUnit unit;
    private String                  id;

    private MvelEvaluator<Object> evaluator;

    private List<MVELDataProvider>  clones;

    public MVELDataProvider() {

    }

    public MVELDataProvider(final MVELCompilationUnit unit,
                            final String id) {
        this.unit = unit;
        this.id = id;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof MVELDataProvider)) {
            return false;
        }

        return unit.equals( ((MVELDataProvider) obj).unit );
    }

    @Override
    public int hashCode() {
        return unit.hashCode();
    }

    public void readExternal( ObjectInput in ) throws IOException,
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
    public void compile( MVELDialectRuntimeData runtimeData) {
        evaluator = createMvelEvaluator( unit.getCompiledExpression( runtimeData ) );
        if (clones != null) {
            for (MVELDataProvider clone : clones) {
                clone.evaluator = createMvelEvaluator( clone.unit.getCompiledExpression(runtimeData) );
            }
        }
    }

    public void compile( MVELDialectRuntimeData runtimeData, RuleImpl rule) {
        evaluator = createMvelEvaluator( unit.getCompiledExpression( runtimeData, rule.toRuleNameAndPathString() ) );
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

    public Iterator getResults(final BaseTuple tuple,
                               final ValueResolver valueResolver,
                               final Object executionContext) {
        return asIterator( evaluate( tuple, valueResolver ) );
    }

    protected Object evaluate( BaseTuple tuple, ValueResolver valueResolver ) {
        VariableResolverFactory factory = unit.getFactory( null, null, null, null, tuple, null, valueResolver, valueResolver.getGlobalResolver() );
        return evaluator.evaluate( factory );
    }

    protected Iterator asIterator( Object result ) {
        if ( result == null ) {
            return Collections.EMPTY_LIST.iterator();
        } else if ( result instanceof Iterable ) {
            return ((Iterable) result).iterator();
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
        clone.evaluator = evaluator;
        if (clones == null) {
            clones = new ArrayList<>();
        }
        clones.add(clone);
        return clone;
    }

    public boolean isReactive() {
        return false;
    }
}
