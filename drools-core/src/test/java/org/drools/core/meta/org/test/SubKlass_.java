package org.drools.core.meta.org.test;

import org.drools.core.factmodel.traits.AbstractTraitFactory;
import org.drools.core.metadata.Lit;
import org.drools.core.metadata.ManyValuedMetaProperty;
import org.drools.core.metadata.MetaClass;
import org.drools.core.metadata.MetaProperty;
import org.drools.core.metadata.OneValuedMetaProperty;
import org.drools.core.metadata.ToManyPropertyLiteral;
import org.drools.core.metadata.ToOnePropertyLiteral;
import org.drools.core.metadata.With;
import org.drools.core.util.ClassUtils;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class SubKlass_<T extends SubKlass> extends Klass_<T> implements Serializable {

    public SubKlass_( T x ) {
        super( x );
        metaClassInfo = SubKlass_Meta.getInstance();
    }

    public static final OneValuedMetaProperty<SubKlass,Integer> subProp = SubKlass_Meta.subProp;

    public static final ManyValuedMetaProperty<SubKlass,AnotherKlass,List<AnotherKlass>> links = SubKlass_Meta.links;


    public static <X extends SubKlass> SubKlass_NewInstance<X> newSubKlass( URI id, With... args ) {
        return SubKlass_Meta.getInstance().newInstance( id, args );
    }

    public static <X, K extends SubKlass> SubKlass_Don<X,K> donSubKlass( X core, With... args ) {
        return new SubKlass_Don<X,K>( core, args );
    }

    public static SubKlass_Modify<? extends SubKlass> modify( SubKlass x, With... args ) {
        return new SubKlass_Modify<SubKlass>( x, args );
    }

    public SubKlass_Modify<T> modify( With... args ) {
        return new SubKlass_Modify<T>( getTarget(), args );
    }


    public static class SubKlass_NewInstance<T extends SubKlass> extends Klass_NewInstance<T> {

        public SubKlass_NewInstance( URI id, With... args ) {
            super( id, args );
        }

        protected T construct() {
            return (T) new SubKlassImpl();
        }

        public SubKlass_NewInstance<T> subProp( int newSubProp ) {
            getSetter().subProp( newSubProp );
            return this;
        }

        public SubKlass_NewInstance<T> links( List<AnotherKlass> newLinks, Lit mode ) {
            getSetter().links( newLinks, mode );
            return this;
        }

        public SubKlass_NewInstance<T> links( AnotherKlass newLink, Lit mode ) {
            getSetter().links( newLink, mode );
            return this;
        }

        public SubKlass_NewInstance<T> prop( String newProp ) {
            getSetter().prop( newProp );
            return this;
        }

        public SubKlass_NewInstance<T> another( AnotherKlass newAnother ) {
            getSetter().another( newAnother );
            return this;
        }

        protected SubKlass_Modify<T> getSetter() {
            if ( setter == null ) {
                setter = new SubKlass_Modify( null, this.with );
            }
            return (SubKlass_Modify<T>) setter;
        }

        @Override
        public Class<T> getInstanceClass() {
            return SubKlass_Meta.getInstance().getTargetClass();
        }
    }

    public static class SubKlass_Modify<T extends SubKlass> extends  Klass_.Klass_Modify<T> implements Serializable {
        public SubKlass_Modify( T x, With... args ) {
            super( x, args );
        }

        public SubKlass_Modify<T> prop( String newVal ) {
            super.prop( newVal );
            return this;
        }

        public SubKlass_Modify<T> another( AnotherKlass newVal ) {
            super.another( newVal );
            return this;
        }

        public SubKlass_Modify<T> subProp( Integer newVal ) {
            addTask( subProp, newVal );
            return this;
        }

        public SubKlass_Modify<T> links( List<AnotherKlass> newLinks, Lit mode ) {
            addTask( links, newLinks, mode );
            return this;
        }

        public SubKlass_Modify<T> links( AnotherKlass newLink, Lit mode ) {
            addTask( links, Collections.singletonList( newLink ), mode );
            return this;
        }


        @Override
        protected MetaClass<T> getMetaClassInfo() {
            return SubKlass_Meta.getInstance();
        }

        public Class getModificationClass() {
            return SubKlass.class;
        }
    }


    public static class SubKlass_Meta<T extends SubKlass> extends Klass_Meta<T> implements Serializable {

        private static SubKlass_Meta instance;

        public static SubKlass_Meta getInstance() {
            if ( instance == null ) {
                instance = new SubKlass_Meta( new MetaProperty[] {  prop, another, subProp, links  } );
            }
            return instance;
        }

        public static final OneValuedMetaProperty<SubKlass,Integer> subProp =
                new ToOnePropertyLiteral<SubKlass,Integer>( 2, "subProp", URI.create( "http://www.test.org#SubKlass?subProp" ) ) {
                    public Integer get( SubKlass o ) { return o.getSubProp(); }
                    public void set( SubKlass o, Integer value ) { o.setSubProp( value ); }
                };

        public static final ManyValuedMetaProperty<SubKlass,AnotherKlass,List<AnotherKlass>> links =
                new ToManyPropertyLiteral<SubKlass,AnotherKlass>( 3, "links", URI.create( "http://www.test.org#SubKlass?links" ) ) {
                    public List<AnotherKlass> get( SubKlass o ) { return o.getLinks(); }
                    public void set( SubKlass o, List<AnotherKlass> value ) { o.setLinks( value ); }
                };


        protected SubKlass_Meta( MetaProperty<T,?,?>[] propertyLiterals ) {
            super( propertyLiterals );
        }

        @Override
        protected void cachePropertyNames() {
            propertyNames = ClassUtils.getSettableProperties( SubKlass.class );
        }

        @Override
        public URI getUri() {
            if ( key == null ) {
                key = URI.create( "http://www.test.org#SubKlass"  );
            }
            return key;
        }

        @Override
        public Object getId() {
            return getUri();
        }

        public SubKlass_NewInstance<T> newInstance( URI id, With... args ) {
            return new SubKlass_NewInstance<T>( id, args );
        }

        @Override
        public Class<T> getTargetClass() {
            return (Class<T>) SubKlass.class;
        }
    }

    public static class SubKlass_Don<K, T extends SubKlass> extends Klass_Don<K,T> implements Serializable {
        public SubKlass_Don( K target, With... args ) {
            super( target, args );
        }

        @Override
        protected MetaClass<T> getMetaClassInfo() {
            return SubKlass_Meta.getInstance();
        }

        @Override
        public Class<T> getTrait() {
            return SubKlass_Meta.getInstance().getTargetClass();
        }

        @Override
        public SubKlass_Don<K,T> setTraitFactory( AbstractTraitFactory factory ) {
            super.setTraitFactory( factory );
            return this;
        }

        public SubKlass_Don<K,T> prop( String newVal ) {
            super.prop( newVal );
            return this;
        }

        public SubKlass_Don<K,T> another( AnotherKlass newVal ) {
            super.another( newVal );
            return this;
        }

        protected SubKlass_Modify<T> getSetter() {
            if ( setter == null ) {
                setter = new SubKlass_Modify( null, this.with );
            }
            return (SubKlass_Modify<T>) setter;
        }

        public SubKlass_Don<K,T> subProp( Integer newVal ) {
            getSetter().subProp( newVal );
            return this;
        }

        public SubKlass_Don<K,T> links( List<AnotherKlass> newLinks, Lit mode ) {
            getSetter().links( newLinks, mode );
            return this;
        }

        public SubKlass_Don<K,T> links( AnotherKlass newLink, Lit mode ) {
            getSetter().links( newLink, mode );
            return this;
        }
    }

}

