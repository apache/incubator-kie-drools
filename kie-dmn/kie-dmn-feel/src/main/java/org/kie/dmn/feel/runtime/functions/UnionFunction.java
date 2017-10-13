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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class UnionFunction
        extends BaseFEELFunction {

    public UnionFunction() {
        super( "union" );
    }

    public FEELFnResult<List<Object>> invoke(@ParameterName("list") Object[] lists) {
        if ( lists == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "lists", "cannot be null"));
        }

        final Set<Object> resultSet = new LinkedHashSet<>();
        for ( final Object list : lists ) {
            if ( list instanceof Collection ) {
                resultSet.addAll((Collection) list);
            } else {
                resultSet.add(list);
            }
        }

        // spec requires us to return a new list
        return FEELFnResult.ofResult( new ArrayList<>(resultSet) );
    }
}
