package org.drools.core.meta.org.test;

import org.drools.core.factmodel.traits.AbstractTraitFactory;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.metadata.ClassLiteral;
import org.drools.core.metadata.Don;
import org.drools.core.metadata.DonLiteral;
import org.drools.core.metadata.Identifiable;
import org.drools.core.metadata.MetaClass;
import org.drools.core.metadata.MetaProperty;
import org.drools.core.metadata.MetadataContainer;
import org.drools.core.metadata.Metadatable;
import org.drools.core.metadata.ModifyLiteral;
import org.drools.core.metadata.NewInstance;
import org.drools.core.metadata.NewInstanceLiteral;
import org.drools.core.metadata.PropertyLiteral;
import org.drools.core.util.ClassUtils;

import java.net.URI;


public class Klass_<T extends Klass> extends MetadataContainer<T> {

    public Klass_( T x ) {
        super( x );
        metaClassInfo = Klass_Meta.getInstance();
    }

    public static final MetaProperty<Klass,String> prop = Klass_Meta.prop;


    public static <X extends Klass> Klass_NewInstance<X> newKlass( Object id ) {
        return Klass_Meta.getInstance().newInstance( id );
    }

    public static <X, K extends Klass> Klass_Don<X,K> donKlass( X core ) {
        return new Klass_Don<X,K>( core );
    }

    public static Klass_Modify<? extends Klass> modify( Klass x ) {
        return new Klass_Modify<Klass>( x );
    }

    public Klass_Modify<T> modify() {
        return new Klass_Modify<T>( getTarget() );
    }




    public static class Klass_NewInstance<T extends Klass> extends NewInstanceLiteral<T> implements NewInstance<T> {

        public Klass_NewInstance( Object id ) {
            super( URI.create( id.toString() ) );
        }

        protected T construct() {
            return (T) new KlassImpl( getId().toString() );
        }

        public Klass_NewInstance<T> prop( String newProp ) {
            getSetter().prop( newProp );
            return this;
        }

        protected Klass_Modify<T> getSetter() {
            if ( setter == null ) {
                setter = new Klass_Modify( null );
            }
            return (Klass_Modify<T>) setter;
        }

        @Override
        public Class<T> getInstanceClass() {
            return Klass_Meta.getInstance().getTargetClass();
        }
    }

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

    public static class Klass_Meta<T extends Klass> extends ClassLiteral<T> {

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
            if ( key == null ) {
                key = URI.create( "http://www.test.org#Klass"  );
            }
            return key;
        }

        @Override
        public Object getId() {
            return getUri();
        }

        @Override
        public Class<T> getTargetClass() {
            return (Class<T>) Klass.class;
        }

        public Klass_NewInstance<T> newInstance( Object id ) {
            return new Klass_NewInstance<T>( id );
        }

    }

    public static class Klass_Don<K, T extends Klass> extends DonLiteral<K,T> {
        public Klass_Don( K target ) {
            super( target );
        }

        @Override
        protected MetaClass<T> getMetaClassInfo() {
            return Klass_Meta.getInstance();
        }

        @Override
        public Class<T> getTrait() {
            return Klass_Meta.getInstance().getTargetClass();
        }

        @Override
        public Klass_Don<K,T> setTraitFactory( AbstractTraitFactory factory ) {
            super.setTraitFactory( factory );
            return this;
        }
    }
}

