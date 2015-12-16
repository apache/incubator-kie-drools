/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.datamodel.rule;

import org.drools.workbench.models.datamodel.oracle.DataType;

public class ExpressionText extends ExpressionPart {

    public ExpressionText() {
    }

    public ExpressionText( final String text,
                           final String classType,
                           final String genericType ) {
        super( text,
               classType,
               genericType );
    }

    public ExpressionText( final String text ) {
        super( text,
               "java.lang.String",
               DataType.TYPE_STRING );
    }

    public void setText( final String text ) {
        this.name = text;
    }

    @Override
    public void accept( final ExpressionVisitor visitor ) {
        visitor.visit( this );
    }

}
