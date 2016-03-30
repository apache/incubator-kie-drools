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
package org.drools.compiler.compiler;

import org.drools.compiler.builder.impl.ProjectCompilationResult;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.drools.compiler.builder.impl.ProjectCompilationResult;
import org.kie.api.io.Resource;

public class PackageCompilationResult extends ProjectCompilationResult {

    private final String packageName;

    public PackageCompilationResult(String pkgName) {
       this.packageName = pkgName;
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public String toString() {
        return this.packageName + ": " + super.toString();
    }


}
