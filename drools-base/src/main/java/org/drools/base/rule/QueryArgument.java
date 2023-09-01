package org.drools.base.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.base.DroolsQuery;
import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.kie.api.runtime.rule.Variable;

public interface QueryArgument extends Externalizable {

    QueryArgument normalize(ClassLoader classLoader);

    Object getValue(ValueResolver valueResolver, BaseTuple tuple);

    static Object evaluateDeclaration( ValueResolver valueResolver, BaseTuple tuple, Declaration declaration ) {
        Object tupleObject = tuple.get( declaration ).getObject();
        if (tupleObject instanceof DroolsQuery && declaration.getExtractor().getIndex() >= 0 &&
                ( (DroolsQuery) tupleObject ).getVariables()[declaration.getExtractor().getIndex()] != null ) {
            return Variable.v;
        }
        return declaration.getValue( valueResolver, tupleObject );
    }

    class Declr implements QueryArgument {
        private Declaration declaration;

        public Declr() { }

        public Declr( Declaration declaration ) {
            this.declaration = declaration;
        }

        @Override
        public Object getValue( ValueResolver valueResolver, BaseTuple tuple ) {
            return QueryArgument.evaluateDeclaration( valueResolver, tuple, declaration );
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
        public Object getValue( ValueResolver valueResolver, BaseTuple baseTuple) {
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
        public Object getValue( ValueResolver valueResolver, BaseTuple tuple) {
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
        public Object getValue( ValueResolver valueResolver, BaseTuple tuple) {
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
