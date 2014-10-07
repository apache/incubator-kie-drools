package org.drools.core.util;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.factmodel.traits.AbstractTraitFactory;
import org.drools.core.factmodel.traits.CoreWrapper;
import org.drools.core.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.Trait;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.factmodel.traits.VirtualPropertyMode;
import org.drools.core.metadata.Metadatable;
import org.drools.core.metadata.Modify;
import org.drools.core.reteoo.KieComponentFactory;
import org.drools.core.rule.TypeDeclaration;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StandaloneTraitFactory<T extends Thing<K>, K extends TraitableBean> extends AbstractTraitFactory<T,K> {

    private ProjectClassLoader classLoader;
    private KieComponentFactory kieComponentFactory;
    private TraitRegistry registry;
    private ClassFieldAccessorStore store;
    private HierarchyEncoder encoder;

    private TripleStore tripleStore;
    private TripleFactory tripleFactory;

    public StandaloneTraitFactory( ProjectClassLoader classLoader ) {
        this( classLoader, new KieComponentFactory() );
    }

    public StandaloneTraitFactory( ProjectClassLoader classLoader, KieComponentFactory factory ) {
        this( classLoader, factory, VirtualPropertyMode.MAP );
    }

    public StandaloneTraitFactory( ProjectClassLoader classLoader, KieComponentFactory factory, VirtualPropertyMode mode ) {
        this.classLoader = classLoader;
        this.kieComponentFactory = factory;
        this.registry = kieComponentFactory.getTraitRegistry();
        this.store = new ClassFieldAccessorStore();
            this.store.setClassFieldAccessorCache( new ClassFieldAccessorCache( this.classLoader ) );
        this.encoder = new HierarchyEncoderImpl();

        encoder.encode( Thing.class, Collections.emptyList() );

        this.mode = mode;
        setMode( this.mode, getComponentFactory() );
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
    protected KieComponentFactory getComponentFactory() {
        return kieComponentFactory;
    }

    @Override
    protected TraitRegistry getTraitRegistry() {
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

    public T getProxy( K core, Class<?> trait, boolean logical ) throws LogicalTypeInconsistencyException {
        encode( trait );
        if ( ! getTraitRegistry().getTraits().containsKey( trait.getName() ) ) {
            try {
                getTraitRegistry().addTrait( trait.getName(), buildClassDefinition( trait, trait ) );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return (T) super.getProxy( core, trait, logical );
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

    public <X> CoreWrapper<X> makeTraitable( X o, Class<X> klass ) {
        if ( o instanceof TraitableBean ) {
            throw new IllegalStateException( "Method makeTraitable should be used on non-traitable objects" );
        }
        try {
            Class<CoreWrapper<X>> wrapperClass = buildCoreWrapper( klass, buildClassDefinition( klass, klass ) );
            CoreWrapper<X> wrapper = getCoreWrapper( klass, buildClassDefinition( klass, wrapperClass ) );
            wrapper.init( o );
            return wrapper;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public T don( K core, Class<T> trait ) throws LogicalTypeInconsistencyException {
        if ( trait.getAnnotation( Trait.class ) == null || ! Thing.class.isAssignableFrom( trait ) ) {
            trait = extendAsProperTrait( trait );
        }
        return getProxy( core, trait, false );
    }

    private Class<T> extendAsProperTrait( Class<T> trait ) {
        String extName = trait.getName() + TraitFactory.SUFFIX;
        if ( ! classLoader.isClassInUse( extName ) ) {
            try {
                ClassDefinition extDef = new ClassDefinition( extName );
                extDef.setSuperClass( Object.class.getName() );

                AnnotationDefinition annot = new AnnotationDefinition( Trait.class.getName() );
                extDef.addAnnotation( annot );

                String[] supers = new String[] { Thing.class.getName(), trait.getName() };
                extDef.setInterfaces( supers );

                byte[] ext = getComponentFactory().getClassBuilderFactory().getTraitBuilder().buildClass( extDef, classLoader );
                Class<?> klass = registerAndLoadTypeDefinition( extName, ext );

                ClassDefinition tDef = buildClassDefinition( trait, trait );
                tDef.setDefinedClass( klass );
                getTraitRegistry().addTrait( tDef );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        try {
            return (Class<T>) Class.forName( extName, false, classLoader );
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        return null;
    }
}


