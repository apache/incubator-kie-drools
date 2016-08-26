/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.factmodel.BuildUtils;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.factmodel.traits.TraitBuilderUtil.*;
import org.drools.core.util.Triple;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleStore;
import org.kie.api.definition.type.FactField;
import org.mvel2.MVEL;
import org.mvel2.asm.ClassVisitor;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.FieldVisitor;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Opcodes;
import org.mvel2.asm.Type;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.drools.core.factmodel.traits.TraitBuilderUtil.*;
import static org.drools.core.rule.builder.dialect.asm.ClassGenerator.createClassWriter;

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



    public byte[] buildClass( ClassDefinition core, ClassLoader classLoader ) throws IOException,
            SecurityException,
            IllegalArgumentException,
            ClassNotFoundException,
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException,
            NoSuchFieldException {


        FieldVisitor fv;
        MethodVisitor mv;

        // get the method bitmask
        BitSet mask = traitRegistry.getFieldMask( getTrait().getName(), core.getDefinedClass().getName() );

        String name = TraitFactory.getPropertyWrapperName( getTrait(), core );
        String masterName = TraitFactory.getProxyName( getTrait(), core );
        Class<?> traitClass = getTrait().getDefinedClass();

        String internalWrapper  = BuildUtils.getInternalType(name);
        String internalProxy    = BuildUtils.getInternalType(masterName);

        String internalCore     = Type.getInternalName(core.getDefinedClass());
        String descrCore        = Type.getDescriptor(core.getDefinedClass());
        String internalTrait    = Type.getInternalName(getTrait().getDefinedClass());

        MixinInfo mixinInfo = findMixinInfo( traitClass );

        ClassWriter cw = createClassWriter( classLoader,
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

        if ( mixinInfo != null ) {
            for ( Class<?> mixinClass : mixinInfo.mixinClasses ) {
                {
                    fv = cw.visitField( ACC_PRIVATE,
                                        getMixinName(mixinClass),
                                        Type.getDescriptor( mixinClass ),
                                        null, null );
                    fv.visitEnd();
                }
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
            mv = cw.visitMethod( ACC_PUBLIC, "<init>", "(" + descrCore + Type.getDescriptor( TripleStore.class ) + Type.getDescriptor( TripleFactory.class ) + Type.getDescriptor( BitSet.class ) + Type.getDescriptor( BitSet.class ) + Type.getDescriptor( boolean.class  ) +")V", null, null );
            mv.visitCode();

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( TripleStore.class ), "getId", "()" + Type.getDescriptor( String.class ) );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "storeId", Type.getDescriptor( String.class ) );

            int size = buildConstructorCore( cw, mv, internalProxy, internalWrapper, internalCore, descrCore, mixinInfo );

            initFields( mv, internalProxy );

            mv.visitInsn( RETURN );
//            mv.visitMaxs( 5 + size, 4 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "_getTraitName", "()" + Type.getDescriptor( String.class ), null, null);
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
            mv = cw.visitMethod( ACC_PUBLIC, "getObject", "()" + Type.getDescriptor( TraitableBean.class ), null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, internalProxy, "object", descrCore );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitableBean.class ) );
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
            mv = cw.visitMethod( ACC_PUBLIC, "_isTop", "()Z", null, null );
            mv.visitCode();
            mv.visitInsn( Thing.class.equals( trait.getDefinedClass() ) ? ICONST_1 : ICONST_0 );
            mv.visitInsn( IRETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC, "shed", Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] {} ), null, null );
            mv.visitCode();

            if ( core.isFullTraiting() ) {
                Iterator<FieldDefinition> iter = trait.getFieldsDefinitions().iterator();
                for ( int j = 0; j < trait.getFieldsDefinitions().size(); j++ ) {
                    FieldDefinition fld = iter.next();
                    boolean hardField = ! TraitRegistry.isSoftField( fld, j, mask );
                    shedField( mv, fld, internalProxy, trait, core, hardField, j + 2 );
                }
            }

            mv.visitInsn( RETURN );
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
            mv.visitMethodInsn( INVOKEVIRTUAL, internalProxy, "getObject", "()" + Type.getDescriptor( TraitableBean.class ) );
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


        buildProxyAccessors( mask, cw, masterName, core, mixinInfo );

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

        buildMixinMethods( masterName, mixinInfo, cw );

        buildCommonMethods( cw, masterName );

        buildExtendedMethods( cw, getTrait(), core, mask );

        buildShadowMethods( cw, trait, core, mask );

        cw.visitEnd();

        return cw.toByteArray();

    }

    protected void buildShadowMethods( ClassWriter cw, ClassDefinition trait, ClassDefinition core, BitSet mask ) {
        for ( Method m : trait.getDefinedClass().getMethods() ) {
            if ( ! TraitFactory.excludeFromShadowing( m, trait ) ) {
                for ( Method q : core.getDefinedClass().getMethods() ) {
                    if ( TraitFactory.isCompatible( m, q ) ) {
                        buildShadowMethod( cw, trait, core, m, q );
                    }
                }
            }
        }
    }

    private void buildShadowMethod( ClassWriter cw, ClassDefinition trait, ClassDefinition core, Method m, Method q ) {
        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                                           m.getName(),
                                           Type.getMethodDescriptor( m ),
                                           null,
                                           null );

        mv.visitCode();
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                            BuildUtils.getInternalType( TraitFactory.getProxyName( trait, core ) ),
                            "getCore",
                            Type.getMethodDescriptor( Type.getType( core.getDefinedClass() ), new Type[] {} ),
                            false );

        for ( int j = 0; j < m.getParameterTypes().length; j++ ) {
            mv.visitVarInsn( BuildUtils.varType( m.getParameterTypes()[ j ].getName() ), j + 1 );
        }
        mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( core.getDefinedClass() ), m.getName(), Type.getMethodDescriptor( m ), core.getDefinedClass().isInterface() );

        mv.visitInsn( BuildUtils.returnType( m.getReturnType().getName() ) );

        mv.visitMaxs( 0, 0 );
        mv.visitEnd();

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


    protected int buildConstructorCore( ClassWriter cw, MethodVisitor mv, String internalProxy, String internalWrapper, String internalCore, String descrCore, MixinInfo mixinInfo ) {
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( proxyBaseClass ), "<init>", "()V" );
        if (mixinInfo != null) {
            for ( Class<?> mixinClass : mixinInfo.mixinClasses ) {
                try {
                    //                    Constructor con = mixinClass.getConstructor( trait.getDefinedClass() );
                    Class actualArg = getPossibleConstructor( mixinClass, trait.getDefinedClass() );

                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitTypeInsn( NEW, Type.getInternalName( mixinClass ) );
                    mv.visitInsn( DUP );
                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKESPECIAL,
                                        Type.getInternalName( mixinClass ),
                                        "<init>",
                                        "(" + Type.getDescriptor( actualArg ) + ")V" );
                    mv.visitFieldInsn( PUTFIELD,
                                       internalProxy,
                                       getMixinName( mixinClass ),
                                       Type.getDescriptor( mixinClass ) );
                } catch (NoSuchMethodException nsme) {
                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitTypeInsn( NEW, Type.getInternalName( mixinClass ) );
                    mv.visitInsn( DUP );
                    mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( mixinClass ), "<init>", "()V" );
                    mv.visitFieldInsn( PUTFIELD,
                                       internalProxy,
                                       getMixinName( mixinClass ),
                                       Type.getDescriptor( mixinClass ) );
                }
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
        mv.visitVarInsn( ALOAD, 4 );
        mv.visitMethodInsn( INVOKEVIRTUAL, internalProxy, "setTypeCode", Type.getMethodDescriptor( Type.VOID_TYPE, new Type[]{ Type.getType( BitSet.class ) } ) );

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
                            "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( TripleStore.class ) + Type.getDescriptor( TripleFactory.class ) + ")V" );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( TraitTypeMap.class ), "<init>", "(" + Type.getDescriptor( Map.class )+ ")V" );
        mv.visitMethodInsn( INVOKEVIRTUAL, internalCore, "_setTraitMap", "(" + Type.getDescriptor( Map.class ) + ")V" );

        mv.visitLabel( l1 );


        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 5 );
        mv.visitMethodInsn( INVOKEVIRTUAL, internalCore, "_setBottomTypeCode", Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] { Type.getType( BitSet.class ) } ) );

        // core.addTrait
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitLdcInsn( trait.getName().endsWith( TraitFactory.SUFFIX ) ? trait.getName().replace(  TraitFactory.SUFFIX , "" ) : trait.getName() );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKEVIRTUAL, internalCore, "addTrait",  Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] { Type.getType( String.class ), Type.getType( Thing.class ) } ) );

        return 3;
    }

    protected void initFields( MethodVisitor mv, String internalProxy ) {
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ILOAD, 6 );
        mv.visitMethodInsn( INVOKESPECIAL, internalProxy, "synchFields", Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] { Type.BOOLEAN_TYPE } ) );
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



    protected void buildProxyAccessors( BitSet mask, ClassWriter cw, String masterName, ClassDefinition core, MixinInfo mixinInfo) {
        int j = 0;

        for ( FieldDefinition field : getTrait().getFieldsDefinitions() ) {
            boolean isSoftField = TraitRegistry.isSoftField( field, j++, mask );
            buildProxyAccessor( mask, cw, masterName, core, mixinInfo, field, isSoftField );
        }

    }


    protected void buildProxyAccessor( BitSet mask, ClassWriter cw, String masterName, ClassDefinition core, MixinInfo mixinInfo, FieldDefinition field, boolean isSoftField ) {
        FieldVisitor fv;

        if ( core.isFullTraiting() ) {
            buildLogicalGetter( cw, field, masterName, trait, core );
            if ( ! isSoftField ) {
                buildHardSetter( cw, field, masterName, trait, core );
            } else {
                buildSoftSetter( cw, field, masterName, trait, core );
            }
        } else {
            if ( isSoftField ) {
                if (mixinInfo == null || !mixinInfo.isMixinGetter( field )) {
                    buildSoftGetter( cw, field, masterName, trait, core );
                    buildSoftSetter( cw, field, masterName, trait, core );
                }
            } else {
                buildHardGetter( cw, field, masterName, trait, core );
                buildHardSetter( cw, field, masterName, trait, core );
            }
        }
    }

    private void logicalSetter( MethodVisitor mv, FieldDefinition field, String masterName, ClassDefinition trait, ClassDefinition core, boolean hardField ) {
        String fieldType = field.getTypeName();
        int reg = 1 + BuildUtils.sizeOf( fieldType );

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( masterName ), "object", Type.getDescriptor( core.getDefinedClass() ) );
        mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitableBean.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( TraitableBean.class ), "_getFieldTMS", Type.getMethodDescriptor( Type.getType( TraitFieldTMS.class ), new Type[] {} ) );

        mv.visitVarInsn( ASTORE, reg );
        mv.visitVarInsn( ALOAD, reg );

        mv.visitLdcInsn( field.resolveAlias() );
        if ( BuildUtils.isPrimitive( fieldType ) ) {
            mv.visitVarInsn( BuildUtils.varType( fieldType ), 1 );
            mv.visitMethodInsn( Opcodes.INVOKESTATIC,
                                BuildUtils.getInternalType( BuildUtils.box( fieldType ) ),
                                "valueOf",
                                Type.getMethodDescriptor( Type.getType( BuildUtils.getTypeDescriptor( BuildUtils.box( fieldType ) ) ), new Type[] { Type.getType( BuildUtils.getTypeDescriptor( fieldType ) ) } ) );
        } else {
            mv.visitVarInsn( ALOAD, 1 );
        }
        if ( BuildUtils.isPrimitive( fieldType ) ) {
            //            mv.visitFieldInsn( GETSTATIC, BuildUtils.getInternalType( BuildUtils.box( fieldType ) ), "TYPE", Type.getDescriptor( Class.class ) );
            mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( BuildUtils.box( field.getTypeName() ) ) ) );
        } else {
            mv.visitLdcInsn( Type.getType( Type.getDescriptor( field.getType() ) ) );
        }
        mv.visitMethodInsn( INVOKEINTERFACE,
                            Type.getInternalName( TraitFieldTMS.class ),
                            "set",
                            Type.getMethodDescriptor( Type.getType( Object.class ), new Type[] {
                                    Type.getType( String.class ), Type.getType( Object.class ), Type.getType( Class.class )
                            } ) );

        mv.visitVarInsn( ASTORE, 1 );
        mv.visitVarInsn( ALOAD, 1 );

        if ( BuildUtils.isPrimitive( fieldType ) ) {

            Label l0 = new Label();
            mv.visitJumpInsn( IFNULL, l0 );
            mv.visitVarInsn( ALOAD, 1 );
            Label l1 = new Label();
            mv.visitJumpInsn( GOTO, l1 );
            mv.visitLabel( l0 );
            mv.visitInsn( BuildUtils.zero( fieldType ) );
            mv.visitMethodInsn( Opcodes.INVOKESTATIC,
                                BuildUtils.getInternalType( BuildUtils.box( fieldType ) ),
                                "valueOf",
                                Type.getMethodDescriptor( Type.getType( BuildUtils.getTypeDescriptor( BuildUtils.box( fieldType ) ) ), new Type[] { Type.getType( BuildUtils.getTypeDescriptor( fieldType ) ) } ) );
            mv.visitLabel( l1 );

            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( BuildUtils.box( fieldType ) ) );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                BuildUtils.getInternalType( BuildUtils.box( fieldType ) ),
                                BuildUtils.numericMorph( BuildUtils.box( fieldType ) ),
                                Type.getMethodDescriptor( Type.getType( field.getType() ), new Type[] {} ) );
            mv.visitVarInsn( BuildUtils.storeType( fieldType ), 1 );
        }

    }


    private void buildLogicalGetter( ClassVisitor cw, FieldDefinition field, String proxy, ClassDefinition trait, ClassDefinition core ) {
        String fieldName = field.getName();
        String fieldType = field.getTypeName();

        String getter = BuildUtils.getterName( fieldName, fieldType );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, getter, "()" + BuildUtils.getTypeDescriptor( fieldType ), null, null );
        mv.visitCode();

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxy ), "object", Type.getDescriptor( core.getDefinedClass() ) );
        mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitableBean.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( TraitableBean.class ), "_getFieldTMS", Type.getMethodDescriptor( Type.getType( TraitFieldTMS.class ), new Type[] {} ) );

        mv.visitLdcInsn( field.resolveAlias() );
        if ( BuildUtils.isPrimitive( fieldType ) ) {
            //            mv.visitFieldInsn( GETSTATIC, BuildUtils.getInternalType( BuildUtils.box( fieldType ) ), "TYPE", Type.getDescriptor( Class.class ) );
            mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( BuildUtils.box( field.getTypeName() ) ) ) );
        } else {
            mv.visitLdcInsn( Type.getType( Type.getDescriptor( field.getType() ) ) );
        }
        mv.visitMethodInsn( INVOKEINTERFACE,
                            Type.getInternalName( TraitFieldTMS.class ),
                            "get",
                            Type.getMethodDescriptor( Type.getType( Object.class ), new Type[] { Type.getType( String.class ), Type.getType( Class.class ) } ) );

        mv.visitVarInsn( ASTORE, 1 );
        mv.visitVarInsn( ALOAD, 1 );

        if ( BuildUtils.isPrimitive( fieldType ) ) {
            Label l0 = new Label();
            mv.visitJumpInsn( IFNULL, l0 );
            mv.visitVarInsn( ALOAD, 1 );
            Label l1 = new Label();
            mv.visitJumpInsn( GOTO, l1 );
            mv.visitLabel( l0 );
            mv.visitInsn( BuildUtils.zero( fieldType ) );
            mv.visitMethodInsn( Opcodes.INVOKESTATIC,
                                BuildUtils.getInternalType( BuildUtils.box( fieldType ) ),
                                "valueOf",
                                Type.getMethodDescriptor( Type.getType( BuildUtils.getTypeDescriptor( BuildUtils.box( fieldType ) ) ), new Type[] { Type.getType( BuildUtils.getTypeDescriptor( fieldType ) ) } ) );
            mv.visitLabel( l1 );

            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( BuildUtils.box( fieldType ) ) );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                BuildUtils.getInternalType( BuildUtils.box( fieldType ) ),
                                BuildUtils.numericMorph( BuildUtils.box( fieldType ) ),
                                Type.getMethodDescriptor( Type.getType( field.getType() ), new Type[] {} ) );
            mv.visitInsn( BuildUtils.returnType( fieldType ) );
        } else {
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( fieldType ) );
            mv.visitInsn( ARETURN );
        }

        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }





    protected void buildHardGetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition proxy, ClassDefinition core ) {
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

        TraitFactory.invokeExtractor( mv, masterName, proxy, core, field );

        if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( fieldType ) );
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
                                           "(" + Type.getDescriptor( field.getType() ) + ")V",
                                           null,
                                           null );
        mv.visitCode();

        if ( core.isFullTraiting() ) {
            // The trait field update will be done by the core setter. However, types may mismatch here
            FieldDefinition hardField = core.getFieldByAlias( field.resolveAlias() );
            boolean isHardField = field.getTypeName().equals( hardField.getTypeName() );
            if ( ! field.getType().isPrimitive() && ! isHardField ) {
                boolean isCoreTrait = hardField.getType().getAnnotation( Trait.class ) != null;
                boolean isTraitTrait = field.getType().getAnnotation( Trait.class ) != null;

                Label l0 = new Label();
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitJumpInsn( IFNULL, l0 );
                if ( isCoreTrait && ! isTraitTrait ) {
                    mv.visitVarInsn( ALOAD, 1 );
                    mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitableBean.class ) );
                    mv.visitLdcInsn( hardField.getTypeName() );
                    mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( TraitableBean.class ), "getTrait", Type.getMethodDescriptor( Type.getType( Thing.class ), new Type[] { Type.getType( String.class ) } ) );
                    mv.visitVarInsn( ASTORE, 1 );
                } else if ( ! isCoreTrait && isTraitTrait ) {
                    mv.visitVarInsn( ALOAD, 1 );
                    mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitProxy.class ) );
                    mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( TraitProxy.class ), "getObject", Type.getMethodDescriptor( Type.getType( TraitableBean.class ), new Type[]{ } ) );
                    mv.visitVarInsn( ASTORE, 1 );
                } else if ( isCoreTrait && isTraitTrait ) {
                    mv.visitVarInsn( ALOAD, 1 );
                    mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitProxy.class ) );
                    mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( TraitProxy.class ), "getObject", Type.getMethodDescriptor( Type.getType( TraitableBean.class ), new Type[] {} ) );
                    mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitableBean.class ) );
                    mv.visitLdcInsn( hardField.getTypeName() );
                    mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( TraitableBean.class ), "getTrait", Type.getMethodDescriptor( Type.getType( Thing.class ), new Type[] { Type.getType( String.class ) } ) );
                    mv.visitVarInsn( ASTORE, 1 );
                } else {
                    // handled by normal inheritance, exceptions should have been thrown
                    if ( ! hardField.getType().isAssignableFrom( field.getType() ) ) {
                        mv.visitInsn( RETURN );
                    }
                }
                Label l1 = new Label();
                mv.visitJumpInsn(GOTO, l1);

                mv.visitLabel( l0 );
                mv.visitInsn( ACONST_NULL );
                mv.visitVarInsn( ASTORE, 1 );
                mv.visitLabel( l1 );
            } else if ( field.getType().isPrimitive() ) {
                if ( ! hardField.getType().equals( field.getType() ) ) {
                    mv.visitInsn( RETURN );
                }
            }

            if ( isHardField && CoreWrapper.class.isAssignableFrom( core.getDefinedClass() ) ) {
                logicalSetter( mv, field, masterName, this.trait, core, true );
            }
        }

        TraitFactory.invokeInjector( mv, masterName, trait, core, field, false, 1 );

        mv.visitInsn( RETURN );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();

    }




    protected void buildSoftSetter( ClassVisitor cw, FieldDefinition field, String proxy, ClassDefinition trait, ClassDefinition core ) {
        buildSoftSetter( cw, field, proxy, trait, core, BuildUtils.setterName( field.getName(), field.getTypeName() ), false );
    }

    protected void buildSoftSetter( ClassVisitor cw, FieldDefinition field, String proxy, ClassDefinition trait, ClassDefinition core, String setterName, boolean protect ) {
        String fieldName = field.getName();
        String type = field.getTypeName();

        MethodVisitor mv = cw.visitMethod( protect ? ACC_PROTECTED : ACC_PUBLIC,
                setterName,
                "(" + Type.getDescriptor( field.getType() ) + ")V",
                null,
                null );
        mv.visitCode();

        if ( core.isFullTraiting() ) {
            logicalSetter( mv, field, proxy, this.trait, core, true );
        }

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


    protected void buildSoftGetter( ClassVisitor cw, FieldDefinition field, String proxy, ClassDefinition trait, ClassDefinition core ) {
        buildSoftGetter( cw, field, proxy, trait, core, BuildUtils.getterName( field.getName(), field.getTypeName() ), false );
    }

    protected void buildSoftGetter( ClassVisitor cw, FieldDefinition field, String proxy, ClassDefinition trait, ClassDefinition core, String getterName, boolean protect ) {
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



    protected void buildExtendedMethods(ClassWriter cw, ClassDefinition trait, ClassDefinition core, BitSet mask ) {
        buildSynchFields( cw, TraitFactory.getProxyName( trait, core ), trait, core, mask );
    }

    protected void buildSynchFields( ClassWriter cw, String proxyName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {
        {
            MethodVisitor mv = cw.visitMethod( ACC_PRIVATE, "synchFields", Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] { Type.BOOLEAN_TYPE } ), null, null );
            mv.visitCode();

            if ( core.isFullTraiting() ) {
                Iterator<FieldDefinition> iter = trait.getFieldsDefinitions().iterator();
                for ( int j = 0; j < trait.getFieldsDefinitions().size(); j++ ) {
                    FieldDefinition fld = iter.next();
                    boolean hardField = ! TraitRegistry.isSoftField( fld, j, mask );
                    synchFieldLogical( mv, fld, proxyName, trait, core, hardField, j + 3 );
                }
            } else {
                for ( FieldDefinition fld : trait.getFieldsDefinitions() ) {
                    if ( fld.getInitExpr() != null ) {
                        synchField( mv, fld, proxyName );
                    }
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
        if ( BuildUtils.isPrimitive( fld.getTypeName() ) ) {
            mv.visitFieldInsn( GETSTATIC, BuildUtils.getInternalType( BuildUtils.box( fld.getTypeName() ) ), "TYPE", Type.getDescriptor( Class.class ) );
        } else {
            mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( fld.getTypeName() ) ) );
        }
        mv.visitMethodInsn( INVOKESTATIC,
                            Type.getInternalName( MVEL.class ),
                            "eval",
                            Type.getMethodDescriptor( Type.getType( Object.class ), new Type[] { Type.getType( String.class ), Type.getType( Class.class ) } ) );
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



    protected void synchFieldLogical( MethodVisitor mv, FieldDefinition fld, String proxyName, ClassDefinition trait, ClassDefinition core, boolean hardField, int j ) {

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxyName ), "object", Type.getDescriptor( core.getDefinedClass() ) );
        mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitableBean.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( TraitableBean.class ), "_getFieldTMS", Type.getMethodDescriptor( Type.getType( TraitFieldTMS.class ), new Type[] {} ) );
        mv.visitVarInsn( ASTORE, 2 );
        mv.visitVarInsn( ALOAD, 2 );
        // fld Name
        mv.visitLdcInsn( fld.resolveAlias() );
        // this
        mv.visitVarInsn( ALOAD, 0 );
        // init expr
        if ( fld.getInitExpr() != null ) {
            mv.visitLdcInsn( fld.getInitExpr() );
        } else {
            mv.visitInsn( ACONST_NULL );
        }
        // fld type
        if ( BuildUtils.isPrimitive( fld.getTypeName() ) ) {
//                mv.visitFieldInsn( GETSTATIC, BuildUtils.getInternalType( BuildUtils.box( fld.getTypeName() ) ), "TYPE", Type.getDescriptor( Class.class ) );
            mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( BuildUtils.box( fld.getTypeName() ) ) ) );
        } else {
            mv.visitLdcInsn( Type.getType( Type.getDescriptor( fld.getType() ) ) );
        }
        mv.visitVarInsn( ILOAD, 1 );
        mv.visitMethodInsn( INVOKEINTERFACE,
                            Type.getInternalName( TraitFieldTMS.class ),
                            "donField",
                            Type.getMethodDescriptor( Type.getType( Object.class ), new Type[] {
                                    Type.getType( String.class ), Type.getType( TraitType.class ), Type.getType( String.class ), Type.getType( Class.class ), Type.BOOLEAN_TYPE
                            } ) );

        mv.visitVarInsn( ASTORE, j );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ALOAD, j );

        if ( BuildUtils.isPrimitive( fld.getTypeName() ) ) {
            Label l0 = new Label();
            mv.visitJumpInsn( IFNULL, l0 );
            mv.visitVarInsn( ALOAD, j );
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( BuildUtils.box( fld.getTypeName() ) ) );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                BuildUtils.getInternalType( BuildUtils.box( fld.getTypeName() ) ),
                                BuildUtils.numericMorph( BuildUtils.box( fld.getTypeName() ) ),
                                Type.getMethodDescriptor( Type.getType( fld.getType() ), new Type[] {} ) );
            Label l1 = new Label();
            mv.visitJumpInsn( GOTO, l1 );
            mv.visitLabel( l0 );
            mv.visitInsn( BuildUtils.zero( fld.getTypeName() ) );
            mv.visitLabel( l1 );
        } else {
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( fld.getType() ) );
        }

        mv.visitMethodInsn( INVOKEVIRTUAL,
                            BuildUtils.getInternalType( proxyName ),
                            BuildUtils.setterName( fld.getName(), fld.getTypeName() ),
                            "(" + BuildUtils.getTypeDescriptor( fld.getTypeName() ) + ")" + Type.getDescriptor( void.class ) );
    }




    protected void shedField( MethodVisitor mv, FieldDefinition fld, String proxyName, ClassDefinition trait, ClassDefinition core, boolean hardField, int j ) {
        FieldDefinition coreField = core.getFieldByAlias( fld.resolveAlias() );

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxyName ), "object", Type.getDescriptor( core.getDefinedClass() ) );
        mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitableBean.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( TraitableBean.class ), "_getFieldTMS", Type.getMethodDescriptor( Type.getType( TraitFieldTMS.class ), new Type[] {} ) );

        mv.visitVarInsn( ASTORE, 1 );
        mv.visitVarInsn( ALOAD, 1 );
        // fld Name
        mv.visitLdcInsn( fld.resolveAlias() );
        // this
        mv.visitVarInsn( ALOAD, 0 );
        // fld type
        if ( BuildUtils.isPrimitive( fld.getTypeName() ) ) {
            mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( BuildUtils.box( fld.getTypeName() ) ) ) );
        } else {
            mv.visitLdcInsn( Type.getType( Type.getDescriptor( fld.getType() ) ) );
        }

        if ( hardField ) {
            if ( BuildUtils.isPrimitive( coreField.getTypeName() ) ) {
                mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( BuildUtils.box( coreField.getTypeName() ) ) ) );
            } else {
                mv.visitLdcInsn( Type.getType( Type.getDescriptor( coreField.getType() ) ) );
            }
        } else {
            mv.visitLdcInsn( Type.getType( Type.getDescriptor( Object.class ) ) );
        }


        mv.visitMethodInsn( INVOKEINTERFACE,
                            Type.getInternalName( TraitFieldTMS.class ),
                            "shedField",
                            Type.getMethodDescriptor( Type.getType( Object.class ), new Type[] {
                                    Type.getType( String.class ), Type.getType( TraitType.class ), Type.getType( Class.class ), Type.getType( Class.class )
                            } ) );

        mv.visitVarInsn( ASTORE, j );

        if ( hardField ) {
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxyName ), "object", Type.getDescriptor( core.getDefinedClass() ) );

            mv.visitVarInsn( ALOAD, j );
            if ( BuildUtils.isPrimitive( coreField.getTypeName() ) ) {
                Label l0 = new Label();
                mv.visitJumpInsn( IFNULL, l0 );
                mv.visitVarInsn( ALOAD, j );
                mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( BuildUtils.box( coreField.getTypeName() ) ) );
                mv.visitMethodInsn( INVOKEVIRTUAL,
                                    BuildUtils.getInternalType( BuildUtils.box( coreField.getTypeName() ) ),
                                    BuildUtils.numericMorph( BuildUtils.box( coreField.getTypeName() ) ),
                                    Type.getMethodDescriptor( Type.getType( coreField.getType() ), new Type[] {} ) );
                Label l1 = new Label();
                mv.visitJumpInsn( GOTO, l1 );
                mv.visitLabel( l0 );
                mv.visitInsn( BuildUtils.zero( coreField.getTypeName() ) );
                mv.visitLabel( l1 );
            } else {
                mv.visitTypeInsn( CHECKCAST, Type.getInternalName( coreField.getType() ) );
            }

            mv.visitMethodInsn( INVOKEVIRTUAL,
                                Type.getInternalName( core.getDefinedClass() ),
                                BuildUtils.setterName( coreField.getName(), coreField.getTypeName() ),
                                "(" + BuildUtils.getTypeDescriptor( coreField.getTypeName() ) + ")" + Type.getDescriptor( void.class ) );
        } else {
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxyName ), "map", Type.getDescriptor( Map.class ) );

            mv.visitLdcInsn( fld.resolveAlias() );
            mv.visitVarInsn( ALOAD, j );

            mv.visitMethodInsn( INVOKEINTERFACE,
                                Type.getInternalName( Map.class ),
                                "put",
                                Type.getMethodDescriptor( Type.getType( Object.class ), new Type[] { Type.getType( Object.class ), Type.getType( Object.class ) } ) );
        }
    }



}