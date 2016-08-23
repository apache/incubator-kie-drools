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
import java.util.Arrays;
import java.util.List;

public class CountFunction
        extends BaseFEELFunction {

    public CountFunction() {
        super( "count" );
    }

    public BigDecimal apply(@ParameterName( "list" ) List list) {
        if ( list == null ) {
            return null;
        } else {
            return BigDecimal.valueOf( list.size() );
        }
    }

    public BigDecimal apply(@ParameterName( "c" ) Object[] list) {
        return apply( Arrays.asList( list ) );
    }

}
