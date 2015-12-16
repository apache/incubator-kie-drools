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
 * A bound Type referenced could not be located in the tree
 */
public class BindingNotFoundParserMessage implements ParserMessage {

    private String binding;

    public BindingNotFoundParserMessage() {
        //Errai marshalling
    }

    public BindingNotFoundParserMessage( final String binding ) {
        this.binding = PortablePreconditions.checkNotNull( "binding",
                                                           binding );
    }

    public String getBinding() {
        return binding;
    }
}
