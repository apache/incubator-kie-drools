/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.api.identifiers;

import java.util.Objects;

import org.kie.efesto.common.api.identifiers.Id;
import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public class LocalComponentIdPmml extends ModelLocalUriId implements Id {
    public static final String PREFIX = "pmml";
    private static final long serialVersionUID = 8621199867598971641L;

    private final String fileName;

    private final String name;

    public LocalComponentIdPmml(String fileName, String name) {
        super(LocalUri.Root.append(PREFIX).append(fileName).append(name));
        this.fileName = fileName;
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public String name() {
        return name;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        LocalComponentIdPmml that = (LocalComponentIdPmml) o;
        return Objects.equals(fileName, that.fileName) && Objects.equals(name, that.name) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileName, name);
    }

}
