package org.drools.core.meta.org.test;

import org.drools.core.factmodel.traits.AbstractTraitFactory;
import org.drools.core.metadata.ClassLiteral;
import org.drools.core.metadata.DonLiteral;
import org.drools.core.metadata.MetaClass;
import org.drools.core.metadata.MetaProperty;
import org.drools.core.metadata.MetadataContainer;
import org.drools.core.metadata.MetadataHolder;
import org.drools.core.metadata.Metadatable;
import org.drools.core.metadata.ModifyLiteral;
import org.drools.core.metadata.NewInstanceLiteral;
import org.drools.core.metadata.PropertyLiteral;
import org.drools.core.util.ClassUtils;

import java.net.URI;

public class AnotherKlass_<T extends AnotherKlass> extends MetadataContainer<T> {

    public AnotherKlass_( T x ) {
        super( x );
        metaClassInfo = AnotherKlass_Meta.getInstance();
    }

    public static final MetaProperty<AnotherKlass,Integer> num = AnotherKlass_Meta.num;

    public static <T extends AnotherKlass> AnotherKlass_NewInstance<T> newAnotherKlass( Object id ) {
        return AnotherKlass_Meta.getInstance().newInstance( id );
    }

    public static AnotherKlass_Modify<? extends AnotherKlass> modify( AnotherKlass x ) {
        return new AnotherKlass_Modify<AnotherKlass>( x );
    }

    public AnotherKlass_Modify<T> modify() {
        return new AnotherKlass_Modify<T>( getTarget() );
    }

    public static <X, K extends AnotherKlass> AnotherKlass_Don<X,K> donAnotherKlass( X core ) {
        return new AnotherKlass_Don<X,K>( core );
    }


    public static class AnotherKlass_NewInstance<T extends AnotherKlass> extends NewInstanceLiteral<T> {

        public AnotherKlass_NewInstance( Object id ) {
            super( id );
        }

        protected T construct() {
            return (T) new AnotherKlassImpl();
        }

        public AnotherKlass_NewInstance<T> num( int newSubProp ) {
            getSetter().num( newSubProp );
            return this;
        }

        protected AnotherKlass_Modify<T> getSetter() {
            if ( setter == null ) {
                setter = new AnotherKlass_Modify( null );
            }
            return (AnotherKlass_Modify<T>) setter;
        }

        @Override
        public Class<T> getInstanceClass() {
            return AnotherKlass_Meta.getInstance().getTargetClass();
        }
    }

    public static class AnotherKlass_Modify<T extends AnotherKlass> extends ModifyLiteral<T> {
        public AnotherKlass_Modify( T x ) {
            super( x );
        }

        public AnotherKlass_Modify num( int newVal ) {
            addTask( num, newVal );
            return this;
        }

        @Override
        protected MetaClass<T> getMetaClassInfo() {
            return AnotherKlass_Meta.getInstance();
        }

        public Class getModificationClass() {
            return AnotherKlass.class;
        }

    }

    protected static class AnotherKlass_Meta<T extends AnotherKlass> extends ClassLiteral<T> {

        private static AnotherKlass_Meta instance;

        public static AnotherKlass_Meta getInstance() {
            if ( instance == null ) {
                instance = new AnotherKlass_Meta( new MetaProperty[] {  num  } );
            }
            return instance;
        }

        public static final MetaProperty<AnotherKlass,Integer> num =
                new PropertyLiteral<AnotherKlass,Integer>( 0, "num", URI.create( "http://www.test.org#AnotherKlass?num" ) ) {
                    public Integer get( AnotherKlass o ) { return o.getNum(); }
                    public void set( AnotherKlass o, Integer value ) { o.setNum( value ); }
                };


        protected AnotherKlass_Meta( MetaProperty<T, ?>[] propertyLiterals ) {
            super( propertyLiterals );
        }

        @Override
        protected void cachePropertyNames() {
            propertyNames = ClassUtils.getSettableProperties( AnotherKlass.class );
        }

        public AnotherKlass_NewInstance<T> newInstance( Object id ) {
            return new AnotherKlass_NewInstance<T>( id );
        }

        @Override
        public URI getUri() {
            if ( key == null ) {
                key = URI.create( "http://www.test.org#AnotherKlass" );
            }
            return key;
        }

        @Override
        public Object getId() {
            return getUri();
        }

        @Override
        public Class<T> getTargetClass() {
            return (Class<T>) AnotherKlass.class;
        }
    }

    public static class AnotherKlass_Don<K, T extends AnotherKlass> extends DonLiteral<K,T> {
        public AnotherKlass_Don( K target ) {
            super( target );
        }

        @Override
        protected MetaClass<T> getMetaClassInfo() {
            return AnotherKlass_Meta.getInstance();
        }

        @Override
        public Class<T> getTrait() {
            return AnotherKlass_Meta.getInstance().getTargetClass();
        }

        @Override
        public AnotherKlass_Don<K,T> setTraitFactory( AbstractTraitFactory factory ) {
            super.setTraitFactory( factory );
            return this;
        }

    }


}

