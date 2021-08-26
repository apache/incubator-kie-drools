/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvel;

import java.util.Map;

public class EvaluatorTemplate implements org.drools.mvel2.CompiledJavaEvaluator {

    @Override
    public Object eval(java.util.Map map) {
        // declarations are on top
        int usedBinding;

        // binding assignment
        {
            usedBinding = (int) map.get("usedBinding");
        }

        // execute MVEL here
        {

        }

        // repopulate map
        {
            map.put("usedBinding", usedBinding);
        }

        return usedBinding;
    }
}
