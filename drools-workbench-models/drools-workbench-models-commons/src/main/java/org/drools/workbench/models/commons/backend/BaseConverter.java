/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.models.commons.backend;

import org.drools.compiler.kie.builder.impl.FormatConverter;

public abstract class BaseConverter implements FormatConverter {

    protected String getDestinationName(String name) {
        return getDestinationName(name, false);
    }

    protected String getDestinationName(String name, boolean hasDsl) {
        return name.substring(0, name.lastIndexOf('.')) + (hasDsl ? ".dslr" : ".drl");
    }
}
