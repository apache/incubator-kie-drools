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

import org.drools.core.base.ClassFieldAccessor;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.factmodel.BuildUtils;
import org.drools.core.factmodel.ClassBuilderFactory;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.factmodel.MapCore;
import org.drools.core.reteoo.KieComponentFactory;
import org.drools.core.util.HierarchyEncoder;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleStore;
import org.drools.core.util.asm.ClassFieldInspector;
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

public abstract class AbstractTraitFactory<T extends Thing<K>, K extends TraitableBean> implements Opcodes, Externalizable {

    protected VirtualPropertyMode mode = VirtualPropertyMode.MAP;

    public final static String SUFFIX = "_Trait__Extension";

    protected static final String pack = "org.drools.core.factmodel.traits.";

    protected Map<String, Constructor> factoryCache = new HashMap<String, Constructor>();

    protected Map<Class, Class<? extends CoreWrapper<?>>> wrapperCache = new HashMap<Class, Class<? extends CoreWrapper<?>>>();


    public AbstractTraitFactory() {
    }


    protected static void setMode( VirtualPropertyMode newMode, KieComponentFactory rcf ) {
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
        HierarchyEncoder hier = getHierarchyEncoder();
        try {
            switch ( mode ) {
                case MAP    :   proxy = konst.newInstance( core, core._getDynamicProperties(), hier.getCode( trait.getName() ), hier.getBottom(), logical );
                    break;
                case TRIPLES:   proxy = konst.newInstance( core, getTripleStore(), getTripleFactory(), hier.getCode( trait.getName() ), hier.getBottom(), logical );
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


    protected Constructor<T> cacheConstructor( String key, K core, Class<?> trait ) {
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

    protected static String getKey( Class core, Class trait  ) {
        return ( trait.getName() + "." + core.getName() );
    }

    public static String getSoftFieldKey( String fieldName, Class fieldType, Class trait, Class core ) {
            return fieldName;
    }




    protected Class<T> buildProxyClass( String key, K core, Class<?> trait ) {

        Class coreKlass = core.getClass();


        // get the trait classDef
        ClassDefinition tdef = getTraitRegistry().getTrait( trait.getName() );
        ClassDefinition cdef = getTraitRegistry().getTraitable( coreKlass.getName() );

        if ( tdef == null ) {
            if ( trait.getAnnotation( Trait.class ) != null ) {
                try {
                    if ( Thing.class.isAssignableFrom( trait ) ) {
                        tdef = buildClassDefinition( trait, null );
                    } else {
                        throw new RuntimeException( "Unable to create definition for class " + trait +
                                                    " : trait interfaces should extend " + Thing.class.getName() + " or be DECLARED as traits explicitly" );
                    }
                } catch ( IOException e ) {
                    throw new RuntimeException( "Unable to create definition for class " + trait + " : " + e.getMessage() );
                }
                getTraitRegistry().addTrait( tdef );
            } else {
                throw new RuntimeException( "Unable to find Trait definition for class " + trait.getName() + ". It should have been DECLARED as a trait" );
            }        }
        if ( cdef == null ) {
            if ( core.getClass().getAnnotation( Traitable.class ) != null ) {
                try {
                    cdef = buildClassDefinition( core.getClass(), core.getClass() );
                } catch ( IOException e ) {
                    throw new RuntimeException( "Unable to create definition for class " + coreKlass.getName() + " : " + e.getMessage() );
                }
                getTraitRegistry().addTraitable( cdef );
            } else {
                throw new RuntimeException( "Unable to find Core class definition for class " + coreKlass.getName() + ". It should have been DECLARED as a trait" );
            }
        }

        String proxyName = getProxyName( tdef, cdef );
        String wrapperName = getPropertyWrapperName( tdef, cdef );

        KieComponentFactory rcf = getComponentFactory();


        TraitPropertyWrapperClassBuilder propWrapperBuilder = (TraitPropertyWrapperClassBuilder) rcf.getClassBuilderFactory().getPropertyWrapperBuilder();

        propWrapperBuilder.init( tdef, getTraitRegistry() );
        try {
            byte[] propWrapper = propWrapperBuilder.buildClass( cdef, getRootClassLoader() );
            registerAndLoadTypeDefinition( wrapperName, propWrapper );
        } catch (Exception e) {
            e.printStackTrace();
        }


        TraitProxyClassBuilder proxyBuilder = (TraitProxyClassBuilder) rcf.getClassBuilderFactory().getTraitProxyBuilder();

        proxyBuilder.init( tdef, rcf.getBaseTraitProxyClass(), getTraitRegistry() );
        try {
            byte[] proxy = proxyBuilder.buildClass( cdef, getRootClassLoader() );
            registerAndLoadTypeDefinition( proxyName, proxy );
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BitSet mask = getTraitRegistry().getFieldMask( trait.getName(), cdef.getDefinedClass().getName() );
            Class<T> wrapperClass = (Class<T>) getRootClassLoader().loadClass( wrapperName );
            Class<T> proxyClass = (Class<T>) getRootClassLoader().loadClass( proxyName );
            return proxyClass;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized <K> CoreWrapper<K> getCoreWrapper( Class<K> coreKlazz , ClassDefinition coreDef ) {
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
            getTraitRegistry().addTraitable( buildClassDefinition( coreKlazz, wrapperClass ) );
            return wrapperClass != null ? wrapperClass.newInstance() : null;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

    }

    public <K> TraitableBean<K,CoreWrapper<K>> asTraitable( K core, ClassDefinition coreDef) {
        if ( coreDef.getDefinedClass() != core.getClass() ) {
            // ensure that a compatible interface cDef is not replaced for the missing actual definition
            try {
                coreDef = buildClassDefinition( core.getClass(), core.getClass() );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        if ( core instanceof Map ) {
            if ( ! coreDef.isTraitable() ) {
                throw new UnsupportedOperationException( "Error: cannot apply a trait to non-traitable class " + core.getClass() + ". Was it declared as @Traitable? ");
            }
            return coreDef.isFullTraiting() ? new LogicalMapCore( (Map) core ) : new MapCore( (Map) core );
        }

        CoreWrapper<K> wrapper = (CoreWrapper<K>) getCoreWrapper( core.getClass(), coreDef );
        if ( wrapper == null ) {
            throw new UnsupportedOperationException( "Error: cannot apply a trait to non-traitable class " + core.getClass() + ". Was it declared as @Traitable? ");
        }
        wrapper.init( core );
        return wrapper;
    }


    public ClassDefinition buildClassDefinition(Class<?> klazz, Class<?> wrapperClass) throws IOException {
        ClassFieldInspector inspector = new ClassFieldInspector( klazz );

        ClassFieldAccessorStore store = getClassFieldAccessorStore();

        ClassDefinition def;
        if ( ! klazz.isInterface() ) {
            String className = wrapperClass.getName();
            String superClass = wrapperClass != klazz ? klazz.getName() : klazz.getSuperclass().getName();
            String[] interfaces = new String[ klazz.getInterfaces().length + 1 ];
            for ( int j = 0; j <  klazz.getInterfaces().length; j++ ) {
                interfaces[ j ] = klazz.getInterfaces()[ j ].getName();
            }
            interfaces[ interfaces.length - 1 ] = CoreWrapper.class.getName();
            def = new ClassDefinition( className, superClass, interfaces );
            def.setDefinedClass( wrapperClass );

            Traitable tbl = wrapperClass.getAnnotation( Traitable.class );
            def.setTraitable( true, tbl != null && tbl.logical() );
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
        } else {
            String className = klazz.getName();
            String superClass = Object.class.getName();
            String[] interfaces = new String[ klazz.getInterfaces().length ];
            for ( int j = 0; j <  klazz.getInterfaces().length; j++ ) {
                interfaces[ j ] = klazz.getInterfaces()[ j ].getName();
            }
            def = new ClassDefinition( className, superClass, interfaces );
            def.setDefinedClass( klazz );

            Map<String, Method> properties = inspector.getGetterMethods();
            for ( String key : properties.keySet() ) {
                Method m = properties.get( key );
                if ( m != null && m.getDeclaringClass() != TraitType.class && m.getDeclaringClass() != Thing.class && inspector.getSetterMethods().containsKey( key ) ) {
                    FieldDefinition fld = new FieldDefinition();
                    fld.setName( getterToFieldName( m.getName() ) );
                    fld.setTypeName( m.getReturnType().getName() );
                    fld.setInherited( true );
                    ClassFieldAccessor accessor = store.getAccessor( def.getDefinedClass().getName(),
                                                                     fld.getName() );
                    fld.setReadWriteAccessor( accessor );

                    def.addField( fld );
                }
            }
        }

        return def;
    }

    private String getterToFieldName( String getter ) {
        getter = getter.startsWith( "is" ) ? getter.substring( 2 ) : getter.substring( 3 );
        getter = getter.substring( 0, 1 ).toLowerCase() + getter.substring( 1 );
        return getter;
    }

    protected <K> Class<CoreWrapper<K>> buildCoreWrapper( Class<K> coreKlazz, ClassDefinition coreDef ) throws IOException, ClassNotFoundException {

        String coreName = coreKlazz.getName();
        String wrapperName = coreName + "Wrapper";

        try {
            byte[] wrapper = new TraitCoreWrapperClassBuilderImpl().buildClass( coreDef, getRootClassLoader() );
            registerAndLoadTypeDefinition( wrapperName, wrapper );
//            JavaDialectRuntimeData data = ((JavaDialectRuntimeData) getPackage( pack ).getDialectRuntimeRegistry().
//                getDialectData( "java" ));

//            String resourceName = JavaDialectRuntimeData.convertClassToResourcePath( wrapperName );
//            data.putClassDefinition( resourceName, wrapper );
//            data.write( resourceName, wrapper );


//            data.onBeforeExecute();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        Class<CoreWrapper<K>> wrapperClass = (Class<CoreWrapper<K>>) getRootClassLoader().loadClass( wrapperName );
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


    public static boolean isCompatible( Method m, Method q ) {
        if ( ! m.getName().equals( q.getName() ) ) {
            return false;
        }
        if ( ! m.getReturnType().isAssignableFrom( q.getReturnType() ) ) {
            return false;
        }
        if ( m.getParameterTypes().length != q.getParameterTypes().length ) {
            return false;
        }
        for ( int j = 0; j < q.getParameterTypes().length; j++ ) {
            if ( ! q.getParameterTypes()[ j ].isAssignableFrom( m.getParameterTypes()[ j ] ) ) {
                return false;
            }
        }
        return true;
    }

    protected static boolean excludeFromShadowing( Method m, ClassDefinition cdef ) {
        return Object.class.equals( m.getDeclaringClass() ) ||
               "getFields".equals( m.getName() ) || "getCore".equals( m.getName() ) || "isTop".equals( m.getName() ) ||
               isGetter( m, cdef ) ||
               isSetter( m, cdef );
    }

    protected static boolean isGetter( Method m, ClassDefinition cdef ) {
        return ( m.getParameterTypes().length == 0 ) &&
               ( ! void.class.equals( m.getReturnType() ) ) &&
               ( m.getName().startsWith( "get" ) || m.getName().startsWith( "is" ) ) &&
               ( cdef.getField( toFieldName( m.getName() ) ) != null );
    }

    private static String toFieldName( String name ) {
        String fname = name;
        if ( ( fname.startsWith( "get" ) || fname.startsWith( "set" ) ) && fname.length() > 3 ) {
            return name.substring( 3, 4 ).toLowerCase() + name.substring( 4 );
        }
        if ( fname.startsWith( "is" ) && fname.length() > 2 ) {
            return name.substring( 2, 3 ).toLowerCase() + name.substring( 3 );
        }
        return name;
    }

    protected static boolean isSetter( Method m, ClassDefinition cdef ) {
        return ( m.getParameterTypes().length == 1 ) &&
               ( void.class.equals( m.getReturnType() ) ) &&
               ( m.getName().startsWith( "set" ) ) &&
               ( cdef.getField( toFieldName( m.getName() ) ) != null );
    }


    protected abstract Class<?> registerAndLoadTypeDefinition( String proxyName, byte[] proxy ) throws ClassNotFoundException;

    protected abstract ClassLoader getRootClassLoader();

    protected abstract KieComponentFactory getComponentFactory();

    protected abstract TraitRegistry getTraitRegistry();

    protected abstract HierarchyEncoder getHierarchyEncoder();

    protected abstract TripleStore getTripleStore();

    protected abstract TripleFactory getTripleFactory();

    protected abstract ClassFieldAccessorStore getClassFieldAccessorStore();

}