/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.xpath;

import java.util.ArrayList;
import java.util.List;

public class Thing {
    private final String name;
    private final List<Thing> children = new ArrayList<Thing>();

    public Thing( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addChild(Thing child) {
        children.add(child);
    }

    public List<Thing> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return name;
    }
}
