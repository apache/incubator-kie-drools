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

package org.drools.core.factmodel;

public final class BuildUtils {

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
            if (temp == null) {
                throw new RuntimeException("Cannot resolve internal type from interface " + intf + "!");
            } else {
                sb.append( temp.replace( ";", "<TK;>;" ) );
            }
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
            while ( type.charAt( ++j ) == '[' ) {
                // Just ignore these chars.
            }
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




    // Can only be used with internal names, i.e. after [ has been prefix
    public static String arrayType( String type ) {
        if ( isArray( type ) )
            if ( type.length() == arrayDimSize(type) +1  ) {
                return type;
            } else {
                StringBuilder ans = new StringBuilder("Ljava/lang/Object;");
                for ( int j = 0; j < arrayDimSize( type ); j++ ) {
                    ans.insert(0, "[");
                }
                return ans.toString();
            }
        return null;
    }


    public static int externalArrayDimSize( String className ) {
        int j = className.length() - 1;
        int dim = 0;
        while ( j > 0 && className.charAt( j ) == ']' ) {
            dim++;
            j = j - 2;
        }
        return dim;
    }

    // Can only be used with internal names, i.e. after [ has been prefix
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
     * Can only be used with internal names, i.e. after [ has been prefix
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
            throw new RuntimeException("Unable to recognize boxed primitive type " + type);
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
            throw new RuntimeException("Unable to recognize primitive type " + type);
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

    public static boolean isBoolean(String type) {
        return "boolean".equals( type );
    }

    public static String getterName( String fieldName, String type ) {
        String prefix = BuildUtils.isBoolean( type ) ? "is" : "get";
        return prefix + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
    }


    public static String setterName(String fieldName) {
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
        } else if ( "java.lang.Character".equals( type ) || "Character".equals( type )) {
            return "charValue";
        } else if ( "java.lang.Boolean".equals( type ) || "Boolean".equals( type )) {
            return "booleanValue";
        } else if ( "java.lang.Float".equals( type ) || "Float".equals( type )) {
            return "floatValue";
        } else if ( "java.lang.Integer".equals( type ) || "Integer".equals( type )) {
            return "intValue";
        } else if ( "java.lang.Long".equals( type ) || "Long".equals( type )) {
            return "longValue";
        } else if ( "java.lang.Short".equals( type ) || "Short".equals( type )) {
            return "shortValue";
        } else {
            throw new RuntimeException("Not a numeric type " + type);
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
                throw new RuntimeException( "No serialization method found for " + type );
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
                    throw new RuntimeException( "No serialization method found for " + type );
                }
            } else {
                return "readObject";
            }
        }

    public static String serializationType( String type ) {
            if ( isPrimitive( type ) ) {
                if ( "byte".equals( type ) ) {
                    return "int";
                } else if ( "char".equals( type ) ) {
                    return "int";
                } else if ( "double".equals( type ) ) {
                    return "double";
                } else if ( "float".equals( type ) ) {
                    return "float";
                } else if ( "int".equals( type ) ) {
                    return "int";
                } else if ( "long".equals( type ) ) {
                    return "long";
                } else if ( "short".equals( type ) ) {
                    return "int";
                } else if ( "boolean".equals( type ) ) {
                    return "boolean";
                } else {
                    return type;
                }
            } else {
                return type;
            }
    }

    private BuildUtils() {
        // It is not allowed to create instances of util classes.
    }
}
