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

import java.util.List;

import org.drools.core.phreak.AbstractReactiveObject;
import org.drools.core.phreak.ReactiveList;

public class TMDirectory extends AbstractReactiveObject {
    private final String name;
    private final List<TMFile> members = new ReactiveList<TMFile>();

    public TMDirectory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<TMFile> getFiles() {
        return members;
    }
}
