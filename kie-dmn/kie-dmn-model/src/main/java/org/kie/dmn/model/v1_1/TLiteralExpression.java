/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.kie.dmn.model.api.ImportedValues;
import org.kie.dmn.model.api.LiteralExpression;

public class TLiteralExpression extends TExpression implements LiteralExpression {

    private String text;
    private ImportedValues importedValues;
    private String expressionLanguage;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText( final String value ) {
        this.text = value;
    }

    @Override
    public ImportedValues getImportedValues() {
        return importedValues;
    }

    @Override
    public void setImportedValues(final ImportedValues value) {
        this.importedValues = value;
    }

    @Override
    public String getExpressionLanguage() {
        return expressionLanguage;
    }

    @Override
    public void setExpressionLanguage( final String value ) {
        this.expressionLanguage = value;
    }

}
