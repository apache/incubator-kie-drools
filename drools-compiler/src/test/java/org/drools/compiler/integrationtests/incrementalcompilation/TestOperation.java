/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests.incrementalcompilation;

import java.util.Arrays;

public class TestOperation {
    private final TestOperationType type;
    private final Object parameter;

    public TestOperation(final TestOperationType type, final Object parameter) {
        this.type = type;
        this.parameter = parameter;
    }

    public TestOperationType getType() {
        return type;
    }

    public Object getParameter() {
        return parameter;
    }

    @Override
    public String toString() {
        if (parameter == null) {
            return type.toString();
        }
        return type + ": " + ( parameter instanceof Object[] ? Arrays.toString( (Object[]) parameter ) : parameter.toString() );
    }
}
