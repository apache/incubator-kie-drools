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
package org.kie.dmn.feel.marshaller;

import java.util.function.Function;

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.DateAndTimeFunction;
import org.kie.dmn.feel.runtime.functions.DateFunction;
import org.kie.dmn.feel.runtime.functions.DurationFunction;
import org.kie.dmn.feel.runtime.functions.NumberFunction;
import org.kie.dmn.feel.runtime.functions.StringFunction;
import org.kie.dmn.feel.runtime.functions.TimeFunction;

import static org.kie.dmn.feel.lang.types.BuiltInType.justNull;

/**
 * An implementation of the FEEL marshaller interface
 * that converts FEEL objects into it's string representation
 * and vice versa
 */
public class FEELStringMarshaller implements FEELMarshaller<String> {

    public static final FEELStringMarshaller INSTANCE = new FEELStringMarshaller();

    private FEELStringMarshaller() {}

    /**
     * Marshalls the give FEEL value into a String. The result is similar to
     * calling the string() function in a FEEL expression, with the difference
     * that a null value is returned as the "null" string instead of the null
     * value itself.
     *
     * @param value the FEEL value to be marshalled
     *
     * @return the string representation of the value
     */
    @Override
    public String marshall(Object value) {
        if( value == null ) {
            return "null";
        }
        return BuiltInFunctions.getFunction( StringFunction.class ).invoke( value ).cata( justNull(), Function.identity());
    }

    /**
     * Unmarshalls the given string into a FEEL value.
     *
     * IMPORTANT: please note that it is only possible to unmarshall simple
     * values, like strings and numbers. Complex values like lists and contexts
     * don't have enough metadata marshalled in the string to enable them to be
     * unmarshalled.
     *
     * @param feelType the expected type of the value to be unmarshalled
     * @param value the marshalled value to unmarshall
     *
     * @return the value resulting from the unmarshalling of the string
     *
     * @throws UnsupportedOperationException in case the type is a complex type,
     *         i.e. RANGE, FUNCTION, CONTEXT, LIST or UNARY_TEST, the implementation
     *         raises the exception.
     */
    @Override
    public Object unmarshall(Type feelType, String value) {
        if ( "null".equals( value ) ) {
            return null;
        } else if ( feelType.equals( org.kie.dmn.feel.lang.types.BuiltInType.NUMBER ) ) {
            return BuiltInFunctions.getFunction( NumberFunction.class ).invoke( value, null, null ).cata( justNull(), Function.identity() );
        } else if ( feelType.equals( org.kie.dmn.feel.lang.types.BuiltInType.STRING ) || feelType.equals( org.kie.dmn.feel.lang.types.BuiltInType.UNKNOWN ) ) {
            return value;
        } else if ( feelType.equals( org.kie.dmn.feel.lang.types.BuiltInType.DATE ) ) {
            return BuiltInFunctions.getFunction( DateFunction.class ).invoke( value ).cata( justNull(), Function.identity() );
        } else if ( feelType.equals( org.kie.dmn.feel.lang.types.BuiltInType.TIME ) ) {
            return BuiltInFunctions.getFunction( TimeFunction.class ).invoke( value ).cata( justNull(), Function.identity() );
        } else if ( feelType.equals( org.kie.dmn.feel.lang.types.BuiltInType.DATE_TIME ) ) {
            return BuiltInFunctions.getFunction( DateAndTimeFunction.class ).invoke( value ).cata( justNull(), Function.identity() );
        } else if ( feelType.equals( org.kie.dmn.feel.lang.types.BuiltInType.DURATION ) ) {
            return BuiltInFunctions.getFunction( DurationFunction.class ).invoke( value ).cata( justNull(), Function.identity() );
        } else if ( feelType.equals( org.kie.dmn.feel.lang.types.BuiltInType.BOOLEAN ) ) {
            return Boolean.parseBoolean( value );
        } else if ( feelType.equals( org.kie.dmn.feel.lang.types.BuiltInType.RANGE ) || feelType.equals( org.kie.dmn.feel.lang.types.BuiltInType.FUNCTION ) || feelType.equals( org.kie.dmn.feel.lang.types.BuiltInType.LIST )
                    || feelType.equals( org.kie.dmn.feel.lang.types.BuiltInType.CONTEXT ) || feelType.equals( org.kie.dmn.feel.lang.types.BuiltInType.UNARY_TEST ) ) {
            throw new UnsupportedOperationException( "FEELStringMarshaller is unable to unmarshall complex types like: "+feelType.getName() );
        }
        return null;
    }
}
