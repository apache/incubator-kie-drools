/*
 * Copyright 2015 JBoss Inc
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

package org.drools.core.meta.org.test;

import org.drools.core.factmodel.traits.AbstractTraitFactory;
import org.drools.core.metadata.ClassLiteral;
import org.drools.core.metadata.DonLiteral;
import org.drools.core.metadata.Lit;
import org.drools.core.metadata.ManyToManyPropertyLiteral;
import org.drools.core.metadata.ManyValuedMetaProperty;
import org.drools.core.metadata.MetaClass;
import org.drools.core.metadata.MetaProperty;
import org.drools.core.metadata.MetadataContainer;
import org.drools.core.metadata.ModifyLiteral;
import org.drools.core.metadata.NewInstanceLiteral;
import org.drools.core.metadata.OneToManyPropertyLiteral;
import org.drools.core.metadata.OneToOnePropertyLiteral;
import org.drools.core.metadata.OneToOneValuedMetaProperty;
import org.drools.core.metadata.OneValuedMetaProperty;
import org.drools.core.metadata.ToOnePropertyLiteral;
import org.drools.core.metadata.With;
import org.drools.core.util.ClassUtils;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class AnotherKlass_<T extends AnotherKlass> extends MetadataContainer<T> implements Serializable {

    public AnotherKlass_( T x ) {
        super( x );
        metaClassInfo = AnotherKlass_Meta.getInstance();
    }

    public static final OneValuedMetaProperty<AnotherKlass,Integer> num = AnotherKlass_Meta.num;

    public static final OneValuedMetaProperty<AnotherKlass,Klass> theKlass = AnotherKlass_Meta.theKlass;

    public static final ManyValuedMetaProperty<AnotherKlass,Klass,List<Klass>> manyKlasses = AnotherKlass_Meta.manyKlasses;

    public static final ManyValuedMetaProperty<AnotherKlass,Klass,List<Klass>> manyMoreKlasses = AnotherKlass_Meta.manyMoreKlasses;



    public static <T extends AnotherKlass> AnotherKlass_NewInstance<T> newAnotherKlass( Object id, With... args ) {
        return AnotherKlass_Meta.getInstance().newInstance( id, args );
    }
    public static <T extends AnotherKlass> AnotherKlass_NewInstance<T> newAnotherKlass( With... args ) {
        return AnotherKlass_Meta.getInstance().newInstance( args );
    }

    public static AnotherKlass_Modify<? extends AnotherKlass> modify( AnotherKlass x, With... args ) {
        return new AnotherKlass_Modify<AnotherKlass>( x, args );
    }

    public AnotherKlass_Modify<T> modify( With... args ) {
        return new AnotherKlass_Modify<T>( getTarget(), args );
    }

    public static <X, K extends AnotherKlass> AnotherKlass_Don<X,K> donAnotherKlass( X core, With... args ) {
        return new AnotherKlass_Don<X,K>( core, args );
    }


    public static class AnotherKlass_NewInstance<T extends AnotherKlass> extends NewInstanceLiteral<T> implements Serializable {

        public AnotherKlass_NewInstance( Object id, With... args ) {
            super( id, args );
        }
        public AnotherKlass_NewInstance(  With... args ) {
            super( args );
        }

        protected T construct() {
            return (T) new AnotherKlassImpl();
        }

        public AnotherKlass_NewInstance<T> num( int val ) {
            getSetter().num( val );
            return this;
        }

        public AnotherKlass_NewInstance<T> manyKlasses( List<Klass> val, Lit mode ) {
            getSetter().manyKlasses( val, mode );
            return this;
        }

        public AnotherKlass_NewInstance<T> manyKlasses( Klass val, Lit mode ) {
            getSetter().manyKlasses( val, mode );
            return this;
        }

        public AnotherKlass_NewInstance<T> manyMoreKlasses( List<Klass> val, Lit mode ) {
            getSetter().manyMoreKlasses( val, mode );
            return this;
        }

        public AnotherKlass_NewInstance<T> manyMoreKlasses( Klass val, Lit mode ) {
            getSetter().manyMoreKlasses( val, mode );
            return this;
        }

        public AnotherKlass_NewInstance<T> theKlass( Klass val ) {
            getSetter().theKlass( val );
            return this;
        }

        protected AnotherKlass_Modify<T> getSetter() {
            if ( setter == null ) {
                setter = new AnotherKlass_Modify( null, this.with );
            }
            return (AnotherKlass_Modify<T>) setter;
        }

        @Override
        public Class<T> getInstanceClass() {
            return AnotherKlass_Meta.getInstance().getTargetClass();
        }
    }

    public static class AnotherKlass_Modify<T extends AnotherKlass> extends ModifyLiteral<T> implements Serializable {
        public AnotherKlass_Modify( T x, With... args ) {
            super( x, args );
        }

        public AnotherKlass_Modify num( int newVal ) {
            addTask( num, newVal );
            return this;
        }

        public AnotherKlass_Modify theKlass( Klass newVal ) {
            addTask( theKlass, newVal, newVal != null ? Lit.SET : Lit.REMOVE );
            return this;
        }


        public AnotherKlass_Modify<T> manyKlasses( List<Klass> val, Lit mode ) {
            addTask( manyKlasses, val, mode );
            return this;
        }

        public AnotherKlass_Modify<T> manyKlasses( Klass val, Lit mode ) {
            addTask( manyKlasses, Collections.singletonList( val ), mode );
            return this;
        }


        public AnotherKlass_Modify<T> manyMoreKlasses( List<Klass> val, Lit mode ) {
            addTask( manyMoreKlasses, val, mode );
            return this;
        }

        public AnotherKlass_Modify<T> manyMoreKlasses( Klass val, Lit mode ) {
            addTask( manyMoreKlasses, Collections.singletonList( val ), mode );
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

    protected static class AnotherKlass_Meta<T extends AnotherKlass> extends ClassLiteral<T> implements Serializable {

        private static AnotherKlass_Meta instance;

        public static AnotherKlass_Meta getInstance() {
            if ( instance == null ) {
                instance = new AnotherKlass_Meta( new MetaProperty[] {  num, theKlass, manyKlasses, manyMoreKlasses } );
            }
            return instance;
        }

        public static final OneValuedMetaProperty<AnotherKlass,Integer> num =
                new ToOnePropertyLiteral<AnotherKlass,Integer>( 0, "num", URI.create( "http://www.test.org#AnotherKlass?num" ) ) {
                    public Integer get( AnotherKlass o ) { return o.getNum(); }
                    public void set( AnotherKlass o, Integer value ) { o.setNum( value ); }
                    public boolean isDatatype() { return true; }
                };

        public static final OneValuedMetaProperty<AnotherKlass,Klass> theKlass =
                new OneToOnePropertyLiteral<AnotherKlass, Klass>( 1, "theKlass", URI.create( "http://www.test.org#AnotherKlass?theKlass" ) ) {
                    public Klass get( AnotherKlass o ) { return o.getTheKlass(); }
                    public void set( AnotherKlass o, Klass value ) { o.setTheKlass( value ); }

                    @Override
                    public OneValuedMetaProperty<Klass, AnotherKlass> getInverse() {
                        return Klass_.another;
                    }
                    public boolean isDatatype() { return false; }
                };

        public static final ManyValuedMetaProperty<AnotherKlass,Klass,List<Klass>> manyKlasses =
                new OneToManyPropertyLiteral<AnotherKlass,Klass>( 2, "manyKlasses", URI.create( "http://www.test.org#AnotherKlass?manyKlasses" ) ) {
                    public List<Klass> get( AnotherKlass o ) { return o.getManyKlasses(); }
                    public void set( AnotherKlass o, List<Klass> value ) { o.setManyKlasses( value ); }

                    @Override
                    public OneValuedMetaProperty<Klass, AnotherKlass> getInverse() {
                        return Klass_.oneAnother;
                    }
                    public boolean isDatatype() { return false; }
                };

        public static final ManyValuedMetaProperty<AnotherKlass,Klass,List<Klass>> manyMoreKlasses =
                new ManyToManyPropertyLiteral<AnotherKlass,Klass>( 3, "manyMoreKlasses", URI.create( "http://www.test.org#AnotherKlass?manyMoreKlasses" ) ) {
                    public List<Klass> get( AnotherKlass o ) { return o.getManyMoreKlasses(); }
                    public void set( AnotherKlass o, List<Klass> value ) { o.setManyMoreKlasses( value ); }

                    @Override
                    public ManyValuedMetaProperty<Klass,AnotherKlass,List<AnotherKlass>> getInverse() {
                        return Klass_.manyOthers;
                    }
                    public boolean isDatatype() { return false; }
                };


        protected AnotherKlass_Meta( MetaProperty<T,?,?>[] propertyLiterals ) {
            super( propertyLiterals );
        }

        @Override
        protected void cachePropertyNames() {
            propertyNames = ClassUtils.getSettableProperties( AnotherKlass.class );
        }

        public AnotherKlass_NewInstance<T> newInstance( Object id, With... args ) {
            return new AnotherKlass_NewInstance<T>( id, args );
        }
        public AnotherKlass_NewInstance<T> newInstance( With... args ) {
            return new AnotherKlass_NewInstance<T>( args );
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

    public static class AnotherKlass_Don<K, T extends AnotherKlass> extends DonLiteral<K,T> implements Serializable {
        public AnotherKlass_Don( K target, With... args ) {
            super( target, args );
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

        public AnotherKlass_Don<K,T> num( int newVal ) {
            getSetter().num( newVal );
            return this;
        }

        public AnotherKlass_Don<K,T> num( Klass newVal ) {
            getSetter().theKlass( newVal );
            return this;
        }

        public AnotherKlass_Don<K,T> manyKlasses( List<Klass> val, Lit mode ) {
            getSetter().manyKlasses( val, mode );
            return this;
        }

        public AnotherKlass_Don<K,T> manyKlasses( Klass val, Lit mode ) {
            getSetter().manyKlasses( val, mode );
            return this;
        }

        public AnotherKlass_Don<K,T> manyMoreKlasses( List<Klass> val, Lit mode ) {
            getSetter().manyMoreKlasses( val, mode );
            return this;
        }

        public AnotherKlass_Don<K,T> manyMoreKlasses( Klass val, Lit mode ) {
            getSetter().manyMoreKlasses( val, mode );
            return this;
        }

        protected AnotherKlass_Modify<T> getSetter() {
            if ( setter == null ) {
                setter = new AnotherKlass_Modify( null, this.with );
            }
            return (AnotherKlass_Modify<T>) setter;
        }

    }


}

