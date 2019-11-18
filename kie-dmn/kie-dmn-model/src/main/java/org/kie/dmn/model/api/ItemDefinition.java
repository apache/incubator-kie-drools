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

package org.kie.dmn.model.api;

import java.util.List;

import javax.xml.namespace.QName;

public interface ItemDefinition extends NamedElement {

    /**
     * Internal Model: this is using QName as per DMN v1.1 in order to maintain internal compatibility with the engine
     */
    QName getTypeRef();

    /**
     * Internal Model: this is using QName as per DMN v1.1 in order to maintain internal compatibility with the engine
     */
    void setTypeRef(QName value);

    UnaryTests getAllowedValues();

    void setAllowedValues(UnaryTests value);

    List<ItemDefinition> getItemComponent();

    String getTypeLanguage();

    void setTypeLanguage(String value);

    boolean isIsCollection();

    void setIsCollection(Boolean value);

    String toString();

    /**
     * @since DMN v1.3
     */
    FunctionItem getFunctionItem();

    /**
     * @since DMN v1.3
     */
    void setFunctionItem(FunctionItem value);

}
