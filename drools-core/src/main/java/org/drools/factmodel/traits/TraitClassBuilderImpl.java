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
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.MethodVisitor;

import java.util.Arrays;

public class TraitClassBuilderImpl implements TraitClassBuilder {


    public byte[] buildClass( ClassDefinition classDef ) {

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
                String[] tmp = BuildUtils.getInternalTypes( classDef.getInterfaces() ); 
                intfaces = new String[ tmp.length + 1 ];
                System.arraycopy( tmp, 0, intfaces, 0, tmp.length );
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




}
