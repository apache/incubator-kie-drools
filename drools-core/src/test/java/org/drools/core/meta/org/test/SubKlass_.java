package org.drools.core.meta.org.test;

import org.drools.core.metadata.MetaClass;
import org.drools.core.metadata.MetaProperty;
import org.drools.core.metadata.PropertyLiteral;
import org.drools.core.util.ClassUtils;

import java.net.URI;

public class SubKlass_<T extends SubKlass> extends Klass_<T>  {

    public SubKlass_( T x ) {
        super( x );
        metaClassInfo = SubKlass_Meta.getInstance();
    }

    public static final MetaProperty<SubKlass,Integer> subProp = SubKlass_Meta.subProp;

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

    public static SubKlass_Modify<? extends SubKlass> modify( SubKlass x ) {
        return new SubKlass_Modify<SubKlass>( x );
    }


    public SubKlass_Modify<T> modify() {
        return new SubKlass_Modify<T>( getTarget() );
    }


    protected static class SubKlass_Meta<T extends SubKlass> extends Klass_Meta<T> {

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
            return URI.create( "http://www.test.org#SubKlass" );
        }

    }
}

