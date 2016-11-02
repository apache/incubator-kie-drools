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

import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.util.StringUtils;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.Context;

public class NewKnowledgeBuilderCommand
    implements
    ExecutableCommand<KnowledgeBuilder> {

    private KnowledgeBuilderConfiguration kbuilderConf;
    
    private KnowledgeBase attachedKnowledgeBase;
    
    private String kbaseId;
    
    private String outIdentifier;

    public NewKnowledgeBuilderCommand() {
    }
    
    public NewKnowledgeBuilderCommand(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }
    
    public NewKnowledgeBuilderCommand(KnowledgeBuilderConfiguration kbuilderConf) {
        this.kbuilderConf = kbuilderConf;
    }
    
    public NewKnowledgeBuilderCommand(KnowledgeBuilderConfiguration kbuilderConf, String kbaseId) {
        this.kbuilderConf = kbuilderConf;
        setAttachedKnowledgeBase( kbaseId );
    }    

    public NewKnowledgeBuilderCommand(KnowledgeBuilderConfiguration kbuilderConf, String kbaseId, String outIdentifier) {
        this.kbuilderConf = kbuilderConf;
        setAttachedKnowledgeBase( kbaseId );
        this.outIdentifier = outIdentifier;
    }    

    public KnowledgeBase getAttachedKnowledgeBase() {
        return attachedKnowledgeBase;
    }

    public void setAttachedKnowledgeBase(KnowledgeBase attachedKnowledgeBase) {
        this.attachedKnowledgeBase = attachedKnowledgeBase;
    }
    
    public void setAttachedKnowledgeBase(String kbaseId) {
        this.kbaseId = kbaseId;
    }




    public KnowledgeBuilder execute(Context context) {
        KnowledgeBuilder kbuilder = null;
        
        if ( !StringUtils.isEmpty( kbaseId )) {
            attachedKnowledgeBase = ( KnowledgeBase ) context.get( kbaseId );
        }
        
        if ( this.kbuilderConf == null ) {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(this.attachedKnowledgeBase);
        } else {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( this.attachedKnowledgeBase,
                                                                    this.kbuilderConf );
        }
        
         if ( context instanceof RegistryContext ) {
            ((RegistryContext) context).register( KnowledgeBuilder.class, kbuilder );
        }

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup( ExecutionResultImpl.class ).setResult( this.outIdentifier, kbuilder );
        }
        
        return kbuilder;
    }

}
