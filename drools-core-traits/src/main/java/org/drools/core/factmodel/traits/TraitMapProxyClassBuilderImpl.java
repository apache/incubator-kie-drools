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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.Map;
import java.util.Set;

import org.drools.core.factmodel.BuildUtils;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.factmodel.traits.TraitBuilderUtil.MixinInfo;
import org.drools.core.util.ExternalizableLinkedHashMap;
import org.kie.api.definition.type.FactField;
import org.mvel2.asm.ClassVisitor;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.FieldVisitor;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Type;

import static org.drools.core.factmodel.traits.TraitBuilderUtil.buildMixinMethods;
import static org.drools.core.factmodel.traits.TraitBuilderUtil.findMixinInfo;
import static org.drools.core.factmodel.traits.TraitBuilderUtil.getMixinName;
import static org.drools.core.rule.builder.dialect.asm.ClassGenerator.createClassWriter;

public class TraitMapProxyClassBuilderImpl extends AbstractProxyClassBuilderImpl implements TraitProxyClassBuilder, Serializable {


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

        String internalWrapper  = BuildUtils.getInternalType( name );
        String internalProxy    = BuildUtils.getInternalType( masterName );

        String descrCore        = Type.getDescriptor( core.getDefinedClass() );
        String internalCore     = Type.getInternalName( core.getDefinedClass() );
        String internalTrait    = Type.getInternalName( traitClass );

        MixinInfo mixinInfo = findMixinInfo(traitClass);

        ClassWriter cw = createClassWriter( classLoader,
                                            ACC_PUBLIC + ACC_SUPER,
                                            internalProxy,
                                            null,
                                            Type.getInternalName( proxyBaseClass ),
                                            new String[]{ internalTrait, Type.getInternalName( Serializable.class ) } );

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
            fv = cw.visitField( ACC_PUBLIC, "map", Type.getDescriptor( Map.class ), "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null );
            fv.visitEnd();
        }

        if ( mixinInfo != null ) {
            for ( Class<?> mixinClass : mixinInfo.mixinClasses ) {
                {
                    fv = cw.visitField( ACC_PRIVATE,
                                        getMixinName(mixinClass),
                                        BuildUtils.getTypeDescriptor( mixinClass.getName() ),
                                        null, null );
                    fv.visitEnd();
                }
            }
        }


        {
            mv = cw.visitMethod( ACC_PUBLIC, "<init>",
                                             "(" + descrCore + Type.getDescriptor( Map.class ) + Type.getDescriptor( BitSet.class ) + Type.getDescriptor( BitSet.class ) + Type.getDescriptor( boolean.class  ) + ")V",
                                             "(" + descrCore + "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;" + Type.getDescriptor( BitSet.class ) + Type.getDescriptor( BitSet.class ) + Type.getDescriptor( boolean.class  ) + ")V", null);
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( proxyBaseClass ), "<init>", "()V", false );


            mv.visitVarInsn( ALOAD, 2 );
            Label l0 = new Label();
            mv.visitJumpInsn( IFNONNULL, l0 );
            mv.visitTypeInsn( NEW, Type.getInternalName( ExternalizableLinkedHashMap.class ) );
            mv.visitInsn( DUP );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( ExternalizableLinkedHashMap.class ), "<init>", "()V", false );
            mv.visitVarInsn( ASTORE, 2  );
            mv.visitLabel( l0 );


            if ( mixinInfo != null ) {
                for ( Class<?> mixinClass : mixinInfo.mixinClasses ) {
                    String mixin = getMixinName( mixinClass );
                    try {
                        Class actualArg = getPossibleConstructor( mixinClass, trait.getDefinedClass() );

                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitTypeInsn( NEW, Type.getInternalName( mixinClass ) );
                        mv.visitInsn( DUP );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKESPECIAL,
                                            Type.getInternalName( mixinClass ),
                                            "<init>",
                                            "(" + Type.getDescriptor( actualArg ) + ")V",
                                            false );
                        mv.visitFieldInsn( PUTFIELD,
                                           internalProxy,
                                           mixin,
                                           Type.getDescriptor( mixinClass ) );
                    } catch (NoSuchMethodException nsme) {
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitTypeInsn( NEW, Type.getInternalName( mixinClass ) );
                        mv.visitInsn( DUP );
                        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( mixinClass ), "<init>", "()V", false );
                        mv.visitFieldInsn( PUTFIELD,
                                           internalProxy,
                                           mixin,
                                           Type.getDescriptor( mixinClass ) );
                    }
                }
            }

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "object", descrCore );

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "map", Type.getDescriptor( Map.class ) );

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 3 );
            mv.visitMethodInsn( INVOKEVIRTUAL, internalProxy, "setTypeCode", Type.getMethodDescriptor( Type.VOID_TYPE, Type.getType( BitSet.class ) ), false );


            mv.visitVarInsn( ALOAD, 0 );
            mv.visitTypeInsn( NEW, internalWrapper );
            mv.visitInsn( DUP );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKESPECIAL, internalWrapper, "<init>", "(" + descrCore + Type.getDescriptor( Map.class ) + ")V", false );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "fields", Type.getDescriptor( Map.class ) );


            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEVIRTUAL, internalCore, "_getDynamicProperties", "()" + Type.getDescriptor( Map.class ), false );
            Label l1 = new Label();
            mv.visitJumpInsn( IFNONNULL, l1 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEVIRTUAL, internalCore, "_setDynamicProperties", "(" + Type.getDescriptor( Map.class ) + ")V", false );
            mv.visitLabel( l1 );

            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEVIRTUAL, internalCore, "_getTraitMap",  "()" + Type.getDescriptor( Map.class ), false );
            Label l2 = new Label();
            mv.visitJumpInsn( IFNONNULL, l2 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitTypeInsn( NEW, Type.getInternalName( TraitTypeMap.class ) );
            mv.visitInsn( DUP );
            mv.visitTypeInsn( NEW, Type.getInternalName( ExternalizableLinkedHashMap.class ) );
            mv.visitInsn( DUP );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( ExternalizableLinkedHashMap.class ), "<init>", "()V", false );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( TraitTypeMap.class ), "<init>", "(" + Type.getDescriptor( Map.class ) + ")V", false );
            mv.visitMethodInsn( INVOKEVIRTUAL, internalCore, "_setTraitMap", "(" + Type.getDescriptor( Map.class ) + ")V", false );
            mv.visitLabel( l2 );

            // core._setBottomTypeCode()
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 4 );
            mv.visitMethodInsn( INVOKEVIRTUAL, internalCore, "_setBottomTypeCode", Type.getMethodDescriptor( Type.VOID_TYPE, Type.getType( BitSet.class ) ), false );

            // core.addTrait
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitLdcInsn( trait.getName().endsWith( TraitFactory.SUFFIX ) ? trait.getName().replace(  TraitFactory.SUFFIX , "" ) : trait.getName() );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, internalCore, "addTrait",  Type.getMethodDescriptor( Type.VOID_TYPE, Type.getType( String.class ), Type.getType( Thing.class ) ), false );

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ILOAD, 5 );
            mv.visitMethodInsn( INVOKESPECIAL, internalProxy, "synchFields", Type.getMethodDescriptor( Type.VOID_TYPE, Type.BOOLEAN_TYPE ), false );

            mv.visitInsn( RETURN );
//            mv.visitMaxs( 5, 3 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }


        {
            mv = cw.visitMethod( ACC_PUBLIC, "writeExternal", "(" + Type.getDescriptor( ObjectOutput.class ) + ")V", null, new String[] { Type.getInternalName( IOException.class ) } );
            mv.visitCode();

            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, internalProxy, "getObject", "()" + Type.getDescriptor( TraitableBean.class ), false );
            mv.visitMethodInsn( INVOKEINTERFACE,
                                Type.getInternalName( ObjectOutput.class ),
                                "writeObject",
                                "(" + Type.getDescriptor( Object.class ) + ")V",
                                true );


            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, internalProxy, "map", Type.getDescriptor( Map.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE,
                                Type.getInternalName( ObjectOutput.class ),
                                "writeObject",
                                "(" + Type.getDescriptor( Object.class ) + ")V",
                                true);

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKESPECIAL,
                                Type.getInternalName( proxyBaseClass ),
                                "writeExternal",
                                "(" + Type.getDescriptor( ObjectOutput.class ) + ")V",
                                false );


            mv.visitInsn( RETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "readExternal", "(" + Type.getDescriptor( ObjectInput.class )+ ")V", 
                                 null, new String[] { Type.getInternalName( IOException.class ), Type.getInternalName( ClassNotFoundException.class ) } );
            mv.visitCode();

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEINTERFACE,
                                Type.getInternalName( ObjectInput.class ),
                                "readObject",
                                "()" + Type.getDescriptor( Object.class ),
                                true );
            mv.visitTypeInsn( CHECKCAST, internalCore );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "object", descrCore );


            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEINTERFACE,
                                Type.getInternalName( ObjectInput.class ),
                                "readObject",
                                "()" + Type.getDescriptor( Object.class ),
                                true );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( Map.class ) );
            mv.visitFieldInsn( PUTFIELD, internalProxy, "map", Type.getDescriptor( Map.class ) );


            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKESPECIAL,
                                Type.getInternalName( proxyBaseClass ),
                                "readExternal",
                                "(" + Type.getDescriptor( ObjectInput.class ) + ")V",
                                false );

            mv.visitInsn( RETURN );
//            mv.visitMaxs( 3, 2 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }


	    helpBuildClass( core, cw, internalProxy, descrCore, mask );

	    buildFields( core, mask, masterName, mixinInfo, cw );

        buildKeys( core, masterName, cw );

        buildMixinMethods( masterName, mixinInfo, cw );

        buildCommonMethods( cw, masterName );

        buildExtendedMethods( cw, trait, core );

        buildShadowMethods( cw, trait, core );

        cw.visitEnd();

        return cw.toByteArray();

    }

    protected void buildKeys( ClassDefinition core, String masterName, ClassWriter cw ) {
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
            buildKeyedEqualityMethods( cw, trait, masterName );
        }
    }

    protected void buildFields( ClassDefinition core, BitSet mask, String masterName, MixinInfo mixinInfo, ClassWriter cw ) {
        int j = 0;
        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {

            boolean hardField = ! TraitRegistry.isSoftField( field, j++, mask );

            if ( core.isFullTraiting() ) {
                buildLogicalGetter( cw, field, masterName, core );
                if ( hardField ) {
                    buildHardSetter( cw, field, masterName, trait, core );
                } else {
                    buildSoftSetter( cw, field, masterName, core );
                }
            } else {
                if ( ! hardField ) {
                    if (mixinInfo == null || !mixinInfo.isMixinGetter( field )) {
                        buildSoftGetter( cw, field, masterName );
                        buildSoftSetter( cw, field, masterName, core );
                    }
                } else {
                    buildHardGetter( cw, field, masterName, trait, core );
                    buildHardSetter( cw, field, masterName, trait, core );
                }
            }
        }
    }

    protected void buildShadowMethods( ClassWriter cw, ClassDefinition trait, ClassDefinition core ) {
        for ( Method m : trait.getDefinedClass().getMethods() ) {
            if ( ! TraitFactory.excludeFromShadowing( m, trait ) ) {
                Method q;
                try {
                    q = core.getDefinedClass().getMethod( m.getName(), m.getParameterTypes() );
                    if ( TraitFactory.isCompatible( m, q ) ) {
                        buildShadowMethod( cw, trait, core, m );
                    }
                } catch ( NoSuchMethodException e ) {
                    // nothing to do here
                }
            }
        }
    }


    protected Class getPossibleConstructor( Class<?> klass, Class<?> arg ) throws NoSuchMethodException {
        Constructor[] ctors = klass.getConstructors();

        for ( Constructor c : ctors ) {
            Class<?>[] cpars = c.getParameterTypes();

            if ( cpars.length != 1 || ! cpars[0].isAssignableFrom( arg ) ) {
                continue;
            }

            return cpars[0];
        }
        throw new NoSuchMethodException( "Constructor for " + klass + " using " + arg + " not found " );
    }

	protected void buildHardGetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition proxy, ClassDefinition core ) {
		buildHardGetter( cw, field, masterName, proxy, core, BuildUtils.getterName( field.getName(), field.getTypeName() ), ACC_PUBLIC );
	}

	protected void buildHardGetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition proxy, ClassDefinition core, String getterName, int accessLevel ) {
		Class fieldType = field.getType();


		MethodVisitor mv = cw.visitMethod( accessLevel,
		                                   getterName,
		                                   "()" + Type.getDescriptor( field.getType() ),
		                                   null,
		                                   null);
		mv.visitCode();

		TraitFactory.invokeExtractor(mv, masterName, core, field );

		if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {
			mv.visitTypeInsn( CHECKCAST, Type.getInternalName( fieldType ) );
		}

		mv.visitInsn( BuildUtils.returnType ( field.getTypeName() ) );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();

    }

    protected void buildHardSetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition trait, ClassDefinition core ) {
        buildHardSetter(cw, field, masterName, trait, core, BuildUtils.setterName( field.getName()), ACC_PUBLIC  );
    }

    protected void buildSoftSetter( ClassVisitor cw, FieldDefinition field, String proxy, ClassDefinition core ) {
        buildSoftSetter(cw, field, proxy, core, BuildUtils.setterName( field.getName()), ACC_PUBLIC );
    }

    protected void buildSoftSetter( ClassVisitor cw, FieldDefinition field, String proxy, ClassDefinition core, String setterName, int accessMode ) {
        String type = field.getTypeName();


        MethodVisitor mv = cw.visitMethod( accessMode,
                                           setterName,
                                           "(" + BuildUtils.getTypeDescriptor( type ) + ")V",
                                           null,
                                           null );
        mv.visitCode();

        if ( core.isFullTraiting() ) {
            logicalSetter( mv, field, proxy, core );
        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxy ), "map", Type.getDescriptor( Map.class ) );
        mv.visitLdcInsn( field.resolveAlias() );
        mv.visitVarInsn( BuildUtils.varType( type ), 1 );
        if ( BuildUtils.isPrimitive( type ) ) {
            TraitFactory.valueOf( mv, type );
        }
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "put", 
                            "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ), true );
        mv.visitInsn( POP );
        mv.visitInsn( RETURN );

        mv.visitMaxs( 0, 0 );
        mv.visitEnd();

    }



    protected void buildSoftGetter( ClassVisitor cw, FieldDefinition field, String proxy ) {
        buildSoftGetter( cw, field, proxy, BuildUtils.getterName( field.getName(), field.getTypeName() ), ACC_PUBLIC );
    }

    protected void buildSoftGetter( ClassVisitor cw, FieldDefinition field, String proxy, String getterName, int accessLevel ) {
        String type = field.getTypeName();

        MethodVisitor mv = cw.visitMethod( accessLevel,
                                           getterName,
                                           "()"+ BuildUtils.getTypeDescriptor( type ),
                                           null,
                                           null );
        mv.visitCode();
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxy ), "map", Type.getDescriptor( Map.class ) );
        mv.visitLdcInsn( field.resolveAlias() );
        mv.visitMethodInsn( INVOKEINTERFACE,
                            Type.getInternalName( Map.class ),
                            "get",
                            "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
                            true );

        String actualType = BuildUtils.isPrimitive( type ) ? BuildUtils.box( type ) : type;

            mv.visitVarInsn( ASTORE, 1 );
            mv.visitVarInsn( ALOAD, 1 );
            Label l0 = new Label();
            mv.visitJumpInsn( IFNULL, l0 );
            mv.visitVarInsn( ALOAD, 1 );

            mv.visitTypeInsn( INSTANCEOF, BuildUtils.getInternalType( actualType ) );
            mv.visitJumpInsn( IFEQ, l0 );
            mv.visitVarInsn( ALOAD, 1 );

            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( actualType ) );

        if ( BuildUtils.isPrimitive( type ) ) {
            TraitFactory.primitiveValue( mv, type );
            mv.visitInsn( BuildUtils.returnType( type ) );
            mv.visitLabel( l0 );
            mv.visitInsn( BuildUtils.zero( type ) );
            mv.visitInsn( BuildUtils.returnType( type ) );
        } else {
            mv.visitInsn( ARETURN );
            mv.visitLabel( l0 );
            mv.visitInsn( ACONST_NULL );
            mv.visitInsn( ARETURN );
        }

        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }

	protected void buildCommonMethods(ClassWriter cw, String proxy ) {

        String proxyType = BuildUtils.getInternalType( proxy );

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "toString", "()" + Type.getDescriptor( String.class ), null, null );
            mv.visitCode();
            mv.visitTypeInsn( NEW, Type.getInternalName( StringBuilder.class ) );
            mv.visitInsn( DUP );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( StringBuilder.class ), "<init>", "()V", false );
            mv.visitLdcInsn( "(@" + proxy + ") : " );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append", 
                                "(" + Type.getDescriptor( String.class ) + ")" + Type.getDescriptor( StringBuilder.class ), false );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, "getFields", "()" + Type.getDescriptor( Map.class ), false );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "entrySet", "()" + Type.getDescriptor( Set.class ), true );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "toString", "()" + Type.getDescriptor( String.class ), false );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append", 
                                "(" + Type.getDescriptor( String.class ) + ")" + Type.getDescriptor( StringBuilder.class ), false );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "toString", "()" + Type.getDescriptor( String.class ), false );
            mv.visitInsn( ARETURN );
//            mv.visitMaxs( 2, 1 );
            mv.visitMaxs( 0, 0 );
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
        mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "getClass", "()" + Type.getDescriptor( Class.class ), false );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "getClass", "()" + Type.getDescriptor( Class.class ), false );
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
                                        "()" + Type.getDescriptor( field.getType() ), false );
                    Label l11 = new Label();
                    mv.visitJumpInsn( IFNULL, l11 );
                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ), false );
                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ), false );
                    mv.visitMethodInsn( INVOKEVIRTUAL,
                                        BuildUtils.getInternalType( field.getTypeName() ),
                                        "equals",
                                        "(" + Type.getDescriptor( Object.class ) + ")Z",
                                        false );
                    Label l12 = new Label();
                    mv.visitJumpInsn( IFNE, l12 );
                    Label l13 = new Label();
                    mv.visitJumpInsn( GOTO, l13 );
                    mv.visitLabel( l11 );
                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ), false );
                    mv.visitJumpInsn( IFNULL, l12 );
                    mv.visitLabel( l13 );
                    mv.visitInsn( ICONST_0 );
                    mv.visitInsn( IRETURN );
                    mv.visitLabel( l12 );

                } else if ( "double".equals( field.getTypeName() ) ) {

                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ), false );
                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ), false );
                    mv.visitMethodInsn( INVOKESTATIC, Type.getInternalName( Double.class ), "compare", "(DD)I", false );
                    Label l5 = new Label();
                    mv.visitJumpInsn( IFEQ, l5 );
                    mv.visitInsn( ICONST_0 );
                    mv.visitInsn( IRETURN );
                    mv.visitLabel( l5 );

                    x = Math.max( x, 4 );

                } else if ( "float".equals( field.getTypeName() ) ) {

                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ), false );
                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ), false );
                    mv.visitMethodInsn( INVOKESTATIC, Type.getInternalName( Float.class ), "compare", "(FF)I", false );
                    Label l6 = new Label();
                    mv.visitJumpInsn( IFEQ, l6 );
                    mv.visitInsn( ICONST_0 );
                    mv.visitInsn( IRETURN );
                    mv.visitLabel( l6 );


                }  else if ( "long".equals( field.getTypeName() ) ) {

                    mv.visitVarInsn( ALOAD, 0 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ), false );
                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ), false );
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
                                        "()" + Type.getDescriptor( field.getType() ), false );
                    mv.visitVarInsn( ALOAD, 2 );
                    mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                        "()" + Type.getDescriptor( field.getType() ), false );
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

            int x = 2;
            int y = 2;
            for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
                if ( field.isKey() ) {
                    if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {

                        mv.visitIntInsn( BIPUSH, 31 );
                        mv.visitVarInsn( ILOAD, 1 );
                        mv.visitInsn( IMUL );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + Type.getDescriptor( field.getType() ), false );
                        Label l8 = new Label();
                        mv.visitJumpInsn( IFNULL, l8 );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + Type.getDescriptor( field.getType() ), false );
                        mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( field.getTypeName() ), "hashCode", "()I", false );
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
                                            "()" + Type.getDescriptor( field.getType() ), false );
                        mv.visitInsn( DCONST_0 ); 
                        mv.visitInsn( DCMPL );
                        Label l2 = new Label();
                        mv.visitJumpInsn( IFEQ, l2 );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + Type.getDescriptor( field.getType() ), false );
                        mv.visitMethodInsn( INVOKESTATIC, Type.getInternalName( Double.class ), "doubleToLongBits", "(D)J", false );
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
                                            "()" + Type.getDescriptor( field.getType() ), false );
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
                                            "()" + Type.getDescriptor( field.getType() ), false );
                        mv.visitInsn( FCONST_0 );
                        mv.visitInsn( FCMPL );
                        Label l6 = new Label();
                        mv.visitJumpInsn( IFEQ, l6 );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ), 
                                            "()" + Type.getDescriptor( field.getType() ), false );
                        mv.visitMethodInsn( INVOKESTATIC, Type.getInternalName( Float.class ), "floatToIntBits", "(F)I", false );
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
                                            "()" + Type.getDescriptor( field.getType() ), false );
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL, proxyType, BuildUtils.getterName( field.getName(), field.getTypeName() ),
                                            "()" + Type.getDescriptor( field.getType() ), false );
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
                                            "()" + Type.getDescriptor( field.getType() ), false );
                        mv.visitInsn( IADD );
                        mv.visitVarInsn( ISTORE, 1 );

                    }
                }

            }
            mv.visitVarInsn( ILOAD, 1 );
            mv.visitInsn( IRETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
    }


    protected void buildExtendedMethods( ClassWriter cw, ClassDefinition trait, ClassDefinition core ) {
        buildSynchFields( cw, TraitFactory.getProxyName( trait, core ), trait, core );
    }


}