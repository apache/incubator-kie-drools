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

import org.drools.model.functions.Operator;

public enum MatchesOperator implements Operator.SingleValue<String, String> {

    INSTANCE;

    @Override
    public boolean eval( String s1, String s2 ) {
        return s1 != null && s1.matches( s2 );
    }

    @Override
    public String getOperatorName() {
        return "matches";
    }
}
