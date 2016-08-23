/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.lang.runtime.functions;

public class SubstringFunction
        extends BaseFEELFunction {

    public SubstringFunction() {
        super( "substring" );
    }

    public String apply(@ParameterName("string") String string, @ParameterName("start position") Number start) {
        if ( string == null || start == null || Math.abs( start.intValue() ) > string.length() ) {
            return null;
        } else {
            if ( start.intValue() > 0 ) {
                return string.substring( start.intValue() - 1 );
            } else if ( start.intValue() < 0 ) {
                return string.substring( string.length() + start.intValue() );
            } else {
                return null;
            }
        }
    }

    public String apply(@ParameterName("string") String string, @ParameterName("start position") Number start, @ParameterName("length") Number length) {
        if ( string == null || start == null || Math.abs( start.intValue() ) > string.length() ) {
            return null;
        } else {
            if ( start.intValue() > 0 ) {
                return string.substring( start.intValue() - 1, Math.min( string.length(), start.intValue() + length.intValue() - 1 ) );
            } else if ( start.intValue() < 0 ) {
                return string.substring( string.length() + start.intValue(), Math.min( string.length(), string.length() + start.intValue() + length.intValue() ) );
            } else {
                return null;
            }
        }
    }
}
