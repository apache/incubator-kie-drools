package org.drools.base.definitions.rule.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.annotation.Annotation;
import java.util.function.Function;

import org.drools.base.base.DroolsQuery;
import org.drools.base.base.ValueResolver;
import org.drools.base.rule.Declaration;
import org.drools.base.base.ObjectType;
import org.kie.api.definition.rule.Query;
import org.kie.api.runtime.rule.Match;

public class QueryImpl extends RuleImpl implements Query {

    private static final long serialVersionUID = 510l;

    public QueryImpl() {

    }
    
    private Declaration[] parameters;

    public QueryImpl(final String name) {
        super( name );
        setActivationListener( "query" );
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( parameters );
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.parameters = ( Declaration[] ) in.readObject();
    }

    /**
     * Override this as Queries will NEVER have a consequence, and it should
     * not be taken into account when deciding if it is valid.
     */
    public boolean isValid() {
        return super.isSemanticallyValid();
    }

    public void setParameters(Declaration[] parameters) {
        this.parameters = parameters;
    }

    public Declaration[] getParameters() {
        return this.parameters;
    }
    
    @Override
    public KnowledgeType getKnowledgeType() {
        return KnowledgeType.QUERY;
    }

    @Override
    public boolean isQuery() {
        return true;
    }

    public boolean isAbductive() {
        return false;
    }
    public boolean processAbduction(Match resultLeftTuple, DroolsQuery dquery, Object[] objects, ValueResolver valueResolver) {
        return true;
    }

    public boolean isReturnBound() {
        return false;
    }

    // The following methods are necessary only to build an abductive query. That's because the query builder
    // is in drools-compiler and we don't want to make drools-tms to depend on it.

    public void setReturnType(ObjectType objectType, String[] params, String[] args, Declaration[] declarations ) throws NoSuchMethodException {
        throw new UnsupportedOperationException("Available only for abductive query");
    }

    public Class<? extends Annotation> getAbductiveAnnotationClass() {
        throw new UnsupportedOperationException("Available only for abductive query");
    }

    public <T extends Annotation> Class<?> getAbductionClass(Function<Class<T>, T> annotationReader) {
        throw new UnsupportedOperationException("Available only for abductive query");
    }
}
