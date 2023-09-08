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
package org.kie.dmn.typesafe;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;

public class DMNTypeSafePackageName {

    public interface Factory {

        DMNTypeSafePackageName create(DMNModel m);
    }

    public static class ModelFactory implements Factory {

        private final String prefix;

        public ModelFactory(String prefix) {
            this.prefix = prefix;
        }

        public ModelFactory() {
            this.prefix = "";
        }

        @Override
        public DMNTypeSafePackageName create(DMNModel model) {
            return new DMNTypeSafePackageName(prefix, model.getNamespace(), model.getName());
        }
    }

    private final String prefix;
    private final String dmnModelNamespace;
    private final String dmnModelName;

    public DMNTypeSafePackageName(String prefix, String dmnModelNamespace, String dmnModelName) {
        this.prefix = prefix;
        this.dmnModelNamespace = dmnModelNamespace;
        this.dmnModelName = dmnModelName;
    }

    public String packageName() {
        return CodegenStringUtil.escapeIdentifier(prefix + dmnModelNamespace + dmnModelName);
    }

    public String appendPackage(String typeName) {
        return packageName() + "." + typeName;
    }

    @Override
    public String toString() {
        return "DMNTypeSafePackageName{" +
                "prefix='" + prefix + '\'' +
                ", dmnModelNamespace='" + dmnModelNamespace + '\'' +
                ", dmnModelName='" + dmnModelName + '\'' +
                '}';
    }
}
