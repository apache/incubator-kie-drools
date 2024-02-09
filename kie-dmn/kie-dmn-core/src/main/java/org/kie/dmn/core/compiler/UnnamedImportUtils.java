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
package org.kie.dmn.core.compiler;

import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.Import;

/**
 * Class meant to provide helper methods to deal with unnamed model imports
 */
public class UnnamedImportUtils {

    private UnnamedImportUtils() {
    }

    public static boolean isInUnnamedImport(DMNNode node, DMNModelImpl model) {
        for (Import imported : model.getDefinitions().getImport()) {
            String importedName = imported.getName();
            if ((node.getModelNamespace().equals(imported.getNamespace()) &&
                    (importedName != null && importedName.isEmpty()))) {
                return true;
            }
        }
        return false;
    }
}