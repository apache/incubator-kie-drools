/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.traits.core.factmodel;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;

import org.drools.compiler.builder.impl.classbuilder.BuildUtils;
import org.drools.base.factmodel.ClassDefinition;
import org.drools.base.factmodel.FieldDefinition;
import org.drools.base.factmodel.traits.CoreWrapper;
import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.Trait;
import org.drools.base.factmodel.traits.TraitFieldTMS;
import org.drools.base.factmodel.traits.TraitType;
import org.drools.base.factmodel.traits.TraitableBean;
import org.drools.mvel.asm.AsmUtil;
import org.mvel2.MVEL;
import org.mvel2.asm.ClassVisitor;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Opcodes;
import org.mvel2.asm.Type;

public abstract class AbstractProxyClassBuilderImpl implements TraitProxyClassBuilder, Serializable {


    protected transient ClassDefinition trait;

	protected transient Class<?> proxyBaseClass;

	protected transient TraitRegistryImpl traitRegistryImpl;

    protected ClassDefinition getTrait() {
        return trait;
    }

    public void init( ClassDefinition trait, Class<?> baseClass, TraitRegistryImpl traitRegistryImpl) {
        this.trait = trait;
        this.proxyBaseClass = baseClass;
        this.traitRegistryImpl = traitRegistryImpl;
    }


	protected void helpBuildClass( ClassDefinition core,
	                               ClassWriter cw,
	                               String internalProxy,
	                               String descrCore,
	                               BitSet mask ) throws IOException,
			SecurityException,
			IllegalArgumentException,
			ClassNotFoundException,
			NoSuchMethodException,
			IllegalAccessException,
			InvocationTargetException,
			InstantiationException,
			NoSuchFieldException {

    	MethodVisitor mv;

		{
			mv = cw.visitMethod( ACC_STATIC, "<clinit>", "()V", null, null );
			mv.visitCode();
			mv.visitLdcInsn( Type.getType( Type.getDescriptor( trait.getDefinedClass() ) ) );
			mv.visitMethodInsn( INVOKEVIRTUAL,
			                    Type.getInternalName( Class.class ), "getName", "()" + Type.getDescriptor( String.class ), false);
			mv.visitFieldInsn(PUTSTATIC,
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
			mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( proxyBaseClass ), "<init>", "()V", false );

			mv.visitInsn( RETURN );
//            mv.visitMaxs( 1, 1 );
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
			mv = cw.visitMethod(ACC_PUBLIC, "getObject", "()" + Type.getDescriptor( TraitableBean.class ), null, null);
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
			mv = cw.visitMethod( ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "getCore", "()" + Type.getDescriptor( Object.class ), null, null );
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, internalProxy, "getCore", "()" + descrCore, false );
			mv.visitInsn(ARETURN);
//            mv.visitMaxs( 1, 1 );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}

		{
			mv = cw.visitMethod( ACC_PUBLIC, "_isTop", "()Z", null, null );
			mv.visitCode();
			mv.visitInsn( Thing.class.equals(trait.getDefinedClass() ) ? ICONST_1 : ICONST_0 );
			mv.visitInsn( IRETURN );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}

		{
			mv = cw.visitMethod( ACC_PUBLIC, "shed", Type.getMethodDescriptor( Type.VOID_TYPE ), null, null );
			mv.visitCode();

			if ( core.isFullTraiting() ) {
				Iterator<FieldDefinition> iter = trait.getFieldsDefinitions().iterator();
				for ( int j = 0; j < trait.getFieldsDefinitions().size(); j++ ) {
					FieldDefinition fld = iter.next();
					boolean hardField = ! TraitRegistryImpl.isSoftField(fld, j, mask );
					shedField( mv, fld, internalProxy, core, hardField, j + 2 );
				}
			}

			mv.visitInsn( RETURN );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}

	}


	protected void buildShadowMethod( ClassWriter cw, ClassDefinition trait, ClassDefinition core, Method m ) {
		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
		                                   m.getName(),
		                                   Type.getMethodDescriptor( m ),
		                                   null,
		                                   null );

		mv.visitCode();
		mv.visitVarInsn( ALOAD, 0 );
		mv.visitMethodInsn( INVOKEVIRTUAL,
		                    BuildUtils.getInternalType(TraitFactoryImpl.getProxyName(trait, core ) ),
		                    "getCore",
		                    Type.getMethodDescriptor( Type.getType( core.getDefinedClass() ) ),
		                    false );

		for ( int j = 0; j < m.getParameterTypes().length; j++ ) {
			mv.visitVarInsn( AsmUtil.varType( m.getParameterTypes()[ j ].getName() ), j + 1 );
		}
		mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( core.getDefinedClass() ), m.getName(), Type.getMethodDescriptor( m ), core.getDefinedClass().isInterface() );

		mv.visitInsn( AsmUtil.returnType( m.getReturnType().getName() ) );

		mv.visitMaxs( 0, 0 );
		mv.visitEnd();

	}




	protected void shedField( MethodVisitor mv, FieldDefinition fld, String proxyName, ClassDefinition core, boolean hardField, int j ) {
		FieldDefinition coreField = core.getFieldByAlias( fld.resolveAlias() );

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxyName ), "object", Type.getDescriptor( core.getDefinedClass() ) );
		mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitableBean.class ) );
		mv.visitMethodInsn( INVOKEINTERFACE,
		                    Type.getInternalName( TraitableBean.class ),
		                    "_getFieldTMS",
		                    Type.getMethodDescriptor( Type.getType( TraitFieldTMS.class ) ),
		                    true );

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
		                    Type.getMethodDescriptor( Type.getType( Object.class ),
		                                              Type.getType( String.class ),
		                                              Type.getType( TraitType.class ),
		                                              Type.getType( Class.class ),
		                                              Type.getType( Class.class ) ),
		                    true );

		mv.visitVarInsn( ASTORE, j );

		if ( hardField ) {
			mv.visitVarInsn( ALOAD, 0 );
			mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxyName ), "object", Type.getDescriptor( core.getDefinedClass() ) );

			mv.visitVarInsn( ALOAD, j );
			fixPrimitive( mv, coreField, j );

			mv.visitMethodInsn( INVOKEVIRTUAL,
			                    Type.getInternalName( core.getDefinedClass() ),
			                    BuildUtils.setterName( coreField.getName()),
			                    "(" + BuildUtils.getTypeDescriptor( coreField.getTypeName() ) + ")" + Type.getDescriptor( void.class ),
			                    false );
		} else {
			mv.visitVarInsn( ALOAD, 0 );
			mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxyName ), "map", Type.getDescriptor( Map.class ) );

			mv.visitLdcInsn( fld.resolveAlias() );
			mv.visitVarInsn( ALOAD, j );

			mv.visitMethodInsn( INVOKEINTERFACE,
			                    Type.getInternalName( Map.class ),
			                    "put",
			                    Type.getMethodDescriptor( Type.getType( Object.class ), Type.getType( Object.class ), Type.getType( Object.class ) ),
			                    true );
		}
	}

	private void fixPrimitive( MethodVisitor mv, FieldDefinition coreField, int j ) {
		if ( BuildUtils.isPrimitive( coreField.getTypeName() ) ) {
			Label l0 = new Label();
			mv.visitJumpInsn( IFNULL, l0 );
			mv.visitVarInsn( ALOAD, j );
			mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( BuildUtils.box( coreField.getTypeName() ) ) );
			mv.visitMethodInsn( INVOKEVIRTUAL,
			                    BuildUtils.getInternalType( BuildUtils.box( coreField.getTypeName() ) ),
			                    BuildUtils.numericMorph( BuildUtils.box( coreField.getTypeName() ) ),
			                    Type.getMethodDescriptor( Type.getType( coreField.getType() ) ),
			                    false );
			Label l1 = new Label();
			mv.visitJumpInsn( GOTO, l1 );
			mv.visitLabel( l0 );
			mv.visitInsn( AsmUtil.zero( coreField.getTypeName() ) );
			mv.visitLabel( l1 );
		} else {
			mv.visitTypeInsn( CHECKCAST, Type.getInternalName( coreField.getType() ) );
		}
	}

	protected void buildHardSetter( ClassVisitor cw, FieldDefinition field, String proxyName, ClassDefinition trait, ClassDefinition core, String setterName, int accessMode ) {
		MethodVisitor mv = cw.visitMethod( accessMode,
		                                   setterName,
		                                   "(" + Type.getDescriptor( field.getType() ) + ")V",
		                                   null,
		                                   null );
		mv.visitCode();

		if ( core.isFullTraiting() ) {
			helpSet( core, field, mv, proxyName );
		}

		TraitFactoryImpl.invokeInjector(mv, proxyName, core, field, false, 1 );

		mv.visitInsn( RETURN );

		mv.visitMaxs( 0, 0 );
		mv.visitEnd();

	}


	protected void synchFieldLogical( MethodVisitor mv, FieldDefinition fld, String proxyName, ClassDefinition core, int j ) {

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxyName ), "object", Type.getDescriptor( core.getDefinedClass() ) );
		mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitableBean.class ) );
		mv.visitMethodInsn( INVOKEINTERFACE,
		                    Type.getInternalName( TraitableBean.class ),
		                    "_getFieldTMS",
		                    Type.getMethodDescriptor( Type.getType( TraitFieldTMS.class ) ),
		                    true );
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
		                    Type.getMethodDescriptor( Type.getType( Object.class ),
		                                              Type.getType( String.class ),
		                                              Type.getType( TraitType.class ),
		                                              Type.getType( String.class ),
		                                              Type.getType( Class.class ),
		                                              Type.BOOLEAN_TYPE ),
		                    true );

		mv.visitVarInsn( ASTORE, j );
		mv.visitVarInsn( ALOAD, 0 );
		mv.visitVarInsn( ALOAD, j );

		fixPrimitive( mv, fld, j );

		mv.visitMethodInsn( INVOKEVIRTUAL,
		                    BuildUtils.getInternalType( proxyName ),
		                    BuildUtils.setterName( fld.getName()),
		                    "(" + BuildUtils.getTypeDescriptor( fld.getTypeName() ) + ")" + Type.getDescriptor( void.class ),
		                    false );
	}




	protected void synchField( MethodVisitor mv, FieldDefinition fld, String proxyName ) {
		mv.visitVarInsn( ALOAD, 0 );
		mv.visitMethodInsn( INVOKEVIRTUAL,
		                    BuildUtils.getInternalType( proxyName ),
		                    BuildUtils.getterName( fld.getName(), fld.getTypeName() ),
		                    "()" + BuildUtils.getTypeDescriptor( fld.getTypeName() ),
		                    false );

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
		                    Type.getMethodDescriptor( Type.getType( Object.class ), Type.getType( String.class ), Type.getType( Class.class ) ),
		                    false );
		if ( BuildUtils.isPrimitive( fld.getTypeName() ) ) {
			mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( BuildUtils.box( fld.getTypeName() ) ) );
			mv.visitMethodInsn( INVOKEVIRTUAL,
			                    BuildUtils.getInternalType( BuildUtils.box( fld.getTypeName() ) ),
			                    BuildUtils.numericMorph( BuildUtils.box( fld.getTypeName() ) ),
			                    "()" + BuildUtils.getTypeDescriptor( fld.getTypeName() ),
			                    false );
		} else {
			mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( fld.getTypeName() ) );
		}
		mv.visitMethodInsn( INVOKEVIRTUAL,
		                    BuildUtils.getInternalType( proxyName ),
		                    BuildUtils.setterName( fld.getName()),
		                    "(" + BuildUtils.getTypeDescriptor( fld.getTypeName() ) + ")" + Type.getDescriptor( void.class ),
		                    false );
		if ( ! BuildUtils.isPrimitive( fld.getTypeName() ) ) {
			mv.visitLabel( l0 );
		}
	}



	protected void buildSynchFields( ClassWriter cw, String proxyName, ClassDefinition trait, ClassDefinition core ) {
		{
			MethodVisitor mv = cw.visitMethod( ACC_PRIVATE, "synchFields", Type.getMethodDescriptor( Type.VOID_TYPE, Type.BOOLEAN_TYPE ), null, null );
			mv.visitCode();
			if ( core.isFullTraiting() ) {
				Iterator<FieldDefinition> iter = trait.getFieldsDefinitions().iterator();
				for ( int j = 0; j < trait.getFieldsDefinitions().size(); j++ ) {
					FieldDefinition fld = iter.next();
					//boolean hardField = ! TraitRegistry.isSoftField( fld, j, mask );
					synchFieldLogical( mv, fld, proxyName, core, j + 3 );
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



	protected void logicalSetter( MethodVisitor mv, FieldDefinition field, String proxyName, ClassDefinition core ) {
		String fieldType = field.getTypeName();
		int reg = 1 + BuildUtils.sizeOf( fieldType );

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxyName ), "object", Type.getDescriptor( core.getDefinedClass() ) );
		mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitableBean.class ) );
		mv.visitMethodInsn( INVOKEINTERFACE,
		                    Type.getInternalName( TraitableBean.class ),
		                    "_getFieldTMS",
		                    Type.getMethodDescriptor( Type.getType( TraitFieldTMS.class ) ),
		                    true );

		mv.visitVarInsn( ASTORE, reg );
		mv.visitVarInsn( ALOAD, reg );

		mv.visitLdcInsn( field.resolveAlias() );
		if ( BuildUtils.isPrimitive( fieldType ) ) {
			mv.visitVarInsn( AsmUtil.varType( fieldType ), 1 );
			mv.visitMethodInsn( Opcodes.INVOKESTATIC,
			                    BuildUtils.getInternalType( BuildUtils.box( fieldType ) ),
			                    "valueOf",
			                    Type.getMethodDescriptor( Type.getType( BuildUtils.getTypeDescriptor( BuildUtils.box( fieldType ) ) ), Type.getType( BuildUtils.getTypeDescriptor( fieldType ) ) ),
			                    false );
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
		                    Type.getMethodDescriptor( Type.getType( Object.class ),
		                                              Type.getType( String.class ),
		                                              Type.getType( Object.class ),
		                                              Type.getType( Class.class ) ),
		                    true );

		mv.visitVarInsn( ASTORE, 1 );
		mv.visitVarInsn( ALOAD, 1 );

		if ( BuildUtils.isPrimitive( fieldType ) ) {

			Label l0 = new Label();
			mv.visitJumpInsn( IFNULL, l0 );
			mv.visitVarInsn( ALOAD, 1 );
			Label l1 = new Label();
			mv.visitJumpInsn( GOTO, l1 );
			mv.visitLabel( l0 );
			mv.visitInsn( AsmUtil.zero( fieldType ) );
			mv.visitMethodInsn( Opcodes.INVOKESTATIC,
			                    BuildUtils.getInternalType( BuildUtils.box( fieldType ) ),
			                    "valueOf",
			                    Type.getMethodDescriptor( Type.getType( BuildUtils.getTypeDescriptor( BuildUtils.box( fieldType ) ) ), Type.getType( BuildUtils.getTypeDescriptor( fieldType ) ) ),
			                    false );
			mv.visitLabel( l1 );

			mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( BuildUtils.box( fieldType ) ) );
			mv.visitMethodInsn( INVOKEVIRTUAL,
			                    BuildUtils.getInternalType( BuildUtils.box( fieldType ) ),
			                    BuildUtils.numericMorph( BuildUtils.box( fieldType ) ),
			                    Type.getMethodDescriptor( Type.getType( field.getType() ) ),
			                    false );
			mv.visitVarInsn( AsmUtil.storeType( fieldType ), 1 );
		}

	}


	protected void buildLogicalGetter( ClassVisitor cw, FieldDefinition field, String proxy, ClassDefinition core ) {
		String fieldName = field.getName();
		String fieldType = field.getTypeName();

		String getter = BuildUtils.getterName( fieldName, fieldType );

		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, getter, "()" + BuildUtils.getTypeDescriptor( fieldType ), null, null );
		mv.visitCode();

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( proxy ), "object", Type.getDescriptor( core.getDefinedClass() ) );
		mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitableBean.class ) );
		mv.visitMethodInsn( INVOKEINTERFACE,
		                    Type.getInternalName( TraitableBean.class ),
		                    "_getFieldTMS",
		                    Type.getMethodDescriptor( Type.getType( TraitFieldTMS.class ) ),
		                    true );

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
		                    Type.getMethodDescriptor( Type.getType( Object.class ), Type.getType( String.class ), Type.getType( Class.class ) ),
		                    true );

		mv.visitVarInsn( ASTORE, 1 );
		mv.visitVarInsn( ALOAD, 1 );

		if ( BuildUtils.isPrimitive( fieldType ) ) {
			Label l0 = new Label();
			mv.visitJumpInsn( IFNULL, l0 );
			mv.visitVarInsn( ALOAD, 1 );
			Label l1 = new Label();
			mv.visitJumpInsn( GOTO, l1 );
			mv.visitLabel( l0 );
			mv.visitInsn( AsmUtil.zero( fieldType ) );
			mv.visitMethodInsn( Opcodes.INVOKESTATIC,
			                    BuildUtils.getInternalType( BuildUtils.box( fieldType ) ),
			                    "valueOf",
			                    Type.getMethodDescriptor( Type.getType( BuildUtils.getTypeDescriptor( BuildUtils.box( fieldType ) ) ), Type.getType( BuildUtils.getTypeDescriptor( fieldType ) ) ),
			                    false );
			mv.visitLabel( l1 );

			mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( BuildUtils.box( fieldType ) ) );
			mv.visitMethodInsn( INVOKEVIRTUAL,
			                    BuildUtils.getInternalType( BuildUtils.box( fieldType ) ),
			                    BuildUtils.numericMorph( BuildUtils.box( fieldType ) ),
			                    Type.getMethodDescriptor( Type.getType( field.getType() ) ),
			                    false );
			mv.visitInsn( AsmUtil.returnType( fieldType ) );
		} else {
			mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( fieldType ) );
			mv.visitInsn( ARETURN );
		}

		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}


	public void helpSet( ClassDefinition core, FieldDefinition field, MethodVisitor mv, String proxyName ) {
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
				mv.visitMethodInsn( INVOKEINTERFACE,
				                    Type.getInternalName( TraitableBean.class ),
				                    "getTrait",
				                    Type.getMethodDescriptor( Type.getType( Thing.class ), Type.getType( String.class ) ),
				                    true );
				mv.visitVarInsn( ASTORE, 1 );
			} else if ( ! isCoreTrait && isTraitTrait ) {
				mv.visitVarInsn( ALOAD, 1 );
				mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitProxyImpl.class ) );
				mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName( TraitProxyImpl.class ), "getObject", Type.getMethodDescriptor(Type.getType(TraitableBean.class ) ), false );
				mv.visitVarInsn( ASTORE, 1 );
			} else if ( isCoreTrait ) {
				mv.visitVarInsn( ALOAD, 1 );
				mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitProxyImpl.class ) );
				mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName( TraitProxyImpl.class ), "getObject", Type.getMethodDescriptor(Type.getType(TraitableBean.class ) ), false );
				mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitableBean.class ) );
				mv.visitLdcInsn( hardField.getTypeName() );
				mv.visitMethodInsn( INVOKEINTERFACE,
				                    Type.getInternalName( TraitableBean.class ),
				                    "getTrait",
				                    Type.getMethodDescriptor( Type.getType( Thing.class ), Type.getType( String.class ) ),
				                    true );
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

		if ( isHardField && CoreWrapper.class.isAssignableFrom(core.getDefinedClass() ) ) {
			logicalSetter( mv, field, proxyName, core );
		}
	}

	public void buildKeyedEqualityMethods( ClassVisitor cw, ClassDefinition trait, String proxy ) {
		String proxyType = BuildUtils.getInternalType( proxy );

		buildKeyedEquals( cw, trait, proxyType );
		buildKeyedHashCode( cw, trait, proxyType );
	}

	protected abstract void buildKeyedHashCode( ClassVisitor cw, ClassDefinition trait, String proxyType );

	protected abstract void buildKeyedEquals( ClassVisitor cw, ClassDefinition trait, String proxyType );


	public void buildEqualityMethods( ClassVisitor cw, String proxy, String core ) {
		// Implemented in subclasses
		assert cw != null;
		assert proxy != null;
		assert core != null;
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
	
}