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

import java.util.Arrays;
import java.util.List;

public class SubstringBeforeFunction
        extends BaseFEELFunction {

    public SubstringBeforeFunction() {
        super( "substring before" );
    }

    @Override
    public List<List<String>> getParameterNames() {
        return Arrays.asList(
                Arrays.asList( "string", "match" )
        );
    }

    public String apply(String string, String match) {
        if ( string == null || match == null ) {
            return null;
        } else {
            int index = string.indexOf( match );
            if( index > 0 ) {
                return string.substring( 0, index );
            } else{
                return "";
            }
        }
    }

}
