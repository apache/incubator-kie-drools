/*
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

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.command.Context;
import org.drools.command.ResolvingKnowledgeCommandContext;
import org.drools.command.impl.FixedKnowledgeCommandContext;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.core.util.StringUtils;
import org.drools.runtime.impl.ExecutionResultImpl;

public class NewKnowledgeBuilderCommand
    implements
    GenericCommand<KnowledgeBuilder> {

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
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( this.attachedKnowledgeBase );
        } else {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( this.attachedKnowledgeBase,
                                                                    this.kbuilderConf );
        }
        
        if( context instanceof KnowledgeCommandContext ) { 
            ((KnowledgeCommandContext) context).setKnowledgeBuilder(kbuilder);
        }
        
        if ( this.outIdentifier != null ) {
            ((ExecutionResultImpl)((KnowledgeCommandContext) context).getExecutionResults()).getResults().put( this.outIdentifier, kbuilder );
        }
        
        return kbuilder;
    }

}
