/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.command.builder;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.command.Context;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

public class KnowledgeBuilderAddCommand
    implements
    GenericCommand<Void> {

    private Resource              resource;
    private ResourceType          resourceType;
    private ResourceConfiguration resourceConfiguration;

    public KnowledgeBuilderAddCommand(Resource resource,
                                      ResourceType resourceType,
                                      ResourceConfiguration resourceConfiguration) {
        this.resource = resource;
        this.resourceType = resourceType;
        this.resourceConfiguration = resourceConfiguration;
    }
    

    public Void execute(Context context) {
        KnowledgeBuilder kbuilder = ((KnowledgeCommandContext) context).getKnowledgeBuilder();
        if ( resourceConfiguration == null ) {
            kbuilder.add( resource,
                          resourceType );
        } else {
            kbuilder.add( resource,
                          resourceType,
                          resourceConfiguration );
        }
        return null;
    }

}
