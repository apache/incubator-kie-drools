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
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.core.factmodel.BuildUtils;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.util.Triple;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleStore;
import org.mvel2.asm.ClassVisitor;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.FieldVisitor;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Type;

import static org.drools.core.rule.builder.dialect.asm.ClassGenerator.createClassWriter;

public class TraitTriplePropertyWrapperClassBuilderImpl extends AbstractPropertyWrapperClassBuilderImpl implements TraitPropertyWrapperClassBuilder, Serializable {


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
		BitSet mask = traitRegistry.getFieldMask(trait.getName(), core.getDefinedClass().getName());

		String name = TraitFactory.getPropertyWrapperName( trait, core );


		String internalWrapper  = BuildUtils.getInternalType( name );
		String descrCore        = Type.getDescriptor( core.getDefinedClass() );

		ClassWriter cw = createClassWriter( classLoader,
		                                    ACC_PUBLIC + ACC_SUPER,
		                                    internalWrapper,
		                                    null,
		                                    Type.getInternalName( TripleBasedStruct.class ),
		                                    new String[] { Type.getInternalName( Serializable.class ) } );

		cw.visitInnerClass( Type.getInternalName( Map.Entry.class ),
		                    Type.getInternalName( Map.class ),
		                    "Entry",
		                    ACC_PUBLIC + ACC_STATIC + ACC_ABSTRACT + ACC_INTERFACE );


		{
			fv = cw.visitField(0, "object", descrCore, null, null);
			fv.visitEnd();
		}


		{
			mv = cw.visitMethod(ACC_PUBLIC,
			                    "<init>",
			                    "()V",
			                    null,
			                    null);

			mv.visitCode();
			mv.visitVarInsn( ALOAD, 0 );
			mv.visitMethodInsn( INVOKESPECIAL,
			                    Type.getInternalName( TripleBasedStruct.class ),
			                    "<init>",
			                    "()V",
			                    false );
			mv.visitInsn( RETURN );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}


		{
			mv = cw.visitMethod(ACC_PUBLIC,
			                    "<init>",
			                    "(" +
					                    descrCore +
					                    Type.getDescriptor( TripleStore.class ) +
					                    Type.getDescriptor( TripleFactory.class ) +
					                    ")V",
			                    null,
			                    null);


			mv.visitCode();
			mv.visitVarInsn( ALOAD, 0 );

			mv.visitMethodInsn( INVOKESPECIAL,
			                    Type.getInternalName( TripleBasedStruct.class ),
			                    "<init>",
			                    "()V",
			                    false );
			mv.visitVarInsn( ALOAD, 0 );
			mv.visitVarInsn( ALOAD, 1 );
			mv.visitFieldInsn( PUTFIELD, internalWrapper, "object", descrCore );
			mv.visitVarInsn( ALOAD, 0 );
			mv.visitVarInsn( ALOAD, 2 );
			mv.visitFieldInsn( PUTFIELD, internalWrapper, "store", Type.getDescriptor( TripleStore.class ) );

			mv.visitVarInsn( ALOAD, 0 );
			mv.visitVarInsn( ALOAD, 3 );
			mv.visitMethodInsn( INVOKEVIRTUAL,
			                    Type.getInternalName( TripleBasedStruct.class ),
			                    "setTripleFactory",
			                    "(" + Type.getDescriptor( TripleFactory.class ) + ")V",
			                    false );


			mv.visitVarInsn( ALOAD, 0 );
			mv.visitVarInsn( ALOAD, 2 );
			mv.visitMethodInsn( INVOKEVIRTUAL,
			                    Type.getInternalName( TripleStore.class ),
			                    "getId",
			                    "()" + Type.getDescriptor( String.class ),
			                    false );
			mv.visitFieldInsn( PUTFIELD,
			                   internalWrapper,
			                   "storeId",
			                   Type.getDescriptor( String.class ) );

			mv.visitVarInsn( ALOAD, 0 );
			mv.visitMethodInsn( INVOKESPECIAL,
			                    internalWrapper,
			                    "initSoftFields",
			                    "()V",
			                    false );
			mv.visitInsn( RETURN );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();


		}

		buildInitSoftFields( cw, internalWrapper, trait, core, mask );

		buildClearSoftFields(cw, internalWrapper, trait, mask);

		buildSize( cw, core );

		buildIsEmpty( cw, core );

		buildGet( cw, name, core );

		buildPut( cw, name, core );

		buildClear( cw, name, trait, core );

		buildRemove( cw, name, trait, core, mask );

		buildContainsKey( cw, core );

		buildContainsValue( cw, name, core );

		buildKeyset( cw, core );

		buildValues( cw, name, core );

		buildEntryset( cw, name, core );

		buildCommonMethods( cw, name );

		buildSpecificMethods( cw, name, core );

		cw.visitEnd();

		return cw.toByteArray();
	}




	protected void buildRemove( ClassWriter cw, String wrapperName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {
		String internalWrapper = BuildUtils.getInternalType( wrapperName );

		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
		                                   "remove",
		                                   "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
		                                   null,
		                                   null );
		mv.visitCode();

		int stack = 0;
		for ( FieldDefinition field : core.getFieldsDefinitions() ) {
			stack = Math.max( stack, BuildUtils.sizeOf( field.getTypeName() ) );
			invokeRemove( mv, wrapperName, core, field.getName(), field );
		}


		int j = 0;
		for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
			boolean isSoftField = TraitRegistry.isSoftField( field, j++, mask );
			if ( isSoftField ) {
				stack = Math.max( stack, BuildUtils.sizeOf( field.getTypeName() ) );

				mv.visitLdcInsn( field.getName() );
				mv.visitVarInsn( ALOAD, 1 );
				mv.visitMethodInsn( INVOKEVIRTUAL,
				                    Type.getInternalName( String.class ),
				                    "equals",
				                    "(" + Type.getDescriptor( Object.class ) + ")Z",
				                    false );
				Label l2 = new Label();
				mv.visitJumpInsn( IFEQ, l2 );
				mv.visitVarInsn( ALOAD, 0 );
				mv.visitFieldInsn( GETFIELD, internalWrapper, "store", Type.getDescriptor( TripleStore.class ) );
				mv.visitVarInsn( ALOAD, 0);
				mv.visitLdcInsn( field.getName() );
				mv.visitMethodInsn( INVOKEVIRTUAL,
				                    internalWrapper,
				                    "propertyKey",
				                    "(" + Type.getDescriptor( Object.class ) + ")" +  Type.getDescriptor( Triple.class ),
				                    false );
				mv.visitMethodInsn( INVOKEVIRTUAL,
				                    Type.getInternalName( TripleStore.class ),
				                    "get",
				                    "(" + Type.getDescriptor( Triple.class ) + ")" + Type.getDescriptor( Triple.class ),
				                    false );

				mv.visitVarInsn( ASTORE, 2 );
				mv.visitVarInsn( ALOAD, 0 );
				mv.visitLdcInsn( field.getName() );
				mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );

				if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
					TraitFactory.valueOf( mv, field.getTypeName() );
				}
				mv.visitMethodInsn( INVOKESPECIAL,
				                    Type.getInternalName( TripleBasedStruct.class ),
				                    "put",
				                    "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
				                    false );
				mv.visitInsn( POP );
				mv.visitVarInsn( ALOAD, 2 );
				mv.visitInsn( ARETURN );
				mv.visitLabel( l2 );
			}
		}

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitVarInsn( ALOAD, 1 );
		mv.visitMethodInsn( INVOKESPECIAL,
		                    Type.getInternalName( TripleBasedStruct.class ),
		                    "remove",
		                    "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
		                    false );

		mv.visitVarInsn( ASTORE, 2 );
		mv.visitVarInsn( ALOAD, 2 );
		mv.visitInsn( ARETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();

	}


	protected void buildInitSoftFields( ClassWriter cw, String wrapperName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {
		MethodVisitor mv = cw.visitMethod(ACC_PRIVATE, "initSoftFields", "()V", null, null);
		mv.visitCode();

		initSoftFields( mv, wrapperName, trait, core, mask );

		mv.visitInsn(RETURN);
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}

	protected void initSoftFields( MethodVisitor mv, String wrapperName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {
		int j = 0;
		for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
			boolean isSoftField = TraitRegistry.isSoftField( field, j++, mask );
			if ( isSoftField ) {
				initSoftField( mv, wrapperName, field, core, wrapperName );
			}
		}
	}



	protected void initSoftField(MethodVisitor mv, String wrapperName, FieldDefinition field, ClassDefinition core, String internalWrapper ) {
		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD,
		                   wrapperName,
		                   "store",
		                   Type.getDescriptor( TripleStore.class ) );
		mv.visitVarInsn( ALOAD, 0);
		mv.visitLdcInsn( field.resolveAlias() );
		mv.visitMethodInsn( INVOKEVIRTUAL,
		                    wrapperName,
		                    "propertyKey",
		                    "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Triple.class ),
		                    false );
		mv.visitMethodInsn( INVOKEVIRTUAL,
		                    Type.getInternalName(TripleStore.class),
		                    "contains",
		                    "(" + Type.getDescriptor( Triple.class ) + ")Z",
		                    false );
		Label l0 = new Label();
		mv.visitJumpInsn( IFNE, l0 );

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn(GETFIELD, wrapperName, "store", Type.getDescriptor( TripleStore.class ) );

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitLdcInsn( field.resolveAlias() );


		mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );
		if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
			TraitFactory.valueOf( mv, field.getTypeName() );
		}
		mv.visitMethodInsn( INVOKEVIRTUAL,
		                    wrapperName,
		                    "property",
		                    "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Triple.class ),
		                    false );
		mv.visitInsn(ICONST_1);
		mv.visitMethodInsn( INVOKEVIRTUAL,
		                    Type.getInternalName( TripleStore.class ),
		                    "put",
		                    "(" + Type.getDescriptor( Triple.class ) + "Z)Z",
		                    false );


		if ( core.isFullTraiting() ) {
			super.registerLogicalField( mv, internalWrapper, field, core );
		}

		mv.visitInsn( POP );
		mv.visitLabel( l0 );

	}


	protected void buildClear( ClassWriter cw, String wrapperName, ClassDefinition trait, ClassDefinition core ) {
		String internalWrapper = BuildUtils.getInternalType( wrapperName );

		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "clear", "()V", null, null );
		mv.visitCode();

		for ( FieldDefinition field : core.getFieldsDefinitions() ) {
			if ( field.isKey() ) continue;
			TraitFactory.invokeInjector( mv, wrapperName, core, field, true, 1 );
		}

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( TripleBasedStruct.class ), "clear", "()V", false );


		mv.visitVarInsn( ALOAD, 0 );
		mv.visitMethodInsn( INVOKESPECIAL, internalWrapper, "clearSoftFields", "()V", false );

		mv.visitInsn( RETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}





	protected void buildClearSoftFields( ClassWriter cw, String wrapperName, ClassDefinition trait, BitSet mask ) {
		MethodVisitor mv = cw.visitMethod( ACC_PRIVATE, "clearSoftFields", "()V", null, null );
		mv.visitCode();

		int j = 0;
		for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
			boolean isSoftField = TraitRegistry.isSoftField( field, j++, mask );
			if ( isSoftField ) {
				clearSoftField(mv, wrapperName, field);
			}
		}
		mv.visitInsn( RETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}

	protected void clearSoftField(MethodVisitor mv, String wrapperName, FieldDefinition field ) {
		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, wrapperName, "store", Type.getDescriptor( TripleStore.class ) );

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitLdcInsn( field.getName() );


		mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );
		if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
			TraitFactory.valueOf( mv, field.getTypeName() );
		}
		mv.visitMethodInsn( INVOKEVIRTUAL,
		                    wrapperName,
		                    "property",
		                    "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Triple.class ),
		                    false );
		mv.visitInsn( ICONST_1 );
		mv.visitMethodInsn( INVOKEVIRTUAL,
		                    Type.getInternalName( TripleStore.class ),
		                    "put",
		                    "(" + Type.getDescriptor( Triple.class ) + "Z)Z",
		                    false );
		mv.visitInsn( POP );
	}



	protected void buildContainsValue( ClassWriter cw, String wrapperName, ClassDefinition core ) {

		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "containsValue", "(" + Type.getDescriptor( Object.class ) + ")Z", null, null );
		mv.visitCode();

		boolean hasNillable = false;
		for ( FieldDefinition field : core.getFieldsDefinitions() ) {
			if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {
				hasNillable = true;
			}
		}
		Label l99 = null;
		if ( hasNillable ) {
			mv.visitVarInsn(ALOAD, 1);
			l99 = new Label();
			mv.visitJumpInsn(IFNONNULL, l99);
		}

		core.getFieldsDefinitions().size();
		for ( FieldDefinition field : core.getFieldsDefinitions() ) {
			if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {
				extractAndTestNotNull( mv, wrapperName, core, field );
			}
		}
		if ( hasNillable ) {
			mv.visitLabel( l99 );
		}

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitVarInsn( ALOAD, 1 );
		mv.visitMethodInsn( INVOKESPECIAL,
		                    Type.getInternalName( TripleBasedStruct.class ),
		                    "containsValue",
		                    "(" + Type.getDescriptor( Object.class ) + ")Z",
		                    false );

		mv.visitInsn( IRETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}


	protected void buildContainsKey(ClassWriter cw, ClassDefinition core ) {

		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
		                                   "containsKey",
		                                   "(" + Type.getDescriptor( Object.class ) + ")Z",
		                                   null,
		                                   null );
		mv.visitCode();

		for ( FieldDefinition field : core.getFieldsDefinitions() ) {
			invokeContainsKey( mv, field.getName() );
		}

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitVarInsn( ALOAD, 1 );
		mv.visitMethodInsn( INVOKESPECIAL,
		                    Type.getInternalName( TripleBasedStruct.class ),
		                    "containsKey",
		                    "(" + Type.getDescriptor( Object.class ) + ")Z",
		                    false );

		mv.visitInsn( IRETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}


	protected void buildSize( ClassVisitor cw, ClassDefinition core ) {

		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "size", "()I", null, null);
		mv.visitCode();
		mv.visitVarInsn( ALOAD, 0 );
		mv.visitMethodInsn( INVOKESPECIAL,
		                    Type.getInternalName( TripleBasedStruct.class ),
		                    "size",
		                    "()I",
		                    false );

		int n = core.getFieldsDefinitions().size();
		for ( int j = 0; j < n; j++ ) {
			mv.visitInsn( ICONST_1 );
			mv.visitInsn( IADD );
		}

		mv.visitInsn(IRETURN);
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();

	}


	protected void buildIsEmpty( ClassVisitor cw, ClassDefinition core ) {
		boolean hasHardFields = core.getFieldsDefinitions().size() > 0;

		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "isEmpty", "()Z", null, null );
		mv.visitCode();

		if ( ! hasHardFields ) {
			mv.visitVarInsn( ALOAD, 0 );
			mv.visitMethodInsn( INVOKESPECIAL,
			                    Type.getInternalName( TripleBasedStruct.class ),
			                    "isEmpty",
			                    "()Z",
			                    false );
		} else {
			mv.visitInsn( ICONST_0 );
		}
		mv.visitInsn( IRETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}






	protected void invokeGet( MethodVisitor mv, String wrapperName, ClassDefinition core, String fieldName, FieldDefinition field) {
		mv.visitLdcInsn( fieldName );
		mv.visitVarInsn( ALOAD, 1);
		mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/String", "equals", "(" + Type.getDescriptor( Object.class ) + ")Z", false );
		Label l0 = new Label();
		mv.visitJumpInsn( IFEQ, l0 );

		TraitFactory.invokeExtractor(mv, wrapperName, core, field );

		if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
			TraitFactory.valueOf( mv, field.getTypeName() );
		}
		mv.visitInsn( ARETURN );
		mv.visitLabel( l0 );
	}



	protected void buildGet( ClassVisitor cw, String wrapperName, ClassDefinition core ) {

		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
		                                   "get",
		                                   "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
		                                   null,
		                                   null );
		mv.visitCode();

		if ( core.getFieldsDefinitions().size() > 0 ) {
			for ( FieldDefinition field : core.getFieldsDefinitions() ) {
				invokeGet( mv, wrapperName, core, field.getName(), field );
			}
		}

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitVarInsn( ALOAD, 1 );
		mv.visitMethodInsn( INVOKESPECIAL,
		                    Type.getInternalName( TripleBasedStruct.class ),
		                    "get",
		                    "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
		                    false );

		mv.visitInsn( ARETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}



	protected void buildPut( ClassVisitor cw, String wrapperName, ClassDefinition core ) {

		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
		                                   "put",
		                                   "(" + Type.getDescriptor( String.class) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
		                                   null,
		                                   null );
		mv.visitCode();

		if ( core.getFieldsDefinitions().size() > 0) {
			for ( FieldDefinition field : core.getFieldsDefinitions() ) {
				invokePut( mv, wrapperName, core, field.getName(), field );
			}
		}

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitVarInsn( ALOAD, 1 );
		mv.visitVarInsn( ALOAD, 2 );
		mv.visitMethodInsn( INVOKESPECIAL,
		                    Type.getInternalName( TripleBasedStruct.class ),
		                    "put",
		                    "(" + Type.getDescriptor( String.class) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
		                    false );
		mv.visitInsn( ARETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}



	protected void buildEntryset( ClassVisitor cw, String wrapperName, ClassDefinition core ) {

		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
		                                   "entrySet",
		                                   "()" + Type.getDescriptor( Set.class ),
		                                   "()Ljava/util/Set<Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/Object;>;>;",
		                                   null );
		mv.visitCode();
		mv.visitTypeInsn( NEW, Type.getInternalName( HashSet.class ) );
		mv.visitInsn( DUP );
		mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( HashSet.class ), "<init>", "()V", false );
		mv.visitVarInsn( ASTORE, 1 );

		for ( FieldDefinition field : core.getFieldsDefinitions() ) {
			buildEntry( mv, field, wrapperName, core );
		}

		mv.visitVarInsn( ALOAD, 1 );
		mv.visitVarInsn( ALOAD, 0 );
		mv.visitMethodInsn( INVOKESPECIAL,
		                    Type.getInternalName( TripleBasedStruct.class ),
		                    "entrySet",
		                    "()" + Type.getDescriptor( Set.class ),
		                    false );
		mv.visitMethodInsn( INVOKEINTERFACE,
		                    Type.getInternalName( Set.class ),
		                    "addAll",
		                    "(" + Type.getDescriptor( Collection.class ) + ")Z",
		                    true );
		mv.visitInsn( POP );

		mv.visitVarInsn( ALOAD, 1 );
		mv.visitInsn( ARETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();

	}




	protected void buildKeyset( ClassVisitor cw, ClassDefinition core ) {

		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
		                                   "keySet",
		                                   "()" + Type.getDescriptor( Set.class ),
		                                   "()Ljava/util/Set<Ljava/lang/String;>;",
		                                   null);
		mv.visitCode();
		mv.visitTypeInsn( NEW, Type.getInternalName( HashSet.class ) );
		mv.visitInsn( DUP );
		mv.visitMethodInsn( INVOKESPECIAL,
		                    Type.getInternalName( HashSet.class ),
		                    "<init>",
		                    "()V",
		                    false );
		mv.visitVarInsn( ASTORE, 1 );

		for ( FieldDefinition field : core.getFieldsDefinitions() ) {
			collectFieldName( mv, field );
		}

		mv.visitVarInsn( ALOAD, 1 );
		mv.visitVarInsn( ALOAD, 0 );
		mv.visitMethodInsn( INVOKESPECIAL,
		                    Type.getInternalName( TripleBasedStruct.class ),
		                    "keySet",
		                    "()" + Type.getDescriptor( Set.class ),
		                    false );
		mv.visitMethodInsn( INVOKEINTERFACE,
		                    Type.getInternalName( Set.class ),
		                    "addAll",
		                    "(" + Type.getDescriptor( Collection.class ) + ")Z",
		                    true );
		mv.visitInsn( POP );

		mv.visitVarInsn( ALOAD, 1 );
		mv.visitInsn( ARETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}


	protected void buildValues( ClassVisitor cw, String wrapperName, ClassDefinition core ) {

		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
		                                   "values",
		                                   "()" + Type.getDescriptor( Collection.class ),
		                                   "()Ljava/util/Collection<Ljava/lang/Object;>;",
		                                   null );
		mv.visitCode();

		mv.visitTypeInsn( NEW, Type.getInternalName( ArrayList.class ) );
		mv.visitInsn( DUP );
		mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( ArrayList.class ), "<init>", "()V", false );
		mv.visitVarInsn( ASTORE, 1 );


		for ( FieldDefinition field : core.getFieldsDefinitions() ) {
			extractAndCollect( mv, wrapperName, field, core );
		}

		mv.visitVarInsn( ALOAD, 1 );
		mv.visitVarInsn( ALOAD, 0 );
		mv.visitMethodInsn( INVOKESPECIAL,
		                    Type.getInternalName( TripleBasedStruct.class ),
		                    "values",
		                    "()" + Type.getDescriptor( Collection.class ),
		                    false);
		mv.visitMethodInsn( INVOKEINTERFACE,
		                    Type.getInternalName( Collection.class ),
		                    "addAll",
		                    "(" + Type.getDescriptor( Collection.class ) + ")Z",
		                    true );
		mv.visitInsn( POP );

		mv.visitVarInsn( ALOAD, 1 );
		mv.visitInsn( ARETURN );

		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}




	public void buildCommonMethods( ClassVisitor cw, String wrapper ) {
		MethodVisitor mv;

		{
			mv = cw.visitMethod( ACC_PUBLIC, "toString", "()" + Type.getDescriptor( String.class ), null, null );
			mv.visitCode();
			mv.visitTypeInsn( NEW, Type.getInternalName( StringBuilder.class ) );
			mv.visitInsn( DUP );
			mv.visitMethodInsn( INVOKESPECIAL,
			                    Type.getInternalName( StringBuilder.class ),
			                    "<init>",
			                    "()V",
			                    false );
			mv.visitLdcInsn("[[");
			mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append",
			                    "(" + Type.getDescriptor( String.class ) +")" + Type.getDescriptor( StringBuilder.class ), false );
			mv.visitVarInsn( ALOAD, 0 );
			mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "entrySet", "()" + Type.getDescriptor( Set.class ), false );
			mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append",
			                    "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( StringBuilder.class ), false );
			mv.visitLdcInsn( "]]" );
			mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append",
			                    "(" + Type.getDescriptor( String.class ) +")" + Type.getDescriptor( StringBuilder.class ), false );
			mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "toString", "()" + Type.getDescriptor( String.class ), false );
			mv.visitInsn( ARETURN );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod( ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "put",
			                     "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
			                     null, null);
			mv.visitCode();
			mv.visitVarInsn( ALOAD, 0 );
			mv.visitVarInsn( ALOAD, 1 );
			mv.visitTypeInsn( CHECKCAST, Type.getInternalName( String.class ) );
			mv.visitVarInsn( ALOAD, 2 );
			mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "put",
			                    "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ), false );
			mv.visitInsn( ARETURN);
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}


	}



	protected void buildSpecificMethods(ClassWriter cw, String wrapper, ClassDefinition core) {
		MethodVisitor mv;

		{
			mv = cw.visitMethod( ACC_PUBLIC, "hashCode", "()I", null, null );
			mv.visitCode();
			mv.visitVarInsn( ALOAD, 0 );
			mv.visitVarInsn( ALOAD, 0 );
			mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapper ), "object", BuildUtils.getTypeDescriptor( core.getName() ));
			mv.visitMethodInsn( INVOKEVIRTUAL,
			                    BuildUtils.getInternalType( wrapper ),
			                    "getTriplesForSubject",
			                    "(" + Type.getDescriptor( Object.class ) + ")" +  Type.getDescriptor( Collection.class ), false );
			mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "hashCode", "()I", false );
			mv.visitInsn( IRETURN );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}

		{
			mv = cw.visitMethod( ACC_PROTECTED, "getObject", "()" + Type.getDescriptor( Object.class ), null, null );
			mv.visitCode();
			mv.visitVarInsn( ALOAD, 0 );
			mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapper ), "object", BuildUtils.getTypeDescriptor( core.getName() ) );
			mv.visitInsn( ARETURN );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "setObject", "(" + Type.getDescriptor( Object.class ) + ")V", null, null );
			mv.visitCode();
			mv.visitVarInsn( ALOAD, 0 );
			mv.visitVarInsn( ALOAD, 1 );
			mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( core.getName() ) );
			mv.visitFieldInsn( PUTFIELD, BuildUtils.getInternalType( wrapper ), "object", BuildUtils.getTypeDescriptor( core.getName() ) );
			mv.visitInsn( RETURN );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}

	}




}

