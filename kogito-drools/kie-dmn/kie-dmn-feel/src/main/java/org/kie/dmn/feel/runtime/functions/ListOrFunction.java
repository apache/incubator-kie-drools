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

package org.kie.dmn.feel.runtime.functions;

import java.util.Arrays;
import java.util.List;

public class ListOrFunction
        extends BaseFEELFunction {

    public ListOrFunction() {
        super( "list or" );
    }

    public Boolean apply(@ParameterName( "list" ) List list) {
        boolean result = false;
        for ( Object element : list ) {
            if ( element instanceof Boolean ) {
                result |= ((Boolean) element);
                if ( result ) {
                    break;
                }
            } else {
                return null;
            }
        }
        return result;
    }

    public Boolean apply(@ParameterName( "list" ) Boolean single) {
        return single;
    }

    public Boolean apply(@ParameterName( "b" ) Object[] list) {
        return apply( Arrays.asList( list ) );
    }
}
