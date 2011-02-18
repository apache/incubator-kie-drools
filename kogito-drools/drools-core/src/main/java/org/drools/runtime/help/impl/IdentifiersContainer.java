/*
 * Copyright 2010 JBoss Inc
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

package org.drools.runtime.help.impl;

import org.drools.runtime.rule.FactHandle;

public class IdentifiersContainer {

    private String identifier;
    private int    index;

    public IdentifiersContainer() {

    }

    public IdentifiersContainer(String identifier,
                                int index) {
        this.identifier = identifier;
        this.index = index;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getIndex() {
        return index;
    }

}
