/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.efesto.common.core.identifiers.componentroots;

import org.kie.efesto.common.api.identifiers.Id;
import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class LocalComponentIdFoo extends LocalUriId implements Id {

    public static final String PREFIX = "foo";

    private final String fileName;
    private final String name;
    private final String secondName;


    public LocalComponentIdFoo(String fileName, String name, String secondName) {
        super(LocalUri.Root.append(PREFIX).append(fileName).append(name).append(secondName));
        this.fileName = fileName;
        this.name = name;
        this.secondName = secondName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public String getSecondName() {
        return secondName;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

}
