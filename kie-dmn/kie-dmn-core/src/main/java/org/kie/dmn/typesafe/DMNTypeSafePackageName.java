/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.typesafe;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;

public class DMNTypeSafePackageName {

    private final DMNModel dmnModel;
    private final String prefix;

    public DMNTypeSafePackageName(DMNModel dmnModel, String prefix) {
        this.dmnModel = dmnModel;
        this.prefix = prefix;
    }

    public DMNTypeSafePackageName(DMNModel dmnModel) {
        this(dmnModel, "");
    }

    public String packageName() {
        return CodegenStringUtil.escapeIdentifier(prefix + dmnModel.getNamespace() + dmnModel.getName());
    }
}
