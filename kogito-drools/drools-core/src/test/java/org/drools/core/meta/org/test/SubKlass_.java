package org.drools.core.meta.org.test;

import org.drools.core.factmodel.traits.AbstractTraitFactory;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.metadata.Don;
import org.drools.core.metadata.DonLiteral;
import org.drools.core.metadata.MetaClass;
import org.drools.core.metadata.MetaProperty;
import org.drools.core.metadata.Metadatable;
import org.drools.core.metadata.NewInstance;
import org.drools.core.metadata.PropertyLiteral;
import org.drools.core.util.ClassUtils;

import java.net.URI;

public class SubKlass_<T extends SubKlass> extends Klass_<T>  {

    public SubKlass_( T x ) {
        super( x );
        metaClassInfo = SubKlass_Meta.getInstance();
    }

    public static final MetaProperty<SubKlass,Integer> subProp = SubKlass_Meta.subProp;


    public static <X extends SubKlass> SubKlass_NewInstance<X> newSubKlass( URI id ) {
        return SubKlass_Meta.getInstance().newInstance( id );
    }

    public static <X, K extends SubKlass> SubKlass_Don<X,K> donSubKlass( X core ) {
        return new SubKlass_Don<X,K>( core );
    }

    public static SubKlass_Modify<? extends SubKlass> modify( SubKlass x ) {
        return new SubKlass_Modify<SubKlass>( x );
    }

    public SubKlass_Modify<T> modify() {
        return new SubKlass_Modify<T>( getTarget() );
    }


    public static class SubKlass_NewInstance<T extends SubKlass> extends Klass_NewInstance<T> {

        public SubKlass_NewInstance( URI id ) {
            super( id );
        }

        protected T construct() {
            return (T) new SubKlassImpl();
        }

        public SubKlass_NewInstance<T> subProp( int newSubProp ) {
            getSetter().subProp( newSubProp );
            return this;
        }

        protected SubKlass_Modify<T> getSetter() {
            if ( setter == null ) {
                setter = new SubKlass_Modify( null );
            }
            return (SubKlass_Modify<T>) setter;
        }

        @Override
        public Class<T> getInstanceClass() {
            return SubKlass_Meta.getInstance().getTargetClass();
        }
    }

    public static class SubKlass_Modify<T extends SubKlass> extends  Klass_.Klass_Modify<T>  {
        public SubKlass_Modify( T x ) {
            super( x );
        }

        public SubKlass_Modify prop( String newVal ) {
            super.prop( newVal );
            return this;
        }

        public SubKlass_Modify subProp( Integer newVal ) {
            addTask( subProp, newVal );
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


    public static class SubKlass_Meta<T extends SubKlass> extends Klass_Meta<T> {

        private static SubKlass_Meta instance;

        public static SubKlass_Meta getInstance() {
            if ( instance == null ) {
                instance = new SubKlass_Meta( new MetaProperty[] {  prop, subProp  } );
            }
            return instance;
        }

        public static final MetaProperty<SubKlass,Integer> subProp =
                new PropertyLiteral<SubKlass,Integer>( 0, "subProp", URI.create( "http://www.test.org#SubKlass?subProp" ) ) {
                    public Integer get( SubKlass o ) { return o.getSubProp(); }
                    public void set( SubKlass o, Integer value ) { o.setSubProp( value ); }
                };


        protected SubKlass_Meta( MetaProperty<T, ?>[] propertyLiterals ) {
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

        public SubKlass_NewInstance<T> newInstance( URI id ) {
            return new SubKlass_NewInstance<T>( id );
        }

        @Override
        public Class<T> getTargetClass() {
            return (Class<T>) SubKlass.class;
        }
    }

    public static class SubKlass_Don<K, T extends SubKlass> extends Klass_Don<K,T> {
        public SubKlass_Don( K target ) {
            super( target );
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
    }

}

