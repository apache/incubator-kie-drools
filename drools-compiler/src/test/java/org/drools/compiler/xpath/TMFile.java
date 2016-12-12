/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.xpath;

import org.drools.core.phreak.AbstractReactiveObject;

public class TMFile extends AbstractReactiveObject {
    private final String name;
    private int size;

    public TMFile(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        notifyModification();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TMFile [name=").append(name).append(", size=").append(size).append("]");
        return builder.toString();
    }
}
