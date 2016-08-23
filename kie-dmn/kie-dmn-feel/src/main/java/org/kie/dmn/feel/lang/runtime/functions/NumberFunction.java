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

import java.math.BigDecimal;

public class NumberFunction
        extends BaseFEELFunction {

    public NumberFunction() {
        super( "number" );
    }

    public BigDecimal apply(@ParameterName("from") String from, @ParameterName("grouping separator") String group, @ParameterName("decimal separator") String decimal) {
        if ( from == null ) {
            return null;
        }
        if ( group != null && !group.equals( " " ) && !group.equals( "." ) && !group.equals( "," ) ) {
            return null;
        }
        if ( decimal != null && ((!decimal.equals( "." ) && !decimal.equals( "," )) || (group != null && decimal.equals( group ))) ) {
            return null;
        }
        if ( group != null ) {
            from = from.replaceAll( "\\" + group, "" );
        }
        if ( decimal != null ) {
            from = from.replaceAll( "\\" + decimal, "." );
        }
        return new BigDecimal( from );
    }

}
