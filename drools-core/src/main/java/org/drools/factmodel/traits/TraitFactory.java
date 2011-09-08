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

package org.drools.factmodel.traits;

import org.drools.KnowledgeBase;
import org.drools.base.ClassFieldAccessor;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.common.AbstractRuleBase;
import org.drools.core.util.asm.ClassFieldInspector;
import org.drools.factmodel.BuildUtils;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.rule.JavaDialectRuntimeData;
import org.drools.rule.Package;
import org.mvel2.asm.*;


import java.io.IOException;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TraitFactory<T extends IThing<K>, K extends ITraitable > implements Opcodes {


    public final static String SUFFIX = "_Trait__Extension";

    private static final String pack = "org.drools.factmodel.traits.";

    private static Map<String, Constructor> factoryCache = new HashMap<String, Constructor>();

    private AbstractRuleBase ruleBase;


    public static void reset() {
        factoryCache.clear();
    }


    public TraitFactory(KnowledgeBase knowledgeBase) {
        ruleBase = (AbstractRuleBase) ((KnowledgeBaseImpl) knowledgeBase).getRuleBase();
    }



    public T getProxy( K core, Class<?> trait ) {
        String traitName = trait.getName();

        if ( core.getTraits().containsKey( traitName ) ) {
            return (T) core.getTraits().get( traitName );
        }

        String key = getKey( core.getClass(), trait );


        Constructor<T> konst = factoryCache.get( key );
        if ( konst == null ) {
            konst = cacheConstructor( key, core, trait );
        }

        T proxy = null;
        try {
            proxy = konst.newInstance( core, core.getDynamicProperties() );
            core.getTraits().put( traitName, proxy );
            return proxy;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }








    private Constructor<T> cacheConstructor( String key, K core, Class<?> trait ) {
        Class<T> proxyClass = buildProxyClass( key, core, trait );
        if ( proxyClass == null ) {
            return null;
        }
        try {
            Constructor konst = proxyClass.getConstructor( core.getClass(), Map.class );
            factoryCache.put( key, konst );
            return konst;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }


    public static String getProxyName( ClassDefinition trait, ClassDefinition core ) {
        return getKey( core.getDefinedClass(), trait.getDefinedClass() ) + "Proxy";
    }

    public static String getPropertyWrapperName( ClassDefinition trait, ClassDefinition core ) {
        return getKey( core.getDefinedClass(), trait.getDefinedClass() ) + "ProxyWrapper";
    }

    private static String getKey( Class core, Class trait  ) {
        return ( trait.getName() + core.getName().replace(".","") );
    }




    private Class<T> buildProxyClass( String key, K core, Class<?> trait ) {

        Class coreKlass = core.getClass();


        // get the trait classDef
        ClassDefinition tdef = TraitRegistry.getInstance().getTrait( trait.getName() );
        ClassDefinition cdef = TraitRegistry.getInstance().getTraitable( coreKlass.getName() );

        String proxyName = getProxyName( tdef, cdef );
        String wrapperName = getPropertyWrapperName( tdef, cdef );

        JavaDialectRuntimeData data = ((JavaDialectRuntimeData) getPackage( tdef.getDefinedClass().getPackage().getName() ).getDialectRuntimeRegistry().
                getDialectData( "java" ));



        TraitProxyClassBuilder propWrapperBuilder = new TraitPropertyWrapperClassBuilderImpl();
            propWrapperBuilder.init( tdef );
        try {
            byte[] propWrapper = propWrapperBuilder.buildClass( cdef );
            data.write(JavaDialectRuntimeData.convertClassToResourcePath( wrapperName ), propWrapper );
        } catch (Exception e) {

        }


        TraitProxyClassBuilder proxyBuilder = new TraitProxyClassBuilderImpl();
            proxyBuilder.init( tdef );
        try {
            byte[] proxy = proxyBuilder.buildClass( cdef );
            data.write(JavaDialectRuntimeData.convertClassToResourcePath( proxyName ), proxy);
        } catch (Exception e) {

        }

        data.onBeforeExecute();

        try {
            long mask = TraitRegistry.getInstance().getFieldMask( trait.getName(), cdef.getDefinedClass().getName() );
            Class<T> proxyClass = (Class<T>) ruleBase.getRootClassLoader().loadClass( proxyName, true );
            bindAccessors( proxyClass, tdef, cdef, mask );
            Class<T> wrapperClass = (Class<T>) ruleBase.getRootClassLoader().loadClass( wrapperName, true );
            bindCoreAccessors( wrapperClass, cdef );
            return proxyClass;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void bindAccessors( Class<T> proxyClass, ClassDefinition tdef, ClassDefinition cdef, long mask ) {
        int j = 0;
        for ( FieldDefinition traitField : tdef.getFieldsDefinitions() ) {
            boolean isSoftField = (mask & (1 << j++)) == 0;
            if ( ! isSoftField ) {
                FieldDefinition field = cdef.getField(traitField.getName());
                Field staticField;
                try {
                    staticField = proxyClass.getField(field.getName() + "_reader");
                    staticField.set(null, field.getFieldAccessor().getReadAccessor() );

                    staticField = proxyClass.getField(field.getName() + "_writer");
                    staticField.set(null, field.getFieldAccessor().getWriteAccessor() );
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    private void bindCoreAccessors( Class<T> wrapperClass, ClassDefinition cdef ) {
        for ( FieldDefinition field : cdef.getFieldsDefinitions() ) {
            Field staticField;
            try {
                staticField = wrapperClass.getField(field.getName() + "_reader");
                staticField.set(null, field.getFieldAccessor().getReadAccessor() );

                staticField = wrapperClass.getField(field.getName() + "_writer");
                staticField.set(null, field.getFieldAccessor().getWriteAccessor() );
            } catch (NoSuchFieldException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
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













    private Map<Class, Class<? extends CoreWrapper<K>>> wrapperCache;



    public CoreWrapper<K> getCoreWrapper( Class<K> coreKlazz ) {
        if ( wrapperCache == null ) {
            wrapperCache = new HashMap<Class, Class<? extends CoreWrapper<K>>>();
        }
        Class<? extends CoreWrapper<K>> wrapperClass = null;
        if ( wrapperCache.containsKey( coreKlazz ) ) {
            wrapperClass = wrapperCache.get( coreKlazz );
        } else {
            try {
                wrapperClass = buildCoreWrapper( coreKlazz );
            } catch (IOException e) {
                return null;
            } catch (ClassNotFoundException e) {
                return null;
            }
            wrapperCache.put( coreKlazz, wrapperClass );
        }

        try {
            TraitRegistry.getInstance().addTraitable( buildWrapperClassDefinition( coreKlazz, wrapperClass ) );
            return wrapperClass != null ? wrapperClass.newInstance() : null;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

    }

    private ClassDefinition buildWrapperClassDefinition(Class<K> coreKlazz, Class<? extends CoreWrapper<K>> wrapperClass) throws IOException {
        ClassFieldInspector inspector = new ClassFieldInspector( coreKlazz );
        ClassFieldAccessorStore store = ruleBase.getPackagesMap().get( pack ).getClassFieldAccessorStore();

        String className = coreKlazz.getName() + "Wrapper";
        String superClass = coreKlazz.getName();
        String[] interfaces = new String[] {CoreWrapper.class.getName()};
        ClassDefinition def = new ClassDefinition( className, superClass, interfaces );
        def.setTraitable(true);
        def.setDefinedClass( wrapperClass );

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

    private Class<CoreWrapper<K>> buildCoreWrapper(Class<K> coreKlazz) throws IOException, ClassNotFoundException {

        String coreName = coreKlazz.getName();
        String wrapperName = coreName + "Wrapper";

        ClassDefinition coreDef = new ClassDefinition( coreKlazz.getName() );
            coreDef.setDefinedClass( coreKlazz );

        try {
            byte[] wrapper = new TraitCoreWrapperClassBuilderImpl().buildClass(coreDef);
            JavaDialectRuntimeData data = ((JavaDialectRuntimeData) getPackage( pack ).getDialectRuntimeRegistry().
                getDialectData( "java" ));
            data.write(JavaDialectRuntimeData.convertClassToResourcePath(wrapperName), wrapper);
            data.onBeforeExecute();
        } catch ( Exception e ) {

        }

        Class<CoreWrapper<K>> wrapperClass = (Class<CoreWrapper<K>>) ruleBase.getRootClassLoader().loadClass( wrapperName, true );
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


    public static void promote( MethodVisitor mv, String fieldType ) {
        mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( BuildUtils.box( fieldType ) ) );
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                BuildUtils.getInternalType( BuildUtils.box( fieldType ) ),
                fieldType + "Value",
                "()"+ BuildUtils.getTypeDescriptor( fieldType ) );
    }


    public static void invokeExtractor( MethodVisitor mv, String masterName, ClassDefinition source, ClassDefinition target, FieldDefinition field ) {
        String fieldType = field.getTypeName();
        mv.visitFieldInsn( GETSTATIC,
                BuildUtils.getInternalType( masterName ),
                field.getName()+"_reader",
                "Lorg/drools/spi/InternalReadAccessor;");

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( masterName ),
                "object",
                BuildUtils.getTypeDescriptor( target.getName() ));

        String returnType = BuildUtils.isPrimitive( fieldType ) ?
                BuildUtils.getTypeDescriptor( fieldType ) :
                "Ljava/lang/Object;";
        mv.visitMethodInsn( INVOKEINTERFACE,
                "org/drools/spi/InternalReadAccessor",
                BuildUtils.extractor( fieldType ),
                "(Ljava/lang/Object;)" + returnType );
    }


    public static void invokeInjector( MethodVisitor mv, String masterName, ClassDefinition source, ClassDefinition target, FieldDefinition field, boolean toNull, int pointer ) {
        String fieldName = field.getName();
        String fieldType = field.getTypeName();
        mv.visitFieldInsn( GETSTATIC,
                BuildUtils.getInternalType( masterName ),
                fieldName + "_writer",
                "Lorg/drools/spi/WriteAccessor;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( masterName ),
                "object",
                BuildUtils.getTypeDescriptor( target.getName() ) );

        if ( toNull ) {
            mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );
        } else {
            mv.visitVarInsn( BuildUtils.varType( fieldType ), pointer);
        }
        String argType = BuildUtils.isPrimitive( fieldType ) ?
                BuildUtils.getTypeDescriptor( fieldType ) :
                "Ljava/lang/Object;";
        mv.visitMethodInsn( INVOKEINTERFACE,
                "org/drools/spi/WriteAccessor",
                BuildUtils.injector( fieldType ),
                "(Ljava/lang/Object;" + argType + ")V");

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


}
