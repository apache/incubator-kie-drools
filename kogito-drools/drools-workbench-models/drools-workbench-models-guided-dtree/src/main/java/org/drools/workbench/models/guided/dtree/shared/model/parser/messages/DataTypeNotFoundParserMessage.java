/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.guided.dtree.shared.model.parser.messages;

import org.drools.workbench.models.datamodel.util.PortablePreconditions;

/**
 * The data-type of a class's field could not be found
 */
public class DataTypeNotFoundParserMessage implements ParserMessage {

    private String className;
    private String fieldName;

    public DataTypeNotFoundParserMessage() {
        //Errai marshalling
    }

    public DataTypeNotFoundParserMessage( final String className,
                                          final String fieldName ) {
        this.className = PortablePreconditions.checkNotNull( "className",
                                                             className );
        this.fieldName = PortablePreconditions.checkNotNull( "fieldName",
                                                             fieldName );
    }

    public String getClassName() {
        return className;
    }

    public String getFieldName() {
        return fieldName;
    }
}
