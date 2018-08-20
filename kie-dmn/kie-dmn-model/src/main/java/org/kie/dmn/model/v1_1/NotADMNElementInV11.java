/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.DMNElement;

public interface NotADMNElementInV11 extends DMNElement {

    @Override
    default String getDescription() {
        return null;
    }

    @Override
    default void setDescription(String value) {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

    @Override
    default ExtensionElements getExtensionElements() {
        return null;
    }

    @Override
    default void setExtensionElements(ExtensionElements value) {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

    @Override
    default String getId() {
        return null;
    }

    @Override
    default void setId(String value) {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

    @Override
    default String getLabel() {
        return null;
    }

    @Override
    default void setLabel(String value) {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }
}
