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

package org.drools.factmodel;

import org.drools.RuntimeDroolsException;
import org.drools.core.util.StringUtils;
import org.mvel2.MVEL;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Opcodes;

import java.io.ObjectOutput;

public class BuildUtils {





    public static String[] getInternalTypes( String[] superClasses ) {
        if ( superClasses == null ) {
            return null;
        }
        String[] internals = new String[ superClasses.length ];
        for ( int j = 0; j < internals.length; j++ ) {
            internals[ j ] = getInternalType( superClasses[ j ] );
        }
        return internals.length > 0 ? internals : null;
    }


    public static String getGenericTypes(String[] interfaces) {

        StringBuilder sb = new StringBuilder();
        sb.append("<K:").append( getTypeDescriptor( Object.class.getName() ) ).append( ">" );
        sb.append( getTypeDescriptor( Object.class.getName() ) );

        for ( String intf : interfaces ) {
            String temp = getTypeDescriptor( intf );
            sb.append( temp.replace( ";", "<TK;>;" ) );
        }
        return sb.toString();
    }



    /**
     * Returns the corresponding internal type representation for the
     * given type.
     *
     * I decided to not use the ASM Type class methods because they require
     * resolving the actual type into a Class instance and at this point,
     * I think it is best to delay type resolution until it is really needed.
     *
     * @param type
     * @return
     */
    public static String getInternalType(String type) {
        String internalType = null;
        if ( "byte".equals( type ) ) {
            internalType = "B";
        } else if ( "char".equals( type ) ) {
            internalType = "C";
        } else if ( "double".equals( type ) ) {
            internalType = "D";
        } else if ( "float".equals( type ) ) {
            internalType = "F";
        } else if ( "int".equals( type ) ) {
            internalType = "I";
        } else if ( "long".equals( type ) ) {
            internalType = "J";
        } else if ( "short".equals( type ) ) {
            internalType = "S";
        } else if ( "boolean".equals( type ) ) {
            internalType = "Z";
        } else if ( "void".equals( type ) ) {
            internalType = "V";
        } else if ( type != null ) {
            // I think this will fail for inner classes, but we don't really
            // support inner class generation at the moment
            internalType = type.replace( '.',
                    '/' );
        }
        return internalType;
    }

    /**
     * Returns the corresponding type descriptor for the
     * given type.
     *
     * I decided to not use the ASM Type class methods because they require
     * resolving the actual type into a Class instance and at this point,
     * I think it is best to delay type resolution until it is really needed.
     *
     * @param type
     * @return
     */
    public static String getTypeDescriptor(String type) {
        String internalType = null;
        if ( "byte".equals( type ) ) {
            internalType = "B";
        } else if ( "char".equals( type ) ) {
            internalType = "C";
        } else if ( "double".equals( type ) ) {
            internalType = "D";
        } else if ( "float".equals( type ) ) {
            internalType = "F";
        } else if ( "int".equals( type ) ) {
            internalType = "I";
        } else if ( "long".equals( type ) ) {
            internalType = "J";
        } else if ( "short".equals( type ) ) {
            internalType = "S";
        } else if ( "boolean".equals( type ) ) {
            internalType = "Z";
        } else if ( "void".equals( type ) ) {
            internalType = "V";
        } else if ( type != null && type.startsWith( "[" ) ) {
            int j = 0;
            while ( type.charAt( ++j ) == '[' ) {}
            if ( type.charAt( j ) == 'L' ) {
                internalType = type.replace( '.', '/' );
            } else {
                internalType = type;
            }
        } else if ( type != null ) {
            // I think this will fail for inner classes, but we don't really
            // support inner class generation at the moment
            internalType = "L" + type.replace( '.', '/' ) + ";";
        }
        return internalType;
    }



    public static String arrayType( String type ) {
        if ( isArray( type ) )
            if ( type.length() == arrayDimSize(type) +1  ) {
                return type;
            } else {
                String ans = "Ljava/lang/Object;";
                for ( int j = 0; j < arrayDimSize( type ); j++ ) {
                    ans = "[" + ans;
                }
                return ans;
            }
        return null;
    }

    public static int arrayDimSize(String type) {
        int j = 0;
        while ( type.charAt( j ) == '[' ) {
            j++;
        }
        return j;
    }

    /**
     * Returns true if the provided type is a primitive type
     *
     * @param type
     * @return
     */
    public static boolean isPrimitive(String type) {
        boolean isPrimitive = false;
        if ( "byte".equals( type ) || "char".equals( type ) || "double".equals( type ) || "float".equals( type ) || "int".equals( type ) || "long".equals( type ) || "short".equals( type ) || "boolean".equals( type ) || "void".equals( type ) ) {
            isPrimitive = true;
        }
        return isPrimitive;
    }



    /**
     * Returns true if the provided type is an arrayType
     *
     * @param type
     * @return
     */
    public static boolean isArray( String type ) {
        return type.startsWith( "[" );
    }


    public static Object getDefaultValue( FieldDefinition fld ) {
        String type = fld.getTypeName();
        if ( "byte".equals( type ) ) {
            return fld.getDefaultValueAs_byte();
        } else if ( "char".equals( type ) ) {
            return fld.getDefaultValueAs_char();
        } else if ( "double".equals( type ) ) {
            return fld.getDefaultValueAs_double();
        } else if ( "float".equals( type ) ) {
            return fld.getDefaultValueAs_float();
        } else if ( "int".equals( type ) ) {
            return fld.getDefaultValueAs_int();
        } else if ( "long".equals( type ) ) {
            return fld.getDefaultValueAs_long();
        } else if ( "short".equals( type ) ) {
            return fld.getDefaultValueAs_short();
        } else if ( "boolean".equals( type ) ) {
            return fld.getDefaultValueAs_boolean();

        } else if ( "java.lang.String".equals( type ) ) {
            return fld.getDefaultValueAsString();

        } else if ( "java.lang.Byte".equals( type ) || "Byte".equals( type )) {
            return fld.getDefaultValueAsByte();
        } else if ( "java.lang.Character".equals( type ) || "Character".equals( type ) ) {
            return fld.getDefaultValueAsChar();
        } else if ( "java.lang.Double".equals( type ) || "Double".equals( type )) {
            return fld.getDefaultValueAsDouble();
        } else if ( "java.lang.Float".equals( type ) || "Float".equals( type )) {
            return fld.getDefaultValueAsFloat();
        } else if ( "java.lang.Integer".equals( type ) || "Integer".equals( type )) {
            return fld.getDefaultValueAsInt();
        } else if ( "java.lang.Long".equals( type ) || "Long".equals( type )) {
            return fld.getDefaultValueAsLong();
        } else if ( "java.lang.Short".equals( type ) || "Short".equals( type )) {
            return fld.getDefaultValueAsShort();
        } else if ( "java.lang.Boolean".equals( type ) || "Boolean".equals( type )) {
            return fld.getDefaultValueAsBoolean();
        }

//        return StringUtils.isEmpty( fld.getInitExpr() ) ? null : MVEL.eval( fld.getInitExpr() );
        return null;

    }

    public static boolean isBoxed(String type) {
        if (type == null) return false;
        return "java.lang.Short".equals(type)
                || "java.lang.Byte".equals(type)
                || "java.lang.Character".equals(type)
                || "java.lang.Double".equals(type)
                || "java.lang.Float".equals(type)
                || "java.lang.Integer".equals(type)
                || "java.lang.Boolean".equals(type)
                || "java.lang.Long".equals(type) ;
    }



    public static String unBox(String type) {
        if ( "java.lang.Byte".equals( type ) || "Byte".equals( type )) {
            return getInternalType("byte");
        } else if ( "java.lang.Character".equals( type ) || "Character".equals( type ) ) {
            return getInternalType("char");
        } else if ( "java.lang.Double".equals( type ) || "Double".equals( type )) {
            return getInternalType("double");
        } else if ( "java.lang.Float".equals( type ) || "Float".equals( type )) {
            return getInternalType("float");
        } else if ( "java.lang.Integer".equals( type ) || "Integer".equals( type )) {
            return getInternalType("int");
        } else if ( "java.lang.Long".equals( type ) || "Long".equals( type )) {
            return getInternalType("long");
        } else if ( "java.lang.Short".equals( type ) || "Short".equals( type )) {
            return getInternalType("short");
        } else if ( "java.lang.Boolean".equals( type ) || "Boolean".equals( type )) {
            return getInternalType("boolean");
        } else {
            throw new RuntimeDroolsException("Unable to recognize boxed primitive type " + type);
        }
    }




    public static String box(String type) {
        if ( "byte".equals( type ) ) {
            return "java.lang.Byte";
        } else if ( "char".equals( type ) ) {
            return "java.lang.Character";
        } else if ( "double".equals( type ) ) {
            return "java.lang.Double";
        } else if ( "float".equals( type ) ) {
            return "java.lang.Float";
        } else if ( "int".equals( type ) ) {
            return "java.lang.Integer";
        } else if ( "long".equals( type ) ) {
            return "java.lang.Long";
        } else if ( "short".equals( type ) ) {
            return "java.lang.Short";
        } else if ( "boolean".equals( type ) ) {
            return "java.lang.Boolean";
        } else {
            throw new RuntimeDroolsException("Unable to recognize primitive type " + type);
        }
    }


    public static int sizeOf(String type) {
        if ( "byte".equals( type ) ) {
            return 1;
        } else if ( "char".equals( type ) ) {
            return 1;
        } else if ( "double".equals( type ) ) {
            return 2;
        } else if ( "float".equals( type ) ) {
            return 1;
        } else if ( "int".equals( type ) ) {
            return 1;
        } else if ( "long".equals( type ) ) {
            return 2;
        } else if ( "short".equals( type ) ) {
            return 1;
        } else if ( "boolean".equals( type ) ) {
            return 1;
        } else {
            return 1;
        }
    }


    public static int returnType(String type) {
        if ( "byte".equals( type ) ) {
            return Opcodes.IRETURN;
        } else if ( "char".equals( type ) ) {
            return Opcodes.IRETURN;
        } else if ( "double".equals( type ) ) {
            return Opcodes.DRETURN;
        } else if ( "float".equals( type ) ) {
            return Opcodes.FRETURN;
        } else if ( "int".equals( type ) ) {
            return Opcodes.IRETURN;
        } else if ( "long".equals( type ) ) {
            return Opcodes.LRETURN;
        } else if ( "short".equals( type ) ) {
            return Opcodes.IRETURN;
        } else if ( "boolean".equals( type ) ) {
            return Opcodes.IRETURN;
        } else if ( "void".equals( type ) ) {
            return Opcodes.RETURN;
        } else {
            return Opcodes.ARETURN;
        }

    }


    public static int varType(String type) {
        if ( "byte".equals( type ) ) {
            return Opcodes.ILOAD;
        } else if ( "char".equals( type ) ) {
            return Opcodes.ILOAD;
        } else if ( "double".equals( type ) ) {
            return Opcodes.DLOAD;
        } else if ( "float".equals( type ) ) {
            return Opcodes.FLOAD;
        } else if ( "int".equals( type ) ) {
            return Opcodes.ILOAD;
        } else if ( "long".equals( type ) ) {
            return Opcodes.LLOAD;
        } else if ( "short".equals( type ) ) {
            return Opcodes.ILOAD;
        } else if ( "boolean".equals( type ) ) {
            return Opcodes.ILOAD;
        } else {
            return Opcodes.ALOAD;
        }
    }


    public static int storeType(String type) {
        if ( "byte".equals( type ) ) {
            return Opcodes.ISTORE;
        } else if ( "char".equals( type ) ) {
            return Opcodes.ISTORE;
        } else if ( "double".equals( type ) ) {
            return Opcodes.DSTORE;
        } else if ( "float".equals( type ) ) {
            return Opcodes.FSTORE;
        } else if ( "int".equals( type ) ) {
            return Opcodes.ISTORE;
        } else if ( "long".equals( type ) ) {
            return Opcodes.LSTORE;
        } else if ( "short".equals( type ) ) {
            return Opcodes.ISTORE;
        } else if ( "boolean".equals( type ) ) {
            return Opcodes.ISTORE;
        } else {
            return Opcodes.ASTORE;
        }
    }

    public static boolean isBoolean(String type) {
        return "boolean".equals( type );
//                || "java.lang.Boolean".equals( type )
//                || "Boolean".equals( type );
    }

    public static int zero( String type ) {
        if ( "byte".equals( type ) ) {
            return Opcodes.ICONST_0;
        } else if ( "char".equals( type ) ) {
            return Opcodes.ICONST_0;
        } else if ( "double".equals( type ) ) {
            return Opcodes.DCONST_0;
        } else if ( "float".equals( type ) ) {
            return Opcodes.FCONST_0;
        } else if ( "int".equals( type ) ) {
            return Opcodes.ICONST_0;
        } else if ( "long".equals( type ) ) {
            return Opcodes.LCONST_0;
        } else if ( "short".equals( type ) ) {
            return Opcodes.ICONST_0;
        } else if ( "boolean".equals( type ) ) {
            return Opcodes.ICONST_0;
        } else {
            return Opcodes.ACONST_NULL;
        }

    }




    public static String getterName( String fieldName, String type ) {
        String prefix = BuildUtils.isBoolean( type ) ? "is" : "get";
        return prefix + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
    }


    public static String setterName(String fieldName, String type) {
        return "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
    }


    public static String extractor(String type) {
        if ( "byte".equals( type ) ) {
            return "getByteValue";
        } else if ( "char".equals( type ) ) {
            return "getCharValue";
        } else if ( "double".equals( type ) ) {
            return "getDoubleValue";
        } else if ( "float".equals( type ) ) {
            return "getFloatValue";
        } else if ( "int".equals( type ) ) {
            return "getIntValue";
        } else if ( "long".equals( type ) ) {
            return "getLongValue";
        } else if ( "short".equals( type ) ) {
            return "getShortValue";
        } else if ( "boolean".equals( type ) ) {
            return "getBooleanValue";
        } else {
            return "getValue";
        }
    }

    public static String injector( String type ) {
        if ( "byte".equals( type ) ) {
            return "setByteValue";
        } else if ( "char".equals( type ) ) {
            return "setCharValue";
        } else if ( "double".equals( type ) ) {
            return "setDoubleValue";
        } else if ( "float".equals( type ) ) {
            return "setFloatValue";
        } else if ( "int".equals( type ) ) {
            return "setIntValue";
        } else if ( "long".equals( type ) ) {
            return "setLongValue";
        } else if ( "short".equals( type ) ) {
            return "setShortValue";
        } else if ( "boolean".equals( type ) ) {
            return "setBooleanValue";
        } else {
            return "setValue";
        }
    }

    public static String numericMorph(String type) {
        if ( "java.lang.Byte".equals( type ) || "Byte".equals( type )) {
            return "byteValue";
        } else if ( "java.lang.Double".equals( type ) || "Double".equals( type )) {
            return "doubleValue";
        } else if ( "java.lang.Float".equals( type ) || "Float".equals( type )) {
            return "floatValue";
        } else if ( "java.lang.Integer".equals( type ) || "Integer".equals( type )) {
            return "intValue";
        } else if ( "java.lang.Long".equals( type ) || "Long".equals( type )) {
            return "longValue";
        } else if ( "java.lang.Short".equals( type ) || "Short".equals( type )) {
            return "shortValue";
        } else {
            throw new RuntimeDroolsException("Not a numeric type " + type);
        }

    }

    public static void pushInt(MethodVisitor mv, int j) {
        switch ( j ) {
            case 0 : mv.visitInsn( Opcodes.ICONST_0 );
                break;
            case 1 : mv.visitInsn( Opcodes.ICONST_1 );
                break;
            case 2 : mv.visitInsn( Opcodes.ICONST_2 );
                break;
            case 3 : mv.visitInsn( Opcodes.ICONST_3 );
                break;
            case 4 : mv.visitInsn( Opcodes.ICONST_4 );
                break;
            case 5 : mv.visitInsn( Opcodes.ICONST_5 );
                break;
            default : mv.visitIntInsn( Opcodes.BIPUSH, j );
        }
    }


    public static String serializationWriterName( String type ) {
        if ( isPrimitive( type ) ) {
            if ( "byte".equals( type ) ) {
                return "writeByte";
            } else if ( "char".equals( type ) ) {
                return "writeChar";
            } else if ( "double".equals( type ) ) {
                return "writeDouble";
            } else if ( "float".equals( type ) ) {
                return "writeFloat";
            } else if ( "int".equals( type ) ) {
                return "writeInt";
            } else if ( "long".equals( type ) ) {
                return "writeLong";
            } else if ( "short".equals( type ) ) {
                return "writeShort";
            } else if ( "boolean".equals( type ) ) {
                return "writeBoolean";
            } else {
                throw new RuntimeDroolsException( "No serialization method found for " + type );
            }
        } else {
            return "writeObject";
        }
    }

    public static String serializationReaderName( String type ) {
            if ( isPrimitive( type ) ) {
                if ( "byte".equals( type ) ) {
                    return "readByte";
                } else if ( "char".equals( type ) ) {
                    return "readChar";
                } else if ( "double".equals( type ) ) {
                    return "readDouble";
                } else if ( "float".equals( type ) ) {
                    return "readFloat";
                } else if ( "int".equals( type ) ) {
                    return "readInt";
                } else if ( "long".equals( type ) ) {
                    return "readLong";
                } else if ( "short".equals( type ) ) {
                    return "readShort";
                } else if ( "boolean".equals( type ) ) {
                    return "readBoolean";
                } else {
                    throw new RuntimeDroolsException( "No serialization method found for " + type );
                }
            } else {
                return "readObject";
            }
        }
}
