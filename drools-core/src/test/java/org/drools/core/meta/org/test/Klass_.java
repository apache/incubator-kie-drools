package org.drools.core.meta.org.test;

import org.drools.core.metadata.MetaClass;
import org.drools.core.metadata.MetaProperty;
import org.drools.core.metadata.MetadataContainer;
import org.drools.core.metadata.PropertyLiteral;
import org.drools.core.util.ClassUtils;

import java.net.URI;

public class Klass_<T extends Klass> extends MetadataContainer<T> {

    public Klass_( T x ) {
        super( x );
        metaClassInfo = Klass_Meta.getInstance();
    }

    public static final MetaProperty<Klass,String> prop = Klass_Meta.prop;

    public static class Klass_Modify<T extends Klass> extends ModifyLiteral<T> {

        @Override
        protected MetaClass<T> getMetaClassInfo() {
            return Klass_Meta.getInstance();
        }

        public Klass_Modify( T x ) {
            super( x );
        }

        public Klass_Modify prop( String newVal ) {
            addTask( Klass_Meta.prop, newVal );
            return this;
        }

        public Class getModificationClass() {
            return Klass.class;
        }

    }

    public static Klass_Modify<? extends Klass> modify( Klass x ) {
        return new Klass_Modify<Klass>( x );
    }

    public Klass_Modify<T> modify() {
        return new Klass_Modify<T>( getTarget() );
    }


    protected static class Klass_Meta<T extends Klass> extends ClassLiteral<T> {

        private static Klass_Meta instance;

        public static Klass_Meta getInstance() {
            if ( instance == null ) {
                instance = new Klass_Meta( new MetaProperty[] {  prop  } );
            }
            return instance;
        }

        public static final MetaProperty<Klass,String> prop =
                new PropertyLiteral<Klass,String>( 0, "prop", URI.create( "http://www.test.org#Klass?prop" ) ) {
                    public String get( Klass o ) { return o.getProp(); }
                    public void set( Klass o, String value ) { o.setProp( value ); }
                };


        protected Klass_Meta( MetaProperty<T, ?>[] propertyLiterals ) {
            super( propertyLiterals );
        }

        @Override
        protected void cachePropertyNames() {
            propertyNames = ClassUtils.getSettableProperties( Klass.class );
        }

        @Override
        public URI getUri() {
            return URI.create( "http://www.test.org#Klass" );
        }

    }

}

