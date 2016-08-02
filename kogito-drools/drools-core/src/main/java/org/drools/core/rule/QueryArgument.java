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

import org.drools.core.base.DroolsQuery;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.util.MVELSafeHelper;
import org.kie.api.runtime.rule.Variable;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.drools.core.rule.QueryArgument.Declr.evaluateDeclaration;

public interface QueryArgument extends Externalizable {

    QueryArgument normalize(ClassLoader classLoader);

    Object getValue( InternalWorkingMemory wm, LeftTuple leftTuple);

    class Declr implements QueryArgument {
        private Declaration declaration;

        public Declr() { }

        public Declr( Declaration declaration ) {
            this.declaration = declaration;
        }

        @Override
        public Object getValue( InternalWorkingMemory wm, LeftTuple leftTuple ) {
            return evaluateDeclaration( wm, leftTuple, declaration );
        }

        static Object evaluateDeclaration( InternalWorkingMemory wm, LeftTuple leftTuple, Declaration declaration ) {
            Object tupleObject = leftTuple.get( declaration ).getObject();
            if ( tupleObject instanceof DroolsQuery && declaration.getExtractor() instanceof ArrayElementReader &&
                 ( (DroolsQuery) tupleObject ).getVariables()[declaration.getExtractor().getIndex()] != null ) {
                return Variable.v;
            }
            return declaration.getValue( wm, tupleObject );
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

    class Expression implements QueryArgument {
        private List<Declaration> declarations;
        private String expression;
        private ParserContext parserContext;

        private transient Class<?> argumentClass;
        private transient Serializable mvelExpr;

        public Expression() { }

        public Expression( List<Declaration> declarations, String expression, ParserContext parserContext ) {
            this.declarations = declarations;
            this.expression = expression;
            this.parserContext = parserContext;
            init();
        }

        private void init() {
            Map<String, Class> inputs = new HashMap<String, Class>();
            for (Declaration d : declarations) {
                inputs.put(d.getBindingName(), d.getDeclarationClass());
            }
            parserContext.setInputs(inputs);

            this.argumentClass = MVEL.analyze( expression, parserContext );
            this.mvelExpr = MVEL.compileExpression( expression, parserContext );
        }

        @Override
        public Object getValue( InternalWorkingMemory wm, LeftTuple leftTuple ) {
            Map<String, Object> vars = new HashMap<String, Object>();
            for (Declaration d : declarations) {
                vars.put(d.getBindingName(), evaluateDeclaration( wm, leftTuple, d ));
            }
            return MVELSafeHelper.getEvaluator().executeExpression( this.mvelExpr, vars );
        }

        @Override
        public QueryArgument normalize( ClassLoader classLoader ) {
            parserContext.getParserConfiguration().setClassLoader( classLoader );
            return new Expression( declarations, expression, parserContext );
        }

        @Override
        public void writeExternal( ObjectOutput out ) throws IOException {
            out.writeObject( declarations );
            out.writeObject( expression );
            out.writeObject( parserContext );
        }

        @Override
        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
            declarations = (List<Declaration>) in.readObject();
            expression = (String) in.readObject();
            parserContext = (ParserContext) in.readObject();
            init();
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
