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

import org.drools.core.util.Triple;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleStore;
import org.drools.core.util.asm.ClassFieldInspector;
import org.drools.definition.type.FactField;
import org.drools.factmodel.BuildUtils;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.WriteAccessor;
import org.mvel2.MVEL;
import org.mvel2.asm.*;

import java.beans.IntrospectionException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.io.Serializable;

public class TraitTripleProxyClassBuilderImpl implements TraitProxyClassBuilder, Serializable {


    private transient ClassDefinition trait;
    
    private transient Class<?> proxyBaseClass;

    private transient TraitRegistry traitRegistry;

    protected ClassDefinition getTrait() {
        return trait;
    }

    public void init( ClassDefinition trait, Class<?> baseClass, TraitRegistry traitRegistry ) {
        this.trait = trait;
        this.proxyBaseClass = baseClass;
        this.traitRegistry = traitRegistry;
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


        ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_MAXS );
        FieldVisitor fv;
        MethodVisitor mv;

        // get the method bitmask
        long mask = traitRegistry.getFieldMask( getTrait().getName(), core.getDefinedClass().getName() );

        String name = TraitFactory.getPropertyWrapperName( getTrait(), core );
        String masterName = TraitFactory.getProxyName( getTrait(), core );


        String internalWrapper  = BuildUtils.getInternalType(name);
        String internalProxy    = BuildUtils.getInternalType(masterName);

        String internalCore     = Type.getInternalName(core.getDefinedClass());
        String descrCore        = Type.getDescriptor(core.getDefinedClass());
        String internalTrait    = Type.getInternalName(getTrait().getDefinedClass());


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
                        } catch ( NoSuchMethodException e ) {

                        }
                    }

                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }



        cw.visit( V1_5,
                  ACC_PUBLIC + ACC_SUPER,
                  internalProxy,
                  null,
                  Type.getInternalName( proxyBaseClass ),
                  new String[] { internalTrait, Type.getInternalName( Externalizable.class ) } );

        {
            fv = cw.visitField( ACC_PRIVATE + ACC_FINAL + ACC_STATIC,
                    TraitType.traitNameField, Type.getDescriptor( String.class ),
                    null, null );
            fv.visitEnd();
        }
        {
            fv = cw.visitField( ACC_PUBLIC, "object", descrCore, null, null );
            fv.visitEnd();
        }
        {
            fv = cw.visitField( ACC_PRIVATE, "store", Type.getDescriptor( TripleStore.class ), null, null );
            fv.visitEnd();
        }
        {
            fv = cw.visitField( ACC_PRIVATE, "storeId", Type.getDescriptor( String.class ), null, null);
            fv.visitEnd();
        }
        if ( mixinClass != null ) {
            {
                fv = cw.visitField( ACC_PRIVATE,
                        mixin,
                        Type.getDescriptor( mixinClass ),
                        null, null);
                fv.visitEnd();
            }
        }

        {
            mv = cw.visitMethod( ACC_STATIC, "<clinit>", "()V", null, null );
            mv.visitCode();
            mv.visitLdcInsn( Type.getType( Type.getDescriptor( trait.getDefinedClass() ) ) );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    Type.getInternalName( Class.class ), "getName", "()" + Type.getDescriptor( String.class ) );
            mv.visitFieldInsn( PUTSTATIC,
                    internalProxy,
                    TraitType.traitNameField,
                    Type.getDescriptor( String.class ) );
            mv.visitInsn( RETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC, "<init>", "()V", null, null );
            mv.visitCode();

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( proxyBaseClass ), "<init>", "()V" );

            mv.visitInsn( RETURN );
//            mv.visitMaxs( 1, 1 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC, "<init>", "(" + descrCore + Type.getDescriptor( TripleStore.class ) + Type.getDescriptor( TripleFactory.class ) + ")V", null, null );
            mv.visitCode();

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( TripleStore.class ), "getId", "()" + Type.getDescriptor( String.class ) );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "storeId", Type.getDescriptor( String.class ) );

            int size = buildConstructorCore( cw, mv, internalProxy, internalWrapper, internalCore, descrCore, mixin, mixinClass );

            initFields( mv, internalProxy );

            mv.visitInsn( RETURN );
//            mv.visitMaxs( 5 + size, 4 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "getTraitName", "()" + Type.getDescriptor( String.class ), null, null);
            mv.visitCode();
            mv.visitFieldInsn( GETSTATIC, internalProxy, TraitType.traitNameField, Type.getDescriptor( String.class ) );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "getCore", "()" + descrCore + "", null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, internalProxy, "object", descrCore );
            mv.visitInsn( ARETURN );
//            mv.visitMaxs( 1, 1 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "getObject", "()" + Type.getDescriptor( Object.class ), null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, internalProxy, "object", descrCore );
            mv.visitInsn( ARETURN );
//            mv.visitMaxs( 1, 1 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "setObject", "(" + Type.getDescriptor( Object.class ) + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitTypeInsn( CHECKCAST, internalCore );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "object", descrCore );
            mv.visitInsn( RETURN );
//            mv.visitMaxs( 2, 2 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }


        {
            mv = cw.visitMethod( ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "getCore", "()" + Type.getDescriptor( Object.class ), null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, internalProxy, "getCore", "()" + descrCore );
            mv.visitInsn( ARETURN );
//            mv.visitMaxs( 1, 1 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC, "isTop", "()Z", null, null );
            mv.visitCode();
            mv.visitInsn( Thing.class.equals( trait.getDefinedClass() ) ? ICONST_1 : ICONST_0 );
            mv.visitInsn( IRETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC, "writeExternal", "(" + Type.getDescriptor( ObjectOutput.class )+ ")V", null, new String[] { Type.getInternalName( IOException.class ) } );
            mv.visitCode();

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( proxyBaseClass ), "writeExternal", "(" + Type.getDescriptor( ObjectOutput.class ) + ")V" );


            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, internalProxy, "getObject", "()" + Type.getDescriptor( Object.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectOutput.class ), "writeObject", "(" + Type.getDescriptor( Object.class ) + ")V" );


            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, internalProxy, "storeId", Type.getDescriptor( String.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectOutput.class ), "writeObject", "(" + Type.getDescriptor( Object.class ) + ")V" );

            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, internalProxy, "store", Type.getDescriptor( TripleStore.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectOutput.class ), "writeObject", "(" + Type.getDescriptor( Object.class ) + ")V" );



            mv.visitInsn( RETURN );
//            mv.visitMaxs( 2, 2 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "readExternal", "(" + Type.getDescriptor( ObjectInput.class ) + ")V", null, 
                                 new String[] { Type.getInternalName( IOException.class ), Type.getInternalName( ClassNotFoundException.class ) } );
            mv.visitCode();

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( proxyBaseClass ), "readExternal", "(" + Type.getDescriptor( ObjectInput.class ) + ")V" );


            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectInput.class ), "readObject", "()" + Type.getDescriptor( Object.class ) );
            mv.visitTypeInsn( CHECKCAST, internalCore );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "object", descrCore );


            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectInput.class ), "readObject", "()" + Type.getDescriptor( Object.class ) );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( String.class ) );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "storeId", Type.getDescriptor( String.class ) );

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectInput.class ), "readObject", "()" + Type.getDescriptor( Object.class ) );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TripleStore.class ) );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "store", Type.getDescriptor( TripleStore.class ) );


            mv.visitInsn( RETURN );
//            mv.visitMaxs( 3, 2 );
            mv.visitMaxs( 0, 0 );
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


    protected int buildConstructorCore( ClassWriter cw, MethodVisitor mv, String internalProxy, String internalWrapper, String internalCore, String descrCore, String mixin, Class mixinClass ) {


        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( proxyBaseClass ), "<init>", "()V" );
        if ( mixinClass != null ) {
            try {
//                    Constructor con = mixinClass.getConstructor( trait.getDefinedClass() );
                Class actualArg = getPossibleConstructor( mixinClass, trait.getDefinedClass() );

                mv.visitVarInsn( ALOAD, 0 );
                mv.visitTypeInsn( NEW, Type.getInternalName(mixinClass) );
                mv.visitInsn( DUP );
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitMethodInsn( INVOKESPECIAL,
                        Type.getInternalName( mixinClass ),
                        "<init>",
                        "("+ Type.getDescriptor( actualArg ) + ")V" );
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
        mv.visitFieldInsn( PUTFIELD, internalProxy, "store", Type.getDescriptor( TripleStore.class ) );

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ALOAD, 3 );
        mv.visitMethodInsn( INVOKEVIRTUAL, internalProxy, "setTripleFactory", "(" + Type.getDescriptor( TripleFactory.class ) + ")V" );


        mv.visitVarInsn( ALOAD, 0 );
        mv.visitTypeInsn( NEW, internalWrapper );
        mv.visitInsn( DUP );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 2 );
        mv.visitVarInsn( ALOAD, 3 );
        mv.visitMethodInsn( INVOKESPECIAL, internalWrapper, "<init>", "(" + descrCore + Type.getDescriptor( TripleStore.class ) + Type.getDescriptor( TripleFactory.class ) + ")V" );
        mv.visitFieldInsn( PUTFIELD, internalProxy, "fields", Type.getDescriptor( Map.class ) );


        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL, internalCore, "_getDynamicProperties", "()" + Type.getDescriptor( Map.class ) );
        Label l0 = new Label();
        mv.visitJumpInsn( IFNONNULL, l0 );

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitTypeInsn( NEW, Type.getInternalName( TripleBasedBean.class ) );
        mv.visitInsn( DUP );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 2 );
        mv.visitVarInsn( ALOAD, 3 );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( TripleBasedBean.class ), "<init>",
                            "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( TripleStore.class ) + Type.getDescriptor( TripleFactory.class ) + ")V" );
        mv.visitMethodInsn( INVOKEVIRTUAL, internalCore, "_setDynamicProperties", "(" + Type.getDescriptor( Map.class ) + ")V" );

        mv.visitLabel( l0 );



        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL, internalCore, "_getTraitMap", "()" + Type.getDescriptor( Map.class ) );
        Label l1 = new Label();
        mv.visitJumpInsn( IFNONNULL, l1 );

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitTypeInsn( NEW, Type.getInternalName( TraitTypeMap.class ) );
        mv.visitInsn( DUP );
        mv.visitTypeInsn( NEW, Type.getInternalName( TripleBasedTypes.class ) );
        mv.visitInsn( DUP );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 2 );
        mv.visitVarInsn( ALOAD, 3 );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( TripleBasedTypes.class ), "<init>",
                            "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( TripleStore.class )  + Type.getDescriptor( TripleFactory.class ) + ")V" );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( TraitTypeMap.class ), "<init>", "(" + Type.getDescriptor( Map.class )+ ")V" );
        mv.visitMethodInsn( INVOKEVIRTUAL, internalCore, "_setTraitMap", "(" + Type.getDescriptor( Map.class ) + ")V" );

        mv.visitLabel( l1 );

        return 3;
    }

    protected void initFields( MethodVisitor mv, String internalProxy ) {
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKESPECIAL, internalProxy, "synchFields", "()V" );
    }

    private Class getPossibleConstructor(Class klass, Class arg) throws NoSuchMethodException {

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
                fv = cw.visitField( ACC_PUBLIC + ACC_STATIC, field.getName() + "_reader", Type.getDescriptor( InternalReadAccessor.class ), null, null );
                fv.visitEnd();
            }
            {
                fv = cw.visitField( ACC_PUBLIC + ACC_STATIC, field.getName() + "_writer", Type.getDescriptor( WriteAccessor.class ), null, null );
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
                mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), mixin, Type.getDescriptor(mixinClass) );
                int j = 1;
                for ( Class arg : method.getParameterTypes() ) {
                    mv.visitVarInsn( BuildUtils.varType( arg.getName() ), j++ );
                }
                mv.visitMethodInsn( INVOKEVIRTUAL,
                        Type.getInternalName(mixinClass),
                        method.getName(),
                        signature );

                mv.visitInsn( BuildUtils.returnType( method.getReturnType().getName() ) );
                int stack = TraitFactory.getStackSize( method ) ;
//                mv.visitMaxs( stack, stack );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();
            }
        }

    }








    protected void buildHardGetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition proxy, ClassDefinition core) {
        buildHardGetter( cw, field, masterName, proxy, core, BuildUtils.getterName( field.getName(), field.getTypeName() ), false );
    }

    protected void buildHardGetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition proxy, ClassDefinition core, String getterName, boolean protect ) {        
        Class fieldType = field.getType();


        MethodVisitor mv = cw.visitMethod( protect ? ACC_PROTECTED : ACC_PUBLIC,
                getterName,
                "()" + Type.getDescriptor( fieldType ),
                null,
                null);
        mv.visitCode();


        if ( field.hasAlias() && proxy.getField( field.getAlias() ) != null ) {
            FieldDefinition aliasedField = proxy.getField( field.getAlias() );
            if ( field.getType().isAssignableFrom( aliasedField.getType() ) ) {
                // a simple cast is sufficient
                TraitFactory.invokeExtractor( mv, masterName, proxy, core, field );
                if ( ! BuildUtils.isPrimitive( fieldType.getName() ) ) {
                    mv.visitTypeInsn( CHECKCAST, Type.getInternalName( fieldType ) );
                }
            } else {
                TraitFactory.invokeExtractor( mv, masterName, proxy, core, field );
                mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitableBean.class ) );
                mv.visitVarInsn( ASTORE, 1 );
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitLdcInsn( fieldType.getName() );
                mv.visitMethodInsn( INVOKEINTERFACE,
                        Type.getInternalName( TraitableBean.class ),
                        "getTrait",
                        Type.getMethodDescriptor( Type.getType( Thing.class ), new Type[] { Type.getType( String.class ) } ) );
                mv.visitTypeInsn( CHECKCAST, Type.getInternalName( fieldType ) );
            }
        } else {
            TraitFactory.invokeExtractor( mv, masterName, proxy, core, field );
            if ( ! BuildUtils.isPrimitive( fieldType.getName() ) ) {
                mv.visitTypeInsn( CHECKCAST, Type.getInternalName( fieldType ) );
            }
        }

        mv.visitInsn( BuildUtils.returnType ( fieldType.getName() ) );
//        mv.visitMaxs( 2, 1 );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();

    }



    protected void buildHardSetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition trait, ClassDefinition core ) {
        buildHardSetter( cw, field, masterName, trait, core, BuildUtils.setterName( field.getName(), field.getTypeName() ), false );
    }

    protected void buildHardSetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition trait, ClassDefinition core, String setterName, boolean protect ) {
        Class fieldType = field.getType();

        MethodVisitor mv = cw.visitMethod( protect ? ACC_PROTECTED : ACC_PUBLIC,
                setterName,
                "(" + Type.getDescriptor(field.getType()) + ")V",
                null,
                null);
        mv.visitCode();

        if ( field.hasAlias() && trait.getField( field.getAlias() ) != null ) {
            FieldDefinition aliasedField = trait.getField( field.getAlias() );
            if ( field.getType().isAssignableFrom( aliasedField.getType() ) ) {
                TraitFactory.invokeInjector( mv, masterName, trait, core, field, false, 1 );
            } else {
                mv.visitFieldInsn( GETSTATIC,
                                   BuildUtils.getInternalType( masterName ),
                                   field.getName() + "_writer",
                                   Type.getDescriptor( WriteAccessor.class ) );
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD,
                                   BuildUtils.getInternalType( masterName ),
                                   "object",
                                   BuildUtils.getTypeDescriptor( core.getName() ) );
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitTypeInsn( CHECKCAST, Type.getInternalName( Thing.class ) );
                mv.visitMethodInsn( INVOKEINTERFACE,
                                    Type.getInternalName( Thing.class ),
                                    "getCore",
                                    Type.getMethodDescriptor( Type.getType( Object.class ), new Type[] {} ) );
                mv.visitMethodInsn( INVOKEINTERFACE,
                                    Type.getInternalName( WriteAccessor.class ),
                                    "setValue",
                                    Type.getMethodDescriptor( Type.getType( void.class ), new Type[] { Type.getType( Object.class ), Type.getType( Object.class  ) } ) );
            }
        } else {
            TraitFactory.invokeInjector( mv, masterName, trait, core, field, false, 1 );
        }

        mv.visitInsn(RETURN);
//        mv.visitMaxs( 2 + BuildUtils.sizeOf( fieldType ),
//                1 + BuildUtils.sizeOf( fieldType ) );
        mv.visitMaxs( 0, 0 );
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
                "(" + Type.getDescriptor( field.getType() ) + ")V",
                null,
                null );
        mv.visitCode();
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxy ), "store", Type.getDescriptor( TripleStore.class ) );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitLdcInsn( field.resolveAlias() );
        mv.visitVarInsn( BuildUtils.varType( type ), 1 );
        if ( BuildUtils.isPrimitive( type ) ) {
            TraitFactory.valueOf( mv, type );
        }
        mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( proxy ), "property", 
                            "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Triple.class ) );
        mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( TripleStore.class ), "put", "(" + Type.getDescriptor( Triple.class )+ ")Z" );

        mv.visitInsn( POP );
        mv.visitInsn( RETURN );
//        mv.visitMaxs( 3 + BuildUtils.sizeOf( type ), 1 + BuildUtils.sizeOf( type ) );
        mv.visitMaxs( 0, 0 );
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
                "()"+ Type.getDescriptor( field.getType() ),
                null,
                null );
        mv.visitCode();
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxy ), "store", Type.getDescriptor( TripleStore.class ) );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitLdcInsn( field.resolveAlias() );

        mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( proxy ), "propertyKey", 
                            "(" + Type.getDescriptor( String.class ) + ")" + Type.getDescriptor( Triple.class ) );
        mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( TripleStore.class ), "get", 
                            "(" + Type.getDescriptor( Triple.class ) + ")" + Type.getDescriptor( Triple.class ) );

        String actualType = BuildUtils.isPrimitive( type ) ? BuildUtils.box( type ) : type;

        mv.visitVarInsn( ASTORE, 1 );
        mv.visitVarInsn( ALOAD, 1 );
        Label l0 = new Label();
        mv.visitJumpInsn( IFNULL, l0 );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Triple.class ), "getValue", "()" + Type.getDescriptor( Object.class ) );
        mv.visitVarInsn( ASTORE, 2 );
        mv.visitVarInsn( ALOAD, 2 );
        mv.visitTypeInsn( INSTANCEOF, BuildUtils.getInternalType( actualType ) );
        Label l1 = new Label();
        mv.visitJumpInsn( IFEQ, l1 );
        mv.visitVarInsn( ALOAD, 2 );
        mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( actualType ) );

        if ( BuildUtils.isPrimitive( type ) ) {
            TraitFactory.primitiveValue( mv, type );
            mv.visitInsn( BuildUtils.returnType( type ) );
            mv.visitLabel( l1 );
            mv.visitInsn( BuildUtils.zero( type ) );
            mv.visitInsn( BuildUtils.returnType( type ) );
            mv.visitLabel( l0 );
            mv.visitInsn( BuildUtils.zero( type ) );
            mv.visitInsn( BuildUtils.returnType( type ) );
        } else {
            mv.visitInsn( ARETURN );
            mv.visitLabel( l1 );
            mv.visitInsn( ACONST_NULL );
            mv.visitInsn( ARETURN );
            mv.visitLabel( l0 );
            mv.visitInsn( ACONST_NULL );
            mv.visitInsn( ARETURN );
        }
//        mv.visitMaxs( 3, 2 );
        mv.visitMaxs( 0, 0 );

        mv.visitEnd();
    }





    public void buildEqualityMethods( ClassVisitor cw, String proxy, String core ) {

    }






    public void buildKeyedEqualityMethods( ClassVisitor cw, ClassDefinition trait, String proxy, String core ) {
        String proxyType = BuildUtils.getInternalType( proxy );

        buildKeyedEquals( cw, trait, proxyType );
        buildKeyedHashCode( cw, trait, proxyType );
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

        int count = 0;

        for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
            if ( field.isKey() ) {
                count++;


                if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {

                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    Label l11 = new Label();
                    mv.visitJumpInsn( IFNULL, l11 );
                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( field.getTypeName() ), "equals", 
                                        "(" + Type.getDescriptor( Object.class ) + ")Z" );
                    Label l12 = new Label();
                    mv.visitJumpInsn( IFNE, l12 );
                    Label l13 = new Label();
                    mv.visitJumpInsn( GOTO, l13 );
                    mv.visitLabel( l11 );
                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitJumpInsn( IFNULL, l12 );
                    mv.visitLabel( l13 );
                    mv.visitInsn( ICONST_0 );
                    mv.visitInsn( IRETURN );
                    mv.visitLabel( l12 );

                } else if ( "double".equals( field.getTypeName() ) ) {

                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
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
                                        "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitMethodInsn( INVOKESTATIC, Type.getInternalName( Float.class ), "compare", "(FF)I" );
                    Label l6 = new Label();
                    mv.visitJumpInsn( IFEQ, l6 );
                    mv.visitInsn( ICONST_0 );
                    mv.visitInsn( IRETURN );
                    mv.visitLabel( l6 );


                }  else if ( "long".equals( field.getTypeName() ) ) {

                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
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
                                        "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
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
//        mv.visitMaxs( x, 3 );
        mv.visitMaxs( 0, 0 );
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
                                            "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                        Label l8 = new Label();
                        mv.visitJumpInsn( IFNULL, l8 );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
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
                                            "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                        mv.visitInsn( DCONST_0 );
                        mv.visitInsn( DCMPL );
                        Label l2 = new Label();
                        mv.visitJumpInsn( IFEQ, l2 );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
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
                        mv.visitInsn( IADD);
                        mv.visitVarInsn( ISTORE, 1 );

                        x = Math.max( 6, x );
                        y = Math.max( 4, y );

                    } else if ( "boolean".equals( field.getTypeName() ) ) {

                        mv.visitIntInsn( BIPUSH, 31 );
                        mv.visitVarInsn( ILOAD, 1 );
                        mv.visitInsn( IMUL );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
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
                                            "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                        mv.visitInsn( FCONST_0 );
                        mv.visitInsn( FCMPL );
                        Label l6 = new Label();
                        mv.visitJumpInsn( IFEQ, l6 );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
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
                                            "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
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
                                            "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) ) ;
                        mv.visitInsn( IADD );
                        mv.visitVarInsn( ISTORE, 1 );

                    }
                }

            }
            mv.visitVarInsn( ILOAD, 1 );
            mv.visitInsn( IRETURN );
//            mv.visitMaxs( x, y );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
    }





    protected void buildCommonMethods( ClassWriter cw, String proxy ) {
        MethodVisitor mv;
        {
            mv = cw.visitMethod( ACC_PUBLIC, "toString", "()" + Type.getDescriptor( String.class ), null, null );
            mv.visitCode();
            mv.visitTypeInsn( NEW, Type.getInternalName( StringBuilder.class ) );
            mv.visitInsn( DUP );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( StringBuilder.class ), "<init>", "()V" );
            mv.visitLdcInsn( "(@" + proxy + ") : " );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append", "(" + Type.getDescriptor( String.class ) + ")" + Type.getDescriptor( StringBuilder.class ) );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( proxy ), "getFields", "()" + Type.getDescriptor( Map.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ) , "entrySet", "()" + Type.getDescriptor( Set.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "toString", "()" + Type.getDescriptor( String.class ));
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append", "(" + Type.getDescriptor( String.class ) + ")" + Type.getDescriptor( StringBuilder.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "toString", "()" + Type.getDescriptor( String.class ));
            mv.visitInsn( ARETURN );
//            mv.visitMaxs( 2, 1 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();

        }

    }



    protected void buildExtendedMethods(ClassWriter cw, ClassDefinition trait, ClassDefinition core ) {
        buildSynchFields( cw, TraitFactory.getProxyName(trait, core), core.getName(), getTrait() );
    }

    protected void buildSynchFields( ClassWriter cw, String proxyName, String coreName, ClassDefinition def ) {
        {
            MethodVisitor mv = cw.visitMethod( ACC_PRIVATE, "synchFields", "()V", null, null );
            mv.visitCode();
            for ( FieldDefinition fld : def.getFieldsDefinitions() ) {
                if ( fld.getInitExpr() != null ) {
                    synchField( mv, fld, proxyName );
                }
            }
            mv.visitInsn( RETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
    }


    protected void synchField( MethodVisitor mv, FieldDefinition fld, String proxyName ) {
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                            BuildUtils.getInternalType( proxyName ),
                            BuildUtils.getterName( fld.getName(), fld.getTypeName() ),
                            "()" + BuildUtils.getTypeDescriptor( fld.getTypeName() ) );

        Label l0 = null;
        if ( ! BuildUtils.isPrimitive( fld.getTypeName() ) ) {
            l0 = new Label();
            mv.visitJumpInsn( IFNONNULL, l0 );
        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitLdcInsn( fld.getInitExpr() );
        mv.visitMethodInsn( INVOKESTATIC,
                            Type.getInternalName( MVEL.class ),
                            "eval",
                            Type.getMethodDescriptor( Type.getType( Object.class ), new Type[] { Type.getType( String.class ) } ) );
        if ( BuildUtils.isPrimitive( fld.getTypeName() ) ) {
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( BuildUtils.box( fld.getTypeName() ) ) );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                BuildUtils.getInternalType( BuildUtils.box( fld.getTypeName() ) ),
                                BuildUtils.numericMorph( BuildUtils.box( fld.getTypeName() ) ),
                                "()" + BuildUtils.getTypeDescriptor( fld.getTypeName() ) );
        } else {
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( fld.getTypeName() ) );
        }
        mv.visitMethodInsn( INVOKEVIRTUAL,
                            BuildUtils.getInternalType( proxyName ),
                            BuildUtils.setterName( fld.getName(), fld.getTypeName() ),
                            "(" + BuildUtils.getTypeDescriptor( fld.getTypeName() ) + ")" + Type.getDescriptor( void.class ) );
        if ( ! BuildUtils.isPrimitive( fld.getTypeName() ) ) {
            mv.visitLabel( l0 );
        }
    }


}