/**
 * Copyright 2010 JBoss Inc
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

package org.drools.command.builder;

import java.util.Collection;

import org.drools.builder.KnowledgeBuilder;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.definition.KnowledgePackage;

public class KnowledgeBuilderGetKnowledgePackagesCommand
    implements
    GenericCommand<Collection<KnowledgePackage>> {

    private String outIdentifier;

    public KnowledgeBuilderGetKnowledgePackagesCommand() {
    }

    public KnowledgeBuilderGetKnowledgePackagesCommand(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public Collection<KnowledgePackage> execute(Context context) {
        KnowledgeBuilder kbuilder = ((KnowledgeCommandContext) context).getKnowledgeBuilder();
        return kbuilder.getKnowledgePackages();
    }

}
