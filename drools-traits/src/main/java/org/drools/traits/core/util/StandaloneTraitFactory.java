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
package org.drools.traits.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.base.factmodel.AnnotationDefinition;
import org.drools.base.factmodel.ClassDefinition;
import org.drools.base.factmodel.traits.CoreWrapper;
import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.Trait;
import org.drools.base.factmodel.traits.TraitableBean;
import org.drools.traits.core.factmodel.HierarchyEncoder;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.drools.traits.core.factmodel.AbstractTraitFactory;
import org.drools.traits.core.factmodel.LogicalTypeInconsistencyException;
import org.drools.traits.core.factmodel.TraitClassBuilderImpl;
import org.drools.traits.core.factmodel.TraitRegistryImpl;
import org.drools.traits.core.factmodel.TripleFactory;
import org.drools.traits.core.factmodel.TripleFactoryImpl;
import org.drools.traits.core.factmodel.TripleStore;
import org.drools.traits.core.factmodel.VirtualPropertyMode;
import org.drools.traits.core.reteoo.TraitRuntimeComponentFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandaloneTraitFactory<T extends Thing<K>, K extends TraitableBean> extends AbstractTraitFactory<T,K> {

    private static final Logger LOG = LoggerFactory.getLogger(StandaloneTraitFactory.class);

    private ProjectClassLoader classLoader;
    private TraitRuntimeComponentFactoryImpl kieComponentFactory = new TraitRuntimeComponentFactoryImpl();
    private TraitRegistryImpl registry;
    private ClassFieldAccessorStore store;
    private HierarchyEncoder encoder;

    private TripleStore tripleStore;
    private TripleFactory tripleFactory;

    public StandaloneTraitFactory( ProjectClassLoader classLoader ) {
        this( classLoader, VirtualPropertyMode.MAP );
    }

    public StandaloneTraitFactory(ProjectClassLoader classLoader, VirtualPropertyMode mode ) {
        this.classLoader = classLoader;
        this.store = new ClassFieldAccessorStore();
            this.store.setClassFieldAccessorCache( new ClassFieldAccessorCache( this.classLoader ) );
        this.encoder = new HierarchyEncoderImpl();

        encoder.encode( Thing.class, Collections.emptyList() );

        this.mode = mode;
        setMode( this.mode, null, kieComponentFactory );
    }

    /**
     * Do not use this constructor! It should be used just by deserialisation.
     */
    public StandaloneTraitFactory() {
    }

    @Override
    protected Class<?> registerAndLoadTypeDefinition( String className, byte[] def ) throws ClassNotFoundException {
        try {
            return this.classLoader.loadClass( className );
        } catch (ClassNotFoundException e) {
            if (def != null ) {
                return classLoader.defineClass(className, def);
            } else {
                throw e;
            }
        }
    }

    @Override
    protected ClassLoader getRootClassLoader() {
        return classLoader;
    }

    @Override
    protected TraitRegistryImpl getTraitRegistry() {
        if(registry == null) {
            registry = new TraitRegistryImpl();
        }
        return registry;
    }

    @Override
    protected HierarchyEncoder getHierarchyEncoder() {
        return encoder;
    }

    @Override
    protected TripleStore getTripleStore() {
        if ( tripleStore == null ) {
            tripleStore = new TripleStore();
        }
        return tripleStore;
    }

    @Override
    protected TripleFactory getTripleFactory() {
        if ( tripleFactory == null ) {
            tripleFactory = new TripleFactoryImpl();
        }
        return tripleFactory;
    }

    @Override
    protected ClassFieldAccessorStore getClassFieldAccessorStore() {
        return store;
    }

    @Override
    public T getProxy(K core, Class<?> trait, boolean logical ) throws LogicalTypeInconsistencyException {
        encode( trait );
        if ( ! getTraitRegistry().getTraits().containsKey( trait.getName() ) ) {
            try {
                getTraitRegistry().addTrait( trait.getName(), buildClassDefinition( trait, trait ) );
            } catch ( IOException e ) {
                LOG.error("Exception", e);
            }
        }
        return super.getProxy(core, trait, logical );
    }

    private void encode( Class<?> trait ) {
        for ( Class sup : trait.getInterfaces() ) {
            encode( sup );
        }
        List<String> supers = new ArrayList( trait.getInterfaces().length );
        for ( Class k : trait.getInterfaces() ) {
            supers.add( k.getName() );
        }
        getHierarchyEncoder().encode( trait.getName(), supers );
    }

    public <X> CoreWrapper<X> makeTraitable(X o, Class<X> klass ) {
        if ( o instanceof TraitableBean ) {
            throw new IllegalStateException( "Method makeTraitable should be used on non-traitable objects" );
        }
        try {
            Class<CoreWrapper<X>> wrapperClass = buildCoreWrapper( klass, buildClassDefinition( klass, klass ) );
            CoreWrapper<X> wrapper = getCoreWrapper( klass, buildClassDefinition( klass, wrapperClass ) );
            wrapper.init( o );
            return wrapper;
        } catch ( Exception e ) {
            LOG.error("Exception", e);
        }
        return null;
    }

    public T don( K core, Class<T> trait ) throws LogicalTypeInconsistencyException {
        if ( trait.getAnnotation( Trait.class ) == null || ! Thing.class.isAssignableFrom(trait ) ) {
            trait = extendAsProperTrait( trait );
        }
        return getProxy( core, trait, false );
    }

    private Class<T> extendAsProperTrait( Class<T> trait ) {
        String extName = trait.getName() + SUFFIX;
        if ( ! classLoader.isClassInUse( extName ) ) {
            try {
                ClassDefinition extDef = new ClassDefinition( extName );
                extDef.setSuperClass( Object.class.getName() );

                AnnotationDefinition annot = new AnnotationDefinition( Trait.class.getName() );
                extDef.addAnnotation( annot );

                String[] supers = new String[] { Thing.class.getName(), trait.getName() };
                extDef.setInterfaces( supers );

                byte[] ext = new TraitClassBuilderImpl().buildClass(extDef, classLoader );
                Class<?> klass = registerAndLoadTypeDefinition( extName, ext );

                ClassDefinition tDef = buildClassDefinition( trait, trait );
                tDef.setDefinedClass( klass );
                getTraitRegistry().addTrait( tDef );
            } catch ( Exception e ) {
                LOG.error("Exception", e);
            }
        }
        try {
            return (Class<T>) Class.forName( extName, false, classLoader );
        } catch ( ClassNotFoundException e ) {
            LOG.error("Exception", e);
        }
        return null;
    }
}


