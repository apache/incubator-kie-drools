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

package org.kie.dmn.model.v1_2;

import org.kie.dmn.model.api.ImportedValues;
import org.kie.dmn.model.api.LiteralExpression;

public class TLiteralExpression extends TExpression implements LiteralExpression {

    protected String text;
    protected ImportedValues importedValues;
    protected String expressionLanguage;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String value) {
        this.text = value;
    }

    @Override
    public ImportedValues getImportedValues() {
        return importedValues;
    }

    @Override
    public void setImportedValues(ImportedValues value) {
        this.importedValues = value;
    }

    @Override
    public String getExpressionLanguage() {
        return expressionLanguage;
    }

    @Override
    public void setExpressionLanguage(String value) {
        this.expressionLanguage = value;
    }

}
