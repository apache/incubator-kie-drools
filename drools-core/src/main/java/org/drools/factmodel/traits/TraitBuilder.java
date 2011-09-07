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
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

public class TraitBuilder<T extends IThing<K>, K extends ITraitable > implements Opcodes {


    private static final String pack = "org.drools.factmodel.traits.";

    private static Map<String, Constructor> factoryCache = new HashMap<String, Constructor>();

    private AbstractRuleBase ruleBase;


    public static void reset() {
        factoryCache.clear();
    }


    public TraitBuilder( KnowledgeBase knowledgeBase ) {
        ruleBase = (AbstractRuleBase) ((KnowledgeBaseImpl) knowledgeBase).getRuleBase();
    }



    public T getProxy( K core, Class<?> trait ) {
        String traitName = trait.getName();

        if ( core.getTraits().containsKey( traitName ) ) {
            return (T) core.getTraits().get( traitName );
        }

        String key = getKey( core, trait );




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


    private String getKey(K core, Class<?> trait) {
        return ( core.getClass().getName()  + trait.getName()).replace(".","");
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

    private Class<T> buildProxyClass( String key, K core, Class<?> trait ) {

        Class coreKlass = core.getClass();

        // get the method bitmask
        long mask = TraitRegistry.getInstance().getFieldMask( trait.getName(), coreKlass.getName() );
        // get the trait classDef
        ClassDefinition tdef = TraitRegistry.getInstance().getTrait( trait.getName() );
        ClassDefinition cdef = TraitRegistry.getInstance().getTraitable( coreKlass.getName() );

        String wrapperName = pack + key + "ProxyWrapper";
        String proxyName = pack + key + "Proxy";

        byte[] wrapper = buildWrapperBytes(wrapperName, proxyName, cdef, tdef, mask );
        byte[] proxy = buildProxyBytes(wrapperName, proxyName, cdef, tdef, mask );


        JavaDialectRuntimeData data = ((JavaDialectRuntimeData) getPackage( pack ).getDialectRuntimeRegistry().
                getDialectData( "java" ));
        data.write(JavaDialectRuntimeData.convertClassToResourcePath(wrapperName), wrapper);
        data.write(JavaDialectRuntimeData.convertClassToResourcePath(proxyName), proxy);
        data.onBeforeExecute();

        try {
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






    public static byte[] buildInterface( ClassDefinition classDef ) {

        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;


        try {
            String cName = BuildUtils.getInternalType(classDef.getClassName());
            String genericTypes = BuildUtils.getGenericTypes( classDef.getInterfaces() );
            String superType = BuildUtils.getInternalType( "java.lang.Object" );
            String[] intfaces = null;
            if ( Object.class.getName().equals( classDef.getSuperClass() ) ) {
                intfaces = BuildUtils.getInternalTypes( classDef.getInterfaces() );
            } else {
                intfaces = BuildUtils.getInternalTypes( classDef.getInterfaces() );
                intfaces = Arrays.copyOf( intfaces, intfaces.length + 1 );
                intfaces[ intfaces.length - 1 ] = BuildUtils.getInternalType( classDef.getSuperClass() );

            }


            cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                    cName,
                    genericTypes,
                    superType,
                    intfaces );

            for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
                String name = field.getName();
                name = name.substring(0,1).toUpperCase() + name.substring(1);
                String target = BuildUtils.getTypeDescriptor(field.getTypeName());

                String prefix = BuildUtils.isBoolean( field.getTypeName() ) ? "is" : "get";

                mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, prefix + name, "()" + target, null, null);
                mv.visitEnd();

                mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "set" + name, "(" + target + ")V", null, null);
                mv.visitEnd();
            }

            cw.visitEnd();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cw.toByteArray();
    }




    private byte[] buildProxyBytes(String name, String masterName, ClassDefinition core, ClassDefinition trait, long mask) {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        String internalWrapper  = BuildUtils.getInternalType(name);
        String internalProxy    = BuildUtils.getInternalType(masterName);
        String descrWrapper     = BuildUtils.getTypeDescriptor(name);
        String descrProxy       = BuildUtils.getTypeDescriptor(masterName);

        String internalCore     = BuildUtils.getInternalType(core.getClassName());
        String descrCore        = BuildUtils.getTypeDescriptor(core.getClassName());
        String internalTrait    = BuildUtils.getInternalType(trait.getClassName());
        String descrTrait       = BuildUtils.getTypeDescriptor(trait.getClassName());


        Class mixinClass = null;
        String mixin = null;
        Set<Method> mixinMethods = new HashSet<Method>();
        Map<String,Method> mixinGetSet = new HashMap<String,Method>();
        try {
            if ( trait.getDefinedClass() != null ) {
                Trait annTrait = trait.getDefinedClass().getAnnotation( Trait.class );
                if ( annTrait != null && ! annTrait.impl().equals(Trait.NullMixin.class) ) {
                    mixinClass = annTrait.impl();
                    mixin = mixinClass.getSimpleName().substring(0,1).toLowerCase() + mixinClass.getSimpleName().substring(1);
                    ClassFieldInspector cfi = new ClassFieldInspector( mixinClass );

                    for ( Method m : mixinClass.getMethods() ) {
                        try {
                            trait.getDefinedClass().getMethod(m.getName(), m.getParameterTypes() );
                            if ( cfi.getGetterMethods().containsValue( m )
                                 || cfi.getSetterMethods().containsValue( m )) {
                                mixinGetSet.put( m.getName(), m );
                            } else {
                                mixinMethods.add( m );
                            }
                        } catch (NoSuchMethodException e) {

                        }
                    }

                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }



        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, internalProxy, null, "org/drools/factmodel/traits/TraitProxy", new String[]{internalTrait});

        {
            fv = cw.visitField(ACC_PUBLIC + ACC_FINAL, "object", descrCore, null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PUBLIC + ACC_FINAL, "map", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null);
            fv.visitEnd();
        }
        if ( mixinClass != null ) {
            {
                fv = cw.visitField( ACC_PRIVATE,
                        mixin,
                        BuildUtils.getTypeDescriptor( mixinClass.getName() ),
                        null, null);
                fv.visitEnd();
            }
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + descrCore + "Ljava/util/Map;)V", "(" + descrCore + "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)V", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TraitProxy", "<init>", "()V");
            if ( mixinClass != null ) {
                try {
                    Constructor con = mixinClass.getConstructor( trait.getDefinedClass() );

                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitTypeInsn(NEW, BuildUtils.getInternalType( mixinClass.getName() ) );
                    mv.visitInsn(DUP);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn( INVOKESPECIAL,
                                        BuildUtils.getInternalType( mixinClass.getName() ),
                                        "<init>",
                                        "("+ BuildUtils.getTypeDescriptor( trait.getDefinedClass().getName() ) + ")V");
                    mv.visitFieldInsn( PUTFIELD,
                            internalProxy,
                            mixin,
                            BuildUtils.getTypeDescriptor( mixinClass.getName() ) );
                } catch ( NoSuchMethodException nsme ) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitTypeInsn(NEW, BuildUtils.getInternalType( mixinClass.getName() ) );
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn(INVOKESPECIAL, BuildUtils.getInternalType( mixinClass.getName() ), "<init>", "()V");
                    mv.visitFieldInsn( PUTFIELD,
                            internalProxy,
                            mixin,
                            BuildUtils.getTypeDescriptor( mixinClass.getName() ) );
                }

            }
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, internalProxy, "object", descrCore);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitFieldInsn(PUTFIELD, internalProxy, "map", "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, internalWrapper);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, internalWrapper, "<init>", "(" + descrCore + "Ljava/util/Map;)V");
            mv.visitFieldInsn(PUTFIELD, internalProxy, "fields", "Ljava/util/Map;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(5, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getCore", "()" + descrCore + "", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, internalProxy, "object", descrCore);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getObject", "()Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, internalProxy, "object", descrCore);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "getCore", "()Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, internalProxy, "getCore", "()" + descrCore + "");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }


        int j = 0;
        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {

            boolean isSoftField = (mask & (1 << j++)) == 0;
            if ( isSoftField ) {
                if ( ! mixinGetSet.containsKey( BuildUtils.getterName( field.getName(), field.getTypeName() ) ) ) {
                    buildSoftGetter( cw, field.getName(), field.getTypeName(), masterName, core.getName() );
                    buildSoftSetter( cw, field.getName(), field.getTypeName(), masterName, core.getName() );
                } else {
                    //
                }

            } else {
                {
                    fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, field.getName()+"_reader", "Lorg/drools/spi/InternalReadAccessor;", null, null);
                    fv.visitEnd();
                }
                {
                    fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, field.getName()+"_writer", "Lorg/drools/spi/WriteAccessor;", null, null);
                    fv.visitEnd();
                }

                buildHardGetter( cw, field, masterName, trait, core );
                buildHardSetter( cw, field, masterName, trait, core );

            }
        }


        buildEqualityMethods( cw, masterName, core.getClassName() );

        if ( mixinClass != null ) {
            buildMixinMethods( cw, masterName, mixin, mixinClass, mixinMethods );
            buildMixinMethods( cw, masterName, mixin, mixinClass, mixinGetSet.values() );
        }



        cw.visitEnd();

        return cw.toByteArray();

    }

    private void buildMixinMethods( ClassWriter cw, String wrapperName, String mixin, Class mixinClass, Collection<Method> mixinMethods ) {
        for ( Method method : mixinMethods ) {
            String signature = buildSignature( method );
            {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                                 method.getName(),
                                 signature,
                                 null,
                                 null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), mixin, BuildUtils.getTypeDescriptor( mixinClass.getName() ) );
            int j = 1;
            for ( Class arg : method.getParameterTypes() ) {
                mv.visitVarInsn( BuildUtils.varType( arg.getName() ), j++ );
            }
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                BuildUtils.getInternalType( mixinClass.getName() ),
                                method.getName(),
                                signature );

            mv.visitInsn( BuildUtils.returnType( method.getReturnType().getName() ) );
            int stack = getStackSize( method ) ;
            mv.visitMaxs(stack, stack);
            mv.visitEnd();
            }
        }

    }


    private void buildHardGetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition proxy, ClassDefinition core ) {
        String fieldName = field.getName();
        String fieldType = field.getTypeName();
        String getter = BuildUtils.getterName( fieldName, fieldType );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                getter,
                "()" + BuildUtils.getTypeDescriptor( fieldType ),
                null,
                null);
        mv.visitCode();


        invokeExtractor( mv, masterName, proxy, core, field );

        if ( !BuildUtils.isPrimitive( fieldType ) ) {
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( fieldType ) );
        }

        mv.visitInsn( BuildUtils.returnType ( fieldType ) );
        mv.visitMaxs(2, 1);
        mv.visitEnd();

    }




    private void buildHardSetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition trait, ClassDefinition core ) {
        String fieldName = field.getName();
        String fieldType = field.getTypeName();
        String setter = "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                BuildUtils.setterName( fieldName, fieldType ),
                "(" + BuildUtils.getTypeDescriptor( fieldType ) + ")V",
                null,
                null);
        mv.visitCode();

        invokeInjector( mv, masterName, trait, core, field, false, 1 );

        mv.visitInsn(RETURN);
        mv.visitMaxs( 2 + BuildUtils.sizeOf( fieldType ),
                1 + BuildUtils.sizeOf( fieldType ) );
        mv.visitEnd();

    }




    private void buildSoftSetter( ClassVisitor cw, String fieldName, String type, String proxy, String core ) {
        String setter = BuildUtils.setterName( fieldName, type );


        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, setter, "(" + BuildUtils.getTypeDescriptor( type ) + ")V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( proxy ), "map", "Ljava/util/Map;");
        mv.visitLdcInsn(fieldName);
        mv.visitVarInsn( BuildUtils.varType( type ), 1);
        if ( BuildUtils.isPrimitive(type) ) {
            valueOf( mv, type );
        }
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitInsn(POP);
        mv.visitInsn(RETURN);
        mv.visitMaxs(2 + BuildUtils.sizeOf( type ), 1 + BuildUtils.sizeOf( type ));
        mv.visitEnd();

    }



    private void buildSoftGetter( ClassVisitor cw, String fieldName, String type, String proxy, String core ) {

        String getter = BuildUtils.getterName( fieldName, type );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, getter, "()"+ BuildUtils.getTypeDescriptor( type ), null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( proxy ), "map", "Ljava/util/Map;");
        mv.visitLdcInsn( fieldName );
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");

        if ( BuildUtils.isPrimitive( type ) ) {
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);
            Label l0 = new Label();
            mv.visitJumpInsn(IFNULL, l0);
            mv.visitVarInsn(ALOAD, 1);
            Label l1 = new Label();

            mv.visitJumpInsn(GOTO, l1);
            mv.visitLabel(l0);
            mv.visitInsn( BuildUtils.zero( type ) );

            valueOf( mv, type );
            mv.visitLabel(l1);

            promote( mv, type );

            mv.visitInsn( BuildUtils.returnType( type ) );
            mv.visitMaxs( 2, 2 );

        } else {
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( type ) );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 2, 1 );
        }

        mv.visitEnd();
    }





    public void buildEqualityMethods( ClassVisitor cw, String proxy, String core ) {

        String proxyType = BuildUtils.getInternalType( proxy );
        String coreType = BuildUtils.getTypeDescriptor( core );


        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "equals", "(Ljava/lang/Object;)Z", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            Label l0 = new Label();
            mv.visitJumpInsn(IF_ACMPNE, l0);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 1);
            Label l1 = new Label();
            mv.visitJumpInsn(IFNONNULL, l1);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l1);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z");
            Label l2 = new Label();
            mv.visitJumpInsn(IFNE, l2);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l2);

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, "org/drools/factmodel/traits/TraitProxy");
            mv.visitVarInsn(ASTORE, 2);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, "getFields", "()Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/factmodel/traits/TraitProxy", "getFields", "()Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z");

            mv.visitInsn(IRETURN);
            mv.visitMaxs(2, 3);
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "hashCode", "()I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, proxyType, "object", coreType);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I");
            mv.visitVarInsn(ISTORE, 1);
            mv.visitIntInsn(BIPUSH, 31);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IMUL);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, proxyType, "map", "Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I");
            mv.visitInsn(IADD);
            mv.visitVarInsn(ISTORE, 1);

            mv.visitLdcInsn( proxy );
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I");
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IMUL);
            mv.visitVarInsn(ISTORE, 1);

            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();

        }
        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V");
            mv.visitLdcInsn("(@" + proxy + ") : ");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, "getFields", "()Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "entrySet", "()Ljava/util/Set;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();

        }
    }

































































    private byte[] buildWrapperBytes(String name, String masterName, ClassDefinition core, ClassDefinition trait, long mask) {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;


        String internalWrapper  = BuildUtils.getInternalType(name);
        String internalProxy    = BuildUtils.getInternalType(masterName);
        String descrWrapper     = BuildUtils.getTypeDescriptor(name);
        String descrProxy       = BuildUtils.getTypeDescriptor(masterName);

        String internalCore     = BuildUtils.getInternalType(core.getClassName());
        String descrCore        = BuildUtils.getTypeDescriptor(core.getClassName());
        String internalTrait    = BuildUtils.getInternalType(trait.getClassName());
        String descrTrait       = BuildUtils.getTypeDescriptor(trait.getClassName());


        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER,
                internalWrapper,
                "Ljava/lang/Object;Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;Lorg/drools/factmodel/traits/MapWrapper;",
                "java/lang/Object",
                new String[]{"java/util/Map", "org/drools/factmodel/traits/MapWrapper"});

        cw.visitInnerClass("java/util/Map$Entry", "java/util/Map", "Entry", ACC_PUBLIC + ACC_STATIC + ACC_ABSTRACT + ACC_INTERFACE);


        for ( FieldDefinition fld : core.getFieldsDefinitions() ) {
            fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, fld.getName()+"_reader", "Lorg/drools/spi/InternalReadAccessor;", null, null);
            fv.visitEnd();
            fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, fld.getName()+"_writer", "Lorg/drools/spi/WriteAccessor;", null, null);
            fv.visitEnd();
        }


        {
            fv = cw.visitField(0, "object", descrCore, null, null);
            fv.visitEnd();
        }

        {
            fv = cw.visitField(0, "map", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null);
            fv.visitEnd();
        }

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                    "(" + descrCore + "Ljava/util/Map;)V",
                    "(" + descrCore + "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)V",
                    null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V" );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, internalWrapper, "object", descrCore );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitFieldInsn(PUTFIELD, internalWrapper, "map", "Ljava/util/Map;" );

            int stackSize = initSoftFields(mv, trait, mask, 2);

            mv.visitInsn(RETURN);
            mv.visitMaxs( 2 + stackSize,
                    3 );
            mv.visitEnd();

        }


        buildSize( cw, name, core.getClassName(), trait, core, mask );

        buildIsEmpty( cw, name, core.getClassName(), trait, core, mask );

        buildGet( cw, name, core.getClassName(), trait, core, mask );

        buildPut( cw, name, core.getClassName(), trait, core, mask );

        buildClear(cw, name, core.getClassName(), trait, core, mask);

        buildRemove(cw, name, core.getClassName(), trait, core, mask);

        buildContainsKey(cw, name, core.getClassName(), trait, core, mask);

        buildContainsValue(cw, name, core.getClassName(), trait, core, mask);

        buildKeyset(cw, name, core.getClassName(), trait, core, mask);

        buildValues(cw, name, core.getClassName(), trait, core, mask);

        buildEntryset(cw, name, core.getClassName(), trait, core, mask);

        buildCommonMethods( cw, name );


        cw.visitEnd();

        return cw.toByteArray();
    }










    private void buildRemove(ClassWriter cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );
        String internalCore = BuildUtils.getInternalType( coreName );
        String descrCore = BuildUtils.getTypeDescriptor( coreName );

        boolean hasPrimitiveFields = false;
        boolean hasObjectFields = false;

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "remove", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitLdcInsn( field.getName() );
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
            Label l1 = new Label();
            mv.visitJumpInsn(IFEQ, l1);

            invokeExtractor( mv, wrapperName, trait, core, field );

            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
               valueOf( mv, field.getTypeName() );
            }
            mv.visitVarInsn(ASTORE, 2);

            invokeInjector( mv, wrapperName, trait, core, field, true, 1);

            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l1);
        }

        int j = 0;
        int stack = 0;
        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
            boolean isSoftField = (mask & (1 << j++)) == 0;
            if ( isSoftField ) {
                stack = Math.max( stack, BuildUtils.sizeOf( field.getTypeName() ) );

                mv.visitLdcInsn( field.getName() );
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
                Label l2 = new Label();
                mv.visitJumpInsn(IFEQ, l2);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, internalWrapper, "map", "Ljava/util/Map;");
                mv.visitLdcInsn( field.getName() );
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
                mv.visitVarInsn(ASTORE, 2);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, internalWrapper, "map", "Ljava/util/Map;");
                mv.visitLdcInsn( field.getName() );
                mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );
                if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                    valueOf( mv, field.getTypeName() );
                }
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
                mv.visitInsn(POP);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitInsn(ARETURN);
                mv.visitLabel(l2);
            }
        }


        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalWrapper, "map", "Ljava/util/Map;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "remove", "(Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitVarInsn(ASTORE, 2);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARETURN);
        mv.visitMaxs( 4 + stack,
                3);
        mv.visitEnd();
    }




    private int initSoftFields( MethodVisitor mv, ClassDefinition trait, long mask, int varNum ) {
        int j = 0;
        int nonPrimitiveFields = 0;
        int stackSize = 0;
        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
            boolean isSoftField = (mask & (1 << j++)) == 0;
            if ( isSoftField ) {
                mv.visitVarInsn(ALOAD, varNum);
                mv.visitLdcInsn( field.getName() );
                mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );
                if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                    valueOf( mv, field.getTypeName() );
                    int size = BuildUtils.sizeOf( field.getTypeName() );
                    stackSize = Math.max( stackSize, size );
                } else {
                    stackSize = Math.max( stackSize, 2 );
                }
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
                mv.visitInsn(POP);
            }
        }
        return stackSize;
    }



    private void buildClear(ClassWriter cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );
        String internalCore = BuildUtils.getInternalType( coreName );
        String descrCore = BuildUtils.getTypeDescriptor( coreName );


        boolean hasPrimitiveFields = false;
        boolean hasObjectFields = false;
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "clear", "()V", null, null);
        mv.visitCode();

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                hasPrimitiveFields = true;
            } else {
                hasObjectFields = true;
            }
            invokeInjector( mv, wrapperName, trait, core, field, true, 1 );
        }

        int stack = 2;
        if ( hasPrimitiveFields ) {
            stack++;
        }
        if ( hasObjectFields ) {
            stack++;
        }

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalWrapper, "map", "Ljava/util/Map;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "clear", "()V");

        int num = initSoftFields(mv, trait, mask, 0);
        stack += num;


        mv.visitInsn(RETURN);
        mv.visitMaxs( stack , 1 );
        mv.visitEnd();


    }





    private void buildContainsValue(ClassWriter cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );
        String internalCore = BuildUtils.getInternalType( coreName );
        String descrCore = BuildUtils.getTypeDescriptor( coreName );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "containsValue", "(Ljava/lang/Object;)Z", null, null);
        mv.visitCode();


        // null check
        mv.visitVarInsn(ALOAD, 1);
        Label l99 = new Label();
        mv.visitJumpInsn(IFNONNULL, l99);

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {
                invokeExtractor( mv, wrapperName, trait, core, field );
                Label l1 = new Label();
                mv.visitJumpInsn(IFNONNULL, l1);
                mv.visitInsn(ICONST_1);
                mv.visitInsn(IRETURN);
                mv.visitLabel(l1);
            }
        }

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalWrapper, "map", "Ljava/util/Map;");
        mv.visitInsn(ACONST_NULL);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "containsValue", "(Ljava/lang/Object;)Z");
        mv.visitInsn(IRETURN);
        mv.visitLabel(l99);

        // non-null values check
        for ( FieldDefinition field : core.getFieldsDefinitions() )   {

            mv.visitVarInsn(ALOAD, 1);

            invokeExtractor( mv, wrapperName, trait, core, field );

            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                valueOf( mv, field.getTypeName() );
            }
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z");

            Label l0 = new Label();
            mv.visitJumpInsn(IFEQ, l0);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l0);

        }

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalWrapper, "map", "Ljava/util/Map;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "containsValue", "(Ljava/lang/Object;)Z");
        mv.visitInsn(IRETURN);
        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ? 3 : 2
                , 2);
        mv.visitEnd();

    }

    private void buildContainsKey(ClassWriter cw, String name, String className, ClassDefinition trait, ClassDefinition core, long mask) {
        String internalWrapper = BuildUtils.getInternalType( name );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "containsKey", "(Ljava/lang/Object;)Z", null, null);
        mv.visitCode();

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitLdcInsn( field.getName() );
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
            Label l0 = new Label();
            mv.visitJumpInsn(IFEQ, l0);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l0);
        }

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalWrapper, "map", "Ljava/util/Map;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "containsKey", "(Ljava/lang/Object;)Z");
        mv.visitInsn(IRETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }


    private void buildSize( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "size", "()I", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalWrapper, "map", "Ljava/util/Map;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "size", "()I");

        int n = core.getFieldsDefinitions().size();
        for ( int j = 0; j < n; j++ ) {
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IADD);
        }

        mv.visitInsn(IRETURN);
        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ? 2 : 1,
                1 );
        mv.visitEnd();
    }


    private void buildIsEmpty( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        boolean hasHardFields = core.getFieldsDefinitions().size() > 0;

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "isEmpty", "()Z", null, null);
        mv.visitCode();

        if ( ! hasHardFields ) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, internalWrapper, "map", "Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "isEmpty", "()Z");
        } else {
            mv.visitInsn(ICONST_0);
        }
        mv.visitInsn(IRETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }








    private void buildGet( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );
        String internalCore = BuildUtils.getInternalType( coreName );
        String descrCore = BuildUtils.getTypeDescriptor( coreName );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();


        if ( core.getFieldsDefinitions().size() > 0) {
            for ( FieldDefinition field : core.getFieldsDefinitions() ) {
                mv.visitLdcInsn( field.getName() );
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
                Label l0 = new Label();
                mv.visitJumpInsn(IFEQ, l0);

                invokeExtractor( mv, wrapperName, trait, core, field );

                if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                    valueOf( mv, field.getTypeName() );
                }
                mv.visitInsn(ARETURN);
                mv.visitLabel(l0);
            }

        }

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalWrapper, "map", "Ljava/util/Map;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }



    private void buildPut( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );
        String internalCore = BuildUtils.getInternalType( coreName );
        String descrCore = BuildUtils.getTypeDescriptor( coreName );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "put", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();

        if ( core.getFieldsDefinitions().size() > 0) {
            int j = 0;
            for ( FieldDefinition field : core.getFieldsDefinitions() ) {
                mv.visitLdcInsn( field.getName() );
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
                Label l1 = new Label();
                mv.visitJumpInsn(IFEQ, l1);


                mv.visitVarInsn(ALOAD, 2);
                if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                    promote( mv, field.getTypeName() );
                    mv.visitVarInsn( BuildUtils.storeType( field.getTypeName() ), 3 );
                    invokeInjector( mv, wrapperName, trait, core, field, false, 3 );
                } else {
                    invokeInjector( mv, wrapperName, trait, core, field, false, 2 );
                }

                mv.visitVarInsn(ALOAD, 2);
                mv.visitInsn(ARETURN);
                mv.visitLabel(l1);
            }

        }


        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalWrapper, "map", "Ljava/util/Map;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(4,5);
        mv.visitEnd();
    }




    private void buildEntryset( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );
        String internalCore = BuildUtils.getInternalType( coreName );
        String descrCore = BuildUtils.getTypeDescriptor( coreName );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "entrySet", "()Ljava/util/Set;", "()Ljava/util/Set<Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/Object;>;>;", null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, "java/util/HashSet");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashSet", "<init>", "()V");
        mv.visitVarInsn(ASTORE, 1);

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn( field.getName() );

            invokeExtractor( mv, wrapperName, trait, core, field );

            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                valueOf( mv, field.getTypeName() );
            }

            mv.visitMethodInsn(INVOKESTATIC, "org/drools/factmodel/traits/TraitProxy", "buildEntry", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/util/Map$Entry;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z");
            mv.visitInsn(POP);
        }

        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalWrapper, "map", "Ljava/util/Map;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "entrySet", "()Ljava/util/Set;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "addAll", "(Ljava/util/Collection;)Z");
        mv.visitInsn(POP);

        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARETURN);
        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ?  4 : 2,
                2);
        mv.visitEnd();


    }


    private void buildKeyset( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "keySet", "()Ljava/util/Set;", "()Ljava/util/Set<Ljava/lang/String;>;", null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, "java/util/HashSet");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashSet", "<init>", "()V");
        mv.visitVarInsn(ASTORE, 1);

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn( field.getName() );
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z");
            mv.visitInsn(POP);
        }

        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalWrapper, "map", "Ljava/util/Map;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "keySet", "()Ljava/util/Set;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "addAll", "(Ljava/util/Collection;)Z");
        mv.visitInsn(POP);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }



    private void buildValues( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );
        String internalCore = BuildUtils.getInternalType( coreName );
        String descrCore = BuildUtils.getTypeDescriptor( coreName );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "values", "()Ljava/util/Collection;", "()Ljava/util/Collection<Ljava/lang/Object;>;", null);
        mv.visitCode();

        mv.visitTypeInsn(NEW, "java/util/ArrayList");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
        mv.visitVarInsn(ASTORE, 1);


        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitVarInsn(ALOAD, 1);

            invokeExtractor( mv, wrapperName, trait, core, field );

            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                valueOf( mv, field.getTypeName() );
            }

            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Collection", "add", "(Ljava/lang/Object;)Z");
            mv.visitInsn(POP);
        }

        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalWrapper, "map", "Ljava/util/Map;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "values", "()Ljava/util/Collection;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Collection", "addAll", "(Ljava/util/Collection;)Z");
        mv.visitInsn(POP);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARETURN);

        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ? 3 : 2,
                2);
        mv.visitEnd();
    }




















    public void buildCommonMethods( ClassVisitor cw, String wrapper ) {

        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "put", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "equals", "(Ljava/lang/Object;)Z", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            Label l0 = new Label();
            mv.visitJumpInsn(IF_ACMPNE, l0);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, "org/drools/factmodel/traits/MapWrapper");
            mv.visitVarInsn(ASTORE, 2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapper ), "map", "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/factmodel/traits/MapWrapper", "getInnerMap", "()Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z");
            mv.visitInsn(IRETURN);
            mv.visitMaxs(2, 3);
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "hashCode", "()I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapper ), "map", "Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I");
            mv.visitInsn(IRETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "getInnerMap", "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapper ), "map", "Ljava/util/Map;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }


        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "putAll", "(Ljava/util/Map;)V", "(Ljava/util/Map<+Ljava/lang/String;+Ljava/lang/Object;>;)V", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "keySet", "()Ljava/util/Set;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "iterator", "()Ljava/util/Iterator;");
            mv.visitVarInsn(ASTORE, 2);
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
            Label l1 = new Label();
            mv.visitJumpInsn(IFEQ, l1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitVarInsn(ASTORE, 3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "put", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;");
            mv.visitInsn(POP);
            mv.visitJumpInsn(GOTO, l0);
            mv.visitLabel(l1);
            mv.visitInsn(RETURN);
            mv.visitMaxs(4, 4);
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V");
            mv.visitLdcInsn("[[[[");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "entrySet", "()Ljava/util/Set;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;");
            mv.visitLdcInsn("]]]]");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }




    }

























    private void valueOf( MethodVisitor mv, String type ) {
        mv.visitMethodInsn( INVOKESTATIC,
                BuildUtils.getInternalType( BuildUtils.box( type ) ),
                "valueOf",
                "(" + BuildUtils.getTypeDescriptor( type ) + ")" +
                        BuildUtils.getTypeDescriptor( BuildUtils.box( type ) )
        );

    }


    private void promote( MethodVisitor mv, String fieldType ) {
        mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( BuildUtils.box( fieldType ) ) );
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                BuildUtils.getInternalType( BuildUtils.box( fieldType ) ),
                fieldType + "Value",
                "()"+ BuildUtils.getTypeDescriptor( fieldType ) );
    }


    private void invokeExtractor( MethodVisitor mv, String masterName, ClassDefinition source, ClassDefinition target, FieldDefinition field ) {
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


    private void invokeInjector( MethodVisitor mv, String masterName, ClassDefinition source, ClassDefinition target, FieldDefinition field, boolean toNull, int pointer ) {
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

        byte[] wrapper = buildCoreWrapperBytes( coreKlazz );

        JavaDialectRuntimeData data = ((JavaDialectRuntimeData) getPackage( pack ).getDialectRuntimeRegistry().
                getDialectData( "java" ));
        data.write(JavaDialectRuntimeData.convertClassToResourcePath(wrapperName), wrapper);
        data.onBeforeExecute();

        Class<CoreWrapper<K>> wrapperClass = (Class<CoreWrapper<K>>) ruleBase.getRootClassLoader().loadClass( wrapperName, true );
        return wrapperClass;
    }



    private byte[] buildCoreWrapperBytes(Class<K> coreKlazz) throws IOException {

        String coreName = coreKlazz.getName();
        String wrapperName = coreName + "Wrapper";

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER,
                 BuildUtils.getInternalType( wrapperName ),
                 BuildUtils.getTypeDescriptor( coreName) +
                         "Lorg/drools/factmodel/traits/CoreWrapper<" + BuildUtils.getTypeDescriptor( coreName ) + ">;",
                 BuildUtils.getInternalType( coreName ),
                 new String[]{"org/drools/factmodel/traits/CoreWrapper"});

        {
            fv = cw.visitField(ACC_PRIVATE, "core", BuildUtils.getTypeDescriptor( coreName ), null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, ITraitable.MAP_FIELD_NAME, "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, ITraitable.TRAITSET_FIELD_NAME, "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Class;>;", null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, BuildUtils.getInternalType( coreName ), "<init>", "()V");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/util/HashMap");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
            mv.visitFieldInsn( PUTFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ITraitable.MAP_FIELD_NAME,
                               "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/util/HashMap");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
            mv.visitFieldInsn( PUTFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ITraitable.TRAITSET_FIELD_NAME,
                               "Ljava/util/Map;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getDynamicProperties", "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ITraitable.MAP_FIELD_NAME,
                               "Ljava/util/Map;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getTraits", "()Ljava/util/Map;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ITraitable.TRAITSET_FIELD_NAME,
                               "Ljava/util/Map;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod(ACC_PUBLIC, "init", "("+ BuildUtils.getTypeDescriptor( coreName ) +")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn( PUTFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               "core",
                               BuildUtils.getTypeDescriptor( coreName ) );
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }


        Method[] ms = coreKlazz.getMethods();
        for ( Method method : ms ) {
            if ( Modifier.isFinal(method.getModifiers()) ) {
                continue;
            }

            String signature = buildSignature( method );
            {
            mv = cw.visitMethod( ACC_PUBLIC,
                                 method.getName(),
                                 signature,
                                 null,
                                 null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), "core", BuildUtils.getTypeDescriptor( coreName ) );
            int j = 1;
            for ( Class arg : method.getParameterTypes() ) {
                mv.visitVarInsn( BuildUtils.varType( arg.getName() ), j++ );
            }
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                BuildUtils.getInternalType( coreName ),
                                method.getName(),
                                signature );

            mv.visitInsn( BuildUtils.returnType( method.getReturnType().getName() ) );
            int stack = getStackSize( method ) ;
            mv.visitMaxs(stack, stack);
            mv.visitEnd();
            }

        }

         {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "init", "(Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( coreName ) );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                BuildUtils.getInternalType( wrapperName ),
                                "init",
                                "(" + BuildUtils.getTypeDescriptor( coreName ) + ")V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    private String buildSignature( Method method ) {
        String sig = "(";
            for ( Class arg : method.getParameterTypes() ) {
                sig += BuildUtils.getTypeDescriptor( arg.getName() );
            }
        sig += ")";
        sig += BuildUtils.getTypeDescriptor( method.getReturnType().getName() );
        return sig;
    }


    private int getStackSize( Method m ) {
        int stack = 1;
        for ( Class klass : m.getParameterTypes() ) {
            stack += BuildUtils.sizeOf( klass.getName() );
        }
        return stack;
    }



}
