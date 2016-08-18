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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndexOfFunction
        extends BaseFEELFunction {

    public IndexOfFunction() {
        super( "index of" );
    }

    @Override
    public List<List<String>> getParameterNames() {
        return Arrays.asList(
                Arrays.asList( "list, match" )
        );
    }

    public List apply(List list, Object match) {
        if ( list == null ) {
            return null;
        }
        List result = new ArrayList();
        for( int i = 0; i < list.size(); i++ ) {
            Object o = list.get( i );
            if ( ( o == null && match == null) ||
                 ( o != null && o.equals( match ) ) ) {
                result.add( BigDecimal.valueOf( i+1 ) );
            }
        }
        return result;
    }
}
