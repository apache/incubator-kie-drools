package org.drools.core.metadata;

import org.drools.core.factmodel.traits.InstantiatorFactory;

import java.net.URI;

public abstract class NewInstanceLiteral<T extends Metadatable> extends AbstractWMTask<T> implements NewInstance<T> {
    protected URI uri;

    protected ModifyLiteral setter;
    protected InstantiatorFactory instantiatorFactory;

    protected With[] with;

    public NewInstanceLiteral( Object identifier, With... args ) {
        this.uri = URI.create( identifier.toString() );
        this.with = args;
    }

    public boolean isInterface() {
        return false;
    }

    @Override
    public KIND kind() {
        return KIND.ASSERT;
    }

    @Override
    public Object getTargetId() {
        return uri;
    }

    @Override
    public Object callUntyped() {
        return construct();
    }

    @Override
    public Modify getInitArgs() {
        return setter;
    }

    @Override
    public T call() {
        T x = (T) construct();
        if ( setter != null ) {
            setter.setTarget( x );
            setter.call();
        }
        return x;
    }

    protected abstract Object construct();

    public InstantiatorFactory getInstantiatorFactory() {
        return instantiatorFactory;
    }

    public NewInstance<T> setInstantiatorFactory( InstantiatorFactory instantiatorFactory ) {
        this.instantiatorFactory = instantiatorFactory;
        return this;
    }

    @Override
    public URI getUri() {
        return URI.create( uri.toString() + "?create" );
    }

    @Override
    public Object getId() {
        return uri;
    }

}

