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

import org.drools.core.util.asm.ClassFieldInspector;
import org.drools.definition.type.FactField;
import org.drools.factmodel.BuildUtils;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;
import org.mvel2.asm.*;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class TraitTripleProxyClassBuilderImpl implements TraitProxyClassBuilder {


    private ClassDefinition trait;

    protected ClassDefinition getTrait() {
        return trait;
    }

    public void init( ClassDefinition trait ) {
        this.trait = trait;
    }



    public byte[] buildClass( ClassDefinition core ) throws IOException,
            IntrospectionException,
            SecurityException,
            IllegalArgumentException,
            ClassNotFoundException,
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException,
            NoSuchFieldException {


        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        // get the method bitmask
        long mask = TraitRegistry.getInstance().getFieldMask( getTrait().getName(), core.getDefinedClass().getName() );

        String name = TraitFactory.getPropertyWrapperName( getTrait(), core );
        String masterName = TraitFactory.getProxyName( getTrait(), core );


        String internalWrapper  = BuildUtils.getInternalType( name );
        String internalProxy    = BuildUtils.getInternalType( masterName );
        String descrWrapper     = BuildUtils.getTypeDescriptor( name );
        String descrProxy       = BuildUtils.getTypeDescriptor( masterName );

        String internalCore     = BuildUtils.getInternalType( core.getClassName() );
        String descrCore        = BuildUtils.getTypeDescriptor( core.getClassName() );
        String internalTrait    = BuildUtils.getInternalType( getTrait().getClassName() );
        String descrTrait       = BuildUtils.getTypeDescriptor( getTrait().getClassName() );


        Class mixinClass = null;
        String mixin = null;
        Set<Method> mixinMethods = new HashSet<Method>();
        Map<String,Method> mixinGetSet = new HashMap<String,Method>();
        try {
            if ( getTrait().getDefinedClass() != null ) {
                Trait annTrait = getAnnotation( getTrait().getDefinedClass(), Trait.class );
                if ( hasImpl( annTrait ) ) {
                    mixinClass = annTrait.impl();
                    mixin = mixinClass.getSimpleName().substring(0,1).toLowerCase() + mixinClass.getSimpleName().substring(1);
                    ClassFieldInspector cfi = new ClassFieldInspector( mixinClass );

                    for ( Method m : mixinClass.getMethods() ) {
                        try {
                            getTrait().getDefinedClass().getMethod(m.getName(), m.getParameterTypes() );
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
            fv = cw.visitField(ACC_PRIVATE, "store", "Lorg/drools/core/util/TripleStore;", null, null);
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
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + descrCore + "Lorg/drools/core/util/TripleStore;)V", null, null);
            mv.visitCode();

            buildConstructorCore( cw, mv, internalProxy, internalWrapper, internalCore, descrCore, mixin, mixinClass );

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


        buildProxyAccessors( mask, cw, masterName, core, mixinGetSet );

        boolean hasKeys = false;
        for ( FactField ff : getTrait().getFields() ) {
            if ( ff.isKey() ) {
                hasKeys = true;
                break;
            }
        }
        if ( ! hasKeys ) {
            buildEqualityMethods( cw, masterName, core.getClassName() );
        } else {
            buildKeyedEqualityMethods( cw, getTrait(), masterName, core.getClassName() );
        }

        if ( mixinClass != null ) {
            buildMixinMethods( cw, masterName, mixin, mixinClass, mixinMethods );
            buildMixinMethods( cw, masterName, mixin, mixinClass, mixinGetSet.values() );
        }


        buildCommonMethods( cw, masterName );


        buildExtendedMethods( cw, getTrait(), core );


        cw.visitEnd();

        return cw.toByteArray();

    }

    private boolean hasImpl( Trait annTrait ) {
        return annTrait != null && ! annTrait.impl().equals( Trait.NullMixin.class );
    }


    private <K extends Annotation> K getAnnotation( Class klass, Class<K> annotationClass ) {
        if ( klass.equals( Thing.class ) ) {
            return null;
        }
        K ann = (K) klass.getAnnotation( annotationClass );

        if ( ann == null ) {
            for ( Class sup : klass.getInterfaces() ) {
                ann = getAnnotation( sup, annotationClass );
                if ( ann != null ) {
                    return ann;
                }
            }
            return null;
        } else {
            return ann;
        }
    }


    protected void buildConstructorCore( ClassWriter cw, MethodVisitor mv, String internalProxy, String internalWrapper, String internalCore, String descrCore, String mixin, Class mixinClass ) {


        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TraitProxy", "<init>", "()V");
        if ( mixinClass != null ) {
            try {
//                    Constructor con = mixinClass.getConstructor( trait.getDefinedClass() );
                Class actualArg = getPossibleConstructor( mixinClass, trait.getDefinedClass() );

                mv.visitVarInsn(ALOAD, 0);
                mv.visitTypeInsn(NEW, BuildUtils.getInternalType( mixinClass.getName() ) );
                mv.visitInsn(DUP);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn( INVOKESPECIAL,
                        BuildUtils.getInternalType( mixinClass.getName() ),
                        "<init>",
                        "("+ BuildUtils.getTypeDescriptor( actualArg.getName() ) + ")V");
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
        mv.visitFieldInsn(PUTFIELD, internalProxy, "store", "Lorg/drools/core/util/TripleStore;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(NEW, internalWrapper);
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, internalWrapper, "<init>", "(" + descrCore + "Lorg/drools/core/util/TripleStore;)V");
        mv.visitFieldInsn(PUTFIELD, internalProxy, "fields", "Ljava/util/Map;");

        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(NEW, "org/drools/factmodel/traits/TripleBasedBean");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedBean", "<init>", "(Ljava/lang/Object;Lorg/drools/core/util/TripleStore;)V");
        mv.visitMethodInsn(INVOKEVIRTUAL, internalCore, "setDynamicProperties", "(Ljava/util/Map;)V");

        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(NEW, "org/drools/factmodel/traits/TripleBasedTypes");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedTypes", "<init>", "(Ljava/lang/Object;Lorg/drools/core/util/TripleStore;)V");
        mv.visitMethodInsn(INVOKEVIRTUAL, internalCore, "setTraitMap", "(Ljava/util/Map;)V");

    }

    private Class getPossibleConstructor(Class klass, Class arg) throws NoSuchMethodException {

        Constructor[] ctors = klass.getConstructors();

        for (Constructor c : ctors) {
            Class[] cpars = c.getParameterTypes();

            if ( cpars.length != 1 || ! cpars[0].isAssignableFrom( arg ) ) {
                continue;
            }

            return cpars[0];
        }
        throw new NoSuchMethodException("Constructor for " + klass + " using " + arg + " not found ");
    }



    protected void buildProxyAccessors( long mask, ClassWriter cw, String masterName, ClassDefinition core, Map<String,Method> mixinGetSet) {
        int j = 0;

        for ( FieldDefinition field : getTrait().getFieldsDefinitions() ) {
            boolean isSoftField = TraitRegistry.isSoftField( field, j++, mask );
            buildProxyAccessor( mask, cw, masterName, core, mixinGetSet, field, isSoftField );
        }

    }


    protected void buildProxyAccessor( long mask, ClassWriter cw, String masterName, ClassDefinition core, Map<String,Method> mixinGetSet, FieldDefinition field, boolean isSoftField ) {
        FieldVisitor fv;

        if ( isSoftField ) {
            if ( ! mixinGetSet.containsKey( BuildUtils.getterName( field.getName(), field.getTypeName() ) ) ) {
                buildSoftGetter( cw, field, masterName );
                buildSoftSetter( cw, field, masterName );
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

            buildHardGetter( cw, field, masterName, getTrait(), core );
            buildHardSetter( cw, field, masterName, getTrait(), core );

        }
    }



    private void buildMixinMethods( ClassWriter cw, String wrapperName, String mixin, Class mixinClass, Collection<Method> mixinMethods ) {
        for ( Method method : mixinMethods ) {
            String signature = TraitFactory.buildSignature( method );
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
                int stack = TraitFactory.getStackSize( method ) ;
                mv.visitMaxs(stack, stack);
                mv.visitEnd();
            }
        }

    }








    protected void buildHardGetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition proxy, ClassDefinition core) {
        buildHardGetter( cw, field, masterName, proxy, core, BuildUtils.getterName( field.getName(), field.getTypeName() ), false );
    }

    protected void buildHardGetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition proxy, ClassDefinition core, String getterName, boolean protect ) {
        String fieldName = field.getName();
        String fieldType = field.getTypeName();


        MethodVisitor mv = cw.visitMethod( protect ? ACC_PROTECTED : ACC_PUBLIC,
                getterName,
                "()" + BuildUtils.getTypeDescriptor( fieldType ),
                null,
                null);
        mv.visitCode();


        TraitFactory.invokeExtractor( mv, masterName, proxy, core, field );

        if ( !BuildUtils.isPrimitive( fieldType ) ) {
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( fieldType ) );
        }

        mv.visitInsn( BuildUtils.returnType ( fieldType ) );
        mv.visitMaxs(2, 1);
        mv.visitEnd();

    }



    protected void buildHardSetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition trait, ClassDefinition core ) {
        buildHardSetter( cw, field, masterName, trait, core, BuildUtils.setterName( field.getName(), field.getTypeName() ), false );
    }

    protected void buildHardSetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition trait, ClassDefinition core, String setterName, boolean protect ) {
        String fieldName = field.getName();
        String fieldType = field.getTypeName();

        MethodVisitor mv = cw.visitMethod( protect ? ACC_PROTECTED : ACC_PUBLIC,
                setterName,
                "(" + BuildUtils.getTypeDescriptor( fieldType ) + ")V",
                null,
                null);
        mv.visitCode();

        TraitFactory.invokeInjector( mv, masterName, trait, core, field, false, 1 );

        mv.visitInsn(RETURN);
        mv.visitMaxs( 2 + BuildUtils.sizeOf( fieldType ),
                1 + BuildUtils.sizeOf( fieldType ) );
        mv.visitEnd();

    }


    protected void buildSoftSetter( ClassVisitor cw, FieldDefinition field, String proxy ) {
        buildSoftSetter( cw, field, proxy, BuildUtils.setterName( field.getName(), field.getTypeName() ), false );
    }

    protected void buildSoftSetter( ClassVisitor cw, FieldDefinition field, String proxy, String setterName, boolean protect  ) {
        String fieldName = field.getName();
        String type = field.getTypeName();

        MethodVisitor mv = cw.visitMethod( protect ? ACC_PROTECTED : ACC_PUBLIC,
                setterName,
                "(" + BuildUtils.getTypeDescriptor( type ) + ")V",
                null,
                null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( proxy ), "store", "Lorg/drools/core/util/TripleStore;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(fieldName);
        mv.visitVarInsn( BuildUtils.varType( type ), 1);
        if ( BuildUtils.isPrimitive(type) ) {
            TraitFactory.valueOf( mv, type );
        }
        mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( proxy ), "property", "(Ljava/lang/String;Ljava/lang/Object;)Lorg/drools/core/util/TripleImpl;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/core/util/TripleStore", "put", "(Lorg/drools/core/util/Triple;)Z");

        mv.visitInsn(POP);
        mv.visitInsn(RETURN);
        mv.visitMaxs(3 + BuildUtils.sizeOf( type ), 1 + BuildUtils.sizeOf( type ));
        mv.visitEnd();

    }



    protected void buildSoftGetter( ClassVisitor cw, FieldDefinition field, String proxy ) {
        buildSoftGetter( cw, field, proxy, BuildUtils.getterName( field.getName(), field.getTypeName() ), false );
    }

    protected void buildSoftGetter( ClassVisitor cw, FieldDefinition field, String proxy, String getterName, boolean protect ) {
        String fieldName = field.getName();
        String type = field.getTypeName();

        MethodVisitor mv = cw.visitMethod( protect ? ACC_PROTECTED : ACC_PUBLIC,
                getterName,
                "()"+ BuildUtils.getTypeDescriptor( type ),
                null,
                null );
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( proxy ), "store", "Lorg/drools/core/util/TripleStore;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn( fieldName );

        mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( proxy ), "propertyKey", "(Ljava/lang/String;)Lorg/drools/core/util/TripleImpl;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/core/util/TripleStore", "get", "(Lorg/drools/core/util/Triple;)Lorg/drools/core/util/Triple;");


        mv.visitVarInsn(ASTORE, 1);
        mv.visitVarInsn(ALOAD, 1);
        Label l0 = new Label();

        mv.visitJumpInsn(IFNULL, l0);
        mv.visitVarInsn(ALOAD, 1);

        mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/core/util/Triple", "getValue", "()Ljava/lang/Object;");
        Label l1 = new Label();
        mv.visitJumpInsn(IFNONNULL, l1);
        mv.visitLabel(l0);
        mv.visitInsn( BuildUtils.zero( type ) );
        mv.visitInsn( BuildUtils.returnType( type ) );
        mv.visitLabel(l1);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/core/util/Triple", "getValue", "()Ljava/lang/Object;");

        if ( BuildUtils.isPrimitive( type ) ) {
            TraitFactory.promote( mv, type );
        } else {
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( type ) );
        }
        mv.visitInsn( BuildUtils.returnType( type ) );
        mv.visitMaxs(3, 2);


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
            mv.visitTypeInsn(CHECKCAST, "org/drools/factmodel/traits/TraitProxy");
            mv.visitVarInsn(ASTORE, 2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, "getFields", "()Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/factmodel/traits/TraitProxy", "getFields", "()Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z");
            Label l1 = new Label();
            mv.visitJumpInsn(IFNE, l1);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, "getObject", "()Ljava/lang/Object;");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/factmodel/traits/TraitProxy", "getObject", "()Ljava/lang/Object;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z");
            Label l2 = new Label();
            mv.visitJumpInsn(IFNE, l2);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l2);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(2, 3);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "hashCode", "()I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TraitProxy", "hashCode", "()I");
            mv.visitVarInsn(ISTORE, 1);
            mv.visitIntInsn(BIPUSH, 31);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IMUL);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, "getObject", "()Ljava/lang/Object;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I");
            mv.visitInsn(IADD);
            mv.visitVarInsn(ISTORE, 1);
            mv.visitIntInsn(BIPUSH, 31);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IMUL);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, "getFields", "()Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I");
            mv.visitInsn(IADD);
            mv.visitVarInsn(ISTORE, 1);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }


    }






    public void buildKeyedEqualityMethods( ClassVisitor cw, ClassDefinition trait, String proxy, String core ) {

        String proxyType = BuildUtils.getInternalType( proxy );
        String coreType = BuildUtils.getTypeDescriptor( core );

        buildKeyedEquals( cw, trait, proxyType );
        buildKeyedHashCode( cw, trait, proxyType );

    }




    protected void buildKeyedEquals( ClassVisitor cw,
                                     ClassDefinition classDef,
                                     String proxyType ) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "equals", "(Ljava/lang/Object;)Z", null, null);
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
        mv.visitJumpInsn(IFNULL, l1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
        Label l2 = new Label();
        mv.visitJumpInsn(IF_ACMPEQ, l2);
        mv.visitLabel(l1);
        mv.visitInsn(ICONST_0);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l2);


        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, proxyType );
        mv.visitVarInsn(ASTORE, 2);

        int x = 2;

        int count = 0;

        for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
            if ( field.isKey() ) {
                count++;


                if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {

                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    Label l11 = new Label();
                    mv.visitJumpInsn(IFNULL, l11);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( field.getTypeName() ), "equals", "(Ljava/lang/Object;)Z");
                    Label l12 = new Label();
                    mv.visitJumpInsn(IFNE, l12);
                    Label l13 = new Label();
                    mv.visitJumpInsn(GOTO, l13);
                    mv.visitLabel(l11);
                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitJumpInsn(IFNULL, l12);
                    mv.visitLabel(l13);
                    mv.visitInsn(ICONST_0);
                    mv.visitInsn(IRETURN);
                    mv.visitLabel(l12);

                } else if ( "double".equals( field.getTypeName() ) ) {

                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "compare", "(DD)I");
                    Label l5 = new Label();
                    mv.visitJumpInsn(IFEQ, l5);
                    mv.visitInsn(ICONST_0);
                    mv.visitInsn(IRETURN);
                    mv.visitLabel(l5);

                    x = Math.max( x, 4 );

                } else if ( "float".equals( field.getTypeName() ) ) {

                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "compare", "(FF)I");
                    Label l6 = new Label();
                    mv.visitJumpInsn(IFEQ, l6);
                    mv.visitInsn(ICONST_0);
                    mv.visitInsn(IRETURN);
                    mv.visitLabel(l6);


                }  else if ( "long".equals( field.getTypeName() ) ) {

                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitInsn(LCMP);
                    Label l8 = new Label();
                    mv.visitJumpInsn(IFEQ, l8);
                    mv.visitInsn(ICONST_0);
                    mv.visitInsn(IRETURN);
                    mv.visitLabel(l8);

                    x = Math.max( x, 4 );

                } else {

                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    Label l4 = new Label();
                    mv.visitJumpInsn(IF_ICMPEQ, l4);
                    mv.visitInsn(ICONST_0);
                    mv.visitInsn(IRETURN);
                    mv.visitLabel(l4);

                }

            }
        }

        mv.visitInsn(ICONST_1);
        mv.visitInsn(IRETURN);
        mv.visitMaxs( x, 3 );
        mv.visitEnd();
    }

    protected void buildKeyedHashCode( ClassVisitor cw,
                                       ClassDefinition classDef,
                                       String proxyType ) {

        MethodVisitor mv;

        {
            mv = cw.visitMethod(ACC_PUBLIC, "hashCode", "()I", null, null);
            mv.visitCode();
            mv.visitIntInsn(BIPUSH, 31);
            mv.visitVarInsn(ISTORE, 1);

            int count = 0;
            int x = 2;
            int y = 2;
            for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
                if ( field.isKey() ) {
                    count++;

                    if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {

                        mv.visitIntInsn(BIPUSH, 31);
                        mv.visitVarInsn(ILOAD, 1);
                        mv.visitInsn(IMUL);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() )) ;
                        Label l8 = new Label();
                        mv.visitJumpInsn(IFNULL, l8);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() )) ;
                        mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( field.getTypeName() ), "hashCode", "()I");
                        Label l9 = new Label();
                        mv.visitJumpInsn(GOTO, l9);
                        mv.visitLabel(l8);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(l9);
                        mv.visitInsn(IADD);
                        mv.visitVarInsn(ISTORE, 1);

                    } else if ( "double".equals( field.getTypeName() ) ) {


                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() )) ;
                        mv.visitInsn(DCONST_0);
                        mv.visitInsn(DCMPL);
                        Label l2 = new Label();
                        mv.visitJumpInsn(IFEQ, l2);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() )) ;
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "doubleToLongBits", "(D)J");
                        Label l3 = new Label();
                        mv.visitJumpInsn(GOTO, l3);
                        mv.visitLabel(l2);
                        mv.visitInsn(LCONST_0);
                        mv.visitLabel(l3);
                        mv.visitVarInsn(LSTORE, 2);
                        mv.visitIntInsn(BIPUSH, 31);
                        mv.visitVarInsn(ILOAD, 1);
                        mv.visitInsn(IMUL);
                        mv.visitVarInsn(LLOAD, 2);
                        mv.visitVarInsn(LLOAD, 2);
                        mv.visitIntInsn(BIPUSH, 32);
                        mv.visitInsn(LUSHR);
                        mv.visitInsn(LXOR);
                        mv.visitInsn(L2I);
                        mv.visitInsn(IADD);
                        mv.visitVarInsn(ISTORE, 1);

                        x = Math.max( 6, x );
                        y = Math.max( 4, y );

                    } else if ( "boolean".equals( field.getTypeName() ) ) {

                        mv.visitIntInsn(BIPUSH, 31);
                        mv.visitVarInsn(ILOAD, 1);
                        mv.visitInsn(IMUL);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() )) ;
                        Label l4 = new Label();
                        mv.visitJumpInsn(IFEQ, l4);
                        mv.visitInsn(ICONST_1);
                        Label l5 = new Label();
                        mv.visitJumpInsn(GOTO, l5);
                        mv.visitLabel(l4);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(l5);
                        mv.visitInsn(IADD);
                        mv.visitVarInsn(ISTORE, 1);

                    } else if ( "float".equals( field.getTypeName() ) ) {

                        mv.visitIntInsn(BIPUSH, 31);
                        mv.visitVarInsn(ILOAD, 1);
                        mv.visitInsn(IMUL);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() )) ;
                        mv.visitInsn(FCONST_0);
                        mv.visitInsn(FCMPL);
                        Label l6 = new Label();
                        mv.visitJumpInsn(IFEQ, l6);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() )) ;
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "floatToIntBits", "(F)I");
                        Label l7 = new Label();
                        mv.visitJumpInsn(GOTO, l7);
                        mv.visitLabel(l6);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(l7);
                        mv.visitInsn(IADD);
                        mv.visitVarInsn(ISTORE, 1);

                        x = Math.max( 3, x );

                    }  else if ( "long".equals( field.getTypeName() ) ) {

                        mv.visitIntInsn(BIPUSH, 31);
                        mv.visitVarInsn(ILOAD, 1);
                        mv.visitInsn(IMUL);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() )) ;
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() )) ;
                        mv.visitIntInsn(BIPUSH, 32);
                        mv.visitInsn(LUSHR);
                        mv.visitInsn(LXOR);
                        mv.visitInsn(L2I);
                        mv.visitInsn(IADD);
                        mv.visitVarInsn(ISTORE, 1);

                        x = Math.max( 6, x );

                    } else {

                        mv.visitIntInsn(BIPUSH, 31);
                        mv.visitVarInsn(ILOAD, 1);
                        mv.visitInsn(IMUL);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) ) ;
                        mv.visitInsn(IADD);
                        mv.visitVarInsn(ISTORE, 1);

                    }
                }

            }
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IRETURN);
            mv.visitMaxs( x, y );
            mv.visitEnd();
        }
    }





    protected void buildCommonMethods( ClassWriter cw, String proxy ) {
        MethodVisitor mv;
//        {
//            mv = cw.visitMethod(ACC_PROTECTED, "propertyKey", "(Ljava/lang/String;)Lorg/drools/core/util/TripleImpl;", null, null);
//            mv.visitCode();
//            mv.visitTypeInsn(NEW, "org/drools/core/util/TripleImpl");
//            mv.visitInsn(DUP);
//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( proxy ), "getObject", "()Ljava/lang/Object;");
//            mv.visitVarInsn(ALOAD, 1);
//            mv.visitFieldInsn(GETSTATIC, "org/drools/runtime/rule/Variable", "v", "Lorg/drools/runtime/rule/Variable;");
//            mv.visitMethodInsn(INVOKESPECIAL, "org/drools/core/util/TripleImpl", "<init>", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V");
//            mv.visitInsn(ARETURN);
//            mv.visitMaxs(5, 2);
//            mv.visitEnd();
//        }
//        {
//            mv = cw.visitMethod(ACC_PROTECTED, "property", "(Ljava/lang/String;Ljava/lang/Object;)Lorg/drools/core/util/TripleImpl;", null, null);
//            mv.visitCode();
//            mv.visitTypeInsn(NEW, "org/drools/core/util/TripleImpl");
//            mv.visitInsn(DUP);
//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( proxy ), "getObject", "()Ljava/lang/Object;");
//            mv.visitVarInsn(ALOAD, 1);
//            mv.visitVarInsn(ALOAD, 2);
//            mv.visitMethodInsn(INVOKESPECIAL, "org/drools/core/util/TripleImpl", "<init>", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V");
//            mv.visitInsn(ARETURN);
//            mv.visitMaxs(5, 3);
//            mv.visitEnd();
//        }

        {
            mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V");
            mv.visitLdcInsn("(@" + proxy + ") : ");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( proxy ), "getFields", "()Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "entrySet", "()Ljava/util/Set;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();

        }

    }



    protected void buildExtendedMethods(ClassWriter cw, ClassDefinition trait, ClassDefinition core ) {
        // empty here, left for subclasses to extend
    }



}