package org.drools.core.meta.org.test;

import org.drools.core.metadata.MetaClass;
import org.drools.core.metadata.MetaProperty;
import org.drools.core.metadata.MetadataContainer;
import org.drools.core.metadata.MetadataHolder;
import org.drools.core.metadata.PropertyLiteral;
import org.drools.core.util.ClassUtils;

import java.net.URI;

public class AnotherKlass_<T extends AnotherKlass> extends MetadataContainer<T> {

    public AnotherKlass_( T x ) {
        super( x );
        metaClassInfo = AnotherKlass_Meta.getInstance();
    }

    public static final MetaProperty<AnotherKlass,Integer> num = AnotherKlass_Meta.num;

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

    public static AnotherKlass_Modify<? extends AnotherKlass> modify( AnotherKlass x ) {
        return new AnotherKlass_Modify<AnotherKlass>( x );
    }

    public AnotherKlass_Modify<T> modify() {
        return new AnotherKlass_Modify<T>( getTarget() );
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

        @Override
        public URI getUri() {
            return URI.create( "http://www.test.org#AnotherKlass" );
        }
    }
}

