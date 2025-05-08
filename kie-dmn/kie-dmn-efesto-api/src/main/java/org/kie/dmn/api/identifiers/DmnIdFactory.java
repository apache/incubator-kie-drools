/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.api.identifiers;

import org.kie.efesto.common.api.identifiers.LocalUri;

public class DmnIdFactory implements DmnComponentRoot {

    public LocalComponentIdDmn get(String nameSpace, String modelName) {
        return new LocalComponentIdDmn(nameSpace, modelName);
    }

    public LocalCompilationSourceIdDmn get(String fileName) {
        if (fileName.contains(LocalUri.SLASH)) {
            fileName = fileName.substring(fileName.lastIndexOf(LocalUri.SLASH) + 1);
        }
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        }
        return new LocalCompilationSourceIdDmn(fileName);
    }
}
