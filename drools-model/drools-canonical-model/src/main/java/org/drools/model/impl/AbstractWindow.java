package org.drools.model.impl;

import java.util.concurrent.TimeUnit;

import org.drools.model.WindowDefinition;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public abstract class AbstractWindow implements WindowDefinition, ModelComponent {

    private final Type type;
    private final long value;

    public AbstractWindow( Type type, long value ) {
        this(type, value, null);
    }

    public AbstractWindow( Type type, long value, TimeUnit timeUnit ) {
        this.type = type;
        this.value = unitToLong( value, timeUnit );
    }

    @Override
    public long getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof AbstractWindow) ) return false;

        AbstractWindow that = ( AbstractWindow ) o;
        return type == that.type && value == that.value;
    }
}
