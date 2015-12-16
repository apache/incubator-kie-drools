/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang.api;

import org.drools.compiler.lang.api.impl.PackageDescrBuilderImpl;
import org.kie.api.io.Resource;

/**
 * A factory API for the *Descr classes 
 */
public class DescrFactory {

    public static PackageDescrBuilder newPackage() {
        return PackageDescrBuilderImpl.newPackage();
    }

    public static PackageDescrBuilder newPackage(Resource resource) {
        return PackageDescrBuilderImpl.newPackage(resource);
    }
}
