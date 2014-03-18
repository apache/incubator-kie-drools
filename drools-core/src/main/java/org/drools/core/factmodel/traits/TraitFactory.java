/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.factmodel.traits;

import org.drools.core.RuleBase;
import org.drools.core.RuntimeDroolsException;
import org.drools.core.base.ClassFieldAccessor;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.factmodel.BuildUtils;
import org.drools.core.factmodel.ClassBuilderFactory;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.KieComponentFactory;
import org.drools.core.reteoo.ReteooRuleBase;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.util.HierarchyEncoder;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleStore;
import org.drools.core.util.asm.ClassFieldInspector;
import org.drools.core.rule.Package;
import org.kie.api.KieBase;
import org.kie.internal.KnowledgeBase;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Opcodes;
import org.mvel2.asm.Type;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class TraitFactory<T extends Thing<K>, K extends TraitableBean> implements Opcodes, Externalizable {

//    private static TripleStore store = new TripleStore( 500, 0.6f );

    public enum VirtualPropertyMode { MAP, TRIPLES }

    private VirtualPropertyMode mode = VirtualPropertyMode.TRIPLES;

    public final static String SUFFIX = "_Trait__Extension";

    private static final String pack = "org.drools.core.factmodel.traits.";

    private Map<String, Constructor> factoryCache = new HashMap<String, Constructor>();

    private Map<Class, Class<? extends CoreWrapper<?>>> wrapperCache = new HashMap<Class, Class<? extends CoreWrapper<?>>>();

    private transient ReteooRuleBase ruleBase;
    
    
    public static void setMode( VirtualPropertyMode newMode, KieBase kBase ) {
        RuleBase ruleBase = ((KnowledgeBaseImpl) kBase).getRuleBase();
        KieComponentFactory rcf = ((ReteooRuleBase) ruleBase).getConfiguration().getComponentFactory();
        ClassBuilderFactory cbf = rcf.getClassBuilderFactory();
        rcf.getTraitFactory().mode = newMode;
        switch ( newMode ) {
            case MAP    :
                cbf.setPropertyWrapperBuilder( new TraitMapPropertyWrapperClassBuilderImpl() );
                cbf.setTraitProxyBuilder( new TraitMapProxyClassBuilderImpl() );
                break;
            case TRIPLES:
                cbf.setPropertyWrapperBuilder( new TraitTriplePropertyWrapperClassBuilderImpl() );
                cbf.setTraitProxyBuilder( new TraitTripleProxyClassBuilderImpl() );
                break;
            default     :   throw new RuntimeException( " This should not happen : unexpected property wrapping method " + newMode );
        }

    }

    public static TraitFactory getTraitBuilderForKnowledgeBase( KieBase kb ) {
        ReteooRuleBase arb = (ReteooRuleBase) ((KnowledgeBaseImpl) kb ).getRuleBase();
        return arb.getConfiguration().getComponentFactory().getTraitFactory();
    }



    public TraitFactory() {        
    }


    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( mode );
        out.writeObject( factoryCache );
        out.writeObject( wrapperCache );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        mode = (VirtualPropertyMode) in.readObject();
        factoryCache = (Map<String, Constructor>) in.readObject();
        wrapperCache = (Map<Class, Class<? extends CoreWrapper<?>>>) in.readObject();
    }
    


    @Deprecated()
    /**
     * Test compatiblity only, do not use
     */
    public T getProxy( K core, Class<?> trait ) throws LogicalTypeInconsistencyException {
        return getProxy( core, trait, false );
    }

    public T getProxy( K core, Class<?> trait, boolean logical ) throws LogicalTypeInconsistencyException {
        String traitName = trait.getName();

        if ( core.hasTrait( traitName ) ) {
            return (T) core.getTrait( traitName );
        }

        String key = getKey( core.getClass(), trait );

        Constructor<T> konst;
        synchronized ( factoryCache ) {
             konst = factoryCache.get( key );
            if ( konst == null ) {
                konst = cacheConstructor( key, core, trait );
            }
        }

        T proxy = null;
        HierarchyEncoder hier = ruleBase.getConfiguration().getComponentFactory().getTraitRegistry().getHierarchy();
        try {
            switch ( mode ) {
                case MAP    :   proxy = konst.newInstance( core, core._getDynamicProperties(), hier.getCode( trait.getName() ), hier.getBottom(), logical );
                    break;
                case TRIPLES:   proxy = konst.newInstance( core, ruleBase.getTripleStore(), getTripleFactory(), hier.getCode( trait.getName() ), hier.getBottom(), logical );
                    break;
                default     :   throw new RuntimeException( " This should not happen : unexpected property wrapping method " + mode );
            }

            return proxy;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new LogicalTypeInconsistencyException( "Could not apply trait " + trait + " to object " + core, trait, core.getClass() );
    }


    public ReteooRuleBase getRuleBase() {
        return ruleBase;
    }

    public void setRuleBase( ReteooRuleBase ruleBase ) {
        this.ruleBase = ruleBase;        
    }


    private Constructor<T> cacheConstructor( String key, K core, Class<?> trait ) {
        Class<T> proxyClass = buildProxyClass( key, core, trait );
        if ( proxyClass == null ) {
            return null;
        }
        try {
            Constructor konst;
            switch ( mode ) {
                case MAP    :   konst = proxyClass.getConstructor( core.getClass(), Map.class, BitSet.class, BitSet.class, boolean.class );
                    break;
                case TRIPLES:   konst = proxyClass.getConstructor( core.getClass(), TripleStore.class, TripleFactory.class, BitSet.class, BitSet.class, boolean.class );
                    break;
                default     :   throw new RuntimeException( " This should not happen : unexpected property wrapping method " + mode );
            }

            factoryCache.put( key, konst );
            return konst;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getProxyName( ClassDefinition trait, ClassDefinition core ) {
        return getKey( core.getDefinedClass(), trait.getDefinedClass() ) + "_Proxy";
    }

    public static String getPropertyWrapperName( ClassDefinition trait, ClassDefinition core ) {
        return getKey( core.getDefinedClass(), trait.getDefinedClass() ) + "_ProxyWrapper";
    }

    private static String getKey( Class core, Class trait  ) {
        return ( trait.getName() + "." + core.getName() );
    }


    public static String getSoftFieldKey( String fieldName, Class fieldType, Class trait, Class core ) {
            return fieldName;
    }




    private Class<T> buildProxyClass( String key, K core, Class<?> trait ) {

        Class coreKlass = core.getClass();


        // get the trait classDef
        ClassDefinition tdef = ruleBase.getTraitRegistry().getTrait( trait.getName() );
        ClassDefinition cdef = ruleBase.getTraitRegistry().getTraitable( coreKlass.getName() );

        if ( tdef == null ) {
            if ( trait.getAnnotation( Trait.class ) != null ) {
                try {
                    if ( Thing.class.isAssignableFrom( trait ) ) {
                        tdef = buildClassDefinition( trait, null );
                    } else {
                        throw new RuntimeDroolsException( "Unable to create definition for class " + trait +
                                                          " : trait interfaces should extend " + Thing.class.getName() + " or be DECLARED as traits explicitly" );
                    }
                } catch ( IOException e ) {
                    throw new RuntimeDroolsException( "Unable to create definition for class " + trait + " : " + e.getMessage() );
                }
                ruleBase.getTraitRegistry().addTrait( tdef );
            } else {
                throw new RuntimeDroolsException( "Unable to find Trait definition for class " + trait.getName() + ". It should have been DECLARED as a trait" );
            }        }
        if ( cdef == null ) {
            if ( core.getClass().getAnnotation( Traitable.class ) != null ) {
                try {
                    cdef = buildClassDefinition( core.getClass(), core.getClass() );
                } catch ( IOException e ) {
                    throw new RuntimeDroolsException( "Unable to create definition for class " + coreKlass.getName() + " : " + e.getMessage() );
                }
                ruleBase.getTraitRegistry().addTraitable( cdef );
            } else {
                throw new RuntimeDroolsException( "Unable to find Core class definition for class " + coreKlass.getName() + ". It should have been DECLARED as a trait" );
            }
        }

        String proxyName = getProxyName( tdef, cdef );
        String wrapperName = getPropertyWrapperName( tdef, cdef );

        KieComponentFactory rcf = ruleBase.getConfiguration().getComponentFactory();


        TraitPropertyWrapperClassBuilder propWrapperBuilder = (TraitPropertyWrapperClassBuilder) rcf.getClassBuilderFactory().getPropertyWrapperBuilder();

        propWrapperBuilder.init( tdef, ruleBase.getTraitRegistry() );
        try {
            byte[] propWrapper = propWrapperBuilder.buildClass( cdef, ruleBase.getRootClassLoader() );
            ruleBase.registerAndLoadTypeDefinition( wrapperName, propWrapper );
        } catch (Exception e) {
            e.printStackTrace();
        }


        TraitProxyClassBuilder proxyBuilder = (TraitProxyClassBuilder) rcf.getClassBuilderFactory().getTraitProxyBuilder();

        proxyBuilder.init( tdef, rcf.getBaseTraitProxyClass(), ruleBase.getTraitRegistry() );
        try {
            byte[] proxy = proxyBuilder.buildClass( cdef, ruleBase.getRootClassLoader() );
            ruleBase.registerAndLoadTypeDefinition( proxyName, proxy );
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BitSet mask = ruleBase.getTraitRegistry().getFieldMask( trait.getName(), cdef.getDefinedClass().getName() );
            Class<T> wrapperClass = (Class<T>) ruleBase.getRootClassLoader().loadClass( wrapperName );
            Class<T> proxyClass = (Class<T>) ruleBase.getRootClassLoader().loadClass( proxyName );
            return proxyClass;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    private Package getPackage(String pack) {
        Package pkg = ruleBase.getPackage( pack );
        if ( pkg == null ) {
            pkg = new Package( pack );
            JavaDialectRuntimeData data = new JavaDialectRuntimeData();
            pkg.getDialectRuntimeRegistry().setDialectData( "java", data );
            data.onAdd(pkg.getDialectRuntimeRegistry(),
                    ruleBase.getRootClassLoader());
            ruleBase.addPackages( Arrays.asList(pkg) );
        }
        return pkg;

    }
















    public synchronized CoreWrapper<K> getCoreWrapper( Class<K> coreKlazz , ClassDefinition coreDef ) {
        if ( wrapperCache == null ) {
            wrapperCache = new HashMap<Class, Class<? extends CoreWrapper<?>>>();
        }
        Class<? extends CoreWrapper<K>> wrapperClass = null;
        if ( wrapperCache.containsKey( coreKlazz ) ) {
            wrapperClass = (Class<? extends CoreWrapper<K>>) wrapperCache.get( coreKlazz );
        } else {
            try {
                wrapperClass = buildCoreWrapper( coreKlazz, coreDef );
            } catch (IOException e) {
                return null;
            } catch (ClassNotFoundException e) {
                return null;
            }
            wrapperCache.put( coreKlazz, wrapperClass );
        }

        try {
            ruleBase.getTraitRegistry().addTraitable( buildClassDefinition( coreKlazz, wrapperClass ) );
            return wrapperClass != null ? wrapperClass.newInstance() : null;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

    }

    private ClassDefinition buildClassDefinition(Class<?> klazz, Class<?> wrapperClass) throws IOException {
        ClassFieldInspector inspector = new ClassFieldInspector( klazz );

        Package traitPackage = ruleBase.getPackagesMap().get( pack );
        if ( traitPackage == null ) {
            traitPackage = new Package( pack );
            traitPackage.setClassFieldAccessorCache( ruleBase.getClassFieldAccessorCache() );
            ruleBase.getPackagesMap().put( pack, traitPackage );
        }
        ClassFieldAccessorStore store = traitPackage.getClassFieldAccessorStore();

        ClassDefinition def;
        if ( ! klazz.isInterface() ) {
            String className = wrapperClass.getName();
            String superClass = wrapperClass != klazz ? klazz.getName() : klazz.getSuperclass().getName();
            String[] interfaces = new String[] {CoreWrapper.class.getName()};
            def = new ClassDefinition( className, superClass, interfaces );
            def.setDefinedClass( wrapperClass );

            Traitable tbl = wrapperClass.getAnnotation( Traitable.class );
            def.setTraitable( true, tbl != null && tbl.logical() );
        } else {
            String className = klazz.getName();
            String superClass = Object.class.getName();
            String[] interfaces = new String[ klazz.getInterfaces().length ];
            for ( int j = 0; j <  klazz.getInterfaces().length; j++ ) {
                interfaces[ j ] = klazz.getInterfaces()[ j ].getName();
            }
            def = new ClassDefinition( className, superClass, interfaces );
            def.setDefinedClass( klazz );
        }
        Map<String, Field> fields = inspector.getFieldTypesField();
        for ( Field f : fields.values() ) {
            if ( f != null ) {
                FieldDefinition fld = new FieldDefinition();
                fld.setName( f.getName() );
                fld.setTypeName( f.getType().getName() );
                fld.setInherited( true );
                ClassFieldAccessor accessor = store.getAccessor( def.getDefinedClass().getName(),
                        fld.getName() );
                fld.setReadWriteAccessor( accessor );

                def.addField( fld );
            }
        }


        return def;
    }

    private Class<CoreWrapper<K>> buildCoreWrapper( Class<K> coreKlazz, ClassDefinition coreDef ) throws IOException, ClassNotFoundException {

        String coreName = coreKlazz.getName();
        String wrapperName = coreName + "Wrapper";

        try {
            byte[] wrapper = new TraitCoreWrapperClassBuilderImpl().buildClass( coreDef, ruleBase.getRootClassLoader() );
            ruleBase.registerAndLoadTypeDefinition( wrapperName, wrapper );
//            JavaDialectRuntimeData data = ((JavaDialectRuntimeData) getPackage( pack ).getDialectRuntimeRegistry().
//                getDialectData( "java" ));

//            String resourceName = JavaDialectRuntimeData.convertClassToResourcePath( wrapperName );
//            data.putClassDefinition( resourceName, wrapper );
//            data.write( resourceName, wrapper );


//            data.onBeforeExecute();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        Class<CoreWrapper<K>> wrapperClass = (Class<CoreWrapper<K>>) ruleBase.getRootClassLoader().loadClass( wrapperName );
        return wrapperClass;
    }




















    public static void valueOf( MethodVisitor mv, String type ) {
        mv.visitMethodInsn( INVOKESTATIC,
                BuildUtils.getInternalType( BuildUtils.box( type ) ),
                "valueOf",
                "(" + BuildUtils.getTypeDescriptor( type ) + ")" +
                        BuildUtils.getTypeDescriptor( BuildUtils.box( type ) )
        );

    }


    public static void primitiveValue( MethodVisitor mv, String fieldType ) {
        mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( BuildUtils.box( fieldType ) ) );
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                BuildUtils.getInternalType( BuildUtils.box( fieldType ) ),
                fieldType + "Value",
                "()"+ BuildUtils.getTypeDescriptor( fieldType ) );
    }


    public static void invokeExtractor( MethodVisitor mv, String masterName, ClassDefinition trait, ClassDefinition core, FieldDefinition field ) {
        FieldDefinition tgtField = core.getFieldByAlias( field.resolveAlias() );
        String fieldType = tgtField.getTypeName();
        String fieldName = tgtField.getName();
        String returnType = BuildUtils.getTypeDescriptor( fieldType );

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                           BuildUtils.getInternalType( masterName ),
                           "object",
                           BuildUtils.getTypeDescriptor( core.getClassName() ) );

        mv.visitMethodInsn( INVOKEVIRTUAL,
                            Type.getInternalName( core.getDefinedClass() ),
                            BuildUtils.getterName( fieldName, fieldType ),
                            Type.getMethodDescriptor( Type.getType( returnType ), new Type[] {} ) );


    }


    public static void invokeInjector( MethodVisitor mv, String masterName, ClassDefinition trait, ClassDefinition core, FieldDefinition field, boolean toNull, int pointer ) {
        FieldDefinition tgtField = core.getFieldByAlias( field.resolveAlias() );
        String fieldType = tgtField.getTypeName();
        String fieldName = tgtField.getName();
        String returnType = BuildUtils.getTypeDescriptor( fieldType );


        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn( GETFIELD,
                           BuildUtils.getInternalType( masterName ),
                           "object",
                           BuildUtils.getTypeDescriptor( core.getName() ) );

        if ( toNull ) {
            mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );
        } else {
            mv.visitVarInsn( BuildUtils.varType( fieldType ), pointer );
        }

        if ( ! BuildUtils.isPrimitive( fieldType ) ) {
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( fieldType ) );
        }

        mv.visitMethodInsn( INVOKEVIRTUAL,
                            Type.getInternalName( core.getDefinedClass() ),
                            BuildUtils.setterName( fieldName, fieldType ),
                            Type.getMethodDescriptor( Type.getType( void.class ), new Type[] { Type.getType( returnType ) } ) );

    }


    public static String buildSignature( Method method ) {
        String sig = "(";
        for ( Class arg : method.getParameterTypes() ) {
            sig += BuildUtils.getTypeDescriptor( arg.getName() );
        }
        sig += ")";
        sig += BuildUtils.getTypeDescriptor( method.getReturnType().getName() );
        return sig;
    }


    public static int getStackSize( Method m ) {
        int stack = 1;
        for ( Class klass : m.getParameterTypes() ) {
            stack += BuildUtils.sizeOf( klass.getName() );
        }
        return stack;
    }

    public TripleFactory getTripleFactory() {
        return ruleBase.getConfiguration().getComponentFactory().getTripleFactory();
    }


}