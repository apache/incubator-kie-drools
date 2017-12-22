/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.casemgmt.impl.command;

import org.drools.core.ClassObjectFilter;
import org.drools.core.command.impl.RegistryContext;
import org.jbpm.casemgmt.api.CaseCommentNotFoundException;
import org.jbpm.casemgmt.api.auth.AuthorizationManager;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CommentInstance;
import org.jbpm.casemgmt.impl.event.CaseEventSupport;
import org.jbpm.casemgmt.impl.model.instance.CaseFileInstanceImpl;
import org.jbpm.casemgmt.impl.model.instance.CommentInstanceImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.identity.IdentityProvider;
import org.kie.api.runtime.Context;

import java.util.Collection;
import java.util.List;

/**
 * Adds or removes comment to/from case
 */
public class CaseCommentCommand extends CaseCommand<String> {

    private static final long serialVersionUID = 6345222909719335923L;

    private String author;
    private String comment;
    private boolean add;
    private boolean update;
    private boolean remove;

    private String commentId;

    private String updatedText;
    
    private List<String> restrictedTo;
    
    private AuthorizationManager authorizationManager;

    public CaseCommentCommand(IdentityProvider identityProvider, String author, String comment, List<String> restrictedTo) {
        super(identityProvider);
        this.author = author;
        this.comment = comment;
        this.add = true;
        this.restrictedTo = restrictedTo;
    }

    public CaseCommentCommand(IdentityProvider identityProvider, String commentId, AuthorizationManager authorizationManager) {
        super(identityProvider);
        this.commentId = commentId;
        this.remove = true;
        this.authorizationManager = authorizationManager;
    }

    public CaseCommentCommand(IdentityProvider identityProvider, String commentId, String author, String updatedText, List<String> restrictedTo, AuthorizationManager authorizationManager) {
        super(identityProvider);
        this.commentId = commentId;
        this.author = author;
        this.updatedText = updatedText;
        this.update = true;
        this.restrictedTo = restrictedTo;
        this.authorizationManager = authorizationManager;
    }

    @Override
    public String execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        Collection<? extends Object> caseFiles = ksession.getObjects(new ClassObjectFilter(CaseFileInstance.class));
        if (caseFiles.size() != 1) {
            throw new IllegalStateException("Not able to find distinct case file - found case files " + caseFiles.size());
        }
        CaseFileInstance caseFile = (CaseFileInstance) caseFiles.iterator().next();
        FactHandle factHandle = ksession.getFactHandle(caseFile);

        CaseEventSupport caseEventSupport = getCaseEventSupport(context);

        
        String commentIdentifier = null;
        if (add) {
            CommentInstance commentInstance = new CommentInstanceImpl(author, comment, restrictedTo);
            caseEventSupport.fireBeforeCaseCommentAdded(caseFile.getCaseId(), caseFile, commentInstance);
            ((CaseFileInstanceImpl)caseFile).addComment(commentInstance);
            
            commentIdentifier = commentInstance.getId();
            caseEventSupport.fireAfterCaseCommentAdded(caseFile.getCaseId(), caseFile, commentInstance);
        } else if (update) {
            CommentInstance toUpdate = ((CaseFileInstanceImpl)caseFile).getComments().stream()
                    .filter(c -> c.getId().equals(commentId))
                    .findFirst()
                    .orElseThrow(() -> new CaseCommentNotFoundException("Cannot find comment with id " + commentId));
            if (!this.author.equals(toUpdate.getAuthor())) {
                throw new IllegalStateException("Only original author can update comment");
            }
            // apply authorization
            authorizationManager.checkCommentAuthorization(caseFile.getCaseId(), caseFile, toUpdate);
            
            caseEventSupport.fireBeforeCaseCommentUpdated(caseFile.getCaseId(), caseFile, toUpdate);
            ((CommentInstanceImpl)toUpdate).setComment(updatedText);
            if (restrictedTo != null) {
                ((CommentInstanceImpl)toUpdate).setRestrictedTo(restrictedTo);
            }
            commentIdentifier = toUpdate.getId();
            caseEventSupport.fireAfterCaseCommentUpdated(caseFile.getCaseId(), caseFile, toUpdate);
        } else if (remove) {
            CommentInstance toRemove = ((CaseFileInstanceImpl)caseFile).getComments().stream()
                    .filter(c -> c.getId().equals(commentId))
                    .findFirst()
                    .orElseThrow(() -> new CaseCommentNotFoundException("Cannot find comment with id " + commentId));
            
            // apply authorization
            authorizationManager.checkCommentAuthorization(caseFile.getCaseId(), caseFile, toRemove);
            
            caseEventSupport.fireBeforeCaseCommentRemoved(caseFile.getCaseId(), caseFile, toRemove);
            ((CaseFileInstanceImpl)caseFile).removeComment(toRemove);
            
            commentIdentifier = toRemove.getId();
            caseEventSupport.fireAfterCaseCommentRemoved(caseFile.getCaseId(), caseFile, toRemove);
        }
        ksession.update(factHandle, caseFile);
        triggerRules(ksession);
        return commentIdentifier;
    }

}
