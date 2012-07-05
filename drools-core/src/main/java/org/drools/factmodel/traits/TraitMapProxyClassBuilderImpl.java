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
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.WriteAccessor;
import org.mvel2.asm.*;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TraitMapProxyClassBuilderImpl implements TraitProxyClassBuilder, Serializable {


    private transient ClassDefinition trait;
    
    private transient Class<?> proxyBaseClass;

    protected ClassDefinition getTrait() {
        return trait;
    }

    public void init( ClassDefinition trait, Class<?> baseClass ) {
        this.trait = trait;
        this.proxyBaseClass = baseClass;
    }


    private boolean hasImpl( Trait annTrait ) {
        return annTrait != null && ! annTrait.impl().equals( Trait.NullMixin.class );
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


        ClassWriter cw = new ClassWriter( 0 );
        FieldVisitor fv;
        MethodVisitor mv;

        // get the method bitmask
        long mask = TraitRegistry.getInstance().getFieldMask( getTrait().getName(), core.getDefinedClass().getName() );

        String name = TraitFactory.getPropertyWrapperName( getTrait(), core );
        String masterName = TraitFactory.getProxyName( getTrait(), core );


        String internalWrapper  = BuildUtils.getInternalType( name );
        String internalProxy    = BuildUtils.getInternalType( masterName );

        String descrCore        = Type.getDescriptor( core.getDefinedClass() );
        String internalCore     = Type.getInternalName( core.getDefinedClass() );
        String internalTrait    = Type.getInternalName( getTrait().getDefinedClass() );


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



        cw.visit( V1_5, ACC_PUBLIC + ACC_SUPER,
                  internalProxy,
                  null,
                  Type.getInternalName( proxyBaseClass ),
                  new String[]{ internalTrait, Type.getInternalName( Serializable.class ) } );

        {
            fv = cw.visitField( ACC_PUBLIC + ACC_FINAL, "object", descrCore, null, null );
            fv.visitEnd();
        }
        {
            fv = cw.visitField( ACC_PUBLIC + ACC_FINAL, "map", Type.getDescriptor( Map.class ), "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null );
            fv.visitEnd();
        }
        if ( mixinClass != null ) {
            {
                fv = cw.visitField( ACC_PRIVATE,
                        mixin,
                        BuildUtils.getTypeDescriptor( mixinClass.getName() ),
                        null, null );
                fv.visitEnd();
            }
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "<init>", "()V", null, null );
            mv.visitCode();

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( proxyBaseClass ), "<init>", "()V" );

            mv.visitInsn( RETURN );
            mv.visitMaxs( 1, 1 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "<init>", "(" + descrCore + Type.getDescriptor( Map.class ) + ")V", "(" + descrCore + "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)V", null);
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( proxyBaseClass ), "<init>", "()V" );
            if ( mixinClass != null ) {
                try {
                    Class actualArg = getPossibleConstructor( mixinClass, trait.getDefinedClass() );

                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitTypeInsn( NEW, Type.getInternalName( mixinClass ) );
                    mv.visitInsn( DUP );
                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKESPECIAL,
                            Type.getInternalName( mixinClass ),
                            "<init>",
                            "("+ Type.getDescriptor( actualArg ) + ")V");
                    mv.visitFieldInsn( PUTFIELD,
                            internalProxy,
                            mixin,
                            Type.getDescriptor( mixinClass ) );
                } catch ( NoSuchMethodException nsme ) {
                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitTypeInsn( NEW, Type.getInternalName( mixinClass ) );
                    mv.visitInsn( DUP );
                    mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( mixinClass ), "<init>", "()V" );
                    mv.visitFieldInsn( PUTFIELD,
                            internalProxy,
                            mixin,
                            Type.getDescriptor( mixinClass ) );
                }

            }
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "object", descrCore );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "map", Type.getDescriptor( Map.class ) );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitTypeInsn( NEW, internalWrapper );
            mv.visitInsn( DUP );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKESPECIAL, internalWrapper, "<init>", "(" + descrCore + Type.getDescriptor( Map.class ) + ")V" );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "fields", Type.getDescriptor( Map.class ) );
            mv.visitInsn( RETURN );
            mv.visitMaxs( 5, 3 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "getCore", "()" + descrCore + "", null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, internalProxy, "object", descrCore );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 1, 1 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getObject", "()" + Type.getDescriptor( Object.class ), null, null);
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, internalProxy, "object", descrCore );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 1, 1 );
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "getCore", "()" + Type.getDescriptor( Object.class ), null, null );
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, internalProxy, "getCore", "()" + descrCore);
            mv.visitInsn(ARETURN);
            mv.visitMaxs( 1, 1 );
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC, "writeExternal", "(" + Type.getDescriptor( ObjectOutput.class ) + ")V", null, new String[] { Type.getInternalName( IOException.class ) } );
            mv.visitCode();

            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, internalProxy, "getObject", "()" + Type.getDescriptor( Object.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectOutput.class ), "writeObject", "(" + Type.getDescriptor( Object.class ) + ")V" );


            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, internalProxy, "map", Type.getDescriptor( Map.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectOutput.class ), "writeObject", "(" + Type.getDescriptor( Object.class ) + ")V" );

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( proxyBaseClass ), "writeExternal", "(" + Type.getDescriptor( ObjectOutput.class ) + ")V" );


            mv.visitInsn( RETURN );
            mv.visitMaxs( 2, 2 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "readExternal", "(" + Type.getDescriptor( ObjectInput.class )+ ")V", 
                                 null, new String[] { Type.getInternalName( IOException.class ), Type.getInternalName( ClassNotFoundException.class ) } );
            mv.visitCode();

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectInput.class ), "readObject", "()" + Type.getDescriptor( Object.class ) );
            mv.visitTypeInsn( CHECKCAST, internalCore );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "object", descrCore );


            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectInput.class ), "readObject", "()" + Type.getDescriptor( Object.class ) );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( Map.class ) );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "map", Type.getDescriptor( Map.class ) );


            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( proxyBaseClass ), "readExternal", "(" + Type.getDescriptor( ObjectInput.class ) + ")V" );

            mv.visitInsn( RETURN );
            mv.visitMaxs( 3, 2 );
            mv.visitEnd();
        }





        int j = 0;
        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {

            boolean isSoftField = TraitRegistry.isSoftField( field, j++, mask );
            if ( isSoftField ) {
                if ( ! mixinGetSet.containsKey( BuildUtils.getterName( field.getName(), field.getTypeName() ) ) ) {
                    buildSoftGetter( cw, field.getName(), field.getTypeName(), masterName, core.getName() );
                    buildSoftSetter( cw, field.getName(), field.getTypeName(), masterName, core.getName() );
                } else {
                    //
                }

            } else {
                {
                    fv = cw.visitField( ACC_PUBLIC + ACC_STATIC, field.getName() + "_reader", Type.getDescriptor( InternalReadAccessor.class ), null, null );
                    fv.visitEnd();
                }
                {
                    fv = cw.visitField( ACC_PUBLIC + ACC_STATIC, field.getName() + "_writer", Type.getDescriptor( WriteAccessor.class ), null, null );
                    fv.visitEnd();
                }

                buildHardGetter( cw, field, masterName, trait, core );
                buildHardSetter( cw, field, masterName, trait, core );

            }
        }

        boolean hasKeys = false;
        for ( FactField ff : trait.getFields() ) {
            if ( ff.isKey() ) {
                hasKeys = true;
                break;
            }
        }
        if ( ! hasKeys ) {
            buildEqualityMethods( cw, masterName, core.getClassName() );
        } else {
            buildKeyedEqualityMethods( cw, trait, masterName, core.getClassName() );
        }

        if ( mixinClass != null ) {
            buildMixinMethods( cw, masterName, mixin, mixinClass, mixinMethods );
            buildMixinMethods( cw, masterName, mixin, mixinClass, mixinGetSet.values() );
        }

        buildCommonMethods( cw, masterName, core.getClassName() );

        cw.visitEnd();

        return cw.toByteArray();

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



    private Class getPossibleConstructor( Class klass, Class arg ) throws NoSuchMethodException {

        Constructor[] ctors = klass.getConstructors();

        for ( Constructor c : ctors ) {
            Class[] cpars = c.getParameterTypes();

            if ( cpars.length != 1 || ! cpars[0].isAssignableFrom( arg ) ) {
                continue;
            }

            return cpars[0];
        }
        throw new NoSuchMethodException( "Constructor for " + klass + " using " + arg + " not found " );
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
                mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), mixin, Type.getDescriptor( mixinClass ) );
                int j = 1;
                for ( Class arg : method.getParameterTypes() ) {
                    mv.visitVarInsn( BuildUtils.varType( arg.getName() ), j++ );
                }
                mv.visitMethodInsn( INVOKEVIRTUAL,
                        Type.getInternalName( mixinClass ),
                        method.getName(),
                        signature );

                mv.visitInsn( BuildUtils.returnType( method.getReturnType().getName() ) );
                int stack = TraitFactory.getStackSize( method ) ;
                mv.visitMaxs( stack, stack );
                mv.visitEnd();
            }
        }

    }


    private void buildHardGetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition proxy, ClassDefinition core ) {
        String fieldName = field.getName();
        Class fieldType = field.getType();
        String getter = BuildUtils.getterName( fieldName, field.getTypeName() );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                getter,
                "()" + Type.getDescriptor( field.getType() ),
                null,
                null);
        mv.visitCode();


        TraitFactory.invokeExtractor( mv, masterName, proxy, core, field );

        if ( ! BuildUtils.isPrimitive( fieldType.getName() ) ) {
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( fieldType ) );
        }

        mv.visitInsn( BuildUtils.returnType ( field.getTypeName() ) );
        mv.visitMaxs( 2, 1 );
        mv.visitEnd();

    }




    private void buildHardSetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition trait, ClassDefinition core ) {
        String fieldName = field.getName();
        String fieldType = field.getTypeName();

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                BuildUtils.setterName( fieldName, fieldType ),
                "(" + Type.getDescriptor( field.getType() ) + ")V",
                null,
                null);
        mv.visitCode();

        TraitFactory.invokeInjector( mv, masterName, trait, core, field, false, 1 );

        mv.visitInsn(RETURN);
        mv.visitMaxs( 2 + BuildUtils.sizeOf( fieldType ),
                1 + BuildUtils.sizeOf( fieldType ) );
        mv.visitEnd();

    }




    private void buildSoftSetter( ClassVisitor cw, String fieldName, String type, String proxy, String core ) {
        String setter = BuildUtils.setterName( fieldName, type );


        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, setter, "(" + BuildUtils.getTypeDescriptor( type ) + ")V", null, null );
        mv.visitCode();
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxy ), "map", Type.getDescriptor( Map.class ) );
        mv.visitLdcInsn( fieldName );
        mv.visitVarInsn( BuildUtils.varType( type ), 1 );
        if ( BuildUtils.isPrimitive( type ) ) {
            TraitFactory.valueOf( mv, type );
        }
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "put", 
                            "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
        mv.visitInsn( POP );
        mv.visitInsn( RETURN );
        mv.visitMaxs( 2 + BuildUtils.sizeOf( type ), 1 + BuildUtils.sizeOf( type ) );
        mv.visitEnd();

    }



    private void buildSoftGetter( ClassVisitor cw, String fieldName, String type, String proxy, String core ) {

        String getter = BuildUtils.getterName( fieldName, type );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, getter, "()"+ BuildUtils.getTypeDescriptor( type ), null, null );
        mv.visitCode();
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxy ), "map", Type.getDescriptor( Map.class ) );
        mv.visitLdcInsn( fieldName );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "get", "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );

        if ( BuildUtils.isPrimitive( type ) ) {
            mv.visitVarInsn( ASTORE, 1 );
            mv.visitVarInsn( ALOAD, 1 );
            Label l0 = new Label();
            mv.visitJumpInsn( IFNULL, l0 );
            mv.visitVarInsn( ALOAD, 1 );
            Label l1 = new Label();

            mv.visitJumpInsn( GOTO, l1 );
            mv.visitLabel( l0 );
            mv.visitInsn( BuildUtils.zero( type ) );

            TraitFactory.valueOf( mv, type );
            mv.visitLabel( l1 );

            TraitFactory.promote( mv, type );

            mv.visitInsn( BuildUtils.returnType( type ) );
            mv.visitMaxs( 2, 2 );

        } else {
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( type ) );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 2, 1 );
        }

        mv.visitEnd();
    }


    public void buildKeyedEqualityMethods( ClassVisitor cw, ClassDefinition trait, String proxy, String core ) {

        String proxyType = BuildUtils.getInternalType( proxy );

        buildKeyedEquals( cw, trait, proxyType );
        buildKeyedHashCode( cw, trait, proxyType );

    }


    public void buildEqualityMethods( ClassVisitor cw, String proxy, String core ) {

        String proxyType = BuildUtils.getInternalType( proxy );
        String coreType = BuildUtils.getTypeDescriptor( core );


        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "equals", "(" + Type.getDescriptor( Object.class ) + ")Z", null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            Label l0 = new Label();
            mv.visitJumpInsn( IF_ACMPNE, l0 );
            mv.visitInsn( ICONST_1 );
            mv.visitInsn( IRETURN );
            mv.visitLabel( l0 );
            mv.visitVarInsn( ALOAD, 1 );
            Label l1 = new Label();
            mv.visitJumpInsn( IFNONNULL, l1 );
            mv.visitInsn( ICONST_0 );
            mv.visitInsn( IRETURN );
            mv.visitLabel( l1 );

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "getClass", "()" + Type.getDescriptor( Class.class ) );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "getClass", "()" + Type.getDescriptor( Class.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "equals", "(" + Type.getDescriptor( Object.class ) + ")Z");
            Label l2 = new Label();
            mv.visitJumpInsn( IFNE, l2 );
            mv.visitInsn( ICONST_0 );
            mv.visitInsn( IRETURN );
            mv.visitLabel( l2 );

            mv.visitVarInsn( ALOAD, 1 );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( proxyBaseClass ) );
            mv.visitVarInsn( ASTORE, 2 );

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, "getFields", "()" + Type.getDescriptor( Map.class ) );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( proxyBaseClass ), "getFields", "()" + Type.getDescriptor( Map.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "equals", "(" + Type.getDescriptor( Object.class) + ")Z" );

            mv.visitInsn( IRETURN );
            mv.visitMaxs( 2, 3 );
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "hashCode", "()I", null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, proxyType, "object", coreType );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "hashCode", "()I" );
            mv.visitVarInsn( ISTORE, 1 );
            mv.visitIntInsn( BIPUSH, 31 );
            mv.visitVarInsn( ILOAD, 1 );
            mv.visitInsn( IMUL );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, proxyType, "map", Type.getDescriptor( Map.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "hashCode", "()I" );
            mv.visitInsn( IADD );
            mv.visitVarInsn( ISTORE, 1 );

            mv.visitLdcInsn( proxy );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( String.class ), "hashCode", "()I" );
            mv.visitVarInsn( ILOAD, 1 );
            mv.visitInsn( IMUL );
            mv.visitVarInsn( ISTORE, 1 );

            mv.visitVarInsn( ILOAD, 1 );
            mv.visitInsn( IRETURN );
            mv.visitMaxs( 2, 2 );
            mv.visitEnd();

        }

    }


    private void buildCommonMethods(ClassWriter cw, String proxy, String core ) {

        String proxyType = BuildUtils.getInternalType( proxy );

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "toString", "()" + Type.getDescriptor( String.class ), null, null );
            mv.visitCode();
            mv.visitTypeInsn( NEW, Type.getInternalName( StringBuilder.class ) );
            mv.visitInsn( DUP );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( StringBuilder.class ), "<init>", "()V" );
            mv.visitLdcInsn( "(@" + proxy + ") : " );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append", 
                                "(" + Type.getDescriptor( String.class ) + ")" + Type.getDescriptor( StringBuilder.class ) );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, "getFields", "()" + Type.getDescriptor( Map.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "entrySet", "()" + Type.getDescriptor( Set.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "toString", "()" + Type.getDescriptor( String.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append", 
                                "(" + Type.getDescriptor( String.class ) + ")" + Type.getDescriptor( StringBuilder.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "toString", "()" + Type.getDescriptor( String.class ) );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 2, 1 );
            mv.visitEnd();
        }

    }


    protected void buildKeyedEquals( ClassVisitor cw,
                                     ClassDefinition classDef,
                                     String proxyType ) {
        MethodVisitor mv;
        mv = cw.visitMethod( ACC_PUBLIC, "equals", "(" + Type.getDescriptor( Object.class ) + ")Z", null, null );
        mv.visitCode();

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ALOAD, 1 );
        Label l0 = new Label();
        mv.visitJumpInsn( IF_ACMPNE, l0 );
        mv.visitInsn( ICONST_1 );
        mv.visitInsn( IRETURN );

        mv.visitLabel( l0 );
        mv.visitVarInsn( ALOAD, 1 );
        Label l1 = new Label();
        mv.visitJumpInsn( IFNULL, l1 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "getClass", "()" + Type.getDescriptor( Class.class ) );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "getClass", "()" + Type.getDescriptor( Class.class ) );
        Label l2 = new Label();
        mv.visitJumpInsn( IF_ACMPEQ, l2 );
        mv.visitLabel( l1 );
        mv.visitInsn( ICONST_0 );
        mv.visitInsn( IRETURN );
        mv.visitLabel( l2 );


        mv.visitVarInsn( ALOAD, 1 );
        mv.visitTypeInsn( CHECKCAST, proxyType );
        mv.visitVarInsn( ASTORE, 2 );

        int x = 2;

        for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
            if ( field.isKey() ) {


                if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {

                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ) );
                    Label l11 = new Label();
                    mv.visitJumpInsn( IFNULL, l11 );
                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ) );
                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ) );
                    mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( field.getTypeName() ), "equals", "(" + Type.getDescriptor( Object.class ) + ")Z" );
                    Label l12 = new Label();
                    mv.visitJumpInsn( IFNE, l12 );
                    Label l13 = new Label();
                    mv.visitJumpInsn( GOTO, l13 );
                    mv.visitLabel( l11 );
                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ) );
                    mv.visitJumpInsn( IFNULL, l12 );
                    mv.visitLabel( l13 );
                    mv.visitInsn( ICONST_0 );
                    mv.visitInsn( IRETURN );
                    mv.visitLabel( l12 );

                } else if ( "double".equals( field.getTypeName() ) ) {

                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ) );
                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ) );
                    mv.visitMethodInsn( INVOKESTATIC, Type.getInternalName( Double.class ), "compare", "(DD)I" );
                    Label l5 = new Label();
                    mv.visitJumpInsn( IFEQ, l5 );
                    mv.visitInsn( ICONST_0 );
                    mv.visitInsn( IRETURN );
                    mv.visitLabel( l5 );

                    x = Math.max( x, 4 );

                } else if ( "float".equals( field.getTypeName() ) ) {

                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ) );
                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ) );
                    mv.visitMethodInsn( INVOKESTATIC, Type.getInternalName( Float.class ), "compare", "(FF)I" );
                    Label l6 = new Label();
                    mv.visitJumpInsn( IFEQ, l6 );
                    mv.visitInsn( ICONST_0 );
                    mv.visitInsn( IRETURN );
                    mv.visitLabel( l6 );


                }  else if ( "long".equals( field.getTypeName() ) ) {

                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ) );
                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ) );
                    mv.visitInsn( LCMP );
                    Label l8 = new Label();
                    mv.visitJumpInsn( IFEQ, l8 );
                    mv.visitInsn( ICONST_0 );
                    mv.visitInsn( IRETURN );
                    mv.visitLabel( l8 );

                    x = Math.max( x, 4 );

                } else {

                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ) );
                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ) );
                    Label l4 = new Label();
                    mv.visitJumpInsn( IF_ICMPEQ, l4 );
                    mv.visitInsn( ICONST_0 );
                    mv.visitInsn( IRETURN );
                    mv.visitLabel( l4 );

                }
            }
        }

        mv.visitInsn( ICONST_1 );
        mv.visitInsn( IRETURN );
        mv.visitMaxs( x, 3 );
        mv.visitEnd();
    }

    protected void buildKeyedHashCode( ClassVisitor cw,
                                       ClassDefinition classDef,
                                       String proxyType ) {

        MethodVisitor mv;

        {
            mv = cw.visitMethod( ACC_PUBLIC, "hashCode", "()I", null, null );
            mv.visitCode();
            mv.visitIntInsn( BIPUSH, 31 );
            mv.visitVarInsn( ISTORE, 1 );

            int count = 0;
            int x = 2;
            int y = 2;
            for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
                if ( field.isKey() ) {
                    count++;

                    if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {

                        mv.visitIntInsn( BIPUSH, 31 );
                        mv.visitVarInsn( ILOAD, 1 );
                        mv.visitInsn( IMUL );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + Type.getDescriptor( field.getType() ) );
                        Label l8 = new Label();
                        mv.visitJumpInsn( IFNULL, l8 );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + Type.getDescriptor( field.getType() ) );
                        mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( field.getTypeName() ), "hashCode", "()I" );
                        Label l9 = new Label();
                        mv.visitJumpInsn( GOTO, l9 );
                        mv.visitLabel( l8 );
                        mv.visitInsn( ICONST_0 );
                        mv.visitLabel( l9 );
                        mv.visitInsn( IADD );
                        mv.visitVarInsn( ISTORE, 1 );

                    } else if ( "double".equals( field.getTypeName() ) ) {


                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + Type.getDescriptor( field.getType() ) );
                        mv.visitInsn( DCONST_0 ); 
                        mv.visitInsn( DCMPL );
                        Label l2 = new Label();
                        mv.visitJumpInsn( IFEQ, l2 );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + Type.getDescriptor( field.getType() ) );
                        mv.visitMethodInsn( INVOKESTATIC, Type.getInternalName( Double.class ), "doubleToLongBits", "(D)J" );
                        Label l3 = new Label();
                        mv.visitJumpInsn( GOTO, l3 );
                        mv.visitLabel( l2 );
                        mv.visitInsn( LCONST_0 );
                        mv.visitLabel( l3 );
                        mv.visitVarInsn( LSTORE, 2 );
                        mv.visitIntInsn( BIPUSH, 31 );
                        mv.visitVarInsn( ILOAD, 1 );
                        mv.visitInsn( IMUL );
                        mv.visitVarInsn( LLOAD, 2 );
                        mv.visitVarInsn( LLOAD, 2 );
                        mv.visitIntInsn( BIPUSH, 32 );
                        mv.visitInsn( LUSHR );
                        mv.visitInsn( LXOR );
                        mv.visitInsn( L2I );
                        mv.visitInsn( IADD );
                        mv.visitVarInsn( ISTORE, 1 );

                        x = Math.max( 6, x );
                        y = Math.max( 4, y );

                    } else if ( "boolean".equals( field.getTypeName() ) ) {

                        mv.visitIntInsn( BIPUSH, 31 );
                        mv.visitVarInsn( ILOAD, 1 );
                        mv.visitInsn( IMUL );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + Type.getDescriptor( field.getType() ) );
                        Label l4 = new Label();
                        mv.visitJumpInsn( IFEQ, l4 );
                        mv.visitInsn( ICONST_1 );
                        Label l5 = new Label();
                        mv.visitJumpInsn( GOTO, l5 );
                        mv.visitLabel( l4 );
                        mv.visitInsn( ICONST_0 );
                        mv.visitLabel( l5 );
                        mv.visitInsn( IADD );
                        mv.visitVarInsn( ISTORE, 1 );

                    } else if ( "float".equals( field.getTypeName() ) ) {

                        mv.visitIntInsn( BIPUSH, 31 );
                        mv.visitVarInsn( ILOAD, 1 );
                        mv.visitInsn( IMUL );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + Type.getDescriptor( field.getType() ) );
                        mv.visitInsn( FCONST_0 );
                        mv.visitInsn( FCMPL );
                        Label l6 = new Label();
                        mv.visitJumpInsn( IFEQ, l6 );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + Type.getDescriptor( field.getType() ) );
                        mv.visitMethodInsn( INVOKESTATIC, Type.getInternalName( Float.class ), "floatToIntBits", "(F)I" );
                        Label l7 = new Label();
                        mv.visitJumpInsn( GOTO, l7 );
                        mv.visitLabel( l6 );
                        mv.visitInsn( ICONST_0 );
                        mv.visitLabel( l7 );
                        mv.visitInsn( IADD );
                        mv.visitVarInsn( ISTORE, 1 );

                        x = Math.max( 3, x );

                    }  else if ( "long".equals( field.getTypeName() ) ) {

                        mv.visitIntInsn( BIPUSH, 31 );
                        mv.visitVarInsn( ILOAD, 1 );
                        mv.visitInsn( IMUL );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ),
                                            "()" + Type.getDescriptor( field.getType() ) );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ),
                                            "()" + Type.getDescriptor( field.getType() ) );
                        mv.visitIntInsn( BIPUSH, 32 );
                        mv.visitInsn( LUSHR );
                        mv.visitInsn( LXOR );
                        mv.visitInsn( L2I );
                        mv.visitInsn( IADD );
                        mv.visitVarInsn( ISTORE, 1 );

                        x = Math.max( 6, x );

                    } else {

                        mv.visitIntInsn( BIPUSH, 31 );
                        mv.visitVarInsn( ILOAD, 1 );
                        mv.visitInsn( IMUL );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ),
                                            "()" + Type.getDescriptor( field.getType() ) );
                        mv.visitInsn( IADD );
                        mv.visitVarInsn( ISTORE, 1 );

                    }
                }

            }
            mv.visitVarInsn( ILOAD, 1 );
            mv.visitInsn( IRETURN );
            mv.visitMaxs( x, y );
            mv.visitEnd();
        }
    }


}