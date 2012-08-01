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

import org.drools.factmodel.BuildUtils;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.WriteAccessor;
import org.mvel2.asm.*;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TraitMapPropertyWrapperClassBuilderImpl implements TraitPropertyWrapperClassBuilder, Serializable {

    private transient Map<String, FieldDefinition> aliases;
    
    private transient ClassDefinition trait;

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


        ClassWriter cw = new ClassWriter( 0 );
        FieldVisitor fv;
        MethodVisitor mv;

        // get the method bitmask
        long mask = TraitRegistry.getInstance().getFieldMask( trait.getName(), core.getDefinedClass().getName() );

        String name = TraitFactory.getPropertyWrapperName(trait, core);


        String internalWrapper  = BuildUtils.getInternalType( name );
        String descrCore        = BuildUtils.getTypeDescriptor( core.getClassName() );


        aliases = new HashMap<String, FieldDefinition>();
        for ( FieldDefinition tfld : trait.getFieldsDefinitions() ) {
            if ( tfld.hasAlias() ) {
                String alias = tfld.getAlias();
                FieldDefinition coreField = core.getField( alias );
                if ( coreField != null ) {
                    aliases.put( tfld.getName(), coreField );
                }
            }
        }
        
        cw.visit( V1_5, ACC_PUBLIC + ACC_SUPER,
                internalWrapper,
                Type.getDescriptor( Object.class ) + Type.getDescriptor( Map.class ) + Type.getDescriptor( MapWrapper.class ),
//                "Ljava/lang/Object;Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;Lorg/drools/factmodel/traits/MapWrapper;",
                Type.getInternalName( Object.class ),
                new String[]{ Type.getInternalName( Map.class ), Type.getInternalName( MapWrapper.class ), Type.getInternalName( Serializable.class ) } );

        cw.visitInnerClass( Type.getInternalName( Map.Entry.class ), Type.getInternalName( Map.class ), "Entry", ACC_PUBLIC + ACC_STATIC + ACC_ABSTRACT + ACC_INTERFACE );


        for ( FieldDefinition fld : core.getFieldsDefinitions() ) {
            fv = cw.visitField( ACC_PUBLIC + ACC_STATIC, fld.getName()+"_reader", Type.getDescriptor( InternalReadAccessor.class ), null, null);
            fv.visitEnd();
            fv = cw.visitField( ACC_PUBLIC + ACC_STATIC, fld.getName()+"_writer", Type.getDescriptor( WriteAccessor.class ), null, null);
            fv.visitEnd();
        }


        {
            fv = cw.visitField( 0, "object", descrCore, null, null );
            fv.visitEnd();
        }

        {
            fv = cw.visitField( 0, "map", Type.getDescriptor( Map.class ), "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null );
            fv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC, "<init>",                                        
                    "(" + descrCore + Type.getDescriptor( Map.class ) + ")V",
                    "(" + descrCore + "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)V",
                    null);
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( Object.class ), "<init>", "()V" );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitFieldInsn( PUTFIELD, internalWrapper, "object", descrCore );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitFieldInsn( PUTFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );

            int stackSize = initSoftFields( mv, trait, mask, 2 );

            mv.visitInsn( RETURN );
            mv.visitMaxs( 2 + stackSize, 3 );
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








    private void invokeRemove( MethodVisitor mv, String wrapperName, ClassDefinition core, String fieldName, FieldDefinition field ) {
        mv.visitLdcInsn( fieldName );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName(String.class), "equals", "(" + Type.getDescriptor( Object.class ) + ")Z" );
        Label l1 = new Label();
        mv.visitJumpInsn( IFEQ, l1 );

        TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );

        if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
            TraitFactory.valueOf( mv, field.getTypeName() );
        }
        mv.visitVarInsn( ASTORE, 2 );

        TraitFactory.invokeInjector( mv, wrapperName, trait, core, field, true, 1 );

        mv.visitVarInsn( ALOAD, 2 );
        mv.visitInsn( ARETURN );
        mv.visitLabel( l1 );
    }

    private void buildRemove( ClassWriter cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "remove", "(" + Type.getDescriptor( Object.class ) +")" + Type.getDescriptor( Object.class ), null, null );
        mv.visitCode();

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            invokeRemove( mv, wrapperName, core, field.getName(), field );
        }
        for ( String alias : aliases.keySet() ) {
            // no need to review the stack, the alias is a core field anyway!
            invokeRemove( mv, wrapperName, core, alias, aliases.get( alias ) );
        }



        int j = 0;
        int stack = 0;
        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
            boolean isSoftField = TraitRegistry.isSoftField( field, j++, mask );
            if ( isSoftField ) {
                stack = Math.max( stack, BuildUtils.sizeOf( field.getTypeName() ) );

                mv.visitLdcInsn( field.getName() );
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( String.class ), "equals", "(" + Type.getDescriptor( Object.class ) + ")Z" );
                Label l2 = new Label();
                mv.visitJumpInsn( IFEQ, l2 );
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
                mv.visitLdcInsn( field.getName() );
                mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "get", "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
                mv.visitVarInsn( ASTORE, 2 );
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
                mv.visitLdcInsn( field.getName() );
                mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );
                if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                    TraitFactory.valueOf( mv, field.getTypeName() );
                }
                mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "put", 
                                    "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
                mv.visitInsn( POP );
                mv.visitVarInsn( ALOAD, 2 );
                mv.visitInsn( ARETURN );
                mv.visitLabel( l2 );
            }
        }


        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "remove", "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
        mv.visitVarInsn( ASTORE, 2 );
        mv.visitVarInsn( ALOAD, 2 );
        mv.visitInsn( ARETURN );
        mv.visitMaxs( 4 + stack, 3 );
        mv.visitEnd();
    }




    private int initSoftFields( MethodVisitor mv, ClassDefinition trait, long mask, int varNum ) {
        int j = 0;
        int stackSize = 0;
        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
            boolean isSoftField = TraitRegistry.isSoftField( field, j++, mask );
            if ( isSoftField ) {
                mv.visitVarInsn( ALOAD, varNum );
                mv.visitLdcInsn( field.getName() );
                mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );
                if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                    TraitFactory.valueOf( mv, field.getTypeName() );
                    int size = BuildUtils.sizeOf( field.getTypeName() );
                    stackSize = Math.max( stackSize, size );
                } else {
                    stackSize = Math.max( stackSize, 2 );
                }
                mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "put", 
                                    "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
                mv.visitInsn( POP );
            }
        }
        return stackSize;
    }



    private void buildClear( ClassWriter cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        boolean hasPrimitiveFields = false;
        boolean hasObjectFields = false;
        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "clear", "()V", null, null );
        mv.visitCode();

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            if ( field.isKey() ) continue;
            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                hasPrimitiveFields = true;
            } else {
                hasObjectFields = true;
            }
            TraitFactory.invokeInjector( mv, wrapperName, trait, core, field, true, 1 );
        }

        int stack = 2;
        if ( hasPrimitiveFields ) {
            stack++;
        }
        if ( hasObjectFields ) {
            stack++;
        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "clear", "()V" );

        int num = initSoftFields( mv, trait, mask, 0 );
        stack += num;


        mv.visitInsn( RETURN );
        mv.visitMaxs( stack , 1 );
        mv.visitEnd();

    }





    private void buildContainsValue( ClassWriter cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "containsValue", "(" + Type.getDescriptor( Object.class ) + ")Z", null, null );
        mv.visitCode();


        // null check
        mv.visitVarInsn( ALOAD, 1 );
        Label l99 = new Label();
        mv.visitJumpInsn( IFNONNULL, l99 );

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {
                TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );
                Label l1 = new Label();
                mv.visitJumpInsn( IFNONNULL, l1 );
                mv.visitInsn( ICONST_1 );
                mv.visitInsn( IRETURN );
                mv.visitLabel( l1 );
            }
        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitInsn( ACONST_NULL );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "containsValue", "(" + Type.getDescriptor( Object.class ) + ")Z" );
        mv.visitInsn( IRETURN );
        mv.visitLabel( l99 );

        // non-null values check
        for ( FieldDefinition field : core.getFieldsDefinitions() )   {

            mv.visitVarInsn( ALOAD, 1 );

            TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );

            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                TraitFactory.valueOf( mv, field.getTypeName() );
            }
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "equals", "(" + Type.getDescriptor( Object.class ) + ")Z" );

            Label l0 = new Label();
            mv.visitJumpInsn( IFEQ, l0 );
            mv.visitInsn( ICONST_1 );
            mv.visitInsn( IRETURN );
            mv.visitLabel( l0 );

        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "containsValue", "(" + Type.getDescriptor( Object.class ) + ")Z" );
        mv.visitInsn( IRETURN );
        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ? 3 : 2, 2 );
        mv.visitEnd();

    }
    
    
    protected void invokeContainsKey( MethodVisitor mv, String fieldName ) {
        mv.visitLdcInsn( fieldName );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( String.class ), "equals", "(" + Type.getDescriptor( Object.class ) + ")Z" );
        Label l0 = new Label();
        mv.visitJumpInsn( IFEQ, l0 );
        mv.visitInsn( ICONST_1 );
        mv.visitInsn( IRETURN );
        mv.visitLabel( l0 );
    }

    private void buildContainsKey(ClassWriter cw, String name, String className, ClassDefinition trait, ClassDefinition core, long mask) {
        String internalWrapper = BuildUtils.getInternalType( name );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "containsKey", "(" + Type.getDescriptor( Object.class ) + ")Z", null, null );
        mv.visitCode();

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            invokeContainsKey( mv, field.getName() );
        }
        for ( String alias : aliases.keySet() ) {
            invokeContainsKey( mv, alias );
        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "containsKey", "(" + Type.getDescriptor( Object.class ) + ")Z" );
        mv.visitInsn( IRETURN );
        mv.visitMaxs( 2, 2 );
        mv.visitEnd();
    }


    private void buildSize( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "size", "()I", null, null );
        mv.visitCode();
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "size", "()I" );

        int n = core.getFieldsDefinitions().size();
        for ( int j = 0; j < n; j++ ) {
            mv.visitInsn( ICONST_1 );
            mv.visitInsn( IADD );
        }

        mv.visitInsn( IRETURN );
        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ? 2 : 1, 1 );
        mv.visitEnd();
    }


    private void buildIsEmpty( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        boolean hasHardFields = core.getFieldsDefinitions().size() > 0;

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "isEmpty", "()Z", null, null );
        mv.visitCode();

        if ( ! hasHardFields ) {
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "isEmpty", "()Z" );
        } else {
            mv.visitInsn( ICONST_0 );
        }
        mv.visitInsn( IRETURN );
        mv.visitMaxs( 1, 1 );
        mv.visitEnd();
    }






    private void invokeGet( MethodVisitor mv, String wrapperName, ClassDefinition core, String fieldName, FieldDefinition field ) {
        mv.visitLdcInsn( fieldName );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( String.class ), "equals", "(" + Type.getDescriptor( Object.class ) + ")Z" );
        Label l0 = new Label();
        mv.visitJumpInsn( IFEQ, l0 );

        TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );

        if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
            TraitFactory.valueOf( mv, field.getTypeName() );
        }
        mv.visitInsn( ARETURN );
        mv.visitLabel( l0 );
    }


    private void buildGet( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "get", "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ), null, null );
        mv.visitCode();


        if ( core.getFieldsDefinitions().size() > 0) {
            for ( FieldDefinition field : core.getFieldsDefinitions() ) {
                invokeGet( mv, wrapperName, core, field.getName(), field );
            }
        }
        for ( String alias : aliases.keySet() ) {
            invokeGet( mv, wrapperName, core, alias, aliases.get( alias ) );
        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "get", "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
        mv.visitInsn( ARETURN );
        mv.visitMaxs( 2, 2 );
        mv.visitEnd();
    }



    protected void invokePut( MethodVisitor mv, String wrapperName, ClassDefinition core, String fieldName, FieldDefinition field ) {
        mv.visitLdcInsn( fieldName );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( String.class ), "equals", "(" + Type.getDescriptor( Object.class ) + ")Z" );
        Label l1 = new Label();
        mv.visitJumpInsn( IFEQ, l1 );


        mv.visitVarInsn( ALOAD, 2 );
        if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
            TraitFactory.promote( mv, field.getTypeName() );
            mv.visitVarInsn( BuildUtils.storeType( field.getTypeName() ), 3 );
            TraitFactory.invokeInjector( mv, wrapperName, trait, core, field, false, 3 );
        } else {
            TraitFactory.invokeInjector( mv, wrapperName, trait, core, field, false, 2 );
        }

        mv.visitVarInsn( ALOAD, 2 );
        mv.visitInsn( ARETURN );
        mv.visitLabel( l1 );
    }


    private void buildPut( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "put", "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ), null, null );
        mv.visitCode();

        if ( core.getFieldsDefinitions().size() > 0) {
            for ( FieldDefinition field : core.getFieldsDefinitions() ) {
                invokePut( mv, wrapperName, core, field.getName(), field );
            }
        }
        for ( String alias : aliases.keySet() ) {
            invokePut( mv, wrapperName, core, alias, aliases.get( alias ) );
        }


        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 2 );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "put", 
                            "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
        mv.visitInsn( ARETURN );
        mv.visitMaxs( 4, 5 );
        mv.visitEnd();
    }




    private void buildEntryset( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "entrySet", "()" + Type.getDescriptor( Set.class ),
                                          "()Ljava/util/Set<Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/Object;>;>;", null);
        mv.visitCode();
        mv.visitTypeInsn( NEW, Type.getInternalName( HashSet.class ) );
        mv.visitInsn( DUP );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( HashSet.class ), "<init>", "()V" );
        mv.visitVarInsn( ASTORE, 1 );

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitLdcInsn( field.getName() );

            TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );

            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                TraitFactory.valueOf( mv, field.getTypeName() );
            }

            mv.visitMethodInsn( INVOKESTATIC, Type.getInternalName( TraitProxy.class ), "buildEntry", 
                                "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Map.Entry.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Set.class ), "add", "(" + Type.getDescriptor( Object.class ) + ")Z" );
            mv.visitInsn( POP );
        }

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "entrySet", "()" + Type.getDescriptor( Set.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Set.class ), "addAll", "(" + Type.getDescriptor( Collection.class )+ ")Z" );
        mv.visitInsn( POP );

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitInsn( ARETURN );
        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ?  4 : 2, 2 );
        mv.visitEnd();

    }


    private void buildKeyset( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "keySet", "()" + Type.getDescriptor( Set.class ), "()Ljava/util/Set<Ljava/lang/String;>;", null );
        mv.visitCode();
        mv.visitTypeInsn( NEW, Type.getInternalName( HashSet.class ) );
        mv.visitInsn( DUP );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( HashSet.class ), "<init>", "()V" );
        mv.visitVarInsn( ASTORE, 1 );

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitLdcInsn( field.getName() );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Set.class ), "add", "(" + Type.getDescriptor( Object.class ) + ")Z" );
            mv.visitInsn( POP );
        }

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "keySet", "()" + Type.getDescriptor( Set.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Set.class ), "addAll", "(" + Type.getDescriptor( Collection.class ) + ")Z" );
        mv.visitInsn( POP );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitInsn( ARETURN );
        mv.visitMaxs( 2, 2 );
        mv.visitEnd();
    }



    private void buildValues( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "values", "()" + Type.getDescriptor( Collection.class ), "()Ljava/util/Collection<Ljava/lang/Object;>;", null );
        mv.visitCode();

        mv.visitTypeInsn( NEW, Type.getInternalName( ArrayList.class ) );
        mv.visitInsn( DUP );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( ArrayList.class ), "<init>", "()V" );
        mv.visitVarInsn( ASTORE, 1 );


        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitVarInsn( ALOAD, 1 );

            TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );

            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                TraitFactory.valueOf( mv, field.getTypeName() );
            }

            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Collection.class ), "add", "(" + Type.getDescriptor( Object.class ) + ")Z" );
            mv.visitInsn( POP );
        }

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "values", "()" + Type.getDescriptor( Collection.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Collection.class ), "addAll", "(" + Type.getDescriptor( Collection.class ) + ")Z" );
        mv.visitInsn( POP );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitInsn( ARETURN );

        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ? 3 : 2, 2);
        mv.visitEnd();
    }




















    public void buildCommonMethods( ClassVisitor cw, String wrapper ) {

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "put", 
                                               "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
                                               null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( String.class ) );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "put",
                                "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 3, 3 );
            mv.visitEnd();
        }

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
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( MapWrapper.class ) );
            mv.visitVarInsn( ASTORE, 2 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapper ), "map", Type.getDescriptor( Map.class ) );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( MapWrapper.class ), "getInnerMap", "()" + Type.getDescriptor( Map.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "equals", "(" + Type.getDescriptor( Object.class ) + ")Z" );
            mv.visitInsn( IRETURN );
            mv.visitMaxs( 2, 3 );
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "hashCode", "()I", null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapper ), "map", Type.getDescriptor( Map.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "hashCode", "()I" );
            mv.visitInsn( IRETURN );
            mv.visitMaxs( 1, 1 );
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "getInnerMap", "()" + Type.getDescriptor( Map.class ), 
                                               "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapper ), "map", Type.getDescriptor( Map.class ) );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 1, 1 );
            mv.visitEnd();
        }


        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "putAll", "(" + Type.getDescriptor( Map.class ) + ")V", 
                                               "(Ljava/util/Map<+Ljava/lang/String;+Ljava/lang/Object;>;)V", 
                                               null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "keySet", "()" + Type.getDescriptor( Set.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Set.class ), "iterator", "()" + Type.getDescriptor( Iterator.class ) );
            mv.visitVarInsn( ASTORE, 2 );
            Label l0 = new Label();
            mv.visitLabel( l0 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Iterator.class ), "hasNext", "()Z" );
            Label l1 = new Label();
            mv.visitJumpInsn( IFEQ, l1 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Iterator.class ), "next", "()" + Type.getDescriptor( Object.class ) );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( String.class ) );
            mv.visitVarInsn( ASTORE, 3 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 3 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 3 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "get",
                                "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "put",
                                "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
            mv.visitInsn( POP );
            mv.visitJumpInsn( GOTO, l0 );
            mv.visitLabel( l1 );
            mv.visitInsn( RETURN );
            mv.visitMaxs( 4, 4 );
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "toString", "()" + Type.getDescriptor( String.class ), null, null );
            mv.visitCode();
            mv.visitTypeInsn( NEW, Type.getInternalName( StringBuilder.class ) );
            mv.visitInsn( DUP );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( StringBuilder.class ), "<init>", "()V" );
            mv.visitLdcInsn("[[");
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append",
                    "(" + Type.getDescriptor( String.class ) +")" + Type.getDescriptor( StringBuilder.class ) );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "entrySet", "()" + Type.getDescriptor( Set.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append",
                    "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( StringBuilder.class ) );
            mv.visitLdcInsn( "]]" );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append",
                    "(" + Type.getDescriptor( String.class ) +")" + Type.getDescriptor( StringBuilder.class ));
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "toString", "()" + Type.getDescriptor( String.class ) );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 2, 1 );
            mv.visitEnd();
        }




    }








}

