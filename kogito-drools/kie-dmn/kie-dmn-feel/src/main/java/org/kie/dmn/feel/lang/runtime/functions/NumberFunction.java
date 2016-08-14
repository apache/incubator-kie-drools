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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;

public class NumberFunction
        extends BaseFEELFunction {

    public NumberFunction() {
        super( "number" );
    }

    @Override
    public List<List<String>> getParameterNames() {
        return Arrays.asList(
                Arrays.asList( "from", "grouping separator", "decimal separator" )
        );
    }

    public BigDecimal apply(String from, String group, String decimal) {
        if( from == null ) {
            return null;
        }
        if( group != null && !group.equals( " " ) && !group.equals( "." ) && !group.equals( "," ) ) {
            return null;
        }
        if( decimal != null && (( !decimal.equals( "." ) && !decimal.equals( "," ) ) || ( group != null && decimal.equals( group ) ) ) ) {
            return null;
        }
        if( group != null ) {
            from = from.replaceAll( "\\"+group, "" );
        }
        if( decimal != null ) {
            from = from.replaceAll( "\\"+decimal, "." );
        }
        return new BigDecimal( from );
    }

    private DecimalFormat buildParser( String group, String decimal ) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        if( group != null ) {
            otherSymbols.setDecimalSeparator(group.charAt( 0 ));
        }
        if( decimal != null ) {
            otherSymbols.setGroupingSeparator(decimal.charAt( 0 ));
        }
        return new DecimalFormat("#,##0.0", otherSymbols);
    }
}
