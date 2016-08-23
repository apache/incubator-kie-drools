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
import java.util.List;

public class SublistFunction
        extends BaseFEELFunction {

    public SublistFunction() {
        super( "sublist" );
    }

    public List apply(@ParameterName("list") List list, @ParameterName("start position") BigDecimal start) {
        return apply( list, start, null );
    }

    public List apply(@ParameterName("list") List list, @ParameterName("start position") BigDecimal start, @ParameterName("length") BigDecimal length) {
        if ( list == null || start == null || start.equals( BigDecimal.ZERO ) || start.abs().intValue() > list.size() ) {
            return null;
        }
        if ( start.intValue() > 0 ) {
            int end = length != null ? start.intValue() - 1 + length.intValue() : list.size();
            if ( end > list.size() ) {
                return null;
            }
            return list.subList( start.intValue() - 1, end );
        } else {
            int end = length != null ? list.size() + start.intValue() + length.intValue() : list.size();
            if ( end > list.size() ) {
                return null;
            }
            return list.subList( list.size() + start.intValue(), end );
        }
    }
}
