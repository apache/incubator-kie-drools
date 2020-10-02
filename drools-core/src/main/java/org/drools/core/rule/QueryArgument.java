/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.kie.api.runtime.rule.Variable;

public interface QueryArgument extends Externalizable {

    QueryArgument normalize(ClassLoader classLoader);

    Object getValue( InternalWorkingMemory wm, LeftTuple leftTuple);

    static Object evaluateDeclaration( InternalWorkingMemory wm, LeftTuple leftTuple, Declaration declaration ) {
        Object tupleObject = leftTuple.get( declaration ).getObject();
        if ( tupleObject instanceof DroolsQuery && declaration.getExtractor().getIndex() >= 0 &&
                ( (DroolsQuery) tupleObject ).getVariables()[declaration.getExtractor().getIndex()] != null ) {
            return Variable.v;
        }
        return declaration.getValue( wm, tupleObject );
    }

    class Declr implements QueryArgument {
        private Declaration declaration;

        public Declr() { }

        public Declr( Declaration declaration ) {
            this.declaration = declaration;
        }

        @Override
        public Object getValue( InternalWorkingMemory wm, LeftTuple leftTuple ) {
            return QueryArgument.evaluateDeclaration( wm, leftTuple, declaration );
        }

        @Override
        public QueryArgument normalize(ClassLoader classLoader) {
            return this;
        }

        @Override
        public void writeExternal( ObjectOutput out ) throws IOException {
            out.writeObject( declaration );
        }

        @Override
        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
            declaration = (Declaration) in.readObject();
        }

        public Declaration getDeclaration() {
            return declaration;
        }

        public Class<?> getArgumentClass() {
            return declaration.getDeclarationClass();
        }
    }

    class Literal implements QueryArgument {
        private Object value;

        public Literal() { }

        public Literal( Object value ) {
            this.value = value;
        }

        @Override
        public Object getValue( InternalWorkingMemory wm, LeftTuple leftTuple) {
            return value;
        }

        @Override
        public QueryArgument normalize(ClassLoader classLoader) {
            try {
                return value instanceof Class ? new Literal(classLoader.loadClass(((Class)value).getName())) : this;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException( e );
            }
        }

        @Override
        public void writeExternal( ObjectOutput out ) throws IOException {
            out.writeObject( value );
        }

        @Override
        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
            value = in.readObject();
        }
    }

    Var VAR = new Var();
    class Var implements QueryArgument {

        @Override
        public Object getValue( InternalWorkingMemory wm, LeftTuple leftTuple) {
            return Variable.v;
        }

        @Override
        public QueryArgument normalize(ClassLoader classLoader) {
            return this;
        }

        @Override
        public void writeExternal( ObjectOutput out ) throws IOException { }

        @Override
        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException { }
    }

    Null NULL = new Null();
    class Null implements QueryArgument {

        @Override
        public Object getValue( InternalWorkingMemory wm, LeftTuple leftTuple) {
            return null;
        }

        @Override
        public QueryArgument normalize(ClassLoader classLoader) {
            return this;
        }

        @Override
        public void writeExternal( ObjectOutput out ) throws IOException { }

        @Override
        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException { }
    }
}
