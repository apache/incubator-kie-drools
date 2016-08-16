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
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;

public class SumFunction
        extends BaseFEELFunction {

    public SumFunction() {
        super( "sum" );
    }

    @Override
    public List<List<String>> getParameterNames() {
        return Arrays.asList(
                Arrays.asList( "list" ),
                Arrays.asList( "n..." )
        );
    }


    public BigDecimal apply(List list) {
        BigDecimal sum = BigDecimal.ZERO;
        for( Object element : list ) {
            if( element instanceof BigDecimal ) {
                sum = sum.add( (BigDecimal) element );
            } else if( element instanceof Number ) {
                sum = sum.add( new BigDecimal( ((Number)element).toString() ) );
            } else {
                return null;
            }
        }
        return sum;
    }

    public BigDecimal apply(Number single) {
        if( single instanceof BigDecimal ) {
            return (BigDecimal) single;
        } else if( single != null ) {
            return new BigDecimal( single.toString() );
        } else {
            return null;
        }
    }

    public BigDecimal apply(Object[] list) {
        return apply( Arrays.asList( list ) );
    }
}
