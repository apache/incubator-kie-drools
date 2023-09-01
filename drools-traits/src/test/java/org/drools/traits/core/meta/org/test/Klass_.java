/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.traits.core.meta.org.test;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.drools.base.util.PropertyReactivityUtil;
import org.drools.traits.core.factmodel.AbstractTraitFactory;
import org.drools.traits.core.metadata.ClassLiteral;
import org.drools.traits.core.metadata.DonLiteral;
import org.drools.traits.core.metadata.Lit;
import org.drools.traits.core.metadata.ManyToManyPropertyLiteral;
import org.drools.traits.core.metadata.ManyToOnePropertyLiteral;
import org.drools.traits.core.metadata.ManyValuedMetaProperty;
import org.drools.traits.core.metadata.MetaClass;
import org.drools.traits.core.metadata.MetaProperty;
import org.drools.traits.core.metadata.MetadataContainer;
import org.drools.traits.core.metadata.ModifyLiteral;
import org.drools.traits.core.metadata.NewInstance;
import org.drools.traits.core.metadata.NewInstanceLiteral;
import org.drools.traits.core.metadata.OneToOnePropertyLiteral;
import org.drools.traits.core.metadata.OneValuedMetaProperty;
import org.drools.traits.core.metadata.ToOnePropertyLiteral;
import org.drools.traits.core.metadata.With;


public class Klass_<T extends Klass> extends MetadataContainer<T> implements Serializable {

    public Klass_( T x ) {
        super( x );
        metaClassInfo = Klass_Meta.getInstance();
    }

    public static final OneValuedMetaProperty<Klass,String> prop = Klass_Meta.prop;

    public static final OneValuedMetaProperty<Klass,AnotherKlass> another = Klass_Meta.another;

    public static final OneValuedMetaProperty<Klass,AnotherKlass> oneAnother = Klass_Meta.oneAnother;

    public static final ManyValuedMetaProperty<Klass,AnotherKlass,List<AnotherKlass>> manyOthers = Klass_Meta.manyAnothers;

    public static <X extends Klass> Klass_NewInstance<X> newKlass( Object id, With... args ) {
        return Klass_Meta.getInstance().newInstance( id, args );
    }
    public static <X extends Klass> Klass_NewInstance<X> newKlass( With... args ) {
        return Klass_Meta.getInstance().newInstance( args );
    }

    public static <X, K extends Klass> Klass_Don<X,K> donKlass( X core, With... args ) {
        return new Klass_Don<X,K>( core, args );
    }

    public static Klass_Modify<? extends Klass> modify( Klass x, With... args ) {
        return new Klass_Modify<Klass>( x, args );
    }

    public Klass_Modify<T> modify( With... args ) {
        return new Klass_Modify<T>( getTarget(), args );
    }




    public static class Klass_NewInstance<T extends Klass> extends NewInstanceLiteral<T> implements NewInstance<T> {

        public Klass_NewInstance( Object id, With... args ) {
            super( URI.create( id.toString() ), args );
        }

        protected T construct() {
            return (T) new KlassImpl( getId().toString() );
        }

        public Klass_NewInstance<T> prop( String newProp ) {
            getSetter().prop( newProp );
            return this;
        }

        public Klass_NewInstance<T> another( AnotherKlass newAnother ) {
            getSetter().another( newAnother );
            return this;
        }

        public Klass_NewInstance<T> manyOthers( List<AnotherKlass> val, Lit mode ) {
            getSetter().manyOthers( val, mode );
            return this;
        }

        public Klass_NewInstance<T> manyOthers( AnotherKlass val, Lit mode ) {
            getSetter().manyOthers( val, mode );
            return this;
        }


        protected Klass_Modify<T> getSetter() {
            if ( setter == null ) {
                setter = new Klass_Modify( null, this.with );
            }
            return (Klass_Modify<T>) setter;
        }
    }

    public static class Klass_Modify<T extends Klass> extends ModifyLiteral<T> implements Serializable {

        @Override
        protected MetaClass<T> getMetaClassInfo() {
            return Klass_Meta.getInstance();
        }

        public Klass_Modify( T x, With... args ) {
            super( x, args );
        }

        public Klass_Modify prop( String newVal ) {
            addTask( Klass_Meta.prop, newVal, newVal != null ? Lit.SET : Lit.REMOVE );
            return this;
        }

        public Klass_Modify another( AnotherKlass newVal ) {
            addTask( Klass_Meta.another, newVal, newVal != null ? Lit.SET : Lit.REMOVE );
            return this;
        }

        public Klass_Modify oneAnother( AnotherKlass newVal ) {
            addTask( Klass_Meta.oneAnother, newVal, newVal != null ? Lit.SET : Lit.REMOVE );
            return this;
        }

        public Klass_Modify<T> manyOthers( List<AnotherKlass> val, Lit mode ) {
            addTask( Klass_Meta.manyAnothers, val, mode );
            return this;
        }

        public Klass_Modify<T> manyOthers( AnotherKlass val, Lit mode ) {
            addTask( Klass_Meta.manyAnothers, Collections.singletonList( val ), mode );
            return this;
        }


        public Class getModificationClass() {
            return Klass.class;
        }

    }

    public static class Klass_Meta<T extends Klass> extends ClassLiteral<T> implements Serializable {

        private static Klass_Meta instance;

        public static Klass_Meta getInstance() {
            if ( instance == null ) {
                instance = new Klass_Meta( new MetaProperty[] {  prop, another, oneAnother, manyOthers  } );
            }
            return instance;
        }

        public static final OneValuedMetaProperty<Klass,String> prop =
                new ToOnePropertyLiteral<Klass,String>( 0, "prop", URI.create( "http://www.test.org#Klass?prop" ) ) {
                    public String get( Klass o ) { return o.getProp(); }
                    public void set( Klass o, String value ) { o.setProp( value ); }
                    public boolean isDatatype() { return true; }
                };

        public static final OneValuedMetaProperty<Klass,AnotherKlass> another =
                new OneToOnePropertyLiteral<Klass, AnotherKlass>( 1, "another", URI.create( "http://www.test.org#Klass?another" ) ) {
                    public AnotherKlass get( Klass o ) { return o.getAnother(); }
                    public void set( Klass o, AnotherKlass value ) { o.setAnother( value ); }

                    @Override
                    public OneValuedMetaProperty<AnotherKlass, Klass> getInverse() {
                        return AnotherKlass_.theKlass;
                    }
                    public boolean isDatatype() { return false; }
                };

        public static final OneValuedMetaProperty<Klass,AnotherKlass> oneAnother =
                new ManyToOnePropertyLiteral<Klass,AnotherKlass>( 2, "oneAnother", URI.create( "http://www.test.org#Klass?oneAnother" ) ) {
                    public AnotherKlass get( Klass o ) { return o.getOneAnother(); }
                    public void set( Klass o, AnotherKlass value ) { o.setOneAnother( value ); }

                    @Override
                    public ManyValuedMetaProperty<AnotherKlass, Klass, List<Klass>> getInverse() {
                        return AnotherKlass_.manyKlasses;
                    }
                    public boolean isDatatype() { return false; }
                };

        public static final ManyValuedMetaProperty<Klass,AnotherKlass,List<AnotherKlass>> manyAnothers =
                new ManyToManyPropertyLiteral<Klass,AnotherKlass>( 3, "manyAnothers", URI.create( "http://www.test.org#AnotherKlass?manyAnothers" ) ) {
                    public List<AnotherKlass> get( Klass o ) { return o.getManyAnothers(); }
                    public void set( Klass o, List<AnotherKlass> value ) { o.setManyAnothers( value ); }

                    @Override
                    public ManyValuedMetaProperty<AnotherKlass,Klass,List<Klass>> getInverse() {
                        return AnotherKlass_.AnotherKlass_Meta.manyMoreKlasses;
                    }
                    public boolean isDatatype() { return false; }
                };



        protected Klass_Meta( MetaProperty<T,?,?>[] propertyLiterals ) {
            super( propertyLiterals );
        }

        @Override
        protected void cachePropertyNames() {
            propertyNames = PropertyReactivityUtil.getAccessibleProperties( Klass.class );
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

        public Klass_NewInstance<T> newInstance( Object id, With... args ) {
            return new Klass_NewInstance<T>( id, args );
        }

    }

    public static class Klass_Don<K, T extends Klass> extends DonLiteral<K,T> implements Serializable {
        public Klass_Don( K target, With... args ) {
            super( target, args );
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

        protected Klass_Modify<T> getSetter() {
            if ( setter == null ) {
                setter = new Klass_Modify( null, this.with );
            }
            return ( Klass_Modify<T>) setter;
        }

        public Klass_Don<K,T> prop( String newProp ) {
            getSetter().prop( newProp );
            return this;
        }

        public Klass_Don<K,T> another( AnotherKlass newAnother ) {
            getSetter().another( newAnother );
            return this;
        }

        public Klass_Don<K,T> oneAnother( AnotherKlass newAnother ) {
            getSetter().oneAnother( newAnother );
            return this;
        }

        public Klass_Don<K,T> manyOthers( List<AnotherKlass> val, Lit mode ) {
            getSetter().manyOthers( val, mode );
            return this;
        }

        public Klass_Don<K,T> manyOthers( AnotherKlass val, Lit mode ) {
            getSetter().manyOthers( val, mode );
            return this;
        }



    }
}

