/*
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
package org.drools.mvel.asm;

import org.mvel2.asm.MethodVisitor;

import static org.mvel2.asm.Opcodes.*;

/**
 * Utility class for handling type conversions in ASM bytecode generation.
 * Consolidates logic for converting between primitive types used by
 * getDecimalValue() and getWholeNumberValue() methods.
 */
public class TypeConversionHelper {

    // Method name constants
    public static final String GET_DECIMAL_VALUE = "getDecimalValue";
    public static final String GET_WHOLE_NUMBER_VALUE = "getWholeNumberValue";
    public static final String GET_VALUE = "getValue";

    /**
     * Represents the type of conversion needed for primitive types
     */
    public enum ConversionType {
        NONE,
        FLOAT,           // double -> float (D2F)
        BYTE,            // long -> int -> byte (L2I, I2B)
        SHORT,           // long -> int -> short (L2I, I2S)
        INT,             // long -> int (L2I)
        CHAR,            // long -> int (L2I) for char
        INTEGER_OBJECT   // long -> int -> Integer (L2I, valueOf)
    }

    /**
     * Determines the type of conversion needed based on read method and declaration type
     */
    public static ConversionType determineConversionType(String readMethod, String declarationType) {
        if (GET_DECIMAL_VALUE.equals(readMethod)) {
            if ("float".equals(declarationType) || "java.lang.Float".equals(declarationType)) {
                return ConversionType.FLOAT;
            }
        } else if (GET_WHOLE_NUMBER_VALUE.equals(readMethod)) {
            return switch (declarationType) {
                case "byte", "java.lang.Byte" -> ConversionType.BYTE;
                case "short", "java.lang.Short" -> ConversionType.SHORT;
                case "int" -> ConversionType.INT;
                case "java.lang.Integer" -> ConversionType.INTEGER_OBJECT;
                case "char", "java.lang.Character" -> ConversionType.CHAR;
                default -> ConversionType.NONE;
            };
        }
        return ConversionType.NONE;
    }

    /**
     * Returns the actual return type descriptor for the given read method
     */
    public static String getActualReturnType(String readMethod, boolean isObject, String typeDescr) {
        if (GET_DECIMAL_VALUE.equals(readMethod)) {
            return "D"; // getDecimalValue always returns double
        } else if (GET_WHOLE_NUMBER_VALUE.equals(readMethod)) {
            return "J"; // getWholeNumberValue always returns long
        } else {
            return isObject ? "Ljava/lang/Object;" : typeDescr;
        }
    }

    /**
     * Emits the appropriate type conversion bytecode instructions
     */
    public static void emitTypeConversion(MethodVisitor mv, ConversionType conversionType) {
        switch (conversionType) {
            case FLOAT:
                mv.visitInsn(D2F); // Convert double to float
                break;
            case BYTE:
                mv.visitInsn(L2I); // Convert long to int first
                mv.visitInsn(I2B); // Convert int to byte
                break;
            case SHORT:
                mv.visitInsn(L2I); // Convert long to int first
                mv.visitInsn(I2S); // Convert int to short
                break;
            case INT:
                mv.visitInsn(L2I); // Convert long to int
                break;
            case CHAR:
                mv.visitInsn(L2I); // Convert long to int for char
                break;
            case INTEGER_OBJECT:
                mv.visitInsn(L2I); // Convert long to int first
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                break;
            case NONE:
                // No conversion needed
                break;
        }
    }
}