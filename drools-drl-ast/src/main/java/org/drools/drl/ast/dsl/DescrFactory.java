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
package org.drools.drl.ast.dsl;

import org.drools.drl.ast.dsl.impl.PackageDescrBuilderImpl;
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
