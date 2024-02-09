/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.util.lambdareplace;

import java.util.Objects;

public class CreatedClass {

    private final String contents;
    private final String className;
    private final String packageName;
    private final String canonicalName;
    private final String sourcePath;

    public CreatedClass(String contents, String className, String packageName) {
        this.className = className;
        this.packageName = packageName;
        this.contents = contents;
        this.canonicalName = packageName + "." + className;
        this.sourcePath = this.canonicalName.replace('.', '/') + ".java";
    }

    public String getClassNameWithPackage() {
        return canonicalName;
    }

    public String getClassNamePath() {
        return sourcePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreatedClass that = (CreatedClass) o;
        return contents.equals(that.contents) &&
                className.equals(that.className) &&
                packageName.equals(that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contents, className, packageName);
    }

    public String getContents() {
        return contents;
    }
}
