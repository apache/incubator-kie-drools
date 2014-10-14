/*
 * Copyright 2014 JBoss Inc
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
 * Multiple "root" TypeNodes were found when constructing the Decision Tree
 */
public class AmbiguousRootParserMessage implements ParserMessage {

    private String className;

    public AmbiguousRootParserMessage() {
        //Errai marshalling
    }

    public AmbiguousRootParserMessage( final String className ) {
        this.className = PortablePreconditions.checkNotNull( "className",
                                                             className );
    }

    public String getClassName() {
        return className;
    }
}
