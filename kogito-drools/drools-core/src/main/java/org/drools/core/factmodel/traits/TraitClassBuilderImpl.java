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

import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.factmodel.BuildUtils;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.factmodel.GeneratedFact;
import org.mvel2.asm.AnnotationVisitor;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Type;

import java.io.Serializable;

import static org.drools.core.factmodel.DefaultBeanClassBuilder.addAnnotationAttribute;
import static org.drools.core.rule.builder.dialect.asm.ClassGenerator.createClassWriter;

public class TraitClassBuilderImpl implements TraitClassBuilder, Serializable {


    public byte[] buildClass( ClassDefinition classDef, ClassLoader classLoader ) {

        init( classDef );

        ClassWriter cw = null;

        try {
            String cName = BuildUtils.getInternalType(classDef.getClassName());
            String genericTypes = BuildUtils.getGenericTypes( classDef.getInterfaces() );
            String superType = Type.getInternalName( Object.class );
            String[] intfaces = null;
            
            if ( Object.class.getName().equals( classDef.getSuperClass() ) ) {
                String[] tmp = BuildUtils.getInternalTypes( classDef.getInterfaces() );
                intfaces = new String[ tmp.length + 2 ];
                System.arraycopy( tmp, 0, intfaces, 0, tmp.length );
                intfaces[ tmp.length ] = Type.getInternalName( Serializable.class );
                intfaces[ tmp.length + 1 ] = Type.getInternalName( GeneratedFact.class );
            } else {
                String[] tmp = BuildUtils.getInternalTypes( classDef.getInterfaces() );
                intfaces = new String[ tmp.length + 3 ];
                System.arraycopy( tmp, 0, intfaces, 0, tmp.length );
                intfaces[ tmp.length ] = BuildUtils.getInternalType( classDef.getSuperClass() );
                intfaces[ tmp.length + 1 ] = Type.getInternalName( Serializable.class );
                intfaces[ tmp.length + 2 ] = Type.getInternalName( GeneratedFact.class );
            }

            cw = createClassWriter( classLoader,
                                    ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                                    cName,
                                    genericTypes,
                                    superType,
                                    intfaces );

            {
                if ( classDef.getDefinedClass() == null || classDef.getDefinedClass().getAnnotation( Trait.class ) == null ) {
                    AnnotationVisitor av0 = cw.visitAnnotation( Type.getDescriptor( Trait.class ), true);
                    for ( AnnotationDefinition adef : classDef.getAnnotations() ) {
                        if ( Trait.class.getName().equals( adef.getName() ) ) {
                            addAnnotationAttribute( adef, av0 );
                            break;
                        }
                    }
                    av0.visitEnd();
                }
            }

            for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
                buildField( cw, field );
            }

            finalizeCreation( classDef );

            cw.visitEnd();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return cw.toByteArray();
    }

    protected void init( ClassDefinition classDef ) {

    }

    private void buildField( ClassWriter cw, FieldDefinition field ) {

        String name = field.getName();
            name = name.substring( 0,1 ).toUpperCase() + name.substring( 1 );
        String type = field.getTypeName();

        buildGetter( cw, field, name, type, null );

        buildSetter( cw, field, name, type, null );

    }

    protected void buildSetter( ClassWriter cw, FieldDefinition field, String name, String type, String generic ) {

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                BuildUtils.setterName( name, type ),
                "(" + BuildUtils.getTypeDescriptor( type ) + ")V",
                generic == null ? null :
                        "(" + BuildUtils.getTypeDescriptor( type ).replace( ";", "<" + BuildUtils.getTypeDescriptor( generic ) + ">;") + ")V",
                null );
        mv.visitEnd();

    }

    protected void buildGetter( ClassWriter cw, FieldDefinition field, String name, String type, String generic ) {
        
        name = name.substring( 0, 1 ).toUpperCase() + name.substring( 1 );
        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                BuildUtils.getterName( name, type ),
                "()" + BuildUtils.getTypeDescriptor( type ),
                generic == null ? null :
                        "()" + BuildUtils.getTypeDescriptor( type ).replace( ";", "<" + BuildUtils.getTypeDescriptor( generic ) + ">;" ),
                null );
        mv.visitEnd();

    }

    protected void finalizeCreation(ClassDefinition trait) {

    }

}
