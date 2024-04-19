/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.api.identifiers;

import java.util.Objects;

import org.kie.efesto.common.api.identifiers.Id;
import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public class LocalComponentIdDmn extends ModelLocalUriId implements Id {

    public static final String PREFIX = "dmn";
    private static final long serialVersionUID = 8621199867598971641L;

    private final String fileName;
    private final String name;

    public LocalComponentIdDmn(String fileName, String name) {
        super(LocalUri.Root.append(PREFIX).append(fileName).append(name));
        this.fileName = fileName;
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        ModelLocalUriId that = (ModelLocalUriId) o;
        return Objects.equals(this.fullPath(), that.fullPath());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
