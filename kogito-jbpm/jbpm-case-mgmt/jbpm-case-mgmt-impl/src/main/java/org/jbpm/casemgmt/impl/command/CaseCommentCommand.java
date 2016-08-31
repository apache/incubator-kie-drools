/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.impl.command;

import java.util.Collection;

import org.drools.core.ClassObjectFilter;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CommentInstance;
import org.jbpm.casemgmt.impl.event.CaseEventSupport;
import org.jbpm.casemgmt.impl.model.instance.CaseFileInstanceImpl;
import org.jbpm.casemgmt.impl.model.instance.CommentInstanceImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.Context;

/**
 * Adds or removes comment to/from case
 */
public class CaseCommentCommand extends CaseCommand<Void> {

    private static final long serialVersionUID = 6345222909719335923L;

    private String author;
    private String comment;
    private boolean add;
    private boolean update;
    private boolean remove;
    
    private String commentId;
    
    private String updatedText;
    
    public CaseCommentCommand(String author, String comment) {
        this.author = author;
        this.comment = comment;
        this.add = true;
    }

    public CaseCommentCommand(String commentId) {
        this.commentId = commentId;
        this.remove = true;
    }
    
    public CaseCommentCommand(String commentId, String author, String updatedText) {
        this.commentId = commentId;
        this.author = author;
        this.updatedText = updatedText;
        this.update = true;
        
    }
    
    @Override
    public Void execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        
        Collection<? extends Object> caseFiles = ksession.getObjects(new ClassObjectFilter(CaseFileInstance.class));
        if (caseFiles.size() != 1) {
            throw new IllegalStateException("Not able to find distinct case file - found case files " + caseFiles.size());
        }
        CaseFileInstance caseFile = (CaseFileInstance) caseFiles.iterator().next();
        FactHandle factHandle = ksession.getFactHandle(caseFile);
        
        CaseEventSupport caseEventSupport = getCaseEventSupport(context);        
        
        if (add) {
            CommentInstance commentInstance = new CommentInstanceImpl(author, comment);
            caseEventSupport.fireBeforeCaseCommentAdded(caseFile.getCaseId(), commentInstance);
            ((CaseFileInstanceImpl)caseFile).addComment(commentInstance);
            caseEventSupport.fireAfterCaseCommentAdded(caseFile.getCaseId(), commentInstance);
        } else if (update) {
            CommentInstance toUpdate = ((CaseFileInstanceImpl)caseFile).getComments().stream()
                    .filter(c -> c.getId().equals(commentId))
                    .findFirst()
                    .get();
            if (!this.author.equals(toUpdate.getAuthor())) {
                throw new IllegalStateException("Only original author can update comment");
            }
            caseEventSupport.fireBeforeCaseCommentUpdated(caseFile.getCaseId(), toUpdate);
            ((CommentInstanceImpl)toUpdate).setComment(updatedText);            
            caseEventSupport.fireBeforeCaseCommentUpdated(caseFile.getCaseId(), toUpdate);
        } else if (remove) {
            CommentInstance toRemove = ((CaseFileInstanceImpl)caseFile).getComments().stream()
                    .filter(c -> c.getId().equals(commentId))
                    .findFirst()
                    .get();
            caseEventSupport.fireBeforeCaseCommentRemoved(caseFile.getCaseId(), toRemove);
            ((CaseFileInstanceImpl)caseFile).removeComment(toRemove);            
            caseEventSupport.fireBeforeCaseCommentRemoved(caseFile.getCaseId(), toRemove);
        }
        ksession.update(factHandle, caseFile);
        return null;
    }

}
