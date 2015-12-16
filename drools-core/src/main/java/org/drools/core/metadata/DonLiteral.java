/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.metadata;

import org.drools.core.factmodel.traits.AbstractTraitFactory;
import org.drools.core.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.util.BitMaskUtil;
import org.drools.core.util.StandaloneTraitFactory;

import java.net.URI;

public abstract class DonLiteral<K, T extends Metadatable> extends AbstractWMTask<T> implements Don<K,T> {
    protected K core;
    private URI key;
    protected AbstractTraitFactory factory;
    protected ModifyLiteral setter;
    protected With[] with;

    protected abstract MetaClass<T> getMetaClassInfo();

    public DonLiteral( K target, With... with ) {
        this.core = target;
        this.with = with;
    }

    public K getCore() {
        return core;
    }

    void setCore( K target ) {
        this.core = target;
    }

    @Override
    public KIND kind() {
        return KIND.DON;
    }

    @Override
    public Object getTargetId() {
        return MetadataContainer.getIdentifier( core );
    }

    public T call() {
        if ( getCore() == null ) {
            return null;
        }
        if ( factory != null && factory instanceof StandaloneTraitFactory ) {
            StandaloneTraitFactory stf = (StandaloneTraitFactory) factory;
            try {
                T x = (T) stf.don( getCore() instanceof TraitableBean ? ((TraitableBean) getCore()) : stf.makeTraitable( getCore(), getCore().getClass() ),
                                    getTrait() != null ? getTrait() : Thing.class );
                if ( setter != null ) {
                    setter.call( x );
                }
                return x;
            } catch ( LogicalTypeInconsistencyException e ) {
                e.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException(  );
        }
        return null;
    }

    public Modify getInitArgs() {
        return setter;
    }

    @Override
    public URI getUri() {
        if ( key == null ) {
            key = createURI();
        }
        return key;
    }

    @Override
    public Object getId() {
        return getUri();
    }

    protected URI createURI() {
        StringBuilder sb = new StringBuilder();
        sb.append( MetadataContainer.getIdentifier( core ) );

        sb.append( "?don=" ).append( getTrait().getName() );

        return URI.create( sb.toString() );
    }

    public static URI createURI( String coreId, Class type ) {
        StringBuilder sb = new StringBuilder();
        sb.append( coreId );

        sb.append( "?don=" ).append( type.getName() );

        return URI.create( sb.toString() );
    }

    @Override
    public Don<K,T> setTraitFactory( AbstractTraitFactory factory ) {
        this.factory = factory;
        return this;
    }

    @Override
    public ModifyLiteral<T> getSetters() {
        return setter;
    }

    @Override
    public Object getTarget() {
        return core;
    }
}
