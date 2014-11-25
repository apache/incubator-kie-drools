/*
 * Copyright 2012 JBoss Inc
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

public class ExpressionGlobalVariable extends ExpressionPart {

    public ExpressionGlobalVariable() {
    }

    public ExpressionGlobalVariable( String name,
                                     String classType,
                                     String genericType ) {
        super( name, classType, genericType );
    }

    public ExpressionGlobalVariable( String name,
                                     String classType,
                                     String genericType,
                                     String parametricType ) {
        super( name, classType, genericType, parametricType );
    }

    @Override
    public void accept( ExpressionVisitor visitor ) {
        visitor.visit( this );
    }

}
