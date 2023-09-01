package org.drools.modelcompiler.constraints;

import java.util.Objects;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;
import org.drools.base.base.extractors.BaseObjectClassFieldReader;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.model.functions.Function1;

public class LambdaReadAccessor extends BaseObjectClassFieldReader implements ReadAccessor {

    private final Function1 lambda;

    public LambdaReadAccessor( Class<?> fieldType, Function1 lambda ) {
        this(0, fieldType, lambda);
    }

    public LambdaReadAccessor( int index, Class<?> fieldType, Function1 lambda ) {
        super(index, fieldType, ValueType.determineValueType( fieldType ));
        this.lambda = lambda;
    }

    @Override
    public Object getValue(ValueResolver valueResolver, Object object) {
        return lambda.apply( object );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        if ( !super.equals( o ) ) return false;
        LambdaReadAccessor that = ( LambdaReadAccessor ) o;
        return Objects.equals( lambda, that.lambda );
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lambda);
    }
}
