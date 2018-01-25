/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.operators;

import java.util.Collection;

import org.drools.model.functions.Operator;

public class ContainsOperator implements Operator.SingleValue<Object, Object> {

    public static final ContainsOperator INSTANCE = new ContainsOperator();

    @Override
    public boolean eval( Object a, Object b ) {
        if ( a instanceof Collection ) {
            return (( Collection ) a).contains( b );
        }
        if (a instanceof Object[]) {
            for (Object o : (( Object[] ) a)) {
                if (o.equals( b )) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getOperatorName() {
        return "contains";
    }
}
