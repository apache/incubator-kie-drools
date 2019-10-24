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

import java.util.List;
import java.util.ListIterator;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.EvalHelper;

public class ListContainsFunction
        extends BaseFEELFunction {

    public ListContainsFunction() {
        super( "list contains" );
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("list") List list, @ParameterName("element") Object element) {
        if ( list == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null"));
        } else {
            if (element == null) {
                return FEELFnResult.ofResult(list.contains(element));
            } else {
                Object e = EvalHelper.coerceNumber(element);
                boolean found = false;
                ListIterator<?> it = list.listIterator();
                while (it.hasNext() && !found) {
                    Object next = EvalHelper.coerceNumber(it.next());
                    found = e.equals(next);
                }
                return FEELFnResult.ofResult(found);
            }
        }
    }

}
